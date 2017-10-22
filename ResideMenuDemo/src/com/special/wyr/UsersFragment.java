package com.special.wyr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.wyr.model.Comment;
import com.special.wyr.model.Item;
import com.special.wyr.model.User;
import com.special.wyr.utils.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class UsersFragment extends Fragment implements View.OnClickListener {

    public boolean isFollower;
    public boolean isSearch;
    public String searchKey;
    public int userId;

    ArrayList<User> users = new ArrayList<User>();
    void load_userslist(  ) {

        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";

        if( isSearch == true ) {
            url = C.base + "load_userslist.php?type=search&member_id=" + G.member_id + "&search=" + searchKey;
        } else {
            if( isFollower == true ) {
                url = C.base + "load_userslist.php?type=followers&member_id=" + userId;
            }
            else
                url = C.base + "load_userslist.php?type=following&member_id="+ userId;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            users.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                User c = new User(obj);
                                users.add(c);
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
            return users.size();
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
            CircleImageView imgThumb;
            TextView txtName;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.cell_user, null);
            rowView.setId(position);

            holder.imgThumb = (CircleImageView) rowView.findViewById(R.id.imgThumb);
            holder.imgThumb.setBorderWidth(4);
            holder.imgThumb.setBorderColor(Color.BLACK);

            User u = users.get(position);

            holder.imgThumb.setImageResource(C.rank_id[u.rank]);
            holder.txtName = (TextView)rowView.findViewById(R.id.txtName);
            holder.txtName.setText(u.username);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    User u = users.get(position);
                    ProfileFragment frgProfile = new ProfileFragment();
                    Bundle b = new Bundle();
                    b.putInt("userId", u.member_id);
                    frgProfile.setArguments(b);

                    G.act.pushFrag(frgProfile);
                }
            });
            return rowView;
        }
    }

    public String title;
    TextView txtTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        p = inflater.inflate(R.layout.users, container, false);
        txtTitle = (TextView)p.findViewById(R.id.txtTitle);
        if( title != null && title.length() > 0 )
            txtTitle.setText(title);

        p.findViewById(R.id.btnLeft).setOnClickListener(this);
        p.findViewById(R.id.btnRefresh).setOnClickListener(this);
        lstView = (ListView)p.findViewById(R.id.lstView);
        myAdapter = new CustomAdapter();
        lstView.setAdapter(myAdapter);

        load_userslist();
        return p;
    }

    public void onClick( View v ) {
        int vid = v.getId();
        switch (vid) {
            case R.id.btnLeft:
                G.act.popFrag();
                break;
            case R.id.btnRefresh:
                load_userslist();
                break;
        }
    }
}
