public class Main {
    public static void main(String[] args) throws Throwable {
        
        int i = Runtime.getRuntime().exec("curl -I http://google.com | grep Content-Length").waitFor();
        System.out.println("main");
        System.out.println("H");
        System.out.println(i);
        i = Runtime.getRuntime().exec("curl -I http://google.com | grep Content-Length").waitFor();
        System.out.println(i);
    }
}
