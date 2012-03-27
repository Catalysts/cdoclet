package cc.catalysts.asdoclet.generator.velocity;

import cc.catalysts.asdoclet.generator.Type;

public class EnumDescriptor extends TypeDescriptor {
	public EnumDescriptor(Type typeName) {
		super(typeName);
	}

	public String getTemplate() {
		return "enum.vm";
	}
}
