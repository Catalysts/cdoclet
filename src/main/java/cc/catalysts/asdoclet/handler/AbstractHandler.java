package cc.catalysts.asdoclet.handler;

import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


import cc.catalysts.asdoclet.generator.Generator;
import cc.catalysts.asdoclet.generator.utils.GeneratorUtils;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public abstract class AbstractHandler implements Handler {
	private static final String COMMENT_PREFIX = "\t * ";

	private final Generator generator;

	protected AbstractHandler(Generator generator) {
		this.generator = generator;
	}

	protected Generator getGenerator() {
		return generator;
	}

	protected String getBeanPropertyName(String name) {
		String value = null;

		if (name.startsWith("set")) {
			value = Introspector.decapitalize(name.substring(3));
		} else if (name.startsWith("get")) {
			value = Introspector.decapitalize(name.substring(3));
		} else if (name.startsWith("is")) {
			value = Introspector.decapitalize(name.substring(2));
		}

		return value;
	}

	protected void processBeanProperty(ClassDoc classDoc, cc.catalysts.asdoclet.generator.Type classType, MethodDoc methodDoc, Set<String> ignore, Map<String, String> commands) {
		Map<String, String> propertyCommands = new HashMap<String, String>(commands);
		Map<String, String> propertyOnlyCommands = new HashMap<String, String>();

		TagParser.processTags(methodDoc.tags(getGenerator().getName() + Constants.TAG_PROPERTY), propertyOnlyCommands);
		propertyCommands.putAll(propertyOnlyCommands);

		if (!TagParser.getBooleanCommand(Constants.COMMAND_IGNORE, propertyCommands)) {
			String propertyName = getBeanPropertyName(methodDoc);
			if (propertyName != null) {
				String overriddenType = TagParser.getStringCommand(Constants.COMMAND_TYPE, propertyCommands);

				cc.catalysts.asdoclet.generator.Type fieldType = getFieldType(methodDoc, findField(classDoc, propertyName), overriddenType);
				cc.catalysts.asdoclet.generator.Type methodType = cc.catalysts.asdoclet.generator.Type.EMPTY;

				if (fieldType == null) {
					fieldType = GeneratorUtils.getType(methodDoc.returnType(), getGenerator(), ignore);
					methodType = GeneratorUtils.getType(methodDoc, getGenerator(), ignore);
				}

				String name = methodDoc.name();
				String comment = methodDoc.commentText();

				if (fieldType != cc.catalysts.asdoclet.generator.Type.NULL && (name.startsWith("get") || name.startsWith("is"))) {
					String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

					if (classDoc.isInterface()) {
						getGenerator().beginGetter(classType, methodType, 0, fieldType, propertyName, comment, false);
						processAnnotations(methodDoc.annotations());
						processAnnotationCommands(propertyOnlyCommands);
						getGenerator().endGetter();

						if (findMethod(setterName, classDoc)) {
							getGenerator().beginSetter(classType, methodType, 0, fieldType, propertyName, comment, false);
							processAnnotations(methodDoc.annotations());
							processAnnotationCommands(propertyOnlyCommands);
							getGenerator().endSetter();
						}
					} else {
						boolean overridden = isOverridden(classDoc, name, ignore);

						getGenerator().beginGetter(classType, methodType, Modifier.PUBLIC, fieldType, propertyName, comment, overridden);
						processAnnotations(methodDoc.annotations());
						processAnnotationCommands(propertyOnlyCommands);
						getGenerator().endGetter();

						// always generate setter, serialization doesn't work otherwise
						getGenerator().beginSetter(classType, methodType, Modifier.PUBLIC, fieldType, propertyName, comment, overridden);
						processAnnotations(methodDoc.annotations());
						processAnnotationCommands(propertyOnlyCommands);
						getGenerator().endSetter();
					}
				}
			}
		}
	}

	private String getBeanPropertyName(MethodDoc methodDoc) {
		String propertyName = null;

		if (methodDoc.parameters().length == 0) {
			propertyName = getBeanPropertyName(methodDoc.name());
		}

		return propertyName;
	}

	private cc.catalysts.asdoclet.generator.Type getFieldType(MethodDoc methodDoc, FieldDoc fieldDoc, String overriddenType) {
		cc.catalysts.asdoclet.generator.Type fieldType;

		if (overriddenType == null) {
			fieldType = checkAnnotations(methodDoc.annotations());

			if (fieldType == null && fieldDoc != null) {
				fieldType = checkAnnotations(fieldDoc.annotations());
			}
		} else {
			fieldType = GeneratorUtils.getType(overriddenType, getGenerator());
		}

		return fieldType;
	}

	private cc.catalysts.asdoclet.generator.Type checkAnnotations(AnnotationDesc[] annotationDescs) {
		cc.catalysts.asdoclet.generator.Type fieldType = null;

		for (AnnotationDesc annotationDesc : annotationDescs) {
			AnnotationTypeDoc annotationTypeDoc = annotationDesc.annotationType();
			fieldType = GeneratorUtils.getAnnotationType(annotationTypeDoc.qualifiedTypeName(), getGenerator());
			if (fieldType != null) break;
		}

		return fieldType;
	}

	private FieldDoc findField(ClassDoc classDoc, String propertyName) {
		FieldDoc fieldDoc = null;
		for (FieldDoc doc : classDoc.fields(false)) {
			if (doc.name().equals(propertyName)) {
				fieldDoc = doc;
				break;
			}
		}

		return fieldDoc;
	}

	private boolean findMethod(String name, ClassDoc superClass) {
		for (MethodDoc superMethod : superClass.methods()) {
			if (name.equals(superMethod.name())) {
				return true;
			}
		}
		return false;
	}

	protected boolean isOverridden(ClassDoc classDoc, String name, Set<String> ignore) {
		if (classDoc.isInterface()) {
			for (ClassDoc interfaceDoc : classDoc.interfaces()) {
				cc.catalysts.asdoclet.generator.Type interfaceClass = GeneratorUtils.getType(interfaceDoc, getGenerator(), ignore);

				if (interfaceClass == cc.catalysts.asdoclet.generator.Type.NULL) return false;
				if (ignore.contains(interfaceDoc.qualifiedTypeName() + "." + name)) return false;
				if (ignore.contains(interfaceDoc.qualifiedTypeName())) return false;
				if (findMethod(name, interfaceDoc)) return true;

				Map<String, String> commands = TagParser.processClassTags(getGenerator(), interfaceDoc);
				Set<String> interfaceIgnore = getIgnore(commands);
				interfaceIgnore.addAll(ignore);

				if (isOverridden(interfaceDoc, name, interfaceIgnore)) return true;
			}
		} else {
			for (ClassDoc superClassDoc = classDoc.superclass(); superClassDoc != null; superClassDoc = superClassDoc.superclass()) {
				cc.catalysts.asdoclet.generator.Type superClass = GeneratorUtils.getType(superClassDoc, getGenerator(), ignore);

				if (superClass == cc.catalysts.asdoclet.generator.Type.NULL) return false;
				if (ignore.contains(superClassDoc.qualifiedTypeName() + "." + name)) return false;
				if (ignore.contains(superClassDoc.qualifiedTypeName())) return false;
				if (findMethod(name, superClassDoc)) return true;

				Map<String, String> commands = TagParser.processClassTags(getGenerator(), superClassDoc);
				ignore.addAll(getIgnore(commands));
			}
		}
		return false;
	}

	protected Set<String> getIgnore(Map<String, String> commands) {
		Set<String> ignore = new HashSet<String>();
		StringTokenizer tokenizer = new StringTokenizer(TagParser.getStringCommand(Constants.COMMAND_IGNORE, "", commands), ",;");
		while (tokenizer.hasMoreTokens()) ignore.add(tokenizer.nextToken());
		return ignore;
	}

	protected void processAnnotations(AnnotationDesc[] annotationDescs) {
		for (AnnotationDesc annotationDesc : annotationDescs) {
			cc.catalysts.asdoclet.generator.Type annotation = GeneratorUtils.getAnnotation(annotationDesc.annotationType().qualifiedTypeName(), getGenerator());
			if (annotation != null && annotation != cc.catalysts.asdoclet.generator.Type.NULL) getGenerator().addAnnotation(annotation);
		}
	}

	protected void processAnnotationCommands(Map<String, String> commands) {
		if (commands.containsKey(Constants.COMMAND_ANNOTATION)) {
			StringTokenizer tokenizer = new StringTokenizer(commands.get(Constants.COMMAND_ANNOTATION), ",");
			while (tokenizer.hasMoreTokens()) {
				getGenerator().addAnnotation(GeneratorUtils.getType(tokenizer.nextToken(), getGenerator()));
			}
		}
	}

	protected void processClassComment(ClassDoc classDoc) {
		StringBuilder description = new StringBuilder();
		String comment = classDoc.commentText();

		if (comment != null) description.append(comment.trim());

		if (description.length() > 0) {
			description.append((char) Character.LINE_SEPARATOR).append(COMMENT_PREFIX);
			description.append((char) Character.LINE_SEPARATOR).append(COMMENT_PREFIX);
		}

		description.append("Generated by AsDoclet from ").append(classDoc.qualifiedTypeName()).append('.').append((char) Character.LINE_SEPARATOR);
		description.append(COMMENT_PREFIX).append("Do not edit.");

		getGenerator().setTypeDescription(description.toString());
	}

	protected void processInterfaces(ClassDoc classDoc, Set<String> ignore) {
		for (Type type : classDoc.interfaceTypes()) {
			cc.catalysts.asdoclet.generator.Type interfaceName = GeneratorUtils.getType(type, getGenerator(), ignore);

			if (interfaceName != cc.catalysts.asdoclet.generator.Type.NULL && !ignore.contains(interfaceName.getName())) {
				getGenerator().addInterface(interfaceName);
			}
		}
	}
}
