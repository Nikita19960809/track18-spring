import java.util.*;

public class FindSimilarity {

    private HashMap<String, Integer> musical_groups;
    private ArrayList<Group> people;
    private HashMap<String, Double> results = new HashMap<>();
    private static final int COEFF = 10000000;

    /**
     * Constructor of class
     *
     * @param  mus_groups map with peformers and number of fans
     * @param  new_people list with groups of people {@link Group}
     *
     */
    public FindSimilarity(HashMap<String, Integer> mus_groups,
                          ArrayList<Group> new_people) {
        musical_groups = new HashMap<String, Integer>(mus_groups);
        people = new ArrayList<>(new_people);

    }

    public void addPerformer(String performer, int num_fans) {
        musical_groups.put(performer, num_fans);
    }

    public void addGroup(Group A) {
        people.add(A);
    }

    public HashMap<String, Integer> getMusical_groups() {
        return musical_groups;
    }

    public ArrayList<Group> getPeople() {
        return people;
    }

    public HashMap<String, Double> getResults() {
        return results;
    }

    /**Method find Jaccard index of people from group and number of fans
     *
     * @param performer name of musical group
     * @param A group of people
     *
     * @return Jaccard index - double
     */
    private double findListeners(String performer, Group A) {
        int N_A = A.getVolumeGroup();
        int countSim = 0;

        for (int i = 0; i < N_A; i++) {
            Person person = A.getGroup()[i];
            String[] performers_person = person.getPerformers();

            for (String namePerformer : performers_person) {
                if (namePerformer.equals(performer)) {
                    countSim++;break;}
            }

        }

        return (double)COEFF*countSim / (musical_groups.get(performer) + N_A - countSim);
    }

    /** Method finds overall similarity
     * <p>
     * Method finds similarity between certain group of people
     * and musical bands from hashmap
     *
     * @param A group of people
     *
     * @return median of all calculated values
     */
    private double findSimilarity(Group A) {

        ArrayList<Double> vector = new ArrayList<Double>();

        for (Map.Entry entry : musical_groups.entrySet()) {
              vector.add(findListeners((String) entry.getKey(), A));
        }

        for (double l: vector) {
            System.out.println(l);
        }

        double median = findMedian(vector);
        results.put(A.toString(),median);

        return median;
    }

    /**calculate median in list
     *
     * @param list = Arraylist of values
     * @return median
     */
    private static double findMedian(ArrayList<Double> list) {
        Collections.sort(list);
        return list.get(list.size()/2);

        }

    /**calculate median in list
     *
     * @param A = first group of people {@link Group}
     * @param B = second group of people {@link Group}
     *
     * @return average value of medians, calculated in {@link #findSimilarity(Group)}}
     */
    public double findTwoGroupsSimilarity(Group A, Group B){

        return ((findSimilarity(A) + findSimilarity(B))/2);

        }


}

