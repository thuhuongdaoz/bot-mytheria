package bem.net;

import bem.Context;
import bem.Logger;
import bem.event.IService;
import bem.event.KEvent;
import protocol.pb.Pbmethod.CommonVector;
import protocol.pb.Pbmethod.ListAction;
import protocol.pb.Pbmethod.ListCommonVector;

/**
 * This class is nothing more than a convenient data structure to hold the
 * information extracted from a single K2 packet (message)
 * 
 * Note: the term 'packet' here is strictly speaking incorrect, as a K2 message
 * could in theory take up more than one TCP packet - but it helps to
 * distinguish these lower-level network messages from the higher-level dialogue
 * 'message's in the protocol.
 */

public class K2Packet {
	public int service;
	public byte[] body;

	public K2Packet(int service) {
		this.service = service;
		this.body = null;
	}

	public K2Packet(int service,byte[] body){
		this.service = service;
		this.body = body;
	}
	
	public Object getDataObject(){
		try {
			Object ret = null;
			switch (service){
			
			case IService.GAME_UPDATE_HERO_MATRIC: 
			case IService.GAME_FIRST_GOD_SUMMON:
			case IService.GAME_MOVE_GOD_SUMMON:
			case IService.GAME_STARTUP_CONFIRM: 
			case IService.GAME_DELETE_CARDS:
			case IService.GAME_BATTLE_LEAVE: 
			case IService.GAME_CHOOSE_WAY_REQUEST: 
			case IService.GAME_SIMULATE_CONFIRM:
			case IService.GAME_BID_MOVE_TO_PREPARE:

				ret = CommonVector.parseFrom(body);
				break;
				
			case IService.GAME_DEAL_CARDS:
			case IService.GAME_STARTUP_END: 
			case IService.GAME_BATTLE_ENDGAME: 
			case IService.GAME_BATTLE_JOIN:
//			case IService.GAME_RESUME:
			{
				ret = ListCommonVector.parseFrom(body);
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
			case IService.GAME_BID_RESULT:
			{
				ret = ListAction.parseFrom(body);
				break;
			}
			default:
				ret = CommonVector.parseFrom(body);
				break;
			}
			if (ret == null){
				Logger.debug("================= Not parse proto object yet - service:" + service);
			}else {
				Logger.debug("--------- Parse object success -----------");
				Logger.debug(ret.toString());
			}
			//
			return ret;
		}catch (Exception ex){
			ex.printStackTrace();
			Logger.debug("------ Error parse object form bytes data");
		}
		//
		return null;
	}
	
	protected Object parseStartGameData(byte[] data) throws Exception{
		try {
			if (true
//					Context.instance.currentGameCode.equals(Constants.GAME_TIENLEN_MB)||
//					Context.instance.currentGameCode.equals(Constants.GAME_TIENLEN_MN)||
//					Context.instance.currentGameCode.equals(Constants.GAME_TIENLEN_MN_DC)
					)
			{
				return CommonVector.parseFrom(body);
			}
		}catch (Exception ex){
			throw ex;
		}
		return null;
	}
	
	protected void printByteArray(byte[] bytes){
		for (int i =0; i <bytes.length ;i++){
			Logger.debug(bytes[i] + " ");
		} 
		
	}
}
