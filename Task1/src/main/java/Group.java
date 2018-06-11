import java.util.Arrays;

public class Group {
    private String name = "";
    private int N;
    private Person[] group;

    public Group(int N, Person[] group_people, String name) {
        this.N = N;
        group = new Person[N];
        System.arraycopy(group_people, 0,group,0, group_people.length);
        this.name = name;

    }

    public Person[] getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public int getVolumeGroup() {
        return N;
    }

    @Override
    public String toString() {
        return name;
    }
}
