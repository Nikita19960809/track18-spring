
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeMath.round;

public class FindSimilarityTest {

    @Test
    public void listAdd() throws Exception {
        String[] s = {"Би-2","Oxxymiron","Ария","Hammerfall"};
        Person A = new Person(4,s);

        String[] s1 = {"Oxxymiron","Metallica","Агата Кристи"};
        Person B = new Person(3,s1);

        Person[] people = {A,B};

        Group groupA = new Group (2,people,"A");

        String[] s2 = {"Ария","Hammerfall","Агата Кристи"};
        Person A1 = new Person(3,s2);

        String[] s3 = {"Oxxymiron","Metallica","Агата Кристи"};
        Person B1 = new Person(3,s3);

        Person[] people1 = {A1,B1};

        Group groupB = new Group (2,people1,"B");

        ArrayList<Group> list = new ArrayList<>();
        list.add(groupA);
        list.add(groupB);

        HashMap<String, Integer> musGroups = new HashMap<>();
        musGroups.put("Би-2",2000000);
        musGroups.put("Hammerfall",100000);
        musGroups.put("Ария",20000000);
        musGroups.put("Oxxymiron",10000);
        musGroups.put("Агата Кристи",50000);
        musGroups.put("Metallica",5000000);

        FindSimilarity f = new FindSimilarity(musGroups,list);

        HashMap<String, Integer> map = f.getMusical_groups();

        Assert.assertEquals(100,f.findTwoGroupsSimilarity(groupA,groupB),0.01);



    }


}
