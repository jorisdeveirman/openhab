package org.openhab.binding.loxone.integration;

import java.io.InputStream;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;

public interface LoxoneConnection {

	void open() throws LoxoneCommunicationException;

	void close();

	LoxoneHost getHost();
	
	InputStream get(String uri) throws LoxoneCommunicationException;

	AsyncHttpClient getNativeHttpClient();
	
	WebSocket getNativeWebSocket();
	
}
