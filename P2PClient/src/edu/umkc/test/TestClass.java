package edu.umkc.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestClass {
	public static DatagramPacket recvPkt = null;
	static BufferedWriter fileOut;

	public static void main(String[] args) throws Exception {
		byte[] data = null;
		DatagramSocket UDSocket = new DatagramSocket(8015);
		byte[] buf = new byte[4096];
		recvPkt = new DatagramPacket(buf, buf.length);
		String replystatus="";
		String filename = "lmn.txt";
		String downloadString = "";
		File file = new File(filename);
		DatagramSocket socket = new DatagramSocket();
		try {

			data = ("download "+filename).getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length,
					InetAddress.getLocalHost(), 8017);
			//System.out.println("Test class packet:"+packet.getData().toString());
			socket.send(packet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		while (true) {
			System.out.println("Listening on: " + UDSocket);
			UDSocket.receive(recvPkt);
			System.out.println(recvPkt);
			replystatus= new String(recvPkt.getData()).trim();
			System.out.println("Reply: "+replystatus);
			FileWriter fileWriter = new FileWriter(file.getName(),true);
			fileOut = new BufferedWriter(fileWriter);
			downloadString = new String(recvPkt.getData()).trim();
			fileOut.write(downloadString + "\n");
			fileOut.close();
		} 
			
	}

}
