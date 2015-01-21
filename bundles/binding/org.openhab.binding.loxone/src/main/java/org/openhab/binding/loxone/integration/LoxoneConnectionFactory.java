package org.openhab.binding.loxone.integration;

import org.openhab.binding.loxone.integration.impl.DefaultLoxoneConnection;

public interface LoxoneConnectionFactory {
	public final static LoxoneConnectionFactory DEFAULT = new DefaultLoxoneConnectionFactory();

	LoxoneConnection createConnection(LoxoneHost host);

	static final class DefaultLoxoneConnectionFactory implements
			LoxoneConnectionFactory {

		@Override
		public LoxoneConnection createConnection(LoxoneHost host) {
			return new DefaultLoxoneConnection(host);
		}

	}

}
