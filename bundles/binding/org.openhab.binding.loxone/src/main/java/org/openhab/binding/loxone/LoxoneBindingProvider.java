/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.loxone;

import org.openhab.binding.loxone.internal.LoxoneBindingConfig;
import org.openhab.core.binding.BindingProvider;

/**
 * @author joris.deveirman
 * @since 1.7.0-SNAPSHOT
 */
public interface LoxoneBindingProvider extends BindingProvider {

	boolean isDefaultLoxoneInstance(String instance);

	String getLoxoneInstance(String itemName);

	String findItemNameByUUIDOrName(String instance, String uuid, String name);

	LoxoneBindingConfig findLoxoneBindingConfigByUUID(String instance, String uuid);

	LoxoneBindingConfig findLoxoneBindingConfigByItemName(String itemName);
}
