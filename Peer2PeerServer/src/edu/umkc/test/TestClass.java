package edu.umkc.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestClass {
	public static DatagramPacket recvPkt = null;

	public static void main(String[] args) throws Exception {
		byte[] data = null;
		//DatagramSocket UDSocket = new DatagramSocket(8185);
		byte[] buf = new byte[4096];
		recvPkt = new DatagramPacket(buf, buf.length);
		String replystatus="";
		

		DatagramSocket socket = new DatagramSocket();
		try {

			data = "lmn".getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length,
					InetAddress.getLocalHost(), 8182);
			//System.out.println("Test class packet:"+packet.getData().toString());
			socket.send(packet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		/*while (true) {
			System.out.println("Listening on: " + UDSocket);
			UDSocket.receive(recvPkt);
			System.out.println(recvPkt);
			replystatus= new String(recvPkt.getData()).trim();
			System.out.println("Reply: "+replystatus);
		} */
			
	}

}
