package org.openhab.binding.loxone.integration.api;

public class IoResponse {
	private final String control;
	private final String value;
	private final int code;

	public IoResponse(String control, String value, int code) {
		this.control = control;
		this.value = value;
		this.code = code;
	}

	public String getControl() {
		return control;
	}

	public String getValue() {
		return value;
	}

	public int getCode() {
		return code;
	}
	
	public boolean success () {
		return code == 200;
	}

	@Override
	public String toString() {
		return "IoResponse [control=" + control + ", value=" + value + ", code=" + code + "]";
	}
}
