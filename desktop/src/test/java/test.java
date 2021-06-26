import Atom.Reflect.UnThread;
import Atom.Time.Time;
import Atom.Utility.Meth;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import arc.math.Mathf;
import com.google.gson.Gson;
import org.o7.Fire.Glopion.Brain.TrainingJeneticData;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class test {
    public static final ArrayList<Time> measurement = new ArrayList<>();
    public static void main(String[] args) throws Throwable{
        Time time = new Time(TimeUnit.MILLISECONDS);
        Runnable r = ()->{
            Time times = new Time(TimeUnit.MILLISECONDS);
            UnThread.sleep(Random.getInt(100,1000));
            measurement.add(times.elapsed());
        };
        ArrayList<Future> futures = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            futures.add(Pool.submit(r));
        }
        while (!futures.isEmpty()){
            futures.remove(0).get();
        }
        File f = new File("cache/Record-1").getAbsoluteFile();
        if(!f.exists())return;
        File[] records = f.listFiles((dir, name) -> name.startsWith("Record-") && name.endsWith(".json"));
        Arrays.sort(records, Comparator.comparing(File::getName));
        Gson gson = new Gson();
        MachineRecorder machineRecorder = gson.fromJson(new FileReader(records[0]),MachineRecorder.class);
        System.out.println(records[0].getName());
        System.out.println(time.elapsedS());
        long[] measure = new long[measurement.size()];
        for (int i = 0, measurementSize = measurement.size(); i < measurementSize; i++) {
            Time m = measurement.get(i);
            measure[i] = m.getSrc();
        }
        System.out.println("Size: " + measure.length);
        System.out.println("Average: " + TrainingJeneticData.avg(measure));
    }
}
