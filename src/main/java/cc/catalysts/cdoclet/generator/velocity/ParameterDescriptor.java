package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

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
