package org.openhab.binding.loxone.integration.impl;

import org.openhab.binding.loxone.integration.api.ConfigurationApi;

public class DefaultConfigurationApi implements ConfigurationApi {
	private final DefaultMiniserver miniserver;

	private DefaultConfigurationApi(DefaultMiniserver miniserver) {
		this.miniserver = miniserver;
	}

	public static ConfigurationApi withMiniServer(DefaultMiniserver miniserver) {
		return new DefaultConfigurationApi(miniserver);
	}

}
