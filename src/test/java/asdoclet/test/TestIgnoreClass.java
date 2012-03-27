package asdoclet.test;

/**
 * @actionscript.class ignore=asdoclet.test.TestOverrideClassBase
 * @cs.class ignore=asdoclet.test.TestOverrideClassBase
 */
public class TestIgnoreClass<T> extends TestOverrideClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}
