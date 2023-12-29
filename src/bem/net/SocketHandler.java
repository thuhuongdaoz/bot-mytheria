package bem.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import bem.Logger;

public class SocketHandler {

	private String host;

	private int port;
	//
	private String altHost;

	private Socket socket;

	private DataInputStream ips;

	private DataOutputStream ops;

	public SocketHandler(String host, int port, String altHost) {
		this.host = host;
		this.port = port;
		this.altHost = altHost;
		
	}

	public void open() throws IOException {
		try {

			socket = new Socket(host, port); 
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("EXception =" +socket.isConnected() +"  "+socket.isClosed());
		}
		ips = new DataInputStream(socket.getInputStream());
		ops = new DataOutputStream(socket.getOutputStream());
	}

	void close() throws Exception {
		// to be honest, i dont know what kind of exception :)
		if (socket != null)
			socket.close();
		if (ips != null)
			ips.close();
		if (ops != null)
			ops.close();
		//
		ips = null;
		ops = null;
		socket = null;
	}

	/**
	 * Note: the term 'packet' here refers to a message, not a TCP packet
	 * (although in almost all cases the two will be synonymous). This is to
	 * avoid confusion with a 'message' - the actual discussion packet.
	 * 
	 * @param body
	 * @param service
	 * @param status
	 * @param sessionId
	 * @throws IOException
	 */
	public void sendNothing(int id){
		try {
			ops.write(id);
			ops.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	protected void sendPacket(K2Packet pkt) throws IOException {
		//format package 
		//2byte -> K2
		//4byte -> service id
		//4byte -> length
		//data[]
		synchronized (ops){
			try {
			ops.write(Constants.MAGIC2, 0, 2); // Magic code 'K2'
			//ops.writeInt(pkt.service);
			int service = pkt.service;
			byte[] bService = new byte[4];
 
			bService[0] = (byte) ((service >> 24) & 0xFF);
			bService[1] = (byte) ((service >> 16) & 0xFF);
			bService[2] = (byte) ((service >> 8) & 0xFF);
			bService[3] = (byte) ((service & 0xFF));
			ops.write(bService, 0, 4);
			
			if (pkt.body != null){
				//ops.writeInt(pkt.body.length);
				int length = pkt.body.length;
				byte[] bLength = new byte[4];
	 
				bLength[0] = (byte) ((length >> 24) & 0xFF);
				bLength[1] = (byte) ((length >> 16) & 0xFF);
				bLength[2] = (byte) ((length >> 8) & 0xFF);
				bLength[3] = (byte) ((length & 0xFF));
				ops.write(bLength, 0, 4);
				
				ops.write(pkt.body);
			}else {
				ops.writeInt(0);
			}
			//flush away
			ops.flush();
			//
			Logger.debug("================== SENT PACKAGE:" + pkt.service + " =================");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void sendPacket1(int id) throws IOException {
		//format package 
		//2byte -> K2
		//4byte -> service id
		//4byte -> length
		//data[]
		synchronized (ops){
			ops.write(Constants.MAGIC2, 0, 2); // Magic code 'K2'
			//ops.writeInt(pkt.service);
			int service = id;
			byte[] bService = new byte[4];
 
			bService[0] = (byte) ((service >> 24) & 0xFF);
			bService[1] = (byte) ((service >> 16) & 0xFF);
			bService[2] = (byte) ((service >> 8) & 0xFF);
			bService[3] = (byte) ((service & 0xFF));
			ops.write(bService, 0, 4);
			
//			if (pkt.body != null){
//				//ops.writeInt(pkt.body.length);
//				int length = pkt.body.length;
//				byte[] bLength = new byte[4];
//	 
//				bLength[0] = (byte) ((length >> 24) & 0xFF);
//				bLength[1] = (byte) ((length >> 16) & 0xFF);
//				bLength[2] = (byte) ((length >> 8) & 0xFF);
//				bLength[3] = (byte) ((length & 0xFF));
//				ops.write(bLength, 0, 4);
//				
//				ops.write(pkt.body);
//			}else {
//				ops.writeInt(0);
//			}
			//flush away
			ops.flush();
			//
			Logger.debug("================== SENT PACKAGE:" + id + " =================");
		}
	}

	protected K2Packet receivePacket() throws IOException,Exception {
		System.out.println("hhhhhhhhhhhhhh1");
		try {
			//magic
			byte[] magic = new byte[2];
			if (readBuffer(magic) <= 0) {
				Logger.debug("-------- Read null magic");
				return null;
			}
			//
			int service = readInt();
			int length = readInt();
			// #debug debug
			Logger.debug("================== RECEIVED PACKAGE:" + service
					+ " =================");
			Logger.debug("[magic = " + new String(magic)+ "; service = " 
					+ service + "; size = " + length+ "]");

			byte[] body = new byte[length];
			System.out.println("hhhhhhhhhhhhhh2");
			if (readBuffer(body) < 0){
				Logger.debug("-------- Read null body");
				return null;
			}
			K2Packet pkt = new K2Packet(service, body);
			return pkt;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	// -----------------------------------------------------------------
	// Reads an entire buffer of data, allowing for blocking (thread doc.
	// se doi o day den khi nao doc duoc du lieu thi thoi)
	// Returns bytes read (should be == to buffer size) or negative
	// bytes read if 'EOF' encountered.
	// -----------------------------------------------------------------
	private int readBuffer(byte[] buff) throws IOException {
		System.out.println("hhhhhhhhhhhhhh3");
		int p = 0, r = 0;
		while (p < buff.length) {
			r = ips.read(buff, p, buff.length - p);
			// System.out.println("doc gi "+r);
			if (r < 0)
				return (p + 1) * -1;
			else
				p += r;
		}
		
		// Logger.debug("packet "+new String(buff));
		return p;
	}
	
	private int readInt() throws IOException{
		return ips.readInt();
	}
}
