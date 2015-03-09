package cc.catalysts.cdoclet.generator;

import org.apache.commons.lang.ClassUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Type {
    public static final Type EMPTY = new Type("", null, null, 0, false, null);
    public static final Type NULL = new Type("null", null, null, 0, false, null);
    public static final Type VOID = new Type("void", null, null, 0, false, null);

    private final String name;
    private final Collection<Type> arguments;
    private final Map<String, Type> bounds;
    private final int dimensions;
    private final boolean generic;
    private final Map<String, String> classMap;

    private Map<String, Type> typeMap;

    public Type(String name, Collection<Type> arguments, Map<String, Type> bounds, int dimensions, boolean generic, Map<String, String> classMap) {
        if (name == null) throw new IllegalArgumentException("name");

        this.name = name;
        this.arguments = arguments;
        this.bounds = bounds;
        this.dimensions = dimensions;
        this.generic = generic;
        this.classMap = classMap;
    }

    public int getDimensions() {
        return dimensions;
    }

    public Collection<String> getImports() {
        Collection<String> imports = getImportsInternal();
        imports.remove(getName());
        // todo remove imports from same package
        return imports;
    }

    protected Collection<String> getImportsInternal() {
        Collection<String> set = new TreeSet<String>();

        addImports(set, getArguments());
        if (getBounds() != null) addImports(set, getBounds().values());

        return set;
    }

    public Collection<Type> getArguments() {
        return (arguments == null) || arguments.isEmpty() ? null : arguments;
    }

    public Map<String, Type> getBounds() {
        return (bounds == null) || bounds.isEmpty() ? null : bounds;
    }

    protected Collection<String> addImports(Collection<String> set, Collection<? extends Type> types) {
        if (types != null) for (Type arg : types) {
            set.addAll(arg.getImports());
            if (arg.getName().indexOf('.') > -1) set.add(arg.getName());
        }
        return set;
    }

    public String getName() {
        return name;
    }

    public Map<String, Type> getTypeMap() {
        if (typeMap == null) typeMap = new HashMap<String, Type>();
        return typeMap;
    }

    public void setTypeMap(Map<String, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public boolean isGeneric() {
        return generic;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Collection<String> getPackageImports() {
        Collection<String> set = new TreeSet<String>();
        for (String imp : getImports()) {
            set.add(ClassUtils.getPackageName(imp));
        }
        set.remove(getNameSpace());
        return set;
    }

    public String getNameSpace() {
        return ClassUtils.getPackageName(getQualifiedTypeName());
    }

    public String getQualifiedTypeName() {
        String typeName = getName();

        if (classMap != null && classMap.containsKey(typeName)) {
            return classMap.get(typeName);
        }

        return typeName;
    }

    public String getTypeName() {
        return ClassUtils.getShortClassName(getQualifiedTypeName());
    }
}
