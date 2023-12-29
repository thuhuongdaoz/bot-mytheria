package data;

import data.EnumTypes.CardOwner;
import data.EnumTypes.SlotState;

public class BoardCard extends Card {
	public class CardStageProps
	{
	    public long id;
	    public int stage;
	}
	public class CardUpdateHeroMatrix
	{
	    public long battleId;
	    public long shard;
	}
	public class CardUltiStage
	{
	    public long heroId;
	    public int stage;
	}
	private int attackCount;
	 public CardSlot slot;
	 public boolean isFragile;
	 public boolean isTired;
	      public long atkValue;
	      public long hpValue;
	      public long hpMaxValue;
	      public long cleaveValue;
	      public long pierceValue;
	      public long breakerValue;
	      public long comboValue;
	      public long overrunValue;
	      public long shieldValue;
	      public long godSlayerValue;
	      private TowerController currentTowerTarget;
	      private BoardCard currentCardTarget;

	    public void MoveToSlot(CardSlot targetSlot)
	    {
	        if (targetSlot == slot)
	            return;

	        if (targetSlot != null)
	        {
	            Selected();
	            this.slot.state= SlotState.Empty;
	            slot = targetSlot;
	            slot.ChangeSlotState(SlotState.Full, this);

	           
	                UpdatePosition();
	                Placed();
	           
	        }
	    }
	    
	    
	    public void Placed()
	    {
//	        SetIdleAnimation();
	    } 

	    @Override
	    public  void UpdatePosition()
	    {
//	        initPosition = slot.transform.position;
//	        initRotation = transform.rotation;
	    }
	    
	    public void OnAddShard(long shard, boolean isPlayer)
	    {
	        if (isPlayer)
	            countShardAddded = shard;
	     
	    }
	    
	    
	    public void SetTired(long isTired)
	    {
	        this.isTired = isTired == 1 ? true : false;
	    }
	    
	    public void OnDamaged(long damage, long health)
	    {
//	        DamagePopup.Create(transform.position, damage, PopupType.Damage);
//	        healthText.text = health.ToString();

	        hpValue = health;
//	        if (shieldValue > 0 && shieldSkeletonEffect != null)
//	            shieldSkeletonEffect.gameObject.SetActive(false);
	    }

	    public void OnDeath()
	    {
//			if (this.heroInfo.type == DBHero.TYPE_GOD)
//			{
//				if (cardOwner == CardOwner.Player)
//					GodCardHandler.instance.GodDead(this.battleID);
//				else
//					GodCardHandler.instance.GodEnemyDead(this.battleID);
//				HandleNetData.QueueNetData(NetData.CARD_CHANGE_STAGE, new CardStageProps() { id = battleID, stage = 0 });
//			}
			if (heroInfo.type != DBHero.TYPE_TROOPER_MAGIC)
			{
//				Transform trans = PoolManager.Pools["Effect"].Spawn(impactMinionDie);
//				trans.position = impactDiePosition.position;
//				trans.rotation = impactDiePosition.rotation;
//				trans.GetComponent<ParticleEffectParentCallback>()
//					.SetOnComplete(OnEndDeadAnim)
//					.SetOnPlay();
//				transform.gameObject.SetActive(false);

				OnEndDeadAnim();
			}
	    }
	    
	    public void UpdateHeroMatrix(long atk, long hp, long hpMax, long cleave, long pierce, long breaker, long combo, long overrun, long shield, long godSlayer, long shard, long fragile, long precide)
	    {
	        atkValue = atk;
	        hpValue = hp;
	        hpMaxValue = hpMax;
	        cleaveValue = cleave;
	        pierceValue = pierce;
	        breakerValue = breaker;
	        comboValue = combo;
	        overrunValue = overrun;
	        shieldValue = shield;
	        countShardAddded = shard;
	        isFragile = fragile == 1;
	        godSlayerValue = godSlayer;
	        if (countShardAddded > 0)
	        {
//	            for (int i = 0; i < lstShardAdded.Count; i++)
//	            {
//	                if (i < countShardAddded)
//	                {
//	                    lstShardAdded[i].gameObject.SetActive(true);
//	                    lstShardAdded[i].sprite = CardData.Instance.GetShardSprite(heroInfo.color); //CardColor.Instance.cardColorInfo[(int)heroInfo.color].shardColorSprite;
//	                }
//	                else
//	                    break;
//	            }
	        }
	        if (shieldValue > 0)
	        {
//	            if (shieldSkeletonEffect == null)
//	            {
//	                Transform trans = PoolManager.Pools["Effect"].Spawn(shieldEffect, shieldSpawnPosition);
//	                trans.localPosition = Vector3.zero;
//	                trans.localRotation = Quaternion.Euler(Vector3.zero);
//	                trans.localScale = new Vector3(0.21f, 0.23f, 0.21f);
//	                shieldSkeletonEffect = trans;
//	            }
//	            if (!shieldSkeletonEffect.gameObject.activeSelf)
//	                shieldSkeletonEffect.gameObject.SetActive(true);
	        }
	        else
	        {
//	            if (shieldSkeletonEffect != null && shieldSkeletonEffect.gameObject.activeSelf)
//	                shieldSkeletonEffect.gameObject.SetActive(false);
	        }

//	        if (healthText != null)
//	            healthText.text = hpValue.ToString();
//	        if (damageText != null)
//	            damageText.text = atkValue.ToString();
//	        Debug.Log("count shard add" + countShardAddded);
//	        HandleNetData.QueueNetData(NetData.CARD_UPDATE_MATRIX, new CardUpdateHeroMatrix() { battleId = battleID,shard =countShardAddded});
	    }
	    
	    public void OnAttackTower(TowerController target /*, out float waitTime*/)
	    {
	        if (attackCount > 0 && comboValue > 0)
	        {
//	            Transform trans = PoolManager.Pools["Effect"].Spawn(comboXEffect.transform);
//	            trans.parent = transform;
//	            trans.localPosition = new Vector3(0, 0.21f, 0);
//	            trans.localRotation = Quaternion.Euler(-90f, 0, 0);
//	            SkeletonAnimation comboEffect = trans.GetComponent<SkeletonAnimation>();
//	            waitTime = comboEffect.skeletonDataAsset.GetSkeletonData(true).FindAnimation("start").Duration;
//	            SoundHandler.main.PlaySFX("Combo", "soundvfx");
//	            comboEffect.AnimationState.SetAnimation(0, "start", false).Complete += delegate
//	            {
	                OnAttackTowerTarget(target);
//	                PoolManager.Pools["Effect"].Despawn(trans);
//	            };
	        }
	        else
	        {
	            OnAttackTowerTarget(target);
//	            waitTime = 0;
	        }
	    }
	    
	    public void OnAttackCard(BoardCard target /*, out float waitTime*/)
	    { 
	    	//waitTime = 0;
	        attackCount += 1;
	        //if (attackCount > 0 && comboValue > 0)
	        //{
	        //    Transform trans = PoolManager.Pools["Effect"].Spawn(comboXEffect.transform);
	        //    trans.parent = transform;
	        //    trans.localPosition = new Vector3(0, 0.21f, 0);
	        //    trans.localRotation = Quaternion.Euler(-90f, 0, 0);
	        //    SkeletonAnimation comboEffect = trans.GetComponent<SkeletonAnimation>();
	        //    waitTime = comboEffect.skeletonDataAsset.GetSkeletonData(true).FindAnimation("start").Duration;
	        //    SoundHandler.main.PlaySFX("Combo", "soundvfx");
	        //    comboEffect.AnimationState.SetAnimation(0, "start", false).Complete += delegate
	        //    {
	        //        PoolManager.Pools["Effect"].Despawn(trans);
	        //        OnAttackCardTarget(target);
	        //    };
	        //}
	        //else
	        //{
	        //    waitTime = 0;
	        //    attackCount += 1;
	        //    OnAttackCardTarget(target);
	        //}
	        OnAttackCardTarget(target);
	    }
	    
