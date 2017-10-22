package com.special.wyr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.special.ResideMenu.ResideMenu;
import com.special.wyr.model.Item;
import com.special.wyr.model.Nitification;
import com.special.wyr.utils.CircleImageView;
import com.special.wyr.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotificationFragment extends Fragment {

    ArrayList<Nitification> notifications = new ArrayList<Nitification>();

    void loadNotifications() {

        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = C.base + "loadNotifications.php";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                G.hud.dismiss();
                try {

                    JSONArray arr = new JSONArray(response);
                    notifications.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        notifications.add( new Nitification(obj) );
                    }
                    myAdapter.notifyDataSetChanged();
                } catch ( JSONException e ) {
                    G.showDialog( getActivity(), C.err_msg );
                    e.printStackTrace();
                }
                //<<<<CHECK RESPONSE FOR SUCCESS HERE<<<<<<<<<
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", error.toString());
                G.showDialog( getActivity(), C.err_msg );
                G.hud.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put( "member_id", String.valueOf(G.member_id) );
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private View p;
    ResideMenu resideMenu;
    private ListView listView;

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
            return notifications.size();
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
            TextView txtComment;
            TextView txtDate;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            CustomAdapter.Holder holder=new CustomAdapter.Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.cell_comment, null);
            rowView.setId(position);

            Nitification notification = notifications.get(position);


            holder.imgThumb = (CircleImageView) rowView.findViewById(R.id.imgThumb);
            holder.imgThumb.setBorderWidth(4);
            holder.imgThumb.setBorderColor(Color.BLACK);

            holder.txtName = (TextView)rowView.findViewById(R.id.txtName);
            holder.txtName.setVisibility(View.GONE);
            holder.txtDate = (TextView)rowView.findViewById(R.id.txtDate);
            holder.txtComment = (TextView)rowView.findViewById(R.id.txtComment);

            holder.txtDate.setText( TimeAgo.toDuration(notification.time) );

            if( notification.info != null ) {
                if( notification.info.rank > 0 )
                    holder.imgThumb.setImageResource(C.rank_id[notification.info.rank]);
                else {
                    if( notification.kind.compareTo("follow") == 0 )
                        holder.imgThumb.setImageResource(R.drawable.noti_follow);
                    else if( notification.kind.compareTo("like") == 0 )
                        holder.imgThumb.setImageResource(R.drawable.noti_like);
                    else
                        holder.imgThumb.setImageResource(R.drawable.noti_comment);
                }
                holder.txtComment.setText(notification.message);

            } else {
                holder.txtComment.setText(notification.message);
                holder.txtName.setVisibility(View.GONE);
                holder.imgThumb.setVisibility(View.GONE);
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Nitification notification = notifications.get(position);
                    if( notification.info == null )
                        return;

                    if( notification.kind.compareTo("like") == 0 ||
                        notification.kind.compareTo("comment") == 0) {
                        // quiz
                        HomeFragment frgQuiz = new HomeFragment();
                        frgQuiz.nSingleQuizId = notification.info.question_id;
                        frgQuiz.isShow = true;
                        G.act.pushFrag(frgQuiz);
                    }
                    else if( notification.kind.compareTo("follow") == 0 ) {
                        // profile
                        ProfileFragment frgProfile = new ProfileFragment();
                        Bundle b = new Bundle();
                        b.putInt( "userId", notification.info.liker_id);
                        frgProfile.setArguments(b);
                        G.act.pushFrag( frgProfile );
                    } else
                        return;
                }
            });
            return rowView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        p = inflater.inflate(R.layout.notification, container, false);
        setUpViews();

        p.findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNotifications();
            }
        });

        loadNotifications();

        listView  = (ListView) p.findViewById(R.id.listView);
        myAdapter = new CustomAdapter();
        listView.setAdapter(myAdapter);
        return p;
    }


    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        p.findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }
}
