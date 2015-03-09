package cc.catalysts.cdoclet.generator;

import cc.catalysts.cdoclet.generator.utils.GeneratorUtils;
import cc.catalysts.cdoclet.handler.Constants;
import cc.catalysts.cdoclet.map.TypeMap;
import com.sun.javadoc.ClassDoc;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.badgersinfoil.metaas.ActionScriptFactory;
import uk.co.badgersinfoil.metaas.ActionScriptProject;
import uk.co.badgersinfoil.metaas.dom.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static cc.catalysts.cdoclet.generator.Languages.ACTIONSCRIPT;

public class AsGenerator implements Generator {
    private static ActionScriptFactory factory = new ActionScriptFactory();

    private final Logger logger = LoggerFactory.getLogger(AsGenerator.class);

    private ActionScriptProject project;

    private ASCompilationUnit unit;
    private ASCompilationUnit proxyUnit;

    private ASType type;
    private ASClassType eventType;
    private ASClassType proxyType;

    private ASMethod method;
    private ASMethod proxyMethod;

    private final Map<String, String> classMap = new HashMap<String, String>();
    private final TypeMap annotationMap;
    private final TypeMap annotationTypeMap;
    private final TypeMap packageMap;
    private final TypeMap typeMap;
    private final Class<? extends Annotation> enumAnnotation;
    private final String suffix;

    private Collection<String> imports;
    private Collection<String> proxyImports;

    public AsGenerator(String destination, String namespace, String suffix, Class<? extends Annotation> enumAnnotation, TypeMap typeMap, TypeMap packageMap, TypeMap annotationTypeMap, TypeMap annotationMap) {
        this.enumAnnotation = enumAnnotation;
        this.typeMap = typeMap;
        this.packageMap = packageMap;
        this.annotationTypeMap = annotationTypeMap;
        this.annotationMap = annotationMap;
        this.suffix = suffix;

        project = factory.newEmptyASProject(destination);

        initTypeMap();
    }

