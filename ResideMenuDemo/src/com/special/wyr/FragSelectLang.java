package com.special.wyr;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;

import org.w3c.dom.Text;


public class FragSelectLang extends Fragment implements View.OnClickListener {

    private View p;
    TextView btnCancel, btnSave;

    String curLang;

    RelativeLayout rnEnglish, rnSpanish;
    View vChkEn, vChkSp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        p = inflater.inflate(R.layout.select_lang, container, false);
        rnEnglish = (RelativeLayout)p.findViewById(R.id.rnEnglish);
        rnSpanish = (RelativeLayout)p.findViewById(R.id.rnSpanish);
        btnCancel = (TextView)p.findViewById(R.id.btnCancel); btnCancel.setOnClickListener(this);
        btnSave = (TextView)p.findViewById(R.id.btnSave); btnSave.setOnClickListener(this);

        rnEnglish.setOnClickListener(this);
        rnSpanish.setOnClickListener(this);

        vChkEn = p.findViewById(R.id.vChkEn);
        vChkSp = p.findViewById(R.id.vChkSp);

        curLang = G.strLang;
        if( curLang.contentEquals("English") == true ) {
            vChkEn.setVisibility(View.VISIBLE); vChkSp.setVisibility(View.GONE);
        } else {
            vChkSp.setVisibility(View.VISIBLE); vChkEn.setVisibility(View.GONE);
        }

        return p;
    }

    public void onClick( View v ) {
        if( v == rnEnglish ) {
            vChkEn.setVisibility(View.VISIBLE); vChkSp.setVisibility(View.GONE);
            curLang = "English";
        } else if( rnSpanish == v ){
            vChkSp.setVisibility(View.VISIBLE); vChkEn.setVisibility(View.GONE);
            curLang = "Spanish";
        } else if( v == btnCancel ) {
            G.act.popFrag();
        } else if( v == btnSave ) {
            G.strLang = curLang;
            G.act.popFrag();
        }
    }
}
