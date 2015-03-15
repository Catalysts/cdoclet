package cc.catalysts.cdoclet.generator.velocity;

import cc.catalysts.cdoclet.generator.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Catalysts Gmbh
 */
public class Descriptor extends Type {
    private String description;
    private Collection<Type> annotations = new ArrayList<Type>();
    private Collection<Type> superclass = new ArrayList<Type>();

    public Descriptor(Type type, Map<String, String> classMap) {
        super(type.getName(), type.getArguments(), type.getBounds(), type.getDimensions(), type.isAbstract(), type.isGeneric(), classMap);
    }

    public Collection<Type> getAnnotations() {
        return annotations;
    }

    public Collection<Type> getSuperclass() {
        return superclass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addAnnotation(Type annotation) {
        annotations.add(annotation);
    }

    public void addSuperclass(Type tag) {
        superclass.add(tag);
    }

    @Override
    public Collection<String> getImportsInternal() {
        return addImports(super.getImportsInternal(), annotations);
    }
}
