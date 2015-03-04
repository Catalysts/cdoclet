package test;

/**
 * @java.class ignore=TestOverrideClassBase
 * @js.class ignore=TestOverrideClassBase
 * @actionscript.class ignore=TestOverrideClassBase
 * @cs.class ignore=TestOverrideClassBase
 */
public class TestIgnoreClass<T> extends test.TestOverrideClassBase<T> {
    @Override
    public T getTest() {
        return super.getTest();
    }

    @Override
    public void testMethod() {
    }
}
