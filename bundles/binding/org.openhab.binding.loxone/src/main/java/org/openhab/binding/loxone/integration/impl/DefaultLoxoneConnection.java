package org.openhab.binding.loxone.integration.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;
import org.openhab.binding.loxone.integration.LoxoneCommunicationException;
import org.openhab.binding.loxone.integration.LoxoneConnection;
import org.openhab.binding.loxone.integration.LoxoneHost;
import org.openhab.binding.loxone.integration.LoxoneConnectionListener;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHandler;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHelper;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonTemplate;
import org.openhab.binding.loxone.integration.impl.support.LoxoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Realm;
import com.ning.http.client.Realm.AuthScheme;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class DefaultLoxoneConnection implements LoxoneConnection {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultLoxoneConnection.class);

	private static final int REQUEST_TIMEOUT_MS = 60000;

	private final LoxoneHost host;

	private final String httpUri;
	private final String wsUri;
	private final AsyncHttpClient client;
	private final LoxoneJsonTemplate template;

	private WebSocket webSocket;
	private boolean connected = false;

	private LoxoneConnectionListener connectionListener;

	public DefaultLoxoneConnection(LoxoneHost host) {
		this.host = host;

		this.httpUri = String.format("http://%s:%d", host.getHost(),
				host.getPort());
		this.wsUri = String.format("ws://%s:%d/ws", host.getHost(),
				host.getPort());

		this.client = new AsyncHttpClient(new NettyAsyncHttpProvider(
				createAsyncHttpClientConfig()));

		this.template = new LoxoneJsonTemplate(this);
	}

	/***
	 * Check if the connection to the Loxone instance is active
	 * 
	 * @return true if an active connection to the Loxone instance exists, false
	 *         otherwise
	 */
	public boolean isConnected() {
		if (webSocket == null || !webSocket.isOpen())
			return false;

		return connected;
	}

	/**
	 * Attempts to create a connection to the XBMC host and begin listening for
	 * updates over the async http web socket
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Override
	public void open() throws LoxoneCommunicationException {
		// cleanup any existing web socket left over from previous attempts
		close();

		String key = getKey();
		String value = host.getUsername() + ":" + host.getPassword();

		String encoded = LoxoneUtils.hmacSha1(value, key);

		try {
			webSocket = client
					.prepareGet(wsUri)
					.setHeader("Sec-WebSocket-Protocol", encoded)
					.setHeader("Connection", "Upgrade")
					.setHeader("Upgrade", "websocket")
					.setHeader("Sec-WebSocket-Extensions",
							"permessage-deflate; client_max_window_bits")
					.execute(createWebSocketHandler()).get();
		} catch (InterruptedException | ExecutionException | IOException e) {
			throw new LoxoneCommunicationException("Failed to open websocket",
					e);
		}

	}

	private String getKey() throws LoxoneCommunicationException {
		return template.get("/jdev/sys/getkey",
				new LoxoneJsonHandler<String>() {

					@Override
					public String handle(JSONObject root) {
						return LoxoneJsonHelper.property(root, "LL.value");
					}
				});
	}

	@Override
	public InputStream get(String url) throws LoxoneCommunicationException {
		ListenableFuture<Response> future;
		try {
			future = client.prepareGet(httpUri + url)
					.setHeader("content-type", "application/json")
					.setHeader("accept", "application/json").execute();
			return future.get().getResponseBodyAsStream();
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new LoxoneCommunicationException("Failed to get " + url, e);
		}
	}

	/***
	 * Close this connection to the Loxone instance
	 */
	@Override
	public void close() {
		// if there is an old web socket then clean up and destroy
		if (webSocket != null) {
			webSocket.close();
			webSocket = null;
		}
	}

	private AsyncHttpClientConfig createAsyncHttpClientConfig() {
		Builder builder = new AsyncHttpClientConfig.Builder();
		builder.setRealm(createRealm());
		builder.setRequestTimeoutInMs(REQUEST_TIMEOUT_MS);
		return builder.build();
	}

	private Realm createRealm() {
		Realm.RealmBuilder builder = new Realm.RealmBuilder();
		builder.setPrincipal(host.getUsername());
		builder.setPassword(host.getPassword());
		builder.setScheme(AuthScheme.BASIC);
		return builder.build();
	}

	private WebSocketUpgradeHandler createWebSocketHandler() {
		WebSocketUpgradeHandler.Builder builder = new WebSocketUpgradeHandler.Builder();
		builder.addWebSocketListener(new LoxoneWebSocketListener());
		return builder.build();
	}

	private void notifyMessageReceived(String message) {
		if (connectionListener != null && !Strings.isNullOrEmpty(message)) {
			connectionListener.handleMessage(message);
		}
	}

	private final class LoxoneWebSocketListener implements
			WebSocketTextListener {

		@Override
		public void onClose(WebSocket arg0) {
			logger.info("[{}]: Websocket closed", host.getHost());
			webSocket = null;
			connected = false;

			if (connectionListener != null) {
				connectionListener.connectionClosed();
			}
		}

		@Override
		public void onError(Throwable e) {
			if (e instanceof ConnectException) {
				logger.debug("[{}]: Websocket connection error", host.getHost());
			} else if (e instanceof TimeoutException) {
				logger.debug("[{}]: Websocket timeout error", host.getHost());
			} else {
				logger.error("[{}]: Websocket error: {}", host.getHost(), e);
			}

		}

		@Override
		public void onOpen(WebSocket socket) {
			logger.trace("[{}]: Websocket opened", host.getHost());
			connected = true;

			//socket.sendTextMessage("jdev/sps/LoxAPPversion");
			
			if (connectionListener != null) {
				connectionListener.connectionOpened();
			}

			// socket.sendTextMessage("jdev/sps/listcmds");
			socket.sendTextMessage("jdev/sps/getloxapp");
			socket.sendTextMessage("jdev/sps/enablestatusupdate");
		}

		@Override
		public void onFragment(String message, boolean bool) {
		}

		@Override
		public void onMessage(String message) {
			logger.trace("[{}]: Message received: {}", host.getHost(), message);
			notifyMessageReceived(message);
		}
	}

	@Override
	public LoxoneHost getHost() {
		return host;
	}

	@Override
	public void setMessageHandler(LoxoneConnectionListener messageHandler) {
		this.connectionListener = messageHandler;
	}

}
