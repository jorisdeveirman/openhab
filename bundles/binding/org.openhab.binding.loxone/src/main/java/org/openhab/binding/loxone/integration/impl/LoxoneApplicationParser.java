package org.openhab.binding.loxone.integration.impl;

import java.util.Collection;
import java.util.Map;

import org.json.simple.JSONObject;
import org.openhab.binding.loxone.integration.api.AbstractLoxoneFunction;
import org.openhab.binding.loxone.integration.api.DimmerFunction;
import org.openhab.binding.loxone.integration.api.LoxoneApplication;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

class LoxoneApplicationParser {
	private static final Logger logger = LoggerFactory.getLogger(LoxoneApplicationParser.class);

	private static final Map<String, Function<LoxoneFunctionFactoryContext, ? extends AbstractLoxoneFunction>> loxoneFunctionFactories = Maps.newHashMap();
	static {
		loxoneFunctionFactories.put("dimmer", new Function<LoxoneFunctionFactoryContext, AbstractLoxoneFunction>() {
			public DimmerFunction apply(LoxoneFunctionFactoryContext input) {
				String name = LoxoneJsonHelper.property(input.json, "name");
				String uuid = LoxoneJsonHelper.property(input.json, "UUIDaction");
				int roomId = LoxoneJsonHelper.property(input.json, "room", int.class);
				int categoryId = LoxoneJsonHelper.property(input.json, "cat", int.class);
				String ro = LoxoneJsonHelper.property(input.json, "detail.InfoOnly");
				return new DimmerFunction(name, uuid, input.application.findRoomById(roomId), input.application.findCategoryById(categoryId), "YES".equalsIgnoreCase(ro));
			}
		});
	}

	public LoxoneApplication parseApplication(JSONObject root) {
		String version = LoxoneJsonHelper.property(root, "LoxLIVE.Version");
		String date = LoxoneJsonHelper.property(root, "LoxLIVE.Date");
		String sn = LoxoneJsonHelper.property(root, "LoxLIVE.SN");
		String friendlyName = LoxoneJsonHelper.property(root, "LoxLIVE.friendlyname");
		LoxoneApplication application = new LoxoneApplication(version, date, sn, friendlyName);
		parseImages(root, application);
		parseCategories(root, application);
		parseRooms(root, application);
		parseFunctions(root, application);
		return application;
	}

	private void parseImages(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> images = LoxoneJsonHelper.property(root, "LoxLIVE.images.image", Collection.class);
		for (JSONObject image : images) {
			int id = LoxoneJsonHelper.property(image, "n", int.class);
			String link = LoxoneJsonHelper.property(image, "link");
			application.addImage(id, link);
		}
	}

	private void parseCategories(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> categories = LoxoneJsonHelper.property(root, "LoxLIVE.Cats.Cat", Collection.class);
		for (JSONObject cat : categories) {
			int id = LoxoneJsonHelper.property(cat, "n", int.class);
			String name = LoxoneJsonHelper.property(cat, "name");
			int image = LoxoneJsonHelper.property(cat, "image", int.class);
			int rank = LoxoneJsonHelper.property(cat, "rating", int.class);
			application.addCategory(id, name, application.findImageById(image), rank);
		}
	}

	private void parseRooms(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> rooms = LoxoneJsonHelper.property(root, "LoxLIVE.Rooms.Room", Collection.class);
		for (JSONObject room : rooms) {
			int id = LoxoneJsonHelper.property(room, "n", int.class);
			String name = LoxoneJsonHelper.property(room, "name");
			int image = LoxoneJsonHelper.property(room, "image", int.class);
			application.addRoom(id, name, application.findImageById(image));
		}
	}

	private void parseFunctions(JSONObject root, LoxoneApplication application) {
		Collection<JSONObject> functions = LoxoneJsonHelper.property(root, "LoxLIVE.Functions.Function", Collection.class);
		for (JSONObject function : functions) {
			String name = LoxoneJsonHelper.property(function, "name");
			String type = LoxoneJsonHelper.property(function, "Type");
			if (Strings.isNullOrEmpty(type)) {
				type = LoxoneJsonHelper.property(function, "detail.Type");
			}
			if (Strings.isNullOrEmpty(type)) {
				logger.warn("Could not determine type for {}", name);
				continue;
			}
			Function<LoxoneFunctionFactoryContext, ? extends AbstractLoxoneFunction> factory = loxoneFunctionFactories.get(type.toLowerCase());
			if (factory == null) {
				logger.warn("No factory found for {} type={}", name, type);
				continue;
			}
			AbstractLoxoneFunction f = factory.apply(new LoxoneFunctionFactoryContext(function, application));
			if (f == null) {
				logger.warn("Could not create LoxoneFunction for {} type={}", name, type);
				continue;
			}
			application.addFunction(f);
		}
	}

	private static final class LoxoneFunctionFactoryContext {
		final JSONObject json;
		final LoxoneApplication application;

		public LoxoneFunctionFactoryContext(JSONObject json, LoxoneApplication application) {
			this.json = json;
			this.application = application;
		}
	}
}
