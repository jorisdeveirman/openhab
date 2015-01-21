/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.loxone.internal;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.loxone.LoxoneBindingProvider;
import org.openhab.binding.loxone.integration.LoxoneCommunicationException;
import org.openhab.binding.loxone.integration.LoxoneHost;
import org.openhab.binding.loxone.integration.MiniserverFactory;
import org.openhab.binding.loxone.integration.api.Miniserver;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service like
 * querying a Website/Device.
 * 
 * @author joris.deveirman
 * @since 1.7.0-SNAPSHOT
 */
public class LoxoneBinding extends AbstractActiveBinding<LoxoneBindingProvider>
		implements ManagedService {

	private static final Logger logger = LoggerFactory
			.getLogger(LoxoneBinding.class);

	private MiniserverFactory miniserverFactory = MiniserverFactory.DEFAULT;

	/**
	 * the refresh interval which is used to poll values from the Loxone server
	 * (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;

	private List<Miniserver> miniservers = new LinkedList<Miniserver>();

	public LoxoneBinding() {
	}

	public void activate() {
		logger.debug("Activate LoxoneBinding");
	}

	public void deactivate() {
		// deallocate resources here that are no longer needed and
		// should be reset when activating this binding again
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected String getName() {
		return "Loxone Refresh Service";
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
		logger.debug("execute() method is called!");
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveUpdate() is called!");
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void updated(Dictionary<String, ?> config)
			throws ConfigurationException {
		if (config != null) {

			// to override the default refresh interval one has to add a
			// parameter to openhab.cfg like
			// <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}

			Map<String, LoxoneHost> hosts = new HashMap<String, LoxoneHost>();

			Enumeration<String> keys = config.keys();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();

				if ("service.pid".equals(key)) {
					continue;
				}

				String[] parts = key.split("\\.");
				String hostname = parts[0];

				LoxoneHost host = hosts.get(hostname);
				if (host == null) {
					host = new LoxoneHost();
				}

				String value = ((String) config.get(key)).trim();

				if ("host".equals(parts[1])) {
					host.setHost(value);
				}
				if ("port".equals(parts[1])) {
					host.setPort(Integer.valueOf(value));
				}
				if ("username".equals(parts[1])) {
					host.setUsername(value);
				}
				if ("password".equals(parts[1])) {
					host.setPassword(value);
				}

				hosts.put(hostname, host);
			}

			updateConfiguration(hosts);

			logger.debug(getName() + " Updated");
			setProperlyConfigured(true);
		}
	}

	private void updateConfiguration(Map<String, LoxoneHost> hosts) {
		for (Miniserver miniserver : miniservers) {
			miniserver.dispose();
		}
		miniservers.clear();

		for (LoxoneHost host : hosts.values()) {
			if (!host.isValid()) {
				logger.warn("Ignoring {} because it is not valid!", host);
				continue;
			}
			try {
				Miniserver miniserver = miniserverFactory
						.createMiniserver(host);
				miniservers.add(miniserver);
			} catch (LoxoneCommunicationException e) {
				logger.warn("Failed to establish connnection to {}", host, e);
			}
		}
	}
}
