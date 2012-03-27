package cc.catalysts.asdoclet.handler;

import java.lang.reflect.InvocationTargetException;

import com.sun.javadoc.ClassDoc;

public interface Handler {
	void process(ClassDoc classDoc) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException;
}
