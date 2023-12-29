package data;

public class LaneController {
	  public long id;
//	    private Transform effectContainer;
//	    private Transform highlighEffect;
//	    private Transform highlightLaneEffect;
	    private boolean canSelect = false;

//	    public event ICallback.CallFunc2<LaneController> onAddToListSkill;
//	    public event ICallback.CallFunc2<LaneController> onEndSkillActive;

	    private void Start()
	    {
//	        GameBattleScene.instance.onEndSkillActive += OnEndSkillActive;
//	        GameBattleScene.instance.onFinishChooseOneTarget += OnEndSkillActive;
	    }

	    public void HighlightLane()
	    {
//	        if (highlightLaneEffect == null)
//	        {
//	            Transform eff = PoolManager.Pools["Effect"].Spawn(highlighEffect.transform, effectContainer);
//	            eff.transform.localPosition = Vector3.zero;
//	            eff.transform.localScale = Vector3.one;
//	            highlightLaneEffect = eff;
//	        }
//	        transform.GetComponent<BoxCollider>().enabled = true;
	        canSelect = true;
	    }

	    public void UnHighlightLane()
	    {
	        canSelect = false;
//	        if (highlightLaneEffect != null)
//	        {
//	            transform.GetComponent<BoxCollider>().enabled = false;
//	            PoolManager.Pools["Effect"].Despawn(highlightLaneEffect);
//	            highlightLaneEffect = null;
//	        }
	    }

	    private void OnMouseDown()
	    {
//	        if (GameBattleScene.instance.skillState == SkillState.None)
//	            return;
//	        if (GameBattleScene.instance.selectedLane == null)
//	        {
//	            if (canSelect)
//	                onAddToListSkill?.Invoke(this);
//	        }
	        //else
	        //    onRemoveFromListSkill?.Invoke(this);
	    }

	    private void OnEndSkillActive()
	    {
//	        UnHighlightLane();
//	        onEndSkillActive?.Invoke(this);
	    }
}
