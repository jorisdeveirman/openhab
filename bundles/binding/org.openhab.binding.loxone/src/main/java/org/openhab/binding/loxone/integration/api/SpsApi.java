package org.openhab.binding.loxone.integration.api;

public interface SpsApi {
	LoxoneApplication getApplication(); 
	
	SpsState state();
	
	void restart();
	
	void stop();
	
	void run();
	
	void log();
	
	void nolog();
}
