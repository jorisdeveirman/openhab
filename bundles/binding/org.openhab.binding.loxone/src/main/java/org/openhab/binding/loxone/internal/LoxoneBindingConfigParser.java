package org.openhab.binding.loxone.internal;

import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;

public class LoxoneBindingConfigParser {
	private static MapSplitter splitter = Splitter.on(",").withKeyValueSeparator(":");

	public static Map<String, String> parse(String bindingConfig) {
		return splitter.split(bindingConfig);
	}
}
