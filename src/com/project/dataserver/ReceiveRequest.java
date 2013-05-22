package com.project.dataserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceiveRequest {
	
	private static final int MAX_CLIENTS = 20;
	static byte[] connectPktData= new byte[1500];
	static DatagramPacket connectPkt;
	static DatagramPacket replyPkt;
	static DatagramSocket serverSocket;
	static ArrayList<Clients> clientList= new ArrayList(); 
	static Clients client;
	static DataInputStream dis;
	static DataOutputStream dos;
	static ByteArrayInputStream bais;
	static ByteArrayOutputStream baos;
	static int clientCount = 0;
	static String fileInitialisationResponse;
	static InitialiseFile initialiseFile;
	static TimerThread mainThread;
	
	public static void main (String[] args){
		
		
		System.out.print("Initialise the media file ? (y/n) ");
		InputStreamReader istream = new InputStreamReader(System.in) ;
        BufferedReader bufRead = new BufferedReader(istream) ;
		try {
			fileInitialisationResponse = bufRead.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (fileInitialisationResponse.equals("y")){
			initialiseFile = new InitialiseFile();
			initialiseFile.startInitialise();
			
		}
		
		/*client = new Clients();
		try {
			client.setIPAddress(InetAddress.getByName("192.168.0.102"));
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		client.setPort(9864);
		clientList.add(client);
		clientCount++;
		
		client = new Clients();
		try {
			client.setIPAddress(InetAddress.getByName("192.168.0.101"));
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		client.setPort(9864);
		clientList.add(client);
		clientCount++;*/
		
		
		try {
			serverSocket = new DatagramSocket(9864);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mainThread = new TimerThread();
		new Thread(mainThread).start();
		
		while (true) {
			connectPkt = new DatagramPacket(connectPktData,
					connectPktData.length);
			try {
				serverSocket.receive(connectPkt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bais = new ByteArrayInputStream(connectPktData);
			dis = new DataInputStream(bais);
			try {
				System.out.println("Received Request " + dis.readInt()
						+ " from " + connectPkt.getAddress().toString());
				client = new Clients();
				client.setIPAddress(connectPkt.getAddress());
				client.setPort(connectPkt.getPort());
				clientList.add(client);
				clientCount++;
				System.out.println("Total elements " + clientList.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			try {
				dos.writeInt(12345);
				dos.flush();
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			replyPkt = new DatagramPacket(baos.toByteArray(),
					baos.toByteArray().length, connectPkt.getAddress(), 9864);

			try {
				serverSocket.send(replyPkt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
	}

}
