package cc.catalysts.cdoclet.map;

import java.util.HashMap;
import java.util.Map;

public class TypeMap {
	protected final Map<String, String> typeMap = new HashMap<String, String>();
	protected final Map<String, String> genericTypeMap = new HashMap<String, String>();
	private String enumType;

	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}

	public void addGenericTypeMapping(String javaType, String asType) {
		if (!genericTypeMap.containsKey(javaType)) genericTypeMap.put(javaType, asType);
	}

	public void addTypeMapping(String javaType, String asType) {
		if (!typeMap.containsKey(javaType)) typeMap.put(javaType, asType);
	}

	public String getType(String typeName, boolean generic, boolean enumeration) {
		return getRawType(typeName, generic, enumeration);
	}

	protected String getRawType(String typeName, boolean generic, boolean enumeration) {
		String asType = null;

		if (enumType != null && enumeration) {
			asType = enumType;
		} else if (generic && genericTypeMap.containsKey(typeName)) {
			asType = genericTypeMap.get(typeName);
		} else if (typeMap.containsKey(typeName)) {
			asType = typeMap.get(typeName);
		}

		return asType;
	}
}
