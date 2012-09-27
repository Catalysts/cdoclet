package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

/**
 * @author Catalysts Gmbh
 */
public class InterfaceDescriptor extends TypeDescriptor {
	public InterfaceDescriptor(Type type) {
		super(type);
	}

	public String getTemplate() {
		return "interface.vm";
	}
}
