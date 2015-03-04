package test;

/**
 * @java.class
 * @js.class
 * @actionscript.class
 * @cs.class
 */
public class TestIgnoreInClass<T> extends test.TestIgnoreInClassBase<T> {
    @Override
    public T getTest() {
        return super.getTest();
    }

    @Override
    public void testMethod() {
    }
}
