package com.special.wyr.model;

import org.json.JSONObject;

public class Nitification {

    /*
    "message":"Carls_1997 is now following you",
            "time":31238313,
            "info":{
        "kind":"follow",
                "liker_id":"793970",
                "liker_name":"Carls_1997",
                "author_id":"590694",
                "rank":"3"
    },
            "kind":"follow"
    */

    public String message;
    public long time;
    public Info info = null;
    public String kind;

    public  Nitification() {

    }

    public Nitification(JSONObject obj) {
        try {
            if( obj.has("message") )
                message = obj.getString("message");
            if( obj.has("kind") )
                kind = obj.getString("kind");

            if( obj.has("time"))
                time = obj.getLong("time");
            if( obj.has("info") ) {
                JSONObject objInfo = obj.getJSONObject("info");
                info = new Info(objInfo);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public class Info {
        public Info() {

        }

        public String kind;
        public int liker_id;
        public String liker_name;
        public int author_id;
        public int rank;
        public int question_id;

        public Info(JSONObject obj) {
            try {
                if( obj.has("kind") )
                    kind = obj.getString("kind");
                if( obj.has("liker_name") )
                    liker_name = obj.getString("liker_name");

                if( obj.has("liker_id"))
                    liker_id = obj.getInt("liker_id");
                if( obj.has("author_id"))
                    author_id = obj.getInt("author_id");
                if( obj.has("rank"))
                    rank = obj.getInt("rank");

                if( obj.has("question_id"))
                    question_id = obj.getInt("question_id");

            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
    }
}
