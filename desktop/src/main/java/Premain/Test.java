package Premain;

import java.io.IOException;
import java.net.URL;

public class Test {
    public static void main(String[] args) throws IOException {
        URL u = new URL("https://github.com/o7-Fire/Mindustry-Glopion/raw/main/build.gradle");
        u.getContent();
        System.out.println(u.getContent());
    }
}
