package bem.event;

public class IService {
    public static int RECEIVE_PACKAGE_FAIL = -1;

    public static final int ERROR = 64000;
    public static final int PONG = 253;
    public static final int MULTI_LANGUAGE = 3030;

    public static final int REGISTER = 0;
    public static final int LOGIN = 13;
    public static final int LOGIN_NORMAL = 1;
    public static final int LOGIN_FAIL = 2;
    public static final int LOGOUT = 7;
    public static final int DUPLICATE_LOGIN = 8;
    public static final int LOGIN_WEB = 9;
    public static final int AUTO_LOGIN = 10;
    public static final int SET_LANGUAGE = 11;
    public static final int LOGIN_3RD = 12;
    public static final int CHECK_VERSION = 20;

    public static final int GET_USER_HERO_CARD = 100;
    public static final int GET_USER_BATTLE_DECK = 101;

    public static final int SET_USER_BATTLE_DECK = 102;
    public static final int GET_USER_DECK = 103;
    public static final int GET_USER_DECK_DETAIL = 104;
    public static final int SET_USER_DECK = 105;
    public static final int UPDATE_USER_DECK = 106;
    public static final int DELETE_USER_DECK = 107;
    public static final int GET_PROFILE = 108;
    public static final int GET_LEADER_BOARD = 109;
    public static final int GET_EVENT = 110;
    public static final int GET_USER_EVENT_INFO = 111;
    public static final int GET_RANK = 130;
    public static final int GAME_BATTLE_AUTO_JOIN = 351;
    public static final int GAME_BATTLE_JOIN = 51;
    public static final int GAME_BATTLE_LEAVE = 52;
    public static final int GAME_START = 54;
    public static final int GAME_DEAL_CARDS = 55;
    //public static final int BEM_DEAL_CARDS = 55;
    public static final int GAME_MULLIGAN = 56;
    public static final int GAME_FIRST_GOD_SUMMON = 57;
    public static final int GAME_MOVE_GOD_SUMMON = 58;
    public static final int GAME_STARTUP_CONFIRM = 59;
    public static final int GAME_STARTUP_END = 60;
    public static final int GAME_START_BATTLE = 61;
    public static final int GAME_START_BATTLE_DETAIL = 611;
    public static final int GAME_GET_SHARD = 64;
    public static final int GAME_GET_SHARD_DETAIL = 641;
    public static final int GAME_ADD_SHARD_HERO = 66;
    public static final int GAME_ADD_SHARD_HERO_DETAIL = 661;
    public static final int GAME_MOVE_CARD_IN_BATTLE = 63;
    public static final int GAME_MOVE_CARD_IN_BATTLE_DETAIL = 631;
    public static final int GAME_SUMMON_CARD_IN_BATTLE = 62;
    public static final int GAME_SUMMON_CARD_IN_BATTLE_DETAIL = 621;
    public static final int GAME_CONFIRM_STARTBATTLE = 65;
    public static final int GAME_CONFIRM_STARTBATTLE_DETAIL = 651;
    public static final int GAME_CHOOSE_WAY_REQUEST = 67;

    public static final int GAME_SIMULATE_BATTLE = 68;
    public static final int GAME_PREPARE_SIMULATE_BATTLE = 81;
    public static final int GAME_SIMULATE_CONFIRM = 69;
    public static final int GAME_BATTLE_ATTACK = 70;
    public static final int GAME_BATTLE_DEAL_DAMAGE = 71;
    public static final int GAME_BATTLE_HERO_DEAD = 72;
    public static final int GAME_BATTLE_HERO_TIRED = 75;
    public static final int GAME_BATTLE_HERO_READY = 79;
    public static final int GAME_BATTLE_ENDROUND = 73;
    public static final int GAME_BATTLE_ENDGAME = 74;

    public static final int GAME_SIMULATE_SKILLS_ON_BATTLE= 76;
    public static final int GAME_SIMULATE_SKILLS = 77;
    public static final int GAME_SKILL_EFFECT = 78;
    public static final int GAME_DELETE_CARDS = 83;

    public static final int GAME_ACTIVE_SKILL = 80;
    public static final int GAME_STATUS_SKILL = 801;
    public static final int GAME_UPDATE_HERO_MATRIC = 82;
    public static final int GET_BALANCE = 112;
    public static final int GET_USER_PACKS = 114;
    public static final int GET_ITEMS = 115;
    public static final int GET_ITEM_DETAIL = 116;
    public static final int BUY_ITEM = 117;
    public static final int OPEN_CHEST = 118;
    public static final int GAME_RESUME = 221;

    public static final int GAME_START_BID = 613;
    public static final int GAME_BID_RESULT = 615;
    public static final int GAME_SUB_BID_RESULT = 6151;
    public static final int GAME_CONFIRM_BID = 616;
    public static final int GAME_BID_MOVE_TO_PREPARE = 617;

}
