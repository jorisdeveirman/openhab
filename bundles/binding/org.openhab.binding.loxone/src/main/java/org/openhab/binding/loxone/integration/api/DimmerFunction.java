package org.openhab.binding.loxone.integration.api;

public class DimmerFunction extends AbstractLoxoneLocatedFunction {
	private String stateMinUuid;
	private String stateMaxUuid;
	private String statePosUuid;
	private String stateStepUuid;
	
	public DimmerFunction(String name, String uuidAction, LoxoneRoom room,
			LoxoneCategory category, boolean readonly) {
		super(name, uuidAction, room, category, readonly);
	}

	@Override
	public <V> V visit(LoxoneFunctionVisitor<V> visitor) {
		return visitor.visit(this);
	}

	public String getStateMinUuid() {
		return stateMinUuid;
	}

	public void setStateMinUuid(String stateMinUuid) {
		this.stateMinUuid = stateMinUuid;
	}

	public String getStateMaxUuid() {
		return stateMaxUuid;
	}

	public void setStateMaxUuid(String stateMaxUuid) {
		this.stateMaxUuid = stateMaxUuid;
	}

	public String getStatePosUuid() {
		return statePosUuid;
	}

	public String getStateStepUuid() {
		return stateStepUuid;
	}

	public void setStateStepUuid(String stateStepUuid) {
		this.stateStepUuid = stateStepUuid;
	}

	public void setStatePosUuid(String statePosUuid) {
		this.statePosUuid = statePosUuid;
	}

}
