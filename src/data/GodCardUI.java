package data;

import java.util.ArrayList;
import java.util.List;
import data.EnumTypes.CardOwner;

public class GodCardUI {
    public DBHero hero;

    private CardSlot currentSelectedCardSlot;
    public long battleId;
    public long frameC;

    CardOwner cardOwner;

    //methods
    public long CurrentCardID;


    public long CurrentCardBattleID;



    public void InitData(long battleID, long id, long frame, CardOwner owner)
    {
        hero = Database.GetHero(id);
        CurrentCardID = id;
        battleId = battleID;
//        Debug.Log("frame check" + frame);
        frameC = frame;
        CurrentCardBattleID = battleID;
//        gameObject.name = godName + battleId;
        cardOwner = owner;
//        m_Count.text = hero.mana.ToString();
//        godSprite = CardData.Instance.GetGodIconSprite(hero.heroNumber.ToString());
//        if (godSprite != null)
//        {
//            //highlightSprite = CardData.Instance.GetGodIconSprite(id + "_Glow");
//            m_GodIcon.sprite = godSprite;
//            //m_GodIcon.material.SetTexture("_BaseMap",godTexture) ;
//            //m_GodIcon.SetNativeSize();
//            //m_GodIcon.transform.localScale = new Vector3(0.4f, 0.4f, 0.4f);
//            //m_GodColor.sprite = CardData.Instance.GetGodIconSprite("God_" + Database.GetHero(id).color);
//        }
//        initPosisiotn = transform.position;
//        if (GameData.main.lstGodDead.Count > 0)
//        {
//            foreach (long battle in GameData.main.lstGodDead)
//            {
//                if (battle == battleId)
//                {
//                    ChangeState(0);
//                    transform.SetAsFirstSibling();
//                }
//            }
//        }
    }
}
