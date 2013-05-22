package com.project.dataserver;

import java.io.File;

public class TimerThread implements Runnable{
	int i;
	int fileIdx=1;
	long startTime;
	long frameLengthInMillis = 10000;
	File srcFile;
	String inpFname = "coded";
	DataStreamer dataStreamer;
	
	public TimerThread(){
		
	}

	@Override
	public void run() {
		
		startTime = System.currentTimeMillis();
		
		srcFile = new File("res/" + inpFname	+ fileIdx);
		
		
		do {
			
			if (ReceiveRequest.serverSocket.isClosed()) {
				break;
			}
			
			System.out.println("Timer Thread: Source File " + srcFile);
			dataStreamer = new DataStreamer(srcFile, fileIdx);
			dataStreamer.run();
			
			try {
				Thread.sleep(frameLengthInMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			fileIdx++;
			srcFile = null;
			srcFile = new File("res/" + inpFname + fileIdx);
			
		}while (srcFile.exists() != Boolean.FALSE);
		
	}

}
