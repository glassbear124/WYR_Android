package com.special.wyr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;

public class FragInput extends Fragment implements View.OnClickListener {

    public boolean isBlue;

    private View p;
    private ResideMenu resideMenu;

    LinearLayout mainSrc;
    TextView txtCounter;
    EditText editText;

    InputMethodManager imm;

    int nLimit = 65;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        p = inflater.inflate(R.layout.input_text, container, false);

        setUpViews();

        mainSrc = (LinearLayout)p.findViewById(R.id.mainSrc);
        txtCounter = (TextView)p.findViewById(R.id.txtCounter);
        editText = (EditText)p.findViewById(R.id.editText);

        if( isBlue == false ) {
            mainSrc.setBackgroundColor(getResources().getColor(R.color.red));
            txtCounter.setText( "Type Second Option Here... 65 Characters Remaining" );
            editText.setHint("Type Second Option Here...");
            editText.setText( G.strAns );
        } else {
            editText.setText( G.strQuiz );
        }

        editText.requestFocus();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        int length = editText.getText().length();
        if( length > 0 ) {
            txtCounter.setVisibility(View.VISIBLE);
            counterShow(length);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if( length == 0 ) {
                    txtCounter.setVisibility(View.GONE);
                } else {
                    counterShow(length);
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                int keycode = keyEvent.getKeyCode();
                if( keycode !=  KeyEvent.KEYCODE_ENTER )
                    return false;

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                HomeFragment pf = new HomeFragment();
                pf.viewStyle = C.SCR_COMPOSE;
                String str = editText.getText().toString().trim();

                if( str.length() > 0 ) {
                    if( isBlue == true ) {
                        G.strQuiz = str;
                    }
                    else {
                        G.strAns = str;
                    }
                }
                ft.replace(R.id.main_fragment, pf);
                ft.commit();
                return false;
            }
        });
        return p;
    }

    void counterShow( int length ) {
        txtCounter.setVisibility(View.VISIBLE);
        if( length == nLimit ) {
            txtCounter.setTextColor(Color.RED);
        } else {
            txtCounter.setTextColor(getResources().getColor(R.color.lightGray));
            if( isBlue == true ) {
                txtCounter.setText( String.format("Type First Option Here... %d Characters Remining", nLimit-length));
            } else {
                txtCounter.setText( String.format("Type Second Option Here... %d Characters Remining", nLimit-length));
            }
        }
    }

    public void onClick( View v ) {

    }

    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();

        p.findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

//        // add gesture operation's ignored views
//        FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
//        resideMenu.addIgnoredView(ignored_view);
    }

}
