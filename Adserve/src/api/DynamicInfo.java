package api;

public interface DynamicInfo {
    LocationData getLocation();
    TransportType getTransportType();
    String getIpAddress();
}
