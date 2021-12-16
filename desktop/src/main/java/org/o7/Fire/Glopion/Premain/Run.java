package org.o7.Fire.Glopion.Premain;

public class Run {
    //run configuration
    //Classpath: Mindustry-Glopion.desktop.test
    //Class org.o7.Fire.Glopion.Premain.Run
    //JVM: 16
    public static void main(String[] args) throws Throwable {
        if (System.getProperty("glopion-deepPatch") == null){
            System.setProperty("glopion-deepPatch", "1");
            System.setProperty("dev", "1");//let's assume you use Intellij Run Button
            System.out.println("DEV ?");
        }
        MindustryLauncher.main(args);
    }
    
    public static class Server {
        //run configuration
        //Classpath: Mindustry-Glopion.desktop.test
        //Class org.o7.Fire.Glopion.Premain.Run$Server
        //JVM: 16
        public static void main(String[] args) throws Throwable {
            if (System.getProperty("glopion-deepPatch") == null){
                System.setProperty("glopion-deepPatch", "1");
                System.setProperty("dev", "1");//let's assume you use Intellij Run Button
                System.out.println("DEV ?");
                if (args.length == 0) {
                    args = new String[]{"host", "Ancient_Caldera", "sandbox"};
                }
            }
            ServerLauncher.main(args);
        }
    }
}
