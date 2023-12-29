package data;

public class EnumTypes {
	public enum CardOwner
	{
	    Player,
	    Enemy
	}

	public enum CardState
	{
	    OnBoard,
	    OnHand
	}

	public enum CardSkillState
	{
	    None,
	    CHOSE_SELF_BLANK_NEXT,
	    ANY_UNIT_BUT_SELF,
	    TWO_ANY_ENEMIES,
	    ANY_ALLY_UNIT,
	    TWO_ANY_ALLIES,
	    MY_HAND_CARD,
	    ANY_UNIT,
	    ANY_LANE_UNIT,
	    ANY_ENEMY_UNIT,
	    ANY_ALL_UNIT, 
	    ANY_MOTAL,
	    ANY_COL_ENEMY,
	    ANY_TARGET
	}

	public enum SlotState
	{
	    Empty,
	    Full,
	}

	public enum SlotType
	{
	    Player,
	    Enemy
	}

	public enum PopupType
	{
	    Bonus,
	    Damage
	}

	public enum SkillState
	{
	    None,
	    CHOOSE_SELF_BLANK_NEXT,
	    ANY_ALLY_BUT_SELF,
	    TWO_ANY_ENEMIES,
	    ANY_ALLY_UNIT,
	    TWO_ANY_ALLIES,
	    CHOOSE_FOUNTAIN,
	    CHOOSE_LANE,
	    ANY_UNIT,
	    ANY_LANE_UNIT,
	    ANY_ENEMY_UNIT,
	    ANY_ALL_UNIT,
	    MY_HAND_CARD,
	    ANY_MOTAL,
	    ANY_BLANK_ALLY,
	    ANY_LANE_ENEMY, 
	    ANY_COL_ENEMY,
	    ANY_TARGET,
	    ANY_ALLY_TARGET,
	    RANDOM_UNIT_IN_SELECTED_LANE,
	    ANY_TARGET_BUT_SELF,
	    ANY_MORTAL_BUT_SELF,
	    ANY_ALLY_TARGET_BUT_SELF,
	    TWO_ANY_ALLIES_BUT_SELF,
	    ANY_UNIT_BUT_SELF,
	    TWO_ANY_ALLIES_JUNGLE_LAW
	}

	public enum CardType
	{
	    God,
	    Normal,
	    Magic
	}

	public enum ProjectileType
	{
	    Fire,
	    Holy,
	    Fairy,
	    Arcane,
	    Void,
	    Dark,
	    Universe,
	    Water,
	    Shock,
	    Lightning,
	    BuffOther,
	    BuffSelf,
	    Draw,
	    SummonMortal,
	    DamAOE,
	    Act,
	    Fight,
	    Debuff,
	    Heal,
	    LetOtherFight,
	    CreateOnHand,
	    Ready,
	    Discard,
	    HealSelf,
	    DrawSelf

	}
	public enum SkillGodActiveState
	{
	    Activable,
	    Activating,
	    Lock,
	    Unlock
	}
	
	public enum ManaState
	{
	    StartTurn,
	    Simulate,
	    UseDone,
	    Update,
	    None
	}
	
	public enum BATTLE_STATE
	{
	    WAIT_COMFIRM,
	    NORMAL
	}
}
