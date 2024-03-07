package bem.net;

import main.MemUtil;
import protocol.pb.Pbmethod.CommonVector;
import protocol.pb.Pbmethod.ListAction;
import protocol.pb.Pbmethod.ListCommonVector;

import java.util.Vector;

import tvlib.util.Util;
import bem.Context;
import bem.KException;
import bem.Logger;
import bem.event.IKEventListener;
import bem.event.IService;
import bem.event.KEvent;
import data.KParser2;

public class InputThread extends Thread {
	public boolean quit = false;
	private Context instance;
	private SocketHandler socket;
	//
	// Event listener
	public Vector listeners;

	public InputThread(Context context,SocketHandler socket) {
		instance = context;
		this.socket = socket;
		this.start();
		listeners = new Vector();
		Logger.debug("------ Started input thread");
	}

	public void run() {		
		while (!quit) {
			
			Thread.yield();
			try {
				Logger.debug("check for icomming message");
				process(socket.receivePacket());
				Thread.sleep(50);
			} catch (Exception io) {
				io.printStackTrace();
				quit = true;
				Logger.error(io.getMessage());
//				MainApp.getCurrentScreen().showNoRespondingMessage();
				instance.close();
			} finally {
//				 closeNetwork();
			}
		}
		
	}

//	public void stop() {
//		quit = true;
//	}

	private void process(K2Packet pkt) throws Exception {
		if (pkt == null) {
			quit = true;
			Logger.debug("QUIT vi ko doc duoc packet");
			return;
		}
		
		MemUtil.logMem();
		//
		if (pkt.service == Constants.ERROR) {
//			CommonString msgProto = (CommonString) pkt.getDataObject();
//			String msg = "";
//			if (msgProto == null) {
//				Logger.debug("----------------- error read 64000");
//				msg = "Lá»—i Ä‘á»�c dá»¯ liÃªu server";
//			} else {
//				msg = msgProto.getValue();
//			}
			return;
		}
		try {
			switch (pkt.service) {
				case IService.PONG:
					instance.session2.SendPong();
					break;


			case IService.GAME_UPDATE_HERO_MATRIC: 
			case IService.GAME_FIRST_GOD_SUMMON:
			case IService.GAME_MOVE_GOD_SUMMON:
			case IService.GAME_STARTUP_CONFIRM:
			case IService.GAME_DELETE_CARDS:
			case IService.GAME_BATTLE_LEAVE: 
			case IService.GAME_CHOOSE_WAY_REQUEST: 
			case IService.GAME_SIMULATE_CONFIRM:
			case IService.LOGIN:
			case IService.LOGIN_NORMAL:
			case IService.SET_USER_BATTLE_DECK:
			case IService.GAME_START:
			{
				
				CommonVector proto = (CommonVector) pkt.getDataObject();
				fireEvent(new KEvent(pkt.service, new Object[] { proto }));
				
				break;

			}
			
			case IService.GAME_MULLIGAN: {
//				Object[] args = event.getArgs();
//				CommonVector commonVector = (CommonVector) args[0];
				// GameCardMulligan(commonVector);
				break;
			}
			
			case IService.GAME_BATTLE_JOIN:
			case IService.GAME_RESUME:
			case IService.GAME_DEAL_CARDS:
			case IService.GAME_STARTUP_END: 
			case IService.GAME_BATTLE_ENDGAME: 
			{
				ListCommonVector proto = (ListCommonVector) pkt.getDataObject();
				fireEvent(new KEvent(pkt.service, new Object[] { proto }));
				
				break;
			}
			case IService.GAME_START_BATTLE:
			case IService.GAME_MOVE_CARD_IN_BATTLE:
			case IService.GAME_SUMMON_CARD_IN_BATTLE:
			case IService.GAME_CONFIRM_STARTBATTLE:
			case IService.GAME_SIMULATE_BATTLE: 
			case IService.GAME_SIMULATE_SKILLS:
			case IService.GAME_ACTIVE_SKILL:
			case IService.GAME_BATTLE_ENDROUND:
			{
				ListAction proto = (ListAction) pkt.getDataObject();
				fireEvent(new KEvent(pkt.service, new Object[] { proto }));
				
				break;
			}

		
		
			default:
				Logger.debug("---------- Not fire event in input thread yet");
				break;
			// //////////// PHOM
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	

	private void joinTableReceived(Object obj) {
		try {
			if (Util.isTienLen(instance.currentGameCode)) {
				Object[] args = KParser2.getJoinTienlenMbTableResult(obj);
//				fireEvent(new KEvent(KEvent.TABLE_JOIN, args));
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	

	public void addEventListener(IKEventListener listener) {
		listeners.addElement(listener);
	}

	public void removeEventListener(IKEventListener listener) {
		listeners.removeElement(listener);
	}

	public void fireEvent(KEvent event) {
		(new FireEvent()).fire(event);
	}

	/**
	 * 
	 * @author sam
	 * 
	 */
	private class FireEvent extends Thread {

		KEvent event;

		void fire(KEvent event) {
			this.event = event;
			start();
		}

		public void run() {
			try {
				if (listeners != null) {

					for (int i = 0; i < listeners.size(); i++) {
						IKEventListener listener = (IKEventListener) listeners.elementAt(i);
						if (listener.dispatch(event))
							return;

					}
				}

			} catch (KException e) {
				Logger.error(e.getMessage());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}
}
