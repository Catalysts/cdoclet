package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.util.Map;

/**
 * @author Catalysts Gmbh
 */
public class InterfaceDescriptor extends TypeDescriptor {
    public InterfaceDescriptor(Type type, Map<String, String> classMap) {
        super(type, classMap);
    }

    public String getTemplate() {
        return "interface.vm";
    }
}
