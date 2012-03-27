package asdoclet.test;

import java.util.Collection;
import java.util.Date;

/**
 * @actionscript.class
 * @cs.class
 */
public interface TestProxyInterfaceBase<T extends Date> extends TestProxyInterfaceGeneric {
	Collection<T> foo(T bar);
}
