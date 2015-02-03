package org.openhab.binding.loxone.integration;

import java.io.InputStream;

public interface LoxoneConnection {

	void open() throws LoxoneCommunicationException;

	void close();

	LoxoneHost getHost();
	
	InputStream get(String uri) throws LoxoneCommunicationException;
	
	void setMessageHandler(LoxoneConnectionListener messageHandler);
}
