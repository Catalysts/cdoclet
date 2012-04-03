package cc.catalysts.cdoclet.generator;

import java.lang.annotation.Annotation;


import cc.catalysts.cdoclet.map.TypeMap;

import com.sun.javadoc.ClassDoc;

public interface Generator {
	void addAnnotation(Type annotation);

	void addBody(String body);

	void addConstant(Type classType, Type constantType, String name, String initializer, String comment);

	void addEnumField(Type classType, Type fieldType, String name, Object value, String comment);

	void addField(Type classType, int modifier, Type fieldType, String fieldName, Object value, String comment);

	void addInterface(Type type);

	void addParameter(Type classType, Type methodType, Type type, String name);

	void beginClass(Type type);

	void beginEnum(Type type);

	void beginGetter(Type classType, Type methodType, int modifier, Type fieldType, String propertyName, String comment, boolean override);

	void beginInterface(Type type);

	void beginMethod(Type classType, Type methodType, int modifier, Type returnType, String methodName, boolean asnyc, boolean override);

	void beginProxy(Type type, Type baseType, Type interfaceType);

	void beginSetter(Type classType, Type methodType, int modifier, Type fieldType, String propertyName, String comment, boolean override);

	void endClass();

	void endEnum();

	void endGetter();

	void endInterface();

	void endMethod(Type callbackType);

	void endProxy();

	void endSetter();

	void generate() throws Exception;

	TypeMap getAnnotationMap();

	TypeMap getAnnotationTypeMap();

	Class<? extends Annotation> getEnumAnnotation();

	String getName();

	TypeMap getTypeMap();

	boolean hasEnumSupport();

	Type postProcessType(Type type);

	void setMethodDescription(String description);

	void setSuperclass(Type type, boolean exception);

	void setTypeDescription(String description);

	boolean traverse(ClassDoc classDoc);
}