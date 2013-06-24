package cdoclet.test;

import java.util.Date;

/**
 * @java.class
 * @actionscript.class async=<code>false</code>
 * @cs.class async=<code>false</code>
 */
public interface TestInterfaceGeneric<A extends Date, B> {
	void foo(A bar, B baz);
}
