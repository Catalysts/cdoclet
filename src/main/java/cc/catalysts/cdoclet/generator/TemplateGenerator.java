package cc.catalysts.cdoclet.generator;

import cc.catalysts.cdoclet.generator.utils.GeneratorUtils;
import cc.catalysts.cdoclet.generator.velocity.*;
import cc.catalysts.cdoclet.map.TypeMap;
import com.sun.javadoc.ClassDoc;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

public class TemplateGenerator implements Generator {

    private Collection<TypeDescriptor> typeDescriptors = new ArrayList<TypeDescriptor>();

    private TypeDescriptor typeDescriptor;
    private ClassDescriptor proxyTypeDescriptor;

    private FieldDescriptor fieldDescriptor;
    private MethodDescriptor methodDescriptor;
    private MethodDescriptor proxyMethodDescriptor;
    private PropertyDescriptor propertyDescriptor;

    private final File destination;
    private final String language;
    private final String namespace;

    private final TypeMap annotationMap;
    private final TypeMap annotationTypeMap;
    private final TypeMap typeMap;
    private final Class<? extends Annotation> enumAnnotation;

    public TemplateGenerator(String destination, String namespace, String language, Class<? extends Annotation> enumAnnotation, TypeMap typeMap, TypeMap annotationTypeMap, TypeMap annotationMap) throws Exception {
        this.destination = new File(destination);
        this.namespace = namespace;
        this.language = language;

        this.enumAnnotation = enumAnnotation;
        this.typeMap = typeMap;
        this.annotationTypeMap = annotationTypeMap;
        this.annotationMap = annotationMap;

        Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "cc.catalysts.cdoclet.generator.velocity.SLF4JLogChute");
        Velocity.setProperty(Velocity.RESOURCE_LOADER, "class");
        Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init();

        initTypeMap();
    }

    private void initTypeMap() {
        if (Languages.CSHARP.equals(language)) {
            initCsTypeMap();
        } else if (Languages.JAVA.equals(language)) {
            initJavaTypeMap();
        }
    }

    private void initJavaTypeMap() {
        annotationMap.addTypeMapping("java.lang.Deprecated", "Deprecated");

        typeMap.addTypeMapping("java.lang.Boolean", "Boolean");
        typeMap.addTypeMapping("java.lang.Byte", "Byte");
        typeMap.addTypeMapping("java.lang.Double", "Double");
        typeMap.addTypeMapping("java.lang.Float", "Float");
        typeMap.addTypeMapping("java.lang.Integer", "Integer");
        typeMap.addTypeMapping("java.lang.Long", "Long");
        typeMap.addTypeMapping("java.lang.Number", "Number");
        typeMap.addTypeMapping("java.lang.Short", "Short");

        typeMap.addTypeMapping("java.lang.Class", "Class");
        typeMap.addTypeMapping("java.lang.Object", "Object");
        typeMap.addTypeMapping("java.lang.String", "String");
        typeMap.addTypeMapping("java.lang.Exception", "Exception");
        typeMap.addTypeMapping("java.lang.Throwable", "Throwable");
    }

    private void initCsTypeMap() {
        annotationMap.addTypeMapping("java.lang.Deprecated", "System.Obsolete");

        typeMap.addTypeMapping("boolean", "bool");

        typeMap.addTypeMapping("java.lang.Boolean", "bool?");
        typeMap.addTypeMapping("java.lang.Byte", "byte?");
        typeMap.addTypeMapping("java.lang.Double", "double?");
        typeMap.addTypeMapping("java.lang.Float", "float?");
        typeMap.addTypeMapping("java.lang.Integer", "int?");
        typeMap.addTypeMapping("java.lang.Long", "long?");
        typeMap.addTypeMapping("java.lang.Number", "long?");
        typeMap.addTypeMapping("java.lang.Short", "short?");

        typeMap.addTypeMapping("java.lang.Class", "System.Type<>");
        typeMap.addTypeMapping("java.lang.Object", "object");
        typeMap.addTypeMapping("java.lang.String", "string");
        typeMap.addTypeMapping("java.lang.Exception", "System.Exception");

        typeMap.addTypeMapping("java.util.Date", "System.DateTime?");
        typeMap.addTypeMapping("java.util.Collection", "System.Collections.ICollection");
        typeMap.addTypeMapping("java.util.List", "System.Collections.IList");
        typeMap.addTypeMapping("java.util.Map", "System.Collections.IDictionary");
        typeMap.addTypeMapping("java.util.Set", "System.Collections.ICollection");

        typeMap.addGenericTypeMapping("java.util.Collection", "System.Collections.Generic.ICollection");
        typeMap.addGenericTypeMapping("java.util.List", "System.Collections.Generic.IList");
        typeMap.addGenericTypeMapping("java.util.Map", "System.Collections.Generic.IDictionary");
        typeMap.addGenericTypeMapping("java.util.Set", "System.Collections.Generic.ICollection");
    }


    public TypeMap getAnnotationMap() {
        return annotationMap;
    }


    public TypeMap getAnnotationTypeMap() {
        return annotationTypeMap;
    }


    public Class<? extends Annotation> getEnumAnnotation() {
        return enumAnnotation;
    }


    public TypeMap getTypeMap() {
        return typeMap;
    }

