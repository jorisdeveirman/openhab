package org.openhab.binding.loxone.integration.impl;

import junit.framework.Assert;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.openhab.binding.loxone.integration.api.LoxoneApplication;

public class LoxoneApplicationParserTest {
	private LoxoneApplicationParser parser = new LoxoneApplicationParser();

	@Test
	public void testParseWorking() {
		JSONObject o = LoxoneTestApplicationConstants.applicationJson;
		LoxoneApplication application = parser.parseApplication(o);
		Assert.assertNotNull(application);
		Assert.assertEquals("17", application.getVersion());
		Assert.assertEquals("EEE000123456", application.getSerialNumber());
		Assert.assertEquals("Demo Loxone", application.getFriendlyName());
		Assert.assertEquals(8, application.getImages().size());
	}
}
