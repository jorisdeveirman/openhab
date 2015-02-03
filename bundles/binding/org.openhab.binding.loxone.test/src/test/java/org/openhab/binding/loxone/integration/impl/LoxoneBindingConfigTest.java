package org.openhab.binding.loxone.integration.impl;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.openhab.binding.loxone.internal.LoxoneBindingConfigParser;

public class LoxoneBindingConfigTest {

	@Test
	public void testBindingConfig() {
		String config = "name:Verlichting badkamer gelijkvloers";
		Map<String, String> properties = LoxoneBindingConfigParser.parse(config);
		Assert.assertEquals("Verlichting badkamer gelijkvloers", properties.get("name"));
	}
}
