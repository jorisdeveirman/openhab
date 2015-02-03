package org.openhab.binding.loxone.internal;

import java.util.List;

import org.openhab.binding.loxone.integration.api.AbstractLoxoneFunction;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.types.State;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class LoxoneBindingConfig implements BindingConfig {
	String itemName;
	String name;
	String uuid;
	String instance;
	List<Class<? extends State>> supportedStates = Lists.newArrayList();

	private AbstractLoxoneFunction loxoneFunction;

	public void associateLoxoneFunction(AbstractLoxoneFunction function) {
		Preconditions.checkState(uuid.equalsIgnoreCase(function.getUuidAction()), "Expected function with uuid " + uuid + " but got " + function.getUuidAction());
		this.loxoneFunction = function;
		this.uuid = function.getUuidAction();
		this.name = function.getName();
	}

	public void disassociateLoxoneFunction() {
		this.loxoneFunction = null;
	}

	public boolean associated() {
		return loxoneFunction != null;
	}

	public boolean readOnly() {
		Preconditions.checkNotNull(loxoneFunction, "No LoxoneFunction associated");
		return loxoneFunction.isReadOnly();
	}

	public String loxoneValue(State state) {
		Preconditions.checkNotNull(loxoneFunction, "No LoxoneFunction associated");
		return LoxoneStateMapper.value(loxoneFunction, state);
	}

	public State state(String loxoneValue) {
		Preconditions.checkNotNull(loxoneFunction, "No LoxoneFunction associated");
		return LoxoneStateMapper.state(loxoneFunction, loxoneValue);
	}
}
