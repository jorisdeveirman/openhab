package org.openhab.binding.loxone.integration.impl.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openhab.binding.loxone.integration.LoxoneCommunicationException;
import org.openhab.binding.loxone.integration.LoxoneConnection;

public class LoxoneJsonTemplate {
	private final JSONParser jsonParser = new JSONParser();
	private final LoxoneConnection connection;
	private String basepath = "";

	public LoxoneJsonTemplate(LoxoneConnection connection) {
		this(connection, "/");
	}
	
	public LoxoneJsonTemplate(LoxoneConnection connection, String basepath) {
		this.connection = connection;
		this.basepath = basepath;
	}

	public <T> T get(String uri, LoxoneJsonHandler<T> handler) {
		InputStream inputStream = connection.get(path(uri));
		try {
			JSONObject root = (JSONObject) jsonParser
					.parse(new InputStreamReader(inputStream));
			return handler.handle(root);
		} catch (IOException e) {
			throw new LoxoneCommunicationException(
					"Communication error while getting " + uri, e);
		} catch (ParseException e) {
			throw new LoxoneCommunicationException(
					"Could not parse result for " + uri, e);
		}
	}

	private String path(String path) {
		return basepath + path;
	}

}
