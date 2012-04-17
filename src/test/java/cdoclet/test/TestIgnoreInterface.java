package cdoclet.test;

/**
 * @java.class ignore=cdoclet.test.TestOverrideInterfaceBase
 * @as.class ignore=cdoclet.test.TestOverrideInterfaceBase
 * @cs.class ignore=cdoclet.test.TestOverrideInterfaceBase
 */
public interface TestIgnoreInterface<T> extends TestOverrideInterfaceBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	T getTest();

	void testMethod();
}
