public class Person {

    private int N;
    private String[] performers;

    public Person(int N, String[] performers_new) {
        this.N = N;
        performers = new String[N];
        System.arraycopy(performers_new,0,performers,0,performers_new.length);
    }

    public String[] getPerformers() {
        return performers;
    }

    public int getAmountPerformers() {
        return N;
    }
}
