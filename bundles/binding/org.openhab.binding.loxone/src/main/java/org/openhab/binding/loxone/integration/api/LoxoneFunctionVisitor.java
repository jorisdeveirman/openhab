package org.openhab.binding.loxone.integration.api;

public interface LoxoneFunctionVisitor<V> {
	V visit(DimmerFunction function);
}
