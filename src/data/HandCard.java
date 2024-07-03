package data;

import data.EnumTypes.CardOwner;
import data.EnumTypes.ManaState;

public class HandCard extends Card {
	public boolean isFleeting = false;
	
	 public void SetHandCardData(long battleID, long id,long frame, CardOwner owner, long mana, long atk, long hp)
	    {
	        OnUpdateManaText(mana);
			atkValue = atk;
			hpValue = hp;
	        SetCardData(battleID, id, frame, owner);
	    }
	    
	 @Override
	    public void SetCardData(long battleID, long id,long frame, CardOwner owner)
	    {
	        super.SetCardData(battleID, id,frame, owner);

	          if (tmpMana == -1)
	            OnUpdateManaText(heroInfo.mana);

//	        if (heroInfo.type == DBHero.TYPE_TROOPER_NORMAL)
//	            SetMinionData(cardTexture, shardTexture, info.name, txt);
//	        else if (heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//	            SetSpellData(cardTexture, shardTexture, info.name, txt);
	    }

	    private void SetSpellData(
//	    		Texture cardTexture, Texture shardTexture, 
	    		String cardName, String description)
	    {
	       
	    }

	    private void SetMinionData(
//	    		Texture cardTexture, Texture shardTexture, 
	    		String cardName, String description)
	    {
	        //if (outline != null)
	        //    outline.material = minionOutlineGlow;
//	        for(int j=0;j<minionRarity.Length;j++)
//	        {
//	           minionRarity[j].gameObject.SetActive((j + 1) == heroInfo.rarity);
//	        }
//	        foreach (GameObject go in minionRarity)
//	            go.SetActive(false);
//	        minionRarity[heroInfo.rarity - 1].SetActive(true);
//	        Texture cardFrame = CardData.Instance.GetCardFrameTexture("Mortal_" + frameC+"_"+heroInfo.rarity);
//	        Debug.Log("Mortal_" + frameC + "_" + heroInfo.rarity);
//	        if (heroInfo.rarity>3)
//	        {
//	            minionFrame[1].SetActive(true);
//	            minionFrame[1].GetComponent<MeshRenderer>().material.SetTexture("_BaseMap", cardFrame);
//	            minionFrame[0].SetActive(false);
//	        }    
//	        else
//	        {
//	            minionFrame[1].SetActive(false);
//	            minionFrame[0].SetActive(true);
//	            minionFrame[0].GetComponent<MeshRenderer>().material.SetTexture("_BaseMap", cardFrame);
//	        }    
//	        Texture cardColor = CardData.Instance.GetCardColorTexture("Minion_" + heroInfo.color);
//	        minionPrint.material.SetTexture("_Print", cardTexture);
//	        minionPrint.material.SetTexture("_Color", cardColor);
//	        minionDamageText.text = heroInfo.atk.ToString();
//	        minionHealthText.text = heroInfo.hp.ToString();
//	        minionRegionText.text = heroInfo.cardRegionDict[heroInfo.speciesId];
//	        minionNameText.text = cardName;
//	        minionDescription.text = description;
//	        for (int i = 0; i < lstMinionShards.Count; i++)
//	        {
//	            if (i < heroInfo.shardRequired)
//	            {
//	                lstMinionShards[i].gameObject.SetActive(true);
//	                lstMinionShards[i].material.SetTexture("_Print", shardTexture);
//	            }
//	            else
//	            {
//	                lstMinionShards[i].gameObject.SetActive(false);
//	            }
//	        }
	    }

	    public void OnUpdateManaText(long mana)
	    {
//	        manaText.text = mana.ToString();
	        tmpMana = mana;
	    }

	    private void OnUpdateMana(int index, long mana, ManaState state, long usedMana)
	    {
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (index == 0)
//	        {
//	            if (GameBattleScene.instance.IsYourTurn)
//	            {
//	                if (GameBattleScene.instance.CheckCardCanUseCondition(this))
//	                {
//	                    HighlighCard();
//	                    canSelect = false;
//	                }    
//	                else
//	                    UnHighlighCard();
//	            }
//	        }
	    }
	    private void OnUpdateShard(int index, long shard)
	    {
	        if (cardOwner == CardOwner.Enemy)
	            return;
//	        if (index==0)
//	        {
//	            if (GameBattleScene.instance.IsYourTurn)
//	            {
//	                if (GameBattleScene.instance.CheckCardCanUseCondition(this))
//	                {
//	                    HighlighCard();
//	                    canSelect=false;
//	                }    
//	                else
//	                    UnHighlighCard();
//	            }
//	        }
	    }

	    @Override
	    public void OnGameConfirmStartBattle()
	    {
	        super.OnGameConfirmStartBattle();
	        MoveFail();
	    }
	    public void GameBattleSimulation(boolean isPlayer, String username, long roundCount)
	    {
	        if (cardOwner == CardOwner.Enemy)
	            return;
	        
//	        if (isPlayer)
//	        {
//	            if (GameBattleScene.instance.CheckCardCanUseCondition(this))
//	            {
//	                HighlighCard();
//	                canSelect=false;
//	            }    
//	            else
//	                UnHighlighCard();
//	        } 
//	        else
//	        {
//	            UnHighlighCard();
//	        }
	    }

	public void OnSwapManaAtk(long mana, long atk)
	{
		this.tmpMana = atk;
		this.atkValue = mana;
	}


		  
}
