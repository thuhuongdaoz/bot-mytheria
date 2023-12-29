package data.game;


public class TienlenMbPlayer extends CardPlayer{
	public int numCardLeft = 0;
	
	public TienlenMbPlayer(String name, int id, String mobile, 
			int position, boolean isHost,boolean isReady,boolean isPlaying) {
		super(name, id, mobile, position, isHost, isReady, isPlaying);
	}
}
