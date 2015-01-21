package org.openhab.binding.loxone.integration.impl.support;

import org.json.simple.JSONObject;

public interface LoxoneJsonHandler<T> {
	T handle(JSONObject root);
}
