package org.o7.Fire.Glopion.Brain;

import com.aparapi.Kernel;

public class AparapiVector extends Kernel {
    final int[] a,b,c;
    public AparapiVector(int[] a, int[] b){
        if(a.length != b.length)
            throw new IllegalArgumentException("Length Array Not Same");
        this.a = a;
        this.b = b;
        this.c = new int[a.length];
    }
    public int[] getOutput(){
        return c;
    }
    public static int doShit(int a, int b){
        int c = 0;
        for (int j = 0; j < 5; j++) {
            c =  a * b + j;
        }
        return c;
    }
    @Override
    public void run() {
       int i = getGlobalId();
       c[i] = doShit(a[i], b[i]);
       /*
        for (int j = 0; j < 5; j++) {
            c[i] = a[i] * b[i] + j;
        }
        
        */
   
    }
}
