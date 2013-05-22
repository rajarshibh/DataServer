package com.project.dataserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import ch.epfl.arni.ncutils.f256.F256CodedPacket;

public class DataStreamer implements Runnable {

	int index;
	int bound = 30;
	int retVal = -1;
	int track = 0;
	int codedPktLen = 1460;
	int noOfBlocks = 10;
	int numberOfPktsToSend = 15;
	int staticIdentifier = 12345;
	byte[] header;
	byte[] pktData;
	byte[] codedData;
	float deficit = 0;
	Clients client;
	Random rand = new Random(System.nanoTime());
	DatagramPacket packet;
	DataOutputStream dataOutStream;
	ByteArrayOutputStream byteOutStream;
	DatagramSocket socket;

	File srcFile = null;
	FileInputStream inputStream = null;
	F256CodedPacket[] codedPkt = new F256CodedPacket[30];

	public DataStreamer(File srcFile, int track) {
		this.track = track;
		this.srcFile = srcFile;
	}

	public void run() {

		try {
			inputStream = new FileInputStream(srcFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket = ReceiveRequest.serverSocket;

		for (int itr = 0; itr < bound; itr++) {
			byte[] temp = new byte[codedPktLen];
			// Reading 1460 bytes from the precoded packets
			try {
				retVal = inputStream.read(temp, 0, codedPktLen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error reading from the coded files!");
				e.printStackTrace();
			}
			// Reconstructing the coded packets from the byte stream
			codedPkt[itr] = new F256CodedPacket(noOfBlocks, temp, 0,
					temp.length);
		}
		System.out.println("DataStreamer: Total elements " + ReceiveRequest.clientList.size());
		for (int j = 0; j < numberOfPktsToSend; j++) {
			for (int i = 0; i < ReceiveRequest.clientCount; i++) {

				client = new Clients(ReceiveRequest.clientList.get(i));

				byteOutStream = new ByteArrayOutputStream();
				dataOutStream = new DataOutputStream(byteOutStream);
				header = new byte[8];
				pktData = new byte[1468];

				try {
					dataOutStream.writeInt(staticIdentifier);
					dataOutStream.writeInt(track);
					// System.out.println("Writing deficit " + deficit);
					dataOutStream.writeFloat(deficit);
					dataOutStream.close();
					header = byteOutStream.toByteArray();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				codedData = new byte[1460];
				System.arraycopy(header, 0, pktData, 0, 8);

				index = (j*ReceiveRequest.clientCount + i)%bound 
						+ (j*ReceiveRequest.clientCount + i)/bound ;//rand.nextInt(bound);
				codedData = codedPkt[index].toByteArray();
				System.arraycopy(codedData, 0, pktData, 8, codedPktLen);
				//System.out.println("DataStreamer: Sending to " + client.getIPAddress() + " packet no. " 
						//+ j + " index " + index);
				packet = new DatagramPacket(pktData, pktData.length,
						client.getIPAddress(), client.getPort());
				try {
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		System.out.println();
		return;

	}
}
