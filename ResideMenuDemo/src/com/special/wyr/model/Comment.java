package com.special.wyr.model;


import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class Comment {

    public int _id;
    public int member_id;
    public int question_id;
    public String text;
    public String tagInfo;
    public String username;
    public int rank;
    public long time;

    public  Comment(JSONObject obj ) {

/*

"id":"199725",
"member_id":"492707",
"question_id":"86",
"text":"I have a dick",
"tagInfo":"[\n [\n\n ],\n [\n\n ]\n]",
"username":"KN2005",
"rank":"3",
"time":"15040286"

*/

        try {
            _id = obj.getInt("id");
            member_id = obj.getInt("member_id");
            question_id = obj.getInt("question_id");
            text = obj.getString("text");
            tagInfo = obj.getString("tagInfo");
            username = obj.getString("username");
            rank = obj.getInt("rank");
            time = obj.getLong("time");

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
