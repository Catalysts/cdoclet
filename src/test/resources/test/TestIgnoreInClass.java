package test;

/**
 * @java.class
 * @actionscript.class
 * @cs.class
 */
public class TestIgnoreInClass<T> extends expectations.java.cdoclet.test.TestIgnoreInClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}
