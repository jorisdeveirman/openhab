package org.openhab.binding.loxone.integration.api;

import org.openhab.binding.loxone.integration.LoxoneHost;

public interface Miniserver {

	LoxoneHost host();

	SystemApi sys();

	SpsApi sps();

	ConfigurationApi cfg();

	void dispose();

	void addLoxoneEventListener(LoxoneEventListener listener);

	void removeLoxoneEventListener(LoxoneEventListener listener);
}
