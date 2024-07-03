package data;

import java.util.ArrayList;

public class DBHeroSkill
{

    public static final long TYPE_PASSIVE_SKILL = 0;
    public static final long TYPE_ACTIVE_SKILL = 1;
    public static final long TYPE_SUMMON_SKILL = 2;

    public static final long OPPOSITE_ENEMY = 1;
    public static final long LANE_ENEMIES = 2;
    public static final long RANDOM_LANE_ENEMY = 3;
    public static final long RANDOM_ENEMY = 4;
    public static final long ALL_ENEMIES = 5;
    public static final long MY_SELF = 6;
    public static final long FRONT_ALLY = 7;
    public static final long BACK_ALLY = 8;
    public static final long NEARBY_ALLY = 9;
    public static final long LANE_ALLIES = 10;
    public static final long ALL_ALLIES = 11;
    public static final long RANDOM_ALLY = 17;
    public static final long SELF_BLANK_NEXT = 18;
    public static final long ANY_OPOSITE_ENEMY_TARGET = 12;
    public static final long LANE_ALLY_TOWER = 13;
    public static final long LANE_ENEMY_TOWER = 14;
    public static final long ALL_UNIT = 15;
    public static final long ALL_ENEMY_UNIT_IN_RANDOM_LANE = 16;
    //Dạng Target Selected mà Player gửi lên
    public static final int MY_HAND_CARD = 1000;
    public static final int ANY_UNIT = 1001;
    public static final int ANY_LANE_ALLIES = 1002;
    public static final int ONE_ALLY_ONE_ENEMY_UNIT = 1003;
    public static final int TWO_ANY_ALLIES = 1004;
    public static final int TWO_ANY_ENEMIES = 1005;
    public static final int ANY_LANE_UNITS = 1006;
    public static final int ANY_ALLY_MORTAL_UNIT = 1007;
    public static final int ANY_ALLY_GOD_UNIT = 1008;
    public static final int ANY_ALLY_BUT_SELF = 1009;
    public static final int ANY_ALLY_UNIT = 1010;  
    public static final int ANY_ALLY_LANE_UNITS = 1011;
    public static final int ANY_ENEMY_MORTAL_UNIT = 1012;
    public static final int ANY_ENEMY_GOD_UNIT = 1013;
    public static final int ANY_ENEMY_UNIT = 1014;
    public static final int ANY_ENEMY_LANE_UNITS = 1015;
    public static final int ANY_TARGET = 1016;
    public static final int ANY_ALLY_TOWER = 1017;
    public static final int ANY_ENEMY_TOWER = 1018;
    public static final int RANDOM_ENEMY_IN_SELECTED_LANE = 1019;
    public static final int CHOSE_SELF_BLANK_NEXT = 1020;
    public static final int CHOSSE_FOUNTAIN = 1021;
    public static final int CHOSSE_LANE = 1022;
    public static final int ANY_ALL_UNIT = 1023;
    public static final int ANY_MOTAL = 1025;
    public static final int ANY_BLANK_ALLY = 1026;
    public static final int ANY_LANE_ENEMY = 1027;
    public static final int ANY_COL_ENEMY = 1028;
    public static final int ANY_ALLY_TARGET = 1029;
    public static final int RANDOM_UNIT_IN_SELECTED_LANE = 1030;
    public static final int TWO_ANY_ALLIES_JUNGLE_LAW = 1032;
    public static final int ANY_TARGET_BUT_SELF = 1033;
    public static final int ANY_MORTAL_BUT_SELF = 1034;
    public static final int ANY_ALLY_TARGET_BUT_SELF = 1035;
    public static final int TWO_ANY_ALLIES_BUT_SELF = 1036;
    public static final int ONE_CARD_IN_HAND_AND_CHOOSE_LANE_UNITS = 1037;
    public static final int ANY_UNIT_BUT_SELF =1039;


    public static final int EFFECT_BUFF_HP = 2;
    public static final int EFFECT_TMP_INCREASE_ATK_AND_HP = 0;
    public static final int EFFECT_INCREASE_ATK_AND_HP = 1;
    public static final int EFFECT_MANA_CREATE_SHARD = 3;
    public static final int EFFECT_MOVE_HERO = 4;
    public static final int EFFECT_DRAW_CARD = 5;
    public static final int EFFECT_DEAL_DAME = 6;
    public static final int EFFECT_INCREASE_SPECIAL_PARAM = 7;
    public static final int EFFECT_TMP_INCREASE_SPECIAL_PARAM = 8;
    public static final int EFFECT_READY = 9;
    public static final int EFFECT_FIGHT = 10;
    public static final int EFFECT_SUMMON_VIRTUAL_HERO = 11;
    public static final int EFFECT_SUMMON_CARD_IN_HAND = 12;
    public static final int EFFECT_USER_MANA_MAX = 13;
    public static final int EFFECT_LEAVE_CARD_IN_HAND = 14;
    public static final int EFFECT_TMP_INCREASE_MANA_MAX = 16;
    public static final int EFFECT_INCREASE_HERO_MANA = 18;
    public static final int EFFECT_DEALDAMGE_TO_HEAL = 19;
    public static final int EFFECT_PLAY_ALL_COLOR_CARD = 20;
    public static final int EFFECT_TMP_INCREASE_HERO_MANA = 21;
    public static final int EFFECT_PENATY = 25;
    public static final int EFFECT_X_TO_Y = 22;
    public static final int EFFECT_X_AND_Y = 27;
    public static final int EFFECT_X_TO_Y_TARGET = 28;

    public static final int SWAP_MANA_ATK = 29;
    public static final int DUPLICATE = 30;
    public static final int KILL = 31;
    public static final int REPLACE = 32;
    public static final int DEAL_DAME_ON_HAND = 33;


    public int id, hero_id, timing, eventSkill, max_turn, sark_god, min_shard, max_shard, skill_type, target , effect_type, enable;
    public String name_skill, note;
    public boolean isUltiType = false;
    public ArrayList<EffectSkill> lstEffectSkill = new ArrayList<EffectSkill>();
    public ArrayList<ListEffectsSkill> lstEffectsSkills = new ArrayList<ListEffectsSkill>();
    public ArrayList<ConditionSkill> lstConditionSkill = new ArrayList<ConditionSkill>();

}