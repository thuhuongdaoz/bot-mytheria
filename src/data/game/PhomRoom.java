package data.game;

public class PhomRoom {
	
	public int id;
	public String name;
	public int online;	
	//de group room
	public String type;
	//de set blind range
	public int minBlind;
	public int maxBlind;
		
	
	public PhomRoom(int id, String name, int online,String type) {
		super();
		this.id = id;
		this.name = name;
		this.online = online;		
		this.type = type;
	
	}

}
