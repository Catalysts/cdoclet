package test;

import java.util.Collection;
import java.util.Date;

/**
 * @java.class
 * @js.class
 * @actionscript.class
 * @cs.class
 */
public interface TestProxyInterfaceBase<T extends Date> extends test.TestProxyInterfaceGeneric {
    Collection<T> foo(T bar);
}
