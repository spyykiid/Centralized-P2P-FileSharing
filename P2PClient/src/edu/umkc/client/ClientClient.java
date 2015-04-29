package edu.umkc.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ClientClient {

	private byte[] data = new byte[4096];
	private  byte[] buf = new byte[4096];
	private static DatagramPacket recvPkt = null;
	private Scanner scan = null;
	private DatagramSocket UDSocket = null,DSocket = null;
	private int choice = 0;
	private String filename = "";
	private String downloadString = "";
	private File file;
	private static BufferedWriter fileOut;
	private InetAddress serverIP = null;
	private String ipaddresses ;
	private DatagramPacket packet;
	private String responseFromPeer;
	public ClientClient() throws IOException {
		scan = new Scanner(System.in);

		try {
			serverIP = InetAddress.getByName("192.168.0.11");
			System.out.println("ServerIP: "+serverIP + "\n"+"ClientIP: "+InetAddress.getLocalHost());
			UDSocket = new DatagramSocket(8185);
			DSocket = new DatagramSocket(8015);
			recvPkt = new DatagramPacket(buf, buf.length);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		runClientMenu();
	}

	public void runClientMenu() throws IOException {
		
		do {
			System.out.println("-----------MENU-----------");
			System.out.println("1-Update centralized directory");
			System.out.println("2-Search for peers");
			System.out.println("3-Ping a peer");
			System.out.println("4-Download a file");
			System.out.println("0-Exit");
			System.out.println("Enter your choice 0/1/2/3/4");
			choice = scan.nextInt();
			switch (choice) {
			case 1:
				updateDirectory();
				break;
			case 2:
				System.out.println("Filename to be searched: ");
				filename = scan.next();
				file = new File(filename);
				search(filename);
				break;
			case 3:  try {
					pingPeer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 4:  download();
				break;
			default:	
				System.out.println("Invalid choice");
			}
		} while (true);
	}

	public void updateDirectory() {

		try {
			java.util.Arrays.fill(data,(byte)0);
			data = "config: ipaddress:10.151.2.116:8017;./shared;xyz.txt,lmn.txt".getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length,serverIP, 8182);
			
			UDSocket.send(packet);
			
			java.util.Arrays.fill(buf,(byte)0);
			UDSocket.receive(recvPkt);
			System.out.println("Status of directory update:"+ new String(recvPkt.getData()).trim());
			recvPkt = new DatagramPacket(buf, buf.length);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void search(String filename) {

		try {
			java.util.Arrays.fill(data,(byte)0);
			data = filename.getBytes();
			packet = new DatagramPacket(data, data.length,
					serverIP, 8182);
			
			UDSocket.send(packet);
			
			java.util.Arrays.fill(buf,(byte)0);
			UDSocket.receive(recvPkt);
			
			ipaddresses = new String(recvPkt.getData()).trim();
			System.out.println("Following peers have the file: " +ipaddresses);
			//System.out.println("");
			
			

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void pingPeer() throws IOException { 
		java.util.Arrays.fill(data,(byte)0);
		data = ("ping "+filename).getBytes(); 
		
		for (String ipvalue : ipaddresses.split(";")) {
			String[] ipvalv = ipvalue.split(":");
			InetAddress iaddr = InetAddress.getByName(ipvalv[1]);
			System.out.println("**Our Client requeting: " + iaddr + " Port: "+ ipvalv[2] +" For file");
			packet = new DatagramPacket(data, data.length, iaddr, Integer.parseInt(ipvalv[2]));
			// System.out.println("Test class packet:"+packet.getData().toString());
			
			UDSocket.send(packet);
		}
		
		java.util.Arrays.fill(data,(byte)0);
		DSocket.receive(packet);
		
		responseFromPeer= new String(packet.getData()).trim();
		System.out.println("**Responce from Peer: " +responseFromPeer);
				}
	public void download() throws IOException{
		
		if(responseFromPeer.equals("File Exists")){
			new Thread(new download(DSocket,packet)).start();
		}

	}

	class download implements Runnable {
		
		private DatagramSocket dsocket = null;
		private DatagramPacket dpacket = null;
		private DatagramPacket packet = null;
		private DatagramPacket download = null;
		
		public download(DatagramSocket dsocket, DatagramPacket dpacket) throws IOException{
			System.out.println("Dsocket in constructor:"+dsocket.getLocalPort());
			this.dsocket = dsocket;
			System.out.println("Dsocket in constructorx:"+this.dsocket.getLocalPort());
			this.dpacket = dpacket;
			
			
			
		}
		public void run() {
			java.util.Arrays.fill(data,(byte)0);
			data = ("download "+filename).getBytes(); 
			InetAddress clientIP = dpacket.getAddress();
			System.out.println("***Client requesting for download from "+ clientIP);
			packet = new DatagramPacket(data, data.length, clientIP, 8017);
			
			
			try {
				dsocket.send(packet);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			java.util.Arrays.fill(data,(byte)0);
			download = new DatagramPacket(data,data.length);
			while (true) {
				System.out.println("Listening for download packet: " + dsocket.getLocalPort());
				try {
					System.out.println("about to send the packet");
					java.util.Arrays.fill(data,(byte)0);
					System.out.println("buffere cleared");
					dsocket.receive(download);
				
					System.out.println("Downloaded packet:" + download);
					System.out.println("Download data"+download.getData());
					//replystatus = new String(download.getData()).trim();
					//System.out.println("Reply: " + replystatus);
					FileWriter fileWriter;
					fileWriter = new FileWriter(file.getName(), true);
					fileOut = new BufferedWriter(fileWriter);
					downloadString = new String(download.getData()).trim();
					fileOut.write(downloadString + "\n");
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		
	}
	
}}
