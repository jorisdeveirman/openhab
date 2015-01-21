package org.openhab.binding.loxone.integration.api;

public class LoxoneCategory {
	private final int id;
	private final String name;
	private final LoxoneImage image;
	private final int rank;

	public LoxoneCategory(int id, String name, LoxoneImage image, int rank) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.rank = rank;
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

	public int getRank() {
		return rank;
	}

}