    private void initTypeMap() {
        annotationMap.addTypeMapping("java.lang.Deprecated", "Deprecated");

        typeMap.setEnumType("String");

        typeMap.addTypeMapping("boolean", "Boolean");
        typeMap.addTypeMapping("byte", "Number");
        typeMap.addTypeMapping("double", "Number");
        typeMap.addTypeMapping("float", "Number");
        typeMap.addTypeMapping("long", "Number");
        typeMap.addTypeMapping("short", "Number");

        // those need to be 'Object's since 'Number's cannot be null
        typeMap.addTypeMapping("java.lang.Boolean", "Object");
        typeMap.addTypeMapping("java.lang.Byte", "Object");
        typeMap.addTypeMapping("java.lang.Double", "Object");
        typeMap.addTypeMapping("java.lang.Float", "Object");
        typeMap.addTypeMapping("java.lang.Integer", "Object");
        typeMap.addTypeMapping("java.lang.Long", "Object");
        typeMap.addTypeMapping("java.lang.Number", "Object");
        typeMap.addTypeMapping("java.lang.Short", "Object");

        typeMap.addTypeMapping("java.lang.Class", "Object");
        typeMap.addTypeMapping("java.lang.Object", "Object");
        typeMap.addTypeMapping("java.lang.String", "String");

        typeMap.addTypeMapping("java.util.Collection", "Array");
        typeMap.addTypeMapping("java.util.Date", "Date");
        typeMap.addTypeMapping("java.util.List", "Array");
        typeMap.addTypeMapping("java.util.Map", "Array");
        typeMap.addTypeMapping("java.util.Set", "Array");

        typeMap.addTypeMapping("java.lang.Exception", "null");
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

    public TypeMap getPackageMap() {
        return packageMap;
    }

    public TypeMap getTypeMap() {
        return typeMap;
    }

// --------------------- Interface Generator ---------------------

    public void addAnnotation(Type tag) {
        if (method != null) {
            method.newMetaTag(tag.getQualifiedTypeName());
        } else {
            type.newMetaTag(tag.getQualifiedTypeName());
        }
    }

    public void addSuperclass(Type tag) {
    }

    public void addBody(String body) {
    }


    public void addConstant(Type classType, Type constantType, String name, String initializer, String comment) {
        if (type instanceof ASClassType) {
            // actionscript/metaas doesn't support constants in interfaces
            createConst((ASClassType) type, name, initializer, constantType.getQualifiedTypeName(), comment);
        }
    }


    public void addEnumField(Type classType, Type fieldType, String name, Object value, String comment) {
        if (value == null) value = name;
        String s = value instanceof Number ? value.toString() : MessageFormat.format("\"{0}\"", name);
        addConstant(classType, fieldType, name, s, comment);
    }


    public void addField(Type classType, int modifier, Type fieldType, String fieldName, Object value, String comment) {
        fieldType = resolveTypeArguments(classType, null, fieldType);

        ASClassType asClassType = (ASClassType) type;
        if (asClassType.getField(fieldName) == null)
            asClassType.newField(fieldName, getVisibility(modifier), fieldType.getQualifiedTypeName());
    }


    public void addInterface(Type name) {
        if (type instanceof ASInterfaceType) {
            ASInterfaceType interfaceType = (ASInterfaceType) type;
            interfaceType.addSuperInterface(name.getQualifiedTypeName());
        } else {
            ASClassType classType = (ASClassType) type;
            classType.addImplementedInterface(name.getQualifiedTypeName());
        }
        addImport(name.getQualifiedTypeName());
    }


    public void addParameter(Type classType, Type methodType, Type type, String name) {
        type = resolveTypeArguments(classType, methodType, type);

        if (method != null) method.addParam(name, type.getQualifiedTypeName());
        if (proxyMethod != null) proxyMethod.addParam(name, type.getQualifiedTypeName());

        addImport(type.getQualifiedTypeName());
    }


    public void beginClass(Type classType) {
        logger.info("Creating ActionScript class {}", classType);

        if (suffix != null) {
            getClassMap().put(classType.getName(), classType.getName() + suffix);
        }

        newClass(classType.getQualifiedTypeName(), false);
        addAnnotation(GeneratorUtils.getType("RemoteClass(alias=\"" + classType.getQualifiedTypeName() + "\")", this));
    }


    public void beginEnum(Type name) {
        logger.info("Creating ActionScript enumeration {}", name);

        newClass(name.getQualifiedTypeName(), true);
    }


    public void beginGetter(Type classType, Type methodType, int modifier, Type fieldType, String propertyName, String comment, boolean override) {
        fieldType = resolveTypeArguments(classType, methodType, fieldType);

        if (type instanceof ASClassType) {
            addField(classType, Modifier.PRIVATE, fieldType, "_" + propertyName, null, null);
            beginMethod(classType, Type.EMPTY, modifier, fieldType, "get " + propertyName, false, override);
            method.addStmt("return _" + propertyName + ";");
        } else {
            beginMethod(classType, Type.EMPTY, modifier, fieldType, "get " + propertyName, false, override);
        }

        setMethodDescription(comment);
    }


    public void beginInterface(Type name) {
        logger.info("Creating ActionScript interface {}", name);

        unit = project.newInterface(name.getQualifiedTypeName());
        type = unit.getType();
        imports = new TreeSet<String>();
    }


    public void beginMethod(Type classType, Type methodType, int modifier, Type returnType, String methodName, boolean asnyc, boolean override) {
        beginMethod(classType, methodType, modifier, returnType, methodName, asnyc, override, null);
    }

    public void beginMethod(Type classType, Type methodType, int modifier, Type returnType, String methodName, boolean asnyc, boolean override, String verb) {
        returnType = resolveTypeArguments(classType, methodType, returnType);

        if (unit != null) {
            method = type.newMethod(methodName, getVisibility(modifier), returnType.getQualifiedTypeName());
            method.setOverride(override);
        }

        if (proxyUnit != null) {
            String proxyMethodName = firstToUpper(methodName);
            boolean hasField = false;

            for (Object o : eventType.getFields()) {
                ASField field = (ASField) o;
                if (field.getName().equals(proxyMethodName)) {
                    hasField = true;
                    break;
                }
            }

            if (!hasField) {
                createConst(eventType, proxyMethodName, MessageFormat.format("\"{0}\"", methodName), "String", null);
                proxyMethod = proxyType.newMethod(methodName, Visibility.PUBLIC, returnType.getQualifiedTypeName());
            }
        }

        addImport(returnType.getQualifiedTypeName());
    }


    public void beginProxy(Type proxy, Type baseType, Type interfaceType) {
        logger.info("Creating ActionScript proxy {}", proxy.getQualifiedTypeName());

        ASCompilationUnit eventUnit = project.newClass(proxy.getQualifiedTypeName() + "Events");
        proxyUnit = project.newClass(proxy.getQualifiedTypeName());

        eventType = (ASClassType) eventUnit.getType();
        proxyType = (ASClassType) proxyUnit.getType();

        proxyType.addImplementedInterface(interfaceType.getQualifiedTypeName());

        proxyImports = new TreeSet<String>();
        proxyImports.add(interfaceType.getQualifiedTypeName());

        if (baseType != Type.NULL) {
            proxyType.setSuperclass(baseType.getQualifiedTypeName());
            proxyImports.add(baseType.getQualifiedTypeName());
        }

        ASMethod callMethod = proxyType.newMethod(Constants.METHOD_CALL, Visibility.PROTECTED, "Object");
        callMethod.addParam("name", "String");
        callMethod.addParam("...args", null);
        callMethod.addStmt("throw new Error(\"Not Implemented\");");

        ASMethod resultMethod = proxyType.newMethod(Constants.METHOD_ON_RESULT, Visibility.PROTECTED, Type.VOID.getQualifiedTypeName());
        resultMethod.addParam("result", "Object");

        ASMethod statusMethod = proxyType.newMethod(Constants.METHOD_ON_STATUS, Visibility.PROTECTED, Type.VOID.getQualifiedTypeName());
        statusMethod.addParam("status", "Object");
    }


    public void beginSetter(Type classType, Type methodType, int modifier, Type fieldType, String propertyName, String comment, boolean override) {
        fieldType = resolveTypeArguments(classType, methodType, fieldType);

        if (type instanceof ASClassType) {
            addField(classType, Modifier.PRIVATE, fieldType, "_" + propertyName, null, null);
            beginMethod(classType, Type.EMPTY, modifier, Type.VOID, "set " + propertyName, false, override);
            method.addParam("value", fieldType.getQualifiedTypeName());
            method.addStmt("_" + propertyName + "=value;");
        } else {
            beginMethod(classType, Type.EMPTY, modifier, Type.VOID, "set " + propertyName, false, override);
            method.addParam("value", fieldType.getQualifiedTypeName());
        }

        setMethodDescription(comment);
    }


    public void endClass() {
        end();
    }


    public void endEnum() {
        end();
    }


    public void endGetter() {
        endMethod(Type.EMPTY);
    }


    public void endInterface() {
        end();
    }


    public void endMethod(Type asyncType) {
        if (proxyMethod != null) {
            String event = eventType.getName() + '.' + firstToUpper(proxyMethod.getName());
            StringBuilder args = new StringBuilder();

            for (Object o : proxyMethod.getArgs()) {
                ASArg arg = (ASArg) o;
                args.append(", ").append(arg.getName());
            }

            if (Type.VOID.getQualifiedTypeName().equals(proxyMethod.getType())) {
                proxyMethod.addStmt(MessageFormat.format("{0}({1}{2});", Constants.METHOD_CALL, event, args));
            } else {
                proxyMethod.addStmt(MessageFormat.format("return {0}({1}{2}) as {3};", Constants.METHOD_CALL, event, args, proxyMethod.getType()));
            }
        }

        proxyMethod = null;
        method = null;
    }


    public void endProxy() {
        for (String imp : proxyImports) proxyUnit.getPackage().addImport(imp);
        proxyImports = null;
        proxyUnit = null;
    }


    public void endSetter() {
        endMethod(Type.EMPTY);
    }


    public void generate() throws IOException {
        //project.performAutoImport(); // todo this is buggy
        project.writeAll();
    }

    @Override
    public Map<String, String> getClassMap() {
        return classMap;
    }


    public String getName() {
        return ACTIONSCRIPT;
    }


    public boolean hasEnumSupport() {
        return false;
    }


    public Type postProcessType(Type type) {
        if (type.getDimensions() > 0) return GeneratorUtils.getType(Collection.class.getCanonicalName(), this);
        return type;
    }


    public void setMethodDescription(String description) {
        if (unit != null && !StringUtils.isEmpty(description)) method.setDescription(description.trim());
    }


    public void setSuperclass(Type name, boolean exception) {
        ((ASClassType) type).setSuperclass(name.getQualifiedTypeName());
        addImport(name.getQualifiedTypeName());
    }


    public void setTypeDescription(String description) {
        if (!StringUtils.isEmpty(description)) type.setDescription(description.trim());
    }


    public boolean traverse(ClassDoc classDoc) {
        return true;
    }

    private void createConst(ASClassType classType, String name, String initializer, String type, String comment) {
        ASField field = classType.newField(name, Visibility.PUBLIC, type);

        field.setInitializer(initializer);
        field.setStatic(true);
        field.setConst(true);

        if (!StringUtils.isEmpty(comment)) field.setDescription(comment.trim());
    }

    private void end() {
        for (String imp : imports) unit.getPackage().addImport(imp);

        type = null;
        unit = null;
        imports = null;
    }

    private String firstToUpper(String methodName) {
        return methodName.length() > 1 ? methodName.substring(0, 1).toUpperCase() + methodName.substring(1) : methodName.toUpperCase();
    }

    private Visibility getVisibility(int modifier) {
        switch (modifier) {
            case Modifier.PUBLIC:
                return Visibility.PUBLIC;

            case Modifier.PROTECTED:
                return Visibility.PROTECTED;

            case Modifier.PRIVATE:
                return Visibility.PRIVATE;

            case Modifier.NATIVE:
                return Visibility.INTERNAL;

            default:
                return Visibility.DEFAULT;
        }
    }

    private void newClass(String name, boolean isFinal) {
        unit = project.newClass(name);
        type = unit.getType();
        ((ASClassType) type).setFinal(isFinal);

        imports = new TreeSet<String>();
    }

    private Type resolveTypeArguments(Type classType, Type methodType, Type type) {
        if (!type.isGeneric()) return type;

        String name = type.getQualifiedTypeName();
        if (methodType != null && methodType.getBounds() != null && methodType.getBounds().containsKey(name)) {
            Type returnType = methodType.getBounds().get(type.getQualifiedTypeName());
            if (returnType != null) {
                name = type.getQualifiedTypeName();
                addImport(name);
                return returnType;
            }
        }

        if (classType != null && classType.getBounds() != null && classType.getBounds().containsKey(name)) {
            Type returnType = classType.getBounds().get(type.getQualifiedTypeName());
            if (returnType != null) {
                name = type.getQualifiedTypeName();
                addImport(name);
                return returnType;
            }
        }

        return GeneratorUtils.getType(Object.class.getCanonicalName(), this);
    }

    private void addImport(String name) {
        if (name.indexOf('.') > -1) {
            if (unit != null) imports.add(name);
            if (proxyUnit != null) proxyImports.add(name);
        }
    }
}
