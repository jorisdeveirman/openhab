package org.openhab.binding.loxone.internal.item;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

import org.openhab.binding.loxone.integration.api.DimmerFunction;
import org.openhab.binding.loxone.internal.LoxoneItemBinding;
import org.openhab.binding.loxone.internal.LoxoneValue;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LoxoneDimmerItemBinding implements LoxoneItemBinding {

	private static final String STATE_MAX = "max";
	private static final String STATE_MIN = "min";
	private static final String STATE_STEP = "step";
	private static final String STATE_POS = "pos";

	private final String itemName;
	private final boolean readOnly;
	private final Map<String, LoxoneValue> states = Maps.newHashMap();
	private final Collection<String> uuids = Lists.newArrayList();
	private String uuidAction;

	public static LoxoneDimmerItemBinding fromFunction(String itemName, DimmerFunction function) {
		LoxoneDimmerItemBinding itemBinding = new LoxoneDimmerItemBinding(itemName, function.isReadOnly());
		itemBinding.states.put(STATE_MAX, LoxoneValue.create(function.getStateMaxUuid(), "100"));
		itemBinding.states.put(STATE_MIN, LoxoneValue.create(function.getStateMinUuid(), "0"));
		itemBinding.states.put(STATE_STEP, LoxoneValue.create(function.getStateStepUuid(), "1"));
		itemBinding.states.put(STATE_POS, LoxoneValue.create(function.getStatePosUuid(), "0"));
		
		itemBinding.uuids.add(function.getStateMaxUuid());
		itemBinding.uuids.add(function.getStateMinUuid());
		itemBinding.uuids.add(function.getStateStepUuid());
		itemBinding.uuids.add(function.getStatePosUuid());

		itemBinding.uuidAction = function.getUuidAction();
		return itemBinding;
	}

	private LoxoneDimmerItemBinding(String itemName, boolean readOnly) {
		this.itemName = itemName;
		this.readOnly = readOnly;
	}

	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	@Override
	public Collection<String> getUuids() {
		return uuids;
	}
	
	@Override
	public LoxoneValue convertToLoxoneValue(Command command) {
		if(command instanceof IncreaseDecreaseType) {
			LoxoneValue posValue = states.get(STATE_POS);
			double min = states.get(STATE_MIN).getValueAsDouble();
			double max = states.get(STATE_MAX).getValueAsDouble();
			double step = states.get(STATE_STEP).getValueAsDouble();
			double value = posValue.getValueAsDouble();
			
			IncreaseDecreaseType incDec = (IncreaseDecreaseType) command;
			switch (incDec) {
			case INCREASE:
				value += step;
				break;
			case DECREASE:
				value -= step;
				break;
			default:
				break;
			}
			if(value < min) {
				value = min;
			}
			if(value > max) {
				value = max;
			}
			posValue.updateValue(toRoundedValue(value));
			return LoxoneValue.create(uuidAction, posValue.getValue());
		}

		throw new IllegalStateException("Could not handle " + command + " and convert to LoxoneValue");
	}

	@Override
	public LoxoneValue convertToLoxoneValue(State state) {
		LoxoneValue posValue = states.get(STATE_POS);
		if (state instanceof PercentType) {
			PercentType percentType = (PercentType) state;
			int percent = percentType.intValue();
			double min = states.get(STATE_MIN).getValueAsDouble();
			double max = states.get(STATE_MAX).getValueAsDouble();
			double value = scaleToValue(min,max,percent);
			posValue.updateValue(toRoundedValue(value));
			return LoxoneValue.create(uuidAction, posValue.getValue());
		}
		if (state instanceof OnOffType) {
			OnOffType onOff = (OnOffType) state;
			switch (onOff) {
			case ON:
				posValue.updateValue(states.get(STATE_MAX).getValue());
				break;
			case OFF:
			default:
				posValue.updateValue(states.get(STATE_MIN).getValue());
				break;
			}
			return LoxoneValue.create(uuidAction, posValue.getValue());
		}
		throw new IllegalStateException("Could not convert " + state + " to LoxoneValue");
	}

	@Override
	public State convertToLoxoneState(LoxoneValue value) {
		LoxoneValue posValue = findLoxoneValue(STATE_POS, value.getUuid());
		if(posValue != null) {
			posValue.updateValue(value.getValue());
			double pos = posValue.getValueAsDouble();
			double min = states.get(STATE_MIN).getValueAsDouble();
			double max = states.get(STATE_MAX).getValueAsDouble();
			return new PercentType((int)(((pos - min) / max) * 100D));// scale the value to 0-100
		}
		LoxoneValue maxValue = findLoxoneValue(STATE_MAX, value.getUuid());
		if(maxValue != null) {
			maxValue.updateValue(value.getValue());
			return new DecimalType(value.getValue());
		}
		LoxoneValue minValue = findLoxoneValue(STATE_MIN, value.getUuid());
		if(minValue != null) {
			minValue.updateValue(value.getValue());
			return new DecimalType(value.getValue());
		}
		LoxoneValue stepValue = findLoxoneValue(STATE_STEP, value.getUuid());
		if(stepValue != null) {
			stepValue.updateValue(value.getValue());
			return new DecimalType(value.getValue());
		}
		return null;
	}
	
	private LoxoneValue findLoxoneValue(String name, String uuid) {
		LoxoneValue lv = states.get(name);
		if(lv != null && lv.getUuid().equals(uuid)) {
			return lv;
		}
		return null;
	}
	
	private static int scaleToValue(double min, double max, double percent) {
		return (int) (((max - min) * (percent / 100)) + min);
	}
	
	private static String toRoundedValue(double value) {
		return NumberFormat.getIntegerInstance().format(value);
	}

}
