package bem.net;

import java.util.Vector;

import bem.Configuration;
import bem.Logger;

public class OutputThread extends Thread {
	public boolean quit = false;
	private Vector messageQueue = new Vector();
	public SocketHandler socket;
	
	private long timeAddLastPkg = 0;

	public OutputThread(SocketHandler socket) {
		this.socket = socket;
		this.start();
		Logger.debug("-------- Started output thread");
	}
	
	public synchronized void queueMessage(K2Packet pkt) {
		long current = System.currentTimeMillis();
		long tmp = current - timeAddLastPkg;
//		if ((pkt.service != Constants.ACTION_LIST_AVATAR) 
//				&& (pkt.service != Constants.ACTION_LOGIN)
//				&& (pkt.service != Constants.ACTION_LEAVE_ROOM)
//				&& (pkt.service != Constants.ACTION_LEAVE_TABLE)
//				//vao ra ban, phong
//				&& (pkt.service != Constants.GET_MY_AVATAR) 
//				&& (pkt.service != Constants.UPDATE_GENDER) 				
//				&& (pkt.service != Constants.ACTION_LIST_ROOM) 
//				&& (pkt.service != Constants.ACTION_JOIN_ROOM) 
//				&& (pkt.service != Constants.ACTION_JOIN_TABLE) 
//				&& (pkt.service != Constants.ACTION_LIST_TABLE)				
//				&& (pkt.service != Constants.GET_NUMBER_TABLE)				
//				&& (pkt.service != Constants.LOAD_IMAGE)		
//				//lat 3 cay
//				&& (pkt.service != Constants.BACAY_FLIP_CARD)
//				&& (tmp < Configuration.INTERVAL_ADD_PKG)){
//			Logger.info("---------------- SKIP PAKAGE:"+pkt.service + " : " + tmp);
//			return;
//		}
		timeAddLastPkg = current;
		messageQueue.addElement(pkt);
		notify();
	}

	public synchronized K2Packet getNextK2Packet()
			throws InterruptedException {
		while (messageQueue.size() == 0)
			wait();		
		K2Packet ym = (K2Packet) messageQueue.elementAt(0);
		messageQueue.removeElementAt(0);
		return ym;
	}

	public void run() {
		try {
			while (!quit) {
				
				K2Packet pkt = getNextK2Packet();
				socket.sendPacket(pkt); 
				//
				Thread.sleep(100);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

	}
	
	
	
// 	public void stop(){
//		quit = true;
//	}
}
