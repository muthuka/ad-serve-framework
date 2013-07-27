package nexage;


import api.AdSource;
import api.Connection;
import api.StaticInfo;

public class AdSourceFactory {

    public static AdSource getInstance(Connection c, StaticInfo i) {
    	return new NexageAdSource(c, i);
    }

}
