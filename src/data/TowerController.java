package data;

public class TowerController {
	public static final int HEAlTH = 15;
	public int pos;
	public int id;
	public long towerHealth;
	private boolean canSelect = false;

//	      private TextMeshPro healthText;
//	      private Transform highlighEffect;
//	      private GameObject towerHealthBorder;
//	      private SpriteRenderer towerSprite;
//	      private Sprite[] towerState;
//	    private VisualEffect highlightVisualEffect;
//	    
//	    private VisualEffect healingVisualEffect;
//	      private Transform healingEffect;
//
//	    public ICallback.CallFunc2<TowerController> onAddToListSkill;
//	    //public ICallback.CallFunc2<TowerController> onRemoveFromListSkill;
//	    public ICallback.CallFunc2<TowerController> onEndSkillActive;

	public void Start(int pos, int id) {
//	        GameBattleScene.instance.onEndSkillActive += OnEndSkillActive;
		this.pos = pos;
		this.id = id;
		towerHealth = HEAlTH;
	}

	public void ActiveMiddleTowerHealth() {
//	        towerHealthBorder.SetActive(true);
	}

	public void OnDamaged(long damage, long health, boolean isDestroyed) {
//	        DamagePopup.Create(transform.position, damage, PopupType.Damage);
	        UpdateHealth(health);
//	        if (isDestroyed)
//	        {
//	            if(id!= 1)
//	            {
//	                CameraShaker.Instance.ShakeOnce(5, 5, .1f, 2f);
//	            } 
//	            else
//	            {
//	                CameraShaker.Instance.ShakeOnce(6, 5, .1f, 4f);
//	            }    
//	            SoundHandler.main.PlaySFX("Nexus Explosion", "sounds");
//	        }
	}

	public void OnHealing(long health, long value) {
	        UpdateHealth(health);
//	        if (healingVisualEffect == null)
//	        {
//	            Transform effect = PoolManager.Pools["Effect"].Spawn(healingEffect);
//	            effect.position = transform.position;
//	            effect.parent = transform;
//	            healingVisualEffect = effect.GetComponent<VisualEffect>();
//	        }
//	        if (healingVisualEffect != null)
//	            healingVisualEffect.Play();
//	        //canSelect = false;
//
//	        DamagePopup.Create(transform.position, value, PopupType.Bonus, () =>
//	        {
//	            healingVisualEffect.Stop();
//	        });
	}

	public void UpdateHealth(long health) {
		towerHealth = health;
//	        healthText.text = towerHealth.ToString();
//	        if (id != 1)
//	        {
//	            if (health <= 0)
//	                towerSprite.sprite = towerState[0];
//	            else if (health <= 5)
//	                towerSprite.sprite = towerState[1];
//	            else if (health <= 10)
//	                towerSprite.sprite = towerState[2];
//	            else
//	                towerSprite.sprite = towerState[3];
//	        }
	}

	public void OnBeingAttacked() {
	}

	public void HighLightTower() {
//	        if (highlightVisualEffect == null)
//	        {
//	            Transform eff = PoolManager.Pools["Effect"].Spawn(highlighEffect.transform);
//	            eff.transform.position = transform.position;
//	            eff.transform.parent = transform;
//	            eff.transform.localScale = new Vector3(0.4f, 0.4f, 0.4f);
//	            highlightVisualEffect = eff.GetComponent<VisualEffect>();
//	        }
//	        highlightVisualEffect.Play();
//	        canSelect = true;
	}

	public void UnHighlightTower() {
//	        canSelect = false;
//	        if (highlightVisualEffect != null)
//	        {
//	            highlightVisualEffect.Stop();
//	        }
	}

	private void OnEndSkillActive() {
//	        UnHighlightTower();
//	        onEndSkillActive?.Invoke(this);
	}

	private void OnMouseDown() {
//	        if (GameBattleScene.instance.skillState == SkillState.None)
//	            return;
//
//	        if (GameBattleScene.instance.selectedTower == null)
//	        {
//	            if (canSelect)
//	                onAddToListSkill?.Invoke(this);
//	        }

	}
}
