package edu.umkc.client;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientServer implements Runnable{ 
	static boolean found = false;
	static int port = 8181;
	static BufferedWriter fileOut;
    static ObjectInputStream inputStream;
    static ObjectOutputStream outputStream;
    public static DatagramPacket recvPkt = null;
	
	public void run(){
		byte[] buf = new byte[4096];
		byte[] data = new byte[4096];
		DatagramPacket response;
	    Scanner inputFile;
		// String fileName = "lmn.txt";
	     String directory = "C:\\Users\\Anvesh\\Desktop\\sharedFolder";   
	     String received = "";
	     
try {
		DatagramSocket UDSocket = new DatagramSocket(8017);
		recvPkt = new DatagramPacket(buf, buf.length);
		while(true)
		{
			System.out.println("Listening on: " + UDSocket);
			java.util.Arrays.fill(buf,(byte)0);
			UDSocket.receive(recvPkt);
			System.out.println(recvPkt);
			InetAddress ClientIP = recvPkt.getAddress();
			received = new String(recvPkt.getData()).trim();
			System.out.println("----Client is requesting me for : " + received);
			if(received.contains("ping"))
			{	
				received = received.substring(5);
				System.out.println("----Searching local Directory for file");
				findFile(received,new File(directory));
				java.util.Arrays.fill(data,(byte)0);
				if (found == true){data = "File Exists".getBytes(); System.out.println("----file exist");}
				if (found == false){data = "No Such File".getBytes();System.out.println("----file not exist");}
				System.out.println("----Clinet about to send response to peer request");
			    response = new DatagramPacket(data, data.length, ClientIP , 8015);
			    System.out.println(data.length);
			    UDSocket.send(response);
			}
			if(received.contains("download"))
			{
				received = received.substring(9);
				System.out.println("Searching local Directory for file for Download");
				findFile(received,new File(directory));
				if (found == false){data = "No Such File".getBytes();System.out.println("file not exits");}
				else
				{
					System.out.println(directory+"\\"+received );
					inputFile = new Scanner(new FileInputStream(new File(directory+"\\"+received)));

					while (inputFile.hasNextLine()) {
					    String thisLine = inputFile.nextLine();
					    System.out.println(thisLine);
					    java.util.Arrays.fill(data,(byte)0);
					    data = thisLine.getBytes();
					    response = new DatagramPacket(data, data.length, ClientIP, 8015);
					    System.out.println("Client Sending the Down packet to " + ClientIP);
						    
					    UDSocket.send(response);
					    
					}
				}
				
			}
		}
				
		//	new Thread(new queryResponse(UDSocket, recvPkt)).start();
	
	} 
catch (IOException e) 
	{
	    System.out.println("Connection Failed: " + port);
	    e.printStackTrace();

	}
   }
	
	 public static  void findFile(String name,File file)
	    {
		 System.out.println("----Directory inside " + file +":::" +"file name: "+ name);
	        File[] list = file.listFiles();
	        if(list!=null)
	        for (File fil : list)
	        { System.out.println("----files in directory:"+fil.getName());
	            if (fil.isDirectory())
	            {
	                findFile(name,fil);
	            }
	            
	            else if (name.equalsIgnoreCase(fil.getName()))
	            {
	                System.out.println("----File Found at Dirctory "+fil.getParentFile());
	                found = true;
	            }
	        }
	    }
}