package nexage;

import api.Ad;
import api.AdSource;

public class SimpleAd extends Ad {
	public SimpleAd(AdSource source) {
		super(source);
	}

	private String content;

	public void clicked() {
	}

	public void rollover() {
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
