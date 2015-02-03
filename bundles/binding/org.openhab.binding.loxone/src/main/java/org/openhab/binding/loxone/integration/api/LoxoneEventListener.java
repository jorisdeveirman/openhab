package org.openhab.binding.loxone.integration.api;

public interface LoxoneEventListener {
	void onApplicationChanged(Miniserver miniserver, LoxoneApplication application);
	
	void onEvent(Miniserver miniserver, LoxoneEvent event);
}
