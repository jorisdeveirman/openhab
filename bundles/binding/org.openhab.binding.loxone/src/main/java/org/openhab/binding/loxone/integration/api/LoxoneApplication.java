package org.openhab.binding.loxone.integration.api;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class LoxoneApplication {
	private String version;
	private String date;
	private String serialNumber;
	private String friendlyName;
	private Collection<LoxoneImage> images = new ArrayList<LoxoneImage>();
	private Collection<LoxoneCategory> categories = new ArrayList<LoxoneCategory>();
	private Collection<LoxoneRoom> rooms = new ArrayList<LoxoneRoom>();
	private Collection<AbstractLoxoneFunction> functions = new ArrayList<AbstractLoxoneFunction>();

	public LoxoneApplication(String version, String date, String serialNumber,
			String friendlyName) {
		this.version = version;
		this.date = date;
		this.serialNumber = serialNumber;
		this.friendlyName = friendlyName;
	}

	public String getVersion() {
		return version;
	}

	public String getDate() {
		return date;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public Collection<LoxoneCategory> getCategories() {
		return categories;
	}

	public Collection<LoxoneRoom> getRooms() {
		return rooms;
	}

	public Collection<LoxoneImage> getImages() {
		return images;
	}

	public Collection<AbstractLoxoneFunction> getFunctions() {
		return functions;
	}

	public void addImage(int id, String link) {
		this.images.add(new LoxoneImage(id, link));
	}

	public void addCategory(int id, String name, LoxoneImage image, int rank) {
		this.categories.add(new LoxoneCategory(id, name, image, rank));
	}

	public void addRoom(int id, String name, LoxoneImage image) {
		this.rooms.add(new LoxoneRoom(id, name, image));
	}

	public void addFunction(AbstractLoxoneFunction function) {
		this.functions.add(function);
	}

	public AbstractLoxoneFunction findFunctionByUUID(final String uuid) {
		return Iterables.find(functions,
				new Predicate<AbstractLoxoneFunction>() {
					@Override
					public boolean apply(AbstractLoxoneFunction image) {
						return image.getUuidAction().equalsIgnoreCase(uuid);
					}
				}, null);
	}

	public LoxoneImage findImageById(final int id) {
		return Iterables.find(images, new Predicate<LoxoneImage>() {
			@Override
			public boolean apply(LoxoneImage image) {
				return image.getId() == id;
			}
		}, null);
	}

	public LoxoneCategory findCategoryById(final int id) {
		return Iterables.find(categories, new Predicate<LoxoneCategory>() {
			@Override
			public boolean apply(LoxoneCategory category) {
				return category.getId() == id;
			}
		}, null);
	}

	public LoxoneRoom findRoomById(final int id) {
		return Iterables.find(rooms, new Predicate<LoxoneRoom>() {
			@Override
			public boolean apply(LoxoneRoom room) {
				return room.getId() == id;
			}
		}, null);
	}
}
