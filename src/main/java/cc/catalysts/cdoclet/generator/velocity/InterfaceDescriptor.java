package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

public class InterfaceDescriptor extends TypeDescriptor {
	public InterfaceDescriptor(Type type) {
		super(type);
	}

	public String getTemplate() {
		return "interface.vm";
	}
}
