package org.o7.Fire.Glopion.Trash;

public class ErrorCatch {
    public static void main(String[] args) {
        try {
            Class.forName("org.o7.Fire.Glopion.GlopionDesktop");
        }catch(ClassNotFoundException e){
            System.out.println("Gay");
        }
    }
}
