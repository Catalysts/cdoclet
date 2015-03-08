package cc.catalysts.cdoclet.map;

public class PackageTypeMap extends TypeMap {
    @Override
    public String getType(String typeName, boolean generic, boolean enumeration) {
        int index = typeName.lastIndexOf('.');
        if (index > 0 && typeName.length() > (index + 1)) {
            String className = typeName.substring(index + 1);
            String packageName = typeName.substring(0, index);

            String mappedPackageName = getRawType(packageName, generic, enumeration);

            if (mappedPackageName != null) {
                return mappedPackageName + "." + className;
            }
        }

        return typeName;
    }
}
