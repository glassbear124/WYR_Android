package com.special.wyr;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends FragmentActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private MenuActivity mContext;

    ResideMenuItem itemWYR;
    ResideMenuItem itemHonest;
    ResideMenuItem itemFeed;

    private ResideMenuItem itemCompose;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;

    View vNotification;
    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        G.act = this;
        G.hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.f)
                .setCornerRadius(90);

        G.tf      = Typeface.createFromAsset( getAssets(), "fonts/avenir.ttf");
        G.tfBold  = Typeface.createFromAsset( getAssets(), "fonts/avenir_bold.ttf");
        G.tfSuper = Typeface.createFromAsset( getAssets(), "fonts/super.ttc");
        G.strLang = "English";
        loadSound();

        mContext = this;
        setUpMenu();

        View ln = resideMenu.scrollViewLeftMenu;

        vNotification = ln.findViewById(R.id.vNotification); vNotification.setOnClickListener(this);
        editText = (EditText)ln.findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    return false;

                int keycode = keyEvent.getKeyCode();
                if( keycode !=  KeyEvent.KEYCODE_ENTER)
                    return false;

                EditText edit = (EditText)view;
                String key = edit.getText().toString();
                edit.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                resideMenu.closeMenu();
                UsersFragment frgUser = new UsersFragment();
                frgUser.isSearch = true;
                frgUser.searchKey = key;
                pushFrag( frgUser );

                return false;
            }
        });

        if( savedInstanceState == null )
            changeFragment(new HomeFragment());
    }

    void loadSound() {
        G.mpSkip = MediaPlayer.create( this, R.raw.skip );

        TypedArray ansIds = getResources().obtainTypedArray(R.array.arrAnswerSound);
        for( int i = 0; i < 7; i++ ) {
            G.mpAns[i] = MediaPlayer.create( this, ansIds.getResourceId(i, 0) );
        }
        G.mpProg = MediaPlayer.create( this, R.raw.prog1 );
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(false);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.57f);
        resideMenu.setShadowVisible(false);

        int dir = ResideMenu.DIRECTION_LEFT;

        itemWYR = new ResideMenuItem(this, R.drawable.icon_questions_filled, "Would You Rather?");
        itemHonest = new ResideMenuItem(this, R.drawable.icon_fire, "Hottest and Trending");
        itemFeed =  new ResideMenuItem(this, R.drawable.icon_feed, "My Feed");
        itemCompose     = new ResideMenuItem(this, R.drawable.icon_home,  "Compose a Question");
        itemProfile  = new ResideMenuItem(this, R.drawable.icon_profile,  "Profile");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "Settings");

        itemWYR.setOnClickListener(this);
        itemHonest.setOnClickListener(this);
        itemFeed.setOnClickListener(this);
        itemCompose.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        // create menu items;
        resideMenu.addMenuItem( itemWYR, dir );
        resideMenu.addMenuItem( itemHonest, dir );
        resideMenu.addMenuItem( itemFeed, dir );
        resideMenu.addMenuItem(itemCompose, dir);
        resideMenu.addMenuItem(itemProfile, dir);
        resideMenu.addMenuItem(itemSettings, dir);

        resideMenu.addMenuItem( new ResideMenuItem(this, R.drawable.icon_trophy_filled, "Leaderboards and Achievement"), dir );
        resideMenu.addMenuItem( new ResideMenuItem(this, R.drawable.icon_share_filled, "Share"), dir );
        resideMenu.addMenuItem( new ResideMenuItem(this, R.drawable.icon_good_quality_filled, "Facebook Page"), dir );
        resideMenu.addMenuItem( new ResideMenuItem(this, R.drawable.icon_star_filled, "Write Review on App Store"), dir );

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }


    @Override
    public void onBackPressed() {

        if( resideMenu.isOpened() == true ) {
            resideMenu.closeMenu();
            return;
        }
        else {
            if( frgStacks.size() > 1 ) {
                popFrag();
            }
            else
                super.onBackPressed();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if( view == itemWYR ) {
            HomeFragment hf = new HomeFragment();
            hf.viewStyle = C.SCR_WYR;
            changeFragment(hf);
        }
        else if( view == itemHonest ) {
            HomeFragment hf = new HomeFragment();
            hf.viewStyle = C.SCR_HONEST;
            changeFragment(hf);
        }
        else if( view == itemFeed ) {
            HomeFragment hf = new HomeFragment();
            hf.viewStyle = C.SCR_FEED;
            changeFragment(hf);
        }
        else if (view == itemCompose ){
            G.strAns = ""; G.strQuiz = "";
            HomeFragment hf = new HomeFragment();
            hf.viewStyle = C.SCR_COMPOSE;
            changeFragment(hf);
        }else if (view == itemProfile){
            ProfileFragment pf = new ProfileFragment();
            Bundle b = new Bundle();
            b.putInt("userId", G.member_id);
            pf.setArguments(b);
            pf.isMyprofile = true;
            changeFragment(pf);
        }else if (view == itemSettings){
            changeFragment(new SettingsFragment());
        }
        else if( view == vNotification ) {
            changeFragment( new NotificationFragment() );
        }
        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {}

        @Override
        public void closeMenu() {}
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();

        List<Fragment> list = getSupportFragmentManager().getFragments();
        if( list != null ) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            for( Fragment frg : list ) {
                if (frg != null)
                    ft.remove(frg);
            }
            ft.commitAllowingStateLoss();
        }


        G.stack_cnt = 0;
        frgStacks.clear();
        frgStacks.add("fragment");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    ArrayList<String> frgStacks = new ArrayList<String>();

    void allFrgHire() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for( Fragment frg : list ) {
            if (frg != null)
                ft.hide(frg);
        }
        ft.commitAllowingStateLoss();
    }

    public void pushFrag( final Fragment tarFrg ) {
        allFrgHire();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String str = String.valueOf(G.stack_cnt);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment frg1 = getSupportFragmentManager().findFragmentByTag(str);
                if (frg1 == null) {
                    ft.add( R.id.main_fragment, tarFrg, str)
                      .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                } else {
                    ft.show(frg1);
                }
                frgStacks.add(str);
                G.stack_cnt++;
                ft.commitAllowingStateLoss();
            }
        });
    }

     public void popFrag() {
         final int size = frgStacks.size();
         if( size < 2 )
             return;

         allFrgHire();
         new Handler().post(new Runnable() {
             @Override
             public void run() {

                 FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                 String strCurId = frgStacks.get(size-1);
                 String strPrevId = frgStacks.get(size-2);

                 Fragment frgCur = getSupportFragmentManager().findFragmentByTag(strCurId);
                 Fragment frgPrev = getSupportFragmentManager().findFragmentByTag(strPrevId);

                 String classCur = frgCur.getClass().getSimpleName();
                 String classPrev = frgPrev.getClass().getSimpleName();

                 if( classCur.compareTo(HomeFragment.class.getSimpleName()) == 0 &&
                     classPrev.compareTo(FragAnswer.class.getSimpleName()) == 0 ) {

                     if( ((HomeFragment)frgCur).isUpdateSingleMode == true ) {
                         ((FragAnswer)frgPrev).loadData();
                     }
                 }

                 ft.show(frgPrev);
                 frgStacks.remove(size-1);
                 ft.commitAllowingStateLoss();
             }
         });
    }
}
