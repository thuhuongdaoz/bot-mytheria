package bem.net;

import java.io.IOException;
import java.util.ArrayList;
import com.google.protobuf.GeneratedMessageV3;
import bem.Configuration;
import bem.Context;
import bem.Logger;
import bem.event.IKEventListener;
import bem.event.IService;
import bem.event.KEvent;
import protocol.pb.Pbmethod.CommonVector;
import protocol.pb.Pbmethod.ListCommonVector;
import tvlib.util.Util;

public class K2P {
	// The actual socket
    Context instance;
	SocketHandler socket;

	// in,out thread
	public InputThread inputThread;
	public OutputThread outputThread;

	public K2P(Context context) {
        instance = context;
		socket = new SocketHandler(Configuration.HOST_PROTO, Configuration.PORT_PROTO, Configuration.ALT_HOST);
	}

    public void SendPong() { sendPackage(IService.PONG); }
    public void MultiLanguage(int type)
    {
        CommonVector proto = CommonVector.newBuilder()
				.addALong(type).build();
        sendPackage(IService.MULTI_LANGUAGE, proto);
    }

    public void Logout()
    {
//        GamePrefs.Logout();
        sendPackage(IService.LOGOUT);
    }
    public void CheckVersion(String ver)
    {
//        Debug.Log("ver" + ver);
        CommonVector commonVector = CommonVector.newBuilder()
				.addAString(ver).build();
        sendPackage(IService.CHECK_VERSION,commonVector);
    }
    
    public void Register(String username, String password, String email)
    {
        CommonVector proto = CommonVector.newBuilder()
        		.addAString(username)
        		.addAString(password)
		        .addAString(Constants.VERSION+"")
		        .addAString(Constants.OS_TYPE)
		        .addAString(Constants.CP_CODE)
		        .addAString(Constants.DEVICE_ID)
		        .addAString(Constants.CLIENT_IP)
		        .addAString(email).build();

        sendPackage(IService.REGISTER, proto);
    }
    public void TransmitAuth(long seedCode)
    {
    	
		String pass = Util.encryptNew(instance.password, (int) seedCode );

        CommonVector authRq = CommonVector.newBuilder()
        		.addAString(instance.username)
        		.addAString(pass)
        		.addAString(Constants.VERSION)
        		.addAString(Constants.PLATFORM)
        		.addAString(Constants.OS_TYPE)
        		.addAString(Constants.CP_CODE)
        		.addAString(Constants.DEVICE_ID)//imei            
        		.addAString(Constants.CLIENT_IP)
        		.addAString(Constants.CLIENT_MCC).build();

        sendPackage(IService.LOGIN, authRq);
    }

    public void LoginBlockchain(String accessToken, String refreshToken)
    {
        //String pass = ICrypto.EncryptNewPwd(GameData.main.loginPassword, "" + seedCode);

        CommonVector authRq =  CommonVector.newBuilder()
        //Debug.Log(GamePrefs.LastLang+"LOGWWEB");
        .addALong(1)
        // authRq.aLong.Add(LangHandler.lastType);
        .addAString(accessToken) // asset token
        .addAString(refreshToken) // refresh token
        .addAString(Constants.VERSION)
        .addAString(Constants.PLATFORM)
        .addAString(Constants.OS_TYPE)
        .addAString(Constants.CP_CODE)
        .addAString(Constants.DEVICE_ID)//imei            
        .addAString(Constants.CLIENT_IP)
        .addAString(Constants.CLIENT_MCC).build();

        sendPackage(IService.LOGIN_WEB, authRq);
//        Debug.Log("Login Web_______________at_____" + accessToken);
//        Debug.Log("Login Web________________rt____" + refreshToken);
    }

    public void AutoLogin(String username)
    {
        CommonVector authRq = CommonVector.newBuilder()
        // Debug.Log(GamePrefs.LastLang+"AUTOLOG");
        		.addALong(1)
        //authRq.aLong.Add(LangHandler.lastType);
        .addAString(username)
        .addAString(Constants.VERSION)
        .addAString(Constants.PLATFORM)
        .addAString(Constants.OS_TYPE)
        .addAString(Constants.CP_CODE)
        .addAString(Constants.DEVICE_ID)//imei            
        .addAString(Constants.CLIENT_IP)
        .addAString(Constants.CLIENT_MCC).build();
//        Debug.Log(Constants.VERSION);

        sendPackage(IService.AUTO_LOGIN, authRq);
    }

