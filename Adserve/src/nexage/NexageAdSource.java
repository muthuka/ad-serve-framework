package nexage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.AdSource;
import api.Connection;
import api.ConnectionParameters;
import api.DynamicInfo;
import api.StaticInfo;
import api.listener.AdListener;
import api.listener.ConnectionResultListener;

public class NexageAdSource implements AdSource {

	protected static final String TAG = "Ad Source";
	private Connection connection = null;
	private StaticInfo staticInfo = null;
	private DynamicInfo dynamicInfo = null;
	
	private SimpleAd currentAd = new SimpleAd(this);
	private List<AdListener> listeners = new ArrayList<AdListener>();
	
	// Nexage Specific
	private String dcn;
	private String position;
	
	// Hold values needed to pass for connections ??
	private ConnectionParameters parameters = new ConnectionParameters();
	
	private ConnectionResultListener resultListener = new ConnectionResultListener() {

		@Override
		public void resultReceived(int statusCode, Map<String, String> headers, String body) {
			
			if (statusCode == 200) {
				// Fill currentAd
				currentAd.setContent(body);
				
				for (AdListener obj : listeners) {
					obj.adReceived(currentAd);
				}
			}
			else {
				currentAd = null;
				
				for (AdListener obj : listeners) {
					obj.adFailed("Unknown", null);
				}
			}
		}
		
	};
	
	public NexageAdSource(Connection c, StaticInfo s) {
		connection = c;
		staticInfo = s;
	}
	
	@Override
	public void fetchAd(DynamicInfo dinfo) {
		dynamicInfo = dinfo;

		// Form parameters
		parameters.protocol = "http";
		parameters.baseURL = "bos.ads.nexage.com/adServe?";
		parameters.additionalParameters = getParameters();
		
		// Start getting ads with connection & parameters
		connection.registerConnectionResultListener(resultListener);
		connection.get(parameters); // GET method
	}
	
	@Override
	public void registerListener(AdListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(AdListener listener) {
		listeners.remove(listener);
	}


	// May need to pass Static & Dynamic if it needs to be cleaner
	private HashMap<String, String> getParameters() {
		HashMap<String, String> toReturn = new HashMap<String, String>();
		
		if (staticInfo != null) {
			if (staticInfo.getAge() > 0)
				toReturn.put("u(age)", staticInfo.getAge() + "");
		}
		
		if (dynamicInfo != null) {
			if (dynamicInfo.getLocation() != null) {
				toReturn.put("req(loc)", dynamicInfo.getLocation().latitude + "," + dynamicInfo.getLocation().longitude);
			}
		}
		
		// Other needed Nexage Parameters
		toReturn.put("dcn", dcn);
		toReturn.put("pos", position);
		
		return toReturn;
	}

	public String getDcn() {
		return dcn;
	}

	public void setDcn(String dcn) {
		this.dcn = dcn;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}
