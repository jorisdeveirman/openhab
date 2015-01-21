package org.openhab.binding.loxone.integration.impl;

import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public interface LoxoneTestApplicationConstants {
	public static final JSONObject applicationJson = (JSONObject) JSONValue.parse(new InputStreamReader(LoxoneTestApplicationConstants.class.getResourceAsStream("/application_demo.json")));	
}
