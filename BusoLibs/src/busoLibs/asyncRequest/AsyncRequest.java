/*
 * AsyncRequest.java
 * 
 * Implements the class that makes asynchronous web requests
 * 
 * Copyright 2012 Shawn Busolits
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain a 
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 */

package busoLibs.asyncRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Makes asynchronous web requests
 * 
 * @author Shawn Busolits
 * @VERSION 1.0
 * 
 */
public class AsyncRequest extends Thread {

	public static enum REQUEST_TYPE {
		GET, POST
	};

	/** URL to be called for information */
	String url;
	/** Parameters for the call */
	List<NameValuePair> params;
	/** Headers for the request */
	List<NameValuePair> headers;
	/** Callback to make when the request completes */
	RequestCallback callback;
	/** Type of request */
	REQUEST_TYPE type;

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
			RequestCallback callback, REQUEST_TYPE type) {
		this.url = url;
		this.params = params;
		this.headers = null;
		this.callback = callback;
	}

	public AsyncRequest(String url, List<NameValuePair> params,
			List<NameValuePair> headers, RequestCallback callback,
			REQUEST_TYPE type) {
		this.url = url;
		this.params = params;
		this.headers = headers;
		this.callback = callback;
		this.type = type;
	}

	public void run() {
		if (type == REQUEST_TYPE.GET) {
			GET();
		} else {
			POST();
		}
	}

	/**
	 * Makes the request and calls the callback upon completion
	 */
	public void POST() {
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

			if (params != null) {
				httppost.setEntity(new UrlEncodedFormEntity(params));
			}

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

	public void GET() {
		// Taken from
		// http://stackoverflow.com/questions/693997/how-to-set-httpresponse-timeout-for-android-in-java
		HttpParams httpParams = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 10000;
		HttpConnectionParams
				.setConnectionTimeout(httpParams, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

		// Taken from
		// http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpGet httpget = new HttpGet(url);

		if (headers != null) {
			for (NameValuePair header : headers) {
				httpget.setHeader(header.getName(), header.getValue());
			}
		}

		try {

			HttpResponse response = httpclient.execute(httpget);
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
