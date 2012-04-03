package cc.catalysts.cdoclet.generator.velocity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.catalysts.cdoclet.generator.Type;

public abstract class TypeDescriptor extends Descriptor {
	private Map<String, PropertyDescriptor> propertyDescriptors = new LinkedHashMap<String, PropertyDescriptor>();
	private Map<String, FieldDescriptor> fieldDescriptors = new LinkedHashMap<String, FieldDescriptor>();
	private Collection<MethodDescriptor> methodDescriptors = new ArrayList<MethodDescriptor>();
	private Collection<Type> interfaces = new ArrayList<Type>();

	public TypeDescriptor(Type type) {
		super(type);
	}

	@Override
	public Collection<String> getImportsInternal() {
		Collection<String> imports = super.getImportsInternal();

		addImports(imports, propertyDescriptors.values());
		addImports(imports, fieldDescriptors.values());
		addImports(imports, methodDescriptors);
		addImports(imports, interfaces);

		return imports;
	}

	public Collection<Type> getInterfaces() {
		return interfaces;
	}

	public Collection<MethodDescriptor> getMethodDescriptors() {
		return methodDescriptors;
	}

	public void addFieldDescriptor(FieldDescriptor fieldDescriptor) {
		fieldDescriptors.put(fieldDescriptor.getFieldName(), fieldDescriptor);
	}

	public void addInterface(Type interfaceName) {
		interfaces.add(interfaceName);
	}

	public void addMethodDescriptor(MethodDescriptor methodDescriptor) {
		methodDescriptors.add(methodDescriptor);
	}

	public void addPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
		PropertyDescriptor pd = propertyDescriptors.get(propertyDescriptor.getPropertyName());
		if (pd != null) {
			pd.setGetter(pd.isGetter() || propertyDescriptor.isGetter());
			pd.setSetter(pd.isSetter() || propertyDescriptor.isSetter());
		} else {
			propertyDescriptors.put(propertyDescriptor.getPropertyName(), propertyDescriptor);
		}
	}

	public Collection<FieldDescriptor> getFieldDescriptors() {
		return fieldDescriptors.values();
	}

	public Collection<PropertyDescriptor> getPropertyDescriptors() {
		return propertyDescriptors.values();
	}

	public abstract String getTemplate();
}
