package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.util.Map;

/**
 * @author Catalysts Gmbh
 */
public class EnumDescriptor extends TypeDescriptor {
	public EnumDescriptor(Type typeName, Map<String, String> classMap) {
		super(typeName, classMap);
	}

	public String getTemplate() {
		return "enum.vm";
	}
}
