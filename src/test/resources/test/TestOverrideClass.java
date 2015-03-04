package test;

/**
 * @java.class
 * @js.class
 * @actionscript.class
 * @cs.class
 */
public class TestOverrideClass<T> extends test.TestOverrideClassBase<T> {
    @Override
    public T getTest() {
        return super.getTest();
    }

    @Override
    public void testMethod() {
    }
}
