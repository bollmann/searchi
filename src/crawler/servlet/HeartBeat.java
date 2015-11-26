package crawler.servlet;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import crawler.clients.HttpClient;
import crawler.requests.Http10Request;
import crawler.servlet.multinodal.status.WorkerStatus;

public class HeartBeat extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	String heartBeatUrl;
	WorkerStatus workerStatus;
	
	public HeartBeat(String heartBeatUrl) {
		this.heartBeatUrl = heartBeatUrl;
	}
	
	public void setWorkerStatus(WorkerStatus workerStatus) {
		this.workerStatus = workerStatus;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sendHeartBeat(heartBeatUrl);
			
		}
	}
	
	public void sendHeartBeat(String heartBeatUrl) {
		Http10Request request = new Http10Request();
		request.setMethod("GET");
		request.addHeader("User-Agent", "cis455crawler");
		String paramString = workerStatus.toParameters();
		logger.info("Sending heartbeat to " + heartBeatUrl + "?" + paramString);
		
		try {
			HttpClient.get(heartBeatUrl + "?" + paramString, request);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
