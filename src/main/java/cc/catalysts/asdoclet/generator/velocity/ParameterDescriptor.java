package cc.catalysts.asdoclet.generator.velocity;

import cc.catalysts.asdoclet.generator.Type;

public class ParameterDescriptor extends Descriptor {
	private String parameterName;

	public ParameterDescriptor(Type parameterType, String parameterName) {
		super(parameterType);
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
