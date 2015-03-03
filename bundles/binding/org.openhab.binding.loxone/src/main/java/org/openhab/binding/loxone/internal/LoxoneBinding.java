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
	private final LoxoneItemBindingRepository loxoneItemBindingRepository = new DefaultLoxoneItemBindingRepository();
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
		// TODO update specific item with application
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
		for (LoxoneBindingProvider provider : providers) {
			LoxoneItemBinding loxoneItemBinding = loxoneItemBindingRepository.findForItemName(itemName);
			final String instance = provider.getLoxoneInstance(itemName);
			if (loxoneItemBinding != null) {
				if (loxoneItemBinding.isReadOnly()) {
					logger.warn("Did not update {} to loxone because it is marked readonly. Ignoring update.", itemName);
					continue;
				}
				LoxoneValue value = loxoneItemBinding.convertToLoxoneValue(command);
				if (value == null) {
					logger.warn("Converted {} to NULL for {}. Ignoring update.", command, itemName);
					continue;
				}
				applyValueToLoxone(instance, itemName, value);
			}
		}
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
		for (LoxoneBindingProvider provider : providers) {
			LoxoneItemBinding loxoneItemBinding = loxoneItemBindingRepository.findForItemName(itemName);
			final String instance = provider.getLoxoneInstance(itemName);
			if (loxoneItemBinding != null) {
				if (loxoneItemBinding.isReadOnly()) {
					logger.warn("Did not update {} to loxone because it is marked readonly. Ignoring update.", itemName);
					continue;
				}
				LoxoneValue value = loxoneItemBinding.convertToLoxoneValue(newState);
				if (value == null) {
					logger.warn("Converted {} to NULL for {}. Ignoring update.", newState, itemName);
					continue;
				}
				applyValueToLoxone(instance, itemName, value);
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

	private void applyValueToLoxone(String instance, String itemName, LoxoneValue value) {
		Miniserver miniserver = findMiniserver(instance);
		try {
			IoResponse response = miniserver.sps().io(value.getUuid(), value.getValue());
			if (!response.success()) {
				logger.warn("Failed to update {} to value {}. Reponse is {}", itemName, value, response);
			}else {
				logger.debug("Updated {} to value {}", itemName,value);
			}
		} catch (LoxoneCommunicationException e) {
			logger.error("Failed to update {} to value {}. An unexpected error occured", itemName, value, e);
		}
	}

	private Miniserver findMiniserver(final String instance) {
		return Iterables.find(miniservers, new Predicate<Miniserver>() {
			public boolean apply(Miniserver miniserver) {
				return instance.equalsIgnoreCase(miniserver.host().getName());
			}
		}, Iterables.getFirst(miniservers, null));
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
			LoxoneItemBinding loxoneItemBinding = loxoneItemBindingRepository.findForUuid(event.getUuid());
			if (loxoneItemBinding != null) {
				logger.debug("Handling {} for {}", event, loxoneItemBinding.getItemName());
				State state = loxoneItemBinding.convertToLoxoneState(LoxoneValue.create(event.getUuid(), event.getValue()));
				if (state != null) {
					eventPublisher.postUpdate(loxoneItemBinding.getItemName(), state);
				} else {
					logger.warn("Converted value {} to State NULL for {}. Ignoring update.", event.getValue(), loxoneItemBinding.getItemName());
				}
			} else {
				logger.trace("Did not process {} (miniserver={}) because no Item Binding is available", event, miniserver.host());
			}
		}

		@Override
		public void onApplicationChanged(Miniserver miniserver, LoxoneApplication application) {
			loxoneItemBindingRepository.evictAll();
			for (LoxoneBindingProvider provider : providers) {
				for (AbstractLoxoneFunction function : application.getFunctions()) {
					String itemName = provider.findItemNameByUUIDOrName(miniserver.host().getName(), function.getUuidAction(), function.getName());
					if (Strings.isNullOrEmpty(itemName)) {
						logger.debug("No Item Binding for {}. Updates will be ignored.", function);
						continue;
					}
					loxoneItemBindingRepository.create(itemName, function);
				}
			}
		}
	}
}
