package test;

/**
 * @cs.class
 * @java.class
 */
public class TästBean {

    private int välue;
    private String näme;

    public int getVälue() {
        return välue;
    }

    public void setVälue(int välue) {
        this.välue = välue;
    }

    public String getNäme() {
        return näme;
    }

    public void setNäme(String näme) {
        this.näme = näme;
    }

    public TestBean getSelf() {
        return this;
    }
}
