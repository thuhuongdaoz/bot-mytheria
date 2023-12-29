package data;

import java.util.ArrayList;

import data.EnumTypes.CardOwner;

public class Card {
	public long battleID;
	public long heroID;
	public long frameC;
	public long coutUsedSkill = 0;
	public long countDoActiveSkill = 0;
	public long countShardAddded = 0;
	// public long skillID;
	// public long effectType;
	public DBHeroSkill skill;
	public ArrayList<DBHeroSkill> lstSkill = new ArrayList<DBHeroSkill>();
	public DBHero heroInfo;
	public boolean canSelect = false;
	public CardOwner cardOwner;
	public boolean isDragging;

	/*
	 * public Cursor cursor; public LayerMask layerMask; public Vector3
	 * initPosition; public Quaternion initRotation; public Transform newCardClone;
	 * public GameObject cardCloneMinion, cardCloneSpell, cardCloneGod;
	 */

	public float currentClickTime, currentTime = 0;
	public boolean isTouch;
	public CardSlot currentSelectedCardSlot;
	public boolean isInteracted;
	public boolean isSelected;
	public boolean isMoving;

	public boolean isHoldM = false;
	public long tmpMana = 0;

    
    
	void Start() {
	}

	public boolean IsPointerOverUIObject() {
		/*
		 * PointerEventData eventDataCurrentPosition = new
		 * PointerEventData(EventSystem.current) { position = new
		 * Vector2(Input.mousePosition.x, Input.mousePosition.y) }; List<RaycastResult>
		 * results = new List<RaycastResult>();
		 * EventSystem.current.RaycastAll(eventDataCurrentPosition, results); return
		 * results.Count > 0;
		 */
		return false;
	}

	void Update() {
		// if (UIManager.instance.godInfoObject == null ||
		// UIManager.instance.godInfoObjectEnemy == null)
		// return;

		// Debug.Log(Input.touchCount + "__________________________________1");
		// if (UIManager.instance.godInfoObject.gameObject.activeSelf ||
		// UIManager.instance.godInfoObjectEnemy.gameObject.activeSelf)
		// return;
		// Debug.Log(Input.touchCount + "__________________________________2");
		// if (IsPointerOverUIObject())
		// return;
		// Debug.Log(Input.touchCount + "__________________________________3");
		// if (Input.GetMouseButtonDown(1))
		// return;
		// Debug.Log(Input.touchCount + "__________________________________4");
		// UIManager.instance.godInfoObject.gameObject.SetActive(false);
		// UIManager.instance.godInfoObjectEnemy.gameObject.SetActive(false);

	}

	void OnDisable() {
		heroID = -1;
		battleID = -1;
	}

	public void OnEndRound(long index) {
		if (!isDragging)
			return;
	}

	public void OnGameConfirmStartBattle() {
		isDragging = false;
//	        countDoActiveSkill = 0;
//	        if (cursor != null)
//	        {
//	            cursor.gameObject.SetActive(true);
//	            PoolManager.Pools["Card"].Despawn(cursor.transform);
//	            cursor = null;
//	            if (newCardClone != null)
//	            {
//	                PoolManager.Pools["Card"].Despawn(newCardClone);
//	                newCardClone.GetComponent<CardOnBoardClone>().cloneSlot = null;
//	                newCardClone = null;
//	            }
//
//	        }
	}

	public void SetCardData(long battleID, long id, long frame, CardOwner owner) {
	        this.battleID = battleID;
	        heroID = id;
	        this.frameC = frame;
	        cardOwner = owner;
	        heroInfo = Database.GetHero(id);
	        //UpdateHeroMatrix(heroInfo.atk, heroInfo.hp, -1, heroInfo.cleave, heroInfo.pierce, heroInfo.breaker, heroInfo.combo, heroInfo.overrun, -1, godSlayerValue, 0);
//
	        heroInfo.lstHeroSkill.forEach(x ->
	        {
	            lstSkill.add(x);
	        });
	}

	public void SetSkill(DBHeroSkill skill) {
		this.skill = skill;
	}

	public void SetSkillReady(DBHeroSkill skill) {
		this.skill = skill;
	}

	public void OnClickInfo() {
		if (heroInfo.type == DBHero.TYPE_GOD) {
			// if (cardOwner == CardOwner.Player)
			// {
			// if(!UIManager.instance.godInfoObject.gameObject.activeInHierarchy)
			// {
			// UIManager.instance.godInfoObject.gameObject.SetActive(true);
			// UIManager.instance.godInfoObject.SetGodInfoData(this);
			// }
			// else
			// {
			// UIManager.instance.godInfoObject.gameObject.SetActive(false);
			// }
			// }
			// else
			// {
			// if(!UIManager.instance.godInfoObjectEnemy.gameObject.activeInHierarchy)
			// {
			// UIManager.instance.godInfoObjectEnemy.gameObject.SetActive(true);
			// UIManager.instance.godInfoObjectEnemy.SetGodInfoData(this);
			// }
			// else
			// {
			// UIManager.instance.godInfoObjectEnemy.gameObject.SetActive(false);
			// }
			ShowCardInfo();
			// }
		} else {
			ShowCardInfo();
		}
	}

	public void ShowCardInfo() {
//		UIManager.instance.ShowPreviewHandCard(this, heroID, this.frameC);

	}

	public void OnActiveSkill(DBHeroSkill cardSkill) {
		skill = cardSkill;
//		UIManager.instance.godInfoObject.gameObject.SetActive(false);
//		if (cardOwner == CardOwner.Enemy)
//			return;
//
//		if (GameBattleScene.instance.IsYourTurn && GameBattleScene.instance.isGameStarted) {
//			GameBattleScene.instance.DoActiveSkill(this);
//			GameBattleScene.instance.DoActiveSkill(this);
//
//		}

	}

