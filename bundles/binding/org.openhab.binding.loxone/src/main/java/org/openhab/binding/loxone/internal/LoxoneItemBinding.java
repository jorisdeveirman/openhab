package org.openhab.binding.loxone.internal;

import java.util.Collection;

import org.openhab.core.types.Command;
import org.openhab.core.types.State;

public interface LoxoneItemBinding {
	String getItemName();
	
	boolean isReadOnly();
	
	Collection<String> getUuids();
	
	LoxoneValue convertToLoxoneValue(State state);

	LoxoneValue convertToLoxoneValue(Command command);
	
	State convertToLoxoneState(LoxoneValue value);
}
