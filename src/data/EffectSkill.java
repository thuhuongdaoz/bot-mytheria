package data;

import java.util.ArrayList;
import java.util.List;

public class EffectSkill
{
    //effect của 1 mệnh đề trong skill
    public  int type, target, heroId;
    public String desc;
    public boolean isFragile;
    public List<SubEffect> subEffects = new ArrayList<>();

}

