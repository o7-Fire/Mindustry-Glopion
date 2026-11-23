package org.o7.Fire.Glopion.Trash;

import Atom.Utility.Random;

public class training {
    public static int random() {
        int i = Random.getInt(100);
        System.out.println("Invoke random: " + i);
        return i;
    }
    
    public static void main(String[] args) {
        for (int i = 0; i < random(); i++) {
            System.out.println(i);
        }
    }
}
