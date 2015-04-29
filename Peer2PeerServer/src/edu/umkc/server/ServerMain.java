package edu.umkc.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ServerMain {

	static int port = 8181;
	static BufferedWriter fileOut;
	static ObjectInputStream inputStream;
	static ObjectOutputStream outputStream;
	public static DatagramPacket recvPkt = null;
	public static InetAddress ClientIP= null;

	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[4096];
		byte[] data = new byte[4096];
		String payload= "";
		File file = new File("directory.txt");
		DatagramPacket response;
		try {
			DatagramSocket UDSocket = new DatagramSocket(8182);
			recvPkt = new DatagramPacket(buf, buf.length);
			while (true) {
				System.out.println("Listening on: " + UDSocket);
				
				java.util.Arrays.fill(buf,(byte)0);
				UDSocket.receive(recvPkt);
				
				//System.out.println(recvPkt);
				payload= new String(recvPkt.getData()).trim();
				ClientIP = recvPkt.getAddress();
				System.out.println("Payload:"+payload);
				
				if (payload.contains("config")) {
					payload = payload.substring(8);
					System.out.println("Config Request received at Server :"+payload);
					FileWriter fileWritter = new FileWriter(file.getName(),true);
	    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    	        payload =  payload+"\n";
	    	        bufferWritter.write(payload);
	    	        bufferWritter.close();
	    	        
	    	        java.util.Arrays.fill(data,(byte)0);
					data = "Config Updated Successfully".getBytes();
					
					System.out.println("About to send response success\n");
					response = new DatagramPacket(data, data.length, recvPkt.getAddress(), 8185);
					UDSocket.send(response);
					recvPkt = new DatagramPacket(buf, buf.length);
				} else {
					
					Thread tread = new Thread(new QueryDirectory(UDSocket, payload, ClientIP ));
					//tread.getPriority();
					tread.start();
					//tread.start();
				}
			}

		} catch (IOException e) {
			System.out.println("Connection Failed: " + port);
			e.printStackTrace();

		}
	}
}
class QueryDirectory implements Runnable {

	private Scanner inputFile;
	private DatagramSocket UDsocket;
	private DatagramPacket response;
	private InetAddress clientIP; 
	private byte[] data;
	private String filename;
	private String ipaddress = "";
	private FileInputStream fileInputstrm;  
	public QueryDirectory(DatagramSocket UDsocket, String Data, InetAddress ClientIP) throws FileNotFoundException {
		this.UDsocket = UDsocket;
		this.clientIP = ClientIP;
		fileInputstrm = new FileInputStream(new File("directory.txt"));
		filename = Data;
		inputFile = new Scanner(fileInputstrm);
	}

	public DatagramPacket search() {
		try {
			System.out.println(" ClientIp: " +clientIP);
			System.out.println("searching file: "+filename );
			while (inputFile.hasNextLine()) {
				String thisLine = inputFile.nextLine();
				//System.out.println(thisLine);
				if (thisLine.contains(filename)) {
					String[] value = thisLine.split(";");
					ipaddress = ipaddress + value[0] +";"; 					
				}
			}
			fileInputstrm.close();
			System.out.println("All IP addresses : "+ipaddress);
			data = new byte[4096];
			java.util.Arrays.fill(data,(byte)0);
			data = ipaddress.getBytes();
			
			response = new DatagramPacket(data, data.length,clientIP, 8185);
			System.out.println("Response sent at Server to client"+ clientIP);
			UDsocket.send(response);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void run() {
	//	System.out.println("About to run SEARCH at SERVER ");		
		this.search();
	}
}
