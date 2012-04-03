package cc.catalysts.cdoclet.handler;

import java.lang.reflect.InvocationTargetException;

import com.sun.javadoc.ClassDoc;

public interface Handler {
	void process(ClassDoc classDoc) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException;
}