	public void Interested(boolean value, boolean animateColor) {
		isInteracted = value;
	}

//	public void MoveBack(float time = 0.2f, ICallback.CallFunc callback = null)
//	    {
//	        MoveTo(initPosition, time, () =>
//	        {
//	            callback?.Invoke();
//
//	        });
//	        transform.rotation = initRotation;
//	    }
//
//	public void MoveTo(Vector3 to, float duration, ICallback.CallFunc complete = null)
//	    {
//	        isMoving = true;
//	        transform.DOKill();
//	        transform.DOMove(to, duration).OnComplete(() =>
//	        {
//	            complete?.Invoke();
//	            isMoving = false;
//	        });
//	    }

	void HighlightUnit() {
		canSelect = true;
	}

	public void UnHighlightUnit() {
		canSelect = false;
	}

	public void OnEndSkillActive() {

	}

	public void UpdatePosition() {

	}

	public void OnMouseDown() {
//		if (IsPointerOverUIObject())
//			return;
//		if (GameBattleScene.instance.skillState == SkillState.None)
//			currentClickTime = Time.time;
	}

	public void OnMouseDrag() {
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (GameBattleScene.instance.battleState == BATTLE_STATE.WAIT_COMFIRM)
//	            return;
//	        if (!isTouch)
//	            return;
//	        if (Time.time - currentClickTime < 0.3f)
//	            return;
//	        if (isDragging)
//	            return;
//	        if (GameBattleScene.instance.skillState != SkillState.None)
//	            return;

	}

	public void OnMouseUp() {
//	        if (Time.time - currentClickTime <= 0.3 && GameBattleScene.instance.skillState == SkillState.None)
//	        {
//	            OnClickInfo();
//	            return;
//	        }
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (!isTouch)
//	            return;
//	        if (!isDragging)
//	            return;
//	        isDragging = false;
//	        isSelected = false;
//	        if (currentSelectedCardSlot != null)
//	        {
//	            currentSelectedCardSlot.UnHighlightSelectedSlot();
//	            currentSelectedCardSlot = null;
//	        }
//	        if (cursor != null)
//	        {
//	            PoolManager.Pools["Card"].Despawn(cursor.transform);
//	            cursor = null;
//	        }
	}

	public void OnMouseOver() {
//	        if (cardOwner != CardOwner.Player)
//	            return;
//	        if (IsPointerOverUIObject())
//	            return;
//	        if (!isInteracted)
//	            return;
//	        if (isDragging)
//	            return;
	}

	public void OnMouseExit() {
//	        if (cardOwner != CardOwner.Player)
//	            return;
//	        if (!isInteracted)
//	            return;
//	        if (isDragging)
//	            return;
	}

	public void OnTouchDown() {
//		if (IsPointerOverUIObject())
//			return;
//		if (GameBattleScene.instance.skillState == SkillState.None)
//			currentClickTime = Time.time;
	}

	public void OnTouchHold() {
//	        if (cardOwner != CardOwner.Player)
//	            return;
//	        if (isInteracted)
//	            return;
//	        isHoldM = true;
	}

	public void OnTouchEnd() {

//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (!isTouch)
//	            return;
//	        if (!isDragging)
//	            return;
//	        isDragging = false;
//	        //isSelected = false;
//	        if (currentSelectedCardSlot != null)
//	        {
//	            currentSelectedCardSlot.UnHighlightSelectedSlot();
//	            currentSelectedCardSlot = null;
//	        }
//	        if (cursor.transform != null)
//	        {
//	            PoolManager.Pools["Card"].Despawn(cursor.transform);
//	            cursor = null;
//	        }
	}

	public void OnTouchMove() {
//	        if (cardOwner == CardOwner.Enemy)
//	            return;
//	        if (GameBattleScene.instance.battleState == BATTLE_STATE.WAIT_COMFIRM)
//	            return;
//	        if (!isTouch)
//	            return;
//	        //if (Time.time - currentClickTime < 0.3f)
//	        //    return;
//	        if (isDragging)
//	            return;
//	        if (GameBattleScene.instance.skillState != SkillState.None)
//	            return;
	}

	public void OnEndHold() {
//	        if (cardOwner != CardOwner.Player)
//	            return;
//	        if (!isInteracted)
//	            return;
//	        if (isDragging)
//	            return;
//	        isHoldM=false;
	}

	public void MoveFail() {
//		if (cursor != null) {
//			PoolManager.Pools["Card"].Despawn(cursor.transform);
//			cursor = null;
//		}
//		if (newCardClone != null) {
//			PoolManager.Pools["Card"].Despawn(newCardClone);
//			newCardClone = null;
//		}
	}

	public void CreateCloneOnDragging() {
//	        Transform trans = PoolManager.Pools["Card"].Spawn(GameBattleScene.instance.cursorObject);
//	        cursor = trans.GetComponent<Cursor>();
//	        if (newCardClone == null)
//	        {
//	            // spawn for both spell, minion, god
//	            if (heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//	                newCardClone = PoolManager.Pools["Card"].Spawn(cardCloneSpell.transform);
//	            else if (heroInfo.type == DBHero.TYPE_TROOPER_NORMAL)
//	                newCardClone = PoolManager.Pools["Card"].Spawn(cardCloneMinion);
//	            else
//	                newCardClone = PoolManager.Pools["Card"].Spawn(cardCloneGod);
//	            newCardClone.GetComponent<CardOnBoardClone>().InitData(heroID,battleID,frameC);
//	            newCardClone.gameObject.SetActive(false);
//	        }
//	        cursor.gameObject.SetActive(true);
//	        cursor.InitCursor(transform.position, this, newCardClone);
	}
}
