package api.listener;
import java.util.EventListener;
import java.util.Map;


public interface ConnectionResultListener extends EventListener {

    void resultReceived(int statusCode, Map<String, String> headers, String body);
}
