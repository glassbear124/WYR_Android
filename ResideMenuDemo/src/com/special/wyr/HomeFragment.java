package com.special.wyr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.ResideMenu.ResideMenu;
import com.special.wyr.model.Answer;
import com.special.wyr.model.Question;
import com.special.wyr.utils.ProgressBarAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeFragment extends Fragment implements View.OnClickListener {

    void registerUser() {

        G.hud.show();
        RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";
        G.member_id = preferences.getInt("member_id", 0);
        if (G.member_id > 0 )
            url = C.base + "register_user.php?member_id="+G.member_id+"&gamertag=%@&game_center=0&android=1";
        else
            url = C.base + "register_user.php?gamertag=Unknown&game_center=0&android=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);

                            Log.v("Login JSON","Login arr="+arr.length());

                            if (arr.length() > 0) {
                                JSONObject obj = arr.getJSONObject(0);

                                int memId = obj.getInt("member_id");
                                if (memId > 0) {
                                    G.member_id = memId;
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("member_id", G.member_id);
                                    editor.putString("promotions", obj.getString("promotions"));
                                    editor.putString("shares", obj.getString("shares"));
                                    editor.putString("approved", obj.getString("approved"));
                                    editor.apply();

                                    load_questions_new();
                                }
                            }
                            else {
                                registerUser();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            G.hud.dismiss();
                            viewError();
                            G.showDialog( getActivity(), C.err_msg );
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.hud.dismiss();
                G.showDialog( getActivity(), C.err_msg );
                viewError();
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    void load_single_question(final int quizId) {
        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
        String url = "";

        url = C.base + "load_single_question.php?member_id="+G.member_id+
                "&question_id=" + quizId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        G.hud.dismiss();
                        try {
                            questionsArray.clear();
                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);
                            Question quiz = new Question(obj);
                            questionsArray.add(quiz);
                            invalidateQuiz();
                        } catch (JSONException e) {
                            G.showDialog( getActivity(), C.err_msg );
                            viewError();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.hud.dismiss();
                G.showDialog( getActivity(), C.err_msg );
                viewError();
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    void load_questions_new() {

        if( isShow == true )
            return;

        if (!loadingQuestions) {
            loadingQuestions = true;
            G.hud.show();

            com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
            String url = "";
            int childrenId = preferences.getInt("childrenSwitch",0);

            if( viewStyle == C.SCR_WYR ) {
                url = C.base + "load_questions_new.php?language=en&member_id="+G.member_id+
                        "&forChildren=" + childrenId;
            }
            else {
                String mode = "";
                if( viewStyle == C.SCR_HONEST ) {
                    if( childrenId == 0 ) {
                        mode = "dirtiest";
                    } else {
                        boolean is1 = btn1.isSelected();
                        boolean is2 = btn2.isSelected();
//                        boolean is3 = btn3.isSelected();

                        if( is1 == true )
                            mode = "hottest";
                        else if( is2 == true )
                            mode = "trending";
                        else
                            mode = "mostLikes";
                    }
                }
                else if( viewStyle == C.SCR_FEED ) {
                    mode = "feed";
                }
                url = C.base + "load_questions_new.php?language=en&member_id="+G.member_id+
                        "&forChildren=" + childrenId + "&mode=" + mode;
            }

            Log.v("questionsURL", url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray arr = new JSONArray(response);

                                questionsArray.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    Question quiz = new Question( arr.getJSONObject(i));
                                    questionsArray.add(quiz);
                                }

                                G.hud.dismiss();
                                loadingQuestions = false;

                                if( questionsArray.size() > 0 )
                                    invalidateQuiz();

                                if( viewStyle == C.SCR_HONEST && isClickSegment == true )
                                    nextAnimation(false);

                            } catch (JSONException e) {
                                G.hud.dismiss();
                                viewError();
                                G.showDialog( getActivity(), C.err_msg );
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    G.hud.dismiss();
                    G.showDialog( getActivity(), C.err_msg );
                    viewError();
                    error.printStackTrace();
                }
            });
            queue.add(stringRequest);
        }
    }

    void submitLikePHP() throws JSONException {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
        String url = C.base + "submit_like.php?member_id="+G.member_id+"&question_id="+questionsArray.get(0)._id;
        //Log.v("submitLikeURL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("submitLike", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.showDialog( getActivity(), C.err_msg );
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    void removeLikePHP() throws JSONException {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
        String url = C.base + "remove_like.php?member_id="+G.member_id+"&question_id="+questionsArray.get(0)._id;
        //Log.v("removeLikeURL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("removeLike", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.showDialog( getActivity(), C.err_msg );
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    void submit_single_answer(final String quizId, final String ans ) {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = C.base + "submit_single_answer.php?member_id="+G.member_id;

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.showDialog( getActivity(), C.err_msg );
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("question_id", quizId);
                params.put("answer", ans);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    void reportQuestion( final int quiz_id, final int param ) {
        G.hud.show();

        String type = "";
        switch ( param ) {
            case R.id.lnSpelling: type = "spelling";
                break;
            case R.id.lnMark: type = "notChildren";
                break;
            case R.id.lnJunk: type = "junk";
                break;
            case R.id.lnWrong: type = "wrongLanguage";
                break;
            case R.id.lnRport: type = "offensive";
                break;
        }

        com.android.volley.RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
        String url = "";

        url = C.base + "reportQuestion.php?qID="+ quiz_id + "&type=" + type + "&member_id" + G.member_id + "&language=en";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        G.hud.dismiss();
                        if( response.compareTo("Success") == 0 ) {
                            G.showDialog( getActivity(), "Question reported successfully.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.hud.dismiss();
                G.showDialog( getActivity(), C.err_msg );
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    void post_new_question( final String ans1, final  String ans2, final String forChild ) {
        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = C.base + "post_new_question.php?member_id="+G.member_id;

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                G.hud.dismiss();

                if( response.compareTo("Success") == 0 ) {
                    G.showDialog( getActivity(), "Your question successfully posted.");
                } else {
                    G.showDialog( getActivity(), "Sorry, post failed for question.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.showDialog( getActivity(), C.err_msg );
                G.hud.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("answer1", ans1);
                params.put("answer2", ans2);
                params.put("nickname", "Android");
                params.put("language", "en");
                params.put("for_children", forChild);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    void viewError() {
        txtOR.setVisibility(View.GONE);
        txtAnswerOne.setText("No Questions available.");
        txtAnswerTwo.setText("No Questions available.");
        txtAuthor.setText("");
    }

    private View p;
    private ResideMenu resideMenu;

    boolean isClickSegment = false;

    ImageView btnLeft;

    View btnAlert;
    TextView txtTitle, txtLikeCnt;
    LinearLayout lnGroupTxt;

    TextView txtAuthor;

    ImageView imgTitleWYRBanner;
    ImageView imgWYRBanner;

    RelativeLayout answerOneView;
    LinearLayout   lnQuiz;
    TextView       txtAnswerOne, txtAnswerOnePercent;
    ProgressBar    progAnswerOne;

    RelativeLayout answerTwoView;
    LinearLayout   lnAns;
    TextView       txtAnswerTwo, txtAnswerTwoPercent;
    ProgressBar    progAnswerTwo;


    TextView txtOR;

    View btnHeart, btnComment;
    TextView btnBottom;

    int nLike;
    public int viewStyle;
    public boolean isShow;
    public boolean isUpdateSingleMode = false;
    public int nSingleQuizId;

    RelativeLayout rnGroup;

    LinearLayout lnSegmenter;
    TextView btn1, btn2, btn3;

    TextView txtAnswerCnt;
    ToggleButton btnSkip;

    ArrayList<Question> questionsArray = new ArrayList<Question>();

    boolean loadingQuestions = false;
    boolean isAnimatingQuestion = false;

    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        p = inflater.inflate(R.layout.home, container, false);

        preferences = getActivity().getSharedPreferences(
                "com.intopi.showapp", Context.MODE_PRIVATE);

        if (preferences.contains("childrenSwitch") && preferences.getInt("childrenSwitch", -1) >= 0) {
            continueViewSetup();
        }
        else {
            lnSegmenter = (LinearLayout)p.findViewById(R.id.lnSegmenter);
            txtTitle = (TextView)p.findViewById(R.id.txtTitle);
            imgWYRBanner = (ImageView)p.findViewById(R.id.imgWYRBanner);

            lnSegmenter.setVisibility(View.GONE);
            txtTitle.setVisibility(View.GONE);
            imgWYRBanner.setVisibility(View.GONE);

            new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Welcome to Would You Rather! This App may contain strong or potentially offensive language. Please select your age group below and we'll do our best to filter the questions for you.")
                    .setCancelText("Children (0-16)")
                    .setConfirmText("Adults (17+)")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("childrenSwitch", 1);
                            editor.apply();
                            continueViewSetup();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("You may get questions that contain language")
                                    .setContentText("If you find a question that has errors or is offensive, please report it by clicking the ⚠️ button in the top right.")
                                    .setCancelText("I'm 17+")
                                    .setConfirmText("Turn On Filtering")
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.cancel();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putInt("childrenSwitch", 0);
                                            editor.apply();
                                            continueViewSetup();
                                        }
                                    })
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.cancel();
                                            sDialog.cancel();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putInt("childrenSwitch", 1);
                                            editor.apply();
                                            continueViewSetup();
                                        }
                                    });
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                    text.setGravity(Gravity.CENTER);
                                    text.setSingleLine(false);
                                }
                            });
                            dialog.show();
                        }
                    })
                    .show();
        }
        return p;
    }

    LinearLayout rlActionSheet;
    Animation aniSlideUp;

    void continueViewSetup() {
        setUpViews();

        myDialog = new Dialog( getActivity(), R.style.CustomTheme);
        myDialog.setContentView(R.layout.actionsheet);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = myDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        myDialog.findViewById(R.id.lnSpelling).setOnClickListener(this);
        myDialog.findViewById(R.id.lnMark).setOnClickListener(this);
        myDialog.findViewById(R.id.lnJunk).setOnClickListener(this);
        myDialog.findViewById(R.id.lnWrong).setOnClickListener(this);
        myDialog.findViewById(R.id.lnRport).setOnClickListener(this);

        myDialog.findViewById(R.id.tvCancel).setOnClickListener(this);
        myDialog.getWindow().setGravity(Gravity.BOTTOM);

        rlActionSheet = (LinearLayout) myDialog.findViewById(R.id.rlActionSheet);
        aniSlideUp = AnimationUtils.loadAnimation( getActivity().getBaseContext(), R.anim.slide_up );

        lnSegmenter = (LinearLayout)p.findViewById(R.id.lnSegmenter);
        txtTitle = (TextView)p.findViewById(R.id.txtTitle);
        imgTitleWYRBanner = (ImageView)p.findViewById(R.id.imgTitleWYRBanner);
        lnGroupTxt= (LinearLayout)p.findViewById(R.id.lnGroupTxt);

        txtAuthor = (TextView)p.findViewById(R.id.txtAuthor);
        txtAuthor.setOnClickListener(this);

        imgWYRBanner = (ImageView)p.findViewById(R.id.imgWYRBanner);

        btn1 = (TextView)p.findViewById(R.id.btn1); btn1.setOnClickListener(this);
        btn2 = (TextView)p.findViewById(R.id.btn2); btn2.setOnClickListener(this);
        btn3 = (TextView)p.findViewById(R.id.btn3); btn3.setOnClickListener(this);
        btn1.setTextColor(getResources().getColor(R.color.navColor));
        btn1.setSelected(true);

        btnAlert = p.findViewById(R.id.btnAlert); btnAlert.setOnClickListener(this);

        txtLikeCnt = (TextView)p.findViewById(R.id.txtLikeCnt);

        answerOneView = (RelativeLayout)p.findViewById(R.id.answerOneView); answerOneView.setOnClickListener(this);
        txtAnswerOne = (TextView)p.findViewById(R.id.txtAnswerOne);
        txtAnswerOnePercent = (TextView)p.findViewById(R.id.txtAnswerOnePercent);
        progAnswerOne = (ProgressBar)p.findViewById(R.id.progAnswerOne);

        lnQuiz = (LinearLayout)p.findViewById(R.id.lnQuiz);

        answerTwoView = (RelativeLayout)p.findViewById(R.id.answerTwoView); answerTwoView.setOnClickListener(this);
        txtAnswerTwo = (TextView)p.findViewById(R.id.txtAnswerTwo);
        txtAnswerTwoPercent = (TextView)p.findViewById(R.id.txtAnswerTwoPercent);
        progAnswerTwo = (ProgressBar)p.findViewById(R.id.progAnswerTwo);

        lnAns = (LinearLayout)p.findViewById(R.id.lnAns);
        txtOR = (TextView)p.findViewById(R.id.txtOR);

        txtAnswerOne.setTypeface(G.tf); txtAnswerOnePercent.setTypeface(G.tfBold);
        txtAnswerTwo.setTypeface(G.tf);  txtAnswerTwoPercent.setTypeface(G.tfBold);

        txtAnswerCnt = (TextView)p.findViewById(R.id.txtAnswerCnt);
        btnHeart = p.findViewById(R.id.btnHeart); btnHeart.setOnClickListener(this);
        btnSkip = (ToggleButton)p.findViewById(R.id.btnSkip); btnSkip.setOnClickListener(this);

        btnComment = p.findViewById(R.id.btnComment); btnComment.setOnClickListener(this);

        btnBottom = (TextView)p.findViewById(R.id.btnBottom); btnBottom.setOnClickListener(this);
        rnGroup = (RelativeLayout)p.findViewById(R.id.rnGroup);

        btnSkip.setTypeface(G.tfSuper);
        btnBottom.setTypeface(G.tfSuper);

        if( viewStyle == C.SCR_COMPOSE ) {

            if( G.strQuiz.length() == 0 )
                txtAnswerOne.setText( "Type First Option Here..." );
            else
                txtAnswerOne.setText( G.strQuiz );

            if( G.strAns.length() == 0 )
                txtAnswerTwo.setText( "Type Second Option Here..." );
            else
                txtAnswerTwo.setText(G.strAns);

            imgTitleWYRBanner.setVisibility(View.GONE);
            lnSegmenter.setVisibility(View.GONE);
            rnGroup.setVisibility(View.INVISIBLE);

            btnBottom.setText("Submit");
            btnBottom.setBackgroundColor( getActivity().getResources().getColor(R.color.green));

            lnGroupTxt.setVisibility(View.GONE);
            btnAlert.setVisibility(View.GONE);

        } else {

            if(  viewStyle == C.SCR_HONEST ) {
                imgTitleWYRBanner.setVisibility(View.GONE);
            }
            else
                lnSegmenter.setVisibility(View.GONE);

            txtTitle.setVisibility(View.GONE);
            imgWYRBanner.setVisibility(View.GONE);

            registerUser();
        }

        if( isShow == true ) {

            load_single_question(nSingleQuizId);

            btnLeft.setImageResource(R.drawable.icn_back);

            txtAnswerCnt.setVisibility(View.GONE);
            btnBottom.setVisibility(View.INVISIBLE);
            btnSkip.setText("Share with Others");
            btnSkip.setTextOn("Share with Others");
            btnSkip.setTextOff("Share with Others");
            btnSkip.setBackgroundColor( getResources().getColor(R.color.yellow));
        } else {
            btnLeft.setImageResource(R.drawable.btn_menu);
        }
        loadAnimation( true );
    }

    void loadAnimation(final boolean isFirst ) {

        Context context = getActivity().getBaseContext();

        final Animation aniQuiz = AnimationUtils.loadAnimation( context, R.anim.ani_big_1 );
        final Animation aniAns  = AnimationUtils.loadAnimation( context, R.anim.ani_big_2 );

        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 20);

        aniQuiz.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                txtAnswerOne.setVisibility(View.VISIBLE);
                txtAnswerTwo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimatingQuestion = false;
                if (!isFirst) {

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        aniQuiz.setInterpolator(interpolator);
        aniAns.setInterpolator(interpolator);

        progAnswerOne.startAnimation(aniQuiz);
        lnQuiz.startAnimation(aniQuiz);

        progAnswerTwo.startAnimation(aniAns);
        lnAns.startAnimation(aniAns);

        final Animation animation1 = AnimationUtils.loadAnimation( context, R.anim.ani_big );
        animation1.setInterpolator(interpolator);
        btnHeart.startAnimation(animation1);
        btnComment.startAnimation(animation1);

        final Animation animation5 = AnimationUtils.loadAnimation( context, R.anim.ani_big );
        animation5.setInterpolator(interpolator);
        txtAnswerCnt.startAnimation(animation5);

        final Animation animation4 = AnimationUtils.loadAnimation( context, R.anim.ani_big );
        animation4.setInterpolator(interpolator);
        lnGroupTxt.startAnimation(animation4);

        final Animation aniOK;
        if( isFirst ) {
            aniOK = AnimationUtils.loadAnimation( context, R.anim.ani_big );
            MyBounceInterpolator interpolator2 = new MyBounceInterpolator(0.1, 20);
            aniOK.setInterpolator(interpolator2);
        }
        else {
            aniOK = AnimationUtils.loadAnimation( context, R.anim.ok_small );
        }
        txtOR.startAnimation(aniOK);
    }

    void nextAnimation( final boolean isNext ) {
        if (!isAnimatingQuestion) {
            isAnimatingQuestion = true;
            Context context = getActivity().getBaseContext();

            final Animation animation_1 = AnimationUtils.loadAnimation(context, R.anim.ani_small_1);
            final Animation animation_2 = AnimationUtils.loadAnimation(context, R.anim.ani_small_2);
            animation_1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    txtAnswerOne.setVisibility(View.GONE);
                    txtAnswerTwo.setVisibility(View.GONE);

                    txtAnswerOnePercent.setVisibility(View.GONE);
                    txtAnswerTwoPercent.setVisibility(View.GONE);

                    txtAnswerOne.setTextSize(20);
                    txtAnswerTwo.setTextSize(20);

                    answerOneView.setEnabled(true);
                    progAnswerOne.setProgressDrawable(getResources().getDrawable(R.drawable.prog_blue));
                    progAnswerOne.setProgress(0);

                    answerTwoView.setEnabled(true);
                    progAnswerTwo.setProgressDrawable(getResources().getDrawable(R.drawable.prog_red));
                    progAnswerTwo.setProgress(0);

                    btnSkip.setChecked(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    progAnswerOne.setAnimation(null);
                    progAnswerTwo.setAnimation(null);

                    txtAnswerOne.setAnimation(null);
                    txtAnswerTwo.setAnimation(null);

                    btnHeart.setAnimation(null);
                    btnComment.setAnimation(null);
                    txtAnswerCnt.setAnimation(null);

                    lnGroupTxt.setAnimation(null);
                    txtOR.setAnimation(null);

                    loadAnimation(false);

                    isClickSegment = false;

                    //Load next question in view
                    if( isNext == true ) {
                        questionsArray.remove(0);
                        invalidateQuiz();

                        if (questionsArray.size() <= 6) {
                            if (!loadingQuestions) {
                                load_questions_new();
                            }
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            progAnswerOne.startAnimation(animation_1);
            progAnswerTwo.startAnimation(animation_2);

            final Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.ani_small);
            btnHeart.startAnimation(animation1);
            btnComment.startAnimation(animation1);

            final Animation animation5 = AnimationUtils.loadAnimation(context, R.anim.ani_small);
            txtAnswerCnt.startAnimation(animation5);

            final Animation animation4 = AnimationUtils.loadAnimation(context, R.anim.ani_small);
            lnGroupTxt.startAnimation(animation4);

            if (isNext == true) {
                final Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.ok_big);
                txtOR.startAnimation(animation2);
            } else {
                final Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.ani_small);
                txtOR.startAnimation(animation2);
            }
        }
    }

    void invalidateQuiz() {
        Question obj = questionsArray.get(0);
        txtAnswerOne.setText(obj.answer1);
        txtAnswerTwo.setText(obj.answer2);
        nLike = obj.likes;
        txtLikeCnt.setText( " x " + nLike );
        txtAnswerCnt.setText( "Answer Count: " + obj.totalAnswers );
        if( obj.author != null && obj.author.length() > 0 ) {
            txtAuthor.setText(obj.author); txtAuthor.setVisibility(View.VISIBLE);
        } else {
            txtAuthor.setVisibility(View.INVISIBLE);
        }

        if( obj.didLike == 1 ) {
            btnHeart.setSelected(true);
        } else
            btnHeart.setSelected(false);

        if( obj.hasAnswered > 0 || (G.member_id==obj.author_id && obj.totalAnswers>0) ) {
            showAnswer( obj, true );
        }

        if( isShow == true && G.member_id == obj.author_id ) {
            btnAlert.setBackgroundResource(R.drawable.trash);
        }
    }

    void clearTitle() {
        btn1.setSelected(false);  btn1.setTextColor(Color.WHITE);
        btn2.setSelected(false);  btn2.setTextColor(Color.WHITE);
        btn3.setSelected(false);  btn3.setTextColor(Color.WHITE);
    }

    Dialog myDialog;
    public void onClick( View v ) {

        int vId = v.getId();
        switch ( vId ) {
            case R.id.lnSpelling:
            case R.id.lnMark:
            case R.id.lnJunk:
            case R.id.lnWrong:
            case R.id.lnRport:
                myDialog.dismiss();
                Question quiz = questionsArray.get(0);
                reportQuestion(quiz._id, vId);
                break;
            case R.id.tvCancel: myDialog.dismiss(); break;
            case R.id.btnAlert:
                if( questionsArray.size() == 0 )
                    return;
                Question quiz1 = questionsArray.get(0);
                if( isShow == true && quiz1.author_id == G.member_id ) {
                    G.showDialog( getActivity(), "Delete Process" );
                } else {
                    rlActionSheet.startAnimation(aniSlideUp);
                    myDialog.show();
                }
                break;
            case R.id.btnBottom:
                if( viewStyle == C.SCR_COMPOSE ) {
                    int forChild = preferences.getInt("childrenSwitch", -1);
                    post_new_question( G.strQuiz, G.strAns, String.valueOf(forChild) );
                }
                else {
                    String message = "I want to share.";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
                    G.mpSkip.start();
                }
                break;
        }

        if( btn1 == v || btn2 == v || btn3 == v ) {
            clearTitle();
            v.setSelected(true);
            ((TextView)v).setTextColor(getResources().getColor(R.color.navColor));
            isClickSegment = true;
            load_questions_new();
        }
        else if( txtAuthor == v ) {

            Question quiz = questionsArray.get(0);
            ProfileFragment frgProfile = new ProfileFragment();
            Bundle b = new Bundle();
            b.putInt("userId", quiz.author_id);
            frgProfile.setArguments(b);
            G.act.pushFrag(frgProfile );

        }
        else if( v == answerTwoView || v == answerOneView ) {
            if(  viewStyle == C.SCR_COMPOSE ) {
                FragInput frgInput = new FragInput();
                if( v == answerOneView )
                    frgInput.isBlue = true;
                G.act.pushFrag(frgInput );

            } else {
                Question quiz = questionsArray.get(0);
                if( quiz.author_id == G.member_id && isShow == true ) {
                    G.showDialog(getActivity(), "You can't answer about your question.");
                    return;
                }

                int answer = 1;
                if( answerTwoView == v ) {
                    submit_single_answer( String.valueOf(quiz._id), "2");
                    answer = 2;
                } else
                    submit_single_answer( String.valueOf(quiz._id), "1");

                if( isShow == true )
                    isUpdateSingleMode = true;

                quiz.hasAnswered = answer;
                showAnswer( quiz, false );
                btnSkip.setChecked(true);
            }
        }
        else if( btnSkip == v ) {

            if( isShow == true ) {
                String message = "I want to share.";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
            } else {
                if (btnSkip.isChecked()) {
                    int id = questionsArray.get(0)._id;
                    submit_single_answer( String.valueOf(id), "0");
                }
                btnSkip.setChecked(false);
                if (!isAnimatingQuestion) {
                    G.mpSkip.start();

                    if (isShow != true) {
                        nextAnimation(true);
                    }
                }
            }
        }
        else if( v == btnHeart ) {
            boolean isSelect = btnHeart.isSelected();
            if( isSelect == false ) {
                nLike++;
                try {
                    submitLikePHP();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                nLike--;
                try {
                    removeLikePHP();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            isSelect = !isSelect;
            txtLikeCnt.setText(String.format(" x %d", nLike));
            btnHeart.setSelected(isSelect);
        }
        else if( btnComment == v ) {

            if( questionsArray != null && questionsArray.size() > 0 ) {
                Question quiz = questionsArray.get(0);
                FragComment frgComment = new FragComment();
                Bundle b = new Bundle();
                b.putInt("quiz_id", quiz._id);
                frgComment.setArguments(b);
                G.act.pushFrag(frgComment);
            }
        }
    }

    private void showAnswer( Question quiz, boolean isAfter ) {
        Random r = new Random();
        int soundId = r.nextInt(7);
        G.mpAns[soundId].start();
        G.mpProg.start();

        if( quiz.hasAnswered == 2 ) { // red
            progAnswerTwo.setProgressDrawable( getResources().getDrawable(R.drawable.prog_red_click) );
        }
        else {
            progAnswerOne.setProgressDrawable( getResources().getDrawable(R.drawable.prog_blue_click) );
        }

        int answerOnePercent;
        int answerTwoPercent;

        if( isAfter == true ) {
            answerOnePercent = quiz.calAnswerAfter(true);
            answerTwoPercent = quiz.calAnswerAfter(false);

        } else {
            answerOnePercent = quiz.calAnswer(true);
            answerTwoPercent = quiz.calAnswer(false);
        }

        txtAnswerTwo.setTextSize(14);
        txtAnswerTwoPercent.setText(answerTwoPercent+"%");
        txtAnswerTwoPercent.setVisibility(View.VISIBLE);
        ProgressBarAnimation animQuiz = new ProgressBarAnimation( progAnswerTwo, 0, answerTwoPercent);
        animQuiz.setDuration(500);
        progAnswerTwo.startAnimation(animQuiz);

        txtAnswerOne.setTextSize(14);
        txtAnswerOnePercent.setText(answerOnePercent+"%");
        txtAnswerOnePercent.setVisibility(View.VISIBLE);
        ProgressBarAnimation anim = new ProgressBarAnimation( progAnswerOne, 0, answerOnePercent);
        anim.setDuration(500);
        progAnswerOne.startAnimation(anim);

        answerTwoView.setEnabled(false);
        answerOneView.setEnabled(false);
    }

    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        btnLeft = (ImageView)p.findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( isShow == false )
                    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                else {

                    int size = G.act.frgStacks.size();
                    if( size > 1 && isUpdateSingleMode == true ) {

                        String frgLast = G.act.frgStacks.get(size-2);
                        Fragment frg = getFragmentManager().findFragmentByTag(frgLast);

                        String frgClass = frg.getClass().getSimpleName();
                        if( frgClass.compareTo("FragAnswer") == 0 ){
                            ((FragAnswer)frg).loadData();
                        }
                    }
                    G.act.popFrag();
                }
            }
        });
    }

    class MyBounceInterpolator implements android.view.animation.Interpolator {
        double mAmplitude = 1;
        double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
}
