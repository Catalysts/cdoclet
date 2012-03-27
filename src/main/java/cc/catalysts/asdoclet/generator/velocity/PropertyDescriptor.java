package cc.catalysts.asdoclet.generator.velocity;

import java.lang.reflect.Modifier;

import cc.catalysts.asdoclet.generator.Type;

public class PropertyDescriptor extends Descriptor {
	private String fieldName;
	private int modifier;
	private boolean getter;
	private boolean setter;
	private boolean override;
	private Type genericType;

	public PropertyDescriptor(int modifier, Type type, Type genericType, String fieldName) {
		super(type);

		this.modifier = modifier;
		this.genericType = genericType;
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Type getGenericType() {
		return genericType;
	}

	public boolean isGetter() {
		return getter;
	}

	public void setGetter(boolean getter) {
		this.getter = getter;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public boolean isSetter() {
		return setter;
	}

	public void setSetter(boolean setter) {
		this.setter = setter;
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