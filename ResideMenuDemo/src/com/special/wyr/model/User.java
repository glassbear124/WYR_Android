package com.special.wyr.model;


import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class User {

/*
"member_id":"114",
"username":"Unknown",
"rank":"10"
    */

    public int member_id;
    public String username;
    public int rank;


    public  User() {

    }

    public User( JSONObject obj) {
        try {

            if( obj.has("member_id") )
                member_id = obj.getInt("member_id");
            if( obj.has("username"))
                username = obj.getString("username");
            if( obj.has("rank"))
                rank = obj.getInt("rank");

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
