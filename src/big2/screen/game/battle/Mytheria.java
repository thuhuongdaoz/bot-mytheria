package big2.screen.game.battle;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import data.*;
import main.Bot;
import net.k2tek.gamb.BaseGambScreen;
import org.json.simple.JSONArray;
import protocol.pb.Pbmethod.Action;
import protocol.pb.Pbmethod.CommonVector;
import protocol.pb.Pbmethod.ListAction;
import protocol.pb.Pbmethod.ListCommonVector;
import bem.Context;
import bem.KException;
import bem.Logger;
import bem.event.KEvent;
import data.EnumTypes.BATTLE_STATE;
import data.EnumTypes.CardOwner;
import data.EnumTypes.SlotState;
import data.EnumTypes.SlotType;
import bem.event.IService;

import static data.Database.normalGroup;
import static data.Database.prioritySpellArr;

public class Mytheria extends BaseGambScreen {

    Bot bot;

    public static final int MAX_ROW = 2, MAX_COLUMN = 4;
    int TYPE_WHEN_SUMON = 0;
    int TYPE_WHEN_START_TURN = 1;
    public BATTLE_STATE battleState;

    private boolean isSurrender = false, onProcessData = false, IsYourTurn = false, isGameStarted = false;
    private int playerIndex, turnCount = 0, roundCount = 0;// turnCount = way, roundCount chưa sử dụng
    private long currentShard, currentMana, tmpCurrentMana, turnMana;
    public boolean isUsedUlti = false;
    ;
    ArrayList<BattlePlayer> mLstBattlePlayer = new ArrayList<BattlePlayer>();


    public ArrayList<CardSlot> playerSlotContainer = new ArrayList<>();
    public ArrayList<CardSlot> enemySlotContainer = new ArrayList<>();


    public ArrayList<BoardCard> lstCardInBattle = new ArrayList<BoardCard>();
    ArrayList<Action> lstSkillQueue = new ArrayList<Action>();

    ArrayList<DBHero> lstHeroPlayer = new ArrayList<DBHero>();
    ArrayList<Long> lstHeroBattleID = new ArrayList<Long>();
    ArrayList<Long> lstHeroFrame = new ArrayList<Long>();
    ArrayList<DBHero> lstGodPlayer = new ArrayList<DBHero>();
    ArrayList<Long> lstGodPlayerBattleID = new ArrayList<Long>();
    ArrayList<Long> lstGodPlayerFrame = new ArrayList<Long>();
    ArrayList<DBHero> lstGodEnemy = new ArrayList<DBHero>();
    ArrayList<Long> lstGodEnemyBattleID = new ArrayList<Long>();
    ArrayList<Long> lstGodEnemyFrame = new ArrayList<Long>();

    //test
    public HandDeckLayout[] Decks = new HandDeckLayout[2];
    public GodCardHandler godCardHandler = new GodCardHandler();
    public List<GodCardUI> playerGodDeck = new ArrayList<>();
    public List<GodCardUI> enemyGodDeck = new ArrayList<>();

    public ArrayList<LaneController> lstLaneInBattle;
    public ArrayList<TowerController> lstTowerInBattle = new ArrayList<>();
    boolean success = false;


    public Random random = new Random();

    public Mytheria(Context context, Bot bot) {
        super(context);
        this.bot = bot;

        InitPlayer();
    }


    private void InitPlayer() {
        Logger.log("BATTLE SCENE", "Init player");
        onProcessData = true;
        for (int pos = 0; pos < 2; pos++) {
            Decks[pos] = new HandDeckLayout();
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 4; j++) {
                    CardSlot slot = new CardSlot(i, j);
                    if (pos == 0) playerSlotContainer.add(slot);
                    else enemySlotContainer.add(slot);
                }


            for (int j = 0; j < 3; j++) {
                TowerController towerController = new TowerController();
                towerController.Start(pos, j);
                lstTowerInBattle.add(towerController);
            }
        }

//	        if (!GameData.main.isResume)
//	        {
        if (!instance.isResume) {
            mLstBattlePlayer = instance.mLstBattlePlayer;
            CalculateClientPosstion();

        } else if (instance.resumeData != null) {

//            LogWriterHandle.WriteLog("INIT RESUME GAME=" + GameData.main.resumeData.aVector.Count);
            //0,17,1,0,0,1,74,0,0,0
            ListCommonVector lcv = instance.resumeData;
            if (lcv.getAVectorCount() == 7) {
                CommonVector cv0 = lcv.getAVector(0);
//                LogWriterHandle.WriteLog("PLAYER_STATUS = " + string.Join(",", cv0.aLong) + " String: " + string.Join(",", cv0.aString));
                int BLOCK = 11, BLOCK_STR = 2;
                int num = (cv0.getALongCount() - 2) / BLOCK;
                mLstBattlePlayer = new ArrayList<>();

                for (int i = 0; i < num; i++) {
                    BattlePlayer player = new BattlePlayer();
                    player.username = cv0.getAString(i * BLOCK_STR);
                    player.screenname = cv0.getAString(i * BLOCK_STR + 1);
                    player.position = cv0.getALong(i * BLOCK);
                    player.id = cv0.getALong(i * BLOCK + 1);
                    player.mana = cv0.getALong(i * BLOCK + 4);
                    player.freedomShard = cv0.getALong(i * BLOCK + 5);

//                    if (cv0.getALong(i * BLOCK + 2) == 1)
//                    {
//                        GameData.main.profile = new UserModel();
//                        GameData.main.profile.SetData(player.id, player.username, player.screenname);
//                    }
                    mLstBattlePlayer.add(player);
                    onUpdateMana(player.username, player.mana);
                    onUpdateShard(player.username, player.freedomShard);
                    if (instance.username.equals(player.username)) {
//                        GameData.main.userProgressionState = player.state;
//                        if (GameData.main.userProgressionState < 15 && ProgressionController.instance == null)
//                        {
//
//                            GameData.main.isResumeOnProgress = true;
//                            GameObject go = Instantiate(progressionPref);
//                        }
                        int index = 0;
                        List<TowerController> pTower = lstTowerInBattle.stream().filter(x -> x.pos == 0).collect(Collectors.toList());
                        if (pTower != null) {
                            for (TowerController x : pTower){
                                x.UpdateHealth(cv0.getALong(i * BLOCK + 6 + index));
//                                if (cv0.getALong(i * BLOCK + 6 + index) <= 0) {
//                                    onTowerDestroyed ?.Invoke(x.pos, x.id, true);
//                                }
                                index++;
                            }
                        }
                        isUsedUlti = cv0.getALong(i * BLOCK + 9) == 1;
//                        ultiID = cv0.aLong[i * BLOCK + 10];
                        //dung ulti chua , hero id đã dùng nếu có
                    } else {
                        int index = 0;
                        List<TowerController> eTower = lstTowerInBattle.stream().filter(x -> x.pos == 1).collect(Collectors.toList());
                        if (eTower != null) {
                            for (TowerController x : eTower){
                                x.UpdateHealth(cv0.getALong(i * BLOCK + 6 + index));
//                                if (cv0.getALong(i * BLOCK + 6 + index) <= 0) {
//                                    onTowerDestroyed ?.Invoke(x.pos, x.id, true);
//                                }
                                index++;
                            }
                        }
                    }
                }

//                currentAvailableRegion = cv0.aLong[cv0.aLong.Count - 2];
                //new : Huong tra ve them huong danh khi resume
                turnCount = (int) cv0.getALong(cv0.getALongCount() - 1);
//                turnArrow[0].SetActive(turnCount % 2 == 0);
//                turnArrow[1].SetActive(turnCount % 2 != 0);
                CalculateClientPosstion();
//                for(BattlePlayer player : mLstBattlePlayer)
//                {
//                    onInitPlayer ?.Invoke(player.clientPostion == POS_6h, player.screenname);
//                }
                //----set list card

                IsYourTurn = true;

                CommonVector cv3 = lcv.getAVector(3);
//                LogWriterHandle.WriteLog("LST_CARD_ON_HAND: " + string.Join(",", cv3.aLong));
                long playerIndex = cv3.getALong(0);
                long numCard = cv3.getALong(1);
                int BLOCK_HERO = 5;
                long numCardEnemy = cv3.getALong(cv3.getALongCount() - 1);
//                List<long> manaList = new List<long>();
                //======== CARD = 1,15,45,40,0,41,35,0,46,41,0,39,32,0,34,24,0,5
                for (int i = 0; i < numCard; i++) {
                    AddListCard(lstHeroPlayer, lstHeroBattleID, lstHeroFrame, cv3.getALong(2 + i * BLOCK_HERO + 1), cv3.getALong(2 + i * BLOCK_HERO), cv3.getALong(2 + i * BLOCK_HERO + 2),0,0,0);
//                    manaList.Add(cv3.aLong[2 + i * BLOCK_HERO + 4]);
                }
                DrawDeckStart(0, lstHeroPlayer, lstHeroBattleID, lstHeroFrame
//                        , manaList
                );

//                List<DBHero> lstHero = new List<DBHero>();
//                List<long> lstID = new List<long>();
//
//                for (int i = 1; i < numCardEnemy; i++) {
//                    DBHero hero = new DBHero();
//                    hero.id = -1;
//                    lstHero.Add(hero);
//                    lstID.Add(-1);
//                }
//                DrawDeckStart(1, lstHero, lstID, lstHeroFrame);
                //Lst Card Dead
                lstGodPlayer = new ArrayList<>();
                lstGodEnemy = new ArrayList<>();
                CommonVector cv4 = lcv.getAVector(4);
                //0,1,1,22,1,5,23,1,24,1,25,1,26,2,27,2
                //sua frame
                int start = 0;
                int BLOCK_GOD = 3;
                long playerIndex1 = cv4.getALong(0);
                boolean isMe = IsMeByServerPos(playerIndex1);
                long numCardPlayer1 = cv4.getALong(1);
                for (int i = 0; i < numCardPlayer1; i++) {
                    if (isMe)
                        AddListCard(lstGodPlayer, lstGodPlayerBattleID, lstGodPlayerFrame, cv4.getALong(2 + i * BLOCK_GOD + 1), cv4.getALong(2 + i * BLOCK_GOD), cv4.getALong(2 + i * BLOCK_GOD + 2),0,0,0);
                    else
                        AddListCard(lstGodEnemy, lstGodEnemyBattleID, lstGodEnemyFrame, cv4.getALong(2 + i * BLOCK_GOD + 1), cv4.getALong(2 + i * BLOCK_GOD), cv4.getALong(2 + i * BLOCK_GOD + 2),0,0,0);
                }

                start = 2 + (int) numCardPlayer1 * BLOCK_GOD;

                long playerIndex2 = cv4.getALong(start);
                isMe = (IsMeByServerPos(playerIndex2));
                long numCardPlayer2 = cv4.getALong(start + 1);
                for (int i = 0; i < numCardPlayer2; i++) {
                    if (isMe) {
                        AddListCard(lstGodPlayer, lstGodPlayerBattleID, lstGodPlayerFrame, cv4.getALong(start + 2 + i * BLOCK_GOD + 1), cv4.getALong(start + 2 + i * BLOCK_GOD), cv4.getALong(start + 2 + i * BLOCK_GOD + 2),0,0,0);
                    } else {
                        AddListCard(lstGodEnemy, lstGodEnemyBattleID, lstGodEnemyFrame, cv4.getALong(start + 2 + i * BLOCK_GOD + 1), cv4.getALong(start + 2 + i * BLOCK_GOD), cv4.getALong(start + 2 + i * BLOCK_GOD + 2),0,0,0);
                    }
                }
                InitGodUI(lstGodPlayerBattleID, lstGodPlayer, lstGodPlayerFrame, lstGodEnemyBattleID, lstGodEnemy, lstGodEnemyFrame);
//                onGameDealCard ?.
//                Invoke(lstGodPlayerBattleID, lstGodPlayer, lstGodPlayerFrame, lstGodEnemyBattleID, lstGodEnemy, lstGodEnemyFrame);




//                CommonVector cv5 = lcv.getAVector(5);
////                LogWriterHandle.WriteLog("LST_CARD_DEAD: " + string.Join(",", cv5.aLong));
//                if (cv5.getALongCount() > 0) {
//                    int start5 = 0;
//                    int BLOCK5 = 15;
//                    long playerIndex51 = cv5.getALong(0);
////                    LogWriterHandle.WriteLog("IS ME = " + IsMeByServerPos(playerIndex51) + " " + playerIndex51 + " " + (IsMeByServerPos(playerIndex51) ? 1 : 0));
//                    long numCardPlayer51 = cv5.getALong(1);
//                    boolean isMe5 = IsMeByServerPos(playerIndex51);
//                    for (int i = 0; i < numCardPlayer51; i++) {
//                        DBHero hero = Database.GetHero(cv5.aLong[2 + i * BLOCK5 + 1]);
//                        if (hero != null) {
//                            if (hero.type == DBHero.TYPE_GOD) {
//                                GameData.main.lstGodDead.Add(cv5.aLong[2 + i * BLOCK5]);
//                                //if (isMe5)
//                                //    AddListCard(lstGodPlayer, lstGodPlayerBattleID, cv5.aLong[2 + i * BLOCK5 + 1], cv5.aLong[2 + i * BLOCK5]);
//                                //else
//                                //    AddListCard(lstGodEnemy, lstGodEnemyBattleID, cv5.aLong[2 + i * BLOCK5 + 1], cv5.aLong[2 + i * BLOCK5]);
//                            }
//                        }
//
//                    }
//                    start5 = 2 + (int) numCardPlayer51 * BLOCK5;
//                    long playerIndex52 = cv5.aLong[start5];
//                    isMe5 = (IsMeByServerPos(playerIndex52));
//                    long numCardPlayer52 = cv5.aLong[start5 + 1];
//
//                    for (int i = 0; i < numCardPlayer52; i++) {
//                        DBHero hero = Database.GetHero(cv5.aLong[2 + i * BLOCK5 + 1]);
//                        if (hero != null) {
//                            if (hero.type == DBHero.TYPE_GOD) {
//                                GameData.main.lstGodDead.Add(cv5.aLong[start5 + 2 + i * BLOCK5]);
//                                //if (isMe5)
//                                //{
//                                //    AddListCard(lstGodPlayer, lstGodPlayerBattleID, cv5.aLong[start5 + 2 + i * BLOCK5 + 1], cv5.aLong[start5 + 2 + i * BLOCK5]);
//                                //}
//                                //else
//                                //{
//                                //    AddListCard(lstGodEnemy, lstGodEnemyBattleID, cv5.aLong[start5 + 2 + i * BLOCK5 + 1], cv5.aLong[start5 + 2 + i * BLOCK5]);
//                                //}
//                            }
//                        }
//
//                    }
//
//                }
//                //----set list god
//                CommonVector cv6 = lcv.aVector[6];
//                LogWriterHandle.WriteLog("LST_CARD_GOD_ORIGIN: " + string.Join(",", cv6.aLong));
//                //0,1,1,22,1,5,23,1,24,1,25,1,26,2,27,2
//                //sua frame
//                int start = 0;
//                int BLOCK_GOD = 3;
//                long playerIndex1 = cv6.aLong[0];
//                bool isMe = IsMeByServerPos(playerIndex1);
//                long numCardPlayer1 = cv6.aLong[1];
//                for (int i = 0; i < numCardPlayer1; i++) {
//                    if (isMe)
//                        AddListCard(lstGodPlayer, lstGodPlayerBattleID, lstGodPlayerFrame, cv6.aLong[2 + i * BLOCK_GOD + 1], cv6.aLong[2 + i * BLOCK_GOD], cv6.aLong[2 + i * BLOCK_GOD + 2]);
//                    else
//                        AddListCard(lstGodEnemy, lstGodEnemyBattleID, lstGodEnemyFrame, cv6.aLong[2 + i * BLOCK_GOD + 1], cv6.aLong[2 + i * BLOCK_GOD], cv6.aLong[2 + i * BLOCK_GOD + 2]);
//                }
//
//                start = 2 + (int) numCardPlayer1 * BLOCK_GOD;
//
//                long playerIndex2 = cv6.aLong[start];
//                isMe = (IsMeByServerPos(playerIndex2));
//                long numCardPlayer2 = cv6.aLong[start + 1];
//                for (int i = 0; i < numCardPlayer2; i++) {
//                    if (isMe) {
//                        AddListCard(lstGodPlayer, lstGodPlayerBattleID, lstGodPlayerFrame, cv6.aLong[start + 2 + i * BLOCK_GOD + 1], cv6.aLong[start + 2 + i * BLOCK_GOD], cv6.aLong[start + 2 + i * BLOCK_GOD + 2]);
//                    } else {
//                        AddListCard(lstGodEnemy, lstGodEnemyBattleID, lstGodEnemyFrame, cv6.aLong[start + 2 + i * BLOCK_GOD + 1], cv6.aLong[start + 2 + i * BLOCK_GOD], cv6.aLong[start + 2 + i * BLOCK_GOD + 2]);
//                    }
//                }
//                onGameDealCard ?.
//                Invoke(lstGodPlayerBattleID, lstGodPlayer, lstGodPlayerFrame, lstGodEnemyBattleID, lstGodEnemy, lstGodEnemyFrame);

                //card on table
                //0,1,2,23,1,5,5,0,0,0,0,0,0,0,0,0,0,1
                //0,17,1,0,0,1,74,0,0,0
                CommonVector cv2 = lcv.getAVector(2);
                if (cv2.getALongCount() > 0) {
                    playerIndex1 = cv2.getALong(0);
//                    LogWriterHandle.WriteLog("IS ME = " + IsMeByServerPos(playerIndex1) + " " + playerIndex1 + " " + (IsMeByServerPos(playerIndex1) ? 1 : 0));
                    numCardPlayer1 = cv2.getALong(1);
                    int BLOCK_CARD_TABLE = 18;
                    for (int i = 0; i < numCardPlayer1; i++) {
                        //sua frame
                        PlaceCardInBattleOnResume(IsMeByServerPos(playerIndex1) ? 0 : 1,
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 1),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 2),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 3),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 4),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 5),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 6),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 7),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 8),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 9),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 10),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 11),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 12),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 13),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 14),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 15),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 16),
                                cv2.getALong(2 + i * BLOCK_CARD_TABLE + 17));
                    }
                    start = 2 + (int) numCardPlayer1 * BLOCK_CARD_TABLE;
                    if (start < cv2.getALongCount()){
                        playerIndex2 = cv2.getALong(start);
                        numCardPlayer2 = cv2.getALong(start + 1);
                        for (int i = 0; i < numCardPlayer2; i++) {
                            //sua frame
                            PlaceCardInBattleOnResume(IsMeByServerPos(playerIndex2) ? 0 : 1,
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 1),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 2),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 3),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 4),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 5),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 6),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 7),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 8),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 9),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 10),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 11),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 12),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 13),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 14),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 15),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 16),
                                    cv2.getALong(start + 2 + i * BLOCK_CARD_TABLE + 17));
                        }
                    }
                }

                final int TIME_ROUND_START = 1;// time counter defaut 60s
                final int TIME_FIRST_SUMMON = 2;// time counter defaut 30s
                final int TIME_CHOSING_WAY = 5;// time counter defaut 60s
                final int TIME_ROUND_COMBAT = 8;// time counter defaut 60s x 3
                final int TIME_ROUND_END = 10; //(not play)
                //table status
                CommonVector cv1 = lcv.getAVector(1);
                long currentPlayer = cv1.getALong(0);
                int timingResume = (int) cv1.getALong(1);
                long timeRemain = cv1.getALong(2);

                //Be ghep ho anh doan code nay
                long nextIndexPlayer = cv1.getALong(3);

                instance.isResume = false;
                switch (timingResume) {
                    case TIME_ROUND_START: {
                        if (!isGameStarted)
                            isGameStarted = true;
//                        StartCoroutine(GameStartBattleSimulation(GetUsernameFromServerPos(currentPlayer), GetTurnEffect()));
                        IsYourTurn = instance.username.equals(GetUsernameFromServerPos(currentPlayer));
//                        onUpdateMana ?.Invoke(0, turnMana, ManaState.Update, 0);
//                        onUpdateMana ?.Invoke(1, turnMana, ManaState.Update, 0);
//                        onResumeTime ?.
//                        Invoke(IsMeByServerPos(currentPlayer), !IsMeByServerPos(currentPlayer), timeRemain, 60);
                        if (nextIndexPlayer == -1) {
//                            onResumeRoundCount ?.Invoke(0);
                            roundCount = 1;
                        } else {
//                            onResumeRoundCount ?.Invoke(-1);
                            roundCount = 0;
                        }
//                        if (timeRemain > 5){
//                            instance.session2.GameConfirmStartBattle();
//                        }
                        break;

                    }
                    case TIME_FIRST_SUMMON: {
                        //cho phep user ra bai phep, mulligan bài, set button ok đúng trạng thái
//                        onResumeTime ?.Invoke(true, true, timeRemain, 30);
                        break;
                    }
//                    case TIME_CHOSING_WAY: {
//                        //chọn ngẫu nhiên đi
////                        onResumeTime ?.
////                        Invoke(IsMeByServerPos(currentPlayer), !IsMeByServerPos(currentPlayer), timeRemain, 60);
//                        StartChooseWay(IsMeByServerPos(currentPlayer));
//                        break;
//                    }
                    case TIME_ROUND_COMBAT: {
                        //ngồi chờ thôi, button hiện đúng trạng thái đang combat, còn lại k phải làm gì cả
//                        onGameBattleChangeTurn ?.Invoke(-1);
                        break;
                    }
                    case TIME_ROUND_END: {
                        break;
                    }
                }
            }


            //yield return new WaitForSeconds(3f);


        }

        onProcessData = false;

    }
    private void PlaceCardInBattleOnResume(int isMe, long battleID, long heroID, long frame, long atk, long hp, long hpMax, long cleave, long pierce, long breaker, long combo, long overrun, long shield, long godSlayer, long fragile, long precide, long shard, long row, long col)
    {
        switch (isMe)
        {
            case 0:
                CardSlot targetSlot = playerSlotContainer.stream().filter(x -> x.xPos == row && x.yPos == col).findAny().orElse(null);
                if (targetSlot != null)
                {
                    DBHero hero = Database.GetHero(heroID);
//                    StartCoroutine(

                    BoardCard card = CreateCard(battleID, heroID, frame,  targetSlot, CardOwner.Player,0,0,0);
                    card.UpdateHeroMatrix(atk, hp, hpMax, cleave, pierce, breaker, combo, overrun, shield, godSlayer, shard, fragile, precide);
//                    );
                }
                break;
            case 1:
                CardSlot targetSlotEnemy = enemySlotContainer.stream().filter(x -> x.xPos == row && x.yPos == col).findAny().orElse(null);
                if (targetSlotEnemy != null)
                {
                    DBHero hero = Database.GetHero(heroID);
//                    StartCoroutine(
                           BoardCard card =  CreateCard(battleID, heroID, frame, targetSlotEnemy, CardOwner.Enemy,0,0,0

                    );
                    card.UpdateHeroMatrix(atk, hp, hpMax, cleave, pierce, breaker, combo, overrun, shield, godSlayer, shard, fragile, precide);
//                    );
                }
                break;
        }
    }
    // Override

    public boolean dispatch(KEvent event) throws KException {
        if (super.dispatch(event))
            return true;
        try {
            Object[] objs;
            int eventType = event.getType();
            switch (eventType) {

                case IService.GAME_BATTLE_LEAVE: {
//                    if (isSurrender) {
//                        Object[] args = event.getArgs();
//                        CommonVector commonVector = (CommonVector) args[0];
////                        PlayerSurrender(commonVector);
//                    }
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    String userLeave = commonVector.getAString(0);
                    if (userLeave.equals(instance.username)){
                        instance.session2.removeEventListener(this);
                        instance.mLstBattlePlayer.clear();
                        instance.session2.addEventListener(bot);
                        bot.reJoinBattle();
                    }

                    break;
                }
                case IService.GAME_DEAL_CARDS: {
                    Object[] args = event.getArgs();
                    ListCommonVector listCommonVector = (ListCommonVector) args[0];
                    GameDealCards(listCommonVector);

                    break;
                }

                case IService.GAME_MULLIGAN: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    // GameCardMulligan(commonVector);
                    break;
                }

                case IService.GAME_FIRST_GOD_SUMMON: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    GameFirstGodSummon(commonVector);
                    break;
                }

                case IService.GAME_MOVE_GOD_SUMMON: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    GameMoveGodSumon(commonVector);
                    break;
                }

                case IService.GAME_STARTUP_CONFIRM: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    GameStartupConfirm(commonVector);
                    break;
                }

                case IService.GAME_STARTUP_END: {
                    Object[] args = event.getArgs();
                    ListCommonVector listCommonVector = (ListCommonVector) args[0];
                    GameStartupEnd(listCommonVector);
                    break;
                }

                case IService.GAME_START_BATTLE: {

                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameStartBattle(listAction);

                    break;
                }

                case IService.GAME_DELETE_CARDS: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    DeleteCardsOnHand(commonVector, true);

                    break;
                }

                case IService.GAME_GET_SHARD: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameGetShard(listAction);
                    break;
                }

                case IService.GAME_ADD_SHARD_HERO: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameAddShardHero(listAction);
                    break;
                }

                case IService.GAME_MOVE_CARD_IN_BATTLE: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameMoveCardInBattle(listAction);
                    break;
                }

                case IService.GAME_SUMMON_CARD_IN_BATTLE: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameSumonCardInBattle(listAction);
                    break;
                }

                case IService.GAME_CONFIRM_STARTBATTLE: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameConfirmStartBattle(listAction);
                    break;
                }

//                case IService.GAME_CHOOSE_WAY_REQUEST: {
//
//                    Object[] args = event.getArgs();
//                    CommonVector commonVector = (CommonVector) args[0];
//                    GameChooseWayRequest(commonVector);
//                    break;
//                }

                case IService.GAME_SIMULATE_BATTLE: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameSimulateBattle(listAction);
                    break;
                }

                case IService.GAME_SIMULATE_CONFIRM: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    GameSimulateConfirm(commonVector);
                    break;
                }

                case IService.GAME_BATTLE_ENDROUND: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];
                    GameBattleEndRound(listAction);
                    break;
                }

                case IService.GAME_BATTLE_ENDGAME: {
                    Object[] args = event.getArgs();
                    ListCommonVector lstCommonVector = (ListCommonVector) args[0];
                    GameBattleEndGame(lstCommonVector);
                    break;
                }

                case IService.GAME_SIMULATE_SKILLS:
                case IService.GAME_ACTIVE_SKILL: {
                    Object[] args = event.getArgs();
                    ListAction listAction = (ListAction) args[0];

                    for (int i = 0; i < listAction.getAActionCount(); i++) {
                        Action a = listAction.getAAction(i);
                        switch (a.getActionId()) {
                            case IService.GAME_STATUS_SKILL: {
                                CommonVector cv = CommonVector.parseFrom(a.getData());
                                if (cv.getALong(0) == 0) {

                                    SkillFailCondition();
                                } else {
                                    success = true;
                                    long battleId = cv.getALong(1);
                                    long skillId = cv.getALong(2);

                                    for (Card card : GetListPlayerCardInBattle()) {
                                        if (card.battleID == battleId && card.skill != null) {
                                            if (card.skill.id == skillId && card.skill.isUltiType) {
                                                isUsedUlti = true;
//                                                isUseUltimate?.Invoke(card.heroID);
//                                                Toast.Show(LangHandler.Get("81", "Ultimate Activated!"));
//                                                Transform target = IUtil.LoadPrefabRecycle("Prefabs/UltiVFX/", card.heroInfo.heroNumber.ToString(), ultiVfxContent);
//                                                target.position = Vector3.zero;
//                                                target.localScale = Vector3.one;
                                            } else if (card.skill.id == skillId && !card.skill.isUltiType) {
                                                card.countDoActiveSkill++;
//                                                onActiveSkillActive?.Invoke(skillId, battleId);
                                            }
                                        }
                                    }
                                }
                                break;
                            }

                            case IService.GAME_SIMULATE_SKILLS_ON_BATTLE:

                                ListAction listActionSkill = ListAction.parseFrom(a.getData());
//                                lstSkillQueue.addAll(listActionSkill.getAActionList());
                                for (int j = 0; j < listActionSkill.getAActionCount(); j++)
                                    SimulateSkillEffect(listActionSkill.getAAction(j));
                                break;

                        }

                    }
                    break;
                }

                case IService.GAME_UPDATE_HERO_MATRIC: {
                    Object[] args = event.getArgs();
                    CommonVector commonVector = (CommonVector) args[0];
                    UpdateHeroMatric(commonVector, false);
                    break;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    private void GameBattleEndGame(ListCommonVector lst) {
//        CommonVector cv1 = lst.getAVector(0);
//        if (cv1.getALong(0) == 1 || cv1.getALong(0) == -1) {
//            String user = "";
//            //UIManager.instance.OnSurrender(1);
//            if (instance.username.equals(cv1.getAString(0))) {
//                user = "Your Opponen ";
//            } else {
//                user = "You ";
//            }
//            switch ((int) cv1.getALong(1)) {
//                case 0:
//
//                    break;
//                case 1:
////	                    Toast.Show(user + "Has Surrendered");
//                    break;
//                case 2:
////	                    Toast.Show(user + "has no card to draw");
//                    break;
//                case 3:
////	                    Toast.Show(user + "has enough 12 zodiac on board");
//                    break;
//
//            }
//            //wait
//            onProcessData = true;
//
////	            fadedScreen.SetActive(true);
////	            fadedScreen.GetComponent<Image>().DOFade(1, 0.2f).onComplete += delegate
////	            {
////	                if (GameData.main.profile.username.Equals(cv1.aString[0]))
////	                {
////	                    victoryRenderTexture.SetActive(true);
////	                    victoryVideo.gameObject.SetActive(true);
////	                    //victoryVideo.Play();
////	                }
////	                else
////	                {
////	                    defeatedRenderTexture.SetActive(true);
////	                    defeatedVideo.gameObject.SetActive(true);
////	                    //defeatedVideo.Play();
////	                }
////	            };
//
////	            SoundHandler.main.PlaySFX("Victory", "sounds");
////            instance.session2.GameBattleLeave();
////	            yield return new WaitForSeconds(5);
////	            SoundHandler.main.Init("BackgroundMusicMain");
//            //if (lst.aVector.Count > 1 && GameData.main.profile.username.Equals(cv1.aString[0]))
//            //{
//            //    CommonVector cv2 = lst.aVector[1];
//            //    RewardModel model = new RewardModel();
//            //    model.goldNumber = cv2.aLong[0];
//            //    Transform trans = PoolManager.Pools["RewardPopup"].Spawn(rewardPopupPrefab);
//            //    trans.SetParent(Game.main.canvas.panelPopup);
//            //    trans.localScale = Vector3.one;
//            //    trans.localPosition = Vector3.zero;
//            //    trans.GetComponent<RectTransform>().sizeDelta = Vector2.zero;
//            //    trans.GetComponent<RewardScene>().InitData(model);
//            //    trans.GetComponent<RewardScene>().onRewardComplete += OnRewardComplete;
//            //}
//            //else
//            //{
//            //    yield return new WaitForSeconds(0.5f);
//            //    OnRewardComplete();
//            //}
////	            yield return new WaitForSeconds(0.5f);
////	            OnRewardComplete();
//
//        } else {
//            switch ((int) cv1.getALong(1)) {
//                case 0:
////	                    Toast.Show("Match Draw");
//                    break;
//                case 1:
//                    break;
//                case 2:
////	                    Toast.Show("Both of players have no card to draw");
//                    break;
//                case 3:
////	                    Toast.Show("Both of players have 12 zodiac on board");
//                    break;
//            }
////	            yield return new WaitForSeconds(3f);
//            onProcessData = true;
//
////	            fadedScreen.SetActive(true);
////	            fadedScreen.GetComponent<Image>().DOFade(1, 0.2f).onComplete += delegate
////	            {
////            instance.session2.GameBattleLeave();
////	                SoundHandler.main.Init("BackgroundMusi
////	                cMain");
////	            };
////	            GameData.main.isUsedUlti = false;
////	            yield return new WaitForSeconds(0.5f);
////	            OnRewardComplete();
//        }

        onProcessData = false;



    }

    private void GameBattleEndRound(ListAction listAction) {

        //wait
        onProcessData = true;

        for (int i = 0; i < lstCardInBattle.size(); i++)
            lstCardInBattle.get(i).SetTired(0);

        roundCount = 0;
        turnCount += 1;
//	        onGameBattleChangeTurn?.Invoke(-1);

        ArrayList<HandCard> cards = new ArrayList<>();
        for (int i = 0; i < Decks[0].GetListCard().size(); i++)
            if (Decks[0].GetListCard().get(i).isFleeting)
                cards.add(Decks[0].GetListCard().get(i));

        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i).battleID > 0)
                Decks[0].RemoveCard(cards.get(i), 0);

