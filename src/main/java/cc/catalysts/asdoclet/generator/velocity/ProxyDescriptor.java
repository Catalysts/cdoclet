package cc.catalysts.asdoclet.generator.velocity;

import cc.catalysts.asdoclet.generator.Type;

public class ProxyDescriptor extends ClassDescriptor {
	public ProxyDescriptor(Type type) {
		super(type);
	}

	@Override
	public String getTemplate() {
		return "proxy.vm";
	}
}
