package org.openhab.binding.loxone.integration.impl;

import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openhab.binding.loxone.integration.LoxoneCommunicationException;
import org.openhab.binding.loxone.integration.LoxoneConnection;
import org.openhab.binding.loxone.integration.LoxoneHost;
import org.openhab.binding.loxone.integration.LoxoneConnectionListener;
import org.openhab.binding.loxone.integration.api.ConfigurationApi;
import org.openhab.binding.loxone.integration.api.LoxoneApplication;
import org.openhab.binding.loxone.integration.api.LoxoneEvent;
import org.openhab.binding.loxone.integration.api.LoxoneEventListener;
import org.openhab.binding.loxone.integration.api.Miniserver;
import org.openhab.binding.loxone.integration.api.SpsApi;
import org.openhab.binding.loxone.integration.api.SystemApi;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * @author joris.deveirman
 * 
 */
public class DefaultMiniserver implements Miniserver {
	private static final Logger logger = LoggerFactory.getLogger(DefaultMiniserver.class);

	private LoxoneConnection connection;
	private final Set<LoxoneEventListener> listeners = Sets.newHashSet();

	public DefaultMiniserver(LoxoneConnection connection) throws LoxoneCommunicationException {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be NULL");
		}
		this.connection = connection;
		this.connection.setMessageHandler(new MiniserverMessageHandler());
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
	public void addLoxoneEventListener(LoxoneEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeLoxoneEventListener(LoxoneEventListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void dispose() {
		connection.close();
		connection = null;
	}

	private void dispatchEvent(LoxoneEvent event) {
		for (LoxoneEventListener listener : listeners) {
			listener.onEvent(this, event);
		}
	}

	private void dispatchApplication(LoxoneApplication application) {
		for (LoxoneEventListener listener : listeners) {
			listener.onApplicationChanged(this, application);
		}
	}

	private final class MiniserverMessageHandler implements LoxoneConnectionListener {
		private final Splitter lineSplitter = Splitter.on("\r\n");
		private final LoxoneApplicationParser applicationParser = new LoxoneApplicationParser();

		@Override
		public void handleMessage(String message) {
			if (message.startsWith("{\"LoxLIVE")) {
				try {
					LoxoneApplication application = applicationParser.parseApplication(LoxoneJsonHelper.parse(message));
					dispatchApplication(application);
				} catch (ParseException e) {
					logger.warn("Could not parse Loxone application {}", message, e);
				}

			} else {
				Iterable<String> messages = lineSplitter.split(message);
				for (String m : messages) {
					if (Strings.isNullOrEmpty(m)) {
						continue;
					}
					try {
						JSONObject root = LoxoneJsonHelper.parse(m);
						LoxoneEvent event = new LoxoneEvent(LoxoneJsonHelper.property(root, "s.u"), LoxoneJsonHelper.property(root, "s.v"));
						dispatchEvent(event);
					} catch (ParseException e) {
						logger.warn("Could not parse Loxone Message {}", m, e);
					}
				}
			}
		}

		@Override
		public void connectionOpened() {
			// LoxoneApplication application = sps().getApplication();
			// dispatchApplication(application);
		}

		@Override
		public void connectionClosed() {
			// TODO reconnect when connection is lost
		}
	}
}
