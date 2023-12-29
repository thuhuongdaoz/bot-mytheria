package data;

import java.util.ArrayList;
import java.util.List;

public class SkillEffect
{
    public long typeEffect;
    public long defCount; // so luong quan bi anh huong;
    public ArrayList<BoardCard> lstCardImpact= new ArrayList<BoardCard>();
    public ArrayList<TowerController> lstTowerImpact = new ArrayList<TowerController>();

}
