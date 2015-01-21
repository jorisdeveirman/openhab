package org.openhab.binding.loxone.integration.api;

public class LoxoneImage {
	private final int id;
	private final String link;

	public LoxoneImage(int id, String link) {
		this.id = id;
		this.link = link;
	}

	public int getId() {
		return id;
	}

	public String getLink() {
		return link;
	}

}
