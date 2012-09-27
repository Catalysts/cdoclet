package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

/**
 * @author Catalysts Gmbh
 */
public class ProxyDescriptor extends ClassDescriptor {
	public ProxyDescriptor(Type type) {
		super(type);
	}

	@Override
	public String getTemplate() {
		return "proxy.vm";
	}
}
