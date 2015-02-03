package org.openhab.binding.loxone.integration.impl.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.base.Defaults;
import com.google.common.base.Function;
import com.google.common.base.Splitter;

public class LoxoneJsonHelper {
	private static final JSONParser jsonParser = new JSONParser();
	private static final Splitter dotSplitter = Splitter.on(".");

	private static final Map<Class<?>, Function<String, ?>> converters = new HashMap<>();
	static {
		converters.put(int.class, new Function<String, Integer>() {
			@Override
			public Integer apply(String input) {
				return Integer.parseInt(input);
			}
		});
	}

	public static String property(JSONObject object, String property) {
		return property(object, property, String.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T property(JSONObject object, String property,
			Class<T> target) {
		JSONObject nextNode = object;
		Object lastValue = null;

		Iterator<String> iterator = dotSplitter.split(property).iterator();
		while (iterator.hasNext()) {
			String singleProperty = iterator.next();
			Object o = singleProperty(nextNode, singleProperty);
			if (o == null) {
				return Defaults.defaultValue(target);
			}
			lastValue = o;
			if (o instanceof JSONObject) {
				nextNode = (JSONObject) o;
			} else {
				break;
			}
		}

		if (lastValue == null) {
			return Defaults.defaultValue(target);
		}
		if (!target.isAssignableFrom(lastValue.getClass())
				&& lastValue instanceof String) {
			Function<String, ?> function = converters.get(target);
			if (function != null) {
				return (T) function.apply((String) lastValue);
			}
			// no converter found, return null
			return Defaults.defaultValue(target);
		} else {
			return (T) lastValue;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T singleProperty(JSONObject object, String property) {
		return (T) object.get(property);
	}

	public static JSONObject parse(String text) throws ParseException {
		return (JSONObject) jsonParser.parse(text);
	}

	public static JSONObject parse(InputStream stream) throws IOException,
			ParseException {
		return (JSONObject) jsonParser.parse(new InputStreamReader(stream));
	}
}
