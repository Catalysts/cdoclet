package cdoclet.test;

import java.util.Collection;
import java.util.Date;

/**
 * @java.class
 * @actionscript.class
 * @cs.class
 */
public interface TestProxyInterfaceBase<T extends Date> extends TestProxyInterfaceGeneric {
	Collection<T> foo(T bar);
}
