package org.o7.Fire.Glopion.Bootstrapper;

import javax.swing.*;
import java.awt.*;

public class Java {
    public static boolean headless = GraphicsEnvironment.isHeadless();
    public static void main(String[] args) {
        System.out.println("Headless: " + headless);
        if(!headless){
            JOptionPane.showMessageDialog(null, "brb downloading, kill this quickly if you don't want", "Downloading", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    
    
}
