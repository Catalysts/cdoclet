package cc.catalysts.asdoclet.handler;

import java.util.Map;
import java.util.Set;
import java.util.Collection;


import cc.catalysts.asdoclet.generator.Generator;
import cc.catalysts.asdoclet.generator.utils.GeneratorUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public class ClassHandler extends AbstractHandler {
	public ClassHandler(Generator generator) {
		super(generator);
	}

// --------------------- Interface Handler ---------------------

	public void process(ClassDoc classDoc) {
		Map<String, String> commands = TagParser.processClassTags(getGenerator(), classDoc);

		Set<String> ignore = getIgnore(commands);

		cc.catalysts.asdoclet.generator.Type type = GeneratorUtils.getType(classDoc, getGenerator(), ignore);

		getGenerator().beginClass(type);

		processAnnotations(classDoc.annotations());
		processAnnotationCommands(commands);
		processSuperClass(classDoc, ignore);
		processInterfaces(classDoc, ignore);
		processClassComment(classDoc);
		processClassConstants(type, classDoc, ignore);
		processBeanProperties(type, classDoc, ignore, commands);

		getGenerator().endClass();
	}

	private void processBeanProperties(cc.catalysts.asdoclet.generator.Type type, ClassDoc classDoc, Set<String> ignore, Map<String, String> commands) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			processBeanProperty(classDoc, type, methodDoc, ignore, commands);
		}
	}

	private void processClassConstants(cc.catalysts.asdoclet.generator.Type classType, ClassDoc classDoc, Collection<String> ignore) {
		for (FieldDoc fieldDoc : classDoc.fields()) {
			if (fieldDoc.isFinal() && fieldDoc.isPublic()) {
				cc.catalysts.asdoclet.generator.Type type = GeneratorUtils.getType(fieldDoc.type(), getGenerator(), ignore);
				getGenerator().addConstant(classType, type, fieldDoc.name(), fieldDoc.constantValueExpression(), fieldDoc.commentText());
			}
		}
	}

	private void processSuperClass(ClassDoc classDoc, Collection<String> ignore) {
		Type type = classDoc.superclassType();
		if ((type != null) && !type.qualifiedTypeName().equals(Object.class.getCanonicalName())) {
			cc.catalysts.asdoclet.generator.Type superClass = GeneratorUtils.getType(type, getGenerator(), ignore);

			if (superClass != cc.catalysts.asdoclet.generator.Type.NULL && !ignore.contains(superClass.getName())) {
				getGenerator().setSuperclass(superClass, isException(classDoc));
			}
		}
	}

	private boolean isException(ClassDoc classDoc) {
		while (classDoc != null) {
			if (Exception.class.getCanonicalName().equals(classDoc.qualifiedTypeName())) return true;
			classDoc = classDoc.superclass();
		}

		return false;
	}
}
