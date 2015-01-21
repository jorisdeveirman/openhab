package org.openhab.binding.loxone.integration.impl;

import org.openhab.binding.loxone.integration.api.SystemApi;

class DefaultSystemApi implements SystemApi {
	private final DefaultMiniserver miniserver;

	private DefaultSystemApi(DefaultMiniserver miniserver) {
		this.miniserver = miniserver;
	}

	public static SystemApi withMiniServer(DefaultMiniserver miniserver) {
		return new DefaultSystemApi(miniserver);
	}

	private static String path(String path) {
		return "/jdev/sys/" + path;
	}
}
