package org.openhab.binding.loxone.integration.impl;

import org.json.simple.JSONObject;
import org.openhab.binding.loxone.integration.api.IoResponse;
import org.openhab.binding.loxone.integration.api.SpsApi;
import org.openhab.binding.loxone.integration.api.SpsState;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHandler;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonHelper;
import org.openhab.binding.loxone.integration.impl.support.LoxoneJsonTemplate;

public class DefaultSpsApi implements SpsApi {
	private final LoxoneJsonTemplate template;
	private final DefaultMiniserver miniserver;

	private DefaultSpsApi(DefaultMiniserver miniserver) {
		this.miniserver = miniserver;
		this.template = new LoxoneJsonTemplate(miniserver.getConnection(), "/jdev/sps/");
	}

	public static SpsApi withMiniServer(DefaultMiniserver miniserver) {
		return new DefaultSpsApi(miniserver);
	}
//													  09891038-00ec-182e-ffffeee000c400a4
	@Override // <20:18:17:656> Sent cmd: jdev/sps/io/089ad95a-0127-0f85-ffffeee000c400a4/100
	public IoResponse io(String uuid, String value) {
		return template.get(String.format("io/%s/%s", uuid, value), new LoxoneJsonHandler<IoResponse>() {
			@Override
			public IoResponse handle(JSONObject root) {
				return new IoResponse(LoxoneJsonHelper.property(root, "LL.control"), LoxoneJsonHelper.property(root, "LL.value"), LoxoneJsonHelper.property(root, "LL.Code", int.class));
			}
		});
	}

	@Override
	public SpsState state() {
		return template.get("state", new LoxoneJsonHandler<SpsState>() {

			@Override
			public SpsState handle(JSONObject root) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	@Override
	public void restart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void log() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nolog() {
		// TODO Auto-generated method stub

	}
}
