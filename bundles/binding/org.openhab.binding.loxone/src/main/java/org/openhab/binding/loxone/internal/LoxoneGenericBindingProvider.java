/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.loxone.internal;

import java.util.Map;

import org.openhab.binding.loxone.LoxoneBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author joris.deveirman
 * @since 1.7.0-SNAPSHOT
 */
public class LoxoneGenericBindingProvider extends AbstractGenericBindingProvider implements LoxoneBindingProvider {

	private static final Logger logger = LoggerFactory.getLogger(LoxoneGenericBindingProvider.class);

	private static final String DEFAULT_INSTANCE = "default";

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "loxone";
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		// if (!(item instanceof SwitchItem || item instanceof DimmerItem)) {
		// throw new BindingConfigParseException("item '" + item.getName()
		// + "' is of type '" + item.getClass().getSimpleName()
		// +
		// "', only Switch- and DimmerItems are allowed - please check your *.items configuration");
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);

		Map<String,String> properties = LoxoneBindingConfigParser.parse(bindingConfig);
		if (properties == null) {
			logger.warn("{} is not a valid configuration for {}", bindingConfig, item);
			return;
		}
		LoxoneBindingConfig config = new LoxoneBindingConfig();
		config.instance = properties.get("instance");
		config.uuid = properties.get("uuid");
		config.name = properties.get("name");
		config.itemName  = item.getName();
		//config.supportedStates = item.getAcceptedDataTypes();

		if (Strings.isNullOrEmpty(config.instance)) {
			config.instance = DEFAULT_INSTANCE;
		}
		if (Strings.isNullOrEmpty(config.name) && Strings.isNullOrEmpty(config.uuid)) {
			logger.warn("{} is not a valid configuration for {}, name or UUID is required", bindingConfig, item);
			return;
		}
		addBindingConfig(item, config);
	}

	@Override
	public String getLoxoneInstance(String itemName) {
		LoxoneBindingConfig bindingConfig = (LoxoneBindingConfig) bindingConfigs.get(itemName);
		return bindingConfig == null ? null : bindingConfig.instance;
	}

	@Override
	public boolean isDefaultLoxoneInstance(String instance) {
		return DEFAULT_INSTANCE.equalsIgnoreCase(instance);
	}

	@Override
	public String findItemNameByUUIDOrName(final String instance, final String uuid, final String name) {
		Map.Entry<String, BindingConfig> entry = Iterables.find(bindingConfigs.entrySet(), new Predicate<Map.Entry<String, BindingConfig>>() {
			public boolean apply(Map.Entry<String, BindingConfig> entry) {
				LoxoneBindingConfig config = (LoxoneBindingConfig) entry.getValue();
				boolean matchesInstance = isDefaultLoxoneInstance(config.instance) ? true : config.instance.equalsIgnoreCase(instance);
				boolean matchesUUID = uuid.equalsIgnoreCase(config.uuid);
				boolean matchesName =  name.equalsIgnoreCase(config.name);
				return  matchesInstance && (matchesName || matchesUUID);
			}
		}, null);
		return entry == null ? null : entry.getKey();
	}

	@Override
	public LoxoneBindingConfig findLoxoneBindingConfigByUUID(final String instance, final String uuid) {
		Map.Entry<String, BindingConfig> entry = Iterables.find(bindingConfigs.entrySet(), new Predicate<Map.Entry<String, BindingConfig>>() {
			public boolean apply(Map.Entry<String, BindingConfig> entry) {
				LoxoneBindingConfig config = (LoxoneBindingConfig) entry.getValue();
				boolean matchesInstance = isDefaultLoxoneInstance(config.instance) ? true : config.instance.equalsIgnoreCase(instance);
				boolean matchesUUID = uuid.equalsIgnoreCase(config.uuid);
				return  matchesInstance && matchesUUID;
			}
		}, null);
		return (LoxoneBindingConfig) (entry == null ? null : entry.getValue());
	}

	@Override
	public LoxoneBindingConfig findLoxoneBindingConfigByItemName(String itemName) {
		return (LoxoneBindingConfig) bindingConfigs.get(itemName);
	}
}
