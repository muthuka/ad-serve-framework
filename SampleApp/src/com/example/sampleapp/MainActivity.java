package com.example.sampleapp;

import java.util.List;

import nexage.AdSourceFactory;
import nexage.NexageAdSource;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import api.Ad;
import api.Connection;
import api.DynamicInfo;
import api.LocationData;
import api.StaticInfo;
import api.TransportType;
import api.listener.AdListener;

public class MainActivity extends Activity {
	
	private static String TAG = "Sample App";
	LocationManager locationManager;
	
	private DynamicInfo dinfo = new DynamicInfo() {
        @Override
        public LocationData getLocation() {
        	if (locationManager == null) {
        		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        	}
        	
        	Location bestResult = null;
            float bestAccuracy = Float.MAX_VALUE;
            long bestTime = Long.MIN_VALUE;
            
            List<String> matchingProviders = locationManager.getAllProviders();
            for (String provider: matchingProviders) {

                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    float accuracy = location.getAccuracy();
                    long time = location.getTime();
                    //there is a bug in the Samsung phones that it adds 1 day to the timestamp of GPS locations
                    //this hack will fix the problem most of the time
                    // (otherwise location time should never be greater than current time)
                    if (time > System.currentTimeMillis() && provider.equals("gps"))
                        time = time - AlarmManager.INTERVAL_DAY;

                    if ((accuracy < bestAccuracy)) {
                        Log.d(TAG, "getCoordinates 1: " + provider);
                        bestResult = location;
                        bestAccuracy = accuracy;
                        bestTime = time;
                    } else if (bestAccuracy == Float.MAX_VALUE && time > bestTime) {
                        Log.d(TAG, "getCoordinates 2: " + provider);
                        bestResult = location;
                        bestTime = time;
                    }
                }
            }
            
            LocationData toReturn = new LocationData();
            toReturn.latitude = bestResult.getLatitude();
            toReturn.longitude = bestResult.getLongitude();
            
            return toReturn;
        }

        @Override
        public TransportType getTransportType() {
            return null;
        }

        @Override
        public String getIpAddress() {
            return null;
        }
    };
    
    private StaticInfo sinfo = new StaticInfo() {

		@Override
		public int getAge() {
			return 30;
		}
	};
	
    Connection connection = new HttpConnection();
    NexageAdSource adSource = (NexageAdSource) AdSourceFactory.getInstance(connection, sinfo);

    AdListener listener = new AdListener() {

        @Override
        public void adReceived(Ad ad) {
            Log.v(TAG, "New ad available");
        }

        @Override
        public void adFailed(String reason, Exception errorInfo) {
            // Make adjustments to the ad space to remove it or do something with it
        }

    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button nexageAd = (Button)findViewById(R.id.button1);
		nexageAd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Log.v(TAG, "Invoke getting an ad");
				if (adSource != null) {
					adSource.setDcn("ab291fd4de624149a764abe6f06c45cd");
					adSource.setPosition("header");
					adSource.registerListener(listener);
					adSource.fetchAd(dinfo);
				}				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
