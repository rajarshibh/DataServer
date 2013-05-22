package com.project.dataserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import ch.epfl.arni.ncutils.FiniteField;
import ch.epfl.arni.ncutils.UncodedPacket;
import ch.epfl.arni.ncutils.f256.F256;
import ch.epfl.arni.ncutils.f256.F256CodedPacket;


public class InitialiseFile{

	
	int part = 0;
	int retVal = 0;
	int bound = 30;
	int frameNumber = 0;
	int noOfBlocks = 10;
	int payloadLen = 1450;
	File srcFile;
	File destFile;
	FiniteField finField;
	BufferedInputStream inpStream;
	FileOutputStream outStream;
	F256CodedPacket[] codedPkt;
	byte[] temp = null;
	
	public void startInitialise() {
		
		finField = F256.getF256();
		srcFile = new File("res","madari.mp3");	
		try {
			inpStream = new BufferedInputStream(new FileInputStream(srcFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		 		
		try {
			while (inpStream.available() > 0){
				
				codedPkt = new F256CodedPacket[bound];
				UncodedPacket[] inputPackets = new UncodedPacket[noOfBlocks];
				
				for(int itr = 0; itr < noOfBlocks; itr++) 
				{
					try {
					    temp = new byte[payloadLen];
						retVal = inpStream.read(temp, 0, payloadLen);
					} catch (IOException e) {
						e.printStackTrace();
					}
				    inputPackets[itr] = new UncodedPacket(itr, temp);
				}
				        			
				F256CodedPacket[] codewords = new F256CodedPacket[noOfBlocks];
	
			    for (int itr = 0; itr < noOfBlocks; itr++) 
			    {
			        codewords[itr] = new F256CodedPacket(inputPackets[itr], noOfBlocks);
			    }
			    long seed = System.nanoTime();
			    Random rand = new Random(seed);
			   
				for(int itr = 0; itr < bound; itr++) 
				{
					codedPkt[itr] = new F256CodedPacket(noOfBlocks, payloadLen);
				    for(int i = 0 ; i < noOfBlocks ; i++) 
				    {
				        int x = rand.nextInt(finField.getCardinality());                
				        F256CodedPacket tempPkt = codewords[i].scalarMultiply(x);
				        codedPkt[itr] = codedPkt[itr].add(tempPkt);
				    }					    					  
				}

				if(frameNumber%40 == 0){
					System.out.println( "Created " + frameNumber + " files");
				}
				frameNumber++;
				destFile = new File("res","coded" + frameNumber);
				try {
					outStream = /*new BufferedOutputStream*/(new FileOutputStream(destFile));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					for (int i=0; i<bound; i++){
						outStream.write(codedPkt[i].toByteArray(), 0, codedPkt[i].getLengthInBytes());
						outStream.flush();
					}
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("File initialisation complete");		
		return;
		
	}

}
