package main;

import bem.Context;
import bem.KException;
import bem.event.IService;
import bem.event.KEvent;
import big2.screen.game.battle.Mytheria;
import data.BattlePlayer;
import protocol.pb.Pbmethod.ListCommonVector;

public class CommonEvent {
	Context instance;
	public CommonEvent(Context instance){
		this.instance = instance;
	}

	public void dispatch(KEvent event) throws KException {
		switch (event.getType()) {
		case IService.GAME_BATTLE_JOIN:
			// if (true)
			// return true;
			try {
//				System.out.println("Vào bàn ");
				// ////////////////
//			if (Util.isTienLen(Context.instance.currentGameCode)) 
			{

//					Object[] args = event.getArgs();
//					ListCommonVector listCommonVector = (ListCommonVector) args[0];
//					int num = listCommonVector.getAVectorCount();
//
//					for (int i = 0; i < num; i ++) {
//
//			            BattlePlayer player = new BattlePlayer();
//			            player.position = listCommonVector.getAVector(i).getALong(0);
//			            player.id = listCommonVector.getAVector(i).getALong(1);
//			            player.username = listCommonVector.getAVector(i).getAString(0);
//			            player.screenname = listCommonVector.getAVector(i).getAString(1);
//			            instance.mLstBattlePlayer.add(player);
//					}



					
				
//					TienlenMbPlayer[] pls = (TienlenMbPlayer[]) args[0];
//					int deposit = ((Integer) args[1]).intValue();
//					PhomRoom mRoomData = (PhomRoom) args[2];
//					String tableName = args[3].toString();
//					screenTlmb.setPlayers(pls, deposit);
//					screenTlmb.setRoomInfo(mRoomData, tableName);
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
}
