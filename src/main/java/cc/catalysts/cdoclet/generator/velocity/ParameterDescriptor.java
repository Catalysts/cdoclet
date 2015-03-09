package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.util.Map;

/**
 * @author Catalysts Gmbh
 */
public class ParameterDescriptor extends Descriptor {
	private String parameterName;

	public ParameterDescriptor(Type parameterType, String parameterName, Map<String, String> classMap) {
		super(parameterType, classMap);
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
