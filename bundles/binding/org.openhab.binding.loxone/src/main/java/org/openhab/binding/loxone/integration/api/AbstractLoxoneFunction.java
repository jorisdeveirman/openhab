package org.openhab.binding.loxone.integration.api;

public abstract class AbstractLoxoneFunction {
	private String name;
	private String uuidAction;

	public AbstractLoxoneFunction(String name, String uuidAction) {
		this.name = name;
		this.uuidAction = uuidAction;
	}

	public String getName() {
		return name;
	}

	public String getUuidAction() {
		return uuidAction;
	}

}
