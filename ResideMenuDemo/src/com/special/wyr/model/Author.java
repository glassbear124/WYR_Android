package com.special.wyr.model;


import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class Author {

/*
    "answers":"0",
            "composes":"19",
            "username":":Cod.47:",
            "rank":"5",
            "shares":"4",
            "promotions":"0",
            "following":"0",
            "followers":"3",
            "isFollowing":"0"
    */

    public int answers;
    public int composes;
    public String username;
    public int rank;
    public int shares;
    public int promotions;
    public int following;
    public int followers;
    public int isFollowing;


    public  Author() {

    }

    public Author( JSONObject obj) {
        try {

            answers = obj.getInt("answers");
            username = obj.getString("username");
            composes = obj.getInt("composes");
            rank = obj.getInt("rank");
            shares = obj.getInt("shares");
            promotions = obj.getInt("promotions");
            following = obj.getInt("following");
            followers = obj.getInt("followers");
            isFollowing = obj.getInt("isFollowing");

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
