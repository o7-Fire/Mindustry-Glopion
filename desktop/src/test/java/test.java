import com.google.gson.Gson;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;

public class test {
    public static void main(String[] args) throws Throwable{
        File f = new File("cache/Record-1").getAbsoluteFile();
        if(!f.exists())return;
        File[] records = f.listFiles((dir, name) -> name.startsWith("Record-") && name.endsWith(".json"));
        Arrays.sort(records, Comparator.comparing(File::getName));
        Gson gson = new Gson();
        MachineRecorder machineRecorder = gson.fromJson(new FileReader(records[0]),MachineRecorder.class);
        System.out.println(records[0].getName());
        
    }
}