    public void LoginFacebook(String token, long type)
    {
        // 0 facebook, 1 google
        CommonVector authRq = CommonVector.newBuilder()
        // Debug.Log(GamePrefs.LastLang+"AUTOLOG");
        .addALong(type)
        .addALong(1)
        //authRq.aLong.Add(LangHandler.lastType);
        .addAString(token)
        .addAString(Constants.VERSION)
        .addAString(Constants.PLATFORM)
        .addAString(Constants.OS_TYPE)
        .addAString(Constants.CP_CODE)
        .addAString(Constants.DEVICE_ID)//imei            
        .addAString(Constants.CLIENT_IP)
        .addAString(Constants.CLIENT_MCC).build();

        sendPackage(IService.LOGIN_3RD, authRq);
    }
    public void SetLanguage(int typeLang)
    {
//        Debug.Log(typeLang);
        CommonVector commonVector = CommonVector.newBuilder()
        .addALong(typeLang).build();
        sendPackage(IService.SET_LANGUAGE, commonVector);
    }
    public void LoginNormal(String username, String password)
    {
        instance.username = username;
        instance.password = password;
        CommonVector cv = CommonVector.newBuilder()
                .addAString(username)
                .addAString(password)
                .build();
        sendPackage(IService.LOGIN, cv);
    }

    public void GetUserHeroCard() { sendPackage(IService.GET_USER_HERO_CARD); }
    public void GetUserBattleDeck() { sendPackage(IService.GET_USER_BATTLE_DECK); }
    public void SetUserBattleDeck() { sendPackage(IService.GET_USER_BATTLE_DECK); }
    public void DeleteUserDeck(long id)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong( id).build();
        sendPackage(IService.DELETE_USER_DECK,cv);
    }
    public void SetUserBattleDeck(ArrayList<Long> lstGod, ArrayList<Long> lstTrooper)
    {
        // [id, number]
        CommonVector cvTrooper = CommonVector.newBuilder()
        .addAllALong(lstTrooper).build();

        CommonVector cvGod = CommonVector.newBuilder()
        .addAllALong(lstGod).build();

        ListCommonVector lcv = ListCommonVector.newBuilder()
        .addAVector(cvTrooper)
        .addAVector(cvGod).build();
        
        sendPackage(IService.SET_USER_BATTLE_DECK, lcv);
    }

    public void SetUserBattleDeck(long deckId)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong(deckId).build();
       
//        Debug.Log(mode);
        sendPackage(IService.SET_USER_BATTLE_DECK, cv);
    }

    public void GetUserDeck()
    {
        sendPackage(IService.GET_USER_DECK);
    }

    public void SetUserCustomDeck(ArrayList<Long> lstGod, ArrayList<Long> lstTrooper, String deckName,boolean isEven)
    {
        // [id, number]
        long even = (isEven? 1 : 0);
        CommonVector cvName = CommonVector.newBuilder()
        .addAString(deckName)
        .addALong(even).build();
        CommonVector cvTrooper = CommonVector.newBuilder()
        .addAllALong(lstTrooper).build();
        CommonVector cvGod = CommonVector.newBuilder()
        .addAllALong(lstGod).build();

        ListCommonVector lcv = ListCommonVector.newBuilder()
        .addAVector(cvName)
        .addAVector(cvGod)
        .addAVector(cvTrooper).build();

        sendPackage(IService.SET_USER_DECK, lcv);
    }
    public void UpdateUserDeck(ArrayList<Long> lstGod, ArrayList<Long> lstTrooper, String deckName,long deckId)
    {
        // [id, number]
        CommonVector cv = CommonVector.newBuilder()
        .addAString(deckName)
        .addALong(deckId).build();
        
        CommonVector cvTrooper = CommonVector.newBuilder()
        .addAllALong(lstTrooper).build();

        CommonVector cvGod = CommonVector.newBuilder()
        .addAllALong(lstGod).build();

        ListCommonVector lcv = ListCommonVector.newBuilder()
        .addAVector(cv)
        .addAVector(cvGod)
        .addAVector(cvTrooper).build();

        sendPackage(IService.UPDATE_USER_DECK, lcv);
    }
    public void GetLeaderboard()
    {
        sendPackage(IService.GET_LEADER_BOARD);
    }

    public void GetUserEventInfo(CommonVector cv)
    {
        sendPackage(IService.GET_USER_EVENT_INFO, cv);
    }

    public void GetEvent()
    {
        sendPackage(IService.GET_EVENT);
    }
    public void GetRank()
    {
        sendPackage(IService.GET_RANK);
    }
    public void GetProfile()
    {
        sendPackage(IService.GET_PROFILE);
    }

    public void GetUserDeckDetail(long id)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong(id).build();
        sendPackage(IService.GET_USER_DECK_DETAIL,cv);
    }

    public void GameBattleLeave()
    {
        sendPackage(IService.GAME_BATTLE_LEAVE);
    }

    public void GameBattleJoin(String mode)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addAString(mode).build();
