package cc.catalysts.asdoclet.generator.velocity;

import cc.catalysts.asdoclet.generator.Type;

public class InterfaceDescriptor extends TypeDescriptor {
	public InterfaceDescriptor(Type type) {
		super(type);
	}

	public String getTemplate() {
		return "interface.vm";
	}
}
