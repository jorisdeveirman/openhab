/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.loxone.internal;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openhab.binding.loxone.LoxoneBindingProvider;
import org.openhab.binding.loxone.integration.LoxoneCommunicationException;
import org.openhab.binding.loxone.integration.LoxoneHost;
import org.openhab.binding.loxone.integration.MiniserverFactory;
import org.openhab.binding.loxone.integration.api.AbstractLoxoneFunction;
import org.openhab.binding.loxone.integration.api.IoResponse;
import org.openhab.binding.loxone.integration.api.LoxoneApplication;
import org.openhab.binding.loxone.integration.api.LoxoneEvent;
import org.openhab.binding.loxone.integration.api.LoxoneEventListener;
import org.openhab.binding.loxone.integration.api.Miniserver;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Implement this class if you are going create an actively polling service like querying a Website/Device.
 * 
 * @author joris.deveirman
 * @since 1.7.0-SNAPSHOT
 */
public class LoxoneBinding extends AbstractBinding<LoxoneBindingProvider> implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(LoxoneBinding.class);

	private final MiniserverFactory miniserverFactory = MiniserverFactory.DEFAULT;
	private final LoxoneEventListener loxoneEventListener = new InternalLoxoneEventListener();

	private final List<Miniserver> miniservers = new LinkedList<Miniserver>();

	public LoxoneBinding() {
	}

	public void activate() {
		logger.debug("Activate LoxoneBinding");
	}

	public void deactivate() {
		updateConfiguration(Collections.<String, LoxoneHost> emptyMap());
		logger.debug("Deactivate LoxoneBinding");
	}

	@Override
	public void allBindingsChanged(BindingProvider provider) {
		logger.debug("BindingProvider changed");
		// TODO update bindings with application
	}

	@Override
	public void bindingChanged(BindingProvider provider, String itemName) {
		logger.debug("Binding changed {}", itemName);
		// update specific item with application
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
	protected void internalReceiveUpdate(final String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveUpdate() is called!");
		// OnOffType o = (OnOffType)newState;
		// DecimalType decimalType = (DecimalType) newState;
		// TODO create command from itemName+State and execute against
		// miniserver
		for (LoxoneBindingProvider provider : providers) {
			final LoxoneBindingConfig config = provider.findLoxoneBindingConfigByItemName(itemName);
			if (config != null) {
				if(!config.associated()) {
					logger.warn("Did not update {} to loxone because it is not associated to a LoxoneFunction. Ignoring update.", itemName);
					continue;
				}
				if (config.readOnly()) {
					logger.warn("Did not update {} to loxone because it is marked readonly. Ignoring update.", itemName);
					continue;
				}
				String value = config.loxoneValue(newState);
				if (Strings.isNullOrEmpty(value)) {
					logger.warn("Converted {} to NULL for {}. Ignoring update.", newState, itemName);
					continue;
				}
				Miniserver miniserver = Iterables.find(miniservers, new Predicate<Miniserver>() {
					public boolean apply(Miniserver miniserver) {
						return config.instance.equalsIgnoreCase(miniserver.host().getName());
					}
				});
				IoResponse response = miniserver.sps().io(config.uuid, value);
				if(!response.success()) {
					logger.warn("Failed to update {} to value {}. Reponse is {}", itemName, response );
				}
			}
		}
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			Map<String, LoxoneHost> hosts = new HashMap<String, LoxoneHost>();

			Enumeration<String> keys = config.keys();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();

				if ("service.pid".equals(key)) {
					continue;
				}

				String[] parts = key.split("\\.");
				String name = parts[0];

				LoxoneHost host = hosts.get(name);
				if (host == null) {
					host = new LoxoneHost(name);
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

				hosts.put(name, host);
			}

			updateConfiguration(hosts);

			logger.debug("Loxone binding Updated");
		}
	}

	/**
	 * Updates the connections as specified by {@code code}. First it disconnects and disposes all existing connections.
	 * Then is creates the connections and registers for events.
	 * 
	 * @param hosts
	 */
	private void updateConfiguration(Map<String, LoxoneHost> hosts) {
		for (Miniserver miniserver : miniservers) {
			try {
				miniserver.dispose();
			} catch (LoxoneCommunicationException e) {
				logger.debug("error while disposing {}", miniserver, e);
			}
		}
		miniservers.clear();

		for (LoxoneHost host : hosts.values()) {
			if (!host.isValid()) {
				logger.warn("Ignoring {} because it is not valid!", host);
				continue;
			}
			try {
				Miniserver miniserver = miniserverFactory.createMiniserver(host);
				miniserver.addLoxoneEventListener(loxoneEventListener);
				miniservers.add(miniserver);
				logger.debug("Successfully established connection to {}", host);
			} catch (LoxoneCommunicationException e) {
				logger.warn("Failed to establish connnection to {}", host, e);
			}
		}
	}

	/**
	 * 
	 * @author joris.deveirman
	 * 
	 */
	private final class InternalLoxoneEventListener implements LoxoneEventListener {

		@Override
		public void onEvent(Miniserver miniserver, LoxoneEvent event) {
			boolean processed = false;
			String loxoneName = miniserver.host().getName();
			for (LoxoneBindingProvider provider : providers) {
				LoxoneBindingConfig config = provider.findLoxoneBindingConfigByUUID(loxoneName, event.getUuid());
				if (config != null) {
					if(!config.associated()) {
						logger.warn("Did not handle {} for {} from loxone because it is not associated to a LoxoneFunction. Ignoring update.", event, config.itemName);
						continue;
					}
					
					logger.debug("Handling {} for {}", event, config.itemName);
					String value = event.getValue();
					State state = config.state(value);
					if (state != null) {
						logger.warn("Converted value {} to State NULL for {}. Ignoring update.", value, config.itemName);
						continue;
					}
					eventPublisher.postUpdate(config.itemName, state);
					processed = true;
				}
			}
			if (!processed) {
				logger.warn("Did not process {} (miniserver={}) because no item binding is available", event, miniserver.host());
			}
		}

		@Override
		public void onApplicationChanged(Miniserver miniserver, LoxoneApplication application) {
			// TODO cache the application
			for (LoxoneBindingProvider provider : providers) {
				provider.disassociateLoxoneFunctions();
				for (AbstractLoxoneFunction function : application.getFunctions()) {
					String itemName = provider.findItemNameByUUIDorName(miniserver.host().getName(), function.getUuidAction(), function.getName());
					if (Strings.isNullOrEmpty(itemName)) {
						logger.debug("No ItemBinding for {}. Updates will be ignored.", function);
						continue;
					}
					provider.associateLoxoneFunction(itemName, function);
				}
			}
		}
	}
}
