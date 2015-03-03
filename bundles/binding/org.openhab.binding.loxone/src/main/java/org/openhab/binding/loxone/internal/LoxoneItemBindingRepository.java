package org.openhab.binding.loxone.internal;

import org.openhab.binding.loxone.integration.api.AbstractLoxoneFunction;

public interface LoxoneItemBindingRepository {
	void evictAll();
	
	LoxoneItemBinding create(String itemName, AbstractLoxoneFunction function);
	
	LoxoneItemBinding findForItemName(String itemName);

	LoxoneItemBinding findForUuid(String uuid);
}
