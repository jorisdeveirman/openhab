package org.openhab.binding.loxone.integration;

import org.openhab.binding.loxone.integration.api.Miniserver;
import org.openhab.binding.loxone.integration.impl.DefaultMiniserver;

public interface MiniserverFactory {

	public static final MiniserverFactory DEFAULT = new DefaultMiniserverFactory();

	Miniserver createMiniserver(LoxoneHost host) throws LoxoneCommunicationException;

	public static final class DefaultMiniserverFactory implements
			MiniserverFactory {

		@Override
		public Miniserver createMiniserver(LoxoneHost host) throws LoxoneCommunicationException {
			LoxoneConnection connection = LoxoneConnectionFactory.DEFAULT
					.createConnection(host);
			return new DefaultMiniserver(connection);
		}

	}
}
