package org.openhab.binding.loxone.internal;

import java.util.List;

import org.openhab.binding.loxone.integration.api.AbstractLoxoneFunction;
import org.openhab.binding.loxone.integration.api.DimmerFunction;
import org.openhab.binding.loxone.integration.api.LoxoneFunctionVisitor;
import org.openhab.binding.loxone.internal.item.LoxoneDimmerItemBinding;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DefaultLoxoneItemBindingRepository implements LoxoneItemBindingRepository {
	private List<LoxoneItemBinding> itemBindings = Lists.newArrayList();

	@Override
	public void evictAll() {
		itemBindings.clear();
	}

	@Override
	public LoxoneItemBinding create(final String itemName, AbstractLoxoneFunction function) {
		LoxoneItemBinding binding = function.visit(new LoxoneFunctionVisitor<LoxoneItemBinding>() {
			
			@Override
			public LoxoneItemBinding visit(DimmerFunction function) {
				return LoxoneDimmerItemBinding.fromFunction(itemName, function);
			}
		});
		if(binding != null){
			itemBindings.add(binding);
		}
		return binding;
	}

	@Override
	public LoxoneItemBinding findForItemName(final String itemName) {
		if(Strings.isNullOrEmpty(itemName)) {
			return null;
		}
		return Iterables.find(itemBindings, new Predicate<LoxoneItemBinding>() {
			@Override
			public boolean apply(LoxoneItemBinding input) {
				return input.getItemName().equals(itemName);
			}
		}, null);
	}
	
	@Override
	public LoxoneItemBinding findForUuid(final String uuid) {
		return Iterables.find(itemBindings, new Predicate<LoxoneItemBinding>() {
			@Override
			public boolean apply(LoxoneItemBinding input) {
				return input.getUuids().contains(uuid);
			}
		}, null);
	}

}
