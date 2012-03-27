package asdoclet.test;

/**
 * @actionscript.class ignore=asdoclet.test.TestOverrideInterfaceBase
 * @cs.class ignore=asdoclet.test.TestOverrideInterfaceBase
 */
public interface TestIgnoreInterface<T> extends TestOverrideInterfaceBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	T getTest();

	void testMethod();
}
