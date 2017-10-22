package com.special.wyr.model;


import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class Answer {

    public int _id;
    public int question_id;
    public int answer;

    public  Answer() {

    }

    public Answer( int ans, int quizId ) {
        answer = ans;
        question_id = quizId;
    }
}
