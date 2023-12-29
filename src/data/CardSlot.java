package data;

import java.util.ArrayList;

import data.EnumTypes.SlotState;
import data.EnumTypes.SlotType;

public class CardSlot {
	/*
	private Transform effectContainer;
	private GameObject slotEffectPrefab;
	private GameObject slotSelectEffectPrefab;
	private GameObject slotEffect, selectSlotEffect;
	 */

	public int xPos;
	public int yPos;
	/* [HideInInspector] */
	public Card currentCard;
	
	public SlotState state = SlotState.Empty;
	public SlotType type;
	
	private boolean canSelect = false;
	
	  public CardSlot slot;
	    public boolean isFragile;
	  public boolean isTired;
	    public DBHeroSkill skill;
	    public ArrayList<DBHeroSkill> lstSkill = new ArrayList<DBHeroSkill>();
	    public DBHero heroInfo;
	/*
	private Transform summonEffect;
	private SkeletonAnimation summonSkeleton;
	
	public event ICallback.
	CallFunc2<CardSlot> onAddToListSkill;
	// public event ICallback.CallFunc2<CardSlot> onRemoveFromListSkill;
	public event ICallback.
	CallFunc2<CardSlot> onEndSkillActive;
	*/

	public CardSlot(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	private void Start() {
		/*
		GameBattleScene.instance.onEndSkillActive += OnEndSkillActive;
		GameBattleScene.instance.onFinishChooseOneTarget += OnEndSkillActive;
		effectContainer = transform.GetChild(0);
		*/
	}
	
//	 	  public void MoveToSlot(CardSlot targetSlot)
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

	public void ChangeSlotState(SlotState targetState, BoardCard card) {
		state = targetState;
		currentCard = card;
	}

	public void HighLightSlot() {
//		if (slotEffect == null) {
//			slotEffect = PoolManager.Pools["Effect"].Spawn(slotEffectPrefab).gameObject;
//			slotEffect.transform.parent = effectContainer;
//			slotEffect.transform.localPosition = Vector3.zero;
//		}
//		slotEffect.SetActive(true);
		canSelect = true;
	}

	public void HighlightSelectedSlot() {
//		if (slotEffect != null && canSelect) {
//			selectSlotEffect = PoolManager.Pools["Effect"].Spawn(slotSelectEffectPrefab).gameObject;
//			selectSlotEffect.transform.parent = effectContainer;
//			selectSlotEffect.transform.localPosition = Vector3.zero;
//			selectSlotEffect.SetActive(true);
//			slotEffect.SetActive(false);
//		}

	}

	public void UnHighlightSelectedSlot() {
//		if (selectSlotEffect != null)
//			selectSlotEffect.SetActive(false);
//		if (slotEffect != null && canSelect)
//			slotEffect.SetActive(true);
	}

	public void UnHighLightSlot() {
		canSelect = false;
//		if (slotEffect != null)
//			slotEffect.SetActive(false);
//		if (selectSlotEffect != null)
//			selectSlotEffect.SetActive(false);
	}

	private void OnMouseDown()
	    {
//	        if (GameBattleScene.instance.skillState == SkillState.None)
//	            return;
//	        if (GameBattleScene.instance.selectedCardSlot == null)
//	        {
//	            if (canSelect)
//	                onAddToListSkill?.Invoke(this);
//	        }
	        //else
	            //onRemoveFromListSkill?.Invoke(this);
	    }

	private void OnEndSkillActive()
	    {
//	        UnHighLightSlot();
//	        onEndSkillActive?.Invoke(this);
	    }
}
