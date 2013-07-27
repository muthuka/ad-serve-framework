package com.example.sampleapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import api.Connection;
import api.ConnectionParameters;
import api.listener.ConnectionResultListener;

/**
 * Created by marumugam on 7/22/13.
 */
public class HttpConnection implements Connection {

	private List<ConnectionResultListener> listeners = new ArrayList<ConnectionResultListener>();

	@Override
	public void get(final ConnectionParameters params) {

		if (params.protocol.equalsIgnoreCase("http")) {
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// Call Nexage server
					String endURL = "http://"
							+ params.baseURL
							+ getURLParameters(params.additionalParameters);
					Log.v("Http Connection", "Trying URL: " + endURL);
					
					HttpURLConnection conn = null;
			        InputStream in = null;
			        BufferedReader reader = null;
			        String bodyContent = "";
			        
			        try {
			        	URL url = new URL(endURL);
			            HttpURLConnection.setFollowRedirects(true);
			            conn = (HttpURLConnection)url.openConnection();
			            conn.setConnectTimeout(10000);
			            conn.setReadTimeout(10000);
			            conn.setDoInput(true);
			            conn.setRequestProperty("Connection", "close");
			            conn.setRequestMethod("GET");
			            
			            int responseCode = conn.getResponseCode();
			            if (responseCode == 200 || responseCode == 201) {
			            	in = conn.getInputStream();
			            	StringBuilder dataBuilder = new StringBuilder();
			            	reader = new BufferedReader(new InputStreamReader(in), 4096);
			            	String line;
			            	while ((line = reader.readLine()) != null) {
			            		dataBuilder.append(line);
			            		dataBuilder.append(System.getProperty("line.separator"));
			            	}
			            	
			            	bodyContent = dataBuilder.toString();
			            	
			            	for (ConnectionResultListener obj : listeners) {
			    				obj.resultReceived(responseCode, null, bodyContent);
			    			}
			            }
			        }
			        catch (Exception e) {
			        	// notify listeners
			        	for (ConnectionResultListener obj : listeners) {
			        		obj.resultReceived(404, null, null);
						}
			        	
			        	Log.v("Http Connection", e.toString());
			        }
			        finally {
			            if (in != null) {
			                try {
			                    in.close();
			                }
			                catch (Exception e) {
			                }
			            }
			            if (reader != null) {
			                try {
			                    reader.close();
			                }
			                catch (Exception e) {
			                }
			            }
			            if (conn != null) {
			                try {
			                    conn.disconnect();
			                }
			                catch (Exception e) {
			                }
			            }
			        }
			        
					Log.v("Http Connection", "Body: " + bodyContent);
					
				}
			}).start();
			

		} else {
			// Notify listeners with 404 Error
			for (ConnectionResultListener obj : listeners) {
				obj.resultReceived(404, null, null);
			}
		}
	}

	private String getURLParameters(HashMap<String, String> additionalParameters) {
		StringBuilder builder = new StringBuilder("mode=test&sdk=MMK&v=1.0");
		builder.append("&dcn=" + additionalParameters.get("dcn"));
		builder.append("&pos=" + additionalParameters.get("pos"));
		
		Log.v("Http Connection", builder.toString());
		return builder.toString();

	}

	@Override
	public void post(ConnectionParameters params) {
	}

	@Override
	public void registerConnectionResultListener(
			ConnectionResultListener listener) {

		listeners.add(listener);
	}

	@Override
	public void removeConnectionResultListener(ConnectionResultListener listener) {

		listeners.remove(listener);

	}
}
