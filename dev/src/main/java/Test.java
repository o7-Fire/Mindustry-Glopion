import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> r = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        HashSet<Integer> h = new HashSet<>();
        while (!r.isEmpty()) {
            int i = r.remove(0);
            h.add(i);
            
            System.out.println(i);
            if (i == 7) r.add(0, 2000000000);
        }
        System.out.println(h);
    }
}
