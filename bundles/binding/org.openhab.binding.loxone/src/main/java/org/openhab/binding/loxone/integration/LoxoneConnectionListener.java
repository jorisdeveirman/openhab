package org.openhab.binding.loxone.integration;

public interface LoxoneConnectionListener {
	void connectionOpened();

	void connectionClosed();

	void handleMessage(String message);
}
