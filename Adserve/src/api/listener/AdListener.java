package api.listener;

import api.Ad;

public interface AdListener {
    void adReceived(Ad ad);
    void adFailed(String reason, Exception errorInfo);
}