	    private void OnAttackCardTarget(BoardCard target)
	    {
	        currentCardTarget = target;
//	        if (!cardAnimator.GetCurrentAnimatorStateInfo(0).IsName("Idle"))
//	            return;
	        
//	        if(comboValue>0&& attackCount==1)
//	        {
//	            foreach (GameObject go in comboKeyEffect)
//	                go.SetActive(true);
//	        }
//	        if (slot.xPos == 0 && slot.xPos == currentCardTarget.slot.xPos)
//	            cardAnimator.SetTrigger("_Attack0");
//	        else if (slot.xPos == 1 && slot.xPos == currentCardTarget.slot.xPos)
//	            cardAnimator.SetTrigger("_Attack2");
//	        else
//	            cardAnimator.SetTrigger("_Attack1");
	    }
	    
	    private void OnAttackTowerTarget(TowerController target)
	    {
	        currentTowerTarget = target;
//	        if (!cardAnimator.GetCurrentAnimatorStateInfo(0).IsName("Idle"))
//	            return; 
//	        if (slot.xPos == 0)
//	            cardAnimator.SetTrigger("_Attack2");
//	        else
//	            cardAnimator.SetTrigger("_Attack3");
	    }
	    
	/*

	     private GameObject[] boardMesh; // skinned mesh renderer (print + frame)
	      private MeshRenderer spellBoardMesh;
	      private SkinnedMeshRenderer ultiMesh;
	      private SkinnedMeshRenderer[] skillAMesh;
	      private GameObject skillA;

	      private TextMeshPro healthText;
	      private TextMeshPro damageText;
	      private TextMeshPro breakerText;
	      private TextMeshPro godSlayerText;
	      private Animator cardAnimator;
	      private List<Image> lstShardAdded;

	      private GameObject outline;

	      private Transform shieldEffect;
	    private Transform shieldSkeletonEffect;
	      private Transform comboXEffect;
	      private Transform cleaveEffect;
	      private Transform overrunEffect;
	      private Transform breakerEffect;
	      private Transform gloryEffect;
	      private Transform healingEffect;
	      private Transform buffEffect;
	      private Transform shieldSpawnPosition;
	      private Transform addShardEffect;
	      private Transform impactFireEffect;
	      private Transform flyingObject;
	      private GameObject[] breakerKeyEffect;
	      private Transform impactBreakerEffect;
	      private GameObject breakerNumber;
	      private GameObject[] godSlayerKeyEffect;
	      private Transform impactgodSlayerEffect;
	      private GameObject godSlayerNumber;
	      private Transform godSlayerImpactPos;
	      private GameObject[] comboKeyEffect;
	      private Transform impactComboEffect;
	      private Transform comboImpactPos;
	      private GameObject[] cleaveKeyEffect;
	      private Transform impactCleaveEffect;
	      private GameObject cleaveIcon;
	      private Transform impactCleavePos;
	      private GameObject[] pierceEffect;
	      private GameObject pierceIcon;
	      private Transform impactPierceEffect;
	      private Transform impactPierceImpactPos;



	    private int attackCount;
	    private CardSkillHandler skillHandle;
	    private BoardCard currentCardTarget;
	    private TowerController currentTowerTarget;
	    private long lastRow = -1, lastCol = -1, currentRow = -1, currentCol = -1;
	    private List<SkillEffect> lstSkillEffect = new List<SkillEffect>();

	   
	    //public event ICallback.CallFunc onDissolve;
	    public event ICallback.CallFunc2<BoardCard> onAddToListSkill;
	    //public event ICallback.CallFunc2<BoardCard> onRemoveFromListSkill;
	    public event ICallback.CallFunc2<BoardCard> onEndSkillActive;
	    #endregion

	    #region Debug Fields
	    [Header("Debug")]
	    public bool isDebug;
	      public long atkValue;
	      public long hpValue;
	      public long hpMaxValue;
	      public long cleaveValue;
	      public long pierceValue;
	      public long breakerValue;
	      public long comboValue;
	      public long overrunValue;
	      public long shieldValue;
	      public long godSlayerValue;
	    #endregion

	    #region Unity Methods
	    
	    */
	    void Start()
	    {
//	        skillHandle = GetComponent<CardSkillHandler>();
//	        GameBattleScene.instance.onGameBattleChangeTurn += OnEndRound;
//	        GameBattleScene.instance.onResetAttackCount += SetAttackCount;
//	        GameBattleScene.instance.onFinishChooseOneTarget += OnEndSkillActive;
//	        GameBattleScene.instance.onEndSkillActive += OnEndSkillActive;
//	        GameBattleScene.instance.onGameConfirmStartBattle += OnGameConfirmStartBattle;
	    }

	    private void FixedUpdate()
	    {
//	        OnDragging();
	    }

	    void OnDisable()
	    {
//	        lastRow = lastCol = currentRow = currentCol = -1;
	    }

	    private void OnEnable()
	    {
//	        lstShardAdded.ForEach(x => x.gameObject.SetActive(false));
	    }

	    public void SetBoardCardData(long battleID, long heroID,long frame, CardOwner owner, CardSlot slot)
	    {
	        SetCardData(battleID, heroID, frame, owner);

	        this.slot = slot;
	    }


