package data;

import java.util.ArrayList;
import java.util.Hashtable;

public class DBHero
{
    public static long TYPE_GOD = 0;
    public static long TYPE_TROOPER_NORMAL = 1;
    public static long TYPE_TROOPER_MAGIC = 2;

    public static long TYPE_BUFF_GOD = 4;
    public static long COLOR_WHITE = 0;
    public static long COLOR_GREEN = 1;
    public static long COLOR_RED = 2;
    public static long COLOR_YELLOW = 3;
    public static long COLOR_PURPLE = 4;

    private static String REGION_NONE = "";
    private static String REGION_AQUATIC = "AQUATIC";
    private static String REGION_BEAST = "BEAST";
    private static String REGION_NATURE = "NATURE";
    private static String REGION_GIANT = "GIANT";
    private static String REGION_ZODIAC = "ZODIAC";

    public static long KEYWORD_CLEAVE = 1;
    public static long KEYWORD_PIERCE = 2;
    public static long KEYWORD_OVERRUN = 3;
    public static long KEYWORD_BREAKER = 4;
    public static long KEYWORD_GODSLAYER = 5;
    public static long KEYWORD_COMBO = 6;
    public static long KEYWORD_DEFENDER = 7;

    public Hashtable<Long, String> cardRegionDict = new Hashtable<Long, String>();
//    {
//        {0, REGION_NONE },
//        {1, REGION_AQUATIC },
//        {2, REGION_BEAST },
//        {3, REGION_NATURE },
//        {4, REGION_GIANT },
//        {5, REGION_ZODIAC }
//    );

    /// <summary>Đánh dấu các hero khác nhau</summary>
    public long id;
    /// <summary>Đánh dấu loại hero; 2 hero cùng loại (cùng heroNumber) nhưng kỹ năng (skill) khác nhau thì id khác nhau</summary>
    public long heroNumber;
    /// <summary>Đánh dấu độ hiếm của hero
    public long rarity;
    /// <summary>Đánh dấu gia tộc (clan) của hero: Thần, Lính hay Phép</summary>
    public long type;
    /// <summary>Đánh dấu màu sắc của hero: Trắng, Xanh, Đỏ,...</summary>
    public long color;
    /// <summary>Năng lượng cần để triệu hồi</summary>
    public long mana;
    /// <summary>Số TÍCH của Thần cùng loại để có thể ra trận; chỉ áp dụng với lính thường và lính phép</summary>
    public long shardRequired;
    /// <summary>Số máu trụ bị mất khi hero bị đối phương tiêu diệt</summary>

    public long owner_god_id; //này là id của hero sở hữu nếu đó là bài buff thần
    public long deathCost, collectible, cleave, breaker, overrun, combo, pierce, disable, virtualHero,godSlayer;
    public long speciesId, atk, hp;
    public String name, speciesName, skills;
    public boolean isFragile;
    public int maxShardUnlockSkill = 0;

    public ArrayList<DBHeroSkill> lstHeroSkill = new ArrayList<DBHeroSkill>();

    public String GetTypeName()
    {
        if (type == TYPE_TROOPER_NORMAL)
            return "Lính thường";
        else if (type == TYPE_TROOPER_MAGIC)
            return "Lính phép";
        return "THẦN";
    }
    public String GetColorName()
    {
        String colorName = "";
        switch ((int) color)
        {
            case 0:
                colorName = "WHITE";
                break;
            case 1:
                colorName = "GREEN";
                break;
            case 2:
                colorName = "RED";
                break;
            case 3:
                colorName = "YELLOW";
                break;
            case 4:
                colorName = "PURPLE";
                break;

              
        }
        return colorName;
    }
}





