package org.openhab.binding.loxone.integration.impl;

import java.util.Collection;

import org.json.simple.JSONObject;
import org.openhab.binding.loxone.integration.api.LoxoneApplication;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHelper;

class LoxoneApplicationParser {
	public LoxoneApplication parseApplication(JSONObject root) {
		String version = LoxoneJsonHelper.property(root, "LoxLIVE.Version");
		String date = LoxoneJsonHelper.property(root, "LoxLIVE.Date");
		String sn = LoxoneJsonHelper.property(root, "LoxLIVE.SN");
		String friendlyName = LoxoneJsonHelper.property(root,
				"LoxLIVE.friendlyname");
		LoxoneApplication application = new LoxoneApplication(version, date,
				sn, friendlyName);
		parseImages(root, application);
		parseCategories(root, application);
		parseRooms(root, application);
		parseFunctions(root, application);
		return application;
	}

	private void parseImages(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> images = LoxoneJsonHelper.property(root,
				"LoxLIVE.images.image", Collection.class);
		for (JSONObject image : images) {
			int id = LoxoneJsonHelper.property(image, "n", int.class);
			String link = LoxoneJsonHelper.property(image, "link");
			application.addImage(id, link);
		}
	}

	private void parseCategories(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> categories = LoxoneJsonHelper.property(root,
				"LoxLIVE.Cats.Cat", Collection.class);
		for (JSONObject cat : categories) {
			int id = LoxoneJsonHelper.property(cat, "n", int.class);
			String name = LoxoneJsonHelper.property(cat, "name");
			int image = LoxoneJsonHelper.property(cat, "image",
					int.class);
			int rank = LoxoneJsonHelper.property(cat, "rating",
					int.class);
			application.addCategory(id, name, application.findImageById(image),
					rank);
		}
	}

	private void parseRooms(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> rooms = LoxoneJsonHelper.property(root,
				"LoxLIVE.Rooms.Room", Collection.class);
		for (JSONObject room : rooms) {
			int id = LoxoneJsonHelper.property(room, "n", int.class);
			String name = LoxoneJsonHelper.property(room, "name");
			int image = LoxoneJsonHelper.property(room, "image",
					int.class);
			application.addRoom(id, name, application.findImageById(image));
		}
	}

	private void parseFunctions(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> functions = LoxoneJsonHelper.property(root,
				"LoxLIVE.Functions.Function", Collection.class);
		for (JSONObject function : functions) {
			String name = LoxoneJsonHelper.property(function, "name");
			// application.addFunction(function);
		}
	}
}
