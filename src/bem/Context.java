package bem;

import java.util.ArrayList;
import java.util.Vector;

import data.BattlePlayer;
import main.Bot;
import protocol.pb.Pbmethod.ListCommonVector;

public class Context {
	// public K2P session;
	public bem.net.K2P session2;
	//
	public boolean connected = false;
	public boolean[] showed = new boolean[6];
	public String currentGameCode = "";
	public String type = "create";
	public Vector vDisableGame = null;
	public boolean inGame = false;
	// user infor
	public String username, password;
//	public static int GREEN = 1, RED = 2, YELLOW = 3, PURPLE = 4;
//	public int deckColor;
	public int gender;
	public int balance;
	public String guildIcon = "", guildName = "", currentPlayMode = "";
    public ArrayList<BattlePlayer> mLstBattlePlayer = new ArrayList<>();

	public boolean isResume = false;
	public ListCommonVector resumeData;


	public Vector tickerList = new Vector();
//	public int offsetTicker = -MainCanvas.W;
	public int offsetTickerRate = 2;

	public Context() {
		// session = new K2P();
		session2 = new bem.net.K2P(this);
//		offsetTickerRate = MathFP.div(MathFP.toFP(80), MathFP.toFP(1000));
	}

	public void connect() throws Exception {
		Logger.info("------------------------------");
		Logger.info("---------- CONNECT ----------");
		Logger.info("------------------------------");
		try {
//			Thread.sleep(1500L);
			// if (Configuration.USE_PROTO){
			session2.connect();
			// }
			// else{
			// session.connect();
			// }
			connected = true;
		} catch (Exception e) {
			connected = false;
			e.printStackTrace();
			throw e;
		}
	}

	public void close() {
		try {
			connected = false;
			session2.disConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
