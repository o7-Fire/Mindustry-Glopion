package org.o7.Fire.Glopion.Trash;

import Atom.Time.Time;
import com.aparapi.Kernel;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import org.o7.Fire.Glopion.Brain.NeuralFunction;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Aparapi extends Kernel{
    public static int x = 5, y = 5;
    public static final int[][] matrix= new int[x][y], output = new int[x][y];
    
    @Override
    public void run() {
        int x = getGlobalId(0);
        int y = getGlobalId(1);
        output[x][y] = matrix[x][y] * matrix[x][y] * matrix[x][y];
    }
    
    public static void main(String[] args) {
        
        for (int[] vector: matrix){
            NeuralFunction.assignRandom(vector);
        }
        Time time = new Time(TimeUnit.MILLISECONDS);
        for (int[] vector : matrix)
            System.out.println(Arrays.toString(vector));
        System.out.println();
        
        Kernel kernel = new Aparapi();
        Device device = KernelManager.instance().bestDevice();
        kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
        kernel.execute(device.createRange2D(x,y));
        
        for (int[] vector : output)
            System.out.println(Arrays.toString(vector));
        System.out.println(kernel.getTargetDevice());
        System.out.println(time.elapsedS());
    }
}
