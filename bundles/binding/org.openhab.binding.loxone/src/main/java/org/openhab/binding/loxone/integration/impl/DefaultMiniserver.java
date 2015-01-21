package org.openhab.binding.loxone.integration.impl;

import org.openhab.binding.loxone.integration.LoxoneCommunicationException;
import org.openhab.binding.loxone.integration.LoxoneConnection;
import org.openhab.binding.loxone.integration.LoxoneHost;
import org.openhab.binding.loxone.integration.api.ConfigurationApi;
import org.openhab.binding.loxone.integration.api.Miniserver;
import org.openhab.binding.loxone.integration.api.SpsApi;
import org.openhab.binding.loxone.integration.api.SystemApi;

public class DefaultMiniserver implements Miniserver {
	private LoxoneConnection connection;

	public DefaultMiniserver(LoxoneConnection connection) throws LoxoneCommunicationException {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be NULL");
		}
		this.connection = connection;
		this.connection.open();
	}

	LoxoneConnection getConnection() {
		return connection;
	}

	@Override
	public LoxoneHost host() {
		return connection.getHost();
	}

	@Override
	public SystemApi sys() {
		return DefaultSystemApi.withMiniServer(this);
	}

	@Override
	public SpsApi sps() {
		return DefaultSpsApi.withMiniServer(this);
	}

	@Override
	public ConfigurationApi cfg() {
		return DefaultConfigurationApi.withMiniServer(this);
	}

	@Override
	public void dispose() {
		connection.close();
		connection = null;
	}

}
