package cc.catalysts.asdoclet.map;

public class ClassTypeMap extends TypeMap {
	@Override
	public String getType(String typeName, boolean generic, boolean enumeration) {
		String asType = getRawType(typeName, generic, enumeration);

		if (asType == null) {
			asType = typeName;
		}

		return asType;
	}
}
