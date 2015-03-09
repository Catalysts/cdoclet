package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.util.Collection;
import java.util.Map;

/**
 * @author Catalysts Gmbh
 */
public class ClassDescriptor extends TypeDescriptor {
    private Type superClass;

    public ClassDescriptor(Type type, Map<String, String> classMap) {
        super(type, classMap);
    }

    public Type getSuperClass() {
        return superClass;
    }

    @Override
    public Collection<String> getImportsInternal() {
        Collection<String> imports = super.getImportsInternal();
        if (superClass != null) {
            imports.add(superClass.getQualifiedTypeName());
            imports.addAll(superClass.getImports());
        }
        return imports;
    }

    public String getTemplate() {
        return "class.vm";
    }

    public void setSuperclass(Type superClass) {
        this.superClass = superClass;
    }
}