	    @Override
	    public void  SetCardData(long battleID, long heroID, long frame, CardOwner owner)
	    {
	        super.SetCardData(battleID, heroID, frame, owner);
			atkValue = heroInfo.atk;
			hpValue = heroInfo.hp;
			hpMaxValue = heroInfo.hp;
//	        Debug.Log("ahdgalkfha" + frame);
//	        Texture cardTexture = CardData.Instance.GetOnBoardTexture(heroInfo.heroNumber);
//	        if (heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//	            SetSpellData(cardTexture);
//	        else if (heroInfo.type == DBHero.TYPE_TROOPER_NORMAL)
//	            SetMinionData(cardTexture);
//	        else
//	            SetGodData(cardTexture);
//	        breakerValue = heroInfo.breaker;
//	        SetTired(0);
	    }

//	    private void SetSpellData(Texture cardTexture)
//	    {
//	        spellBoardMesh.material.SetTexture("_print", cardTexture);
//	    }

//	    private void SetMinionData(Texture cardTexture)
//	    {
//	        Texture cardFrame = CardData.Instance.GetCardFrameTexture("MortalB_" + frameC + "_" + heroInfo.rarity);
//	        if (heroInfo.rarity > 3)
//	        {
//	            boardMesh[1].SetActive(true);
//	            boardMesh[1].GetComponent<SkinnedMeshRenderer>().materials[0].SetTexture("_print_img", cardTexture);
//	            boardMesh[1].GetComponent<SkinnedMeshRenderer>().materials[1].SetTexture("_print_img", cardFrame);
//	            boardMesh[0].SetActive(false);
//	        }
//	        else
//	        { // thu tu material bi dao nguoc thu tu giua low va high frame
//	            boardMesh[1].SetActive(false);
//	            boardMesh[0].SetActive(true);
//	            boardMesh[0].GetComponent<SkinnedMeshRenderer>().materials[1].SetTexture("_print_img", cardTexture);
//	            boardMesh[0].GetComponent<SkinnedMeshRenderer>().materials[0].SetTexture("_print_img", cardFrame);
//	        }
//
//	        if (healthText != null)
//	            healthText.text = heroInfo.hp.ToString();
//	        if(damageText != null)
//	            damageText.text = heroInfo.atk.ToString();
//	        if(breakerText!= null)
//	            breakerText.text = heroInfo.breaker.ToString();
//	        if (godSlayerText != null)
//	            godSlayerText.text = heroInfo.godSlayer.ToString();
//	    }
//
//	    private void SetGodData(Texture cardTexture)
//	    {
//	        Debug.Log(heroInfo.rarity+"______");
//	        Texture cardFrame = CardData.Instance.GetCardFrameTexture("GodB_" +frameC+"_"+heroInfo.rarity+"_"+ heroInfo.color);
//	        foreach (GameObject go in boardMesh)
//	            go.SetActive(false);
//	        GameObject f= boardMesh[heroInfo.rarity - 1];
//	        f.SetActive(true);
//	        if (heroInfo.rarity > 3)
//	        {
//	            f.GetComponent<SkinnedMeshRenderer>().materials[0].SetTexture("_print_img", cardTexture);
//	            f.GetComponent<SkinnedMeshRenderer>().materials[1].SetTexture("_print_img", cardFrame);
//
//	        }
//	        else
//	        {
//	            f.GetComponent<SkinnedMeshRenderer>().materials[1].SetTexture("_print_img", cardTexture);
//	            f.GetComponent<SkinnedMeshRenderer>().materials[0].SetTexture("_print_img", cardFrame);
//	        }
//
//	        //Texture ultiTexture = null;
//	        //Texture leftTexture = null;
//	        //Texture rightTexture = null;
//	        Texture skillATexture= null;
//
//	        foreach (DBHeroSkill sk in heroInfo.lstHeroSkill)
//	        {
//	            if (sk.isUltiType)
//	            {
//	                //ultiTexture = CardData.Instance.GetUltiTexture(sk.id.ToString());
//	            }
//	            else
//	            {
//	                if (CardData.Instance.GetCardSkillDataInfo(heroInfo.id).skillIds.Contains(sk.id))
//	                {
//	                    //if (leftTexture == null)
//	                    //{
//	                    //    leftTexture = CardData.Instance.GetUltiTexture(sk.id.ToString());
//	                    //    if (leftTexture != null && boardMesh != null)
//	                    //        boardMesh.materials[2].SetTexture("_print_img", leftTexture);
//	                    //}
//	                    //else
//	                    //{
//	                    //    rightTexture = CardData.Instance.GetUltiTexture(sk.id.ToString());
//	                    //    if (rightTexture != null && boardMesh != null)
//	                    //        boardMesh.materials[1].SetTexture("_print_img", rightTexture);
//	                    //}
//	                    if (sk.skill_type == 1)
//	                    {
//	                        if (skillATexture == null)
//	                        {
//
//
//	                            skillATexture = CardData.Instance.GetUltiTexture(sk.id.ToString());
//	                            if (skillAMesh != null && skillATexture != null)
//	                            {
//	                                if (skillAMesh.Count() == 4)
//	                                {
//	                                    skillA.SetActive(true);
//	                                    foreach (SkinnedMeshRenderer mesh in skillAMesh)
//	                                        mesh.materials[1].SetTexture("_BaseMap", skillATexture);
//	                                    if (skillA.gameObject.GetComponent<ActiveSkillCard>() != null)
//	                                    {
//	                                        skillA.gameObject.GetComponent<ActiveSkillCard>().InitData(this, sk);
//	                                    }
//
//	                                    //ultiMesh.materials[1].SetTexture("_print_img", skillATexture);
//	                                }
//
//	                            }
//	                        }
//	                    }
//
//	                }
//	            }
//	        }
//	        //if (ultiTexture != null)
//	        //{
//	        //    if (ultiMesh != null)
//	        //    {
//	        //        ultiMesh.gameObject.SetActive(true);
//	        //        ultiMesh.materials[1].SetTexture("_print_img", ultiTexture);
//	        //    }
//	        //}
//	        //else
//	        //{
//	        //    if (ultiMesh != null)
//	        //        ultiMesh.gameObject.SetActive(false);
//	        //}
//
//	       
//	        if (healthText != null)
//	            healthText.text = heroInfo.hp.ToString();
//	        if(damageText != null)
//	            damageText.text = heroInfo.atk.ToString();
//	        if (breakerText != null)
//	            breakerText.text = heroInfo.breaker.ToString();
//	        if (godSlayerText != null)
//	            godSlayerText.text = heroInfo.godSlayer.ToString();
//	    }

	    private void SetAttackCount()
	    {
//	        attackCount = 0;
	    }
	    
	    @Override
	    public void OnGameConfirmStartBattle()
	    {
//	        base.OnGameConfirmStartBattle();
	    }
	    
	    @Override
	    public void OnEndRound(long index)
	    {
//	        if (index == -1)
//	        {
//	            if (shieldValue > 0 && shieldSkeletonEffect != null)
//	                shieldSkeletonEffect.gameObject.SetActive(false);
//
//	            if (gameObject.activeSelf && isFragile)
//	            {
//	                OnDeath();
//	                SoundHandler.main.PlaySFX("BrokenCard", "sounds");
//	                GameBattleScene.instance.lstCardInBattle.Remove(this);
//	                
//	            }
//	        }
	    }

	    @Override
	    public void OnEndSkillActive()
	    {
//	        UnHighlightUnit();
//	        onEndSkillActive?.Invoke(this);
	    }

	    @Override
	    public void HighlightUnit()
	    {
//	        if (outline != null)
//	            outline.SetActive(true);
//	        canSelect = true;
	    }

	    @Override
	    public void UnHighlightUnit()
	    {
//	        if (outline != null)
//	            outline.SetActive(false);
//	        canSelect = false;
	    }

	    public void Selected()
	    {
//	        if (heroInfo.type != DBHero.TYPE_TROOPER_MAGIC)
//	            cardAnimator.SetBool("_IsSelected", true);
	    }


	    public void SetSummonAnimation()
	    {
//	        if (heroInfo.type != DBHero.TYPE_TROOPER_MAGIC)
//	            cardAnimator.SetTrigger("_IsSpawn");
//	        if (heroInfo.type == DBHero.TYPE_GOD)
//	        {
//	            HandleNetData.QueueNetData(NetData.CARD_CHANGE_STAGE, new CardStageProps() { id = battleID, stage = 1 });
//	            HandleNetData.QueueNetData(NetData.Card_UPDATE_ULTI_STAGE, new CardUltiStage() { heroId = heroID, stage = 0 });
//	        }
	    }
	    public void SetIdleAnimation()
	    {
//	        if (heroInfo.type != DBHero.TYPE_TROOPER_MAGIC)
//	            cardAnimator.SetBool("_IsSelected", false);
	    }

