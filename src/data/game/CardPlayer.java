package data.game;

public class CardPlayer {
	public int id;
	public boolean isHost = false;
	public boolean isReady = false;
	public int position;
	public String mobile,username;
	public int balance;
	public boolean isPlaying = false;
	public int quiter;
	
	public CardPlayer(String name, int id, String mobile, 
			int position, boolean isHost,boolean isReady,boolean isPlaying) {
		super();
		this.id = id;
		this.mobile = mobile;
		this.username = name;
		this.position = position;
		this.isHost = isHost;
		this.isReady = isReady;
		this.isPlaying = isPlaying;
		//

	}
}
