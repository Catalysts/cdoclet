package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.util.Map;

public class ProxyDescriptor extends ClassDescriptor {
    public ProxyDescriptor(Type type, Map<String, String> classMap) {
        super(type, classMap);
    }

    @Override
    public String getTemplate() {
        return "proxy.vm";
    }
}