//	        if (card != null)
//	        {
//	            card.ToList().ForEach(x =>
//	            {
//	                if (x.battleID > 0)
//	                {
//	                    Debug.Log(x.battleID + x.gameObject.name);
//	                    Decks[0].RemoveCard(x);
//	                    PoolManager.Pools["Card"].Despawn(x.transform);
//	                }
//	            });
//	        }
        for (int i = 0; i < lstCardInBattle.size(); i++) {
            if (lstCardInBattle.get(i).isFragile) {
                lstCardInBattle.get(i).OnDeath();
//	                SoundHandler.main.PlaySFX("BrokenCard", "sounds");
                lstCardInBattle.remove(lstCardInBattle.get(i));
                i--;
            }
        }
        Decks[0].ReBuildDeck(0);
        try {

            for (int k = 0; k < listAction.getAActionCount(); k++) {
                Action a = listAction.getAAction(k);
                switch (a.getActionId()) {
                    case IService.GAME_SIMULATE_SKILLS_ON_BATTLE:
                        ListAction listActionSkill = ListAction.parseFrom(a.getData());
//                        lstSkillQueue.addAll(listActionSkill.getAActionList());
                        for (Action ac : listActionSkill.getAActionList())
                            SimulateSkillEffect(ac);

                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //wait 1
        onProcessData = false;
    }

    private void GameSimulateConfirm(CommonVector commonVector) {
        onProcessData = false;
    }


    private void GameSimulateBattle(ListAction listAction) {


        try {
            Thread.sleep(2000);
//	       wait 1

            onProcessData = true;

            for (int k = 0; k < listAction.getAActionCount(); k++) {
                Action a = listAction.getAAction(k);
                switch (a.getActionId()) {
                    case IService.GAME_BATTLE_ATTACK:
//                        //aLong: [    battleId, def battle Id, godSlayer, defender, count, atkType Count,       [type, def Count, [def battleID]]           ]
//                        //wait 0.5f
//                        CommonVector cv = CommonVector.parseFrom(a.getData());
////	                    WriteLogBattle("BATTLE_ATTACK: ", "", string.Join(",", cv.aLong));
//
//                        long totalCount = cv.getALong(4) + 5;
//                        //long totalCount = cv.aLong[2] + 3;
//                        for (int i = 0; i < cv.getALongCount(); i += (int) totalCount) {
//                            BoardCard atkCard = null;
//                            for (int j = 0; j < lstCardInBattle.size(); j++)
//                                if (lstCardInBattle.get(j).battleID == cv.getALong(i)) {
//                                    atkCard = lstCardInBattle.get(j);
//                                    break;
//                                }
//
//                            // atk tower
//                            if (cv.getALong(i + 1) < 0) {
//                                ArrayList<TowerController> lstTower = new ArrayList<>();
//                                for (int j = 0; j < lstTowerInBattle.size(); j++)
//                                    if (cv.getALong(i + 1) < -10 && lstTowerInBattle.get(j).pos == GetClientPosFromServerPos(1))
//                                        lstTower.add(lstTowerInBattle.get(j));
//                                    else if (cv.getALong(i + 1) >= -10 && lstTowerInBattle.get(j).pos == GetClientPosFromServerPos(0))
//                                        lstTower.add(lstTowerInBattle.get(j));
////	                            var lstTower = lstTowerInBattle.Where(t => cv.aLong[i + 1] < -10 ? t.pos == GetClientPosFromServerPos(1) : t.pos == GetClientPosFromServerPos(0));
//
//                                long id = cv.getALong(i + 1) < -10 ? (long) Math.abs(cv.getALong(i + 1)) - 11 : (long) Math.abs(cv.getALong(i + 1)) - 1;
//
//                                TowerController defTower = null;
//
//                                for (int j = 0; j < lstTower.size(); j++)
//                                    if (lstTower.get(j).id == id) {
//                                        defTower = lstTower.get(j);
//                                        break;
//                                    }
//
//                                if (atkCard != null && defTower != null) {
//                                    atkCard.OnAttackTower(defTower);
//                                    //wait 1
//                                }
//                            }
//                            // atk card
//                            else {
//                                BoardCard defCard = null;
//                                for (int j = 0; j < lstCardInBattle.size(); j++)
//                                    if (lstCardInBattle.get(j).battleID == cv.getALong(i + 1)) {
//                                        defCard = lstCardInBattle.get(j);
//                                        break;
//                                    }
//                                if (atkCard != null && defCard != null) {
//                                    atkCard.OnAttackCard(defCard);
//                                    //wait
//                                }
//                            }
//
//                            int count = i + 6;
//                            ArrayList<SkillEffect> effects = new ArrayList<SkillEffect>();
//                            if (cv.getALong(i + 2) > 0) {
//                                SkillEffect eff = new SkillEffect();
//                                eff.typeEffect = DBHero.KEYWORD_GODSLAYER;
//                                effects.add(eff);
//                            }
//                            if (cv.getALong(i + 3) > 0) {
//                                SkillEffect eff = new SkillEffect();
//                                eff.typeEffect = DBHero.KEYWORD_DEFENDER;
//                                effects.add(eff);
//                            }
//                            // them ca eff cho breaker va godSlayer
//
//                            while (count < i + cv.getALong(i + 4) + 5) {
//                                // loai effect
//                                long typeEffect = cv.getALong(count);
//                                long defCount = cv.getALong(count + 1);
//                                SkillEffect eff = new SkillEffect();
//                                eff.typeEffect = typeEffect;
//                                eff.defCount = defCount;
//                                for (int j = 0; j < defCount; j++) {
//
//                                    // effect here
//                                    //cac quan ảnh huong : bai hoac tru,
//                                    //switch case cho tung effect.  goi sang card, truyen vao loai effect và so luong bai kem theo id quan bi anh huong
//                                    ArrayList<TowerController> lstTower = new ArrayList<>();
//                                    if (cv.getALong(count + 2 + j) < 0) {
//                                        if (cv.getALong(i + 1) < -10)
//                                            for (int jj = 0; jj < lstTowerInBattle.size(); jj++) {
//                                                if (lstTowerInBattle.get(jj).pos == GetClientPosFromServerPos(1))
//                                                    lstTower.add(lstTowerInBattle.get(jj));
//                                            }
//                                        else
//                                            for (int jj = 0; jj < lstTowerInBattle.size(); jj++)
//                                                if (lstTowerInBattle.get(jj).pos == GetClientPosFromServerPos(0))
//                                                    lstTower.add(lstTowerInBattle.get(jj));
//
//                                        long id = cv.getALong(i + 1) < -10 ? (long) Math.abs(cv.getALong(i + 1)) - 11 : (long) Math.abs(cv.getALong(i + 1)) - 1;
//
//                                        for (int jj = 0; jj < lstTower.size(); jj++)
//                                            if (lstTower.get(jj).id == id) {
//                                                eff.lstTowerImpact.add(lstTower.get(jj));
//                                                break;
//                                            }
//                                    } else {
//                                        for (int jj = 0; jj < lstCardInBattle.size(); jj++)
//                                            if (lstCardInBattle.get(jj).battleID == cv.getALong(i + 1)) {
//                                                eff.lstCardImpact.add(lstCardInBattle.get(jj));
//                                                break;
//                                            }
//                                    }
//                                    if (j == defCount - 1) {
//                                        effects.add(eff);
//                                    }
//
//                                }
//                                count += (int) defCount + 2;
//
//                            }
//                            totalCount = cv.getALong(i + 4) + 5;
//
//                        }
                        break;

                    case IService.GAME_BATTLE_DEAL_DAMAGE:
                        CommonVector cv1 = CommonVector.parseFrom(a.getData());
//	                    WriteLogBattle("DEAL_DAMAGE: ", "", string.Join(",", cv1.aLong));
                        //wait
                        for (int i = 0; i < cv1.getALongCount(); i += 3) {
                            if (cv1.getALong(i) < 0) {
                                TowerDealDamage(cv1.getALong(i), cv1.getALong(i + 1), cv1.getALong(i + 2));
                            } else {
//                                ArrayList<BoardCard> lstCard = new ArrayList<>();
//                                for (int j = 0; j < lstCardInBattle.size(); j++)
//                                    if (lstCardInBattle.get(j).battleID == cv1.getALong(i))
//                                        lstCard.add(lstCardInBattle.get(j));
//                                for (int j = 0; j < lstCard.size(); j++)
//                                    lstCard.get(j).OnDamaged(cv1.getALong(i + 1), cv1.getALong(i + 2));
                                long battleId = cv1.getALong(i);
                                BoardCard card = lstCardInBattle.stream().filter(c -> c.battleID == battleId).findAny().orElse(null);
                                card.OnDamaged(cv1.getALong(i + 1), cv1.getALong(i + 2));

                            }
                        }
                        break;

                    case IService.GAME_BATTLE_HERO_DEAD:
                        //aLong:hp_remain1,hp_remain2,hp_remain3,[battleIdHero,deadcost, indexfountain]
                        //aString:username
                        ListCommonVector lstCv = ListCommonVector.parseFrom(a.getData());
                        //wait
                        for (int kk = 0; kk < lstCv.getAVectorCount(); kk++) {
                            CommonVector c = lstCv.getAVector(kk);
//	                        WriteLogBattle("HERO_DEAD: ", string.Join(",", c.aString), string.Join(",", c.aLong));

                            for (int i = 3; i < c.getALongCount(); i += 3) {
                                ArrayList<BoardCard> lstCard = new ArrayList<>();
                                for (int j = 0; j < lstCardInBattle.size(); j++)
                                    if (lstCardInBattle.get(j).battleID == c.getALong(i)) {
                                        lstCard.add(lstCardInBattle.get(j));
                                    }
                                for (BoardCard card : lstCard) {
                                    card.OnDeath();
                                    lstCardInBattle.remove(card);
                                }

                                // if no damage to tower, continue
                                if (c.getALong(i + 2) == -1) {
                                    continue;
                                } else {
                                    long towerID = 0;
                                    if (GetServerPostFromUsername(c.getAString(0)) == 0) {
                                        towerID = c.getALong(i + 2) * -1;
                                    } else {
                                        towerID = (c.getALong(i + 2) * -1) - 10;
                                    }
                                    TowerDealDamage(towerID, c.getALong(i + 1), c.getALong((int) c.getALong(i + 2) - 1));
                                }
                            }
                        }
                        break;

                    case IService.GAME_BATTLE_HERO_TIRED:
                        CommonVector cv2 = CommonVector.parseFrom(a.getData());
//	                    WriteLogBattle("HERO_TIRED: ", "", string.Join(",", cv2.aLong));

//	                    wait
                        for (int j = 0; j < lstCardInBattle.size(); j++)
                            for (int jj = 0; jj < cv2.getALongCount(); jj++)
                                if (cv2.getALong(jj) == lstCardInBattle.get(j).battleID) {
                                    lstCardInBattle.get(j).SetTired(1);
                                }

                        break;
                    case IService.GAME_BATTLE_HERO_READY: {
                        CommonVector cv3 = CommonVector.parseFrom(a.getData());
//	                        WriteLogBattle("HERO_READY: ", GameData.main.profile.username, string.Join(",", cv3.aLong));

                        for (int j = 0; j < lstCardInBattle.size(); j++)
                            for (int jj = 0; jj < cv3.getALongCount(); jj++)
                                if (cv3.getALong(jj) == lstCardInBattle.get(j).battleID) {
                                    lstCardInBattle.get(j).SetTired(0);
                                }

                        break;
                    }
                    case IService.GAME_SIMULATE_SKILLS_ON_BATTLE:

                        ListAction listActionSkill = ListAction.parseFrom(a.getData());
//                        lstSkillQueue.addAll(listActionSkill.getAActionList());
                        for (Action ac : listActionSkill.getAActionList())
                            SimulateSkillEffect(ac);

                        break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //wait

        CommonVector common = CommonVector.newBuilder().build();
        instance.session2.GameSimulateConfirm(common);

        //wait
        onProcessData = false;
//	        waitTimeToBattle = 0f;
    }

    void TowerDealDamage(long towerID, long damage, long hpRemain) {
        ArrayList<TowerController> lstTower = new ArrayList<>();

        if (towerID < -10)
            for (int j = 0; j < lstTowerInBattle.size(); j++) {
                if (lstTowerInBattle.get(j).pos == GetClientPosFromServerPos(1))
                    lstTower.add(lstTowerInBattle.get(j));
            }
        else
            for (int j = 0; j < lstTowerInBattle.size(); j++)
                if (lstTowerInBattle.get(j).pos == GetClientPosFromServerPos(0))
                    lstTower.add(lstTowerInBattle.get(j));

        long id = towerID < -10 ? (long) Math.abs(towerID) - 11 : (long) Math.abs(towerID) - 1;

        TowerController tower = null;
        for (int j = 0; j < lstTower.size(); j++)
            if (lstTower.get(j).id == id) {
                tower = lstTower.get(j);
                break;
            }

        if (tower != null) {
            tower.OnDamaged(damage, hpRemain, hpRemain == 0);
//            if (hpRemain <= 0) {
//
//                TowerController towerCheck = null;
//                for (int j = 0; j < lstTower.size(); j++)
//                    if (lstTower.get(j).id == 1) {
//                        towerCheck = lstTower.get(j);
//                        break;
//                    }
//
//                if (towerCheck != null)
//                    towerCheck.ActiveMiddleTowerHealth();
//            }
        }


    }

    private void GameConfirmStartBattle(ListAction listAction) {
        onProcessData = true;
        IsYourTurn = false;

//	        onGameConfirmStartBattle?.Invoke();
        int timePrepare = 0;

        try {

            for (int i = 0; i < listAction.getAActionCount(); i++) {
                Action a = listAction.getAAction(i);
                switch (a.getActionId()) {
                    case IService.GAME_PREPARE_SIMULATE_BATTLE: {

                        //wait 1s
                        ListAction listAction1 = ListAction.parseFrom(a.getData());

                        for (int j = 0; j < listAction1.getAActionCount(); j++) {
                            Action action = listAction1.getAAction(j);
                            timePrepare += 3;
                            if (action.getActionId() == IService.GAME_SIMULATE_SKILLS_ON_BATTLE) {
                                ListAction listAction2 = ListAction.parseFrom(action.getData());
//                                lstSkillQueue.addAll(listAction2.getAActionList());
                                for (Action ac : listAction2.getAActionList())
                                    SimulateSkillEffect(ac);
                            }
                        }

                        break;
                    }

                    case IService.GAME_CONFIRM_STARTBATTLE_DETAIL:
                        //aLong: index, nexIndex, indexfirstplayer , hasShardRemain
                        //aString: username, nextusername, usernamefirstplayer, message remind shard

                        //wait 1s

                        CommonVector commonVector = CommonVector.parseFrom(a.getData());

                        long nextIndex = commonVector.getALong(1);

                        SkillFailCondition();

                        // check 2 ben deu xong
                        if (nextIndex == -1) {
//	                        onGameBattleChangeTurn?.Invoke(1);
//                            StartChooseWay(instance.username.equals(commonVector.getAString(2)));
                        } else {
                            roundCount += 1;
//	                        onGameBattleChangeTurn?.Invoke(0);
                            GameStartBattleSimulation(commonVector.getAString(1));

                            IsYourTurn = instance.username.equals(commonVector.getAString(1));
//	                        ButtonOrbController.instance.UpdateBattleSwordState(commonVector.aString[1], commonVector.aString[2]);
                        }

                        boolean isHasShardRemain = commonVector.getALong(3) == 1;
//	                    if (isHasShardRemain)
//	                        Toast.Show(commonVector.aString[3]);

                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        onProcessData = false;
//	        onResetAttackCount?.Invoke();
        // wait 1s
        bot();



    }

    public void GameStartBattleSimulation(String username) {
        // wait 1
//	        onGameBattleSimulation?.Invoke(IsYourTurn, username, roundCount);
        if (IsYourTurn) {
//	            turnEffect[0].SetActive(true);
//	            turnEffect[1].SetActive(false);
        } else {
//	            turnEffect[0].SetActive(false);
//	            turnEffect[1].SetActive(true);
        }
    }

    private void StartChooseWay(boolean isPlayer) {
//	        turnEffect.ForEach(x => x.SetActive(false));
//	        yield return new WaitForSeconds(0.5f);
        //wait 0.5
//	        onGameChooseWay?.Invoke(isPlayer);

        if (isPlayer)
            ChooseWayRequest(turnCount % 2 == 0 ? 0 : 1);
//	        SoundHandler.main.PlaySFX(turnCount % 2 == 0 ? "AttackPhase1" : "AttackPhase2", "sounds");
    }

    private void GameChooseWayRequest(CommonVector commonVector) {

        onProcessData = true;

//	        WriteLogBattle("CHOOSE_WAY: ", string.Join(",", commonVector.aString), string.Join(",", commonVector.aLong));
//	        onGameChooseWayRequest?.Invoke();

//	        yield return new WaitForSeconds(0.5f);
        onProcessData = false;

    }

    public void ChooseWayRequest(int index) {
        CommonVector cvv = CommonVector.newBuilder()
                .addALong(index).build();

        instance.session2.GameChooseWayRequest(cvv);
    }

    private void SkillFailCondition() {
        OnEndSkillPhase();
    }

    public void OnEndSkillPhase() {
//	        lstSelectedSkillHandCard.Clear();
//	        lstSelectedSkillBoardCard.Clear();
//	        curCardSkill = null;
//	        onBoardClone = null;
//	        selectedTower = null;
//	        selectedCardSlot = null;
//	        selectedLane = null;
//	        UpdateSpellTarget(null, null, true);
//	        cancelSkill.SetActive(false);
//	        skillState = SkillState.None;
//	        UpdateSpellTarget(null, null, true);
//	        onUpdateMana(0, currentMana, ManaState.UseDone, 0);
//	        chooseTargets.gameObject.SetActive(false);
//	        isStartFindTarget = false;
//	        onEndSkillActive?.Invoke();


    }

    private void GameSumonCardInBattle(ListAction listAction) {
        //wait 0
        onProcessData = true;
        float delayTime = 0f;
        float skillWaitTime = 4f;
        boolean isPlayer = false;
        battleState = BATTLE_STATE.NORMAL;
        boolean isSummonGodPlayer = false;
        try {
            for (int i = 0; i < listAction.getAActionCount(); i++) {
                Action a = listAction.getAAction(i);

                switch (a.getActionId()) {
                    case IService.GAME_SUMMON_CARD_IN_BATTLE_DETAIL:
                        //aLong: sucess? 1 : 0, mana, battleId, heroId, frame,row, col, isHoiSinh,isFragile
                        //aString: username
                        CommonVector commonVector = CommonVector.parseFrom(a.getData());
//                    WriteLogBattle("SUMMON CARD: ", string.Join(",", commonVector.aString), string.Join(",", commonVector.aLong));
                        if (commonVector.getALong(0) == 0) {
//                        Toast.Show(commonVector.aString[0]);
                            // hiennt fail to summon
                        } else {

                            long mana = commonVector.getALong(1);
//                        onUpdateMana?.Invoke(GameData.main.profile.username.Equals(commonVector.aString[0]) ? 0 : 1, mana, ManaState.UseDone, 0);
                            onUpdateMana(commonVector.getAString(0), mana);
                            //Destroy(currentGodCardUI);

                            DBHero heroSummon = Database.GetHero(commonVector.getALong(3));
                            // để ý chỗ này, có thể sẽ đc buff 2 lần khi add card
                            heroSummon.atk +=commonVector.getALong(5);
                            heroSummon.hp += commonVector.getALong(6);
                            heroSummon.mana += commonVector.getALong(7);
                            // is god
                            isPlayer = instance.username.equals(commonVector.getAString(0));
                            if (instance.username.equals(commonVector.getAString(0))) {
                                if (commonVector.getALong(8) == -1 || commonVector.getALong(9) == -1) {

                                    HandCard card = GetHandCard(commonVector.getALong(2));
                                    if (card != null) {
                                        //sua frame
//                                        CreateCard(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), null, CardOwner.Player);
                                        card.MoveFail();
                                        Decks[0].RemoveCard(card, 0);
                                        Decks[0].ReBuildDeck(0);
//                                    PoolManager.Pools["Card"].Despawn(card.transform);
//                                        HideMagicCard(commonVector.getALong(2), true);
                                    }
                                    success = true;
                                } else {
                                    CardSlot slot = GetSlot(SlotType.Player, commonVector.getALong(8), commonVector.getALong(9));
                                    if (slot != null) {
                                        if (heroSummon.type == DBHero.TYPE_GOD) {
                                            isSummonGodPlayer = true;
//                                        Transform spawnEffect = null;
//                                        float delay = 0;
//                                        if (commonVector.aLong[3] == 1)
//                                        {
//                                            spawnEffect = natureVineSpawnEffect;
//                                            delay = 0.4f;
//                                        }
//                                        else
//                                        {
//                                            spawnEffect = holySpawnEffect;
//                                            delay = 0.1f;
//                                        }
                                            //sua frame
                                            BoardCard card = CreateCard(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), slot, CardOwner.Player,
                                                    commonVector.getALong(5),commonVector.getALong(6),commonVector.getALong(7));
                                            if (card != null) {
                                                card.isFragile = commonVector.getALong(11) != 0;
                                                card.Placed();
                                            }
//                                        onSpawnRandomGod?.Invoke(commonVector.aLong[3]);
                                            spawnGod(commonVector.getALong(2));
                                        } else {
                                            HandCard cardToRemove = GetHandCard(commonVector.getALong(2));
                                            if (cardToRemove != null) {
                                                //sua frame
                                                BoardCard card = CreateCard(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), slot, CardOwner.Player,
                                                        commonVector.getALong(5),commonVector.getALong(6),commonVector.getALong(7));
                                                if (card != null) {
                                                    card.isFragile = commonVector.getALong(11) != 0;
                                                    //CheckHeroSkill(TYPE_WHEN_SUMON, card,slot.xPos,slot.yPos);

                                                }
                                                Decks[0].RemoveCard(cardToRemove, 0);
                                                cardToRemove.MoveFail();
//                                            PoolManager.Pools["Card"].Despawn(cardToRemove.transform);
                                            }
                                            success = true;
                                        }

                                    }
                                }
                            } else {
                                if (commonVector.getALong(8) == -1 || commonVector.getALong(9) == -1) {
//                                    for (int j = 0; j < Decks[1].GetListCard().size(); j++)
//                                        if (Decks[1].GetListCard().get(j) != null) {
//
//                                            HandCard card = Decks[1].GetListCard().get(j);
//                                            if (card != null) {
//                                                card.SetHandCardData(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), CardOwner.Enemy, -1);
//                                                Decks[1].RemoveCard(card, 1);
//                                                CreateCard(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), null, CardOwner.Enemy);
//                                                break;
//                                            }
//                                        }
                                } else {

                                    CardSlot slot = null;
                                    for (int j = 0; j < enemySlotContainer.size(); j++)
                                        if (enemySlotContainer.get(j).xPos == commonVector.getALong(8) && enemySlotContainer.get(j).yPos == commonVector.getALong(9)) {
                                            slot = enemySlotContainer.get(j);
                                            break;
                                        }

                                    if (slot != null) {
                                        if (heroSummon.type == DBHero.TYPE_GOD) {
//                                        Transform spawnEffect = null;
//                                        float delay = 0;
//                                        if (commonVector.aLong[3] == 1)
//                                        {
//                                            spawnEffect = natureVineSpawnEffect;
//                                            delay = 0.4f;
//                                        }
//                                        else
//                                        {
//                                            spawnEffect = holySpawnEffect;
//                                            delay = 0.1f;
//                                        }
                                            //sua frame
                                            BoardCard card = CreateCard(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), slot, CardOwner.Enemy,
                                                    commonVector.getALong(5),commonVector.getALong(6),commonVector.getALong(7));
                                            card.isFragile = commonVector.getALong(11) != 0;

//                                        onSpawnRandomGodEnemy?.Invoke(commonVector.aLong[3]);
                                        } else {
//                                            ArrayList<HandCard> enemyCards = Decks[1].GetListCard();
//                                            HandCard card = null;
//                                            for (int j = 0; j < enemyCards.size(); j++)
//                                                if (enemyCards.get(j) != null) {
//                                                    card = enemyCards.get(j);
//                                                    break;
//                                                }
//
//                                            if (card != null) {
//                                                card.SetHandCardData(commonVector.getALong(2), commonVector.getALong(3), 1, CardOwner.Enemy, -1);
//                                                Decks[1].RemoveCard(card, 1);

                                            BoardCard boardCard = CreateCard(commonVector.getALong(2), commonVector.getALong(3), commonVector.getALong(4), slot, CardOwner.Enemy,
                                                    commonVector.getALong(5),commonVector.getALong(6),commonVector.getALong(7));
                                            boardCard.isFragile = commonVector.getALong(10) != 0;
//                                                PoolManager.Pools["Card"].Despawn(card.transform);
//                                            }
                                        }
                                    }
                                }
                            }
                            delayTime += 4f;
                        }

                        break;


                    case IService.GAME_SIMULATE_SKILLS_ON_BATTLE:

//                    if (!isPlayer)
//                        yield return new WaitForSeconds(skillWaitTime);
                        ListAction listActionSkill = ListAction.parseFrom(a.getData());
                        //delayTime += CalculateTimeForSkill(listActionSkill);
                        for (int j = 0; j < listActionSkill.getAActionCount(); j++)
//                            lstSkillQueue.add(listActionSkill.getAAction(j));
                            SimulateSkillEffect(listActionSkill.getAAction(j));
                        break;

                    case IService.GAME_UPDATE_HERO_MATRIC:


                        CommonVector cv = CommonVector.parseFrom(a.getData());
//                    WriteLogBattle("UPDATE_HERO_MATRIC", string.Join(",", cv.aString), string.Join(",", cv.aLong));
//                    yield return new WaitForSeconds(1f);
                        UpdateHeroMatric(cv, true);

                        delayTime += 1f;

                        break;

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //wait delay
        onProcessData = false;


    }

    public void spawnGod(long battleId) {
        for (GodCardUI godCard : playerGodDeck)
            if (godCard.battleId == battleId) {
                playerGodDeck.remove(godCard);
                return;
            }
    }

    private void UpdateHeroMatric(CommonVector cv, boolean attached) {
//        yield return new WaitForSeconds(0f);
        onProcessData = true;

        //[batleId,atk,hp,hpMax,cleave.Pierce, Breaker, Combo, Overrun, Shield, shard]
        int BLOCK = 12;
        int num = cv.getALongCount() / BLOCK;

//        WriteLogBattle("UpdateHeroMatric", GameData.main.profile.username, string.Join(",", cv.aLong));

        for (int i = 0; i < num; i++) {
            //update hero matric
            //35,2,5,5,0,0,0,0,0,0,1

            BoardCard card = null;

            for (int j = 0; j < lstCardInBattle.size(); j++)
                if (lstCardInBattle.get(j).battleID == cv.getALong(i * BLOCK)) {
                    card = lstCardInBattle.get(j);
                    break;
                }

            if (card != null) {
                card.UpdateHeroMatrix(cv.getALong(i * BLOCK + 1), cv.getALong(i * BLOCK + 2), cv.getALong(i * BLOCK + 3), cv.getALong(i * BLOCK + 4), cv.getALong(i * BLOCK + 5),
                        cv.getALong(i * BLOCK + 6), cv.getALong(i * BLOCK + 7), cv.getALong(i * BLOCK + 8), cv.getALong(i * BLOCK + 9), cv.getALong(i * BLOCK + 10), cv.getALong(i * BLOCK + 11), 0, 0);
            }
        }

//        yield return new WaitForSeconds(1f);
        if (!attached)
            onProcessData = false;
    }

    private void GameMoveCardInBattle(ListAction listAction) {

        //wait 0
        onProcessData = true;
        float delayTime = 0f;
        battleState = BATTLE_STATE.NORMAL;

        try {
            for (int i = 0; i < listAction.getAActionCount(); i++) {
                Action a = listAction.getAAction(i);

                switch (a.getActionId()) {
                    case IService.GAME_MOVE_CARD_IN_BATTLE_DETAIL:
                        //along: sucess ? 1 : 0, [battleId, heroId, row old, col old, row new, col new, isTired ? 1 : 0]
                        CommonVector commonVector = CommonVector.parseFrom(a.getData());
//	                    WriteLogBattle("MOVE_CARD: ", string.Join(",", commonVector.aString), string.Join(",", commonVector.aLong));
                        //1,3,3,0,2,0,0,1,2,2,0,0,0,2,1
                        if (commonVector.getALong(0) == 0) {
//	                        Toast.Show(commonVector.aString[0]);
                        } else {
                            if (IsYourTurn) {
                                for (int j = 1; j < commonVector.getALongCount(); j += 7) {
                                    BoardCard card = GetBoardCard(commonVector.getALong(j));
                                    if (card != null) {
                                        CardSlot slot = GetSlot(SlotType.Player, commonVector.getALong(j + 4), commonVector.getALong(j + 5));
                                        if (slot != null)
                                            card.MoveToSlot(slot);
                                    }
                                    card.SetTired(commonVector.getALong(j + 6));
                                    card.MoveFail();
                                }
                            } else {
                                for (int j = 1; j < commonVector.getALongCount(); j += 7) {
                                    BoardCard card = GetBoardCard(commonVector.getALong(j));
                                    if (card != null) {
                                        CardSlot slot = GetSlot(SlotType.Enemy, commonVector.getALong(j + 4), commonVector.getALong(j + 5));
                                        if (slot != null)
                                            card.MoveToSlot(slot);
                                    }
                                    card.SetTired(commonVector.getALong(j + 6));
                                }
                            }
                            delayTime += 1f;
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //wait delay
        onProcessData = false;

    }

    private void GameAddShardHero(ListAction listAction) {
        // wait 0
        onProcessData = true;

        float delayTime = 0f;

        try {
            for (int i = 0; i < listAction.getAActionCount(); i++) {
                Action a = listAction.getAAction(i);

                {
                    switch (a.getActionId()) {
                        case IService.GAME_ADD_SHARD_HERO_DETAIL: {
                            //along: sucess ? 1: 0, row, col, battleId, heroShard, numbershard remain
                            //aString: username
                            CommonVector commonVector = CommonVector.parseFrom(a.getData());
//	                        WriteLogBattle("ADD_SHARD: ", string.Join(",", commonVector.aString), string.Join(",", commonVector.aLong));

                            if (commonVector.getALong(0) == 0) {
//	                            Toast.Show(commonVector.aString[0]);
                            } else {
//	                            SoundHandler.main.PlaySFX("Drop shard on god", "sounds");
                                long heroShard = commonVector.getALong(4);
                                long shardRemain = commonVector.getALong(5);
//	                            onUpdateShard?.Invoke(GameData.main.profile.username.Equals(commonVector.aString[0]) ? 0 : 1, shardRemain);
                                onUpdateShard(commonVector.getAString(0), shardRemain);

                                BoardCard card = GetBoardCard(commonVector.getALong(3));
                                if (card != null)
                                    card.OnAddShard(commonVector.getALong(4), true);

                                delayTime += 1f;
                            }
                            break;
                        }

                        case IService.GAME_SIMULATE_SKILLS_ON_BATTLE: {
                            ListAction listActionSkill = ListAction.parseFrom(a.getData());
                            for (int j = 0; j < listActionSkill.getAActionCount(); j++)
//                                lstSkillQueue.add(listActionSkill.getAAction(j));
                                SimulateSkillEffect(listActionSkill.getAAction(j));
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // wait delay
        onProcessData = false;
    }

    private void GameGetShard(ListAction listAction) {
        // wait 1
        onProcessData = true;

        try {

            for (int i = 0; i < listAction.getAActionCount(); i++) {
                Action a = listAction.getAAction(i);

                switch (a.getActionId()) {
                    case IService.GAME_GET_SHARD_DETAIL: {
                        CommonVector commonVector = CommonVector.parseFrom(a.getData());
//	                    WriteLogBattle("GET_SHARD: ", string.Join(",", commonVector.aString), string.Join(",", commonVector.aLong));

                        if (commonVector.getALong(0) == 0) {
//	                        Toast.Show(commonVector.aString[0]);
//	                        yield return new WaitForSeconds(0f);
                            onProcessData = false;
                        } else {

                            long shard = commonVector.getALong(1);
                            long mana = commonVector.getALong(2);

                            onUpdateShard(commonVector.getAString(0), shard);
//	                        onUpdateShardClick?.Invoke(GameData.main.profile.username.Equals(commonVector.aString[0]) ? 0 : 1, 2);
//	                        onUpdateMana?.Invoke(GameData.main.profile.username.Equals(commonVector.aString[0]) ? 0 : 1, mana, ManaState.UseDone, 0);
                            onUpdateMana(commonVector.getAString(0), mana);
                            // wait 1
                            onProcessData = false;
                        }
                        break;
                    }
                    case IService.GAME_SIMULATE_SKILLS_ON_BATTLE: {
                        ListAction listActionSkill = ListAction.parseFrom(a.getData());
                        for (Action ac : listActionSkill.getAActionList())
//                        lstSkillQueue.Add(ac);
                            SimulateSkillEffect(ac);
                        break;
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onUpdateShard(String username, long shard) {
        if (instance.username.equals(username)) currentShard = shard;
    }

    public void onUpdateMana(String username, long mana) {
        if (instance.username.equals(username)) currentMana = mana;
    }

    public void InitGodUI(List<Long> godBattleID1, List<DBHero> godHero1, List<Long> frame1, List<Long> godBattleID2, List<DBHero> godHero2, List<Long> frame2) {
        if (godHero1.size() > 6 || godHero2.size() > 6) {
            return;
        }
        for (int i = 0; i < godHero1.size(); i++) {
            GodCardUI card = new GodCardUI();
            card.InitData(godBattleID1.get(i), godHero1.get(i).id, frame1.get(i), EnumTypes.CardOwner.Player); //Database.GetHero(godHero1[indexPlayerBattleID].id).name); ; ;
            playerGodDeck.add(card);
        }
//        for (int i = 0; i < godHero2.size(); i++){
//            GodCardUI card = new GodCardUI();
//            card.InitData(godBattleID2.get(i), godHero2.get(i).id,frame2.get(i), EnumTypes.CardOwner.Enemy); //Database.GetHero(godHero1[indexPlayerBattleID].id).name); ; ;
//            enemyGodDeck.add(card);
//        }
    }

    void DrawDeckStart(int index, List<DBHero> heroList, List<Long> battleIDList, List<Long> frameList
//					   ,List<long> manaList
    ) {
        for (int i = 0; i < heroList.size(); i++) {
//			Transform cardGO = null;
//			if (heroList[i].id > 0)
//				cardGO = PoolManager.Pools["Card"].Spawn(m_MinionCard);
//			else
//				cardGO = PoolManager.Pools["Card"].Spawn(/*m_EnemyMinionCard*/m_MinionCard);

//			cardGO.position = spawnPosition[index].position + new Vector3(0, 0.2f, 0);
//			cardGO.rotation = Quaternion.Euler(cardGO.rotation.x, 180, cardGO.rotation.z);

            //LogWriterHandle.WriteLog(cardGO.gameObject.name);
//			HandCard card = cardGO.GetComponent<HandCard>();
            HandCard card = new HandCard();
            if (battleIDList.get(i) > 0) {
//				if (manaList != null)
//					card.SetHandCardData(battleIDList[i], heroList[i].id, frameList[i], CardOwner.Player, manaList[i]);
//				else
                card.SetHandCardData(battleIDList.get(i), heroList.get(i).id, frameList.get(i), CardOwner.Player, -1,0,0);


            } else
                card.cardOwner = CardOwner.Enemy;
            Decks[index].AddNewCard(card);
//			card.transform.localRotation = Quaternion.Euler(180f, -90f, 0);
        }
        //SoundHandler.main.PlaySFX("DrawCard");
        Decks[index].ReBuildDeck(index);
        //Decks[index].RebuildDeckOnDrawDeck(index, showOpponentCardPoint);
    }

    void AddNewCard(int index, DBHero hero, long battleID, long frame, boolean isFleeting
//			  , ICallback.CallFunc2<HandCard> callback = null
            ,long atk, long hp, long mana) {
//	        Transform cardGO;
//	        if (hero.id > 0)
//	            cardGO = PoolManager.Pools["Card"].Spawn(m_MinionCard);
//	        else
//	            cardGO = PoolManager.Pools["Card"].Spawn(/*m_EnemyMinionCard*/m_MinionCard);
//
//	        if (isFleeting)
//	            cardGO.position = Decks[index].transform.position;
//	        else
//	            cardGO.position = spawnPosition[index].position + new Vector3(0, 0.2f, 0);
//	        cardGO.rotation = Quaternion.Euler(0, -90f, 180);

        HandCard card = new HandCard();

        if (battleID > 0)
            card.SetHandCardData(battleID, hero.id, frame, CardOwner.Player, mana,atk,hp);
        else
            card.cardOwner = CardOwner.Enemy;

        Decks[index].AddNewCard(card);


//	        card.transform.localRotation = Quaternion.Euler(180f, -90f, 0);
//	        Decks[index].ReBuildDeck(index, () => callback?.Invoke(card));

        // Decks[index].RebuildDeckOnAddCard(index, showOpponentCardPoint, card);
        card.isFleeting = isFleeting;
    }

    private void GameStartBattle(ListAction listAction) {
        System.out.println("GameStartBattle");
        // wait 0
        onProcessData = true;

        if (!isGameStarted)
            isGameStarted = true;

        float delayTime = 0f;

        try {

            for (int i = 0; i < listAction.getAActionCount(); i++) {
                Action a = listAction.getAAction(i);
                switch (a.getActionId()) {
                    case IService.GAME_START_BATTLE_DETAIL: {
                        CommonVector commonVector = CommonVector.parseFrom(a.getData());
//	                        WriteLogBattle("GAME_START_BATTLE: ", string.Join(",", commonVector.aString), string.Join(",", commonVector.aLong));
                        // way
                        turnCount = (int) commonVector.getALong(0);
//	                        turnArrow[0].SetActive(turnCount % 2 == 0);
//	                        turnArrow[1].SetActive(turnCount % 2 != 0);

//	                        GameStartBattleSimulation(commonVector.getAString(0), GetTurnEffect());
                        // bat dau luot cua minh
                        IsYourTurn = instance.username.equals(commonVector.getAString(0));
//	                        ButtonOrbController.instance.UpdateBattleSwordState(commonVector.aString[0], commonVector.aString[0]);
                        System.out.println("GameStartBattle");
                        System.out.println("IsYourTurn = " + IsYourTurn);
                        for (int j = 1; j < commonVector.getAStringCount(); j++) {
                            long shard = commonVector.getALong(3 + (j - 1) * 2);
                            long cardSize = commonVector.getALong(3 + ((j - 1) * 2) + 1);
                            String username = commonVector.getAString(j);
                            if (cardSize > 0) {
                                if (instance.username.equals(username)) {
                                    long battleID = commonVector.getALong(commonVector.getALongCount() - 6);
                                    long heroID = commonVector.getALong(commonVector.getALongCount() - 5);
                                    System.out.println("drawcard battleId = " + battleID + " heroId = " + heroID);
                                    long frame = commonVector.getALong(commonVector.getALongCount() - 4);
                                    long atk = commonVector.getALong(commonVector.getALongCount() - 3);
                                    long hp = commonVector.getALong(commonVector.getALongCount() - 2);
                                    long cardMana = commonVector.getALong(commonVector.getALongCount() - 1);
                                    DBHero hero = Database.GetHero(heroID);
                                    //them
                                    AddNewCard(0, hero, battleID, frame, false, atk, hp, cardMana);
                                } else {
                                    DBHero hero = new DBHero();
                                    hero.id = -1;
                                    AddNewCard(1, hero, -1, 1, false, 0, 0, 0);
                                }
                            }
//	                            onUpdateShard?.Invoke(GameData.main.profile.username.Equals(username) ? 0 : 1, shard);
                            onUpdateShard(username, shard);

                        }
//	                        SoundHandler.main.PlaySFX("ManaRegen1", "sounds");
                        currentMana = turnMana = commonVector.getALong(2);

//	                        onUpdateMana?.Invoke(0, commonVector.aLong[2], ManaState.StartTurn, 0);
//	                        onUpdateMana?.Invoke(1, commonVector.aLong[2], ManaState.StartTurn, 0);
                        // lstCardInBattle.ForEach(card => CheckHeroSkill(TYPE_WHEN_START_TURN, card));
//                        delayTime += 1f;
                        break;
                    }

                    case IService.GAME_SIMULATE_SKILLS_ON_BATTLE: {
                        ListAction listActionSkill = ListAction.parseFrom(a.getData());

                        for (int j = 0; j < listActionSkill.getAActionCount(); j++)
                            SimulateSkillEffect(listActionSkill.getAAction(j));


//					lstSkillQueue.add(listActionSkill.getAAction(j));
//					 StartCoroutine(SimulateSkillEffect(listActionSkill, true));
//					 delayTime += CalculateTimeForSkill(listAction);
                        break;
                    }

                    case IService.GAME_DELETE_CARDS: {
                        CommonVector commonVector = CommonVector.parseFrom(a.getData());
                        DeleteCardsOnHand(commonVector, true);

                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // wait delay
        onProcessData = false;
        //BOT
        bot();


    }

    public void bot() {
        try {
            if (IsYourTurn) {
                //trên sân có vị trí để summon không
                int rowEmty = -1;
                int colEmty = -1;
                List<Integer> lstXIndex = new ArrayList<>();
                List<Integer> lstYIndex = new ArrayList<>();
                for (CardSlot slot : playerSlotContainer) {
                    if (slot.state == SlotState.Empty) {
                        if (slot.yPos != 3) { //có cần thêm đk là x =0,1 không?
                            lstXIndex.add(slot.xPos);
                            lstYIndex.add(slot.yPos);
                        }
                    }
                }
                //này là gì? lấy god card hiện tại à?
                int godRow = 0;
                int godCol = 0;
                BoardCard godCard = null;
                for (int i = 0; i < playerSlotContainer.size(); i++) {
                    CardSlot slot = playerSlotContainer.get(i);
                    if (slot.state == SlotState.Full) {
                        Card card = slot.currentCard;
                        if (card.heroInfo.type == DBHero.TYPE_GOD) {
                            godCard = (BoardCard) card;
                            godRow = slot.xPos;
                            godCol = slot.yPos;
                            break;
                        }
                    }
                }
                //chạy luồng ko có chỗ trống
                if(lstXIndex.size() == 0){
                    if(godCard != null){
                        //trên tay có buf của thần và có đủ mana chơi hay ko?=========
                        for (HandCard handCard : Decks[0].GetListCard()) {
                            if(handCard.heroInfo.type == DBHero.TYPE_BUFF_GOD
                                    && handCard.heroInfo.owner_god_id == godCard.heroID
                                    && handCard.tmpMana <= currentMana){
                                currentMana -= handCard.tmpMana;
                                SummonBuffGodInBattlePhase(godCard.battleID, handCard.heroID, godRow,godCol); //để xem có phải gửi vị trí lên ko
                            }
                        }
                    }
                    //luồng chơi phép riêng
                    List<HandCard> handCards = new ArrayList<>();
                    for (HandCard handCard : Decks[0].GetListCard()) {
                        if (handCard.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC && CanSummon(handCard)) {
                            handCards.add(handCard);
                        }
                    }
                    if (handCards.size() != 0) {
                        for (int i = 0; i < prioritySpellArr.size(); i++) {
                            JSONArray arr = (JSONArray) prioritySpellArr.get(i);
                            for (HandCard card : handCards) {
                                long heroId = card.heroID;
                                if (arr.contains(heroId)) {
                                    if (SummonSpellInBattlePhase(card)) {
                                        Thread.sleep(2000);
                                        if (success) {
                                            instance.session2.GameConfirmStartBattle();
                                            return;
                                        }
                                    }
                                }
                            }

                        }
                    }
                    //enturn thôi
                    instance.session2.GameConfirmStartBattle();
                    return;

                }

                //trường hợp có chỗ trống

                if(godCard != null) {
                    if (playerGodDeck.size() != 0) {
                        long maxMana = 0;
                        int index = -1;
                        //này là lấy index và maxmana của tướng có nhiều mana nhất trong bộ
                        for (int i = 0; i < playerGodDeck.size(); i++) {
                            DBHero god = playerGodDeck.get(i).hero;
                            if (god.mana <= currentMana && god.mana > maxMana) {
                                maxMana = god.mana;
                                index = i;
                            }
                        }
                        long row = -1, col = -1;
                        //lấy ra tướng được chọn
                        GodCardUI godCardSelected = playerGodDeck.get(index);

                        //đây là gì nhỉ? chọn mấy con hero chỉ hợp hàng sau
                        if (godCardSelected.hero.id == 347 || godCardSelected.hero.id == 349 || godCardSelected.hero.id == 350 || godCardSelected.hero.id == 351)
                            row = 0;
                        else row = 1;
                        List<Integer> lstIndex = new ArrayList<>();

                        //lấy ra danh sách các slot trống ư? với đk là slot đó ở row tương ứng vs vị trí của thần và tình trạng là empty, sau đó lưu y vào
                        for (CardSlot slot : playerSlotContainer) {
                            if (slot.xPos == row && slot.state == SlotState.Empty) {
                                if (slot.yPos != 3) lstIndex.add(slot.yPos);
                            }
                        }
                        //nếu ko có vị trí trống, thì chuyển sang row còn lại và duyệt chăng?
                        if (lstIndex.size() == 0) {
                            row = 1 - row;
                            for (CardSlot slot : playerSlotContainer) {
                                if (slot.xPos == row && slot.state == SlotState.Empty) {
                                    if (slot.yPos != 3) lstIndex.add(slot.yPos);
                                }
                            }
                        }
                        //vẫn ko có thì thôi, chơi game
                        if (lstIndex.size() == 0) {
                            instance.session2.GameConfirmStartBattle();
                            return;
                        }

                        //lấy bừa 1 vị trí trong số các vị trí trống
                        col = lstIndex.get(random.nextInt(lstIndex.size()));

                        //triệu hồi vô đó
                        SummonGodInBattlePhase(godCardSelected, row, col);
                        lstXIndex.remove(row);
                        lstYIndex.remove(col);
                        godCard = null;
                        Thread.sleep(2000);
                    }
                }
                if (godCard != null){
                    //trên tay có buf của thần và có đủ mana chơi hay ko?=========
                    //này là gì? lấy god card hiện tại à?
                    for (int i = 0; i < playerSlotContainer.size(); i++) {
                        CardSlot slot = playerSlotContainer.get(i);
                        if (slot.state == SlotState.Full) {
                            Card card = slot.currentCard;
                            if (card.heroInfo.type == DBHero.TYPE_GOD) {
                                godCard = (BoardCard) card;
                                break;
                            }
                        }
                    }
                    for (HandCard handCard : Decks[0].GetListCard()) {
                        if(handCard.heroInfo.type == DBHero.TYPE_BUFF_GOD
                                && handCard.heroInfo.owner_god_id == godCard.heroID
                                && handCard.tmpMana <= currentMana){
                            currentMana -= handCard.tmpMana;
                            SummonBuffGodInBattlePhase(godCard.battleID, handCard.heroID, godRow,godCol ); //để xem có phải gửi vị trí lên ko
                        }
                    }
                }
                Thread.sleep(5000);

                //luồng chơi lính hoặc phép
                List<HandCard> canSummonCardsNormal = new ArrayList<>(); //=================sửa Cummon thành Summon=========================================================
                List<HandCard> canSummonCardsMagic = new ArrayList<>();
                //duyệt xem trên tay có thẻ nào
                for (HandCard card : Decks[0].GetListCard()) {
                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card))
                        canSummonCardsNormal.add(card);
                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC && CanSummon(card))
                        canSummonCardsMagic.add(card);
                }
                int indexAction = -1;
                if (canSummonCardsNormal.size() != 0 && canSummonCardsMagic.size() != 0){
                    indexAction = random.nextInt(2);
                } else if (canSummonCardsNormal.size() != 0){
                    indexAction = 0;
                } else if (canSummonCardsMagic.size() != 0) {
                    indexAction = 1;
                } else {
                    //đếch có con nào thì sử lý sao???
                }

                switch (indexAction){
                    //summon lính
                    case 0: {
                        //lấy mảng vị trí kẻ thù
                        CardSlot enemySlotMatrix[][] = new CardSlot[2][4];
                        //ấy mảng gì đó cũng ko biết nữa cơ
                        CardSlot playerSlotMatrix[][] = new CardSlot[2][4];
                        //này là gì, lấy vị trí kẻ thù à?
                        for (CardSlot slot : enemySlotContainer) {
                            enemySlotMatrix[slot.xPos][slot.yPos] = slot;
                        }
                        //còn này là lấy mảng tướng của mình hiện tại à?
                        for (CardSlot slot : playerSlotContainer) {
                            playerSlotMatrix[slot.xPos][slot.yPos] = slot;
                        }
                        //này chắc hẳn là lấy số lượng thẻ địch và mình trên sân?
                        List<Integer> lstValidEmptyCol = new ArrayList<>();
                        for (int j = 0; j < MAX_COLUMN; j++) {
                            int enemyCardCount = 0;
                            int playerCardCount = 0;
                            for (int i = 0; i < MAX_ROW; i++) {
                                if (enemySlotMatrix[i][j].state != SlotState.Empty) enemyCardCount++;
                            }
                            for (int i = 0; i < MAX_ROW; i++) {
                                if (playerSlotMatrix[i][j].state != SlotState.Empty) playerCardCount++;
                            }
                            //nếu không phải ở cột cuối cùng và không có kẻ địch nào trên sân và số thẻ đồng minh trong cột j chưa full thì thêm vào lstValidEmptyCol
                            if (j < MAX_COLUMN - 1 && enemyCardCount == 0 && playerCardCount < MAX_ROW) {
                                lstValidEmptyCol.add(j);
                            }
                        }
                        //nếu có 1 cột nào đó chưa có kẻ địch và có thể triệu gồi vô đó
                        if (lstValidEmptyCol.size() > 0) {
                            //lấy ngẫu nhiên 1 cột trong các cột trống
                            int col = lstValidEmptyCol.get(random.nextInt(lstValidEmptyCol.size()));
                            //này là ưu tiên nếu có 2 vt rí trống thì 80% hàng trước, 20% hàng sau,
                            int row;
                            if (playerSlotMatrix[0][col].state == SlotState.Empty) {
                                if (playerSlotMatrix[1][col].state == SlotState.Empty) {
                                    int k = random.nextInt(100);
                                    if (k < 80) row = 0;
                                    else row = 1;
                                } else {
                                    row = 0;
                                }
                            } else {
                                row = 1;
                            }
                            //hàm if này là nếu có tướng thuộc nhóm và có thể triệu hồi thì triệu hồi luôn, còn không thì trả về false
                            if (summonCardInGroup(1, row, col)) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                            // chưa có
                            if (summonCardInGroup(2, row, col)) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                        }
                        //chuyển tới luồng thần đối phương có ở hàng trước không
                        List<Integer> lstValidEnemyGodCol = new ArrayList<>();
                        for (int j = 0; j < MAX_COLUMN - 1; j++) {
                            //full = true là hàng sau cột j trống và hàng trước cột j trống
                            boolean full = playerSlotMatrix[0][j].state != SlotState.Empty && playerSlotMatrix[1][j].state != SlotState.Empty;
                            if (!full) {
                                if ((enemySlotMatrix[0][j].state == SlotState.Full && enemySlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_GOD)
                                        || (enemySlotMatrix[1][j].state == SlotState.Full && enemySlotMatrix[1][j].currentCard.heroInfo.type == DBHero.TYPE_GOD && enemySlotMatrix[0][j].state == SlotState.Empty)) {
                                    lstValidEnemyGodCol.add(j);
                                }
                            }
                        }
                        //nếu có
                        if (lstValidEnemyGodCol.size() > 0) {
                            int col = lstValidEnemyGodCol.get(random.nextInt(lstValidEnemyGodCol.size()));
                            int row;
                            if (playerSlotMatrix[0][col].state == SlotState.Empty) {
                                if (playerSlotMatrix[1][col].state == SlotState.Empty) {
                                    int k = random.nextInt(100);
                                    if (k < 80) row = 0;
                                    else row = 1;
                                } else {
                                    row = 0;
                                }
                            } else {
                                row = 1;
                            }
                            if (summonCardInGroup(10, row, col)) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                            if (summonCardInGroup(5, row, col)) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                            //chưa có
                            if (summonCardInGroup(2, row, col)) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                            // chưa có
                            if (summonCardInGroup(6, row, col)) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                            long maxAtk = -1;
                            HandCard selectCard = null;
                            for (HandCard card : Decks[0].GetListCard()) {
                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
                                    if (card.atkValue > maxAtk) {
                                        maxAtk = card.atkValue;
                                        selectCard = card;
                                    }
                                }
                            }
                            SummonNormalInBattlePhase(selectCard, row, col);
                            Thread.sleep(2000);
                            instance.session2.GameConfirmStartBattle();
                            return;
                        }
                        // luồng có địch <3 máu ở hàng trước
                        List<Integer> lstValidEnemyHpCol = new ArrayList<>();
                        for (int j = 0; j < MAX_COLUMN - 1; j++) {
                            boolean full = playerSlotMatrix[0][j].state != SlotState.Empty && playerSlotMatrix[1][j].state != SlotState.Empty;
                            if (!full) {
                                if (enemySlotMatrix[0][j].state == SlotState.Full && enemySlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && ((BoardCard) enemySlotMatrix[0][j].currentCard).hpValue < 3
                                ) {
                                    lstValidEnemyHpCol.add(j);
                                }

                            }
                        }
                        //nếu có
                        if (lstValidEnemyHpCol.size() > 0) {
                            int col = lstValidEnemyHpCol.get(random.nextInt(lstValidEnemyHpCol.size()));
                            int row;
                            if (canSummonCardInGroup(13) || canSummonCardInGroup(5) || canSummonCardInGroup(6)) {
                                if (playerSlotMatrix[0][col].state == SlotState.Empty) {
                                    row = 0;
                                } else {
                                    CommonVector cv = CommonVector.newBuilder()
                                            .addALong(0)
                                            .addALong(col)
                                            .addALong(1).addALong(col).build();
                                    instance.session2.GameMoveCardInbattle(cv);
                                    Thread.sleep(2000);
                                    row = 1;
                                }
                                if (summonCardInGroup(13, row, col)) {
                                    Thread.sleep(2000);
                                    instance.session2.GameConfirmStartBattle();
                                    return;
                                }
                                if (summonCardInGroup(5, row, col)) {
                                    Thread.sleep(2000);
                                    instance.session2.GameConfirmStartBattle();
                                    return;
                                }
                                //chưa có
                                summonCardInGroup(6, row, col);
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                        }
                        // chưa có, lane đồng minh có thần ko, lane là 2 đường 1 á
                        if (canSummonCardInGroup(15)) {
                            // todo : nếu bộ bài khác có group lính 15
                            //duyệt qua từng đường, xem thần ở vị trí nào
                            boolean lane1Full = true;
                            boolean lane0Full = true;
                            List<Integer> lstValidPlayerGodCol = new ArrayList<>();
                            for(int j=0;j<MAX_COLUMN-1;j++){
                                if( j == 0 || j == 1){
                                    boolean full = playerSlotMatrix[0][0].state != SlotState.Empty
                                            && playerSlotMatrix[1][0].state != SlotState.Empty
                                            && playerSlotMatrix[0][1].state != SlotState.Empty
                                            && playerSlotMatrix[1][1].state != SlotState.Empty;
                                    if(!full){
                                        lane0Full = false;
                                        if((playerSlotMatrix[0][j].state == SlotState.Full && playerSlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_GOD)
                                                || playerSlotMatrix[1][j].state == SlotState.Full && playerSlotMatrix[1][j].currentCard.heroInfo.type == DBHero.TYPE_GOD){
                                            lstValidPlayerGodCol.add(0);
                                            lstValidPlayerGodCol.add(1);
                                        }
                                    }
                                } else {
                                    boolean full = playerSlotMatrix[0][j].state != SlotState.Empty && playerSlotMatrix[1][j].state != SlotState.Empty;
                                    if(!full){
                                        lane1Full = false;
                                        if((playerSlotMatrix[0][j].state == SlotState.Full && playerSlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_GOD)
                                                || playerSlotMatrix[1][j].state == SlotState.Full && playerSlotMatrix[1][j].currentCard.heroInfo.type == DBHero.TYPE_GOD){
                                            lstValidPlayerGodCol.add(j);
                                        }
                                    }
                                }
                            }
                            //nếu có 1 đường thỏa mãn có tướng và còn trống
                            if(lstValidPlayerGodCol.size()>0) {
                                int col = lstValidEnemyGodCol.get(random.nextInt(lstValidEnemyGodCol.size()));
                                int row;
                                if (playerSlotMatrix[0][col].state == SlotState.Empty) {
                                    if (playerSlotMatrix[1][col].state == SlotState.Empty) {
                                        int k = random.nextInt(100);
                                        if (k < 80) row = 0;
                                        else row = 1;
                                    } else {
                                        row = 0;
                                    }
                                } else {
                                    row = 1;
                                }
                                if (summonCardInGroup(15, row, col)) {
                                    Thread.sleep(2000);
                                    instance.session2.GameConfirmStartBattle();
                                    return;
                                }
                            } else {
                                //nếu không có đường thỏa mãn có tướng còn trống, hoặc không có tướng
                                List<Integer> cols = new ArrayList<>();
                                List<Integer> rows= new ArrayList<>();
                                //duyệt tất cả vị trí trống cho nó nhanh
                                for(int j =0; j< MAX_COLUMN-1;j++){
                                    for(int i =0; i< MAX_ROW;j++){
                                        if(playerSlotMatrix[i][j].state != SlotState.Full){
                                            cols.add(j);
                                            rows.add(i);
                                        }
                                    }
                                }
                                //dù là trường hợp 1 lane trống hay 2 lane trống, có tướng nhưng full hay ko có tướng đều như nhau
                                //2 lane trống, tức là đã ko thể chạy if đầu tiên và xuống else này, tức ko có tướng, vậy random trong số chỗ trống thôi
                                //1 lane trống, tức là trong số cols chỉ chứa cột 0 1 hoặc 2, vậy ta cũng chỉ cần random index bất kỳ rồi lấy thôi
                                int i = random.nextInt(cols.size());
                                if (summonCardInGroup(15, rows.get(i), cols.get(i))) {
                                    Thread.sleep(2000);
                                    instance.session2.GameConfirmStartBattle();
                                    return;
                                }
                            }
                        }

                        // chưa có
                        if (canSummonCardInGroup(8)) {
                            // todo : nếu bộ bài khác có group lính 8
                            List<Integer> cols = new ArrayList<>();
                            List<Integer> rows= new ArrayList<>();
                            //duyệt tất cả vị trí trống cho nó nhanh
                            for(int j =0; j< MAX_COLUMN-1;j++){
                                for(int i =0; i< MAX_ROW;j++){
                                    if(playerSlotMatrix[i][j].state != SlotState.Full){
                                        cols.add(j);
                                        rows.add(i);
                                    }
                                }
                            }
                            int i = random.nextInt(cols.size());
                            if (summonCardInGroup(8, rows.get(i), cols.get(i))) {
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;
                            }
                        }

                        // chưa có
                        if (canSummonCardInGroup(6)) {
                            //hmmm....
                            //có thần đối phương trên sân không
                            boolean ValidEnemyGod =false;
                            for(int j=0;j< MAX_COLUMN-1;j++){
                                if ((enemySlotMatrix[0][j].state == SlotState.Full && enemySlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_GOD)
                                        || (enemySlotMatrix[1][j].state == SlotState.Full && enemySlotMatrix[1][j].currentCard.heroInfo.type == DBHero.TYPE_GOD)) {
                                    //lstValidEnemyGod.add(j);
                                    ValidEnemyGod = true;
                                }
                            }
                            //hiện tại thì 2 đoạn sử lý triệu hồi của 2 trường hợp ValidEnemyGod khá giống nhau và khá dài, sau nêếu làm target thì sẽ thu gọn lại
                            if(ValidEnemyGod) {
                                int ValidAllyGod = -1;
                                List<Integer> cols = new ArrayList<>();
                                List<Integer> rows = new ArrayList<>();
                                //lấy danh sách vị trí trống
                                //duyệt tất cả vị trí trống cho nó nhanh
                                for(int j =0; j< MAX_COLUMN-1;j++){
                                    for(int i =0; i< MAX_ROW;j++){
                                        if(playerSlotMatrix[i][j].state != SlotState.Full){
                                            cols.add(j);
                                            rows.add(i);
                                        }
                                    }
                                }
                                //nếu mảng cols chỉ có 1 ptu thì triệu hồi vô đó
                                if(cols.size() == 1) {
                                    summonCardInGroupWithMaxMana(6,cols.get(0), rows.get(0));
                                } else if (cols.size() >= 1){
                                    //kiểm tra xem có tướng đồng minh ko để ưu tiên
                                    if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1 )
                                        ValidAllyGod = 0;
                                    else if (godCard.slot.yPos == 2)
                                        ValidAllyGod = 1;
                                    if(ValidAllyGod == -1){
                                        int i = random.nextInt(cols.size());
                                        summonCardInGroupWithMaxMana(6,cols.get(i), rows.get(i));
                                    } else {
                                        List<Integer> rowInLane = new ArrayList<>();
                                        for(int j = 0; j< cols.size(); j++){
                                            if (cols.get(j)==ValidAllyGod) {
                                                rowInLane.add(rows.get(j));
                                            }
                                        }
                                        if(rowInLane.size() > 0){
                                            summonCardInGroupWithMaxMana(6, rowInLane.get(random.nextInt(rowInLane.size())), cols.get(ValidAllyGod));
                                        }
                                    }
                                }

                                //todo: triệu hồi với mục tiêu chỉ định

                            }
                            else {
                                boolean normalHpAtk = false;
                                outer:
                                for(int j =0; j< MAX_COLUMN-1;j++){
                                    for(int i =0; i< MAX_ROW;j++){
                                        if(enemySlotMatrix[i][j].state == SlotState.Full
                                                && enemySlotMatrix[i][j].currentCard.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL
                                                && enemySlotMatrix[i][j].currentCard.hpValue == 1
                                                && enemySlotMatrix[i][j].currentCard.atkValue >= 2) {
                                            normalHpAtk = true;
                                            break outer;
                                        }
                                    }
                                }
                                if (normalHpAtk) {
                                    int ValidAllyGod = -1;
                                    List<Integer> cols = new ArrayList<>();
                                    List<Integer> rows = new ArrayList<>();
                                    //lấy danh sách vị trí trống
                                    //duyệt tất cả vị trí trống cho nó nhanh
                                    for(int j =0; j< MAX_COLUMN-1;j++){
                                        for(int i =0; i< MAX_ROW;j++){
                                            if(playerSlotMatrix[i][j].state != SlotState.Full){
                                                cols.add(j);
                                                rows.add(i);
                                            }
                                        }
                                    }
                                    //nếu mảng cols chỉ có 1 ptu thì triệu hồi vô đó
                                    if(cols.size() == 1) {
                                        summonCardInGroupWithMaxMana(6,cols.get(0), rows.get(0));
                                    } else if (cols.size() >= 1){
                                        //kiểm tra xem có tướng đồng minh ko để ưu tiên
                                        if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1 )
                                            ValidAllyGod = 0;
                                        else if (godCard.slot.yPos == 2)
                                            ValidAllyGod = 1;
                                        if(ValidAllyGod == -1){
                                            int i = random.nextInt(cols.size());
                                            summonCardInGroupWithMaxMana(6,cols.get(i), rows.get(i));
                                        } else {
                                            List<Integer> rowInLane = new ArrayList<>();
                                            for(int j = 0; j< cols.size(); j++){
                                                if (cols.get(j)==ValidAllyGod) {
                                                    rowInLane.add(rows.get(j));
                                                }
                                            }
                                            if(rowInLane.size() > 0){
                                                summonCardInGroupWithMaxMana(6, rowInLane.get(random.nextInt(rowInLane.size())), cols.get(ValidAllyGod));
                                            }
                                        }
                                    }

                                    //todo: triệu hồi với mục tiêu chỉ định? làm ở đâu ta?
                                }
                            }
                        }

                        //chị hương này viết dài vậy...dài gấp 3 của mình ròi...
//                        if(canSummonCardInGroup(3)){
//                            List<HandCard> cards = normalGroup(3);
//                            HandCard selectCard = null;
//                            long maxMana = -1;
//                            for (HandCard card : cards) {
//                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                    if (card.tmpMana > maxMana) {
//                                        maxMana = card.tmpMana;
//                                        selectCard = card;
//                                    }
//                                }
//                            }
//                            if (selectCard != null) {
//                                int lane = -1;
//                                outer:
//                                for (int i = 0; i < MAX_ROW; i++)
//                                    for (int j = 0; j < MAX_COLUMN; j++) {
//                                        CardSlot slot = playerSlotMatrix[i][j];
//                                        if (slot.state == SlotState.Full && slot.currentCard.heroInfo.type == DBHero.TYPE_GOD) {
//                                            if (j == 0 || j == 1) lane = 0;
//                                            else lane = 2;
//                                            break outer;
//                                        }
//
//                                    }
//                                int min = 0, max = 3;
//                                if (lane == 0) {
//                                    min = 0;
//                                    max = 1;
//
//                                } else {
//                                    min = 2;
//                                    max = 2;
//                                }
//
//                                int row = -1, col = -1;
//                                outer:
//                                for (int i = 0; i < MAX_ROW; i++)
//                                    for (int j = min; j <= max; j++) {
//                                        CardSlot slot = playerSlotMatrix[i][j];
//                                        if (slot.state == SlotState.Empty) {
//                                            row = i;
//                                            col = j;
//                                            break outer;
//                                        }
//                                    }
//                                if (row == -1 || col == -1) {
//                                    min = 2 - min;
//                                    max = 3 - max;
//                                    outer:
//                                    for (int i = 0; i < MAX_ROW; i++)
//                                        for (int j = min; j <= max; j++) {
//                                            CardSlot slot = playerSlotMatrix[i][j];
//                                            if (slot.state == SlotState.Empty) {
//                                                row = i;
//                                                col = j;
//                                                break outer;
//                                            }
//                                        }
//                                }
//                                if (row != -1 && col != -1){
//                                    SummonNormalInBattlePhase(selectCard, row, col);
//                                    Thread.sleep(2000);
//                                    instance.session2.GameConfirmStartBattle();
//                                    return;
//                                }
//                            }
//                        }
                        if(canSummonCardInGroup(3)){
                            int ValidAllyGod = -1;
                            List<Integer> cols = new ArrayList<>();
                            List<Integer> rows = new ArrayList<>();
                            //lấy danh sách vị trí trống
                            //duyệt tất cả vị trí trống cho nó nhanh
                            for(int j =0; j< MAX_COLUMN-1;j++){
                                for(int i =0; i< MAX_ROW;j++){
                                    if(playerSlotMatrix[i][j].state != SlotState.Full){
                                        cols.add(j);
                                        rows.add(i);
                                    }
                                }
                            }
                            //nếu mảng cols chỉ có 1 ptu thì triệu hồi vô đó
                            if(cols.size() == 1) {
                                summonCardInGroupWithMaxMana(6,cols.get(0), rows.get(0));
                            } else if (cols.size() >= 1){
                                //kiểm tra xem có tướng đồng minh ko để ưu tiên
                                if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1 )
                                    ValidAllyGod = 0;
                                else if (godCard.slot.yPos == 2)
                                    ValidAllyGod = 1;
                                if(ValidAllyGod == -1){
                                    int i = random.nextInt(cols.size());
                                    summonCardInGroupWithMaxMana(6,cols.get(i), rows.get(i));
                                } else {
                                    List<Integer> rowInLane = new ArrayList<>();
                                    for(int j = 0; j< cols.size(); j++){
                                        if (cols.get(j)==ValidAllyGod) {
                                            rowInLane.add(rows.get(j));
                                        }
                                    }
                                    if(rowInLane.size() > 0){
                                        summonCardInGroupWithMaxMana(6, rowInLane.get(random.nextInt(rowInLane.size())), cols.get(ValidAllyGod));
                                    }
                                }
                            }
                        }

                        //này cuũng dài nữa...
                        //kiểm tra số vị trí trống trong các lane cho các nhóm lính tiếp theo
                        int[] cardInLane = new int[2];
                        int validBlankInLane2 = 0;
                        cardInLane[0] = 0;
                        //này là kiểm tra xem lane 1 có bao nhiêu quân này
                        for (int i = 0; i < MAX_ROW; i++)
                            for (int j = 0; j <= 1; j++) {
                                CardSlot slot = playerSlotMatrix[i][j];
                                if (slot.state == SlotState.Full) cardInLane[0]++;

                            }
                        cardInLane[1] = 0;
                        //này là lane 2 có bao nhiêu quân này, và đếm luôn cột ngoài cùng có bao nhiêu card, vậy vị trí trống của cột 3 là sao nhỉ?  cột 4 ko tính nên lane 2 chỉ tính cột 3 sao?
                        for (int i = 0; i < MAX_ROW; i++)
                            for (int j = 2; j <= 3; j++) {
                                CardSlot slot = playerSlotMatrix[i][j];
                                if (slot.state == SlotState.Full) cardInLane[1]++;
                                else if (j == 2) validBlankInLane2 ++;
                            }

                        int min, max;
                        int row = -1, col = -1;
                        if(canSummonCardInGroup(4)){

                            //nếu lane 1  có 3 con này
                            if (cardInLane[0] == 3) {
                                //nếu lane 2 cũng có 3 lane và ở vị trí cột 3 không trống
                                if (cardInLane[1] == 3 && validBlankInLane2 > 0) {
                                    int value = random.nextInt(2);
                                    if (value == 0) {
                                        min = 0;
                                        max = 1;
                                    } else {
                                        min = 2;
                                        max = 2;
                                    }

                                    outer:
                                    for (int i = 0; i < MAX_ROW; i++)
                                        for (int j = min; j <= max; j++)
                                            if (playerSlotMatrix[i][j].state == SlotState.Empty) {
                                                row = i;
                                                col = j;
                                                break outer;
                                            }
                                    summonCardInGroupWithMaxMana(4,row, col);
                                } else {
                                    //trường hợp chỉ có lane 1 có 3 card và dư 1 vị trí trống
                                    min = 0;
                                    max = 1;
                                    outer:
                                    for (int i = 0; i < MAX_ROW; i++)
                                        for (int j = min; j <= max; j++)
                                            if (playerSlotMatrix[i][j].state == SlotState.Empty) {
                                                row = i;
                                                col = j;
                                                break outer;
                                            }
                                    summonCardInGroupWithMaxMana(4,row, col);
                                }
                            } else {
                                //không trường hợp chỉ có lane 2 có chỗ trống
                                if (cardInLane[1] == 3 && validBlankInLane2 > 0) {
                                    min = 2;
                                    max = 2;
                                    outer:
                                    for (int i = 0; i < MAX_ROW; i++)
                                        for (int j = min; j <= max; j++)
                                            if (playerSlotMatrix[i][j].state == SlotState.Empty) {
                                                row = i;
                                                col = j;
                                                break outer;
                                            }
                                    summonCardInGroupWithMaxMana(4,row, col);
                                } else {
                                    //trường hợp này là luồng không thỏa mãn có đường có 3 quân rồi========================================================
                                    //cả 2 lane đều ko trống thì ko làm gì cả
                                    if( cardInLane[0] != 4 && cardInLane[1] != 4) {
                                        if (cardInLane[0] == 4) {
                                            //nếu lane 1 full
                                            min = 2;
                                            max = 3;
                                        } else if (cardInLane[1] == 4) {
                                            //nếu lane 2 full
                                            min = 0;
                                            max = 1;
                                        } else {
                                            //nếu 2 lane chưa full
                                            //này là xem god ở vị trí nào để ưu tiên nếu 2 lane có số quân bằng nhau
                                            if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1) {
                                                min = 0;
                                                max = 1;
                                            } else {
                                                min = 2;
                                                max = 3;
                                            }
                                        }
                                        row = 0;
                                        for (int j = min; j <= max; j++)
                                            if (playerSlotMatrix[0][j].state == SlotState.Empty) {
                                                col = j;
                                                break;
                                            }
                                        //nếu cột có thần ko có chỗ trống ở hàng trước, thì gửi di chuyển hàng sau tới client
                                        if (col == -1) {
                                            col = random.nextInt(2);
                                            CommonVector cv = CommonVector.newBuilder()
                                                    .addALong(0)
                                                    .addALong(col)
                                                    .addALong(1).addALong(col).build();
                                            instance.session2.GameMoveCardInbattle(cv);
                                            Thread.sleep(2000);
                                        }
                                        summonCardInGroupWithMaxMana(4,row, col);
                                    }
                                }
                            }
                        }

                        //todo: bộ basic k có nhóm 2
                        if(canSummonCardInGroup(2))

                        //này dễ lỗi ở chỗ nếu 2 lane cùng full hoặc ko có godcard trên sân nè!
                        if(canSummonCardInGroup(16)){
                            int lane = -1;
                            //nếu lane 1 nhỏ hơn 3
                            if (cardInLane[0] < 3) {
                                //lane 2 cũng <3
                                if (cardInLane[1] < 3) {
                                    //xác định vị trí god ở lane nào
                                    if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1 ) {
                                        lane = 1;
                                    } else lane = 0;

                                } else {
                                    lane = 0;
                                }
                            } else {
                                if (cardInLane[2] < 3) {
                                    lane = 2;
                                }
                            }
                            //nếu có lane nào <3 card
                            if (lane != -1) {
                                if (lane == 0) {
                                    min = 0;
                                    max = 1;
                                } else {
                                    min = 2;
                                    max = 3;
                                }
                                for (int j = min; j <= max; j++) {
                                    if (playerSlotMatrix[0][j].state == SlotState.Full) {
                                        for (int k = min; k <= max; k++) {
                                            if (playerSlotMatrix[1][k].state == SlotState.Empty) {
                                                CommonVector cv = CommonVector.newBuilder()
                                                        .addALong(0)
                                                        .addALong(j)
                                                        .addALong(1)
                                                        .addALong(k).build();
                                                instance.session2.GameMoveCardInbattle(cv);
                                                Thread.sleep(2000);
                                                break;
                                            }
                                        }
                                    }
                                }
                                summonCardInGroupWithMaxMana(16,0,min);
                                Thread.sleep(2000);
                                instance.session2.GameConfirmStartBattle();
                                return;

                            }
                        }

                        //gì đó, cuối cùng khi ko có các nhóm tướng cần có nữa

                        HandCard selectCard = null;
                        long maxMana = -1;
                        for (HandCard handCard : Decks[0].GetListCard()) {
                            if (handCard.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(handCard)) {
                                if (handCard.tmpMana > maxMana) {
                                    maxMana = handCard.tmpMana;
                                    selectCard = handCard;
                                }
                            }
                        }
                        if (selectCard != null) {
                            int ValidAllyGod = -1;
                            List<Integer> cols = new ArrayList<>();
                            List<Integer> rows = new ArrayList<>();
                            //lấy danh sách vị trí trống
                            //duyệt tất cả vị trí trống cho nó nhanh
                            for(int j =0; j< MAX_COLUMN-1;j++){
                                for(int i =0; i< MAX_ROW;j++){
                                    if(playerSlotMatrix[i][j].state != SlotState.Full){
                                        cols.add(j);
                                        rows.add(i);
                                    }
                                }
                            }
                            //nếu mảng cols chỉ có 1 ptu thì triệu hồi vô đó
                            if(cols.size() == 1) {
                                summonCardInGroupWithMaxMana(6,cols.get(0), rows.get(0));
                            } else if (cols.size() >= 1){
                                //kiểm tra xem có tướng đồng minh ko để ưu tiên
                                if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1 )
                                    ValidAllyGod = 0;
                                else if (godCard.slot.yPos == 2)
                                    ValidAllyGod = 1;
                                //nếu ko có thần đồng minh random đê
                                if(ValidAllyGod == -1){
                                    int i = random.nextInt(rows.size());
                                    SummonNormalInBattlePhase(selectCard, rows.get(i), cols.get(i) );
                                } else {
                                    List<Integer> rowInLane = new ArrayList<>();
                                    for(int j = 0; j< cols.size(); j++){
                                        if (cols.get(j)==ValidAllyGod) {
                                            rowInLane.add(rows.get(j));
                                        }
                                    }
                                    if(rowInLane.size() > 0){
                                        SummonNormalInBattlePhase(selectCard, rowInLane.get(random.nextInt(rowInLane.size())), cols.get(ValidAllyGod));
                                    }

                                }
                            }
                        }

                        //todo: basic k có nhóm 7 => k check nhom 9

//                        if(!canSummonCardInGroup(18)){
//                            List<HandCard> cards = normalGroup(18);
//                            HandCard selectCard = null;
//                            long maxMana = -1;
//                            int[] cardInLane = new int[3];
//                            for (HandCard card : cards) {
//                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                    if (card.tmpMana > maxMana) {
//                                        maxMana = card.tmpMana;
//                                        selectCard = card;
//                                    }
//                                }
//                            }
//                            int godLane;
//
//                            if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1) {
//                                godLane = 0;
//                            } else {
//                                godLane = 2;
//                            }
//                            if (selectCard != null) {
//                                if (cardInLane[godLane] <= 3) {
//                                    if (godCard.slot.xPos == 0) {
//                                        CommonVector cv = CommonVector.newBuilder()
//                                                .addALong(0)
//                                                .addALong(godCard.slot.yPos)
//                                                .addALong(1)
//                                                .addALong(godCard.slot.yPos).build();
//                                        instance.session2.GameMoveCardInbattle(cv);
//                                        Thread.sleep(2000);
//                                    }
//                                    if (playerSlotMatrix[0][godCard.slot.yPos].state == SlotState.Full) {
//                                        int nearByCol = godLane + (1 - godCard.slot.yPos);
//                                        int row = -1;
//                                        for (int i = 0; i < MAX_ROW; i++)
//                                            if (playerSlotMatrix[0][nearByCol].state == SlotState.Empty) {
//                                                row = i;
//                                                break;
//                                            }
//                                        CommonVector cv = CommonVector.newBuilder()
//                                                .addALong(0)
//                                                .addALong(godCard.slot.yPos)
//                                                .addALong(row)
//                                                .addALong(nearByCol).build();
//                                        instance.session2.GameMoveCardInbattle(cv);
//                                        Thread.sleep(2000);
//                                    }
//                                    SummonNormalInBattlePhase(selectCard, 0, godCard.slot.yPos);
//
//
//                                } else {
//                                    int min, max;
//                                    if (godLane == 0) {
//                                        min = 2;
//                                        max = 3;
//                                    } else {
//                                        min = 0;
//                                        max = 1;
//                                    }
//                                    int row = -1, col = -1;
//                                    for (int i = 0; i < MAX_ROW; i++)
//                                        for (int j = min; j <= max; j++)
//                                            if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                                row = i;
//                                                col = j;
//                                                break;
//                                            }
//                                    SummonNormalInBattlePhase(selectCard, 0, godCard.slot.yPos);
//
//
//                                }
//                                Thread.sleep(2000);
//                                instance.session2.GameConfirmStartBattle();
//                                return;
//                            }
//                            selectCard = null;
//                            maxMana = -1;
//                            for (HandCard card : Decks[0].GetListCard()) {
//                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                    if (card.tmpMana > maxMana) {
//                                        maxMana = card.tmpMana;
//                                        selectCard = card;
//                                    }
//                                }
//                            }
//                            if (selectCard != null) {
//                                int min, max;
//                                if (cardInLane[godLane] <= 3) {
//                                    min = godLane;
//                                    max = godLane + 1;
//                                } else {
//                                    min = 2 - godLane;
//                                    max = 2 - godLane + 1;
//                                }
//                                List<Integer> lstRow = new ArrayList<>();
//                                List<Integer> lstCol = new ArrayList<>();
//                                for (int i = 0; i < MAX_ROW; i++)
//                                    for (int j = min; j <= max; j++)
//                                        if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                            lstRow.add(i);
//                                            lstCol.add(j);
//                                            break;
//                                        }
//                                int row = lstRow.get(random.nextInt(lstRow.size()));
//                                int col = lstCol.get(random.nextInt(lstCol.size()));
//                                SummonNormalInBattlePhase(selectCard, row, col);
//                                Thread.sleep(2000);
//                                instance.session2.GameConfirmStartBattle();
//                                return;
//                            }
//                        }
//
                        break;

                    }
                    // đánh phép xuống
                    case 1: {
                        List<HandCard> handCards = new ArrayList<>();
                        for (HandCard handCard : Decks[0].GetListCard()) {
                            if (handCard.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC && CanSummon(handCard)) {
                                handCards.add(handCard);
                            }
                        }
                        if (handCards.size() == 0) {
                            break;
                        }
                        outer:
                        for (int i = 0; i < prioritySpellArr.size(); i++) {
                            JSONArray arr = (JSONArray) prioritySpellArr.get(i);
                            for (HandCard card : handCards) {
                                long heroId = card.heroID;
                                if (arr.contains(heroId)) {
                                    if (SummonSpellInBattlePhase(card)) {
                                        Thread.sleep(2000);
                                        if (success) {
                                            instance.session2.GameConfirmStartBattle();
                                            return;
                                        }
                                    }
                                }
                            }

                        }
                        break;
                    }
                    //dùng skill thần
                }

//                //nếu ko có chỗ trống trên sân
//                if (count == 0) {
//                    if (playerGodDeck.size() == 0) {
//                        instance.session2.GameConfirmStartBattle();
//                        return;
//                    }
//                    long maxMana = 0;
//                    int index = -1;
//                    //này là lấy index và maxmana của tướng có nhiều mana nhất trong bộ
//                    for (int i = 0; i < playerGodDeck.size(); i++) {
//                        DBHero god = playerGodDeck.get(i).hero;
//                        if (god.mana <= currentMana && god.mana > maxMana) {
//                            maxMana = god.mana;
//                            index = i;
//                        }
//                    }
//                    long row = -1, col = -1;
//                    //lấy ra tướng được chọn
//                    GodCardUI godCardSelected = playerGodDeck.get(index);
//
//                    //đây là gì nhỉ? chọn mấy con hero chỉ hợp hàng sau
//                    if (godCardSelected.hero.id == 347 || godCardSelected.hero.id == 349 || godCardSelected.hero.id == 350 || godCardSelected.hero.id == 351)
//                        row = 0;
//                    else row = 1;
//                    List<Integer> lstIndex = new ArrayList<>();
//
//                    //lấy ra danh sách các slot trống ư? với đk là slot đó ở row tương ứng vs vị trí của thần và tình trạng là empty, sau đó lưu y vào
//                    for (CardSlot slot : playerSlotContainer) {
//                        if (slot.xPos == row && slot.state == SlotState.Empty) {
//                            if (slot.yPos != 3) lstIndex.add(slot.yPos);
//                        }
//                    }
//                    //nếu ko có vị trí trống, thì chuyển sang row còn lại và duyệt chăng?
//                    if (lstIndex.size() == 0) {
//                        row = 1 - row;
//                        for (CardSlot slot : playerSlotContainer) {
//                            if (slot.xPos == row && slot.state == SlotState.Empty) {
//                                if (slot.yPos != 3) lstIndex.add(slot.yPos);
//                            }
//                        }
//                    }
//                    //vẫn ko có thì thôi, chơi game
//                    if (lstIndex.size() == 0){
//                        instance.session2.GameConfirmStartBattle();
//                        return;
//                    }
//
//                    //lấy bừa 1 vị trí trong số các vị trí trống
//                    col = lstIndex.get(random.nextInt(lstIndex.size()));
//
//                    //triệu hồi vô đó
//                    SummonGodInBattlePhase(godCardSelected, row, col);
//                    Thread.sleep(2000);
//                }
//
//                //này là gì? lấy god card hiện tại à?
//                BoardCard godCard = null;
//                for (int i = 0; i < playerSlotContainer.size(); i++) {
//                    CardSlot slot = playerSlotContainer.get(i);
//                    if (slot.state == SlotState.Full) {
//                        Card card = slot.currentCard;
//                        if (card.heroInfo.type == DBHero.TYPE_GOD) {
//                            godCard = (BoardCard) card;
//                            break;
//                        }
//                    }
//                }
//                //này cần sửa thành max mana để unlock skill, chắc cần sửa ở đoạn này
//                long maxShardNeed = godCard.heroInfo.maxShardUnlockSkill;
//                for (HandCard handCard : Decks[0].GetListCard()) {
//                    maxShardNeed = Math.max(maxShardNeed, handCard.heroInfo.shardRequired);
//                }
//
//                //nay là nếu đủ đk để gọi shard, thì thêm vào, sửa thành mana ko biết sẽ ntn, chắc sẽ giống kỹ năng bt
//                while (currentShard > 0 && godCard.countShardAddded < maxShardNeed) {
//                    AddShard(godCard);
//                    Thread.sleep(2000);
//                }
//
//                // này là do currentShard =0 rồi nhưng vế còn lại vẫn thỏa mãn nên sửa sao? dùng mana nên chắc sẽ khác
//                if (godCard.countShardAddded < maxShardNeed) {
//                    GetShard();
//                    Thread.sleep(2000);
//                    AddShard(godCard);
//                    Thread.sleep(2000);
//                }
//                // nếu chẳng còn mana nào thì bắt đầu game thôi
//                if (currentMana <= 0) {
//                    instance.session2.GameConfirmStartBattle();
//                    return;
//                }
//                //này là lại kiểm tra xem còn chỗ trống ko này
//                boolean isFull = true;
//                for (int i = 0; i < playerSlotContainer.size(); i++) {
//                    CardSlot slot = playerSlotContainer.get(i);
//                    if (slot.state == SlotState.Empty) {
//                        isFull = false;
//                        break;
//                    }
//                }
//                //success là biến thành công summon lên sân?
//                success = false;
//                //nếu hết chõ trống
//                if (isFull) {
//                    int[] weight = {5, 50, 30, 15}; //???
//                    int n = weight.length;
//                    int[] sum = new int[n];
//                    sum[0] = weight[0];
//                    for (int i = 1; i < n; i++)
//                        sum[i] = sum[i - 1] + weight[i]; //5, 55, 85, 100??? làm gì ta?
//                    int rmd = random.nextInt(sum[n - 1]); //random trong 0-100? làm gì?
//                    //đánh dấu hành động là 0?
//                    int indexAction = 0;
//                    //này là gì đây? chạy từ 0 đến 4 để làm gì? tìm ra hành động có indexAction =i?
//                    for (int i = 0; i < n; i++)
//                        if (sum[i] > rmd) {
//                            indexAction = i;
//                            break;
//                        }
//                    //này là sao? sao chỉ chạy với indexAction = 1?
//                    switch (indexAction) {
//                            //0 và 2 là summon lính và đánh phép xuống, loại bỏ do hết chỗ ròi
//                        case 0:
//                        case 2: {
//                            break;
//                        }
//                        //1 là dùng skill thần
//                        case 1: {
//                            //này là lấy các thể trên tay, nếu nó là luồng chơi phép riêng?
//                            List<HandCard> handCards = new ArrayList<>();
//                            for (HandCard handCard : Decks[0].GetListCard()) {
//                                if (handCard.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC && CanSummon(handCard)) {
//                                    handCards.add(handCard);
//                                }
//                            }
//                            if (handCards.size() == 0) {
//                                break;
//                            }
//                            outer: //thoát khỏi nhiều vòng lặp trồng lên nhau
//                            //lặp qua những bài phép chăng? này chắc chắn là bài phép ưu tiên, chấc luồng chơi phép riêng, mà sao đây lại lỗi nhỉ?
//                            for (int i = 0; i < prioritySpellArr.size(); i++) {
//                                JSONArray arr = (JSONArray) prioritySpellArr.get(i);
//                                for (HandCard card : handCards) {
//                                    long heroId = card.heroID;
//                                    if (arr.contains(heroId)) {
//                                        if (SummonSpellInBattlePhase(card)) {
//                                            Thread.sleep(2000);
//                                            if (success) break outer;
//                                        }
//                                    }
//                                }
//
//                            }
//
//                            break;
//                        }
//                        //dùng ulti thần
//                        default: {
//                            //này là gì? mặc định sẽ là chơi buff cho tương đó ấy à? nhìn có vẻ giống, hay là ulti thần ha?
//                            boolean haveActiveSkill = false;
//                            for (DBHeroSkill skill : godCard.lstSkill) {
//                                if (skill.skill_type == 1 && !skill.isUltiType) {
//                                    godCard.OnActiveSkill(skill);
//                                    haveActiveSkill = true;
//                                    break;
//                                }
//                            }
//                            if (haveActiveSkill) {
//                                boolean ok = DoActiveSkill(godCard);
//                                if (ok) {
//                                    Thread.sleep(2000);
//                                }
//                            }
//                            break;
//                        }
//
//                    }
//                    instance.session2.GameConfirmStartBattle();
//                } else {
//                    //nếu có chỗ trống??? lại bộ khỉ ho cò gái gì đây?
//                    int[] weight = {50, 30, 15, 5};
//                    int n = weight.length;
//                    int[] sum = new int[n];
//                    int indexAction = 0;
//                    while (!success) {
//                        sum[0] = weight[0];
//                        for (int i = 1; i < n; i++)
//                            sum[i] = sum[i - 1] + weight[i];
//                        if (sum[n - 1] <= 0) {
//                            instance.session2.GameConfirmStartBattle();
//                            return;
//                        }
//                        int rmd = random.nextInt(sum[n - 1]);
//                        for (int i = 0; i < n; i++)
//                            if (sum[i] > rmd) {
//                                indexAction = i;
//                                break;
//                            }
//                        switch (indexAction) {
//                                //summon lính
//                            case 0: {
//                                List<HandCard> canCummonCards = new ArrayList<>();
//                                for (HandCard card : Decks[0].GetListCard()) {
//                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card))
//                                        canCummonCards.add(card);
//                                }
//                                if (canCummonCards.size() == 0) {
//                                    weight[0] = 0;
//                                    break;
//                                }
//
//                                CardSlot enemySlotMatrix[][] = new CardSlot[2][4];
//                                CardSlot playerSlotMatrix[][] = new CardSlot[2][4];
//                                for (CardSlot slot : enemySlotContainer) {
//                                    enemySlotMatrix[slot.xPos][slot.yPos] = slot;
//                                }
//                                for (CardSlot slot : playerSlotContainer) {
//                                    playerSlotMatrix[slot.xPos][slot.yPos] = slot;
//                                }
//                                List<Integer> lstValidEmptyCol = new ArrayList<>();
//                                for (int j = 0; j < MAX_COLUMN; j++) {
//                                    int enemyCardCount = 0;
//                                    int playerCardCount = 0;
//                                    for (int i = 0; i < MAX_ROW; i++) {
//                                        if (enemySlotMatrix[i][j].state != SlotState.Empty) enemyCardCount++;
//                                    }
//                                    for (int i = 0; i < MAX_ROW; i++) {
//                                        if (playerSlotMatrix[i][j].state != SlotState.Empty) playerCardCount++;
//                                    }
//                                    if (j < MAX_COLUMN - 1 && enemyCardCount == 0 && playerCardCount < MAX_ROW) {
//                                        lstValidEmptyCol.add(j);
//
//                                    }
//                                }
//                                if (lstValidEmptyCol.size() > 0) {
//                                    int col = lstValidEmptyCol.get(random.nextInt(lstValidEmptyCol.size()));
//                                    int row;
//                                    if (playerSlotMatrix[0][col].state == SlotState.Empty) {
//                                        if (playerSlotMatrix[1][col].state == SlotState.Empty) {
//                                            int k = random.nextInt(100);
//                                            if (k < 80) row = 0;
//                                            else row = 1;
//                                        } else {
//                                            row = 0;
//                                        }
//                                    } else {
//                                        row = 1;
//                                    }
//
//                                    if (summonCardInGroup(1, row, col)) {
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                    // chưa có
//                                    if (summonCardInGroup(2, row, col)) {
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//
//                                }
//                                List<Integer> lstValidEnemyGodCol = new ArrayList<>();
//                                for (int j = 0; j < MAX_COLUMN - 1; j++) {
//                                    boolean full = playerSlotMatrix[0][j].state != SlotState.Empty && playerSlotMatrix[1][j].state != SlotState.Empty;
//                                    if (!full) {
//                                        if ((enemySlotMatrix[0][j].state == SlotState.Full && enemySlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_GOD)
//                                                || (enemySlotMatrix[1][j].state == SlotState.Full && enemySlotMatrix[1][j].currentCard.heroInfo.type == DBHero.TYPE_GOD && enemySlotMatrix[0][j].state == SlotState.Empty)) {
//                                            lstValidEnemyGodCol.add(j);
//                                        }
//
//                                    }
//                                }
//                                if (lstValidEnemyGodCol.size() > 0) {
//                                    int col = lstValidEnemyGodCol.get(random.nextInt(lstValidEnemyGodCol.size()));
//                                    int row;
//                                    if (playerSlotMatrix[0][col].state == SlotState.Empty) {
//                                        if (playerSlotMatrix[1][col].state == SlotState.Empty) {
//                                            int k = random.nextInt(100);
//                                            if (k < 80) row = 0;
//                                            else row = 1;
//                                        } else {
//                                            row = 0;
//                                        }
//                                    } else {
//                                        row = 1;
//                                    }
//                                    if (summonCardInGroup(10, row, col)) {
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                    if (summonCardInGroup(5, row, col)) {
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                    //chưa có
//                                    if (summonCardInGroup(2, row, col)) {
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                    // chưa có
//                                    if (summonCardInGroup(6, row, col)) {
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                    long maxAtk = -1;
//                                    HandCard selectCard = null;
//                                    for (HandCard card : Decks[0].GetListCard()) {
//                                        if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                            if (card.heroInfo.atk > maxAtk) {
//                                                maxAtk = card.heroInfo.atk;
//                                                selectCard = card;
//                                            }
//                                        }
//                                    }
//                                    SummonNormalInBattlePhase(selectCard, row, col);
//                                    Thread.sleep(2000);
//                                    instance.session2.GameConfirmStartBattle();
//                                    return;
//                                }
////                                else {
//                                List<Integer> lstValidEnemyHpCol = new ArrayList<>();
//                                for (int j = 0; j < MAX_COLUMN - 1; j++) {
//                                    boolean full = playerSlotMatrix[0][j].state != SlotState.Empty && playerSlotMatrix[1][j].state != SlotState.Empty;
//                                    if (!full) {
//                                        if (enemySlotMatrix[0][j].state == SlotState.Full && enemySlotMatrix[0][j].currentCard.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && ((BoardCard) enemySlotMatrix[0][j].currentCard).hpValue < 3
//                                        ) {
//                                            lstValidEnemyHpCol.add(j);
//                                        }
//
//                                    }
//                                }
//                                if (lstValidEnemyHpCol.size() > 0) {
//                                    int col = lstValidEnemyHpCol.get(random.nextInt(lstValidEnemyHpCol.size()));
//                                    int row;
//                                    if (canSummonCardInGroup(13) || canSummonCardInGroup(5) || canSummonCardInGroup(6)) {
//                                        if (playerSlotMatrix[0][col].state == SlotState.Empty) {
//                                            row = 0;
//                                        } else {
//                                            CommonVector cv = CommonVector.newBuilder()
//                                                    .addALong(0)
//                                                    .addALong(col)
//                                                    .addALong(1).addALong(col).build();
//                                            instance.session2.GameMoveCardInbattle(cv);
//                                            Thread.sleep(2000);
//                                            row = 1;
//                                        }
//                                        if (summonCardInGroup(13, row, col)) {
//                                            Thread.sleep(2000);
//                                            instance.session2.GameConfirmStartBattle();
//                                            return;
//                                        }
//                                        if (summonCardInGroup(5, row, col)) {
//                                            Thread.sleep(2000);
//                                            instance.session2.GameConfirmStartBattle();
//                                            return;
//                                        }
//                                        //chưa có
//                                        summonCardInGroup(6, row, col);
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                }
//                                // chưa có
//                                if (canSummonCardInGroup(15)) {
//                                    // todo : nếu bộ bài khác có group lính 15
//                                }
//                                // chưa có
//                                if (canSummonCardInGroup(8)) {
//                                    // todo : nếu bộ bài khác có group lính 8
//                                }
//
//                                // chưa có
//                                if (canSummonCardInGroup(6)) {
//                                    // todo : nếu bộ bài khác có group lính 6
//                                }
//
//
//                                List<HandCard> cards = normalGroup(3);
//                                HandCard selectCard = null;
//                                long maxMana = -1;
//                                for (HandCard card : cards) {
//                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                        if (card.tmpMana > maxMana) {
//                                            maxMana = card.tmpMana;
//                                            selectCard = card;
//                                        }
//                                    }
//                                }
//                                if (selectCard != null) {
//                                    int lane = -1;
//                                    outer:
//                                    for (int i = 0; i < MAX_ROW; i++)
//                                        for (int j = 0; j < MAX_COLUMN; j++) {
//                                            CardSlot slot = playerSlotMatrix[i][j];
//                                            if (slot.state == SlotState.Full && slot.currentCard.heroInfo.type == DBHero.TYPE_GOD) {
//                                                if (j == 0 || j == 1) lane = 0;
//                                                else lane = 2;
//                                                break outer;
//                                            }
//
//                                        }
//                                    int min = 0, max = 3;
//                                    if (lane == 0) {
//                                        min = 0;
//                                        max = 1;
//
//                                    } else {
//                                        min = 2;
//                                        max = 2;
//                                    }
//
//                                    int row = -1, col = -1;
//                                    outer:
//                                    for (int i = 0; i < MAX_ROW; i++)
//                                        for (int j = min; j <= max; j++) {
//                                            CardSlot slot = playerSlotMatrix[i][j];
//                                            if (slot.state == SlotState.Empty) {
//                                                row = i;
//                                                col = j;
//                                                break outer;
//                                            }
//                                        }
//                                    if (row == -1 || col == -1) {
//                                        min = 2 - min;
//                                        max = 3 - max;
//                                        outer:
//                                        for (int i = 0; i < MAX_ROW; i++)
//                                            for (int j = min; j <= max; j++) {
//                                                CardSlot slot = playerSlotMatrix[i][j];
//                                                if (slot.state == SlotState.Empty) {
//                                                    row = i;
//                                                    col = j;
//                                                    break outer;
//                                                }
//                                            }
//                                    }
//                                    if (row != -1 && col != -1){
//                                        SummonNormalInBattlePhase(selectCard, row, col);
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//                                    }
//                                }
//                                int[] cardInLane = new int[3];
//                                int validBlankInLane2 = 0;
//                                cardInLane[0] = 0;
//                                for (int i = 0; i < MAX_ROW; i++)
//                                    for (int j = 0; j <= 1; j++) {
//                                        CardSlot slot = playerSlotMatrix[i][j];
//                                        if (slot.state == SlotState.Full) cardInLane[0]++;
//
//                                    }
//                                cardInLane[2] = 0;
//                                for (int i = 0; i < MAX_ROW; i++)
//                                    for (int j = 2; j <= 3; j++) {
//                                        CardSlot slot = playerSlotMatrix[i][j];
//                                        if (slot.state == SlotState.Full) cardInLane[2]++;
//                                        else if (j == 2) validBlankInLane2 ++;
//                                    }
//                                cards = normalGroup(4);
//                                selectCard = null;
//                                maxMana = -1;
//                                for (HandCard card : cards) {
//                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                        if (card.tmpMana > maxMana) {
//                                            maxMana = card.tmpMana;
//                                            selectCard = card;
//                                        }
//                                    }
//                                }
//                                if (selectCard != null) {
//                                    int min, max;
//                                    int row = -1, col = -1;
//
//                                    if (cardInLane[0] == 3) {
//                                            if (cardInLane[2] == 3 && validBlankInLane2 > 0) {
//                                                TowerController tower0 = getAllyTowerByLane(0);
//                                                TowerController tower2 = getAllyTowerByLane(2);
//                                                if (tower0.towerHealth >= tower2.towerHealth) {
//                                                    min = 0;
//                                                    max = 1;
//                                                } else {
//                                                    min = 2;
//                                                    max = 2;
//                                                }
//
//                                                outer:
//                                                for (int i = 0; i < MAX_ROW; i++)
//                                                    for (int j = min; j <= max; j++)
//                                                        if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                                            row = i;
//                                                            col = j;
//                                                            break outer;
//                                                        }
//                                                SummonNormalInBattlePhase(selectCard, row, col);
//                                            } else {
//                                                min = 0;
//                                                max = 1;
//                                                outer:
//                                                for (int i = 0; i < MAX_ROW; i++)
//                                                    for (int j = min; j <= max; j++)
//                                                        if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                                            row = i;
//                                                            col = j;
//                                                            break outer;
//                                                        }
//                                                SummonNormalInBattlePhase(selectCard, row, col);
//                                            }
//                                    } else {
//                                        if (cardInLane[2] == 3 && validBlankInLane2 > 0) {
//                                            min = 2;
//                                            max = 2;
//                                            outer:
//                                            for (int i = 0; i < MAX_ROW; i++)
//                                                for (int j = min; j <= max; j++)
//                                                    if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                                        row = i;
//                                                        col = j;
//                                                        break outer;
//                                                    }
//                                            SummonNormalInBattlePhase(selectCard, row, col);
//                                        } else {
//                                            if (cardInLane[0] == 4) {
//                                                min = 2;
//                                                max = 3;
//                                            } else if (cardInLane[2] == 4) {
//                                                min = 0;
//                                                max = 1;
//                                            } else {
//                                                if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1) {
//                                                    min = 0;
//                                                    max = 1;
//                                                } else {
//                                                    min = 2;
//                                                    max = 3;
//                                                }
//                                            }
//                                            row = 0;
//                                            for (int j = min; j <= max; j++)
//                                                if (playerSlotMatrix[0][j].state == SlotState.Empty) {
//                                                    col = j;
//                                                    break;
//                                                }
//                                            if (col == -1) {
//                                                col = random.nextInt(2);
//                                                CommonVector cv = CommonVector.newBuilder()
//                                                        .addALong(0)
//                                                        .addALong(col)
//                                                        .addALong(1).addALong(col).build();
//                                                instance.session2.GameMoveCardInbattle(cv);
//                                                Thread.sleep(2000);
//                                            }
//                                            SummonNormalInBattlePhase(selectCard, row, col);
//                                        }
//                                    }
//                                    Thread.sleep(2000);
//                                    instance.session2.GameConfirmStartBattle();
//                                    return;
//                                }
//                                //todo: bộ basic k có nhóm 2
//                                cards = normalGroup(16);
//                                selectCard = null;
//                                maxMana = -1;
//                                for (HandCard card : cards) {
//                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                        if (card.tmpMana > maxMana) {
//                                            maxMana = card.tmpMana;
//                                            selectCard = card;
//                                        }
//                                    }
//                                }
//                                if (selectCard != null) {
//                                    int lane = -1;
//                                    if (cardInLane[0] < 3) {
//                                        if (cardInLane[2] < 3) {
//                                            if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1) {
//                                                lane = 2;
//                                            } else lane = 0;
//
//                                        } else {
//                                            lane = 0;
//                                        }
//                                    } else {
//                                        if (cardInLane[2] < 3) {
//                                            lane = 2;
//                                        }
//                                    }
//                                    if (lane != -1) {
//                                        int min, max;
//                                        if (lane == 0) {
//                                            min = 0;
//                                            max = 1;
//                                        } else {
//                                            min = 2;
//                                            max = 3;
//                                        }
//                                        for (int j = min; j <= max; j++) {
//                                            if (playerSlotMatrix[0][j].state == SlotState.Full) {
//                                                for (int k = min; k <= max; k++) {
//                                                    if (playerSlotMatrix[1][k].state == SlotState.Empty) {
//                                                        CommonVector cv = CommonVector.newBuilder()
//                                                                .addALong(0)
//                                                                .addALong(j)
//                                                                .addALong(1)
//                                                                .addALong(k).build();
//                                                        instance.session2.GameMoveCardInbattle(cv);
//                                                        Thread.sleep(2000);
//                                                        break;
//                                                    }
//                                                }
//
//                                            }
//                                        }
//                                        SummonNormalInBattlePhase(selectCard, 0, min);
//                                        Thread.sleep(2000);
//                                        instance.session2.GameConfirmStartBattle();
//                                        return;
//
//                                    }
//                                }
//                                //todo: basic k có nhóm 7 => k check nhom 9
//                                cards = normalGroup(18);
//                                selectCard = null;
//                                maxMana = -1;
//                                for (HandCard card : cards) {
//                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                        if (card.tmpMana > maxMana) {
//                                            maxMana = card.tmpMana;
//                                            selectCard = card;
//                                        }
//                                    }
//                                }
//                                int godLane;
//
//                                if (0 <= godCard.slot.yPos && godCard.slot.yPos <= 1) {
//                                    godLane = 0;
//                                } else {
//                                    godLane = 2;
//                                }
//                                if (selectCard != null) {
//                                    if (cardInLane[godLane] <= 3) {
//                                        if (godCard.slot.xPos == 0) {
//                                            CommonVector cv = CommonVector.newBuilder()
//                                                    .addALong(0)
//                                                    .addALong(godCard.slot.yPos)
//                                                    .addALong(1)
//                                                    .addALong(godCard.slot.yPos).build();
//                                            instance.session2.GameMoveCardInbattle(cv);
//                                            Thread.sleep(2000);
//                                        }
//                                        if (playerSlotMatrix[0][godCard.slot.yPos].state == SlotState.Full) {
//                                            int nearByCol = godLane + (1 - godCard.slot.yPos);
//                                            int row = -1;
//                                            for (int i = 0; i < MAX_ROW; i++)
//                                                if (playerSlotMatrix[0][nearByCol].state == SlotState.Empty) {
//                                                    row = i;
//                                                    break;
//                                                }
//                                            CommonVector cv = CommonVector.newBuilder()
//                                                    .addALong(0)
//                                                    .addALong(godCard.slot.yPos)
//                                                    .addALong(row)
//                                                    .addALong(nearByCol).build();
//                                            instance.session2.GameMoveCardInbattle(cv);
//                                            Thread.sleep(2000);
//                                        }
//                                        SummonNormalInBattlePhase(selectCard, 0, godCard.slot.yPos);
//
//
//                                    } else {
//                                        int min, max;
//                                        if (godLane == 0) {
//                                            min = 2;
//                                            max = 3;
//                                        } else {
//                                            min = 0;
//                                            max = 1;
//                                        }
//                                        int row = -1, col = -1;
//                                        for (int i = 0; i < MAX_ROW; i++)
//                                            for (int j = min; j <= max; j++)
//                                                if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                                    row = i;
//                                                    col = j;
//                                                    break;
//                                                }
//                                        SummonNormalInBattlePhase(selectCard, 0, godCard.slot.yPos);
//
//
//                                    }
//                                    Thread.sleep(2000);
//                                    instance.session2.GameConfirmStartBattle();
//                                    return;
//                                }
//                                selectCard = null;
//                                maxMana = -1;
//                                for (HandCard card : Decks[0].GetListCard()) {
//                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
//                                        if (card.tmpMana > maxMana) {
//                                            maxMana = card.tmpMana;
//                                            selectCard = card;
//                                        }
//                                    }
//                                }
//                                if (selectCard != null) {
//                                    int min, max;
//                                    if (cardInLane[godLane] <= 3) {
//                                        min = godLane;
//                                        max = godLane + 1;
//                                    } else {
//                                        min = 2 - godLane;
//                                        max = 2 - godLane + 1;
//                                    }
//                                    List<Integer> lstRow = new ArrayList<>();
//                                    List<Integer> lstCol = new ArrayList<>();
//                                    for (int i = 0; i < MAX_ROW; i++)
//                                        for (int j = min; j <= max; j++)
//                                            if (playerSlotMatrix[i][j].state == SlotState.Empty) {
//                                                lstRow.add(i);
//                                                lstCol.add(j);
//                                                break;
//                                            }
//                                    int row = lstRow.get(random.nextInt(lstRow.size()));
//                                    int col = lstCol.get(random.nextInt(lstCol.size()));
//                                    SummonNormalInBattlePhase(selectCard, row, col);
//                                    Thread.sleep(2000);
//                                    instance.session2.GameConfirmStartBattle();
//                                    return;
//                                }
//                                weight[0] = 0;
//                                break;
//
//
////                                }
//
//
//                            }
//                            // đánh phép xuống
//                            case 1: {
//                                List<HandCard> handCards = new ArrayList<>();
//                                for (HandCard handCard : Decks[0].GetListCard()) {
//                                    if (handCard.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC && CanSummon(handCard)) {
//                                        handCards.add(handCard);
//                                    }
//                                }
//                                if (handCards.size() == 0) {
//                                    weight[1] = 0;
//                                    break;
//                                }
//                                outer:
//                                for (int i = 0; i < prioritySpellArr.size(); i++) {
//                                    JSONArray arr = (JSONArray) prioritySpellArr.get(i);
//                                    for (HandCard card : handCards) {
//                                        long heroId = card.heroID;
//                                        if (arr.contains(heroId)) {
//                                            if (SummonSpellInBattlePhase(card)) {
//                                                Thread.sleep(2000);
//                                                if (success) {
//                                                    instance.session2.GameConfirmStartBattle();
//                                                    return;
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                }
//
//
//                                weight[1] = 0;
//                                break;
//                            }
//                            //dùng skill thần
//                            case 2: {
//                                weight[2] = 0;
//                                break;
//                            }
//                            //dùng ulti thần
//                            default: {
//                                boolean haveActiveSkill = false;
//                                for (DBHeroSkill skill : godCard.lstSkill) {
//                                    if (skill.skill_type == 1 && !skill.isUltiType) {
//
//                                        godCard.OnActiveSkill(skill);
//                                        haveActiveSkill = true;
//                                        break;
//                                    }
//                                }
//                                if (haveActiveSkill) {
//                                    boolean ok = DoActiveSkill(godCard);
//                                    if (ok) {
//                                        Thread.sleep(2000);
//                                        if (success) {
//                                            instance.session2.GameConfirmStartBattle();
//                                            return;
//                                        }
//                                    }
//                                }
//                                weight[n - 1] = 0;
//                                break;
//                            }
//                        }
//                    }
//                    instance.session2.GameConfirmStartBattle();
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SummonGodInBattlePhase(GodCardUI godCardSelected, long row, long col) {
        CommonVector.Builder builder = CommonVector.newBuilder()
                .addALong(godCardSelected.battleId)
                .addALong(row)
                .addALong(col);
        for (DBHeroSkill skill : godCardSelected.hero.lstHeroSkill) {
            if (skill.skill_type == DBHeroSkill.TYPE_SUMMON_SKILL) {
                builder.addALong(skill.id);
            }
        }
        instance.session2.GameSummonCardInBatttle(builder.build());
    }
    public void SummonGodInTable(HandCard handCard, long row, long col) {

        int index = -1;
        //này là lấy index và maxmana của tướng có nhiều mana nhất trong bộ
        for (int i = 0; i < playerGodDeck.size(); i++) {
            DBHero god = playerGodDeck.get(i).hero;
            if (god.id == handCard.heroID) {
                index = i;
                break;
            }
        }
        GodCardUI godCardSelected = playerGodDeck.get(index);;
        CommonVector.Builder builder = CommonVector.newBuilder()
                .addALong(godCardSelected.battleId)
                .addALong(row)
                .addALong(col);
        ArrayList<DBHeroSkill> lstHeroSkill = new ArrayList<DBHeroSkill>();

        for (DBHeroSkill skill : godCardSelected.hero.lstHeroSkill) {
            if (skill.skill_type == DBHeroSkill.TYPE_SUMMON_SKILL) {
                builder.addALong(skill.id);
            }
        }
        instance.session2.GameSummonCardInBatttle(builder.build());
    }

    public void SummonBuffGodInBattlePhase(Long battleId, Long heroId, int row, int col) {
        //ở đây cần build và gửi lên service cái gì ta?, thôi cứ gửi thêm 1 trường nữa là cái d của thẻ buff vậy
        CommonVector.Builder builder = CommonVector.newBuilder()
                .addALong(battleId)
                .addALong(row)
                .addALong(col);
        builder.addALong(heroId);
        instance.session2.GameSummonCardInBatttle(builder.build());
    }
    public void SummonNormalInBattlePhase(HandCard card, long row, long col) {
//        LogWriterHandle.WriteLog("SummonCardInBattlePhase==" + card.heroInfo.color);
        if (!CheckHeroSkill(TYPE_WHEN_SUMON, card, row, col)) {
            CommonVector cv = CommonVector.newBuilder()
                    .addALong(card.battleID)
                    .addALong(row)
                    .addALong(col).build();
            instance.session2.GameSummonCardInBatttle(cv);
        }
    }

    public boolean SummonSpellInBattlePhase(HandCard card) {
//        LogWriterHandle.WriteLog("SummonCardInBattlePhase==" + card.heroInfo.color);

        //xem sửa hay ko nè, chắc phải sửa nhưng sau đi, làm luồng chính cái đã ==============
        return CheckHeroSkill(TYPE_WHEN_SUMON, card, -1, -1);
    }

    public boolean CanSummon(HandCard card) {
        if (card.tmpMana <= currentMana) {
            if (card.heroInfo.color == DBHero.COLOR_WHITE)
                return true;

//            if (card.heroInfo.speciesId == currentAvailableRegion)
//                return true;

//            boolean isOk = false;
            long totalShard = 0;

            for (Card c : GetListPlayerCardInBattle()) {
                if (card.heroInfo.color == c.heroInfo.color) {
                    if (c.heroInfo.type == DBHero.TYPE_GOD) {
                        totalShard += c.countShardAddded;
                    }
                }
            }

            if (totalShard >= card.heroInfo.shardRequired)
                return true;

//            if (!isOk)
//                Toast.Show(LangHandler.Get("54", "Not enough shards consumed on same-color Gods on the board to summon"));

            return false;
        }
//        Toast.Show(LangHandler.Get("55", "Not enough mana requirement"));
        return false;
    }

    public boolean CheckHeroSkill(int when, Card card, long row, long col) {
        DBHero hero = card.heroInfo;

        for (DBHeroSkill skill : hero.lstHeroSkill) {
            card.SetSkill(skill);
            if (skill.skill_type == DBHeroSkill.TYPE_SUMMON_SKILL
//                    && when == TYPE_WHEN_SUMON
            ) {
                //check condition

                boolean shardOk = CheckShard(card);
                if (!shardOk) {
                    return false;
                }

                if (CheckSkllCondition(card)) {
                    //find target do now
                    boolean skillPS = false;
                    for (ListEffectsSkill skInfo : skill.lstEffectsSkills) {
                        for (EffectSkill effSkill : skInfo.lstEffect) {
                            if (effSkill.target >= 1000) {
                                skillPS = true;
                            }
                        }
                    }
                    if (skillPS) {
                        card.SetSkillReady(skill);
                        return FindTargetToActiveSkill(card, true, row, col);

                    }
                    //if (skill.target >= 1000)
                    //{
                    //    card.SetSkillReady(skill);
                    //    FindTargetToActiveSkill(card, true, row, col);
                    //    return true;
                    //}
                    else {
                        CommonVector cv = CommonVector.newBuilder().addALong(card.battleID)
                                .addALong(row)
                                .addALong(col)
                                .addALong(card.skill.id).build();


//                        LogWriterHandle.WriteLog("----------------OnActivateSkill SUMMON w target <= 1000 =" + string.Join(",", cv.aLong));
                        instance.session2.GameSummonCardInBatttle(cv);

                        return true;
                    }
                }


//            }
//            else if (skill.skill_type == DBHeroSkill.TYPE_ACTIVE_SKILL && when != TYPE_WHEN_SUMON)
//            {
//                //change mode skill can active
//                if (card.skill != null)
//                {
//                    bool shardOk = CheckShard(card);
//                    if (!shardOk)
//                    {
//                        return false;
//                    }
//
//                    if (card.skill.isUltiType && !GameData.main.isUsedUlti)
//                    {
//                        card.SetSkillReady(skill);
//                        return true;
//                    }
//                    else if (card.skill.isUltiType && GameData.main.isUsedUlti)
//                    {
//
//                        //@Tony
//                        //card.SetSkillState(false);
//                        Toast.Show(LangHandler.Get("82", "Ultimate already activated for this match"));
//                    }
//                    if (!card.skill.isUltiType && card.countDoActiveSkill == 0)
//                    {
//                        card.SetSkillReady(skill);
//                        return true;
//                    }
//                    else if (!card.skill.isUltiType && card.countDoActiveSkill != 0)
//                    {
//                        Toast.Show(" You can use active skill once per turn");
//                    }
//                }
//                else
//                {
//                    card.SetSkillReady(skill);
//                }
//                return false;
            }
        }
        return false;

    }

    boolean CheckSkllCondition(Card card) {

//        if (card.skill != null)
//        {
//            for(ConditionSkill condition : card.skill.lstConditionSkill)
//            {
//                switch (condition.type)
//                {
//
//                    case ConditionSkill.CONDITION_TYPE_SMALLER:
//                    {
//
//                        int number = condition.number;
//                        int species = condition.species;
//                        int pos = condition.pos;
//                        int count = 1;
//                        if (pos == 0)
//                        {
//                            foreach (Card c in lstCardInBattle)
//                            if (c.heroInfo.speciesId == species)
//                                count++;
//                        }
//                        else if (pos == 1)
//                        {
//
//                            foreach (Card c in GetListPlayerCardInBattle())
//                            if (c.heroInfo.speciesId == species)
//                                count++;
//
//                        }
//                        else if (pos == 2)
//                        {
//                            foreach (Card c in GetListEnemyCardInBattle())
//                            if (c.heroInfo.speciesId == species)
//                                count++;
//                        }
//
//                        if (count >= number)
//                            return false;
//
//                        break;
//                    }
//
//                    case ConditionSkill.CONDITION_TYPE_BIGGER:
//                    {
//                        int number = condition.number;
//                        int species = condition.species;
//                        int pos = condition.pos;
//                        int count = 1;
//
//                        if (pos == 0)
//                        {
//                            foreach (Card c in lstCardInBattle)
//                            if (c.heroInfo.speciesId == species)
//                                count++;
//                        }
//                        else if (pos == 1)
//                        {
//
//                            foreach (Card c in GetListPlayerCardInBattle())
//                            if (c.heroInfo.speciesId == species)
//                                count++;
//
//                        }
//                        else if (pos == 2)
//                        {
//                            foreach (Card c in GetListEnemyCardInBattle())
//                            if (c.heroInfo.speciesId == species)
//                                count++;
//                        }
//                        if (count < number)
//                            return false;
//
//                        break;
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
        return true;
    }

    public List<HandCard> normalGroup(int group) {
        List<HandCard> result = new ArrayList<>();
        for (HandCard card : Decks[0].GetListCard()) {
            if (IntStream.of(normalGroup[group]).anyMatch(x -> x == card.heroID)) {
                result.add(card);
            }
        }
        return result;

    }

    public boolean summonCardInGroup(int group, int row, int col) {

        List<HandCard> validCards = normalGroup(group);
        if (validCards.size() <= 0) return false;
        for (HandCard card : validCards) {

            if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
                SummonNormalInBattlePhase(card, row, col);
                return true;
            }
        }
        return false;
    }

    public boolean summonCardInGroupWithMaxMana(int group, int row, int col) {

        List<HandCard> validCards = normalGroup(group);
        if (validCards.size() <= 0) return false;
        int maxMana = 0;
        HandCard cardMaxMana = null;
        for (HandCard card : validCards) {
            if(maxMana <= card.tmpMana){
                maxMana = (int) card.tmpMana;
                cardMaxMana = card;
            }
        }
        if (cardMaxMana.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(cardMaxMana)) {
            SummonNormalInBattlePhase(cardMaxMana, row, col);
            return true;
        }
        return false;
    }

    public boolean canSummonCardInGroup(int group) {

        List<HandCard> validCards = normalGroup(group);
        if (validCards.size() == 0) return false;
        for (HandCard card : validCards) {
            if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && CanSummon(card)) {
                return true;
            }
        }
        return false;
    }

//    public boolean haveBoardCardInGroup(int group) {
//        for (BoardCard card : GetListPlayerCardInBattle()){
//
//        }
//    }

    public TowerController getAllyTowerByLane(int lane) {
        TowerController tower = lstTowerInBattle.stream().filter(t -> t.pos == 0 && t.id == lane).findAny().orElse(null);
        if (tower.towerHealth > 0) return tower;
        return lstTowerInBattle.stream().filter(t -> t.pos == 0 && t.id == 1).findAny().orElse(null);

    }

    public void AddShard(BoardCard card) {
        if (card.countShardAddded >= 6) {
//            Toast.Show(LangHandler.Get("toast-13", "God has maximum shard"));
            return;
        }
        CommonVector cv = CommonVector.newBuilder().addALong(card.slot.xPos).addALong(card.slot.yPos).build();
        instance.session2.GameAddShardHero(cv);
    }

    public void GetShard() {
        instance.session2.GameGetShard();
    }

    public boolean DoActiveSkill(Card card) {
        if (card.skill == null)
            return false;
        if (!CheckShard(card)
//                || CheckSkllCondition(card)
        )
            return false;


        //find target do now
        if (!CheckHeroActiveSkill(card))
            return false;

        boolean skillPS = false;
        for (ListEffectsSkill skInfo : card.skill.lstEffectsSkills) {
            for (EffectSkill effSkill : skInfo.lstEffect) {
                if (effSkill.target >= 1000) {
                    skillPS = true;
                }
            }
        }
        if (skillPS) {
            return FindTargetToActiveSkill(card, false, -1, -1);
        }
        //if (skill.target >= 1000)
        //{
        //    card.SetSkillReady(skill);
        //    FindTargetToActiveSkill(card, true, row, col);
        //    return true;
        //}
        else {
            CommonVector cv = CommonVector.newBuilder()
                    .addALong(card.skill.id)
                    .addALong(card.battleID)
                    .build();

//                    LogWriterHandle.WriteLog("----------------OnActivateSkill SUMMON w target <= 1000 =" + string.Join(",", cv.aLong));
            instance.session2.GameActiveSkill(cv);
            return true;
        }
//            }

//        }

    }

    public boolean CheckShard(Card card) {

//        if (card.heroInfo.color == DBHero.COLOR_WHITE)
//            return true;

//        if (card.heroInfo.speciesId == currentAvailableRegion)
//            return true;

//        bool isOk = false;
//
//        foreach (Card c in GetListPlayerCardInBattle())
//        {
//
//            if (card.heroInfo.color == c.heroInfo.color)
//            {
//                if (c.countShardAddded >= card.heroInfo.shardRequired && c.countShardAddded >= card.skill.min_shard)
//                    if (card.skill.max_shard == -1)
//                        isOk = true;
//                    else if (c.countShardAddded <= card.skill.max_shard)
//                        isOk = true;
//            }
//        }
//        return isOk;
        if (card.countShardAddded >= card.skill.min_shard)
            if (card.skill.max_shard == -1 || card.countShardAddded <= card.skill.max_shard)
                return true;
        return false;
    }

    public boolean CheckHeroActiveSkill(
            Card card) {
        //change mode skill can active
        if (card.skill != null) {
            boolean shardOk = CheckShard(card);
            if (!shardOk) {
                return false;
            }
            if (card.skill.isUltiType && !isUsedUlti) {
                return true;
            } else if (card.skill.isUltiType && isUsedUlti) {

                //@Tony
                //card.SetSkillState(false);
//                Toast.Show(LangHandler.Get("82", "Ultimate already activated for this match"));
            }
            if (!card.skill.isUltiType && card.countDoActiveSkill == 0) {
                BoardCard bCard = (BoardCard) card;
                if (bCard != null) {
                    if (bCard.isTired)
//                        Toast.Show(" You can't use active skill when god is tired.")
                        ;
                    else
                        return true;
                }
                //return true;
            } else if (!card.skill.isUltiType && card.countDoActiveSkill != 0) {
//                Toast.Show(" You can use active skill once per turn");
            }
        } else {
        }
        return false;
    }

    boolean FindTargetToActiveSkill(Card card
            , boolean isSummon, long row, long col
    ) {
        List<Long> lstTarget = new ArrayList<>();
//        curCardSkill = card;
//        if (!isStartFindTarget)
//        {
//            countSkillInfo = 0;
//            countEffect = 0;
//            lstTarget = new CommonVector();
//            if (!cancelSkill.activeSelf)
//                cancelSkill.SetActive(true);
//        }

//
//        isStartFindTarget = true;
//        //onChangeSkillState?.Invoke(true);
//        //ButtonOrbController.instance.onActiveSkill += ActiveSkill;
//        onEndSkillActive += EndSkill;
//
//
//        void ActiveSkill()
//        {
//            OnActivateSkill();
//        }
//
//        void EndSkill()
//        {
//            if (card.newCardClone != null)
//            {
//                card.MoveFail();
//            }
//            //ButtonOrbController.instance.onActiveSkill -= ActiveSkill;
//            onEndSkillActive -= EndSkill;
//        }
//        LogWriterHandle.WriteLog(card.battleID);
//        LogWriterHandle.WriteLog(card.skill.id);

//                        boolean skillPS = false;
        boolean isKick = false;
        for (int i = 0; i < card.skill.lstEffectsSkills.size(); i++) {
            boolean isInfo = false;
            ListEffectsSkill skillInfo = card.skill.lstEffectsSkills.get(i);
//            if (i == countSkillInfo)
//            {
            for (int j = 0; j < skillInfo.lstEffect.size(); j++) {
//                    if (j == countEffect)
//                    {

                EffectSkill eff = skillInfo.lstEffect.get(j);

                if (eff.target >= 1000) {
//                            skillPS = true;
//                            if (!chooseTargets.transform.parent.gameObject.activeSelf)
//                            {
//                                chooseTargets.transform.parent.gameObject.SetActive(true);
//                            }
                    switch (eff.target) {
//                                case DBHeroSkill.CHOSE_SELF_BLANK_NEXT:
//                                {
////                                    chooseTargets.text = "Choose 1 slot.";
//                                    // not in use
//                                    //chọn 1 ô trống ben canh minh (thường để dùng skill triệu hồi)
//                                    List<CardSlot> slots = ChooseSelfBlankNext(card.GetComponent<BoardCard>());
//                                    if (slots.Count == 0)
//                                    {
//                                        if (skillInfo.info == 0 && countSkillInfo == 0)
//                                        {
//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
//                                            SkillFailCondition();
//
//                                        }
//                                        else if (skillInfo.info > 0)
//                                        {
//                                            // trường hợp k co target thi sẽ gui -1
//                                            lstTarget.aLong.Add(-1);
//                                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                                            {
//                                                countEffect++;
//                                                Find();
//                                            }
//                                            else
//                                            {
//                                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                {
//                                                    countSkillInfo++;
//                                                    countEffect = 0;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    ActiveSkill();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        foreach (CardSlot cs in slots)
//                                        cs.HighLightSlot();
//                                        skillState = SkillState.CHOOSE_SELF_BLANK_NEXT;
//                                    }
//                                    break;
//                                }

                        case DBHeroSkill.ANY_ALLY_BUT_SELF: {
                            //chọn ally unit bất kì nhưng không phải bản thân
                            //ChooseAllyUnitButSelf(card);
//                                    chooseTargets.text = "Choose 1 ally.";
                            List<BoardCard> cards = GetListPlayerCardInBattle().stream().filter(x -> x != card).collect(Collectors.toList()); //ChooseAnyUnit(true, card.GetComponent<BoardCard>());
                            if (cards.size() == 0) {
                                lstTarget.add(-1l);


//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found"));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
////                                                Toast.Show(LangHandler.Get("stt-45", "Target not found"));
//
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
                            } else {
                                switch (eff.type) {
                                    case DBHeroSkill.EFFECT_X_AND_Y: {
                                        List<SubEffect> subEffects = eff.subEffects;
                                        if (subEffects.get(0).type == DBHeroSkill.EFFECT_READY && (subEffects.get(1).type == DBHeroSkill.EFFECT_TMP_INCREASE_ATK_AND_HP || subEffects.get(1).type == DBHeroSkill.EFFECT_INCREASE_ATK_AND_HP) ) {
                                            long maxAtk = 0;
                                            BoardCard selectCard = null;
                                            for (BoardCard card1 : cards) {
                                                if (card1.atkValue > maxAtk) {
                                                    maxAtk = card1.atkValue;
                                                    selectCard = card1;
                                                }

                                            }
                                            lstTarget.add(1l);
                                            lstTarget.add(selectCard.battleID);

                                        } else if (subEffects.get(0).type == DBHeroSkill.EFFECT_BUFF_HP && subEffects.get(1).type == DBHeroSkill.EFFECT_INCREASE_ATK_AND_HP) {
                                            long maxLostHp = -1;
                                            BoardCard selectCard = null;
                                            for (BoardCard card1 : cards) {
                                                if ((card1.hpMaxValue - card1.hpValue) > maxLostHp) {
                                                    maxLostHp = card1.hpMaxValue - card1.hpValue;
                                                    selectCard = card1;

                                                }
                                            }
                                            lstTarget.add(1l);
                                            lstTarget.add(selectCard.battleID);
                                        }
                                        isInfo = true;
                                        break;
                                    }
                                    case DBHeroSkill.EFFECT_TMP_INCREASE_ATK_AND_HP:
                                    case DBHeroSkill.EFFECT_INCREASE_ATK_AND_HP:
                                    case DBHeroSkill.EFFECT_INCREASE_SPECIAL_PARAM: {
                                        long maxAtk = 0;
                                        BoardCard selectCard = null;
                                        for (BoardCard card1 : cards) {
                                            if (card1.atkValue > maxAtk) {
                                                maxAtk = card1.atkValue;
                                                selectCard = card1;
                                            }

                                        }
                                        lstTarget.add(1l);
                                        lstTarget.add(selectCard.battleID);
                                        isInfo = true;
                                        break;
                                    }
                                    case DBHeroSkill.EFFECT_BUFF_HP: {
                                        long maxLostHp = -1;
                                        BoardCard selectCard = null;
                                        for (BoardCard card1 : cards) {
                                            if ((card1.hpMaxValue - card1.hpValue) > maxLostHp) {
                                                maxLostHp = card1.hpMaxValue - card1.hpValue;
                                                selectCard = card1;

                                            }
                                        }
                                        lstTarget.add(1l);
                                        lstTarget.add(selectCard.battleID);
                                        isInfo = true;
                                        break;

                                    }
//                                    case DBHeroSkill.EFFECT_DEAL_DAME:
                                    default: {
                                        long maxHp = 0;
                                        BoardCard selectCard = null;
                                        for (BoardCard card1 : cards) {
                                            if (card1.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL) {
                                                if (card1.hpValue > maxHp) {
                                                    maxHp = card1.hpValue;
                                                    selectCard = card1;
                                                }
                                            }

                                        }
                                        if (selectCard == null) {
                                            lstTarget.add(-1l);

                                        } else {
                                            lstTarget.add(1l);
                                            lstTarget.add(selectCard.battleID);
                                            isInfo = true;
                                        }

                                        break;
                                    }


                                }
                            }
                            break;
                        }
                        case DBHeroSkill.TWO_ANY_ENEMIES: {
                            // chọn 2 hero địch
//                                    chooseTargets.text = "Choose 2 enemies.";
                            List<BoardCard> cards = GetListEnemyCardInBattle();
                            if (cards.size() <= 1) {
                                lstTarget.add(-1l);
                            } else {
                                switch (eff.type) {
                                    case DBHeroSkill.EFFECT_MOVE_HERO: {
                                        long maxAtk = 0;
                                        BoardCard selectCard1 = null;
                                        for (BoardCard card1 : cards) {
                                            if (card1.atkValue > maxAtk) {
                                                maxAtk = card1.atkValue;
                                                selectCard1 = card1;
                                            }
                                        }
                                        long maxHp = 0;
                                        BoardCard selectCard2 = null;
                                        for (BoardCard card2 : cards) {
                                            if (card2 != selectCard1) {
                                                if (card2.hpValue > maxHp) {
                                                    maxHp = card2.hpValue;
                                                    selectCard2 = card2;
                                                }
                                            }
                                        }
                                        lstTarget.add(6l);
                                        lstTarget.add(selectCard1.battleID);
                                        lstTarget.add((long) selectCard1.slot.xPos);
                                        lstTarget.add((long) selectCard1.slot.yPos);
                                        lstTarget.add(selectCard2.battleID);
                                        lstTarget.add((long) selectCard2.slot.xPos);
                                        lstTarget.add((long) selectCard2.slot.yPos);
                                        break;
                                    }
                                }
                                isInfo = true;
                            }


                            break;
                        }
                        case DBHeroSkill.ANY_ALLY_UNIT: {
                            // chọn ally unit bất kì kể cả phải bản thân
                            //ChooseAnyAllyUnit();
//                                    chooseTargets.text = "Choose 1 ally.";
                            List<BoardCard> cards = GetListPlayerCardInBattle();
//                                    CardOnBoardClone clone = null;
////                                    cards.ForEach(x =>
////                                            {
////                                                    x.HighlightUnit();
////                                    x.onAddToListSkill += AddBoardCardToSkillList;
////                                    //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
////                                    x.onEndSkillActive += OnEndSkillBoard;
////                                            });
//                                    if (card.newCardClone != null)
//                                        clone = card.newCardClone.GetComponent<CardOnBoardClone>();
//                                    if (clone != null)
//                                    {
//                                        clone.HighlightUnit();
//                                        clone.onAddToListSkill += AddCloneCardToSkillList;
//                                        //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                        clone.onEndSkillActive += OnEndSkillClone;
//                                    }
//                                    skillState = SkillState.ANY_ALLY_UNIT;
                            switch (eff.type) {
                                case DBHeroSkill.EFFECT_BUFF_HP: {
                                    long maxLostHp = -1;
                                    BoardCard selectCard = null;
                                    for (BoardCard card1 : cards) {
                                        if ((card1.hpMaxValue - card1.hpValue) > maxLostHp) {
                                            maxLostHp = card1.hpMaxValue - card1.hpValue;
                                            selectCard = card1;

                                        }
                                    }
                                    lstTarget.add(1l);
                                    lstTarget.add(selectCard.battleID);
                                    break;
                                }
                                case DBHeroSkill.EFFECT_DEAL_DAME: {
                                    long maxHp = 0;
                                    BoardCard selectCard = null;
                                    for (BoardCard card1 : cards) {
                                        if (card1.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL) {
                                            if (card1.hpValue > maxHp) {
                                                maxHp = card1.hpValue;
                                                selectCard = card1;
                                            }
                                        }

                                    }
                                    if (selectCard == null) {
                                        selectCard = cards.stream().filter(x -> x.heroInfo.type == DBHero.TYPE_GOD).findAny().orElse(null);
                                    }
                                    lstTarget.add(1l);
                                    lstTarget.add(selectCard.battleID);
                                    break;
                                }

                            }
                            isInfo = true;
                            break;
                        }
                        case DBHeroSkill.TWO_ANY_ALLIES: {
                            // chọn 2 hero đồng minh (vd: đổi chỗ)
                            //ChooseTwoAllyUnit(card);
//                                    chooseTargets.text = "Choose 2 allies.";
                            List<BoardCard> cards = GetListPlayerCardInBattle();
//                                    CardOnBoardClone clone = null;
//                                    if (card.newCardClone != null)
//                                        clone = card.newCardClone.GetComponent<CardOnBoardClone>();
//
//                                    if (clone != null)
//                                    {
//                                        {
//                                            if (cards.Count + 1 <= 1)
//                                            {
//                                                if (skillInfo.info == 0 && countSkillInfo == 0)
//                                                {
//                                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                                    {
//                                                        card.GetComponent<HandCard>().MoveFail();
//                                                        //SkillFailCondition();
//                                                        Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                    }
//                                                    else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                                    {
//                                                        //SkillFailCondition();
//                                                        Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                    }
//                                                    else
//                                                    {
//                                                        if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                        {
//                                                            CommonVector cv = new CommonVector();
//                                                            cv.aLong.Add(card.battleID);
//                                                            cv.aLong.Add(row);
//                                                            cv.aLong.Add(col);
//                                                            Game.main.socket.GameSummonCardInBatttle(cv);
//                                                        }
//                                                    }
//                                                    SkillFailCondition();
//
//                                                }
//                                                else if (skillInfo.info > 0)
//                                                {
//                                                    // trường hợp k co target thi sẽ gui -1
//                                                    lstTarget.aLong.Add(-1);
//                                                    if (countEffect < skillInfo.lstEffect.Count - 1)
//                                                    {
//                                                        countEffect++;
//                                                        Find();
//                                                    }
//                                                    else
//                                                    {
//                                                        if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                        {
//                                                            countSkillInfo++;
//                                                            countEffect = 0;
//                                                            Find();
//                                                        }
//                                                        else
//                                                        {
//                                                            ActiveSkill();
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            else
//                                            {
//                                                cards.ForEach(x =>
//                                                        {
//                                                                x.HighlightUnit();
//                                                x.onAddToListSkill += AddBoardCardToSkillList;
//                                                //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                                x.onEndSkillActive += OnEndSkillBoard;
//                                                        });
//                                                clone.HighlightUnit();
//                                                clone.onAddToListSkill += AddCloneCardToSkillList;
//                                                //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                                clone.onEndSkillActive += OnEndSkillClone;
//                                                skillState = SkillState.TWO_ANY_ALLIES;
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
                            if (cards.size() <= 1) {
                                lstTarget.add(-1l);
//                                            if (skillInfo.info == 0 && countSkillInfo == 0)
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                                {
//                                                    card.GetComponent<HandCard>().MoveFail();
//                                                    //SkillFailCondition();
//                                                    Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                }
//                                                else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                                {
//                                                    //SkillFailCondition();
//                                                    Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                }
//                                                else
//                                                {
//                                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                    {
//                                                        CommonVector cv = new CommonVector();
//                                                        cv.aLong.Add(card.battleID);
//                                                        cv.aLong.Add(row);
//                                                        cv.aLong.Add(col);
//                                                        Game.main.socket.GameSummonCardInBatttle(cv);
//                                                    }
//                                                }
//                                                SkillFailCondition();
//
//                                            }
//                                            else if (skillInfo.info > 0)
//                                            {
//                                                // trường hợp k co target thi sẽ gui -1
//                                                lstTarget.aLong.Add(-1);
//                                                if (countEffect < skillInfo.lstEffect.Count - 1)
//                                                    countEffect++;
//                                                else
//                                                {
//                                                    if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                    {
//                                                        countSkillInfo++;
//                                                        countEffect = 0;
//                                                        Find();
//                                                    }
//                                                    else
//                                                    {
//                                                        ActiveSkill();
//                                                    }
//                                                }
//                                            }
                            } else {
//                                            cards.ForEach(x =>
//                                                    {
//                                                            x.HighlightUnit();
//                                            x.onAddToListSkill += AddBoardCardToSkillList;
//                                            //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                            x.onEndSkillActive += OnEndSkillBoard;
//                                                    });
//                                            skillState = SkillState.TWO_ANY_ALLIES;
                                switch (eff.type) {
                                    case DBHeroSkill.EFFECT_MOVE_HERO: {
                                        long maxAtk = 0;
                                        BoardCard selectCard1 = null;
                                        for (BoardCard card1 : cards) {
                                            if (card1.atkValue > maxAtk) {
                                                maxAtk = card1.atkValue;
                                                selectCard1 = card1;
                                            }
                                        }
                                        long maxHp = 0;
                                        BoardCard selectCard2 = null;
                                        for (BoardCard card2 : cards) {
                                            if (card2 != selectCard1) {
                                                if (card2.hpValue > maxHp) {
                                                    maxHp = card2.hpValue;
                                                    selectCard2 = card2;
                                                }
                                            }
                                        }
                                        lstTarget.add(6l);
                                        lstTarget.add(selectCard1.battleID);
                                        lstTarget.add((long) selectCard1.slot.xPos);
                                        lstTarget.add((long) selectCard1.slot.yPos);
                                        lstTarget.add(selectCard2.battleID);
                                        lstTarget.add((long) selectCard2.slot.xPos);
                                        lstTarget.add((long) selectCard2.slot.yPos);
                                        break;
                                    }
                                }
                                isInfo = true;
                            }
//                                    }

                            break;
                        }

                        case DBHeroSkill.CHOSSE_FOUNTAIN: {
                            // chọn foutain
//                                                    chooseTargets.text = "Choose 1 tower.";
                            List<TowerController> lstTower = lstTowerInBattle.stream().filter(t -> t.pos == 1 && t.towerHealth > 0).collect(Collectors.toList());
                            switch (eff.type) {
                                case DBHeroSkill.EFFECT_DEAL_DAME: {
                                    long minHeath = 16;
                                    TowerController selectedTower = null;
                                    for (TowerController tower : lstTower) {
                                        if (tower.towerHealth < minHeath) {
                                            minHeath = tower.towerHealth;
                                            selectedTower = tower;
                                        }

                                    }
                                    long foutainID = 0;
                                    switch ((int) GetServerPostFromUsername(instance.username)) {

                                        // user o pos 6h -> send pos 12h
                                        case POS_6h:
                                            foutainID = -10 - (selectedTower.id + 1);
                                            break;
                                        // user o pos 12h -> send pos 6h
                                        case POS_12h:
                                            foutainID = -(selectedTower.id + 1);
                                            break;
                                    }
                                    lstTarget.add(1l);
                                    lstTarget.add(foutainID);
                                    break;
                                }
                            }

                        }

//                                case DBHeroSkill.ANY_ALLY_LANE_UNITS:
//                                {
//                                    chooseTargets.text = "Choose 1 lane.";
//                                    lstLaneInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightLane();
//                                    x.onAddToListSkill += AddLaneToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveLaneFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillLane;
//                                            });
//                                    skillState = SkillState.CHOOSE_LANE;
//                                    break;
//                                }

//                                case DBHeroSkill.CHOSSE_LANE:
//                                {
//                                    // chọn lane
//                                    chooseTargets.text = "Choose 1 lane.";
//                                    lstLaneInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightLane();
//                                    x.onAddToListSkill += AddLaneToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveLaneFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillLane;
//                                            });
//                                    skillState = SkillState.CHOOSE_LANE;
//                                    break;
//                                }

//                                case DBHeroSkill.ANY_UNIT:
//                                {
//                                    chooseTargets.text = "Choose 1 unit.";
//                                    lstCardInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightUnit();
//                                    x.onAddToListSkill += AddBoardCardToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillBoard;
//                                            });
//                                    if (card.newCardClone != null)
//                                    {
//                                        CardOnBoardClone clone = card.newCardClone.GetComponent<CardOnBoardClone>();
//                                        if (clone != null)
//                                        {
//                                            clone.HighlightUnit();
//                                            clone.onAddToListSkill += AddCloneCardToSkillList;
//                                            //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                            clone.onEndSkillActive += OnEndSkillClone;
//                                        }
//
//                                    }
//                                    skillState = SkillState.ANY_UNIT;
//                                    break;
//                                }
                        case DBHeroSkill.ANY_ENEMY_UNIT: {
//                                    chooseTargets.text = "Choose 1 enemy.";
                            List<BoardCard> cards = GetListEnemyCardInBattle();
                            if (cards.size() == 0) {
                                lstTarget.add(-1l);
                            } else {
                                switch (eff.type) {
                                    case DBHeroSkill.EFFECT_DEAL_DAME: {
                                        long maxAtk = 0;
                                        BoardCard selectCard = null;
                                        for (BoardCard card1 : cards) {
                                            if (card1.atkValue > maxAtk) {
                                                maxAtk = card1.atkValue;
                                                selectCard = card1;
                                            }

                                        }
                                        lstTarget.add(1l);
                                        lstTarget.add(selectCard.battleID);
                                        break;
                                    }
                                }
                                isInfo = true;
                            }
                            break;
                        }
//                                case DBHeroSkill.ANY_LANE_UNITS:
//                                {
//                                    chooseTargets.text = "Choose 1 lane.";
//                                    lstLaneInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightLane();
//                                    x.onAddToListSkill += AddLaneToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveLaneFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillLane;
//                                            });
//                                    skillState = SkillState.ANY_LANE_UNIT;
//                                    break;
//                                }
//                                case DBHeroSkill.RANDOM_ENEMY_IN_SELECTED_LANE:
//                                {
//
//                                    chooseTargets.text = "Choose 1 lane.";
//                                    lstLaneInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightLane();
//                                    x.onAddToListSkill += AddLaneToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveLaneFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillLane;
//                                            });
//                                    skillState = SkillState.CHOOSE_LANE;
//                                    break;
//                                }

//                                case DBHeroSkill.ANY_ALL_UNIT:
//                                {
//                                    // đã bỏ
//                                    ChooseEnemyTower();
//                                    GetListEnemyCardInBattle().ForEach(x =>
//                                            {
//                                                    x.HighlightUnit();
//                                    x.onAddToListSkill += AddBoardCardToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillBoard;
//                                            });
//                                    skillState = SkillState.ANY_ALL_UNIT;
//                                    break;
//                                }

//                                case DBHeroSkill.MY_HAND_CARD:
//                                {
//
//                                    chooseTargets.text = "Choose 1 hand card.";
//                                    // không liên quan gì đến bản thân
//                                    skillState = SkillState.MY_HAND_CARD;
//                                    Decks[0].GetListCard.Where(c => c != card).ToList().ForEach(x =>
//                                        {
//                                                x.HighlighCard();
//                                    x.onAddToListSkill += AddHandCardToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveHandCardFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillHand;
//                                            });
//                                    break;
//                                }
//                                case DBHeroSkill.ANY_MOTAL:
//                                {
//
//                                    chooseTargets.text = "Choose 1 mortal.";
//                                    CardOnBoardClone clone = null;
//                                    List<BoardCard> cards = lstCardInBattle.Where(x => x.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL).ToList();
//                                    if (card.newCardClone != null)
//                                        clone = card.newCardClone.GetComponent<CardOnBoardClone>();
//                                    if (clone != null && card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL)
//                                    {
//                                        cards.ForEach(x =>
//                                                {
//                                                        x.HighlightUnit();
//                                        x.onAddToListSkill += AddBoardCardToSkillList;
//                                        //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                        x.onEndSkillActive += OnEndSkillBoard;
//                                                });
//                                        clone.HighlightUnit();
//                                        clone.onAddToListSkill += AddCloneCardToSkillList;
//                                        //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                        clone.onEndSkillActive += OnEndSkillClone;
//                                        skillState = SkillState.ANY_MOTAL;
//                                    }
//                                    else
//                                    {
//                                        if (cards.Count == 0)
//                                        {
//                                            if (skillInfo.info == 0 && countSkillInfo == 0)
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                                {
//                                                    card.GetComponent<HandCard>().MoveFail();
//                                                    //SkillFailCondition();
//                                                    Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                }
//                                                else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                                {
//                                                    //SkillFailCondition();
//                                                    Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                }
//
//                                                SkillFailCondition();
//
//                                            }
//                                            else if (skillInfo.info > 0)
//                                            {
//                                                // trường hợp k co target thi sẽ gui -1
//                                                lstTarget.aLong.Add(-1);
//                                                if (countEffect < skillInfo.lstEffect.Count - 1)
//                                                {
//                                                    countEffect++;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                    {
//                                                        countSkillInfo++;
//                                                        countEffect = 0;
//                                                        Find();
//                                                    }
//                                                    else
//                                                    {
//                                                        ActiveSkill();
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        else
//                                        {
//                                            cards.ForEach(x =>
//                                                    {
//                                                            x.HighlightUnit();
//                                            x.onAddToListSkill += AddBoardCardToSkillList;
//                                            //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                            x.onEndSkillActive += OnEndSkillBoard;
//                                                    });
//                                        }
//                                    }
//                                    skillState = SkillState.ANY_MOTAL;
//                                    break;
//                                }

//                                case DBHeroSkill.ANY_BLANK_ALLY:
//                                {
//
//                                    chooseTargets.text = "Choose 1 position to summon.";
//                                    //chọn 1 ô trống ben canh minh (thường để dùng skill triệu hồi)
//                                    List<CardSlot> slots = playerSlotContainer.Where(x => x.state == SlotState.Empty).ToList();
//                                    if (slots.Count == 0)
//                                    {
//                                        if (skillInfo.info == 0 && countSkillInfo == 0)
//                                        {
//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
//                                            SkillFailCondition();
//
//                                        }
//                                        else if (skillInfo.info > 0)
//                                        {
//                                            // trường hợp k co target thi sẽ gui -1
//                                            lstTarget.aLong.Add(-1);
//                                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                                            {
//                                                countEffect++;
//                                                Find();
//                                            }
//                                            else
//                                            {
//                                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                {
//                                                    countSkillInfo++;
//                                                    countEffect = 0;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    ActiveSkill();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        slots.ForEach(x =>
//                                                {
//                                                        x.HighLightSlot();
//                                        x.onAddToListSkill += AddSlotToSkillList;
//                                        //x.onRemoveFromListSkill -= RemoveSlotFromSkillList;
//                                        x.onEndSkillActive -= OnEndSkillSlot;
//                                                });
//                                        skillState = SkillState.ANY_BLANK_ALLY;
//                                    }
//                                    break;
//                                }

//                                case DBHeroSkill.ANY_LANE_ENEMY:
//                                {
//                                    // chọn enemy cung duong
//
//                                    chooseTargets.text = "Choose 1 enemy.";
//                                    List<BoardCard> cards = GetListEnemyCardInBattle().Where(x => (col <= 1 && x.slot.yPos <= col) || (col >= 2 && x.slot.yPos >= col)).ToList();
//
//                                    if (cards.Count == 0)
//                                    {
//                                        if (skillInfo.info == 0 && countSkillInfo == 0)
//                                        {
//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
//                                            SkillFailCondition();
//
//                                        }
//                                        else if (skillInfo.info > 0)
//                                        {
//                                            // trường hợp k co target thi sẽ gui -1
//                                            lstTarget.aLong.Add(-1);
//                                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                                            {
//                                                countEffect++;
//                                                Find();
//                                            }
//                                            else
//                                            {
//                                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                {
//                                                    countSkillInfo++;
//                                                    countEffect = 0;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    ActiveSkill();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        cards.ForEach(x =>
//                                                {
//                                                        x.HighlightUnit();
//                                        x.onAddToListSkill += AddBoardCardToSkillList;
//                                        //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                        x.onEndSkillActive += OnEndSkillBoard;
//                                                });
//                                        skillState = SkillState.ANY_LANE_ENEMY;
//                                    }
//                                    break;
//                                }
//
//                                case DBHeroSkill.ANY_COL_ENEMY:
//                                {
//
//                                    chooseTargets.text = "Choose 1 enemy.";
//                                    List<BoardCard> cards = GetListEnemyCardInBattle().Where(x => x.slot.yPos == col).ToList();
//
//                                    if (cards.Count == 0)
//                                    {
//                                        if (skillInfo.info == 0 && countSkillInfo == 0)
//                                        {
//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
//                                            SkillFailCondition();
//
//                                        }
//                                        else if (skillInfo.info > 0)
//                                        {
//                                            // trường hợp k co target thi sẽ gui -1
//                                            lstTarget.aLong.Add(-1);
//                                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                                            {
//                                                countEffect++;
//                                                Find();
//                                            }
//                                            else
//                                            {
//                                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                {
//                                                    countSkillInfo++;
//                                                    countEffect = 0;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    ActiveSkill();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        cards.ForEach(x =>
//                                                {
//                                                        x.HighlightUnit();
//                                        x.onAddToListSkill += AddBoardCardToSkillList;
//                                        //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                        x.onEndSkillActive += OnEndSkillBoard;
//                                                });
//                                        skillState = SkillState.ANY_COL_ENEMY;
//                                    }
//                                    break;
//                                }
//
//                                case DBHeroSkill.ANY_TARGET:
//                                {
//
//                                    chooseTargets.text = "Choose 1 target.";
//                                    lstCardInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightUnit();
//                                    x.onAddToListSkill += AddBoardCardToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillBoard;
//                                            });
//                                    lstTowerInBattle.Where(x => x.towerHealth > 0).ToList().ForEach(t =>
//                                        {
//                                                t.HighLightTower();
//                                    t.onAddToListSkill += AddTowerToSkillList;
//                                    //t.onRemoveFromListSkill += RemoveTowerFromSkillList;
//                                    t.onEndSkillActive += OnEndSkillTower;
//                                            });
//
//                                    CardOnBoardClone clone = null;
//                                    if (card.newCardClone != null)
//                                        card.newCardClone.GetComponent<CardOnBoardClone>();
//                                    if (clone != null)
//                                    {
//                                        clone.HighlightUnit();
//                                        clone.onAddToListSkill += AddCloneCardToSkillList;
//                                        //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                        clone.onEndSkillActive += OnEndSkillClone;
//                                    }
//                                    skillState = SkillState.ANY_TARGET;
//                                    break;
//                                }
//                                case DBHeroSkill.ANY_ALLY_TARGET:
//                                {
//                                    // than linh tru dong minh
//
//                                    chooseTargets.text = "Choose 1 ally target.";
//                                    GetListPlayerCardInBattle().ForEach(c =>
//                                            {
//                                                    c.HighlightUnit();
//                                    c.onAddToListSkill += AddBoardCardToSkillList;
//                                    // c.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                    c.onEndSkillActive += OnEndSkillBoard;
//                                            });
//                                    lstTowerInBattle.Where(t => t.pos == 0 && t.towerHealth > 0).ToList().ForEach(x =>
//                                        {
//                                                x.HighLightTower();
//                                    x.onAddToListSkill += AddTowerToSkillList;
//                                    // x.onRemoveFromListSkill += RemoveTowerFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillTower;
//                                            });
//                                    CardOnBoardClone clone = null;
//                                    if (card.newCardClone != null)
//                                        card.newCardClone.GetComponent<CardOnBoardClone>();
//                                    if (clone != null)
//                                    {
//                                        clone.HighlightUnit();
//                                        clone.onAddToListSkill += AddCloneCardToSkillList;
//                                        //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                        clone.onEndSkillActive += OnEndSkillClone;
//                                    }
//                                    skillState = SkillState.ANY_ALLY_TARGET;
//                                    break;
//                                }
//                                case DBHeroSkill.RANDOM_UNIT_IN_SELECTED_LANE:
//                                {
//                                    // chon 1 lane => random unit
//
//                                    chooseTargets.text = "Choose 1 lane.";
//                                    lstLaneInBattle.ForEach(x =>
//                                            {
//                                                    x.HighlightLane();
//                                    x.onAddToListSkill += AddLaneToSkillList;
//                                    //x.onRemoveFromListSkill += RemoveLaneFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillLane;
//                                            });
//                                    skillState = SkillState.RANDOM_UNIT_IN_SELECTED_LANE;
//                                    break;
//                                }
                        case DBHeroSkill.ANY_TARGET_BUT_SELF:
                        case DBHeroSkill.ANY_ALLY_TARGET_BUT_SELF: {

//                                    chooseTargets.text = "Choose 1 target.";


                            List<BoardCard> cards = GetListPlayerCardInBattle().stream().filter(c -> c != card).collect(Collectors.toList());
                            long maxLostHp = -1;
                            BoardCard selectCard = null;
                            for (BoardCard card1 : cards) {
                                if ((card1.hpMaxValue - card1.hpValue) > maxLostHp) {
                                    maxLostHp = card1.hpMaxValue - card1.hpValue;
                                    selectCard = card1;

                                }
                            }


                            List<TowerController> lstTower = lstTowerInBattle.stream().filter(x -> x.pos == 0 && x.towerHealth > 0).collect(Collectors.toList());
                            long maxLostHeath = -1;
                            TowerController selectedTower = null;
                            for (TowerController tower : lstTower) {
                                if (TowerController.HEAlTH - tower.towerHealth > maxLostHeath) {
                                    maxLostHeath = TowerController.HEAlTH - tower.towerHealth;
                                    selectedTower = tower;
                                }
                            }
                            if (maxLostHp > maxLostHeath) {
                                lstTarget.add(1l);
                                lstTarget.add(selectCard.battleID);
                            } else {
                                long foutainID = 0;
                                switch ((int) GetServerPostFromClientPos(0)) {

                                    // user o pos 6h -> send pos 12h
                                    case POS_6h:
                                        foutainID = -(selectedTower.id + 1);
                                        break;
                                    // user o pos 12h -> send pos 6h
                                    case POS_12h:
                                        foutainID = -10 - (selectedTower.id + 1);
                                        break;
                                }
                                lstTarget.add(1l);
                                lstTarget.add(foutainID);
                            }
                            isInfo = true;
                            break;
                        }
//                                case DBHeroSkill.ANY_MORTAL_BUT_SELF:
//                                {
//
//                                    chooseTargets.text = "Choose 1 mortal.";
//                                    List<BoardCard> cards = lstCardInBattle.Where(x => (x.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL) && (x != card)).ToList();
//                                    if (cards.Count == 0)
//                                    {
//                                        if (skillInfo.info == 0 && countSkillInfo == 0)
//                                        {
//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
//                                            SkillFailCondition();
//
//                                        }
//                                        else if (skillInfo.info > 0)
//                                        {
//                                            // trường hợp k co target thi sẽ gui -1
//                                            lstTarget.aLong.Add(-1);
//                                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                                            {
//                                                countEffect++;
//                                                Find();
//                                            }
//                                            else
//                                            {
//                                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                {
//                                                    countSkillInfo++;
//                                                    countEffect = 0;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    ActiveSkill();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        cards.ForEach(x =>
//                                                {
//                                                        x.HighlightUnit();
//                                        x.onAddToListSkill += AddBoardCardToSkillList;
//                                        //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                        x.onEndSkillActive += OnEndSkillBoard;
//                                                });
//                                    }
//                                    skillState = SkillState.ANY_MORTAL_BUT_SELF;
//                                    break;
//                                }
//                                case DBHeroSkill.ANY_ALLY_TARGET_BUT_SELF:
//                                {
//
//                                    chooseTargets.text = "Choose 1 ally target.";
//                                    GetListPlayerCardInBattle().Where(c => c != card).ToList().ForEach(c =>
//                                        {
//                                                c.HighlightUnit();
//                                    c.onAddToListSkill += AddBoardCardToSkillList;
//                                    // c.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                    c.onEndSkillActive += OnEndSkillBoard;
//                                            });
//                                    lstTowerInBattle.Where(t => t.pos == 0 && t.towerHealth > 0).ToList().ForEach(x =>
//                                        {
//                                                x.HighLightTower();
//                                    x.onAddToListSkill += AddTowerToSkillList;
//                                    // x.onRemoveFromListSkill += RemoveTowerFromSkillList;
//                                    x.onEndSkillActive += OnEndSkillTower;
//                                            });
//                                    skillState = SkillState.ANY_ALLY_TARGET_BUT_SELF;
//                                    break;
//                                }
//                                case DBHeroSkill.TWO_ANY_ALLIES_BUT_SELF:
//                                {
//
//                                    chooseTargets.text = "Choose 2 allies.";
//                                    List<BoardCard> cards = GetListPlayerCardInBattle().Where(x => x != card).ToList();
//                                    if (cards.Count <= 1)
//                                    {
//                                        if (skillInfo.info == 0 && countSkillInfo == 0)
//                                        {
//                                            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                            {
//                                                card.GetComponent<HandCard>().MoveFail();
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                            {
//                                                //SkillFailCondition();
//                                                Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                            }
//                                            else
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                {
//                                                    CommonVector cv = new CommonVector();
//                                                    cv.aLong.Add(card.battleID);
//                                                    cv.aLong.Add(row);
//                                                    cv.aLong.Add(col);
//                                                    Game.main.socket.GameSummonCardInBatttle(cv);
//                                                }
//                                            }
//                                            SkillFailCondition();
//
//                                        }
//                                        else if (skillInfo.info > 0)
//                                        {
//                                            // trường hợp k co target thi sẽ gui -1
//                                            lstTarget.aLong.Add(-1);
//                                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                                            {
//                                                countEffect++;
//                                                Find();
//                                            }
//                                            else
//                                            {
//                                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                {
//                                                    countSkillInfo++;
//                                                    countEffect = 0;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    ActiveSkill();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        cards.ForEach(x =>
//                                                {
//                                                        x.HighlightUnit();
//                                        x.onAddToListSkill += AddBoardCardToSkillList;
//                                        //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                        x.onEndSkillActive += OnEndSkillBoard;
//                                                });
//                                        skillState = SkillState.TWO_ANY_ALLIES_BUT_SELF;
//                                    }
//                                    break;
//                                }
//                                case DBHeroSkill.TWO_ANY_ALLIES_JUNGLE_LAW:
//                                {
//                                    chooseTargets.text = "Choose upto 2 allies.";
//                                    List<BoardCard> cards = GetListPlayerCardInBattle();
//                                    CardOnBoardClone clone = null;
//                                    if (card.newCardClone != null)
//                                        clone = card.newCardClone.GetComponent<CardOnBoardClone>();
//
//                                    if (clone != null)
//                                    {
//                                        cards.ForEach(x =>
//                                                {
//                                                        x.HighlightUnit();
//                                        x.onAddToListSkill += AddBoardCardToSkillList;
//                                        //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                        x.onEndSkillActive += OnEndSkillBoard;
//                                                });
//                                        clone.HighlightUnit();
//                                        clone.onAddToListSkill += AddCloneCardToSkillList;
//                                        //clone.onRemoveFromListSkill += RemoveCloneCardFromSkillList;
//                                        clone.onEndSkillActive += OnEndSkillClone;
//                                        skillState = SkillState.TWO_ANY_ALLIES_JUNGLE_LAW;
//                                    }
//                                    else
//                                    {
//                                        int count = cards.Count;
//                                        if (cards.Count == 0)
//                                        {
//                                            if (skillInfo.info == 0 && countSkillInfo == 0)
//                                            {
//                                                if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC)
//                                                {
//                                                    card.GetComponent<HandCard>().MoveFail();
//                                                    //SkillFailCondition();
//                                                    Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                }
//                                                else if (card.heroInfo.type == DBHero.TYPE_GOD)
//                                                {
//                                                    //SkillFailCondition();
//                                                    Toast.Show(LangHandler.Get("stt-45", "Target not found."));
//                                                }
//                                                else
//                                                {
//                                                    if (card.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL && isSummon)
//                                                    {
//                                                        CommonVector cv = new CommonVector();
//                                                        cv.aLong.Add(card.battleID);
//                                                        cv.aLong.Add(row);
//                                                        cv.aLong.Add(col);
//                                                        Game.main.socket.GameSummonCardInBatttle(cv);
//                                                    }
//                                                }
//                                                SkillFailCondition();
//
//                                            }
//                                            else if (skillInfo.info > 0)
//                                            {
//                                                // trường hợp k co target thi sẽ gui -1
//                                                lstTarget.aLong.Add(-1);
//                                                if (countEffect < skillInfo.lstEffect.Count - 1)
//                                                {
//                                                    countEffect++;
//                                                    Find();
//                                                }
//                                                else
//                                                {
//                                                    if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                                    {
//                                                        countSkillInfo++;
//                                                        countEffect = 0;
//                                                        Find();
//                                                    }
//                                                    else
//                                                    {
//                                                        ActiveSkill();
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        else
//                                        {
//                                            cards.ForEach(x =>
//                                                    {
//
//                                                            LogWriterHandle.WriteLog("check2" + cards.Count() + "/" + countEffect);
//                                            LogWriterHandle.WriteLog("log skill " + x.battleID + "___________" + x.gameObject.name);
//                                            x.HighlightUnit();
//                                            x.onAddToListSkill += AddBoardCardToSkillList;
//                                            //x.onRemoveFromListSkill += RemoveBoardCardFromSkillList;
//                                            x.onEndSkillActive += OnEndSkillBoard;
//                                                    });
//                                            skillState = SkillState.TWO_ANY_ALLIES_JUNGLE_LAW;
//                                        }
//                                    }
//                                    break;
//                                }
                        case DBHeroSkill.ANY_UNIT_BUT_SELF: {

//                                    chooseTargets.text = "Choose 1 unit.";
                            List<BoardCard> cards = lstCardInBattle.stream().filter(x -> x != card).collect(Collectors.toList());
                            if (cards.size() == 0) {
                                lstTarget.add(1l);
                            } else {
                                switch (eff.type) {
                                    case DBHeroSkill.EFFECT_DEAL_DAME: {
                                        long maxHp = 0;
                                        BoardCard selectCard = null;
                                        for (BoardCard card1 : cards) {
                                            if (card1.heroInfo.type == DBHero.TYPE_TROOPER_NORMAL) {
                                                if (card1.hpValue > maxHp) {
                                                    maxHp = card1.hpValue;
                                                    selectCard = card1;
                                                }
                                            }

                                        }
                                        if (selectCard == null) {
                                            lstTarget.add(-1l);

                                        } else {
                                            lstTarget.add(1l);
                                            lstTarget.add(selectCard.battleID);
                                            isInfo = true;
                                        }
                                        break;
                                    }
                                }
                            }


                            break;
                        }
                    }


                } else {
                    isInfo = true;
//                            if (countEffect < skillInfo.lstEffect.Count - 1)
//                            {
//                                countEffect++;
//                                Find();
//                            }
//                            else
//                            {
//                                if (countSkillInfo < card.skill.lstEffectsSkills.Count - 1)
//                                {
//                                    countSkillInfo++;
//                                    countEffect = 0;
//                                    Find();
//                                }
//                                else
//                                {
//                                    //ActiveSkill();
//                                    CommonVector cv = new CommonVector();
//                                    cv.aLong.Add(card.skill.id);
//                                    cv.aLong.Add(card.battleID);
//
//                                    LogWriterHandle.WriteLog("ACTIVE SKILL NO TARGET =" + string.Join(",", cv.aLong));
//
//                                    Game.main.socket.GameActiveSkill(cv);
//                                }
//                            }
                }
//                    }
            }

//            }
            if (i == 0) isKick = isInfo;
            if (!isInfo) break;
        }
//        if (!isKick) {
//            if (card.skill.skill_type == DBHeroSkill.TYPE_ACTIVE_SKILL) return false;
//            if (card.heroInfo.type == DBHero.TYPE_TROOPER_MAGIC) return false;
//            CommonVector cv = CommonVector.newBuilder().addALong(card.battleID)
//                    .addALong(row)
//                    .addALong(col).build();
//            instance.session2.GameSummonCardInBatttle(cv);
//            return true;
//        }
        if (!isKick)
            return false;


        if (card.skill.skill_type == DBHeroSkill.TYPE_ACTIVE_SKILL) {
            CommonVector cv = CommonVector.newBuilder()
                    .addALong(card.skill.id)
                    .addALong(card.battleID)
                    .addAllALong(lstTarget).build();
            instance.session2.GameActiveSkill(cv);
        } else if (card.skill.skill_type == DBHeroSkill.TYPE_SUMMON_SKILL) {
            CommonVector cv = CommonVector.newBuilder().addALong(card.battleID)
                    .addALong(row)
                    .addALong(col)
                    .addALong(card.skill.id)
                    .addAllALong(lstTarget).build();

            instance.session2.GameSummonCardInBatttle(cv);
        }

        return true;
    }


    private void SimulateSkillEffect(Action action) {
        try {
            switch (action.getActionId()) {
                case IService.GAME_SKILL_EFFECT: {
                    CommonVector cv = null;
                    cv = CommonVector.parseFrom(action.getData());

//					WriteLogBattle("GAME_SKILL_EFFECT: ", string.Join(",", cv.aString), string.Join(",", cv.aLong));
                    //if (cv.aLong[0] == 0)
                    //{
                    //    Toast.Show(cv.aString[0]);
                    //    break;
                    //}

                    //yield return new WaitForSeconds(0.5f);

//					OnEndSkillPhase();
                    if (cv.getALongCount() > 0) {

                        long skillId = cv.getALong(0);
                        long effectId = cv.getALong(1);
                        long battleId = cv.getALong(2);

                        long clientIndexPlayer = 0;

                        for (Card c : lstCardInBattle)
                            if (c.battleID == battleId) {
                                if (c.cardOwner == CardOwner.Player)
                                    clientIndexPlayer = 0;
                                else
                                    clientIndexPlayer = 1;
                            }
//						Debug.Log("check buff card" + battleId);
//						Debug.Log("check list card" + lstCardInBattle.Count);
//
                        BoardCard buffCard = lstCardInBattle.stream().filter(x -> x.battleID == battleId).findAny().orElse(null);
                        if (buffCard != null) {
                            if (buffCard.cardOwner == CardOwner.Player)
                                clientIndexPlayer = 0;
                            else
                                clientIndexPlayer = 1;
                        }
//						else
//						{
//							yield return new WaitForSeconds(0.5f);
//							buffCard = lstCardInBattle.FirstOrDefault(x => x.battleID == battleId);
//							if (buffCard != null)
//							{
//								if (buffCard.cardOwner == CardOwner.Player)
//									clientIndexPlayer = 0;
//								else
//									clientIndexPlayer = 1;
//							}
//						}
//
//						LogWriterHandle.WriteLog("GAME_SKILL_EFFECT 78: " + string.Join(",", cv.aLong) + "|" + clientIndexPlayer);
//						WriteLogBattle("GAME_SKILL_EFFECT 78:", GameData.main.profile.username, string.Join(",", cv.aLong));

//						for (Card card : GetListPlayerCardInBattle())
//						{
//
//							if (card.battleID == battleId && card.skill != null)
//							{
//								if (card.skill.id == skillId && card.skill.isUltiType)
//								{
//									GameData.main.isUsedUlti = true;
//									isUseUltimate?.Invoke(card.heroID);
//									Toast.Show(LangHandler.Get("81", "Ultimate Activated!"));
//								}
//								else if (card.skill.id == skillId && !card.skill.isUltiType)
//								{
//									card.countDoActiveSkill++;
//									onActiveSkillActive?.Invoke(skillId, battleId);
//								}
//							}
//						}
//						SkillSound soundSkill = CardData.Instance.GetCardSkillSound(skillId);
//						if (soundSkill != null)
//						{
//							string sound = soundSkill.soundID;
//							SoundHandler.main.PlaySFX(sound, "soundvfx");
//						}
                        switch ((int) effectId) {
                            case (int) DBHeroSkill.EFFECT_BUFF_HP: {
                                //buff hp for list hero
                                //[skillId, type_eff, battleId,[valueHpBuff1, buffBatleId1, heroHp1, HeroHpmax1]]
                                //GAME_SKILL_EFFECT 78: 40,2,72,5,72,4,5
                                //GAME_SKILL_EFFECT 78: 5,2,6,1,2,2,4,5
                                int BLOCK = 4;
                                int num = (cv.getALongCount() - 3) / BLOCK;
                                for (int i = 0; i < num; i++) {
                                    long valueHpBuff = cv.getALong(3 + i * BLOCK);
                                    long buffBatleId = cv.getALong(3 + i * BLOCK + 1);
                                    long heroHp = cv.getALong(3 + i * BLOCK + 2);
                                    long heroHpmax = cv.getALong(3 + i * BLOCK + 3);
                                    //Do buff HP ....
//                                    ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
                                    ;
//                                    if (buffBatleId < 0) {
//
//                                        List<TowerController> lstTower = lstTowerInBattle.stream().filter(t -> {
//                                            if (buffBatleId < -10) {
//                                                return t.pos == GetClientPosFromServerPos(1);
//                                            } else
//                                                return t.pos == GetClientPosFromServerPos(0);
//                                        }).collect(Collectors.toList());
//
//
//                                        long id = buffBatleId < -10 ? Math.abs(buffBatleId) - 11 : Math.abs(buffBatleId) - 1;
//                                        TowerController tower = lstTower.stream().filter(t -> t.id == id).findAny().orElse(null);
//
//                                        if (tower != null) {
//
//                                            if (info != null && buffCard != null) {
//                                                buffCard.OnCastSkill(skillId, effectId, tower.gameObject, () = >
//                                                        {
//                                                                tower.OnHealing(heroHpmax, valueHpBuff);
//                                                    });
//                                            } else
//                                                tower.OnHealing(heroHpmax, valueHpBuff);
//                                        }
//                                    }
//                                    else {
                                    BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == buffBatleId).findAny().orElse(null);
//                                        if (skillId == 40) {
//                                            if (buffCard != null) {
//
//                                                buffCard.OnGlory(() = >
//                                                        {
//                                                if (card != null) {
//                                                    if (info != null && buffCard != null) {
//                                                        buffCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
//                                                                {
//                                                                        card.OnHealing(valueHpBuff, heroHp, heroHpmax);
//                                                                });
//                                                    } else
//                                                        card.OnHealing(valueHpBuff, heroHp, heroHpmax);
//                                                }
//                                                    });
//                                            }
//                                        } else {
                                    if (card != null) {
//                                                if (info != null && buffCard != null) {
//                                                    buffCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
//                                                            {
//                                                                    card.OnHealing(valueHpBuff, heroHp, heroHpmax);
//                                                        });
//                                                } else
                                        card.OnHealing(valueHpBuff, heroHp, heroHpmax);
                                    }
//                                        }
//                                    }
                                }

                                //@Chau GAME_SKILL_EFFECT 78: 156,2,59,3,-3,15,15|0 hồi máu cho trụ

                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_TMP_INCREASE_ATK_AND_HP: {
                                //increase TMP ATK and HP
                                //[skillId,type_eff,battleId,[valueAtkBuff1,valueHpBuff1,buffBatleId1,heroAtk1,heroHp1,HeroHpmax1]]
                                //38,0,32,32,1,0,3,3,3
                                int BLOCK = 6;
                                int num = (cv.getALongCount() - 3) / BLOCK;
//								ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x => x.skillID == skillId && x.effectID == effectId);
                                for (int i = 0; i < num; i++) {

                                    long valueAtkBuff = cv.getALong(3 + i * BLOCK);
                                    long valueHpBuff = cv.getALong(3 + i * BLOCK + 1);
                                    long buffBatleId = cv.getALong(3 + i * BLOCK + 2);
                                    long heroAtk = cv.getALong(3 + i * BLOCK + 3);
                                    long heroHp = cv.getALong(3 + i * BLOCK + 4);
                                    long HeroHpmax = cv.getALong(3 + i * BLOCK + 5);
                                    //Do increse TMP ATK buff HP ....
                                    BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == buffBatleId).findAny().orElse(null);
//                                    LogWriterHandle.WriteLog("EFFECT_TMP_INCREASE_ATK_AND_HP = " + buffBatleId + " | " + valueAtkBuff + " | " + valueHpBuff + "|" + (card == null ? "NULL" : "NOT NULL"));
                                    if (card != null) {
//                                        if (info != null && buffCard != null) {
//                                            //LogWriterHandle.WriteLog(buffCard.gameObject.name);
//                                            buffCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
//                                                    {
//                                                            card.OnBuffEffect(valueAtkBuff, valueHpBuff, heroAtk, heroHp, HeroHpmax);
//                                                });
//                                        } else
                                        card.OnBuffEffect(valueAtkBuff, valueHpBuff, heroAtk, heroHp, HeroHpmax);

                                    }
                                }

//                                yield return new WaitForSeconds(0.3f);

                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_INCREASE_ATK_AND_HP: {
                                //increase ATK and HP
                                //[skillId,type_eff,battleId,[valueAtkBuff1,valueHpBuff1,buffBatleId1,heroAtk1,heroHp1,HeroHpmax1]]
                                int BLOCK = 6;
                                int num = (cv.getALongCount() - 3) / BLOCK;
//                                ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
                                ;
                                for (int i = 0; i < num; i++) {
                                    long valueAtkBuff = cv.getALong(3 + i * BLOCK);
                                    long valueHpBuff = cv.getALong(3 + i * BLOCK + 1);
                                    long buffBatleId = cv.getALong(3 + i * BLOCK + 2);
                                    long heroAtk = cv.getALong(3 + i * BLOCK + 3);
                                    long heroHp = cv.getALong(3 + i * BLOCK + 4);
                                    long HeroHpmax = cv.getALong(3 + i * BLOCK + 5);
                                    //Do increse ATK buff HP ....
                                    BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == buffBatleId).findAny().orElse(null);
                                    // do glory
//                                    if (skillId == 27 || skillId == 41 || skillId == 35) {
//
//                                        if (buffCard != null) {
//                                            buffCard.OnGlory(() = >
//                                                    {
//                                            if (card != null) {
//                                                //ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x => x.skillID == skillId && x.effectID == effectId);
//                                                if (info != null && buffCard != null) {
//                                                    buffCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
//                                                            {
//                                                                    card.OnBuffEffect(valueAtkBuff, valueHpBuff, heroAtk, heroHp, HeroHpmax);
//                                                        });
//                                                } else {
//                                                    card.OnBuffEffect(valueAtkBuff, valueHpBuff, heroAtk, heroHp, HeroHpmax);
//                                                }
//                                            }
//                                                });
//                                        }
//                                    } else {
//                                        if (card != null) {
//                                            if (info != null && buffCard != null) {
//                                                buffCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
//                                                        {
//                                                                card.OnBuffEffect(valueAtkBuff, valueHpBuff, heroAtk, heroHp, HeroHpmax);
//                                                    });
//
//                                            } else
                                    card.OnBuffEffect(valueAtkBuff, valueHpBuff, heroAtk, heroHp, HeroHpmax);
//                                        }
//                                    }
                                }
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_MANA_CREATE_SHARD: {
                                //mana create shard
                                //[skillId,type_eff, battleId,indexplayer,manaDegre, shardIncre, cur_mana, cur_shard]
                                //106,3,18,0,0,1,0,1 | 0
                                long battleID = cv.getALong(2);
                                long serverIndexPlayer = cv.getALong(3);
                                long manaDergee = cv.getALong(4);
                                long shardIncre = cv.getALong(5);
                                long cur_mana = cv.getALong(6);
                                long cur_shard = cv.getALong(7);
//                                LogWriterHandle.WriteLog("EFFECT_MANA_CREATE_SHARD 78 = " + cur_mana + " " + cur_shard + "  " + currentMana + " " + currentShard);
//                                yield return new WaitForSeconds(0.5f);
//                                var card = lstCardInBattle.FirstOrDefault(x = > x.battleID == battleID);
//                                if (card != null) {
//                                    if (card.heroInfo.heroNumber == 7 || card.heroInfo.heroNumber == 13 || card.heroInfo.heroNumber == 101) {
//                                        if (IsMeByServerPos(serverIndexPlayer)) {
//                                            Transform trans = PoolManager.Pools["Effect"].Spawn(shardFlyEffect);
//                                            trans.localRotation = Quaternion.Euler(new Vector3(-90f, 0, 0));
//                                            trans.localScale = new Vector3(0.5f, 0.5f, 0.5f);
//                                            TwoPointEffectHandle tp = trans.GetComponent < TwoPointEffectHandle > ();
//                                            tp.SetupEffect(card.transform.position, ButtonOrbController.instance.playerShard.transform.position, () = >
//                                                    {
//                                                            SkeletonAnimation skel = tp.GetComponent < SkeletonAnimation > ();
//                                            skel.AnimationState.SetAnimation(0, "start", false).Complete += delegate
//                                            {
//                                                if (manaDergee > 0)
//                                                    onUpdateMana ?.Invoke(0, cur_mana, ManaState.UseDone, 0);
//                                                        else
//                                                onUpdateMana ?.Invoke(0, cur_mana, ManaState.StartTurn, 0);
//                                                onUpdateShard ?.Invoke(0, cur_shard);
//                                            } ;
//                                                });
//                                        } else {
//                                            if (manaDergee > 0)
//                                                onUpdateMana ?.Invoke(1, cur_mana, ManaState.UseDone, 0);
//                                                else
//                                            onUpdateMana ?.Invoke(1, cur_mana, ManaState.StartTurn, 0);
//                                            onUpdateShard ?.Invoke(1, cur_shard);
//                                        }
//                                    } else if (card.heroInfo.heroNumber == 353 || card.heroInfo.heroNumber == 354) {
//                                        Transform trans = PoolManager.Pools["Effect"].Spawn(bathalaShard);
//                                        trans.position = Vector3.zero;
//                                        trans.GetComponent<ParticleEffectParentCallback> ()
//                                                .SetOnPlay();
//                                        onUpdateShard ?.Invoke(IsMeByServerPos(serverIndexPlayer) ? 0 : 1, cur_shard);
//                                    } else {
//                                        if (IsMeByServerPos(serverIndexPlayer)) {
//                                            if (manaDergee > 0)
//                                                onUpdateMana ?.Invoke(0, cur_mana, ManaState.UseDone, 0);
//                                                else
//                                            onUpdateMana ?.Invoke(0, cur_mana, ManaState.StartTurn, 0);
//                                            onUpdateShard ?.Invoke(0, cur_shard);
//                                        } else {
//                                            if (manaDergee > 0)
//                                                onUpdateMana ?.Invoke(1, cur_mana, ManaState.UseDone, 0);
//                                                else
//                                            onUpdateMana ?.Invoke(1, cur_mana, ManaState.StartTurn, 0);
//                                            onUpdateShard ?.Invoke(1, cur_shard);
//                                        }
//                                    }
//                                } else {

                                //Do mana create shard
                                if (IsMeByServerPos(serverIndexPlayer)) {
//                                        if (manaDergee > 0)
//                                            onUpdateMana ?.Invoke(0, cur_mana, ManaState.UseDone, 0);
//                                            else
//                                        onUpdateMana ?.Invoke(0, cur_mana, ManaState.StartTurn, 0);
//                                        onUpdateShard ?.Invoke(0, cur_shard);
                                    currentMana = cur_mana;
                                    currentShard = cur_shard;

                                }
//                                    else {
//                                        if (manaDergee > 0)
//                                            onUpdateMana ?.Invoke(1, cur_mana, ManaState.UseDone, 0);
//                                            else
//                                        onUpdateMana ?.Invoke(1, cur_mana, ManaState.StartTurn, 0);
//                                        onUpdateShard ?.Invoke(1, cur_shard);
//                                    }
//                                }
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_MOVE_HERO: {
                                //[skillId,type_eff, battleId,indexplayer,[BatleId1,row1,colum1]]
                                long serverIndexPlayer = cv.getALong(3);
                                CardSlot targetSlot1 = GetSlot(IsMeByServerPos(serverIndexPlayer) ? SlotType.Player : SlotType.Enemy, cv.getALong(5), cv.getALong(6));
                                CardSlot targetSlot2 = GetSlot(IsMeByServerPos(serverIndexPlayer) ? SlotType.Player : SlotType.Enemy, cv.getALong(8), cv.getALong(9));
                                BoardCard card1 = GetBoardCard(cv.getALong(4));
                                BoardCard card2 = GetBoardCard(cv.getALong(7));
                                card1.MoveToSlot(targetSlot1);
                                card2.MoveToSlot(targetSlot2);
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_DRAW_CARD: {
//                                [skillId,type_eff, battleId,indexplayer,[BatleId1,HeroId1,frame1,ATK1,HP1,HPMAX1,fragile1,pleeting1,cleave1.Pierce1, Breaker1, Combo1, Overrun1, Shield1,GodSlayer1,mana1]]
                                long serverIndexPlayer = cv.getALong(3);
                                int BLOCK = 16, START = 4;
                                int num = (cv.getALongCount() - START) / BLOCK;
                                //GAME_SKILL_EFFECT 78: 12,5,47,44,3,1,1,1,0,0,1,0,0,0,0,0
//                                ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId);
                                for (int i = 0; i < num; i++) {
                                    long newBattleId = cv.getALong(START + i * BLOCK);
                                    long newHeroId = cv.getALong(START + i * BLOCK + 1);
                                    long frame = cv.getALong(START + i * BLOCK + 2);
                                    long fragile = cv.getALong(START + i * BLOCK + 6);
                                    long fleeting = cv.getALong(START + i * BLOCK + 7);
                                    long cardMana = cv.getALong(START + i * BLOCK + 15);
                                    //add heroid --> check fragile va precide
                                    //Do draw card ....
//                                    if (info != null && buffCard != null) {
//                                        buffCard.OnCastSkill(skillId, effectId, null, () = > {});
//                                    }
                                    if (IsMeByServerPos(serverIndexPlayer)) {
                                        DBHero hero = Database.GetHero(newHeroId);
                                        AddNewCard(0, hero, newBattleId, frame, fleeting != 0,0,0 cardMana);
                                    }
//                                    else {
//                                        DBHero hero = new DBHero();
//                                        {
//                                            id = -1
//                                        } ;
//                                        AddNewCard(1, hero, -1, 1);
//                                    }
                                }
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_DEAL_DAME: {
                                //deal dame fountain
                                //[skillId,type_eff,battleId,[battleId1,dame1,hp1,Hp1Max]]
                                //-----------GAME_SKILL = 0,6,42,///-11,1,14
                                //GAME_SKILL_EFFECT 78: 43,6,36,41,1,4,5,41,1,3,5,41,1,2,5
                                //12,6,47,40,1,5,6
                                //11,6,27,-11,2,13
                                //GAME_SKILL_EFFECT 78: 83,6,41,3,1,3,6,14,1,1,2,16,1,0,1 | 0
                                long attackBattleId = cv.getALong(2);
                                int BLOCK = 4;
                                int num = (cv.getALongCount() - 3) / BLOCK;
                                float waitTime = 0;
                                boolean isAtkCardDead = false;
//								yield return new WaitForSeconds(0.5f);

                                BoardCard attackCard = lstCardInBattle.stream().filter(x -> x.battleID == attackBattleId).findAny().orElse(null);
                                for (int i = 0; i < num; i++) {
                                    long battleiD = cv.getALong(3 + i * BLOCK);
                                    long deal = cv.getALong(3 + i * BLOCK + 1);
                                    long newhp = cv.getALong(3 + i * BLOCK + 2);
                                    if (attackCard != null) {
                                        if (battleiD < 0) {
                                            List<TowerController> lstTower = lstTowerInBattle.stream().filter(t -> {
                                                if (battleiD < -10) {
                                                    return t.pos == GetClientPosFromServerPos(1);
                                                } else
                                                    return t.pos == GetClientPosFromServerPos(0);
                                            }).collect(Collectors.toList());

                                            long id = battleiD < -10 ? Math.abs(battleiD) - 11 : Math.abs(battleiD) - 1;
                                            TowerController defTower = lstTower.stream().filter(t -> t.id == id).findAny().orElse(null);


                                            if (defTower != null) {
//												ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x => x.skillID == skillId && x.effectID == effectId);
//												if (info != null)
//												{
//													if (skillId == 42)
//													{
//														for (int j = 0; j < deal; j++)
//														{
//															attackCard.OnCastSkill(skillId, effectId, defTower.gameObject, () =>
//																	{
//																			TowerDealDamage(battleiD, deal / deal, newhp);
//                                                                });
//															yield return new WaitForSeconds(0.5f);
//														}
//													}
//													else
//													{
//														attackCard.OnCastSkill(skillId, effectId, defTower.gameObject, () =>
//																{
//																		TowerDealDamage(battleiD, deal / deal, newhp);
//                                                            });
//													}
//												}
//												else
//												{
//													if (attackCard != null)
//														attackCard.OnAttackWithSkill(defTower.transform, delegate { TowerDealDamage(battleiD, deal, newhp); }, out waitTime);
//												}
                                                TowerDealDamage(battleiD, deal, newhp);
                                            }
                                        } else {
                                            BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == battleiD).findAny().orElse(null);

                                            if (card != null) {
                                                if (attackCard != null) {
//													ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x => x.skillID == skillId && x.effectID == effectId);
//													if (info != null)
//													{
//														attackCard.OnCastSkill(skillId, effectId, card.gameObject, () =>
//																{
//																		card.OnDamaged(deal, newhp);
//														if (newhp <= 0)
//														{
//															if (card.battleID == attackCard.battleID)
//															{
//																isAtkCardDead = true;
//															}
//															else
//															{
//																card.OnDeath();
//																SoundHandler.main.PlaySFX("BrokenCard", "sounds");
//																lstCardInBattle.Remove(card);
//															}
//														}
//                                                            });
//													}
//													else
//													{
//														attackCard.OnAttackWithSkill(card.transform, () =>
//																{
                                                    card.OnDamaged(deal, newhp);
                                                    if (newhp <= 0) {
                                                        if (card.battleID == attackCard.battleID) {
                                                            isAtkCardDead = true;
                                                        } else {
                                                            card.OnDeath();
//																SoundHandler.main.PlaySFX("BrokenCard", "sounds");
                                                            lstCardInBattle.remove(card);
                                                        }
                                                    }
//                                                            }
//															, out waitTime);
//													}
                                                } else {
                                                    card.OnDamaged(deal, newhp);
                                                    if (newhp <= 0) {
                                                        card.OnDeath();
//														SoundHandler.main.PlaySFX("BrokenCard", "sounds");
                                                        lstCardInBattle.remove(card);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (battleiD < 0) {
                                            List<TowerController> lstTower = lstTowerInBattle.stream().filter(t -> battleiD < -10 ? t.pos == GetClientPosFromServerPos(1) : t.pos == GetClientPosFromServerPos(0)).collect(Collectors.toList());
                                            long id = battleiD < -10 ? (long) Math.abs(battleiD) - 11 : (long) Math.abs(battleiD) - 1;
                                            TowerController defTower = lstTower.stream().filter(t -> t.id == id).findAny().orElse(null);
                                            if (defTower != null) {
                                                // long seige
//												if (skillId == 42 && attackCard.heroID == 40)
//												{
//													for (int j = 0; j < deal; j++)
//													{
//														TowerDealDamage(battleiD, deal / deal, newhp);
//														yield return new WaitForSeconds(0.5f);
//													}
//												}
//												else
//												{
                                                TowerDealDamage(battleiD, deal, newhp);
//												}
                                            }
                                        } else {
                                            BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == battleiD).findAny().orElse(null);
                                            if (card != null) {
                                                card.OnDamaged(deal, newhp);
                                                if (newhp <= 0) {
                                                    card.OnDeath();
//													SoundHandler.main.PlaySFX("BrokenCard", "sounds");
                                                    lstCardInBattle.remove(card);
                                                }
                                            }
                                        }
                                    }
//									waitTimeToBattle = waitTime;
//									yield return new WaitForSeconds(0.1f);
                                }
//								yield return new WaitForSeconds(waitTime);
                                if (isAtkCardDead) {
                                    attackCard.OnDeath();
//									SoundHandler.main.PlaySFX("BrokenCard", "sounds");
                                    lstCardInBattle.remove(attackCard);
                                }
                                break;
                            }

//                            case (int) DBHeroSkill.EFFECT_INCREASE_SPECIAL_PARAM: {
//
//                                //increase special param
//                                //[skillId,type_eff, battleId,[battleId1,cleave1Add,Pierce1Add, Breaker1Add, Combo1Add, Overrun1Add, Shield1Add, godSlayerAdd cleave1, Pierce1, Breaker1, Combo1, Overrun1, Shield1, GodSplayer]]
//                                int BLOCK = 18;
//                                int num = (cv.getALongCount() - 3) / BLOCK;
////                                ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
//                                ;
////                                if (skillId == 30 || skillId == 214) {
////                                    ultimateRenderTexture.gameObject.SetActive(true);
////                                    ultimateVideo.clip = CardData.Instance.GetVideo("Ares");
////                                    ultimateVideo.gameObject.SetActive(true);
////                                    ultimateVideo.Play();
////                                    yield return new WaitForSeconds((float) ultimateVideo.length);
////                                    ultimateRenderTexture.SetActive(false);
////                                    ultimateVideo.gameObject.SetActive(false);
////                                }
//
//                                for (int i = 0; i < num; i++) {
//                                    long battleIdReceive = cv.getALong(3 + i * BLOCK);
//                                    long cleaveAdd = cv.getALong(3 + i * BLOCK + 1);
//                                    long pierceAdd = cv.getALong(3 + i * BLOCK + 2);
//                                    long breakerAdd = cv.getALong(3 + i * BLOCK + 3);
//                                    long combo1Add = cv.getALong(3 + i * BLOCK + 4);
//                                    long overrun1Add = cv.getALong(3 + i * BLOCK + 5);
//                                    long shield1Add = cv.getALong(3 + i * BLOCK + 6);
//                                    long godSlayerAdd = cv.getALong(3 + i * BLOCK + 7);
//                                    long rootAdd = cv.getALong(3 + i * BLOCK + 8);
//                                    long cleave = cv.getALong(3 + i * BLOCK + 9);
//                                    long pierce = cv.getALong(3 + i * BLOCK + 10);
//                                    long breaker = cv.getALong(3 + i * BLOCK + 11);
//                                    long combo = cv.getALong(3 + i * BLOCK + 12);
//                                    long overrun = cv.getALong(3 + i * BLOCK + 13);
//                                    long shield = cv.getALong(3 + i * BLOCK + 14);
//                                    long godSlayer = cv.getALong(3 + i * BLOCK + 15);
//                                    long root = cv.getALong(3 + i * BLOCK + 16);
//                                    long tired = cv.getALong(3 + i * BLOCK + 17);
//                                    //Do increse special param ....
////                                    yield return new WaitForSeconds(0.5f);
//                                    BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == battleIdReceive).findAny().orElse(null);
//                                    if (card != null) {
////                                        if (info != null && buffCard != null) {
////                                            buffCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
////                                                    {
////                                                            card.OnAddSpecialBuff(skillId, cleaveAdd, pierceAdd, breakerAdd, combo1Add, overrun1Add, shield1Add, godSlayerAdd, cleave, pierce, breaker, combo, overrun, shield, godSlayer);
////                                                });
////                                        } else
//                                            card.OnAddSpecialBuff(skillId, cleaveAdd, pierceAdd, breakerAdd, combo1Add, overrun1Add, shield1Add, godSlayerAdd, cleave, pierce, breaker, combo, overrun, shield, godSlayer);
////                                    }
//                                }
//                                break;
//                            }
//                            case (int) DBHeroSkill.EFFECT_TMP_INCREASE_SPECIAL_PARAM: {
//                                //increase TMP special param
//                                //[skillId,type_eff, battleId,[battleId1,cleave1Add,Pierce1Add, Breaker1Add, Combo1Add, Overrun1Add, Shield1Add, GodSplayerAdd, cleave1, Pierce1, Breaker1, Combo1, Overrun1, Shield1, GodSplayer]]
//                                int BLOCK = 15;
//                                int num = (cv.aLong.Count - 3) / BLOCK;
//                                ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
//                                ;
//                                for (int i = 0; i < num; i++) {
//                                    long battleIdReceive = cv.aLong[3 + i * BLOCK];
//                                    long cleaveAdd = cv.aLong[3 + i * BLOCK + 1];
//                                    long pierceAdd = cv.aLong[3 + i * BLOCK + 2];
//                                    long breakerAdd = cv.aLong[3 + i * BLOCK + 3];
//                                    long combo1Add = cv.aLong[3 + i * BLOCK + 4];
//                                    long overrun1Add = cv.aLong[3 + i * BLOCK + 5];
//                                    long shield1Add = cv.aLong[3 + i * BLOCK + 6];
//                                    long godSlayerAdd = cv.aLong[3 + i * BLOCK + 7];
//
//                                    long cleave = cv.aLong[3 + i * BLOCK + 8];
//                                    long pierce = cv.aLong[3 + i * BLOCK + 9];
//                                    long breaker = cv.aLong[3 + i * BLOCK + 10];
//                                    long combo = cv.aLong[3 + i * BLOCK + 11];
//                                    long overrun = cv.aLong[3 + i * BLOCK + 12];
//                                    long shield = cv.aLong[3 + i * BLOCK + 13];
//                                    long godSlayer = cv.aLong[3 + i * BLOCK + 14];
//                                    //Do increse TMP special param ....
//                                    var card = lstCardInBattle.FirstOrDefault(x = > x.battleID == battleIdReceive);
//                                    if (card != null) {
//                                        if (info != null && buffCard != null) {
//                                            buffCard.OnCastSkill(skillId, effectId, null, () = >
//                                                    {
//                                                            card.OnAddSpecialBuff(skillId, cleaveAdd, pierceAdd, breakerAdd, combo1Add, overrun1Add, shield1Add, godSlayerAdd, cleave, pierce, breaker, combo, overrun, shield, godSlayer);
//                                                });
//                                        } else
//                                            card.OnAddSpecialBuff(skillId, cleaveAdd, pierceAdd, breakerAdd, combo1Add, overrun1Add, shield1Add, godSlayerAdd, cleave, pierce, breaker, combo, overrun, shield, godSlayer);
//                                    }
//                                }
//
//                                break;
//                            }
                            case (int) DBHeroSkill.EFFECT_READY: {
                                //[skillId,type_eff, battleId,[batleId1, isTired]
                                //ready hero
                                //GAME_SKILL_EFFECT 78: 59,9,19,19,1 | 0
                                //68,9,57,3,1 | 0
                                long attackBattleId = cv.getALong(2);
                                int BLOCK = 2;
                                int num = (cv.getALongCount() - 3) / BLOCK;
//                                yield return new WaitForSeconds(0.5f);
                                BoardCard attackCard = lstCardInBattle.stream().filter(x -> x.battleID == attackBattleId).findAny().orElse(null);


                                for (int i = 0; i < num; i++) {
                                    long battleIdReceive = cv.getALong(3 + i * BLOCK);
                                    long isTired = cv.getALong(3 + i * BLOCK + 1);
                                    // change state of hero
                                    BoardCard card = lstCardInBattle.stream().filter(x -> x.battleID == battleIdReceive).findAny().orElse(null);
                                    if (card != null) {
//                                        if (attackCard != null) {
//                                            ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
//                                            ;
//                                            if (info != null) {
//                                                attackCard.OnCastSkill(skillId, effectId, card.gameObject, () = >
//                                                        {
//                                                                card.SetTired(isTired);
//                                                card.SetMovable(isTired == 0);
//                                                    });
//                                            } else {
//                                                card.SetTired(isTired);
//                                                card.SetMovable(isTired == 0);
//                                            }
//                                        } else {
                                        card.SetTired(isTired);
//                                            card.SetMovable(isTired == 0);
//                                        }
                                    }
                                }
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_FIGHT: {
//                                ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
                                ;
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_SUMMON_VIRTUAL_HERO: {

                                //[skillId,type_eff, battleId,indexPlayer,[hủy, BattleId1,heroId1,frame1,Atk1,Hp1,fragile1,pleeting1,row1,colum1]]
                                //GAME_SKILL_EFFECT 78: 17,11,38,75,45,1,5,0,0,0,1
                                //fragile: tướng ở trên bàn chỉ tồn tại trong 1 turn hoặc 1 round
                                //precide: quân bài ở trên tay chỉ tồn tại dc 1 turn

                                long serverIndexPlayer = cv.getALong(3);

//								if (skillId == 17 || skillId == 234)
//								{
//									ultimateRenderTexture.gameObject.SetActive(true);
//									ultimateVideo.clip = CardData.Instance.GetVideo("Poseidon");
//									ultimateVideo.gameObject.SetActive(true);
//									ultimateVideo.Play();
//									yield return new WaitForSeconds((float)ultimateVideo.length);
//									ultimateRenderTexture.SetActive(false);
//									ultimateVideo.gameObject.SetActive(false);
//								}

                                int BLOCK = 10, START = 4;
                                int num = (cv.getALongCount() - START) / BLOCK;
//								ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x => x.skillID == skillId && x.effectID == effectId);
//								if (buffCard != null && info != null)
//								{
//									buffCard.OnCastSkill(skillId, effectId, null, () =>
//											{
//											});
//								}
                                for (int i = 0; i < num; i++) {
                                    long newBattleId = cv.getALong(START + i * BLOCK + 1);
                                    long heroId = cv.getALong(START + i * BLOCK + 2);
                                    long frame = cv.getALong(START + i * BLOCK + 3);
                                    long atk = cv.getALong(START + i * BLOCK + 4);
                                    long hp = cv.getALong(START + i * BLOCK + 5);
                                    long fragile = cv.getALong(START + i * BLOCK + 6);
                                    long fleeting = cv.getALong(START + i * BLOCK + 7);
                                    long row = cv.getALong(START + i * BLOCK + 8);
                                    long colum = cv.getALong(START + i * BLOCK + 9);
                                    //Do sumon virtual card
//                                    WriteLogBattle("SUMMON_VIRTUAL_HERO: ", "BattleID: " + newBattleId + ", ROW: " + row + ", COL: " + colum + ", HEROID: " + heroId, "");
                                    CardSlot slot = null;
//                                    Transform spawnEffect = null;
//                                    float delayTime = 0f;
//                                    GameObject objectToSpawn = null;
                                    CardOwner owner = CardOwner.Player;

                                    if (IsMeByServerPos(serverIndexPlayer)) {
                                        slot = playerSlotContainer.stream().filter(x -> x.xPos == row && x.yPos == colum).findAny().orElse(null);
//                                        objectToSpawn = m_MinionOnBoardCard;
                                        owner = CardOwner.Player;
                                    } else {
                                        slot = enemySlotContainer.stream().filter(x -> x.xPos == row && x.yPos == colum).findAny().orElse(null);
//                                        objectToSpawn = m_EnemyMinionOnBoardCard;
                                        owner = CardOwner.Enemy;
                                    }

                                    if (slot != null) {
//                                        if (buffCard != null) {
//                                            if (buffCard.heroInfo.type == DBHero.TYPE_GOD) {
//                                                switch (skillId) {
//                                                    case 14:
//                                                    case 16:
//                                                    case 17:
//                                                    case 233:
//                                                    case 234: {
//                                                        spawnEffect = poseidonSummonEffect;
//                                                        delayTime = 1f;
//                                                        DefaultSpawn();
//                                                        break;
//                                                    }
//                                                    case 126:
//                                                    case 228: {
//                                                        spawnEffect = hadesSkillSummonEffect;
//                                                        delayTime = 1.45f;
//                                                        StartCoroutine(CreateCardEffectAnimation(newBattleId, heroId, frame, objectToSpawn, slot.transform, spawnEffect, slot, owner, delayTime, (card) = >
//                                                                {
//                                                                        UpdatHeroMatrixSummon(card);
//                                                                }));
//                                                        break;
//                                                    }
//                                                    case 196:
//                                                    case 283: {
//                                                        spawnEffect = sontinhSummonEffect;
//                                                        delayTime = 1f;
//                                                        DefaultSpawn();
//                                                        break;
//                                                    }
//                                                    default: {
//                                                        spawnEffect = IsMeByServerPos(serverIndexPlayer) ? minionSkillSummonEffect : enemyMinionSkillSummonEffect;
//                                                        delayTime = 1f;
//                                                        DefaultSpawn();
//                                                        break;
//                                                    }
//                                                }
//                                            } else {
//                                                spawnEffect = IsMeByServerPos(serverIndexPlayer) ? minionSkillSummonEffect : enemyMinionSkillSummonEffect;
//                                                delayTime = 1f;
//                                                DefaultSpawn();
//                                            }
//                                        } else {
//                                            spawnEffect = IsMeByServerPos(serverIndexPlayer) ? minionSkillSummonEffect : enemyMinionSkillSummonEffect;
//                                            delayTime = 1f;
//                                            DefaultSpawn();
//                                        }
                                        CreateCard(newBattleId, heroId, frame

                                                , slot, owner,0,0,0

                                        ).UpdateHeroMatrix(atk, hp, hp, 0, 0, 0, 0, 0, 0, 0, 0, fragile, 0);
                                    }
//                                    void UpdatHeroMatrixSummon (BoardCard card)
//                                    {
//                                        card.UpdateHeroMatrix(atk, hp, hp, 0, 0, 0, 0, 0, 0, 0, 0);
//                                        card.isFragile = fragile != 0;
//                                        SoundHandler.main.PlaySFX("SummonCard", "sounds");
//                                    }
//
//                                    void DefaultSpawn ()
//                                    {
//                                        StartCoroutine(CreateCard(newBattleId, heroId, frame, objectToSpawn, slot.transform, spawnEffect, slot, owner, delayTime, (card) = >
//                                                {
//                                                        UpdatHeroMatrixSummon(card);
//                                            }));
//                                    }
                                }

                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_SUMMON_CARD_IN_HAND: {
                                //[skillId,type_eff, battleId,indexplayer,[BatleId1,HeroId1,frame1,ATK1,HP1,HPMAX1,fragile1,pleeting1,cleave1.Pierce1, Breaker1, Combo1, Overrun1, Shield1,godSlayer1, cardMana, hủy(0 ; ko, 1 : bị hủy vì có nhiều hơn 10 lá bài trên tay)]]
                                long serverIndexPlayer = cv.getALong(3);
                                int BLOCK = 17, START = 4;
                                int num = (cv.getALongCount() - START) / BLOCK;

//                                ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId && x.effectID == effectId)
                                ;
                                //GAME_SKILL_EFFECT 78: 12,5,47,44,3,1,1,1,0,0,1,0,0,0,0,0
                                //GAME_SKILL_EFFECT 78: 49,12,3,0,64,68,0,0,0,0,1,0,0,0,0,0,0 | 0
                                for (int i = 0; i < num; i++) {
                                    long newBattleId = cv.getALong(START + i * BLOCK);
                                    long newHeroId = cv.getALong(START + i * BLOCK + 1);
                                    long frame = cv.getALong(START + i * BLOCK + 2);
                                    long fragile = cv.getALong(START + i * BLOCK + 6);
                                    long fleeting = cv.getALong(START + i * BLOCK + 7);
                                    long cardMana = cv.getALong(START + i * BLOCK + 15);
                                    //add heroid --> check fragile va precide
                                    //Do draw card ....
//                                    if (info != null && buffCard != null) {
//                                        buffCard.OnCastSkill(skillId, effectId, null, () = >
//                                                {
//                                        if (IsMeByServerPos(serverIndexPlayer)) {
//                                            DBHero hero = Database.GetHero(newHeroId);
//                                            AddNewCard(0, hero, newBattleId, frame, fleeting == 1, card = >
//                                                    {
//                                                            card.isFleeting = fleeting != 0;
//                                            card.OnUpdateManaText(cardMana);
//                                            if (buffCard != null)
//                                                buffCard.SummonNewCard(card.transform.position, null);
//                                                    });
//                                        } else {
//                                            DBHero hero = new DBHero
//                                            {
//                                                id = -1
//                                            } ;
//                                            AddNewCard(1, hero, -1, 1, true);
//                                        }
//                                            });
//                                    } else {
                                    if (IsMeByServerPos(serverIndexPlayer)) {
                                        DBHero hero = Database.GetHero(newHeroId);
//                                            AddNewCard(0, hero, newBattleId, frame, fleeting == 1, card = >
//                                                    {
//                                                            card.isFleeting = fleeting != 0;
//                                            card.OnUpdateManaText(cardMana);
//                                            if (buffCard != null)
//                                                buffCard.SummonNewCard(card.transform.position, null);
//                                                });
                                        AddNewCard(0, hero, newBattleId, frame, fleeting == 1,0,0 cardMana);

                                    }
//                                        else {
//                                            DBHero hero = new DBHero
//                                            {
//                                                id = -1
//                                            } ;
//                                            AddNewCard(1, hero, -1, frame, true);
//                                        }
//                                    }
                                }


                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_USER_MANA_MAX: {
                                long serverIndexPlayer = cv.getALong(3);
                                //GAME_SKILL_EFFECT 78: 57,13,46,1,1,7 | 0
                                //@Chau them mana cho user
                                long addedMana = cv.getALong(4);
                                long currentMana = cv.getALong(5);
//                                yield return new WaitForSeconds(1);
//                                onUpdateMana ?.Invoke((int) GetClientPosFromServerPos(serverIndexPlayer), currentMana, ManaState.StartTurn, 0);
                                if ((int) GetClientPosFromServerPos(serverIndexPlayer) == 0)
                                    this.currentMana = currentMana;
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_LEAVE_CARD_IN_HAND: {
                                //skillId,type_eff, battleId,indexplayer,[battleId,heroId]
                                long serverIndexPlayer = cv.getALong(3);
                                int BLOCK = 2, START = 4;
                                int num = (cv.getALongCount() - START) / BLOCK;
                                //GAME_SKILL_EFFECT 78: 72,14,27,1,44,78 | 0

                                for (int i = 0; i < num; i++) {
                                    long deleteBattleId = cv.getALong(START + i * BLOCK);
                                    long deleteHeroId = cv.getALong(START + i * BLOCK + 1);

                                    List<HandCard> myCards = Decks[0].GetListCard();

                                    for (HandCard card : myCards)
                                        if (deleteBattleId == card.battleID) {
                                            Decks[0].RemoveCard(card, 0);
//                                        PoolManager.Pools["Card"].Despawn(card.transform);
                                            break;
                                        }
                                }
//                                Decks[0].ReBuildDeck();

                                break;
                            }
//                            case (int) DBHeroSkill.EFFECT_TMP_INCREASE_MANA_MAX: {
//                                LogWriterHandle.WriteLog("EFFECT_TMP_INCREASE_MANA_MAX:" + cv.aLong[5] + " mana");
//                                long serverIndexPlayer = cv.aLong[3];
//                                onUpdateMana ?.
//                                Invoke((int) GetClientPosFromServerPos(serverIndexPlayer), cv.aLong[5], ManaState.UseDone, 0);
//                                break;
//                            }
                            case (int) DBHeroSkill.EFFECT_INCREASE_HERO_MANA: {
                                //skillId,type_eff, battleId,[battleId1,mana1]
                                int BLOCK = 2, START = 3;
                                int num = (cv.getALongCount() - START) / BLOCK;

                                for (int i = 0; i < num; i++) {
                                    long battleID = cv.getALong(START + i * BLOCK);
                                    long additionMana = cv.getALong(START + i * BLOCK + 1);
                                    HandCard card = Decks[0].GetListCard().stream().filter(x -> x.battleID == battleID).findAny().orElse(null);
                                    if (card != null)
                                        card.OnUpdateManaText(additionMana);
                                }
                                break;
                            }
                            case (int) DBHeroSkill.EFFECT_TMP_INCREASE_HERO_MANA: {
                                //skillId,type_eff, battleId,[battleId1,mana1]
                                int BLOCK = 2, START = 3;
                                int num = (cv.getALongCount() - START) / BLOCK;

                                for (int i = 0; i < num; i++) {
                                    long battleID = cv.getALong(START + i * BLOCK);
                                    long additionMana = cv.getALong(START + i * BLOCK + 1);
                                    HandCard card = Decks[0].GetListCard().stream().filter(x -> x.battleID == battleID).findAny().orElse(null);
                                    if (card != null)
                                        card.OnUpdateManaText(additionMana);
                                }
                                break;
                            }
//                            case (int) DBHeroSkill.EFFECT_PLAY_ALL_COLOR_CARD: {
//                                currentAvailableRegion = cv.aLong[3];
//                                break;
//                            }
                            case (int) DBHeroSkill.EFFECT_PENATY: {
//                                WriteLogBattle("EFFECT_PENATY: ", "", string.Join(",", cv.aLong));
                                int BLOCK = 3, START = 3;
                                int num = (cv.getALongCount() - START) / BLOCK;
//                                float waitTime = 0;
//                                yield return new WaitForSeconds(0.5f);
//                                LogWriterHandle.WriteLog("than bi chet" + cv.aLong[2]);
                                //var attackCard = lstCardInBattle.FirstOrDefault(x => x.battleID == cv.aLong[2]);
                                // cv.along[2] la battle id cua than bi chet
                                for (int i = 0; i < num; i++) {
                                    long battleiD = cv.getALong(3 + i * BLOCK);
                                    long deal = cv.getALong(3 + i * BLOCK + 1);
                                    long newhp = cv.getALong(3 + i * BLOCK + 2);
                                    if (battleiD < 0) {
                                        List<TowerController> lstTower = lstTowerInBattle.stream().filter(t -> battleiD < -10 ? t.pos == GetClientPosFromServerPos(1) : t.pos == GetClientPosFromServerPos(0)).collect(Collectors.toList());
                                        ;
                                        long id = battleiD < -10 ? (long) Math.abs(battleiD) - 11 : (long) Math.abs(battleiD) - 1;
                                        TowerController defTower = lstTower.stream().filter(t -> t.id == id).findAny().orElse(null);
                                        if (defTower != null) {
//                                            ProjectileDataInfo info = ProjectileData.Instance.projectileInfo.FirstOrDefault(x = > x.skillID == skillId)
//                                            ;
//                                            if (info != null) {
//                                                TowerDealDamage(battleiD, deal, newhp);
//                                            } else {
//                                                TowerDealDamage(battleiD, deal, newhp);
//                                            }
                                            TowerDealDamage(battleiD, deal, newhp);
                                        }
                                    } else {
                                        // battle>0 -> card
//                                        WriteLogBattle("EFFECT_PENATY: ", "", string.Join(",", cv.aLong));
                                    }
                                }
                                break;
                            }

                        }
//                        HideMagicCard(battleId, true);
                    } else {
                        //StartCoroutine(HideMagicCard(currentCardSelected.battleID));
                    }
                    break;
                }

                case IService.GAME_BATTLE_ATTACK: {
////					yield return new WaitForSeconds(1);
//                    CommonVector cv = CommonVector.parseFrom(action.getData());
////	                    WriteLogBattle("BATTLE_ATTACK: ", "", string.Join(",", cv.aLong));
//
//                    long totalCount = cv.getALong(4) + 5;
//                    //long totalCount = cv.aLong[2] + 3;
//                    for (int i = 0; i < cv.getALongCount(); i += (int) totalCount) {
//                        BoardCard atkCard = null;
//                        for (int j = 0; j < lstCardInBattle.size(); j++)
//                            if (lstCardInBattle.get(j).battleID == cv.getALong(i)) {
//                                atkCard = lstCardInBattle.get(j);
//                                break;
//                            }
//
//                        // atk tower
//                        if (cv.getALong(i + 1) < 0) {
//                            ArrayList<TowerController> lstTower = new ArrayList<>();
//                            for (int j = 0; j < lstTowerInBattle.size(); j++)
//                                if (cv.getALong(i + 1) < -10 && lstTowerInBattle.get(j).pos == GetClientPosFromServerPos(1))
//                                    lstTower.add(lstTowerInBattle.get(j));
//                                else if (cv.getALong(i + 1) >= -10 && lstTowerInBattle.get(j).pos == GetClientPosFromServerPos(0))
//                                    lstTower.add(lstTowerInBattle.get(j));
////	                            var lstTower = lstTowerInBattle.Where(t => cv.aLong[i + 1] < -10 ? t.pos == GetClientPosFromServerPos(1) : t.pos == GetClientPosFromServerPos(0));
//
//                            long id = cv.getALong(i + 1) < -10 ? (long) Math.abs(cv.getALong(i + 1)) - 11 : (long) Math.abs(cv.getALong(i + 1)) - 1;
//
//                            TowerController defTower = null;
//
//                            for (int j = 0; j < lstTower.size(); j++)
//                                if (lstTower.get(j).id == id) {
//                                    defTower = lstTower.get(j);
//                                    break;
//                                }
//
//                            if (atkCard != null && defTower != null) {
//                                atkCard.OnAttackTower(defTower);
//                                //wait 1
//                            }
//                        }
//                        // atk card
//                        else {
//                            BoardCard defCard = null;
//                            for (int j = 0; j < lstCardInBattle.size(); j++)
//                                if (lstCardInBattle.get(j).battleID == cv.getALong(i + 1)) {
//                                    defCard = lstCardInBattle.get(j);
//                                    break;
//                                }
//                            if (atkCard != null && defCard != null) {
//                                atkCard.OnAttackCard(defCard);
//                                //wait
//                            }
//                        }
//
//                        int count = i + 6;
//                        ArrayList<SkillEffect> effects = new ArrayList<SkillEffect>();
//                        if (cv.getALong(i + 2) > 0) {
//                            SkillEffect eff = new SkillEffect();
//                            eff.typeEffect = DBHero.KEYWORD_GODSLAYER;
//                            effects.add(eff);
//                        }
//                        if (cv.getALong(i + 3) > 0) {
//                            SkillEffect eff = new SkillEffect();
//                            eff.typeEffect = DBHero.KEYWORD_DEFENDER;
//                            effects.add(eff);
//                        }
//                        // them ca eff cho breaker va godSlayer
//
//                        while (count < i + cv.getALong(i + 4) + 5) {
//                            // loai effect
//                            long typeEffect = cv.getALong(count);
//                            long defCount = cv.getALong(count + 1);
//                            SkillEffect eff = new SkillEffect();
//                            eff.typeEffect = typeEffect;
//                            eff.defCount = defCount;
//                            for (int j = 0; j < defCount; j++) {
//
//                                // effect here
//                                //cac quan ảnh huong : bai hoac tru,
//                                //switch case cho tung effect.  goi sang card, truyen vao loai effect và so luong bai kem theo id quan bi anh huong
//                                ArrayList<TowerController> lstTower = new ArrayList<>();
//                                if (cv.getALong(count + 2 + j) < 0) {
//                                    if (cv.getALong(i + 1) < -10)
//                                        for (int jj = 0; jj < lstTowerInBattle.size(); jj++) {
//                                            if (lstTowerInBattle.get(jj).pos == GetClientPosFromServerPos(1))
//                                                lstTower.add(lstTowerInBattle.get(jj));
//                                        }
//                                    else
//                                        for (int jj = 0; jj < lstTowerInBattle.size(); jj++)
//                                            if (lstTowerInBattle.get(jj).pos == GetClientPosFromServerPos(0))
//                                                lstTower.add(lstTowerInBattle.get(jj));
//
//                                    long id = cv.getALong(i + 1) < -10 ? (long) Math.abs(cv.getALong(i + 1)) - 11 : (long) Math.abs(cv.getALong(i + 1)) - 1;
//
//                                    for (int jj = 0; jj < lstTower.size(); jj++)
//                                        if (lstTower.get(jj).id == id) {
//                                            eff.lstTowerImpact.add(lstTower.get(jj));
//                                            break;
//                                        }
//                                } else {
//                                    for (int jj = 0; jj < lstCardInBattle.size(); jj++)
//                                        if (lstCardInBattle.get(jj).battleID == cv.getALong(i + 1)) {
//                                            eff.lstCardImpact.add(lstCardInBattle.get(jj));
//                                            break;
//                                        }
//                                }
//                                if (j == defCount - 1) {
//                                    effects.add(eff);
//                                }
//
//                            }
//                            count += (int) defCount + 2;
//                        }
//                        totalCount = cv.getALong(i + 4) + 5;
//
//                    }
                    break;
                }
                case IService.GAME_BATTLE_DEAL_DAMAGE: {
                    CommonVector cv1 = CommonVector.parseFrom(action.getData());
//	                    WriteLogBattle("DEAL_DAMAGE: ", "", string.Join(",", cv1.aLong));
                    //wait
                    for (int i = 0; i < cv1.getALongCount(); i += 3) {
                        if (cv1.getALong(i) < 0) {
                            TowerDealDamage(cv1.getALong(i), cv1.getALong(i + 1), cv1.getALong(i + 2));
                        } else {
//                                ArrayList<BoardCard> lstCard = new ArrayList<>();
//                                for (int j = 0; j < lstCardInBattle.size(); j++)
//                                    if (lstCardInBattle.get(j).battleID == cv1.getALong(i))
//                                        lstCard.add(lstCardInBattle.get(j));
//                                for (int j = 0; j < lstCard.size(); j++)
//                                    lstCard.get(j).OnDamaged(cv1.getALong(i + 1), cv1.getALong(i + 2));
                            long battleId = cv1.getALong(i);
                            BoardCard card = lstCardInBattle.stream().filter(c -> c.battleID == battleId).findAny().orElse(null);
                            card.OnDamaged(cv1.getALong(i + 1), cv1.getALong(i + 2));

                        }
                    }
                    break;
                }
                case IService.GAME_BATTLE_HERO_DEAD: {
                    ListCommonVector lstCv = ListCommonVector.parseFrom(action.getData());
                    //wait
                    for (int kk = 0; kk < lstCv.getAVectorCount(); kk++) {
                        CommonVector c = lstCv.getAVector(kk);
//	                        WriteLogBattle("HERO_DEAD: ", string.Join(",", c.aString), string.Join(",", c.aLong));

                        for (int i = 3; i < c.getALongCount(); i += 3) {
                            ArrayList<BoardCard> lstCard = new ArrayList<>();
                            for (int j = 0; j < lstCardInBattle.size(); j++)
                                if (lstCardInBattle.get(j).battleID == c.getALong(i)) {
                                    lstCard.add(lstCardInBattle.get(j));
                                }
                            for (BoardCard card : lstCard) {
                                card.OnDeath();
                                lstCardInBattle.remove(card);
                            }

                            // if no damage to tower, continue
                            if (c.getALong(i + 2) == -1) {
                                continue;
                            } else {
                                long towerID = 0;
                                if (GetServerPostFromUsername(c.getAString(0)) == 0) {
                                    towerID = c.getALong(i + 2) * -1;
                                } else {
                                    towerID = (c.getALong(i + 2) * -1) - 10;
                                }
                                TowerDealDamage(towerID, c.getALong(i + 1), c.getALong((int) c.getALong(i + 2) - 1));
                            }
                        }
                    }
                    break;
                }
                case IService.GAME_BATTLE_HERO_TIRED: {
                    CommonVector cv2 = CommonVector.parseFrom(action.getData());
//	                    WriteLogBattle("HERO_TIRED: ", "", string.Join(",", cv2.aLong));

//	                    wait
                    for (int j = 0; j < lstCardInBattle.size(); j++)
                        for (int jj = 0; jj < cv2.getALongCount(); jj++)
                            if (cv2.getALong(jj) == lstCardInBattle.get(j).battleID) {
                                lstCardInBattle.get(j).SetTired(1);
                            }

                    break;
                }
                case IService.GAME_BATTLE_HERO_READY: {
//					CommonVector cv3 = ISocket.Parse<CommonVector>(action.data);
//					WriteLogBattle("HERO_READY_SKILL_SIMULATE: ", GameData.main.profile.username, string.Join(",", cv3.aLong));
//
//					yield return new WaitForSeconds(1);

                    break;
                }
                case IService.GAME_SIMULATE_SKILLS_ON_BATTLE: {
                    ListAction listActionSkill = ListAction.parseFrom(action.getData());
//                        lstSkillQueue.addAll(listActionSkill.getAActionList());
                    for (Action ac : listActionSkill.getAActionList())
                        SimulateSkillEffect(ac);

                    break;

                    //StartCoroutine(SimulateSkillEffect(listActionSkill, true));
                    //yield return new WaitForSeconds(CalculateTimeForSkill(listActionSkill));
                }

            }

//			canContinue = true;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public List<BoardCard> GetListPlayerCardInBattle() {
        List<BoardCard> lst = lstCardInBattle.stream().filter(c -> c.cardOwner == CardOwner.Player).collect(Collectors.toList());
        return lst;
    }

    public List<BoardCard> GetListEnemyCardInBattle() {
        List<BoardCard> lst = lstCardInBattle.stream().filter(c -> c.cardOwner == CardOwner.Enemy).collect(Collectors.toList());
        return lst;
    }

    private void DeleteCardsOnHand(CommonVector commonVector, boolean attached) {
        // wait 0
        if (!attached)
            onProcessData = true;

        ArrayList<HandCard> myCards = Decks[0].GetListCard();

        for (int i = 0; i < commonVector.getALongCount(); i++)
            for (HandCard card : myCards) {
                if (commonVector.getALong(i) == card.battleID) {
                    Decks[0].RemoveCard(card, 0);
                    break;

                }

//                    PoolManager.Pools["Card"].Despawn(card.transform);
            }

        // wait 1
        if (!attached)
            onProcessData = false;
    }

    private void GameStartupEnd(ListCommonVector listCommonVector) {
        // wait 1
        onProcessData = true;
        float waitingTime = 0;
//	        onGameStartupEnd?.Invoke();

        for (int i = 0; i < listCommonVector.getAVectorCount(); i++) {
            CommonVector cv = listCommonVector.getAVector(i);

            if (!instance.username.equals(cv.getAString(0))) {
                if (cv.getALongCount() > 0) {
                    CardSlot slot = null;
                    for (int j = 0; j < enemySlotContainer.size(); j++)
                        if (enemySlotContainer.get(j).xPos == cv.getALong(0)
                                && enemySlotContainer.get(j).yPos == cv.getALong(1)) {
                            slot = enemySlotContainer.get(j);
                            break;
                        }

                    if (slot != null) {
                        // sua frame
                        CreateCard(cv.getALong(2), cv.getALong(3), cv.getALong(4),
//	                        		m_EnemyGodOnBoardCard, slot.transform, holySpawnEffect, 
                                slot, CardOwner.Enemy, cv.getALong(5), cv.getALong(6), cv.getALong(7)
//                                , 0.1f
                        );

                    }
//	                    onSpawnRandomGodEnemy?.Invoke(cv.aLong[3]);

                    waitingTime += 1f;
                }
            } else if (instance.username.equals(cv.getAString(0))) {
                if (cv.getALongCount() > 2) {
                    boolean exist = false;

                    for (int j = 0; j < lstCardInBattle.size(); j++) {
                        Card c = lstCardInBattle.get(j);
                        if (c.battleID == cv.getALong(2))
                            exist = true;
                    }

                    if (!exist) {
                        CardSlot slot = playerSlotContainer.stream().filter(x -> x.xPos == cv.getALong(0) && x.yPos == cv.getALong(1)).findAny().orElse(null);

                        if (slot != null) {
//                            if (cv.getALong(3) == 1) {
//                                // sua frame
//                                CreateCard(cv.getALong(2), cv.getALong(3), cv.getALong(4),
////	                                		m_GodCard, slot.transform, null,
//                                        slot, CardOwner.Player);
//                            } else {// sua frame
                            CreateCard(cv.getALong(2), cv.getALong(3), cv.getALong(4),
//	                                		m_GodCard, slot.transform, null,
                                    slot, CardOwner.Player,cv.getALong(5), cv.getALong(6), cv.getALong(7));
//                            }
//	                            Debug.Log("onSpawnRandomGod" + cv.aLong[2] + "/" + cv.aLong[3]);
//	                            onSpawnRandomGod?.Invoke(cv.aLong[2]);
                            spawnGod(cv.getALong(2));
                        }

                        waitingTime += 1f;
                    }
                }
            }
        }

        // wait 1
        onProcessData = false;
    }

    private void GameStartupConfirm(CommonVector commonVector) {
        // wait 1
        onProcessData = true;

//	        onGameStartupConfirm?.Invoke(GameData.main.profile.username.Equals(commonVector.aString[0]));

        // wait 1
        onProcessData = false;
    }

    private BoardCard GetBoardCard(long battleID) {

        for (int i = 0; i < lstCardInBattle.size(); i++) {

            BoardCard card = lstCardInBattle.get(i);
            if (card.battleID == battleID)
                return card;
        }

        return null;
    }

    private CardSlot GetSlot(SlotType type, long x, long y) {
        ArrayList<CardSlot> lstSlot = type == SlotType.Player ? playerSlotContainer : enemySlotContainer;

        for (int i = 0; i < lstSlot.size(); i++) {

            CardSlot slot = lstSlot.get(i);
            if (slot.xPos == x && slot.yPos == y)
                return slot;
        }
        return null;
    }

    private void GameMoveGodSumon(CommonVector commonVector) {
        // wait 0
        onProcessData = true;

//        if (commonVector.aLong[0] == 0)
//            Toast.Show(commonVector.aString[0]);

        BoardCard card = GetBoardCard(commonVector.getALong(1));
        if (card != null) {
            CardSlot slot = GetSlot(SlotType.Player, commonVector.getALong(4), commonVector.getALong(5));
            if (slot != null)
                card.MoveToSlot(slot);
        }

        // wait 0.5
        onProcessData = false;
    }

    private BoardCard CreateCard(long battleID, long heroID, long frame
//			 , GameObject spawnObject, Transform spawnPos, Transform effectToSpawn
            , CardSlot targetSlot, CardOwner owner, long atk, long hp, long mana
//            , float delay, ICallback.CallFunc2<BoardCard> callback = null
    ) {
//	        if (effectToSpawn != null)
//	        {
//	            ParticleSystem ps = PoolManager.Pools["Effect"].Spawn(effectToSpawn).GetComponent<ParticleSystem>();
//	            ps.transform.parent = targetSlot.transform;
//	            ps.transform.localRotation = Quaternion.Euler(Vector3.zero);
//	            ps.Play();
//	            ps.transform.localPosition = new Vector3(0, 0.1f, 0);
//	        }
//	        yield return new WaitForSeconds(delay);
        // wait delay
//	        Transform spawnCard = PoolManager.Pools["Card"].Spawn(spawnObject);
//	        spawnCard.parent = targetSlot != null ? targetSlot.transform : null;
//	        spawnCard.position = targetSlot != null ? targetSlot.transform.position : spawnPos.position;
//	        spawnCard.localRotation = Quaternion.Euler(Vector3.zero);

        BoardCard card = new BoardCard();
        card.SetBoardCardData(battleID, heroID, frame, owner, targetSlot, atk, hp, mana);
        if (targetSlot != null) {
            targetSlot.ChangeSlotState(SlotState.Full, card);
            card.slot = targetSlot;
            card.SetSummonAnimation();
            card.UpdatePosition();
        }
        lstCardInBattle.add(card);

        return card;
//	        callback?.Invoke(card);
    }
    private void GameFirstGodSummon(CommonVector commonVector) {
        // wait 0
        onProcessData = true;

        if (commonVector.getALong(0) == 0) {
//	            Toast.Show(commonVector.aString[0]);
            onProcessData = false;
        } else {
            // Destroy(currentGodCardUI);
            long godBattleId = commonVector.getALong(1);
            for (GodCardUI godCard : playerGodDeck)
                if (godCard.battleId == godBattleId) {
                    playerGodDeck.remove(godCard);
                    break;
                }


            for (int i = 0; i < playerSlotContainer.size(); i++) {
                CardSlot slot = playerSlotContainer.get(i);
                if (slot.xPos == commonVector.getALong(7) && slot.yPos == commonVector.getALong(8)) {
//                    if (commonVector.getALong(2) == 1) {
//                        CreateCard(commonVector.getALong(1), commonVector.getALong(2), 1,
////	        	            		   m_GodCard, slot.transform,null,
//                                slot, CardOwner.Player, 0.4f);
//
//                    } else {
                    CreateCard(godBattleId, commonVector.getALong(2), commonVector.getALong(3),
//	        						m_GodCard, slot.transform, null,
                            slot, CardOwner.Player, commonVector.getALong(4), commonVector.getALong(5), commonVector.getALong(6)
//                                , 0.4f
                    );
//                    }
                }
            }


            // wait 1
            onProcessData = false;
        }

    }

    private void GameDealCards(ListCommonVector listCommonVector) {
        onProcessData = true;
        //gì đó?
        // draw card
        // show than cua 2 ben
        // draw card cho 2 ben

        if (listCommonVector.getAVectorCount() >= 2) {

            IsYourTurn = true;
            turnCount = 0;

            CommonVector cv0 = listCommonVector.getAVector(0);
            CommonVector cv1 = listCommonVector.getAVector(1);

            if (instance.username.equals(cv0.getAString(0))) {
                for (int i = 1; i < cv0.getALongCount(); i += 6) {
                    AddListCard(lstGodPlayer, lstGodPlayerBattleID, lstGodPlayerFrame, cv0.getALong(i),
                            cv0.getALong(i - 1), cv0.getALong(i + 1),
                            cv0.getALong(i + 2),cv0.getALong(i + 3),cv0.getALong(i + 4));

                }
                for (int i = 1; i < cv1.getALongCount(); i += 6) {
                    AddListCard(lstGodEnemy, lstGodEnemyBattleID, lstGodEnemyFrame, cv1.getALong(i),
                            cv1.getALong(i - 1), cv1.getALong(i + 1),
                            cv0.getALong(i + 2),cv0.getALong(i + 3),cv0.getALong(i + 4));
                }
            } else {
                for (int i = 1; i < cv0.getALongCount(); i += 6) {
                    AddListCard(lstGodEnemy, lstGodEnemyBattleID, lstGodEnemyFrame, cv0.getALong(i),
                            cv0.getALong(i - 1), cv0.getALong(i + 1),
                            cv0.getALong(i + 2),cv0.getALong(i + 3),cv0.getALong(i + 4));
                }
                for (int i = 1; i < cv1.getALongCount(); i += 6) {
                    AddListCard(lstGodPlayer, lstGodPlayerBattleID, lstGodPlayerFrame, cv1.getALong(i),
                            cv1.getALong(i - 1), cv1.getALong(i + 1),
                            cv0.getALong(i + 2),cv0.getALong(i + 3),cv0.getALong(i + 4));
                }
            }

            InitGodUI(lstGodPlayerBattleID, lstGodPlayer, lstGodPlayerFrame, lstGodEnemyBattleID, lstGodEnemy, lstGodEnemyFrame);


            CommonVector cv2 = listCommonVector.getAVector(2);
            for (int i = 1; i < cv2.getALongCount(); i += 6) {
                AddListCard(lstHeroPlayer, lstHeroBattleID, lstHeroFrame, cv2.getALong(i), cv2.getALong(i - 1),
                        cv2.getALong(i + 1),
                        cv0.getALong(i + 2),cv0.getALong(i + 3),cv0.getALong(i + 4));
            }

            DrawDeckStart(0, lstHeroPlayer, lstHeroBattleID, lstHeroFrame);

//            ArrayList<DBHero> lstHero = new ArrayList<DBHero>();
//            ArrayList<Long> lstID = new ArrayList<Long>();
//            ArrayList<Long> lstFrame = new ArrayList<Long>();
//
//            for (int i = 1; i < cv2.getALongCount(); i += 3) {
//                DBHero hero = new DBHero();
//                hero.id = -1;
//                lstHero.add(hero);
//                lstID.add((long) -1);
//                lstFrame.add((long) 1);
//            }
//            DrawDeckStart(1, lstHero, lstID, lstFrame);

            // wait 1
            onProcessData = false;

            //BOT : summon first god
            long maxMana = 0;
            int index = -1;
            for (int i = 0; i < playerGodDeck.size(); i++) {
                DBHero god = playerGodDeck.get(i).hero;
                if (god.mana > maxMana) {
                    maxMana = god.mana;
                    index = i;
                }
            }
            long row, col;
            GodCardUI godCard = playerGodDeck.get(index);
            if (godCard.CurrentCardID == 347 || godCard.CurrentCardID == 349 || godCard.CurrentCardID == 350 || godCard.CurrentCardID == 351)
                row = 0;
            else row = 1;
            col = random.nextInt(3);
            SummonGodInReadyPhase(godCard, row, col);
            instance.session2.GameStartupConfirm();


        }
    }

    public void SummonGodInReadyPhase(GodCardUI godCard, long row, long col) {
        CommonVector cv = CommonVector.newBuilder()
                .addALong(godCard.battleId)
                .addALong(row)
                .addALong(col).build();
        instance.session2.GameFirstGodSummon(cv);
    }

    private void AddListCard(ArrayList<DBHero> lstHero, ArrayList<Long> lstID, ArrayList<Long> lstFrame, long heroID,
                             long battleID, long frame, long atk, long hp, long mana) {
        DBHero hero = Database.GetHero(heroID);
        hero.atk += atk;
        hero.hp += hp;
        hero.mana += mana;
        lstHero.add(hero);
        lstID.add(battleID);
        lstFrame.add(frame);
    }


    void PlayerSurrender(CommonVector commonVector) {
//	        yield return new WaitForSeconds(0f); wait 0

        if (isSurrender) {

            // wait 3
            // Game.main.LoadScene("SelectModeScene", delay: 0.3f, curtain: true);
        }


    }


    public void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void gambActionPerform(int actionId) {
        // TODO Auto-generated method stub

    }

    private HandCard GetHandCard(long battleID) {
        for (int i = 0; i < Decks[0].GetListCard().size(); i++)
            if (Decks[0].GetListCard().get(i).battleID == battleID)
                return Decks[0].GetListCard().get(i);

        return null;
    }

    private void HideMagicCard(long battleId, boolean isPlayer) {

        for (int i = 0; i < lstCardInBattle.size(); i++)
            if (lstCardInBattle.get(i).battleID == battleId && lstCardInBattle.get(i).heroInfo.type == DBHero.TYPE_TROOPER_MAGIC) {
                lstCardInBattle.remove(lstCardInBattle.get(i));
                break;
//	            PoolManager.Pools["Card"].Despawn(card.transform);
            }
    }


    private long GetClientPosFromServerPos(long serverPos) {
        for (int i = 0; i < mLstBattlePlayer.size(); i++)
            if (mLstBattlePlayer.get(i).position == serverPos)
                return mLstBattlePlayer.get(i).clientPostion;
        return -1;
    }

    private String GetUsernameFromServerPos(long serverPos) {
        for (int i = 0; i < mLstBattlePlayer.size(); i++)
            if (mLstBattlePlayer.get(i).position == serverPos)
                return mLstBattlePlayer.get(i).username;
        return "";
    }

    private long GetServerPostFromClientPos(long clientPos) {
        for (int i = 0; i < mLstBattlePlayer.size(); i++)
            if (mLstBattlePlayer.get(i).clientPostion == clientPos)
                return mLstBattlePlayer.get(i).position;

        return -1;
    }

    private long GetServerPostFromUsername(String username) {
        for (int i = 0; i < mLstBattlePlayer.size(); i++)
            if (mLstBattlePlayer.get(i).username.equals(username))
                return mLstBattlePlayer.get(i).position;

        return -1;
    }

    private long GetClientPostFromUsername(String username) {
        for (int i = 0; i < mLstBattlePlayer.size(); i++)
            if (mLstBattlePlayer.get(i).username.equals(username))
                return mLstBattlePlayer.get(i).clientPostion;
        return -1;
    }

    private void CalculateClientPosstion() {
        if (mLstBattlePlayer.size() < 0 || mLstBattlePlayer.size() > 2)
            return;

        for (int i = 0; i < mLstBattlePlayer.size(); i++) {
            BattlePlayer player = mLstBattlePlayer.get(i);
            if (IsMe(player.username)) {
                player.clientPostion = POS_6h;

                for (int j = 0; j < mLstBattlePlayer.size(); j++) {
                    BattlePlayer otherPlayer = mLstBattlePlayer.get(j);
                    if (!IsMe(otherPlayer.username))
                        otherPlayer.clientPostion = POS_12h;
                }
                break;
            }
        }


    }

    private boolean IsMe(String username) {
        if (username.equals(instance.username))
            return true;
        return false;
    }

    private boolean IsMeByClientPos(long clientPos) {
        if (clientPos == 0)
            return true;
        return false;
    }

    private boolean IsMeByServerPos(long serverPos) {
        return IsMe(GetUsernameFromServerPos(serverPos));
    }

    final int POS_6h = 0;
    final int POS_12h = 1;
    int MAX_PLAYER = 2;
}
