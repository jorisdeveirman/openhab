package org.openhab.binding.loxone.internal;

public class LoxoneValue {
	private final String uuid;
	private String value;

	private LoxoneValue(String uuid, String value) {
		this.uuid = uuid;
		this.value = value;
	}
	
	public static LoxoneValue create(String uuid, String value) {
		return new LoxoneValue(uuid, value);
	}

	public String getUuid() {
		return uuid;
	}

	public String getValue() {
		return value;
	}
	
	public int getValueAsInt() {
		return Integer.parseInt(value);
	}
	public double getValueAsDouble() {
		return Double.parseDouble(value);
	}


	public void updateValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "LoxoneValue [uuid=" + uuid + ", value=" + value + "]";
	}

}
