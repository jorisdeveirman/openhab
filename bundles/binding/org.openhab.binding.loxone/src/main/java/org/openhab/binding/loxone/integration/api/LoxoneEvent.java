package org.openhab.binding.loxone.integration.api;

public class LoxoneEvent {
	private final String uuid;
	private final String value;

	public LoxoneEvent(String uuid, String value) {
		this.uuid = uuid;
		this.value = value;
	}

	public String getUuid() {
		return uuid;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "LoxoneEvent [uuid=" + uuid + ", value=" + value + "]";
	}

}
