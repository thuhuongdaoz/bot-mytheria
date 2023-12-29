package data.game;

import java.util.Vector;

public class TienlenMbResult {
	public String username = "";
	public int balance;
	public int winPosition;
	public int diffBalance;
	public Vector vCardId;
	public int thoi = 0;
	
	public TienlenMbResult(String usn,int winPosition, int balance, int diffBallance,int thoi,Vector vCardId){
		this.username = usn;
		this.winPosition = winPosition;
		this.balance = balance;
		this.diffBalance = diffBallance;
		this.vCardId = vCardId;
		this.thoi = thoi;
	}
}
