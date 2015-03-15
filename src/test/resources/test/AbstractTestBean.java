package test;

/**
 * @cs.class
 * @java.class
 */
public abstract class AbstractTestBean {

    private int value;
    private String name;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestBean getSelf() {
        return this;
    }
}