// --------------------- Interface Generator ---------------------


    public void addAnnotation(Type annotation) {
        if (methodDescriptor != null || proxyMethodDescriptor != null) {
            if (methodDescriptor != null) methodDescriptor.addAnnotation(annotation);
            if (proxyMethodDescriptor != null) proxyMethodDescriptor.addAnnotation(annotation);
        } else if (propertyDescriptor != null) {
            propertyDescriptor.addAnnotation(annotation);
        } else {
            typeDescriptor.addAnnotation(annotation);
        }
    }

    public void addSuperclass(Type tag) {
        if (methodDescriptor != null || proxyMethodDescriptor != null) {
            if (methodDescriptor != null) methodDescriptor.addSuperclass(tag);
            if (proxyMethodDescriptor != null) proxyMethodDescriptor.addSuperclass(tag);
        } else if (propertyDescriptor != null) {
            propertyDescriptor.addSuperclass(tag);
        } else {
            typeDescriptor.addSuperclass(tag);
        }
    }

    public void addBody(String body) {
        if (methodDescriptor != null) methodDescriptor.addBody(body);
        if (proxyMethodDescriptor != null) proxyMethodDescriptor.addBody(body);
    }


    public void addConstant(Type classType, Type constantType, String name, String initializer, String comment) {
        addField(classType, Modifier.STATIC, constantType, name, initializer, comment);
    }


    public void addEnumField(Type classType, Type fieldType, String name, Object value, String comment) {
        addField(classType, 0, fieldType, name, value, comment);
    }


    public void addField(Type classType, int modifier, Type fieldType, String propertyName, Object value, String comment) {
        fieldDescriptor = new FieldDescriptor(modifier, fieldType, propertyName, value);
        setFieldDescription(comment);

        typeDescriptor.addFieldDescriptor(fieldDescriptor);
    }


    public void addInterface(Type name) {
        typeDescriptor.addInterface(name);
    }


    public void addParameter(Type classType, Type methodType, Type type, String name) {
        if (methodDescriptor != null) {
            ParameterDescriptor parameterDescriptor = new ParameterDescriptor(type, name);
            methodDescriptor.addParameterDescriptor(parameterDescriptor);
        }
        if (proxyMethodDescriptor != null) {


            type = replaceType(type);


            ParameterDescriptor parameterDescriptor = new ParameterDescriptor(type, name);
            proxyMethodDescriptor.addParameterDescriptor(parameterDescriptor);
        }
    }

    private Type replaceType(Type type) {
        if (type.getTypeMap().containsKey(type.getName())) return type.getTypeMap().get(type.getName());

        Collection<Type> arguments = new ArrayList<Type>();
        if (type.getArguments() != null) {
            for (Type argument : type.getArguments()) {
                argument.setTypeMap(type.getTypeMap());
                arguments.add(replaceType(argument));
            }
        }

        return new Type(type.getName(), arguments, type.getBounds(), type.getDimensions(), type.isGeneric());
    }


    public void beginClass(Type type) {
        beginType(new ClassDescriptor(type));
    }


    public void beginEnum(Type type) {
        beginType(new EnumDescriptor(type));
    }


    public void beginGetter(Type classType, Type methodType, int modifier, Type returnType, String propertyName, String description, boolean override) {
        propertyDescriptor = new PropertyDescriptor(modifier, returnType, methodType, propertyName);
        propertyDescriptor.setGetter(true);
        propertyDescriptor.setOverride(override);
        propertyDescriptor.setDescription(description);
        typeDescriptor.addPropertyDescriptor(propertyDescriptor);

        if (!override && typeDescriptor instanceof ClassDescriptor) {
            addField(classType, Modifier.PRIVATE, returnType, propertyName, null, description);
        }
    }


    public void beginInterface(Type type) {
        beginType(new InterfaceDescriptor(type));
    }


    public void beginMethod(Type classType, Type methodType, int modifier, Type returnType, String methodName, boolean asnyc, boolean override) {
        beginMethod(classType, methodType, modifier, returnType, methodName, asnyc, override, null);
    }

    public void beginMethod(Type classType, Type methodType, int modifier, Type returnType, String methodName, boolean asnyc, boolean override, String verb) {
        if (typeDescriptor != null) {
            methodDescriptor = new MethodDescriptor(modifier, returnType, methodType, methodName, asnyc);
            methodDescriptor.setOverride(override);
            typeDescriptor.addMethodDescriptor(methodDescriptor);
        }
        if (proxyTypeDescriptor != null) {
            proxyMethodDescriptor = new MethodDescriptor(modifier, returnType, methodType, methodName, asnyc, verb);
            proxyTypeDescriptor.addMethodDescriptor(proxyMethodDescriptor);
        }
    }


    public void beginProxy(Type proxy, Type baseType, Type interfaceType) {
        proxyTypeDescriptor = new ProxyDescriptor(proxy);
        proxyTypeDescriptor.addInterface(interfaceType);

        if (baseType != Type.NULL) proxyTypeDescriptor.setSuperclass(baseType);

        typeDescriptors.add(proxyTypeDescriptor);
    }


    public void beginSetter(Type classType, Type methodType, int modifier, Type returnType, String propertyName, String description, boolean override) {
        propertyDescriptor = new PropertyDescriptor(modifier, returnType, methodType, propertyName);
        propertyDescriptor.setSetter(true);
        propertyDescriptor.setOverride(override);
        propertyDescriptor.setDescription(description);
        typeDescriptor.addPropertyDescriptor(propertyDescriptor);

        if (!override && typeDescriptor instanceof ClassDescriptor) {
            addField(classType, Modifier.PRIVATE, returnType, propertyName, null, description);
        }
    }


    public void endClass() {
        typeDescriptor = null;
    }


    public void endEnum() {
        typeDescriptor = null;
    }


    public void endGetter() {
        propertyDescriptor = null;
    }


    public void endInterface() {
        typeDescriptor = null;
    }


    public void endMethod(Type callbackType) {
        if (callbackType != cc.catalysts.cdoclet.generator.Type.EMPTY) {
            if (methodDescriptor != null) methodDescriptor.setCallbackType(callbackType);
            if (proxyMethodDescriptor != null) proxyMethodDescriptor.setCallbackType(replaceType(callbackType));
        }

        methodDescriptor = null;
        proxyMethodDescriptor = null;
    }


    public void endProxy() {
        proxyTypeDescriptor = null;
    }


    public void endSetter() {
        propertyDescriptor = null;
    }


    public void generate() throws Exception {
        for (TypeDescriptor descriptor : typeDescriptors) {
            String ns = descriptor.getNameSpace();

            if (namespace != null && ns.indexOf(namespace) == 0) {
                ns = ns.substring(namespace.length());
                if (ns.startsWith(".")) ns = ns.substring(1);
            }

            String folderName = ns.replace('.', File.separatorChar);

            File folder = new File(this.destination, folderName);
            folder.mkdirs();

            Writer writer = new FileWriter(new File(folder, descriptor.getTypeName() + "." + language));

            VelocityContext context = new VelocityContext();
            String s = descriptor.getTemplate();
            Template template = Velocity.getTemplate("templates/" + language + "/" + s);

            context.put("typeDescriptor", descriptor);
            template.merge(context, writer);
            writer.close();
        }
    }


    public String getName() {
        return language;
    }


    public boolean hasEnumSupport() {
        return true;
    }


    public Type postProcessType(Type type) {
        return type;
    }


    public void setMethodDescription(String description) {
        if (!StringUtils.isEmpty(description)) {
            if (methodDescriptor != null) methodDescriptor.setDescription(description.trim());
            if (proxyMethodDescriptor != null) proxyMethodDescriptor.setDescription(description.trim());
        }
    }


    public void setSuperclass(Type type, boolean exception) {
        ((ClassDescriptor) typeDescriptor).setSuperclass(type);

        if (exception) {
            if (Languages.CSHARP.equals(language)) {

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addBody(" : base() {}");
                endMethod(Type.EMPTY);

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(String.class.getCanonicalName(), this), "message");
                addBody(" : base(message) {}");
                endMethod(Type.EMPTY);

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(String.class.getCanonicalName(), this), "message");
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(Exception.class.getCanonicalName(), this), "exception");
                addBody(" : base(message, exception) {}");
                endMethod(Type.EMPTY);

            } else if (Languages.JAVA.equals(language)) {

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addBody("{\n\t}");
                endMethod(Type.EMPTY);

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(String.class.getCanonicalName(), this), "message");
                addBody("{\n\t\tsuper(message);\n\t}");
                endMethod(Type.EMPTY);

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(Throwable.class.getCanonicalName(), this), "cause");
                addBody("{\n\t\tsuper(cause);\n\t}");
                endMethod(Type.EMPTY);

                beginMethod(type, Type.EMPTY, Modifier.PUBLIC, Type.EMPTY, typeDescriptor.getTypeName(), false, false);
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(String.class.getCanonicalName(), this), "message");
                addParameter(type, Type.EMPTY, GeneratorUtils.getType(Throwable.class.getCanonicalName(), this), "cause");
                addBody("{\n\t\tsuper(message, cause);\n\t}");
                endMethod(Type.EMPTY);

            }
        }
    }


    public void setTypeDescription(String description) {
        if (!StringUtils.isEmpty(description)) typeDescriptor.setDescription(description.trim());
    }


    public boolean traverse(ClassDoc classDoc) {
        return classDoc.isIncluded();
    }

    private void beginType(TypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
        typeDescriptors.add(typeDescriptor);
    }

    private void setFieldDescription(String description) {
        if (!StringUtils.isEmpty(description)) fieldDescriptor.setDescription(description.trim());
    }
}
