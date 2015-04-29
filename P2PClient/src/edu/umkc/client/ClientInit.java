package edu.umkc.client;

import java.io.IOException;


public class ClientInit {

	public static void main(String[] args) throws IOException {
		Thread tread = new Thread(new ClientServer());
		tread.start();
		new ClientClient();
	}

}
