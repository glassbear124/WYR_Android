package com.special.wyr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.ResideMenu.ResideMenu;
import com.special.wyr.model.Author;
import com.special.wyr.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class FragAnswer extends Fragment implements View.OnClickListener {

    int userId, index;
    boolean isMyProfile;
    ArrayList<Question> questions = new ArrayList<Question>();


    void load_questions_based_on_answers() {

        G.hud.show();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";

        url = C.base + "load_questions_based_on_answers.php?member_id="+G.member_id+
                "&user_id=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            questions.clear();
                            for( int i = 0; i < size; i++ ) {
                                Question quiz = new Question( arr.getJSONObject(i));
                                questions.add(quiz);
                            }
                            myAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            G.hud.dismiss();
                            G.showDialog( getActivity(), C.err_msg );
                            e.printStackTrace();
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

    void load_questions_based_on_likes() {

        G.hud.show();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";

//        userId = 3408;
        url = C.base + "load_questions_based_on_likes.php?member_id="+G.member_id+
                "&user_id=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            questions.clear();
                            for( int i = 0; i < size; i++ ) {
                                Question quiz = new Question( arr.getJSONObject(i));
                                questions.add(quiz);
                            }
                            myAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            G.hud.dismiss();
                            G.showDialog( getActivity(), C.err_msg );
                            e.printStackTrace();
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

    void load_user_composed_questions() {

        G.hud.show();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";

//        userId = 4376;
        url = C.base + "load_user_composed_questions.php?member_id="+G.member_id+
                "&user_id=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            questions.clear();
                            for( int i = 0; i < size; i++ ) {
                                Question quiz = new Question( arr.getJSONObject(i));
                                questions.add(quiz);
                            }
                            myAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            G.hud.dismiss();
                            G.showDialog( getActivity(), C.err_msg );
                            e.printStackTrace();
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


    private View p;

    ListView lstView;
    CustomAdapter myAdapter;

    public class CustomAdapter extends BaseAdapter {

        Context context;
        private LayoutInflater inflater=null;
        public CustomAdapter() {
            // TODO Auto-generated constructor stub
            context=getActivity().getBaseContext();
            inflater = (LayoutInflater)context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return questions.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView txtAns1;
            TextView txtAns2;
            TextView txtRight;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.cell_answer, null);
            rowView.setId(position);

            holder.txtAns1 = (TextView)rowView.findViewById(R.id.txtAns1);
            holder.txtAns2 = (TextView)rowView.findViewById(R.id.txtAns2);
            holder.txtRight = (TextView)rowView.findViewById(R.id.txtRight);

            Question quiz = questions.get(position);

            if( (isMyProfile == true && quiz.totalAnswers>0)  || quiz.hasAnswered > 0 ) {
                int a1 = quiz.calAnswerAfter(true);
                int a2 = quiz.calAnswerAfter(false);
                holder.txtAns1.setText( a1 + "% - " + quiz.answer1 );
                holder.txtAns2.setText( a2 + "% - " + quiz.answer2 );
            } else {
                holder.txtAns1.setText( quiz.answer1 );
                holder.txtAns2.setText( quiz.answer2 );
            }

            if( quiz.hasAnswered > 0 ) {
                holder.txtRight.setVisibility(View.VISIBLE);
            } else
                holder.txtRight.setVisibility(View.INVISIBLE);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Question quiz = questions.get(position);
                    HomeFragment frgHome = new HomeFragment();
                    frgHome.isShow = true;
                    frgHome.nSingleQuizId = quiz._id;
                    G.act.pushFrag(frgHome);
                }
            });
            return rowView;
        }
    }

    TextView txtBackTitle, txtTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        p = inflater.inflate(R.layout.answer, container, false);

        p.findViewById(R.id.btnLeft).setOnClickListener(this);
        p.findViewById(R.id.btnRefresh).setOnClickListener(this);

        txtBackTitle = (TextView)p.findViewById(R.id.txtBackTitle);
        txtTitle = (TextView)p.findViewById(R.id.txtTitle);

        lstView = (ListView)p.findViewById(R.id.lstView);
        myAdapter = new CustomAdapter();
        lstView.setAdapter(myAdapter);

        Bundle b = getArguments();
        userId = b.getInt("user_id");
        index = b.getInt("index");
        isMyProfile = b.getBoolean("is_my_profile");

        loadData();
        return p;
    }

    public void loadData() {
        if( index == 1 ) {
            txtTitle.setText("Questions");
            load_user_composed_questions();
        }
        else if( index == 2 ){
            txtTitle.setText("Likes");
            load_questions_based_on_likes();
        } else {
            txtTitle.setText("Answers");
            load_questions_based_on_answers();
        }
    }

    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.btnLeft:
                G.act.popFrag();
                break;
            case R.id.btnRefresh:
                loadData();
                break;
        }
    }

}
