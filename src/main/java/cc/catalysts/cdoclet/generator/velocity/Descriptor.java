package cc.catalysts.cdoclet.generator.velocity;

import java.util.ArrayList;
import java.util.Collection;

import cc.catalysts.cdoclet.generator.Type;

public class Descriptor extends Type {
	private String description;
	private Collection<Type> annotations = new ArrayList<Type>();

	public Descriptor(Type type) {
		super(type.getName(), type.getArguments(), type.getBounds(), type.getDimensions(), type.isGeneric());
	}

	public Collection<Type> getAnnotations() {
		return annotations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addAnnotation(Type annotation) {
		annotations.add(annotation);
	}

	@Override
	public Collection<String> getImportsInternal() {
		return addImports(super.getImportsInternal(), annotations);
	}
}
