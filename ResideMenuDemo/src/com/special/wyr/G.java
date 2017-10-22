package com.special.wyr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.graphics.Typeface;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class G {

    static MediaPlayer mpSkip = null;
    static MediaPlayer [] mpAns = new MediaPlayer [7];
    static MediaPlayer mpProg = new MediaPlayer();

    public static KProgressHUD hud = null;

    public static int member_id;

    static public Typeface tf;
    static public Typeface tfBold;
    static public Typeface tfSuper;

    static public String strLang;

    static public String strQuiz;
    static public String strAns;

    static public MenuActivity act;

    public static int stack_cnt = 0;


    static void showDialog(Context context, String message ) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).create();
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}
