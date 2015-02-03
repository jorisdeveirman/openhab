package org.openhab.binding.loxone.integration.api;

public abstract class AbstractLoxoneLocatedFunction extends
		AbstractLoxoneFunction {
	private final LoxoneRoom room;
	private final LoxoneCategory category;

	public AbstractLoxoneLocatedFunction(String name, String uuidAction,
			LoxoneRoom room, LoxoneCategory category, boolean readonly) {
		super(name, uuidAction, readonly);
		this.room = room;
		this.category = category;
	}

	public LoxoneRoom getRoom() {
		return room;
	}

	public LoxoneCategory getCategory() {
		return category;
	}
}
