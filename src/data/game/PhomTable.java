package data.game;

public class PhomTable {
	public int id;
	public int roomId;
	public String name;
	public String desc;
	public int player;
	public boolean full;
	public boolean isPlay;
	public boolean locked;
	
	public PhomTable(int id, int roomId, String name, String desc, int player,
			boolean isPlay,boolean isLocked,int maxPlayer) {
		this.id = id;
		this.roomId = roomId;
		this.name = name;
		this.desc = desc;
		this.isPlay = isPlay;
		this.player = player;
		this.locked = isLocked;
		this.full = (player == maxPlayer);
	}

}
