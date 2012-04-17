package cdoclet.test;

/**
 * @java.class
 * @as.class
 * @cs.class
 */
public interface TestIgnoreInInterface<T> extends TestIgnoreInInterfaceBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	T getTest();

	void testMethod();
}
