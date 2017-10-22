package com.special.wyr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.ResideMenu.ResideMenu;
import com.special.wyr.model.Author;
import com.special.wyr.utils.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    public int userId;
    public Author author;

    TextView [] txtRights = new TextView[4];

    void load_followers() {
        G.hud.show();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";

        url = C.base + "load_followers.php?member_id="+G.member_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);
                            try {
                                String following = obj.getString("following");
                                String follower = obj.getString("followers");

                                txtFollowingCnt.setText( following );
                                txtFollowerCnt.setText( follower );

                            } catch ( JSONException e ) {
                                G.hud.dismiss();
                                G.showDialog( getActivity(), C.err_msg );
                                e.printStackTrace();
                            }

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

    void load_external_userinfo() {

        G.hud.show();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
        String url = "";

   //     userId = 2358;
        url = C.base + "load_external_userinfo.php?member_id="+G.member_id+
                "&user_id_to_load=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            author = new Author( arr.getJSONObject(0) );
                            fillView();
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

    void API_follow( final boolean isFollow ) {
        G.hud.show();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";

        String api = "handle_follow.php?member_id=";
        if( isFollow == false )
            api = "handle_unfollow.php?member_id=";

        url = C.base + api + G.member_id+ "&user_id_to_load=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        G.hud.dismiss();
                        if( response.compareTo("Success") == 0 ) {
                            drawTextFollow(isFollow);

                            if( isFollow == true ) {
                                author.isFollowing = 1;
                                author.followers++;
                            } else {
                                author.isFollowing = 0;
                                author.followers--;
                            }
                            txtFollowerCnt.setText( String.valueOf(author.followers) );

                        } else {
                            String error = "Failed to follow. Play try again later.";
                            if( isFollow == false)
                                error = "Failed to unfollow. Play try again later.";
                            Toast.makeText( getActivity(), error, Toast.LENGTH_SHORT );
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.hud.dismiss();
                String error1 = "Failed to follow. Play try again later.";
                if( isFollow == false)
                    error1 = "Failed to unfollow. Play try again later.";
                Toast.makeText( getActivity(), error1, Toast.LENGTH_SHORT );
                G.showDialog( getActivity(), C.err_msg );
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    void fillView() {

        imgAvatar.setImageResource(C.rank_id[author.rank]);
        txtUserName.setText( "Rank: "+C.rank_name[author.rank]);

        txtTitle.setText( author.username );
        txtFollowerCnt.setText( String.valueOf(author.followers) );
        txtFollowingCnt.setText( String.valueOf(author.following) );
        if( author.isFollowing == 0 ) {
            drawTextFollow(false);
        } else {
            drawTextFollow(true);
        }

        txtRights[0].setText( String.valueOf(author.answers));
        txtRights[1].setText( String.valueOf(author.composes));
        txtRights[2].setText( String.valueOf(author.promotions));
        txtRights[3].setText( String.valueOf(author.shares));
    }

    private ResideMenu resideMenu;

    private View p;
    CircleImageView imgAvatar;
    TextView txtLevel, txtFollow;
    LinearLayout lnMajor, lnDetail;
    public boolean isMyprofile;
    TextView txtTitle;

    TextView txtUserName;
    TextView txtFollowing, txtFollowingCnt, txtFollower, txtFollowerCnt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        p = inflater.inflate(R.layout.profile, container, false);
        setUpViews();

        txtTitle = (TextView)p.findViewById(R.id.txtTitle);

        imgAvatar = (CircleImageView)p.findViewById(R.id.imgAvatar);
        imgAvatar.setBorderColor(Color.BLACK);
        imgAvatar.setBorderWidth(4);

        txtUserName = (TextView)p.findViewById(R.id.txtUserName);

        txtFollowing = (TextView)p.findViewById(R.id.txtFollowing); txtFollowing.setOnClickListener(this);
        txtFollowingCnt = (TextView)p.findViewById(R.id.txtFollowingCnt); txtFollowingCnt.setOnClickListener(this);
        txtFollower = (TextView)p.findViewById(R.id.txtFollower); txtFollower.setOnClickListener(this);
        txtFollowerCnt = (TextView)p.findViewById(R.id.txtFollowerCnt); txtFollowerCnt.setOnClickListener(this);
        lnMajor = (LinearLayout) p.findViewById(R.id.lnMajor);

        txtLevel = (TextView)p.findViewById(R.id.txtLevel);
        txtFollow = (TextView)p.findViewById(R.id.txtFollow); txtFollow.setOnClickListener(this);

        String [] commonStr = getResources().getStringArray(R.array.profile_common_items);
        String [] detailStr;

        if( isMyprofile == true ) {
            txtFollow.setVisibility(View.GONE);
            detailStr = getResources().getStringArray(R.array.my_profile_detail_items);
            lnMajor.addView( inflater.inflate( R.layout.cell_profile_fb, null ) );
        } else {
            txtLevel.setVisibility(View.GONE);
            detailStr = getResources().getStringArray(R.array.profile_detail_items );
        }

        for( int i = 0; i < commonStr.length + detailStr.length; i++ ) {

            int lnID;
            if( i < commonStr.length ) {
                lnID = R.layout.cell_profile;
            } else {
                lnID = R.layout.cell_profile_detail;
            }

            View v = inflater.inflate(lnID, null);
            TextView txtLeft = (TextView) v.findViewById(R.id.txtLeft);
            if ( i < commonStr.length ) {
                txtLeft.setText(commonStr[i]);
                txtRights[i] = (TextView)v.findViewById(R.id.txtRight);
            } else {

                v.setTag(i-commonStr.length);
                txtLeft.setText(detailStr[i - commonStr.length]);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int i = (int)view.getTag();
                        FragAnswer frgAnswer = new FragAnswer();
                        Bundle b = new Bundle();
                        b.putInt("user_id", userId);
                        b.putInt("index", i+1);
                        b.putBoolean("is_my_profile", isMyprofile );
                        frgAnswer.setArguments(b);
                        G.act.pushFrag(frgAnswer);
                    }
                });
            }

            if( i == commonStr.length + detailStr.length - 1 ) {
                View vSep = v.findViewById(R.id.vSep);
                vSep.setVisibility(View.GONE);
            }
            lnMajor.addView(v);
        }

        p.findViewById(R.id.btnRight).setOnClickListener(this);

        lnDetail = (LinearLayout)p.findViewById(R.id.lnDetail);
        if( isMyprofile == false )
            lnDetail.setVisibility(View.GONE);

        init();
        return p;
    }

    public void init() {

        if( isMyprofile == true ) {
            load_followers();
        }

        Bundle b = getArguments();
        if( b == null )
            return;
        userId = b.getInt("userId", 0);
        if( userId == 0 )
            return;
        load_external_userinfo();
    }

    void drawTextFollow( boolean isSel ) {

        if( isSel == true ) {
            txtFollow.setText( "Following" );
            txtFollow.setTextColor(Color.WHITE);
            txtFollow.setBackgroundResource(R.drawable.round_corner_green);
        } else {
            txtFollow.setText("Follow");
            txtFollow.setTextColor(Color.BLACK);
            txtFollow.setBackgroundResource(R.drawable.round_corner);
        }
        txtFollow.setSelected(isSel);
        txtFollow.setEnabled(true);
    }

    public void onClick( View v ) {

        int vId = v.getId();

        switch (vId) {
            case R.id.btnRight:
                if( isMyprofile == true ) {
                    load_followers();
                } else {
                    load_external_userinfo();
                }
                break;

            case R.id.txtFollow:
                boolean isSel = txtFollow.isSelected();
                isSel = !isSel;
                txtFollow.setEnabled(false);
                API_follow(isSel);
                break;

            case R.id.txtFollower:
            case R.id.txtFollowerCnt:
                UsersFragment frgUser = new UsersFragment();
                frgUser.title = "Followers";
                frgUser.isFollower = true;
                frgUser.userId = userId;
                G.act.pushFrag(frgUser);
                break;

            case R.id.txtFollowing:
            case R.id.txtFollowingCnt:
                UsersFragment frgFollowing = new UsersFragment();
                frgFollowing.title = "Following";
                frgFollowing.userId = userId;
                G.act.pushFrag(frgFollowing);
                break;

        }
    }

    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();

        ImageView btnLeft = (ImageView)p.findViewById(R.id.btnLeft);
        if( isMyprofile == false )
            btnLeft.setImageResource(R.drawable.icn_back);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( isMyprofile == true )
                    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                else
                    G.act.popFrag();
            }
        });
    }
}
