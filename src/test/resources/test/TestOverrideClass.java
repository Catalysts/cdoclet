package test;

/**
 * @java.class
 * @actionscript.class
 * @cs.class
 */
public class TestOverrideClass<T> extends expectations.java.test.TestOverrideClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}
