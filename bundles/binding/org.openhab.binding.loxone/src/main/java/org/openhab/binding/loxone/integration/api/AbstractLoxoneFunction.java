package org.openhab.binding.loxone.integration.api;

public abstract class AbstractLoxoneFunction {
	private final String name;
	private final String uuidAction;
	private final boolean readonly;

	public AbstractLoxoneFunction(String name, String uuidAction, boolean readonly) {
		this.name = name;
		this.uuidAction = uuidAction;
		this.readonly = readonly;
	}

	public String getName() {
		return name;
	}

	public String getUuidAction() {
		return uuidAction;
	}
	
	public boolean isReadOnly() {
		return readonly;
	}
	
	public abstract <V> V visit(LoxoneFunctionVisitor<V> visitor);

}
