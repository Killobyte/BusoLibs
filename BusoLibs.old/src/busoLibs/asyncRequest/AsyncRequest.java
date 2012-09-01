/*
 * AsyncRequest.java
 * Implements the class that makes asynchronous web requests
 * Copyright (C) Shawn Busolits, 2012 All Rights Reserved
 */

package busoLibs.asyncRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Makes asynchronous web requests
 * 
 * @author Shawn Busolits
 * @VERSION 1.0
 * 
 */
public class AsyncRequest extends Thread {

	/** URL to be called for information */
	String url;
	/** Parameters for the call */
	List<NameValuePair> params;
	/** Callback to make when the request completes */
	RequestCallback callback;

	/**
	 * Initializes the class with the provided URL and callback
	 * 
	 * @param url
	 *            URL to hit for data
	 * @param params
	 *            Parameters to pass to the URL
	 * @param callback
	 *            Callback to make when the request returns
	 */
	public AsyncRequest(String url, List<NameValuePair> params,
			RequestCallback callback) {
		this.url = url;
		this.params = params;
		this.callback = callback;
	}

	/**
	 * Makes the request and calls the callback upon completion
	 */
	public void run() {
		try {
			// Taken from
			// http://stackoverflow.com/questions/693997/how-to-set-httpresponse-timeout-for-android-in-java
			HttpParams httpParams = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParams,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 10000;
			HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

			HttpClient httpclient = new DefaultHttpClient(httpParams);
			HttpPost httppost = new HttpPost(url);

			httppost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse response = httpclient.execute(httppost);

			// Taken from
			// http://stackoverflow.com/questions/2845599/how-do-i-parse-json-from-a-java-httpresponse
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String result = reader.readLine();

			callback.doOnResult(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
