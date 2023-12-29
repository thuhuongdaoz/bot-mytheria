package net.k2tek.gamb;

import java.util.Random;
import java.util.Vector;

import bem.Configuration;
import bem.Context;
import bem.KException;
import bem.Logger;
import bem.event.IKEventListener;
import bem.event.IService;
import bem.event.KEvent;
import data.game.PhomRoom;
import main.Bot;
import main.CommonEvent;
import tvlib.util.Util;

public abstract class BaseGambScreen implements IKEventListener, GambActionHandler {
	protected Context instance;

	// seat position
	protected final int OWNER_EXPECTED_INDEX = 2;
	protected final int[] ORIN_POS = new int[] { P12H, P9H, P6H, P3H };
	//
	public final static int P12H = 0;
	public final static int P3H = 3;
	public final static int P6H = 6;
	public final static int P9H = 9;

		// ///////

	
	// luu lai thong tin room truoc
	protected PhomRoom roomData;
	//
	protected final String MSG_PLAYING = "Ván bài đã bắt đầu";
	protected boolean gamePlaying = false;
	//
	
	public boolean isMuteAll = false;

	//
	protected int[] playerPos = new int[] { P12H, P9H, P6H, P3H };
	//

	// New
	protected int countQuiterChat;
	protected String quiterChat = "Điểm penalty của tôi khá cao, tôi có thể quit giữa game!";

	public BaseGambScreen(Context context) {
		instance = context;
		initSmiley();

	}

	public void setRoomInfo(PhomRoom room, String tableName) {
		this.roomData = room;
		
	}

	public void onActivate() {
		instance.session2.addEventListener(this);
	}

	public void onDeactivate() {
		
	}

	protected int getPosIndex(int pos) {
		for (int i = 0; i < ORIN_POS.length; i++) {
			if (pos == ORIN_POS[i])
				return i;
		}
		return 0;
	}

	public void initSmiley() {
	}

	public boolean isLogout = false;

	public boolean dispatch(KEvent event) throws KException {

//		switch (event.getType()) {
//
//		case KEvent.LOGIN_OK:
//			Logger.debug("Login OK fired");
////			LoginResponse loginRps = (LoginResponse) event.getObject();
////			// User
////			bem.proto.User userProto = loginRps.getUser();
////			instance.balance = userProto.getBalance();
////			instance.gender = userProto.getGender();
////			instance.vDisableGame = loginRps.getLstDisableGame();
////			// Configuration.hasFriendNeedProcess =
////			// loginRps.getHasRequestFriend();
////			// Configuration.hasUnreadMessage = loginRps.getHasMessage();
////			instance.username = userProto.getUsername();
////			instance.guildIcon = userProto.getGuildIcon();
////			instance.guildName = userProto.getGuildName();
////
////			// join game
////			System.out.println("Đăng nhập thành công :" + userProto.getUsername());
////			// instance.session2.quickTabke(instance.currentGameCode);
////			String game = instance.currentGameCode;
////			// int max1 = 5000;
////			// int min1 = 1000;
////			// Random r1 = new Random();
////			//
////			// int id = r1.nextInt(max1 - min1 + 1) + min1;
////
////			// instance.session2.createTable(1000 + new
////			// Random().nextInt(10) * 1000, 4, "", game);
////
////			if (instance.type.equals(Bot.TYPE_CREATE_NEW)) {
////				Random r1 = new Random();
////				int id = 1 + r1.nextInt(5);
////				instance.session2.createTable(id * 1000, 4, "", game);
////			} else {
////				instance.session2.quickTabke(game);
////			}
//
//			break;
//
//		case IService.GAME_BATTLE_JOIN:
//			if (isLogout) {
//				try {
//					// instance.session2.removeEventListener(this);
//					CommonEvent.dispatch(event);
//
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//				isLogout = false;
//			}
//
//			break;
//
//		// case KEvent.TABLE_LEAVE:
//		// instance.session2.logout();
//		// System.out.println("table leave");
//		// break;
//
//		case KEvent.LOGOUT_OK:
//			System.out.println("logout ok");
////			isLogout = true;
////			try {
////				Thread.sleep(10000);
////				instance.session2.login(instance.username, instance.password);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//
//			break;
//		default:
//			break;
//		}

		return false;
	}



	protected int getPlayerIndex(String playerName) {
		
		return -1;
	}
}
