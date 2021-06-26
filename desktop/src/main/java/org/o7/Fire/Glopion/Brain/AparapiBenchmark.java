package org.o7.Fire.Glopion.Brain;

import Atom.Time.Time;
import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import org.o7.Fire.Glopion.Brain.AparapiVector;
import org.o7.Fire.Glopion.Brain.JeneticNeuralNetwork;
import org.o7.Fire.Glopion.Brain.NeuralFunction;
import org.o7.Fire.Glopion.Brain.TrainingJeneticData;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static org.o7.Fire.Glopion.Brain.TrainingJeneticData.*;
public class AparapiBenchmark {
    public static void vectorBenchmark(){
        int error = 0;
        int length = 1024 * 1024;
        //Device device = Device.best();
        //System.out.println("Best Device: " + device.getShortDescription());
        int[] a = new int[length], b = new int[length];
        System.out.println("Vector Operation: " + length );
        NeuralFunction.assignRandom(a);
        NeuralFunction.assignRandom(b);
        for (Device d : KernelManager.instance().getDefaultPreferences().getPreferredDevices(null)) {
            Range range;
            System.out.println();
            if (d instanceof OpenCLDevice){
                System.out.println("Device: " + ((OpenCLDevice) d).getName());
                range = d.createRange(length);
            }else{
                System.out.println("Device: " + d.getShortDescription());
                range = d.createRange(length);
            }
            
            System.out.println("Work Item: " + Arrays.toString(d.getMaxWorkItemSize()));
            ArrayList<Time> measurement = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                AparapiVector vector = new AparapiVector(a, b);
                Time time = new Time(TimeUnit.MILLISECONDS);
                vector.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
                vector.execute(range);
                measurement.add(time.elapsed());
            }
        
            AparapiVector vector = new AparapiVector(a, b);
            Time time = new Time(TimeUnit.MILLISECONDS);
            vector.execute(range);
            System.out.println("Execution Mode: " + vector.getExecutionMode());
            System.out.println("Execution Target: " + vector.getTargetDevice());
            System.out.println("Took: " + time.elapsedS());
            long[] measure = new long[measurement.size()];
            for (int i = 0, measurementSize = measurement.size(); i < measurementSize; i++) {
                Time m = measurement.get(i);
                measure[i] = m.getSrc();
            }
            System.out.println("Average: " + TrainingJeneticData.avg(measure));
        }
        System.out.println();
        System.out.println("Native Java");
        int[] c = new int[length];
        ArrayList<Time> measurement = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            Time time = new Time(TimeUnit.MILLISECONDS);
            for (int i = 0; i < length; i++) {
                c[i] = AparapiVector.doShit(a[i], b[i]);
            }
            measurement.add(time.elapsed());
        }
    
        long[] measure = new long[measurement.size()];
        for (int i = 0, measurementSize = measurement.size(); i < measurementSize; i++) {
            Time m = measurement.get(i);
            measure[i] = m.getSrc();
        }
        Time time = new Time(TimeUnit.MILLISECONDS);
        for (int i = 0; i < length; i++) {
            c[i] = AparapiVector.doShit(a[i], b[i]);
        }
        System.out.println("Took: " + time.elapsedS());
        System.out.println("Average: " + TrainingJeneticData.avg(measure));
    
    }
    
    public static void nueuralNetworkBenchmark(){
    
        
        
    }
    public static void main(String[] args) {
        
        vectorBenchmark();
        nueuralNetworkBenchmark();
       
      
    }
}
