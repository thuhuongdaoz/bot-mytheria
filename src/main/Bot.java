package main;

import bem.Context;
import bem.KException;
import bem.Logger;
import bem.event.IKEventListener;
import bem.event.IService;
import bem.event.KEvent;
import big2.screen.game.battle.Mytheria;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.BattlePlayer;
import data.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import protocol.pb.Pbmethod;
import protocol.pb.Pbmethod.CommonVector;
import protocol.pb.Pbmethod.ListCommonVector;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Bot implements IKEventListener {
    String userName, password, mode;
    Context instance;
    CommonEvent commonEvent;


    public List<Long> lstDeckId = Arrays.asList(1l,2l,3l,4l);


    public static String TYPE_CREATE_NEW = "create";
    public static String TYPE_JOIN = "join";

//	public Bot(String username, String pass, String mode) {
//		new Bot(username, pass, mode);
//	}

    public Bot(String username, String pass, String mode
//            , List<Long> lstDeckId
    ) {
        if (!username.equals("")) {
            this.userName = username;
            this.password = pass;
            this.mode = mode;
//            this.lstDeckId = Arrays.asList(1l,2l,3l,4l);
            this.instance = new Context();
            this.commonEvent = new CommonEvent(instance);



            new Thread() {
                public void run() {
                    if (!instance.connected) {
                        try {
                            instance.connect();
                            Thread.sleep(500);
                            instance.session2.addEventListener(Bot.this);
//							Context.instance.session2.getInitConfig(Configuration.CP, Configuration.VERSION);
                            Thread.sleep(500);
                            instance.session2.LoginNormal(userName, password);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        try {
                            Logger.error("disconnect");
                            ;
                            instance.session2.addEventListener(Bot.this);

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                }
            }.start();
        }
    }
    public void reJoinBattle(){
//        Random random = new Random();
//        int index = random.nextInt(4);
//        instance.session2.SetUserBattleDeck(lstDeckId.get(index));
        instance.session2.GameBattleJoin(mode);

    }

    /**
     * @param args
     */

    public static final String EXIT_COMMAND = "exit";


public static void main(String[] args) throws IOException, ParseException, InterruptedException {
    Database.ParseAll();
    JSONParser parser = new JSONParser();
    JSONObject jBotConfig = (JSONObject) parser.parse(new FileReader("bot.json"));
    JSONArray jBotArr = (JSONArray) jBotConfig.get("bot");
    //sv dev
//        for (int i = 0; i < jBotArr.size(); i += 25){
//        JSONObject jBot = (JSONObject) jBotArr.get(i);
//        String username = (String) jBot.get("username");
//        String pass = "towardthefuture";
//        String mode = (String) jBot.get("mode");
//        Bot b = new Bot(username, pass, mode);
//    }
    //sv test + pro
    for (int i = 0; i < jBotArr.size(); i++){
        JSONObject jBot = (JSONObject) jBotArr.get(i);
        String username = (String) jBot.get("username");
        String pass = "towardthefuture";
        String mode = (String) jBot.get("mode");
        Bot b = new Bot(username, pass, mode);
    }




    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Enter some text, or '" + EXIT_COMMAND + "' to quit");
    while (true) {

        System.out.print("> ");
        String input;
        try {
            input = br.readLine();
            System.out.println(input);

            if (input.length() == EXIT_COMMAND.length() && input.toLowerCase().equals(EXIT_COMMAND)) {
                System.out.println("Exiting.");
                return;
            }

            System.out.println("...response goes here...");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
    @Override
    public boolean dispatch(KEvent event) throws KException {


        switch (event.getType()) {

            case IService.LOGIN_NORMAL:
//			Logger.debug("Login OK fired");
//			LoginResponse loginRps = (LoginResponse) event.getObject();
//			// User
//			User userProto = loginRps.getUser();h
//			Context.instance.balance = userProto.getBalance();
//			Context.instance.type = type;
//			Context.instance.gender = userProto.getGender();
//			Context.instance.vDisableGame = loginRps.getLstDisableGame();
//			// Configuration.hasFriendNeedProcess =
//			// loginRps.getHasRequestFriend();
//			// Configuration.hasUnreadMessage = loginRps.getHasMessage();
//			Context.instance.username = userProto.getUsername();
//			Context.instance.guildIcon = userProto.getGuildIcon();
//			Context.instance.guildName = userProto.getGuildName();
//
//			// join game
                System.out.println("Đăng nhập thành công :" + userName);
//			Context.instance.currentGameCode = game;

//			Logger.debug("Balance =" + userProto.getBalance());
//			// int max1 = 5000;
//			// int min1 = 1000;
//			// Random r1 = new Random();
//			//
//			// int id = r1.nextInt(max1 - min1 + 1) + min1;
//
//			// Context.instance.session2.createTable(1000 + new
//			// Random().nextInt(10) * 1000, 4, "", game);
//
//			if (type.equals(TYPE_CREATE_NEW)) {
//				
//				if (blind.equals("random")){
//					Random r1 = new Random();
//					int id = 1 + r1.nextInt(5);
//					Context.instance.session2.createTable(1000 * id, 4, "", game);
//				} else {
//					int b = -1;
//					try {
//						b = Integer.valueOf(blind);
//					} catch (Exception e){
//						
//					}
//					
//					if (b != -1)
//						Context.instance.session2.createTable(b, 4, "", game);
//					else {
//						Random r1 = new Random();
//						int id = 1 + r1.nextInt(5);
//						Context.instance.session2.createTable(1000 * id, 4, "", game);
//					}
//				}
//			} else {
//				// Context.instance.session2.RequestTablePartByPart(game);
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Context.instance.session2.quickTabke(game);
//			}

//                Random random = new Random();
//                int index = random.nextInt(4);
//                instance.session2.SetUserBattleDeck(lstDeckId.get(index));

                instance.session2.GameBattleJoin(mode);
                break;

            case IService.SET_USER_BATTLE_DECK: {
//                Object[] args = event.getArgs();
//                CommonVector commonVector = (CommonVector) args[0];
//                if (commonVector.getALong(0) == 0) {
//                    System.out.println("deck không hợp lệ");
//                } else {
//                    instance.session2.GameBattleJoin(mode);
//                }

                break;
            }
            case IService.GAME_BATTLE_JOIN:
                try {
                    System.out.println("table join");
                    System.out.println("vào bàn");
                    Object[] args = event.getArgs();
                    ListCommonVector listCommonVector = (ListCommonVector) args[0];
                    int num = listCommonVector.getAVectorCount();

                    for (int i = 0; i < num; i ++) {
                        BattlePlayer player = new BattlePlayer();
                        player.position = listCommonVector.getAVector(i).getALong(0);
                        player.id = listCommonVector.getAVector(i).getALong(1);
                        player.username = listCommonVector.getAVector(i).getAString(0);
                        player.screenname = listCommonVector.getAVector(i).getAString(1);
                        instance.mLstBattlePlayer.add(player);
                    }
//                    commonEvent.dispatch(event);
//                    if (instance.mLstBattlePlayer.size() == 2) {
//                        instance.session2.removeEventListener(this);
//                        Mytheria screen = new Mytheria(instance, this);
//                        instance.session2.addEventListener(screen);
//                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                break;

            case IService.GAME_START:
                Mytheria screen = new Mytheria(instance, this);
                instance.session2.removeEventListener(this);
                instance.session2.addEventListener(screen);
                break;
            case IService.GAME_BATTLE_LEAVE:
//			Context.instance.session2.logout();
                System.out.println("table leave");
                break;
            case IService.GAME_RESUME:
            {
                Object[] args = event.getArgs();

                ListCommonVector lcv = (ListCommonVector) args[0];
                ResumeGame(lcv);
                break;
            }
            case KEvent.LOGOUT_OK:
                System.out.println("logout ok");
                try {
                    Thread.sleep(10000);
//				Context.instance.session2.login(userName, password);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }

        return false;
    }
    private void ResumeGame(ListCommonVector lcv)
    {


//        for (CommonVector cv : lcv.getAVectorList())
//        {
////            LogWriterHandle.WriteLog(string.Join(",", cv.aLong));
////            LogWriterHandle.WriteLog(string.Join(",", cv.aString));
//        }

        instance.isResume = true;
        instance.resumeData = lcv;

//        GamePrefs.isLoggedIn = true;
//        Game.main.socket.GetUserHeroCard();
//        Game.main.socket.GetLeaderboard();

        Mytheria screen = new Mytheria(instance,this);
        instance.session2.removeEventListener(this);
        instance.session2.addEventListener(screen);
//        Game.main.LoadScene("BattleScene", delay: 2f, curtain: true);


    }



}

// java -jar bot.jar h1 111111 phom & java -jar bot.jar h2 111111 phom & java
// -jar bot.jar h3 111111 phom & java -jar bot.jar h4 111111 phom &java -jar
// bot.jar h5 111111 phom & java -jar bot.jar h6 111111 tienlenmn & java -jar
// bot.jar h7 111111 tienlenmn & java -jar bot.jar h8 111111 tienlenmn & java
// -jar bot.jar h9 111111 tienlenmn & java -jar bot.jar h10 111111 tienlenmn
