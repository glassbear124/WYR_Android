package com.special.wyr;

/**
 * Created by xiaosoft on 5/3/17.
 */

public class C {
    final public static int tintColor = 0xFFE5007E;

    final public static String base = "http://216.172.178.47/~intopi/WYR/";

    final public static int SCR_WYR = 0;
    final public static int SCR_HONEST = SCR_WYR+1;
    final public static int SCR_FEED = SCR_HONEST + 1;
    final public static int SCR_COMPOSE = SCR_FEED + 1;

    final public static int REPORT_SPELL = 0;
    final public static int REPORT_MARK = REPORT_SPELL+1;
    final public static int REPORT_DIRTY = REPORT_MARK+1;
    final public static int REPORT_WRONG = REPORT_DIRTY+1;
    final public static int REPORT_OFFENSIVE = REPORT_WRONG+1;

    final public static int [] rank_id = {
        R.drawable.ic1_babys_room,
        R.drawable.ic1_babys_room,
        R.drawable.ic2_businessman,
        R.drawable.ic3_medal,
        R.drawable.ic4_fireman,
        R.drawable.ic5_ninja_head,
        R.drawable.ic6_armored_helmet,
        R.drawable.ic7_crown,
        R.drawable.ic8_queen_gb,
        R.drawable.ic9_moderator,
        R.drawable.ic10_zeus
    };

    final public static String [] rank_name = {"Baby", "Baby", "Grown Up", "Experienced", "Hero", "Ninja",
        "Knight", "Emperor", "Leader", "WYR King/Queen", "WYR God" };

    final public static String err_msg = "En error has occurred, please try again later";
}