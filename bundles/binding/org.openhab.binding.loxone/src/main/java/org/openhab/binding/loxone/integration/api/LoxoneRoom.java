package org.openhab.binding.loxone.integration.api;

public class LoxoneRoom {
	private final int id;
	private final String name;
	private final LoxoneImage image;

	public LoxoneRoom(int id, String name, LoxoneImage image) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LoxoneImage getImageId() {
		return image;
	}

}
