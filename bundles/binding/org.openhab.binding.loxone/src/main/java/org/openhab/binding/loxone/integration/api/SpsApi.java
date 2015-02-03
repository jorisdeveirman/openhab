package org.openhab.binding.loxone.integration.api;

public interface SpsApi {
	SpsState state();

	IoResponse io(String uuid, String value);

	void restart();

	void stop();

	void run();

	void log();

	void nolog();
}
