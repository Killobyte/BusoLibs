/*
 * AsyncRequest.java
 * Implements the class that makes asynchronous web requests
 * Copyright (C) Shawn Busolits, 2012 All Rights Reserved
 */

package busoLibs.asyncRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Makes asynchronous web requests
 * @author Shawn Busolits
 * @VERSION 1.0
 *
 */
public class AsyncRequest extends Thread {
	
	/** URL to be called for information */
	URL url;
	/** Callback to make when the request completes */
	RequestCallback callback;
	
	/**
	 * Initializes the class with the provided URL and callback
	 * @param url URL to hit for data
	 * @param callback Callback to make when the request returns
	 */
	public AsyncRequest(URL url, RequestCallback callback) {
		this.url = url;
		this.callback = callback;
	}
	
	/**
	 * Makes the request and calls the callback upon completion
	 */
	public void run() {
		try {
			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    	        	
			StringBuilder responseBuilder = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) { 
				responseBuilder.append(inputLine);
			}
			in.close();
			    	        	
			String result = responseBuilder.toString();
			
			callback.doOnResult(result);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
