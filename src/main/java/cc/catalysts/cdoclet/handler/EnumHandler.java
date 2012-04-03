package cc.catalysts.cdoclet.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.catalysts.cdoclet.CDoclet;
import cc.catalysts.cdoclet.generator.Generator;
import cc.catalysts.cdoclet.generator.Type;
import cc.catalysts.cdoclet.generator.utils.GeneratorUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;

public class EnumHandler extends AbstractHandler {
	private final Logger logger = LoggerFactory.getLogger(EnumHandler.class);

	public EnumHandler(Generator generator) {
		super(generator);
	}

// --------------------- Interface Handler ---------------------

	public void process(ClassDoc classDoc) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
		Type type = GeneratorUtils.getType(classDoc.qualifiedName(), getGenerator());

		getGenerator().beginEnum(type);
		processClassComment(classDoc);
		processEnumConstants(type, classDoc);

		getGenerator().endEnum();
	}

	private void processEnumConstants(Type type, ClassDoc classDoc) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
		Method getter = findGetter(classDoc);
		Type valueType = GeneratorUtils.getType(classDoc, getGenerator(), new HashSet<String>());

		for (FieldDoc fieldDoc : classDoc.enumConstants()) {
			Object value = null;

			if (getter != null) {
				@SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
				Enum<?> enuum = Enum.valueOf((Class<Enum>) getter.getDeclaringClass(), fieldDoc.name());
				value = getter.invoke(enuum);
			}

			getGenerator().addEnumField(type, valueType, fieldDoc.name(), value, fieldDoc.commentText());
		}
	}

	private Method findGetter(ClassDoc classDoc) throws ClassNotFoundException {
		Class<? extends Annotation> annotation = getGenerator().getEnumAnnotation();

		Method getter = null;
		if (annotation != null) {
			try {
				Class<?> clazz = Class.forName(classDoc.qualifiedName());

				boolean present = false;
				for (Method method : clazz.getMethods()) {
					if (method.isAnnotationPresent(annotation)) {
						if (present) {
							throw new RuntimeException("enumeration " + classDoc + " has multiple annotations");
						}

						getter = method;
						present = true;
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return getter;
	}
}
