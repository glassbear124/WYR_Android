package com.special.wyr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.wyr.model.Comment;
import com.special.wyr.utils.CircleImageView;
import com.special.wyr.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragComment extends Fragment implements View.OnClickListener {

    int quizId;
    int startPos = 0;

    ArrayList<Comment> comments = new ArrayList<Comment>();

    void load_comments_new() {
        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";
        // sample quizId: 86
        url = C.base + "load_comments_new.php?question_id=" + quizId + "&numberOfCommentsLoaded=" + startPos;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            G.hud.dismiss();
                            JSONArray arr = new JSONArray(response);
                            if( startPos == 0 )
                                comments.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                Comment c = new Comment(obj);
//                                comments.add(c);
                                comments.add(0, c);
                            }

                            if( comments.size() == 50 ) {
                                isHaveMore = true;
                                startPos += 50;
                            }
                            else
                                isHaveMore = false;

                            if( comments.size() == 0 )
                                txtTitle.setText("No Comment");

                            myAdapter.notifyDataSetChanged();
                            scrollMyListViewToBottom();

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

    void remove_single_comment( final Comment comment ) {
        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = "";
        // sample quizId: 86
        url = C.base + "remove_single_comment.php?question_id=" + quizId + "&comment_id=" + comment._id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    G.hud.dismiss();
                    if( response.compareTo("Suceess") == 0 ) {
                        comments.remove(comment);
                        myAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getActivity(), "Failed to remove comment.", Toast.LENGTH_SHORT);
                    }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.hud.dismiss();
                G.showDialog( getActivity(), C.err_msg );
                Toast.makeText(getActivity(), "Failed to remove comment.", Toast.LENGTH_SHORT);
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }


    void submitComment( final int question_id, final String comment, final String username )
    {
        G.hud.show();

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        String url = C.base + "submitComment.php?member_id="+G.member_id;
        Log.d("submitComment URL", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                G.hud.dismiss();
                editComment.setText("");
                load_comments_new();
                startPos = 0;
                isHaveMore = false;
                Log.d("submitComment Response", response);
                //<<<<CHECK RESPONSE FOR SUCCESS HERE<<<<<<<<<
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                G.hud.dismiss();
                G.showDialog( getActivity(), C.err_msg );
                Log.d("submitComment Error", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("question_id", String.valueOf(question_id));
                params.put("text", comment);
                params.put("username", username);
                params.put("tag", "");
                Log.d("submitComment Params", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void scrollMyListViewToBottom() {
        lstView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lstView.setSelection(lstView.getCount() - 1);
            }
        });
    }

    ListView lstView;
    CustomAdapter myAdapter;

    boolean isHaveMore = false;

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
            if( isHaveMore == true )
                return comments.size() + 1;
            else
                return comments.size();
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
            TextView txtDate;
            TextView txtName;
            TextView txtComment;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View rowView;
            if( isHaveMore == true && position == 0 ) {
                rowView = inflater.inflate(R.layout.cell_more, null);
                rowView.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public  void onClick(View v) {
                        load_comments_new();
                    }
                });
            }
            else {
                rowView = inflater.inflate(R.layout.cell_comment, null);
                Holder holder=new Holder();

                final Comment c;
                if( isHaveMore == true )
                    c = comments.get(position-1);
                else
                    c = comments.get(position);

                holder.imgThumb = (CircleImageView) rowView.findViewById(R.id.imgThumb);
                holder.txtName = (TextView)rowView.findViewById(R.id.txtName);
                holder.txtComment = (TextView)rowView.findViewById(R.id.txtComment);
                holder.txtDate = (TextView)rowView.findViewById(R.id.txtDate);

                holder.imgThumb.setBorderWidth(4);
                holder.imgThumb.setBorderColor(Color.BLACK);
                holder.imgThumb.setImageResource(C.rank_id[c.rank]);

                holder.imgThumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProfile(c.member_id);
                    }
                });

                holder.txtName.setText(c.username);
                holder.txtName.setTag(c);
                holder.txtName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProfile(c.member_id);
                    }
                });

                holder.txtComment.setText(c.text);
                holder.txtDate.setText( TimeAgo.toDuration(c.time) );
                rowView.setId(position);
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( c.member_id == G.member_id ) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity(), R.style.Theme_AppCompat_Dialog );
                            alertDialogBuilder
                                    .setMessage("Do you want to delete this comment?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            remove_single_comment( c );
                                        }
                                    })
                                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            showProfile(c.member_id);
                        }
                    }
                });
            }


            return rowView;
        }

        void showProfile( int _id ) {
            ProfileFragment frgProfile = new ProfileFragment();
            Bundle b = new Bundle();
            b.putInt("userId", _id);
            frgProfile.setArguments(b);
            G.act.pushFrag(frgProfile);
        }
    }

    EditText editComment;
    TextView txtTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View p = inflater.inflate(R.layout.comment, container, false);
        txtTitle = (TextView)p.findViewById(R.id.txtTitle);

        Bundle b = getArguments();
        quizId = b.getInt("quiz_id");

        editComment = (EditText) p.findViewById(R.id.editComment);
        p.findViewById(R.id.btnLeft).setOnClickListener(this);
        p.findViewById(R.id.btnRefresh).setOnClickListener(this);
        lstView = (ListView)p.findViewById(R.id.lstView);
        myAdapter = new CustomAdapter();
        lstView.setAdapter(myAdapter);

        p.findViewById(R.id.txtSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strComment = editComment.getText().toString();
                submitComment( quizId, strComment, "user");
            }
        });
        load_comments_new();
        return p;
    }

    public void onClick( View v ) {
        int vId = v.getId();
        switch(vId) {
            case R.id.btnLeft:    G.act.popFrag();     break;
            case R.id.btnRefresh:
                startPos = 0;
                load_comments_new();
                break;
        }
    }
}
