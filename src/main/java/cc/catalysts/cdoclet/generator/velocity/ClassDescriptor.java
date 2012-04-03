package cc.catalysts.cdoclet.generator.velocity;

import java.util.Collection;

import cc.catalysts.cdoclet.generator.Type;

public class ClassDescriptor extends TypeDescriptor {
	private Type superClass;

	public ClassDescriptor(Type type) {
		super(type);
	}

	public Type getSuperClass() {
		return superClass;
	}

	@Override
	public Collection<String> getImportsInternal() {
		Collection<String> imports = super.getImportsInternal();
		if (superClass != null) {
			imports.add(superClass.getName());
			imports.addAll(superClass.getImports());
		}
		return imports;
	}

	public String getTemplate() {
		return "class.vm";
	}

	public void setSuperclass(Type superClass) {
		this.superClass = superClass;
	}
}
