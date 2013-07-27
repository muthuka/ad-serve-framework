package api;

import api.listener.ConnectionResultListener;

public interface Connection {

    void get(ConnectionParameters params);
    void post(ConnectionParameters params);
    
    void registerConnectionResultListener(ConnectionResultListener listener);
    void removeConnectionResultListener(ConnectionResultListener listener);

}
