package org.openhab.binding.loxone.integration.api;

public class DimmerFunction extends AbstractLoxoneLocatedFunction {

	public DimmerFunction(String name, String uuidAction, LoxoneRoom room,
			LoxoneCategory category, boolean readonly) {
		super(name, uuidAction, room, category, readonly);
	}

	@Override
	public void visit(LoxoneFunctionVisitor visitor) {
		visitor.visit(this);
	}

}
