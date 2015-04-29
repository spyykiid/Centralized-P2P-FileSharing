package edu.umkc.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class QueryDirectory1 implements Runnable {

	private Scanner inputFile;
	private DatagramSocket UDsocket;
	private DatagramPacket packet, response;
	private String regex = ";";
	private byte[] data;
	private String filename;
	private String ipaddress = "";

	public QueryDirectory1(DatagramSocket UDsocket, DatagramPacket packet) {
		this.UDsocket = UDsocket;
		this.packet = packet;
	}

	public DatagramPacket search() {
		filename = new String(packet.getData()).trim();
		try {
			inputFile = new Scanner(new FileInputStream(new File(
					"directory.txt")));
			//System.out.println("Filename"+filename );
			while (inputFile.hasNextLine()) {
				String thisLine = inputFile.nextLine();
				//System.out.println(thisLine);
				if (thisLine.contains(filename)) {
					String[] value = thisLine.split(";");
					ipaddress = ipaddress + value[0] +";"; 					
					System.out.println(ipaddress);
				}
				data = ipaddress.getBytes();
				response = new DatagramPacket(data, data.length,
						packet.getAddress(), 8185);
				UDsocket.send(response);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void run() {
		System.out.println("runmethod");
		this.search();
		 
	}

}
