package data;


public class ConditionSkill
{

    public static long NON_CONDITION = 1000;
    public static long FULL_LANE = 0;
    public static long SELF_READY = 1;
    public static long SELF_IN_FRONT_ROW = 2;
    public static long SELF_IN_BACK_ROW = 3;
    public static long SELF_ATK_ENEMY = 4;
    public static long SELF_DEFEAT_ENEMY = 5;
    public static long SELF_MOVE_LANE = 6;
    public static long SELF_GET_SHARK = 7;
    public static long SELF_DEAD = 8;
    public static long SELF_GET_DAME = 9;
    public static long SELF_BEFOR_ATK = 10;

    public static long BLANK_FRONT = 11;
    public static long BLANK_NEXT = 12;
    public static long BLANK_IN_LANE = 13;
    public static long CALL_SPELL = 14;

    public static long NUMBER_CARDS_IN_HAND = 15;
    public static long ENEMY_DEAD = 16;
    public static long ALLY_AQUATIC_DEAD = 17;
    public static long ALLY_DEAD_FRONT = 18;

    public static int CONDITION_TYPE_SMALLER = 30;
    public static int CONDITION_TYPE_BIGGER = 31;

    public int type;
    public String desc;
    public int pos, species, number;

}