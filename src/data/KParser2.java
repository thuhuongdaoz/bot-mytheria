package data;


public class KParser2 {
	

	public static Object[] getJoinRoomResult(Object obj) {
		try {
//			ListGambTableResponse rps = (ListGambTableResponse) obj;
//			Vector vGambTable = rps.getLstTable();
//			PhomTable[] kTables = new PhomTable[vGambTable.size()];
//			for (int i = 0; i < kTables.length; i++) {
//				GambTable table = (GambTable) vGambTable.elementAt(i);
//				PhomTable kTable = new PhomTable(table.getId(), table.getRoomId(), table.getRoomId() + "", table.getBlind() + "$", table.getNumPlayer(), table.getIsPlaying() == 1, table.getIsLocked() == 1, table.getMaxPlayer());
//				kTables[i] = kTable;
//			}
//			int balance = rps.getBalance();
//			return new Object[] { kTables, new Integer(balance) };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static Object[] getJoinTienlenMbTableResult(Object obj) {
		try {
//			JoinTableResponse rps = (JoinTableResponse) obj;
//			Vector vGambPlayer = rps.getLstPlayer();
//			TienlenMbPlayer[] kPlayers = new TienlenMbPlayer[vGambPlayer.size()];
//			for (int i = 0; i < kPlayers.length; i++) {
//				GambPlayer protoPlayer = (GambPlayer) vGambPlayer.elementAt(i);
//				boolean playing = false;
//				if (!playing) {
//					playing = protoPlayer.getIsPlaying() == 1;
//				}
//				//
//				TienlenMbPlayer kPlayer = new TienlenMbPlayer(protoPlayer.getUsername(), protoPlayer.getId(), "",// player.getString("mobile"),
//						protoPlayer.getSeatPosition(), protoPlayer.getIsHost() == 1, protoPlayer.getIsReady() == 1, playing);
//				kPlayer.balance = protoPlayer.getBalance();
//				int gender = protoPlayer.getGender();
//				Vector vAvtId = protoPlayer.getLstAvatarId();
//				kPlayer.avtInfo = LoadingImageManager.addImage(new LoadingImage(null, protoPlayer.getImgAvatar()));// new
//																													// AvatarInfo(gender,
//																													// vAvtId);
//				kPlayer.iconLv = LoadingImageManager.addImage(new LoadingImage(null, "medal_" + protoPlayer.getLevel()));
//				kPlayer.numCardLeft = protoPlayer.getTlNumCardLeft();
//				//
//				kPlayers[i] = kPlayer;
//			}
//			// deposit
//			Integer deposit = new Integer(rps.getBlind());
//			//
//			PhomRoom mRoom = new PhomRoom(rps.getRoomId(), "", 0, "");
//			String tableName = rps.getTableName();
//			return new Object[] { kPlayers, deposit, mRoom, tableName };

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
}