package com.project.dataserver;

import java.net.InetAddress;

public class Clients {
	
	private InetAddress IP;
	private int port;
	
	public Clients (Clients client){
		this.IP = client.getIPAddress();
		this.port = client.getPort();
	}
	
	public Clients() {
		// TODO Auto-generated constructor stub
	}

	void setIPAddress (InetAddress IP){
		this.IP = IP;
	}
	
	void setPort (int port){
		this.port = port;
	}
	
	InetAddress getIPAddress(){
		return IP;
	}
	
	int getPort(){
		return port;
	}

}