	    public void OnAttackCard(BoardCard target, float waitTime)
	    { 
//	    	waitTime = 0;
//	        attackCount += 1;
//	        OnAttackCardTarget(target);
	    }

//	    public void OnAttackTower(TowerController target, out float waitTime)
//	    {
//	        if (attackCount > 0 && comboValue > 0)
//	        {
//	            Transform trans = PoolManager.Pools["Effect"].Spawn(comboXEffect.transform);
//	            trans.parent = transform;
//	            trans.localPosition = new Vector3(0, 0.21f, 0);
//	            trans.localRotation = Quaternion.Euler(-90f, 0, 0);
//	            SkeletonAnimation comboEffect = trans.GetComponent<SkeletonAnimation>();
//	            waitTime = comboEffect.skeletonDataAsset.GetSkeletonData(true).FindAnimation("start").Duration;
//	            SoundHandler.main.PlaySFX("Combo", "soundvfx");
//	            comboEffect.AnimationState.SetAnimation(0, "start", false).Complete += delegate
//	            {
//	                OnAttackTowerTarget(target);
//	                PoolManager.Pools["Effect"].Despawn(trans);
//	            };
//	        }
//	        else
//	        {
//	            OnAttackTowerTarget(target);
//	            waitTime = 0;
//	        }
//	    }

//	    private void OnAttackCardTarget(BoardCard target)
//	    {
//	        currentCardTarget = target;
//	        if (!cardAnimator.GetCurrentAnimatorStateInfo(0).IsName("Idle"))
//	            return;
//	        
//	        if(comboValue>0&& attackCount==1)
//	        {
//	            foreach (GameObject go in comboKeyEffect)
//	                go.SetActive(true);
//	        }
//	        if (slot.xPos == 0 && slot.xPos == currentCardTarget.slot.xPos)
//	            cardAnimator.SetTrigger("_Attack0");
//	        else if (slot.xPos == 1 && slot.xPos == currentCardTarget.slot.xPos)
//	            cardAnimator.SetTrigger("_Attack2");
//	        else
//	            cardAnimator.SetTrigger("_Attack1");
//	    }

//	    private void OnAttackTowerTarget(TowerController target)
//	    {
//	        currentTowerTarget = target;
//	        if (!cardAnimator.GetCurrentAnimatorStateInfo(0).IsName("Idle"))
//	            return; 
//	        if (slot.xPos == 0)
//	            cardAnimator.SetTrigger("_Attack2");
//	        else
//	            cardAnimator.SetTrigger("_Attack3");
//	    }

//	    public void OnDamaged(long damage, long health)
//	    {
//	        DamagePopup.Create(transform.position, damage, PopupType.Damage);
//	        healthText.text = health.ToString();
//	        hpValue = health;
//	        if (shieldValue > 0 && shieldSkeletonEffect != null)
//	            shieldSkeletonEffect.gameObject.SetActive(false);
//	    }

//	    public void OnBeingAttacked()
//	    {
//	        if (cardAnimator.GetCurrentAnimatorStateInfo(0).IsName("Idle"))
//	        {
//	            cardAnimator.SetTrigger("_BeingAttacked");
//	        }
//	    }

//	    public void OnAttackTriggerCallback()
//	    {
//	        DisplayAttackEffect();
//	        if (currentCardTarget != null)
//	        {
//	            if(heroInfo.atk>4)
//	            {
//	                CameraShaker.Instance.ShakeOnce(3, 3, .1f, 1.5f);
//	            }    
//	            currentCardTarget.OnBeingAttacked();
//	            currentCardTarget = null;
//	            SoundHandler.main.PlaySFX("CardAttack2", "sounds");
//	        }
//	        if (currentTowerTarget != null)
//	        {
//	            if (heroInfo.type == DBHero.TYPE_GOD|| heroInfo.atk>4)
//	            {
//	                CameraShaker.Instance.ShakeOnce(3,1, .1f, 1.5f);
//	            }
//	            Transform trans = PoolManager.Pools["Effect"].Spawn(impactFireEffect.transform);
//	            trans.position = currentTowerTarget.transform.position + new Vector3(0, 0.2f, 0);
//	            trans.GetComponent<ParticleSystem>().Play();
//	            currentTowerTarget.OnBeingAttacked();
//	            currentTowerTarget = null;
//	            SoundHandler.main.PlaySFX("CardAttack3", "sounds");
//
//	        }
//	    }
//	    public void OnEndAttackCallback()
//	    {
//	        breakerNumber.SetActive(false);
//	        foreach (GameObject go in breakerKeyEffect)
//	            go.SetActive(false);
//	        godSlayerNumber.SetActive(false);
//	        foreach (GameObject go in godSlayerKeyEffect)
//	            go.SetActive(false);
//	        foreach (GameObject go in comboKeyEffect)
//	            go.SetActive(false);
//	        cleaveIcon.SetActive(false);
//	        foreach (GameObject go in cleaveKeyEffect)
//	            go.SetActive(false);
//	        pierceIcon.SetActive(false);
//	        foreach(GameObject go in pierceEffect)
//	            go.SetActive(false);
//	        foreach(GameObject go in comboKeyEffect)
//	            go.SetActive(false);
//	    }   
//	    public void DisplayListEffect(List<SkillEffect> lstEff)
//	    {
//	        // set bang null khi dung xong 
//	        lstSkillEffect = lstEff ;
//	        foreach (SkillEffect effect in lstEff)
//	        {
//	            switch (effect.typeEffect)
//	            {
//	                case DBHero.KEYWORD_CLEAVE:
//	                    {
//	                        cleaveIcon.SetActive(true);
//	                        foreach (GameObject go in cleaveKeyEffect)
//	                            go.SetActive(true);
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_PIERCE:
//	                    {
//	                        Debug.Log("àlsjglskrjtlkr");
//	                        pierceIcon.SetActive(true);
//	                        foreach (GameObject go in pierceEffect)
//	                            go.SetActive(true);
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_OVERRUN:
//	                    {
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_BREAKER:
//	                    {
//	                        breakerNumber.SetActive(true);
//	                        foreach (GameObject go in breakerKeyEffect)
//	                            go.SetActive(true);
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_GODSLAYER:
//	                    {
//	                        godSlayerNumber.SetActive(true);
//	                        foreach (GameObject go in godSlayerKeyEffect)
//	                            go.SetActive(true);
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_COMBO:
//	                    {
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_DEFENDER:
//	                    {
//	                        break;
//	                    }
//	            }   
//	        }
//	    }
//	    private void DisplayListEffectImpact()
//	    {
//	        foreach (SkillEffect effect in lstSkillEffect)
//	        {
//	            switch (effect.typeEffect)
//	            {
//	                case DBHero.KEYWORD_CLEAVE:
//	                    {
//	                        // sqawn la ok 
//	                        if(currentCardTarget!= null)
//	                        {
//	                            Transform trans = PoolManager.Pools["Effect"].Spawn(impactCleaveEffect.transform);
//	                            trans.parent = currentCardTarget.impactCleavePos;
//	                            trans.localRotation = Quaternion.Euler(0, 0, 0);
//	                            trans.localPosition = new Vector3(0, 0f, 0);
//	                            if (slot.yPos == 0 || slot.yPos == 2)
//	                            {
//	                                // attack from left to right (x = -1)
//	                                StartCoroutine(trans.gameObject.GetComponent<CleaveEffectControl>().OnCleaveHit(false));
//	                            }
//	                            else if (slot.yPos == 1 || slot.yPos == 3)
//	                            {
//	                                // attack from right to left (x = 1)
//	                                StartCoroutine(trans.gameObject.GetComponent<CleaveEffectControl>().OnCleaveHit(true));
//	                            }
//	                        }
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_PIERCE:
//	                    {
//	                        //spawn kem target
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_OVERRUN:
//	                    {
//	                        //spawn 1-2 target 
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_BREAKER:
//	                    {
//	                        //spawn la ok , danh thang vao target la tru
//	                        if (currentTowerTarget != null)
//	                        {
//	                                Transform trans = PoolManager.Pools["Effect"].Spawn(impactBreakerEffect);
//	                                trans.parent = currentTowerTarget.transform;
//	                                trans.localPosition = new Vector3(0, 0f, 0);
//	                                trans.localRotation = Quaternion.Euler(0f, 0, 0);
//	                        }
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_GODSLAYER:
//	                    {
//	                        //spawn la ok , danh thang vao target la than
//	                        if(currentCardTarget!= null)
//	                        {
//	                            Transform trans = PoolManager.Pools["Effect"].Spawn(impactgodSlayerEffect.transform);
//	                            trans.parent = currentCardTarget.godSlayerImpactPos;
//	                            trans.localRotation = Quaternion.Euler(0, 0, 0);
//	                            trans.localPosition = new Vector3(0, 0f, 0f);
//	                            trans.GetChild(2).GetChild(0).GetChild(0).GetComponent<TextMeshPro>().text = godSlayerValue.ToString();
//	                        }
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_COMBO:
//	                    {
//	                        break;
//	                    }
//	                case DBHero.KEYWORD_DEFENDER:
//	                    {
//	                        break;
//	                    }
//	            }
//	        }
//	    }
//	    private void DisplayAttackEffect()
//	    {
//	        // attack card
//	        if (currentCardTarget != null)
//	        {
//	            if (cleaveValue > 0)
//	            {
//	                //Transform trans = PoolManager.Pools["Effect"].Spawn(impactCleaveEffect.transform);
//	                //trans.parent = impactCleavePos;
//	                //trans.localRotation = Quaternion.Euler(0, 0, 0);
//	                //trans.localPosition = new Vector3(0, 0f, 0);
//	                //if (slot.yPos == 0 || slot.yPos == 2)
//	                //{
//	                //    // attack from left to right (x = -1)
//	                //    StartCoroutine(trans.gameObject.GetComponent<CleaveEffectControl>().OnCleaveHit(false));
//	                //}
//	                //else if (slot.yPos == 1 || slot.yPos == 3)
//	                //{
//	                //    // attack from right to left (x = 1)
//	                //    StartCoroutine(trans.gameObject.GetComponent<CleaveEffectControl>().OnCleaveHit(true));
//	                //}
//	            }
//	            if (overrunValue > 0)
//	            {
//	                Transform trans = PoolManager.Pools["Effect"].Spawn(overrunEffect.transform);
//	                trans.parent = trans;
//	                trans.localRotation = Quaternion.Euler(-90f, 0, 0);
//	                trans.localPosition = new Vector3(0, 0.21f, 0);
//	                trans.GetComponent<SkeletonAnimation>().state.SetAnimation(0, "start", false).Complete += delegate
//	                {
//	                    PoolManager.Pools["Effect"].Despawn(trans);
//	                };
//	            }
//	            if (godSlayerValue>0)
//	            {
//	                //Transform trans = PoolManager.Pools["Effect"].Spawn(impactgodSlayerEffect.transform);
//	                //trans.parent = godSlayerImpactPos.transform;
//	                //trans.localRotation = Quaternion.Euler(0, 0, 0);
//	                //trans.localPosition = new Vector3(0,0f, 0f);
//	                //trans.GetChild(2).GetChild(0).GetChild(0).GetComponent<TextMeshPro>().text = godSlayerValue.ToString();
//	            }    
//	            if(comboValue>0)
//	            {
//	                //Transform trans = PoolManager.Pools["Effect"].Spawn(impactComboEffect.transform);
//	                //trans.parent = comboImpactPos.transform;
//	                //trans.localRotation = Quaternion.Euler(0, 0, 0);
//	                //trans.localPosition = new Vector3(0, 0f, 0f);
//	            }
//	        }
//	        if (currentTowerTarget != null)
//	        {
//	            if (breakerValue > 0)
//	            {
//	                Transform trans = PoolManager.Pools["Effect"].Spawn(impactBreakerEffect);
//	                trans.parent = currentTowerTarget.transform;
//	                trans.localPosition = new Vector3(0, 0f, 0);
//	                trans.localRotation = Quaternion.Euler(0f, 0, 0);
//	                //OnAttackWithSkill(currentTowerTarget.transform, null, out float waitTime);
//	            }
//	        }
//	    }
//
//	    public void OnAttackWithSkill(Transform target, ICallback.CallFunc callback, out float waitTime)
//	    {
//	        TwoPointEffectHandle twoPoint = new TwoPointEffectHandle();
//	        Transform trans = PoolManager.Pools["Effect"].Spawn(breakerEffect.transform);
//	        trans.parent = transform;
//	        trans.localPosition = new Vector3(0, 0.21f, 0);
//	        trans.localRotation = Quaternion.Euler(-90f, 0, 0);
//	        SkeletonAnimation towerDealDamageEffect = trans.GetComponent<SkeletonAnimation>();
//	        twoPoint = towerDealDamageEffect.GetComponent<TwoPointEffectHandle>();
//	        twoPoint.SetupEffect(twoPoint.transform.position, target.position, () =>
//	        {
//	            Debug.Log("target");
//	            callback?.Invoke();
//	            Debug.Log(target.gameObject.name+"______"+twoPoint.gameObject.name);
//	            towerDealDamageEffect.AnimationState.Event += ((entry, e) =>
//	            {
//	                Debug.Log("target1");
//	                //callback?.Invoke();
//	                Debug.Log("target2");
//	                SoundHandler.main.PlaySFX("Breaker", "soundvfx");
//	            });
//	            towerDealDamageEffect.state.SetAnimation(0, "start", false).Complete += delegate
//	            {
//	                Destroy(trans.gameObject);
//	            };
//	        });
//	        waitTime = towerDealDamageEffect.skeletonDataAsset.GetSkeletonData(true).FindAnimation("start").Duration;
//	        Debug.Log("_______________check ");
//	    }
//
//	    public void OnCastSkill(long skillID,long effectID ,GameObject target, ICallback.CallFunc callback)
//	    {
//	        skillHandle.CastSkill(skillID,effectID,this.transform,callback,target);
//	    }
//
//	    public void OnGlory(ICallback.CallFunc callback)
//	    {
//	        Transform trans = PoolManager.Pools["Effect"].Spawn(gloryEffect.transform);
//	        trans.parent = transform;
//	        trans.localPosition = new Vector3(0, 0, 0.17f);
//
//	        trans.localRotation = Quaternion.Euler(70f, cardOwner == CardOwner.Player ? -180f : 0, 0);
//	        SkeletonAnimation gloryAnimation = trans.GetComponent<SkeletonAnimation>();
//
//	        gloryAnimation.AnimationState.Event += (entry, e) =>
//	        {
//	            if (e.Data.Name == "run")
//	            {
//	                callback?.Invoke();
//	                SoundHandler.main.PlaySFX("Glory", "soundvfx");
//	            }
//	        };
//
//	        gloryAnimation.AnimationState.SetAnimation(0, "start", false).Complete += delegate
//	        {
//	            PoolManager.Pools["Effect"].Despawn(trans);
//	        };
//	    }
//
//	    public void SetTired(long isTired)
//	    {
//	        Debug.Log(heroID + " adhgalfkg----------------" + gameObject.name);
//	        Color color = isTired == 0 ? new Color(1,1,1) : new Color(87f / 255f, 87f / 255f, 87f / 255f);
//	        if (boardMesh != null && heroInfo.type!= DBHero.TYPE_TROOPER_MAGIC)
//	        {
//	            if (heroInfo.type == DBHero.TYPE_GOD)
//	            {
//	                if(heroInfo.rarity>3)
//	                    boardMesh[heroInfo.rarity-1].GetComponent<SkinnedMeshRenderer>().materials[0].SetFloat("_tired", isTired);
//	                else
//	                    boardMesh[heroInfo.rarity - 1].GetComponent<SkinnedMeshRenderer>().materials[1].SetFloat("_tired", isTired);
//
//	            }
//	            else
//	            {
//	                if(heroInfo.rarity>3)
//	                    boardMesh[1].GetComponent<SkinnedMeshRenderer>().materials[0].SetFloat("_tired", isTired);
//	                else
//
//	                    boardMesh[0].GetComponent<SkinnedMeshRenderer>().materials[1].SetFloat("_tired", isTired);
//	            }
//	        }
//	        this.isTired = isTired == 1 ? true : false;
//	        if (this.isTired)
//	        {
//	            SoundHandler.main.PlaySFX("Debuff", "sounds");
//	        }
//	    }
//
//	    public void OnAddShard(long shard, bool isPlayer)
//	    {
//	        if (isPlayer)
//	            countShardAddded = shard;
//	        HandleNetData.QueueNetData(NetData.CARD_UPDATE_MATRIX, new CardUpdateHeroMatrix() { battleId = battleID,shard = countShardAddded});
//	        HandleNetData.QueueNetData(NetData.Card_UPDATE_ULTI_STAGE, new CardUltiStage() { heroId = heroID,stage =0 });
//	        lstShardAdded[(int)shard - 1].gameObject.SetActive(true);
//	        lstShardAdded[(int)shard - 1].sprite = CardData.Instance.GetShardSprite(heroInfo.color);
//
//	        Transform trans = PoolManager.Pools["Effect"].Spawn(addShardEffect.transform);
//	        trans.position = new Vector3(transform.position.x, transform.position.y + 0.15f, transform.position.z);
//	        trans.parent = transform;
//	        trans.GetComponent<ChangeShard>().shard[heroInfo.color-1].SetActive(true);
//
//	        //trans.GetComponent<SkeletonAnimation>().state.SetAnimation(0, "full", false).Complete += delegate
//	        //{
//	        //    PoolManager.Pools["Effect"].Despawn(trans);
//	        //};
//	        StartCoroutine(DespawnShardObject(trans));
//	    }
//	    IEnumerator DespawnShardObject(Transform trans)
//	    {
//	        yield return new WaitForSeconds(1.5f);
//	        PoolManager.Pools["Effect"].Despawn(trans);
//	    }    
//	    public void OnDeath()
//	    {
//	        if (heroInfo.type != DBHero.TYPE_TROOPER_MAGIC)
//	            cardAnimator.SetBool("_IsDead", true);
//	        if (this.heroInfo.type == DBHero.TYPE_GOD)
//	        {
//	            if (cardOwner == CardOwner.Player)
//	            {
//	                GodCardHandler.instance.GodDead(this.battleID);
//	            }
//	            else
//	            {
//	                GodCardHandler.instance.GodEnemyDead(this.battleID);
//	            }
//	            HandleNetData.QueueNetData(NetData.CARD_CHANGE_STAGE, new CardStageProps() { id = battleID,stage = 0 });
//	        }
//	        //onDissolve?.Invoke();
//	    }
//
	    public void OnEndDeadAnim()
	    {
	        if (slot != null)
	        {
	            slot.ChangeSlotState(SlotState.Empty, null);
	            slot = null;
	        }
//	        PoolManager.Pools["Card"].Despawn(transform);
//	        if (heroInfo.type != DBHero.TYPE_TROOPER_MAGIC)
//	            cardAnimator.SetBool("_IsDead", false);
	    }
//
//
//	    public void SummonNewCard(Vector3 destination, ICallback.CallFunc callback)
//	    {
//	        Transform trans = PoolManager.Pools["Effect"].Spawn(flyingObject);
//	        trans.position = transform.position;
//	        trans.DOMoveX(destination.x, 0.5f).SetEase(Ease.InQuad);
//	        trans.DOMoveY(destination.y, 0.5f).SetEase(Ease.OutQuad);
//	        trans.DOMoveZ(destination.z, 0.5f).SetEase(Ease.InQuad).onComplete += delegate
//	        {
//	            callback?.Invoke();
//	            PoolManager.Pools["Effect"].Despawn(trans);
//	        };
//	    }
//
	    public void OnHealing(long hpAmount, long hpvalue, long hpMax)
	    {
//	        Transform effect = PoolManager.Pools["Effect"].Spawn(healingEffect.transform);
//	        effect.position = transform.position;
//	        effect.parent = transform;
//	        outline.SetActive(false);
//	        effect.GetComponent<VisualEffect>().Play();
//
//	        DamagePopup.Create(transform.position, hpAmount, PopupType.Bonus, () =>
//	        {
//	            effect.GetComponent<VisualEffect>().Stop();
//	            PoolManager.Pools["Effect"].Despawn(effect);
//	        });
//	        healthText.text = (hpvalue < hpMax ? hpvalue : hpMax).ToString();
	        this.hpValue = hpvalue;
			this.hpMaxValue = hpMax;
	    }

