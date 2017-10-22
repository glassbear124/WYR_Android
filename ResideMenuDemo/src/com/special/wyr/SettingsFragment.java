package com.special.wyr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private View p;
    LinearLayout lnMajor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        p = inflater.inflate(R.layout.settings, container, false);
        setUpViews();

        lnMajor = (LinearLayout) p.findViewById(R.id.lnMajor);

        String [] commonStr;
        commonStr = getResources().getStringArray(R.array.setting_items);

        for( int i = 0; i < commonStr.length; i++ ) {

            int lnID;
            if( i == 0 )
                lnID = R.layout.cell_setting_detail;
            else if( i < 5 )
                lnID = R.layout.cell_setting;
            else
                lnID = R.layout.cell_profile_detail;

            View v = inflater.inflate(lnID, null);
            TextView txtLeft = (TextView) v.findViewById(R.id.txtLeft);
            if ( i < commonStr.length ) {
                txtLeft.setText(commonStr[i]);
            }

            if( i == commonStr.length - 1 ) {
                View vSep = v.findViewById(R.id.vSep);
                vSep.setVisibility(View.GONE);
            }

            if( i == 0 ) {
                TextView txtDetail = (TextView)v.findViewById(R.id.txtDetail);
                txtDetail.setText(G.strLang);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        G.pushScreen( G.scr_setting, G.scr_lang );
                        FragSelectLang frgLang = new FragSelectLang();
                        G.act.pushFrag(frgLang );
                    }
                });
            }
            lnMajor.addView(v);
        }
        return p;
    }

    public void onClick( View v ) {

    }

    private ResideMenu resideMenu;
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
