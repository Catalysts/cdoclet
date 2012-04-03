package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

public class EnumDescriptor extends TypeDescriptor {
	public EnumDescriptor(Type typeName) {
		super(typeName);
	}

	public String getTemplate() {
		return "enum.vm";
	}
}
