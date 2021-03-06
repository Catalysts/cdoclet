package test;

import java.sql.Date;

/**
 * @java.class proxy=<code>true</code>
 * @js.class proxy=<code>true</code> verb=get
 * @actionscript.class proxy=<code>true</code>
 * @cs.class proxy=<code>true</code> async=<code>true</code> async.callbacktype=AsyncCallback&lt;$type$&gt;
 */
public interface TestProxyInterface extends test.TestProxyInterfaceBase<Date> {
    String NAME = "test";

    /**
     * @js.method verb=POST
     */
    TestBean baz(long ago);
}
