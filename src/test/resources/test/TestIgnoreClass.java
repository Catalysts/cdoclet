package test;

/**
 * @java.class ignore=TestOverrideClassBase
 * @actionscript.class ignore=TestOverrideClassBase
 * @cs.class ignore=TestOverrideClassBase
 */
public class TestIgnoreClass<T> extends expectations.java.test.TestOverrideClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}
