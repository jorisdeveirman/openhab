package org.openhab.binding.loxone.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.openhab.binding.loxone.integration.api.AbstractLoxoneFunction;
import org.openhab.binding.loxone.integration.api.DimmerFunction;
import org.openhab.binding.loxone.integration.api.LoxoneFunctionVisitor;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;

public class LoxoneStateMapper {
	public static State state(AbstractLoxoneFunction function, final String value) {
		final AtomicReference<State> state = new AtomicReference<>();
		function.visit(new LoxoneFunctionVisitor() {

			@Override
			public void visit(DimmerFunction function) {
				state.set(toPercentType(value));
			}
		});
		return state.get();
	}
	
	public static String value(AbstractLoxoneFunction function, final State state) {
		final AtomicReference<String> value = new AtomicReference<>();
		function.visit(new LoxoneFunctionVisitor() {

			@Override
			public void visit(DimmerFunction function) {
				value.set(((PercentType) state).toString());
			}
		});
		return value.get();
	}

	private static PercentType toPercentType(String value) {
		return new PercentType(value);
	}
}
