package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author Catalysts Gmbh
 */
public class FieldDescriptor extends Descriptor {
	private String fieldName;
	private Object value;
	private int modifier;

	public FieldDescriptor(int modifier, Type type, String fieldName, Object value, Map<String, String> classMap) {
		super(type, classMap);

		this.fieldName = fieldName;
		this.value = value;
		this.modifier = modifier;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getValue() {
		return value;
	}

	public String getModifier() {
		switch (modifier) {
			case Modifier.PUBLIC:
				return "public";

			case Modifier.PROTECTED:
				return "protected";

			case Modifier.PRIVATE:
				return "private";

			case Modifier.STATIC:
				return "static";

			default:
				return "";
		}
	}

	public String getPropertyName() {
		if (fieldName.length() > 0) {
			return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		} else {
			return fieldName;
		}
	}
}