	    public void OnBuffEffect(long atkAmount, long hpAmount, long atkValue, long hpValue, long hpMax)
	    {
//	        Transform effect = PoolManager.Pools["Effect"].Spawn(buffEffect.transform);
//	        effect.position = new Vector3(transform.position.x, transform.position.y + 0.15f, transform.position.z);
//	        effect.parent = transform;
//
//	        effect.GetComponent<VisualEffect>().Play();
//	        outline.SetActive(false);
//
//	        DamagePopup.Create(healthText.transform.position, hpAmount, PopupType.Bonus);
//	        DamagePopup.Create(damageText.transform.position, atkAmount, PopupType.Bonus, () =>
//	        {
//	            effect.GetComponent<VisualEffect>().Stop();
//	            PoolManager.Pools["Effect"].Despawn(effect);
//	        });
//	        healthText.text = (hpValue < hpMax ? hpValue : hpMax).ToString();
//	        damageText.text = atkValue.ToString();
			this.atkValue = atkValue;
	        this.hpValue = hpValue;
			this.hpMaxValue = hpMax;
	    }
//
//	    public void OnAddSpecialBuff(long skillID, long cleaveAdd, long pierceAdd, long breakerAdd, long comboAdd, long overrunAdd, long shieldAdd, long godSlayerAdd, long cleave, long pierce, long breaker, long combo, long overrun, long shield, long godSlayer)
//	    {
//	        outline.SetActive(false);
//
//	        if (cleaveAdd > 0)
//	        {
//	            DamagePopup.Create(transform.position, cleaveAdd, PopupType.Bonus, () =>
//	            {
//	            });
//	            cleaveValue = cleave;
//	        }
//	        if (pierceAdd > 0)
//	        {
//	            DamagePopup.Create(transform.position, pierceAdd, PopupType.Bonus, () =>
//	            {
//	            });
//	            pierceValue = pierce;
//	        }
//	        if (breakerAdd > 0)
//	        {
//	            DamagePopup.Create(transform.position, breakerAdd, PopupType.Bonus, () =>
//	            {
//	            });
//	            breakerValue = breaker;
//	        }
//	        if (comboAdd > 0)
//	        {
//	            DamagePopup.Create(transform.position, comboAdd, PopupType.Bonus, () =>
//	            {
//	            });
//	            comboValue = combo;
//	        }
//	        if (overrunAdd > 0)
//	        {
//	            DamagePopup.Create(transform.position, overrunAdd, PopupType.Bonus, () =>
//	            {
//	            });
//	            overrunValue = overrun;
//	        }
//	        if (shieldAdd > 0)
//	        {
//	            if (shieldSkeletonEffect == null)
//	            {
//	                Transform trans = PoolManager.Pools["Effect"].Spawn(shieldEffect.transform, shieldSpawnPosition);
//	                trans.localPosition = Vector3.zero;
//	                trans.localRotation = Quaternion.Euler(Vector3.zero);
//	                trans.localScale = new Vector3(0.85f, 0.87f, 0.6f);
//	                shieldSkeletonEffect = trans;
//	               // shieldSkeletonEffect = trans.GetComponent<ParticleSystem>();
//	            }
//	            if (!shieldSkeletonEffect.gameObject.activeSelf)
//	                shieldSkeletonEffect.gameObject.SetActive(true);
//	            //shieldSkeletonEffect.Play();
//	            shieldValue = shield;
//	        }
//	        if (godSlayerAdd > 0)
//	        {
//	            DamagePopup.Create(transform.position, godSlayerAdd, PopupType.Bonus, () =>
//	            {
//	            });
//	            godSlayerValue = godSlayer;
//	        }
//	    }
//
//	    public void UpdateHeroMatrix(long atk, long hp, long hpMax, long cleave, long pierce, long breaker, long combo, long overrun, long shield, long godSlayer, long shard, long fragile = 0, long precide = 0)
//	    {
//	        atkValue = atk;
//	        hpValue = hp;
//	        hpMaxValue = hpMax;
//	        cleaveValue = cleave;
//	        pierceValue = pierce;
//	        breakerValue = breaker;
//	        comboValue = combo;
//	        overrunValue = overrun;
//	        shieldValue = shield;
//	        countShardAddded = shard;
//	        isFragile = fragile == 1;
//	        godSlayerValue = godSlayer;
//	        if (countShardAddded > 0)
//	        {
//	            for (int i = 0; i < lstShardAdded.Count; i++)
//	            {
//	                if (i < countShardAddded)
//	                {
//	                    lstShardAdded[i].gameObject.SetActive(true);
//	                    lstShardAdded[i].sprite = CardData.Instance.GetShardSprite(heroInfo.color); //CardColor.Instance.cardColorInfo[(int)heroInfo.color].shardColorSprite;
//	                }
//	                else
//	                    break;
//	            }
//	        }
//	        if (shieldValue > 0)
//	        {
//	            if (shieldSkeletonEffect == null)
//	            {
//	                Transform trans = PoolManager.Pools["Effect"].Spawn(shieldEffect, shieldSpawnPosition);
//	                trans.localPosition = Vector3.zero;
//	                trans.localRotation = Quaternion.Euler(Vector3.zero);
//	                trans.localScale = new Vector3(0.21f, 0.23f, 0.21f);
//	                //shieldSkeletonEffect = trans.GetComponent<ParticleSystem>();
//	                shieldSkeletonEffect = trans;
//	            }
//	            if (!shieldSkeletonEffect.gameObject.activeSelf)
//	                shieldSkeletonEffect.gameObject.SetActive(true);
//	            //shieldSkeletonEffect.Play();
//	        }
//	        else
//	        {
//	            if (shieldSkeletonEffect != null && shieldSkeletonEffect.gameObject.activeSelf)
//	                shieldSkeletonEffect.gameObject.SetActive(false);
//	        }
//
//	        if (healthText != null)
//	            healthText.text = hpValue.ToString();
//	        if (damageText != null)
//	            damageText.text = atkValue.ToString();
//	        Debug.Log("count shard add" + countShardAddded);
//	        HandleNetData.QueueNetData(NetData.CARD_UPDATE_MATRIX, new CardUpdateHeroMatrix() { battleId = battleID,shard =countShardAddded});
//	    }
//	    #endregion
//
//	    #region Movement
//	    public override void UpdatePosition()
//	    {
//	        initPosition = slot.transform.position;
//	        initRotation = transform.rotation;
//	    }
//	    // Check is single target when active skill
//	    private bool IsSingleTarget(SkillState skState)
//	    {
//	        if (skState == SkillState.TWO_ANY_ENEMIES || skState == SkillState.TWO_ANY_ALLIES || skState == SkillState.TWO_ANY_ALLIES_BUT_SELF || skState == SkillState.TWO_ANY_ALLIES_JUNGLE_LAW)
//	            return false;
//	        return true;
//	    }
//	#if UNITY_STANDALONE
//	    public override void OnMouseDown()
//	    {
//	        base.OnMouseDown();
//	        if (IsPointerOverUIObject())
//	            return;
//	        if (GameBattleScene.instance.skillState != SkillState.None)
//	        {
//	            if (IsSingleTarget(GameBattleScene.instance.skillState))
//	            {
//	                if (GameBattleScene.instance.lstSelectedSkillBoardCard.Count < 1)
//	                {
//	                    Debug.Log("add board card to list skill sigle" + GameBattleScene.instance.lstSelectedSkillBoardCard.Count);
//	                    if (canSelect)
//	                        onAddToListSkill?.Invoke(this);
//	                }
//	                //else
//	                //{
//	                //    onRemoveFromListSkill?.Invoke(this);
//	                //}
//	            }
//	            else
//	            {
//	                if (GameBattleScene.instance.lstSelectedSkillBoardCard.Count < 2)
//	                {
//	                    if (canSelect)
//	                    {
//	                        Debug.Log("add board card to list skill" + GameBattleScene.instance.lstSelectedSkillBoardCard.Count);
//	                        canSelect = false;
//	                        onAddToListSkill?.Invoke(this);
//	                    }
//	                    //else
//	                    //{
//	                    //    onRemoveFromListSkill?.Invoke(this);
//	                    //    canSelect = true;
//	                    //}
//	                }
//	                //else
//	                //{
//	                //   // onRemoveFromListSkill?.Invoke(this);
//	                //    canSelect = true;
//	                //}
//	            }
//	        }
//	        else
//	        {
//	            if (GameBattleScene.instance.IsYourTurn && cardOwner == CardOwner.Player)
//	                isTouch = true;
//
//	            else
//	                isTouch = false;
//	        }
//	    }
//
//	    public override void OnMouseDrag()
//	    {
//	        base.OnMouseDrag();
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (!isTouch)
//	            return;
//	        if (isDragging)
//	            return;
//	        if (isMoving)
//	            return;
//	        if (Time.time - currentClickTime < 0.3f)
//	            return;
//	        if (isSelected)
//	        {
//	            Selected();
//	        }
//	        RaycastHit hit;
//	        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
//	        if (Physics.Raycast(ray, out hit/*, Mathf.Infinity, ~layerMask*/))
//	        {
//	            if(hit.collider != null)
//	            {
//	                if (hit.collider.gameObject != gameObject)
//	                {
//	                    CreateCloneOnDragging(); 
//	                    isDragging = true;
//	                    isSelected = true;
//	                }
//	                if(GameBattleScene.instance.skillState== SkillState.None&& isSelected)
//	                {
//	                    foreach (CardSlot slot in GameBattleScene.instance.ChooseSelfAnyBlank())
//	                    {
//	                        slot.HighLightSlot();
//	                    }
//	                }    
//	                
//	            }
//	        }
//	    }
//
//	    
//
//	    public override void OnMouseUp()
//	    {
//	        base.OnMouseUp();
//	        Placed();
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        RaycastHit hit;
//	        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
//	        
//	        if (Physics.Raycast(ray, out hit, Mathf.Infinity, layerMask))
//	        {
//	            if (hit.collider != null)
//	            {
//	                if (hit.collider.GetComponent<CardSlot>() != null)
//	                {
//	                    if (hit.collider.GetComponent<CardSlot>().type != SlotType.Enemy)
//	                    {
//	                        CardSlot targetSlot = hit.collider.GetComponent<CardSlot>();
//	                        if (!isTired&& targetSlot!= slot)
//	                        {
//	                            // move god in prepare phase
//	                            if (!GameBattleScene.instance.isGameStarted)
//	                                GameBattleScene.instance.MoveGodInReadyPhase(this, slot.xPos, slot.yPos, targetSlot.xPos, targetSlot.yPos);
//	                            else
//	                                GameBattleScene.instance.MoveCardInBattlePhase(this, slot.xPos, slot.yPos, targetSlot.xPos, targetSlot.yPos);
//	                        }
//	                    }
//	                }
//	            }
//	        }
//
//	        if (newCardClone != null)
//	        {
//	            PoolManager.Pools["Card"].Despawn(newCardClone);
//	            newCardClone.GetComponent<CardOnBoardClone>().cloneSlot = null;
//	            newCardClone = null;
//	        }
//	        foreach (CardSlot slot in GameBattleScene.instance.playerSlotContainer)
//	            slot.UnHighLightSlot();
//	        isDragging = false;
//	    }
//	#else
//	    public override void OnTouchDown()
//	    {
//	        base.OnTouchDown();
//	        if (IsPointerOverUIObject())
//	            return;
//	        if (GameBattleScene.instance.skillState != SkillState.None)
//	        {
//	            if (IsSingleTarget(GameBattleScene.instance.skillState))
//	            {
//	                if (GameBattleScene.instance.lstSelectedSkillBoardCard.Count < 1)
//	                {
//	                    if (canSelect)
//	                        onAddToListSkill?.Invoke(this);
//	                }
//	                //else
//	                //{
//	                //    onRemoveFromListSkill?.Invoke(this);
//	                //}
//	            }
//	            else
//	            {
//	                if (GameBattleScene.instance.lstSelectedSkillBoardCard.Count < 2)
//	                {
//	                    if (canSelect)
//	                    {
//	                        onAddToListSkill?.Invoke(this);
//	                        canSelect = false;
//	                    }
//	                    //else
//	                    //{
//	                    //    onRemoveFromListSkill?.Invoke(this);
//	                    //    canSelect = true;
//	                    //}
//	                }
//	                //else
//	                //{
//	                //   // onRemoveFromListSkill?.Invoke(this);
//	                //    canSelect = true;
//	                //}
//	            }
//	        }
//	        else
//	        {
//	            if (GameBattleScene.instance.IsYourTurn && cardOwner == CardOwner.Player)
//	                isTouch = true;
//
//	            else
//	                isTouch = false;
//	        }
//	    }
//	    public override void OnTouchMove()
//	    {
//	        base.OnTouchMove();
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (!isTouch)
//	            return;
//	        if (isDragging)
//	            return;
//	        if (isSelected)
//	        {
//	            Selected();
//	        }
//	        RaycastHit hit;
//	        Ray ray = Camera.main.ScreenPointToRay(GameBattleScene.instance.touch.position);
//	        if (Physics.Raycast(ray, out hit/*, Mathf.Infinity, ~layerMask*/))
//	        {
//	            if (hit.collider != null)
//	            {
//	                if (hit.collider.gameObject != gameObject)
//	                {
//	                    CreateCloneOnDragging();
//	                    isDragging = true;
//	                    isSelected = true;
//	                }
//	                if (GameBattleScene.instance.skillState == SkillState.None && isSelected)
//	                {
//	                    foreach (CardSlot slot in GameBattleScene.instance.ChooseSelfAnyBlank())
//	                    {
//	                        slot.HighLightSlot();
//	                        Debug.Log(slot.gameObject.name);
//	                    }
//	                }
//
//	            }
//	        }
//	    }
//	    public override void OnTouchEnd()
//	    {
//	        base.OnTouchEnd();
//	        Placed();
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (!isTouch)
//	            return;
//	        RaycastHit hit;
//	        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
//
//	        if (Physics.Raycast(ray, out hit, Mathf.Infinity, layerMask))
//	        {
//	            if (hit.collider != null)
//	            {
//	                if (hit.collider.GetComponent<CardSlot>() != null)
//	                {
//	                    if (hit.collider.GetComponent<CardSlot>().type != SlotType.Enemy)
//	                    {
//	                        CardSlot targetSlot = hit.collider.GetComponent<CardSlot>();
//	                        if (!isTired && targetSlot != slot)
//	                        {
//	                            // move god in prepare phase
//	                            if (!GameBattleScene.instance.isGameStarted)
//	                                GameBattleScene.instance.MoveGodInReadyPhase(this, slot.xPos, slot.yPos, targetSlot.xPos, targetSlot.yPos);
//	                            else
//	                                GameBattleScene.instance.MoveCardInBattlePhase(this, slot.xPos, slot.yPos, targetSlot.xPos, targetSlot.yPos);
//	                        }
//	                    }
//	                }
//	            }
//	        }
//
//	        if (newCardClone != null)
//	        {
//	            PoolManager.Pools["Card"].Despawn(newCardClone);
//	            newCardClone.GetComponent<CardOnBoardClone>().cloneSlot = null;
//	            newCardClone = null;
//	        }
//	        foreach (CardSlot slot in GameBattleScene.instance.playerSlotContainer)
//	            slot.UnHighLightSlot();
//	        isDragging = false;
//	    }
//	#endif
//	    private void OnDragging()
//	    {
//	        if (GameBattleScene.instance == null)
//	            return;
//	        if (!GameBattleScene.instance.IsYourTurn)
//	            return;
//	        if (!isDragging)
//	            return;
//	        if (heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//	            return;
//
//	#if UNITY_STANDALONE
//	        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
//	#elif UNITY_ANDROID
//	        Ray ray = Camera.main.ScreenPointToRay(GameBattleScene.instance.touch.position);
//	#endif
//
//	        if (Physics.Raycast(ray, out RaycastHit hit, Mathf.Infinity, layerMask))
//	        {
//	            if (hit.collider != null)
//	            {
//	                if (hit.collider.GetComponent<CardSlot>() != null)
//	                {
//	                    if (hit.collider.GetComponent<CardSlot>().type != SlotType.Enemy)
//	                    {
//	                        CardSlot slot = hit.collider.GetComponent<CardSlot>();
//	                        if (currentSelectedCardSlot == null)
//	                        {
//	                            currentSelectedCardSlot = slot;
//	                            currentSelectedCardSlot.HighlightSelectedSlot();
//	                        }
//	                        else
//	                        {
//	                            if (slot != currentSelectedCardSlot &slot)
//	                            {
//	                                currentSelectedCardSlot.UnHighlightSelectedSlot();
//	                                currentSelectedCardSlot = slot;
//	                                currentSelectedCardSlot.HighlightSelectedSlot();
//	                            }
//	                        }
//	                    }
//	                }
//	            }
//	        }
//	    }
//
//	    public void MoveToSlot(CardSlot targetSlot)
//	    {
//	        if (targetSlot == slot)
//	            return;
//
//	        if (targetSlot != null)
//	        {
//	            Selected();
//	            this.slot.state= SlotState.Empty;
//	            slot = targetSlot;
//	            slot.ChangeSlotState(SlotState.Full, this);
//
//	            MoveTo(targetSlot.transform.position, 0.2f, () =>
//	            {
//	                UpdatePosition();
//	                Placed();
//	            });
//	        }
//	    }
}
