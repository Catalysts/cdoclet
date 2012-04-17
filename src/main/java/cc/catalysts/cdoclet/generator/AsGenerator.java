package cc.catalysts.cdoclet.generator;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.catalysts.cdoclet.generator.utils.GeneratorUtils;
import cc.catalysts.cdoclet.handler.Constants;
import cc.catalysts.cdoclet.map.TypeMap;

import com.sun.javadoc.ClassDoc;
import uk.co.badgersinfoil.metaas.ActionScriptFactory;
import uk.co.badgersinfoil.metaas.ActionScriptProject;
import uk.co.badgersinfoil.metaas.dom.ASArg;
import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASCompilationUnit;
import uk.co.badgersinfoil.metaas.dom.ASField;
import uk.co.badgersinfoil.metaas.dom.ASInterfaceType;
import uk.co.badgersinfoil.metaas.dom.ASMethod;
import uk.co.badgersinfoil.metaas.dom.ASType;
import uk.co.badgersinfoil.metaas.dom.Visibility;

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

	private final TypeMap annotationMap;
	private final TypeMap annotationTypeMap;
	private final TypeMap typeMap;
	private final Class<? extends Annotation> enumAnnotation;

	private Collection<String> imports;
	private Collection<String> proxyImports;

	public AsGenerator(String destination, String namespace, Class<? extends Annotation> enumAnnotation, TypeMap typeMap, TypeMap annotationTypeMap, TypeMap annotationMap) {
		this.enumAnnotation = enumAnnotation;
		this.typeMap = typeMap;
		this.annotationTypeMap = annotationTypeMap;
		this.annotationMap = annotationMap;

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

	public TypeMap getTypeMap() {
		return typeMap;
	}

// --------------------- Interface Generator ---------------------

	public void addAnnotation(Type tag) {
		if (method != null) {
			method.newMetaTag(tag.getName());
		} else {
			type.newMetaTag(tag.getName());
		}
	}

	
	public void addBody(String body) {
	}

	
	public void addConstant(Type classType, Type constantType, String name, String initializer, String comment) {
		createConst((ASClassType) type, name, initializer, constantType.getName(), comment);
	}

	
	public void addEnumField(Type classType, Type fieldType, String name, Object value, String comment) {
		if (value == null) value = name;
		String s = value instanceof Number ? value.toString() : MessageFormat.format("\"{0}\"", name);
		addConstant(classType, fieldType, name, s, comment);
	}

	
	public void addField(Type classType, int modifier, Type fieldType, String fieldName, Object value, String comment) {
		fieldType = resolveTypeArguments(classType, null, fieldType);

		ASClassType asClassType = (ASClassType) type;
		if (asClassType.getField(fieldName) == null) asClassType.newField(fieldName, getVisibility(modifier), fieldType.getName());
	}

	
	public void addInterface(Type name) {
		if (type instanceof ASInterfaceType) {
			ASInterfaceType interfaceType = (ASInterfaceType) type;
			interfaceType.addSuperInterface(name.getName());
		} else {
			ASClassType classType = (ASClassType) type;
			classType.addImplementedInterface(name.getName());
		}
		addImport(name.getName());
	}

	
	public void addParameter(Type classType, Type methodType, Type type, String name) {
		type = resolveTypeArguments(classType, methodType, type);

		if (method != null) method.addParam(name, type.getName());
		if (proxyMethod != null) proxyMethod.addParam(name, type.getName());

		addImport(type.getName());
	}

	
	public void beginClass(Type classType) {
		logger.info("Creating ActionScript class {}", classType);

		newClass(classType, false);
		addAnnotation(GeneratorUtils.getType("RemoteClass(alias=\"" + classType.getName() + "\")", this));
	}

	
	public void beginEnum(Type name) {
		logger.info("Creating ActionScript enumeration {}", name);

		newClass(name, true);
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

		unit = project.newInterface(name.getName());
		type = unit.getType();
		imports = new TreeSet<String>();
	}

	
	public void beginMethod(Type classType, Type methodType, int modifier, Type returnType, String methodName, boolean asnyc, boolean override) {
		returnType = resolveTypeArguments(classType, methodType, returnType);

		if (unit != null) {
			method = type.newMethod(methodName, getVisibility(modifier), returnType.getName());
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
				proxyMethod = proxyType.newMethod(methodName, Visibility.PUBLIC, returnType.getName());
			}
		}

		addImport(returnType.getName());
	}

	
	public void beginProxy(Type proxy, Type baseType, Type interfaceType) {
		logger.info("Creating ActionScript proxy {}", proxy.getName());

		ASCompilationUnit eventUnit = project.newClass(proxy.getName() + "Events");
		proxyUnit = project.newClass(proxy.getName());

		eventType = (ASClassType) eventUnit.getType();
		proxyType = (ASClassType) proxyUnit.getType();

		proxyType.addImplementedInterface(interfaceType.getName());

		proxyImports = new TreeSet<String>();
		proxyImports.add(interfaceType.getName());

		if (baseType != Type.NULL) {
			proxyType.setSuperclass(baseType.getName());
			proxyImports.add(baseType.getName());
		}

		ASMethod callMethod = proxyType.newMethod(Constants.METHOD_CALL, Visibility.PROTECTED, "Object");
		callMethod.addParam("name", "String");
		callMethod.addParam("...args", null);
		callMethod.addStmt("throw new Error(\"Not Implemented\");");

		ASMethod resultMethod = proxyType.newMethod(Constants.METHOD_ON_RESULT, Visibility.PROTECTED, Type.VOID.getName());
		resultMethod.addParam("result", "Object");

		ASMethod statusMethod = proxyType.newMethod(Constants.METHOD_ON_STATUS, Visibility.PROTECTED, Type.VOID.getName());
		statusMethod.addParam("status", "Object");
	}

	
	public void beginSetter(Type classType, Type methodType, int modifier, Type fieldType, String propertyName, String comment, boolean override) {
		fieldType = resolveTypeArguments(classType, methodType, fieldType);

		if (type instanceof ASClassType) {
			addField(classType, Modifier.PRIVATE, fieldType, "_" + propertyName, null, null);
			beginMethod(classType, Type.EMPTY, modifier, Type.VOID, "set " + propertyName, false, override);
			method.addParam("value", fieldType.getName());
			method.addStmt("_" + propertyName + "=value;");
		} else {
			beginMethod(classType, Type.EMPTY, modifier, Type.VOID, "set " + propertyName, false, override);
			method.addParam("value", fieldType.getName());
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

	
	public String getName() {
		return "as";
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
		((ASClassType) type).setSuperclass(name.getName());
		addImport(name.getName());
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

	private void newClass(Type classType, boolean isFinal) {
		unit = project.newClass(classType.getName());
		type = unit.getType();
		((ASClassType) type).setFinal(isFinal);

		imports = new TreeSet<String>();
	}

	private Type resolveTypeArguments(Type classType, Type methodType, Type type) {
		if (!type.isGeneric()) return type;

		String name = type.getName();
		if (methodType != null && methodType.getBounds() != null && methodType.getBounds().containsKey(name)) {
			Type returnType = methodType.getBounds().get(type.getName());
			if (returnType != null) {
				name = type.getName();
				addImport(name);
				return returnType;
			}
		}

		if (classType != null && classType.getBounds() != null && classType.getBounds().containsKey(name)) {
			Type returnType = classType.getBounds().get(type.getName());
			if (returnType != null) {
				name = type.getName();
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
