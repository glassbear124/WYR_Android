package com.special.wyr.model;


import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class Question {

    public int _id;
    public String answer1;
    public String answer2;
    public int likes;
    public int totalAnswers;
    public int totalAnswer1;
    public int totalAnswer2;
    public int author_id;
    public String author;
    public String category;
    public int promoted;
    public int  highlighted_author;
    public long random_order;
    public int didLike;


 /*
    "id":"595",
    "answer1":"Listen to skrillex",
    "answer2":"Listen to avicii",
    "likes":"12",
    "totalAnswers":"3545",
    "totalAnswer1":"1806",
    "totalAnswer2":"1739",
    "author_id":"2358",
    "author":":Cod.47:",
    "category":null,
    "promoted":"0",
    "highlighted_author":"0",
    "random_order":"217132",
    "didLike":0
*/

    public long created_date;
    public int for_children;
    public int junk;
    public int confirmations;
    public String highlighted_color;
    public int like_count;
    public int comment_count;

    public int answer_count;
    public int answer1_answers;
    public int answer2_answers;

    public String language;

    public int hasAnswered;



 /*
 "id":"744",
"author_id":"2358",
"author":":Cod.47:",
"answer1":"Watch pewdiepie",
"answer2":"Toby games",
"created_date":"1455598067",
"promoted":"0",
"for_children":"1",
"junk":"1",
"confirmations":"0",
"category":null,
"highlighted_author":"0",
"highlighted_color":null,
"like_count":"13",
"comment_count":"1",
"random_order":"278735",
"answer_count":"3623",
"answer1_answers":"2377",
"answer2_answers":"1246",
"language":"en",
"hasAnswered":"0",
"totalAnswers":13,
"totalAnswer1":8,
"totalAnswer2":5,
"likes":1,
"didLike":0
  */

/*

"id":"321",
"author_id":"796",
"author":"mathilde3001",
"answer1":"Always summer",
"answer2":"Always winter",
"created_date":"1455393315",
"promoted":"0",
"for_children":"6",
"junk":"6",
"confirmations":"0",
"category":null,
"highlighted_author":"0",
"highlighted_color":null,
"like_count":"214",
"comment_count":"28",
"random_order":"319411",
"answer_count":"20509",
"answer1_answers":"15619",
"answer2_answers":"4889",
"language":"en",
"totalAnswers":2,
"totalAnswer1":2,
"totalAnswer2":0,
"likes":23,
"didLike":0,
"hasAnswered":0

*/

    public  Question(JSONObject obj ) {

        String s;

        try {

            if( obj.has("id") == true )
                _id = obj.getInt("id");

            if( obj.has("answer1") == true )
                answer1 = obj.getString("answer1");

            if( obj.has("answer2") == true )
                answer2 = obj.getString("answer2");

            if( obj.has("likes") == true )
                likes = obj.getInt("likes");

            if( obj.has("totalAnswers") == true )
                totalAnswers = obj.getInt("totalAnswers");

            if( obj.has("totalAnswer1") == true )
                totalAnswer1 = obj.getInt("totalAnswer1");

            if( obj.has("totalAnswer2") == true )
                totalAnswer2 = obj.getInt("totalAnswer2");

            if( obj.has("author_id") == true )
                author_id = obj.getInt("author_id");
            if( obj.has("promoted") == true )
                promoted = obj.getInt("promoted");
            if( obj.has("highlighted_author") == true )
                highlighted_author = obj.getInt("highlighted_author");
            if( obj.has("random_order") == true )
                random_order = obj.getLong("random_order");
            if( obj.has("didLike") == true )
                didLike = obj.getInt("didLike");

            if( obj.has("author") == true )
                author = obj.getString("author");
            if( obj.has("category") == true )
                category = obj.getString("category");

            if( obj.has("created_date") == true )
                created_date = obj.getLong("created_date");

            if( obj.has("language") == true )
                language = obj.getString("language");

            if( obj.has("highlighted_color") == true )
                highlighted_color = obj.getString("highlighted_color");

            if( obj.has("for_children") == true )
                for_children = obj.getInt("for_children");

            if( obj.has("junk") == true )
                junk = obj.getInt("junk");

            if( obj.has("confirmations") == true )
                confirmations = obj.getInt("confirmations");

            if( obj.has("like_count") == true )
                like_count = obj.getInt("like_count");

            if( obj.has("comment_count") == true )
                comment_count = obj.getInt("comment_count");

            if( obj.has("answer_count") == true )
                answer_count = obj.getInt("answer_count");

            if( obj.has("answer1_answers") == true )
                answer1_answers = obj.getInt("answer1_answers");

            if( obj.has("answer2_answers") == true )
                answer2_answers = obj.getInt("answer2_answers");

            if( obj.has("hasAnswered") == true )
                hasAnswered = obj.getInt("hasAnswered");

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public int calAnswer( boolean isFirst ) {
        float percent = 0;
        if( isFirst == true ) {
            percent = (float) (totalAnswer1+1) / (float) (totalAnswers+1) * 100;
        } else {
            percent = (float) (totalAnswer2+1) / (float) (totalAnswers+1) * 100;
        }
        return Math.round(percent);
    }


    public int calAnswerAfter( boolean isFirst ) {
        float answerOnePercent = 0;
        if( isFirst == true ) {
           answerOnePercent = (float) (totalAnswer1) / (float) (totalAnswers) * 100;
        } else {
           answerOnePercent = (float) (totalAnswer2) / (float) (totalAnswers) * 100;
        }
        return Math.round(answerOnePercent);
    }

}