//        Debug.Log("Event or rank: " + cv.aString[0]);
        sendPackage(IService.GAME_BATTLE_AUTO_JOIN, cv);
    }

    public void GameMulligan(CommonVector commonVector)
    {
        sendPackage(IService.GAME_MULLIGAN, commonVector);
    }

    public void GameFirstGodSummon(CommonVector commonVector)
    {
        sendPackage(IService.GAME_FIRST_GOD_SUMMON, commonVector);
    }

    public void GameMoveGodSummon(CommonVector commonVector)
    {
        sendPackage(IService.GAME_MOVE_GOD_SUMMON, commonVector);
    }

    public void GameStartupConfirm()
    {
        sendPackage(IService.GAME_STARTUP_CONFIRM);
    }


    public void GameMoveCardInbattle(CommonVector commonVector)
    {
        sendPackage(IService.GAME_MOVE_CARD_IN_BATTLE, commonVector);
    }

    public void GameSummonCardInBatttle(CommonVector commonVector)
    {
        sendPackage(IService.GAME_SUMMON_CARD_IN_BATTLE, commonVector);
    }

    public void GameConfirmStartBattle()
    {
        sendPackage(IService.GAME_CONFIRM_STARTBATTLE);
    }

    public void GameChooseWayRequest(CommonVector commonVector)
    {
        sendPackage(IService.GAME_CHOOSE_WAY_REQUEST, commonVector);
    }

    public void GameSimulateConfirm(CommonVector commonVector)
    {
        sendPackage(IService.GAME_SIMULATE_CONFIRM, commonVector);
    }

    public void GameActiveSkill(CommonVector commonVector)
    {
        sendPackage(IService.GAME_ACTIVE_SKILL, commonVector);
    }

    //-----------------------method received
    public void GetCCU() { sendPackage(63744); }
    public void GetUserPack()
    {
        sendPackage(IService.GET_USER_PACKS);
    }
    public void GetItems(long type)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong(type).build();
        sendPackage(IService.GET_ITEMS, cv);
    }
    public void GetItemDetail(long itemID )
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong(itemID).build();
        sendPackage(IService.GET_ITEM_DETAIL,cv);
    }
    public void BuyItem(long idPackage)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong(idPackage).build();
        
        sendPackage(IService.BUY_ITEM, cv);
    }
    public void OpenUserPack(long itemId)
    {
        CommonVector cv = CommonVector.newBuilder()
        .addALong(itemId).build();
        sendPackage(IService.OPEN_CHEST, cv);
    }
    public void GetUserCurrency()
    {
        sendPackage(IService.GET_BALANCE);
    }
	
	/****************************************************** API ***************************************************************/

	/********************************************************************/


	private void sendPackage(int serviceId) {
		bem.net.K2Packet pkt = new bem.net.K2Packet(serviceId);
		outputThread.queueMessage(pkt);
	}

	private void sendPackage(int serviceId, GeneratedMessageV3 request) {
		try {
			bem.net.K2Packet pkt = new bem.net.K2Packet(serviceId, request.toByteArray());
			outputThread.queueMessage(pkt);
			//
			Logger.debug("----------- Queue message ------------");
			Logger.debug(request.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void connect() throws IOException {
		Logger.debug("--connecting....");
		socket.open();
		Logger.debug("--connected");
		// start in/out thread
		inputThread = new InputThread(instance,socket);
		outputThread = new OutputThread(socket);
		// fireEvent(KEvent.CONNECTION);
	}

	public void disConnect() throws Exception {
		try {
			socket.close();
			inputThread.notifyAll();
			inputThread.quit = true;
			outputThread.notifyAll();
			outputThread.quit = true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public void addEventListener(IKEventListener listener) {
		if (inputThread != null)
			inputThread.addEventListener(listener);
	}

	public void removeEventListener(IKEventListener listener) {
		if (inputThread != null)
			inputThread.removeEventListener(listener);
	}

		
}
