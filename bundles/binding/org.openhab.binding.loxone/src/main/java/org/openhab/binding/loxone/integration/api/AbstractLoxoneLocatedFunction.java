package org.openhab.binding.loxone.integration.api;

public class AbstractLoxoneLocatedFunction extends AbstractLoxoneFunction {
	private LoxoneRoom room;
	private LoxoneCategory category;

	public AbstractLoxoneLocatedFunction(String name, String uuidAction,
			LoxoneRoom room, LoxoneCategory category) {
		super(name, uuidAction);
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
