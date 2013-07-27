package api;

import api.listener.AdListener;

public interface AdSource {

    void fetchAd(DynamicInfo dinfo);
    
    void registerListener(AdListener listener);
    void removeListener(AdListener listener);
}
