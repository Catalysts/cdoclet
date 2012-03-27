package cc.catalysts.asdoclet.generator.velocity;

import java.util.ArrayList;
import java.util.Collection;

import cc.catalysts.asdoclet.generator.Type;

public class MethodDescriptor extends FieldDescriptor {
	private Collection<ParameterDescriptor> parameterDescriptors = new ArrayList<ParameterDescriptor>();
	private boolean async;
	private boolean override;
	private String body;
	private Type genericType;
	private Type callbackType;

	public MethodDescriptor(int modifier, Type type, Type genericType, String methodName, boolean async) {
		super(modifier, type, methodName, null);
		this.genericType = genericType;
		this.async = async;
	}

	public String getBody() {
		return body;
	}

	public Type getCallbackType() {
		return callbackType;
	}

	public void setCallbackType(Type callbackType) {
		this.callbackType = callbackType;
	}

	public Type getGenericType() {
		return genericType;
	}

	public Collection<ParameterDescriptor> getParameterDescriptors() {
		return parameterDescriptors;
	}

	public boolean isAsync() {
		return async;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public void addBody(String body) {
		this.body = body;
	}

	public void addParameterDescriptor(ParameterDescriptor parameterDescriptor) {
		parameterDescriptors.add(parameterDescriptor);
	}

	@Override
	public Collection<String> getImportsInternal() {
		return addImports(super.getImportsInternal(), parameterDescriptors);
	}

	public String getMethodName() {
		return getFieldName();
	}

	public String getUpperMethodName() {
		return getPropertyName();
	}
}
