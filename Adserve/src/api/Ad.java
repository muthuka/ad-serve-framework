package api;

public abstract class Ad {
	public Ad(AdSource source) {
		// Need Ad Source for subsequent getAd calls or Rollover
	}
    public abstract void clicked();
    public abstract void rollover();
}
