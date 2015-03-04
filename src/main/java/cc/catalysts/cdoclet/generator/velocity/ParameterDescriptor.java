package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

/**
 * @author Catalysts Gmbh
 */
public class ParameterDescriptor extends Descriptor {
	private String parameterName;

	public ParameterDescriptor(Type parameterType, String parameterName) {
		super(parameterType, parameterType.getName());
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
