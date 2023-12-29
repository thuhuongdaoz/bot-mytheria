package data;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Database
{
    // Values
    public static ArrayList<DBHero> lstHero = new ArrayList<DBHero>();
    public static ArrayList<DBRank> lstRank = new ArrayList<DBRank>();

    public static JSONArray prioritySpellArr;
    public static int[][] normalGroup = {{},{9,17,30,25,97,105,125,95,14,123,176,196,197,111,29,15,24,96,175},{18,45,102,127,181},{4,12,99,240},{118,168,191},{34,38,46,47,54,74,113,122,172,174,178,179,237},{57,58},{6,199},{},{78,81,86,164},{171,108,107,177,84,71,146,77},{157,182,104,103,158},{19,3,16,11,88,121,163,193},{32,35,39,93,124,159},{},{3,36,89,126,161,162},{10,100,190,198},{5,7,8,13,26,43,44,48,51,52,53,55,56,76,90,91,94,98,101,110,112,119,147,160,169,170,173,180,194,195,355},{79,72,73,75,85,87,106,120,149,165}};
    // Methods
    public static DBHero GetHero(long id)
    {
    	for (int i = 0; i < lstHero.size(); i ++)
        {
    		DBHero hero = lstHero.get(i);
            if (hero.id == id)            
                return hero;            
        }
        return null;
    }

    public static ArrayList<DBHero> GetHeroByType(long type)
    {
    	ArrayList<DBHero> lst = new ArrayList<DBHero>();
    	for (int i = 0; i < lstHero.size(); i ++)
        {
    		DBHero hero = lstHero.get(i);
            if (hero.type == type)
                lst.add(hero);
        }
        return lst;
    }
    public static ArrayList<DBRank>GetListRank()
    {
        return lstRank;
    }
    public static ArrayList<DBHero> GetHeroByHeroNumber(long heroNumber)
    {
    	ArrayList<DBHero> lst = new ArrayList<DBHero>();
    	for (int i = 0; i < lstHero.size(); i ++)
        {
    		DBHero hero = lstHero.get(i);
            if (hero.heroNumber == heroNumber)
                lst.add(hero);
        }

        return lst;
    }

    public static void ParseAll()
    {

        ParseHero();
        ParseHeroSkill();
//        ParseRank();
        ParsePrioritySpell();
    }
    public static void ParseHero()
    {        
//        string aJSON = BundleHandler.LoadDatabase("db_hero");

    	JSONParser parser = new JSONParser();
    	Object obj;
		try {
			obj = parser.parse(new FileReader("hero.json"));
		
    	JSONArray jArray = (JSONArray)obj;

        for (int i = 0; i < jArray.size(); i++)
        {
            JSONObject o = (JSONObject) jArray.get(i);
            DBHero hero = new DBHero();
            hero.id = (long) o.get ("id");
            hero.heroNumber = (long) o.get ("hero_number");
            hero.type = (long) o.get ("type");
            hero.rarity = (long) o.get ("rarity");
            hero.color = (long) o.get ("color");
            hero.speciesId = (long) o.get ("species_id");
            hero.speciesName = (String) o.get ("species_name");
            hero.mana = (long) o.get ("mana");
            hero.shardRequired = (long) o.get ("shard_required");
            hero.atk = (long) o.get ("atk");
            hero.hp = (long) o.get ("hp");
            hero.deathCost = (long) o.get ("death_cost");
            hero.collectible = (long) o.get ("collectible");
            hero.cleave = (long) o.get ("cleave");
            hero.breaker = (long) o.get ("breaker");
            hero.overrun = (long) o.get ("overrun");
            hero.combo = (long) o.get ("combo");
            hero.pierce = (long) o.get ("pierce");
            hero.godSlayer = (long) o.get ("god_slayer");
            hero.disable = (long) o.get ("disable");
            hero.virtualHero = (long) o.get ("virtual");
            hero.isFragile = ((long) o.get ("fragile")) == 1;
            hero.name = (String) o.get ("name");
            lstHero.add(hero);            
        }
        
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    public static void ParseHeroSkill()
    {

//        string aJSON = BundleHandler.LoadDatabase("heroSkill");

    	JSONParser parser = new JSONParser();
		try {
			JSONArray jArray = (JSONArray)parser.parse(new FileReader("skill.json"));

        //Debug.Log(aJSON);
        for (int i = 0; i < jArray.size(); i++)
        {
        	
        	JSONObject o = (JSONObject) jArray.get(i);
            DBHeroSkill skill = new DBHeroSkill();
            skill.id =  ((Long) o.get ("id")).intValue();
            skill.hero_id = ((Long) o.get ("hero_id")).intValue();
            skill.timing = ((Long) o.get ("timing")).intValue();
            skill.eventSkill = ((Long) o.get ("event")).intValue();
            skill.max_turn = ((Long) o.get ("max_turn")).intValue();
//            skill.sark_god = ((Long) o.get ("sark_god")).intValue();
            skill.min_shard = ((Long) o.get ("min_shard")).intValue();
            skill.max_shard = ((Long) o.get ("max_shard")).intValue();
            skill.skill_type = ((Long) o.get ("skill_type")).intValue();
            skill.enable = ((Long) o.get ("enable")).intValue();
            skill.isUltiType = (Long) o.get ("util_type") == 1;
            skill.name_skill = (String) o.get ("name_skill");
            skill.note = (String) o.get ("note");
            String effects = ((String) o.get ("effects")).replace("\\", "");
            Object NE = parser.parse(effects);
            if (NE != null)
            {
                JSONArray jEffectArray = (JSONArray) NE;
                for (int j = 0; j < jEffectArray.size(); j++)
                {

                    JSONObject obj = (JSONObject) jEffectArray.get(j);
                    ListEffectsSkill lstEffectsSkill = new ListEffectsSkill();
                    lstEffectsSkill.info = ((Long) obj.get("info")).intValue();
                    Object effect = (Object) obj.get("effect");
                    //Debug.Log(obj["effect"]);
                    //JSONNode E= JSON.Parse(effect);
                    if (effect != null)
                    {
                        JSONArray jEffectArrayInfo = (JSONArray) effect;
                        for(int z = 0; z < jEffectArrayInfo.size(); z++)
                        {
                            JSONObject eff = ((JSONObject) jEffectArrayInfo.get (z));
                            EffectSkill effectSkill = new EffectSkill();
                            effectSkill.type = ((Long) eff. get ("type")).intValue();
                            effectSkill.target = ((Long) eff. get ("target")).intValue();
//                            effectSkill.heroId = ((Long) eff. get ("heroId")).intValue();
                            effectSkill.desc = (String) eff.get("desc");
                            JSONArray jSubEffArr = (JSONArray) eff.get ("subEffect");
                            if (jSubEffArr != null){
                                for (int k = 0; k < jSubEffArr.size(); k++){
                                    JSONObject jSubEff = (JSONObject) jSubEffArr.get(k);
                                    SubEffect subEffect = new SubEffect();
                                    subEffect.type = ((Long) jSubEff.get("type")).intValue();
                                    effectSkill.subEffects.add(subEffect);
                                }
                            }
//                            effectSkill.isFragile =((String)  eff. get ("max_turn")).equals("true");
                            lstEffectsSkill.lstEffect.add(effectSkill);
                        }    
                    }  
                    skill.lstEffectsSkills.add(lstEffectsSkill);
                }
            }
            
            
            String conditions = ((String) o.get("conditions")).replace("\\", "");
            
            Object NC = parser.parse(conditions);
            if (NC != null)
            {
                JSONArray jConditionArray = (JSONArray) NC;
                for (int j = 0; j < jConditionArray.size(); j++)
                {
                    ConditionSkill conditionSkill = new ConditionSkill();
                    JSONObject con = ((JSONObject) jConditionArray.get (j));
                    conditionSkill.desc = (String) con.get ("desc");
//                    conditionSkill.number = Integer.parseInt((String) con.get ("number"));
//                    conditionSkill.species = Integer.parseInt((String) con.get ("species"));
//                    conditionSkill.pos = Integer.parseInt((String) con.get ("pos"));
                    conditionSkill.type = ((Long) con.get ("type")).intValue();
                    skill.lstConditionSkill.add(conditionSkill);
                    //Debug.Log("CONDITION ="+ conditionSkill.type+" "+ conditionSkill.species+" "+ conditionSkill.number);
                    //"conditions" : "[{\"type\":1000,\"desc\":\"không có diều kiện\"}]",

                }
            }
            
            for (int j = 0; j < lstHero.size(); j ++)
                if (lstHero.get(j) .id == skill.hero_id && skill.enable==1){
                    lstHero.get(j).lstHeroSkill.add(skill);
                    lstHero.get(j).maxShardUnlockSkill = Math.max(lstHero.get(j).maxShardUnlockSkill, skill.min_shard);
                }

        }


		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void ParseRank()
    {
//        string aJSON = BundleHandler.LoadDatabase("rank");
//        //Debug.Log(aJSON);
//        JSONNode N = JSON.Parse(aJSON);
//        JSONArray jArray = N.AsArray;
//
//        for (int i = 0; i < jArray.Count; i++)
//        {
//            JSONObject o = jArray[i].AsObject;
//            DBRank rank = new DBRank();
//            rank.id = Long.parseLong((String) o.get ("id"));
//            rank.name = Long.parseLong((String) o.get ("name"].Value;
//            rank.elo = Long.parseLong((String) o.get ("elo"));
//            rank.eloReset = Long.parseLong((String) o.get ("elo_reset"));
//            rank.eloParamReset = Long.parseLong((String) o.get ("elo_param_reset"));
//            //hero.skills = Long.parseLong((String) o.get ("skills"].Value;
//            //Debug.Log("---------------Hero =" + hero.name + " " + hero.color);
//
//            lstRank.Add(rank);
//        }
    }
    public static void ParsePrioritySpell(){
        JSONParser parser = new JSONParser();
        String json = "{\"default\":[[132,133,61,134],[40,116],[130],[20,60,67,138],[41],[129,59,166],[21,42,203],[63,64,65,136,131,272],[82,114],[62,115,83,140,200,135,128],[201],[167],[202]]}";

        JSONObject obj;

        try {
            obj = (JSONObject) parser.parse(json);
            prioritySpellArr = (JSONArray) obj.get("default");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
