package test;

/**
 * @java.class
 * @actionscript.class
 * @cs.class
 */
public interface TestOverrideInterface<T> extends test.TestOverrideInterfaceBase<T> {
    public T getTest();

    void testMethod();
}
