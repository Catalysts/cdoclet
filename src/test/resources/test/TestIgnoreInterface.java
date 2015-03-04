package test;

/**
 * @java.class ignore=TestOverrideInterfaceBase
 * @actionscript.class ignore=TestOverrideInterfaceBase
 * @cs.class ignore=TestOverrideInterfaceBase
 */
public interface TestIgnoreInterface<T> extends test.TestOverrideInterfaceBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	T getTest();

	void testMethod();
}
