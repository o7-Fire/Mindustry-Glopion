package org.o7.Fire.Glopion.Experimental;


import Atom.Utility.Pool;
import arc.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class WebhookStandalone {
    public static class Content{
        public String content = "This", username = "None", avatar_url;
    }
    private static final ExecutorService staticExecutorService = Pool.service;
    public URL realUrl = null;
    public String url;
    public ExecutorService executorService = staticExecutorService;
    public Content content = new Content();
    public long flushInterval = 4000;
    protected PrintStream printStream = null;
    static Gson gson = new GsonBuilder().create();
    public WebhookStandalone(String url) throws MalformedURLException {
        this(new URL(url));
        
    }
    
    public WebhookStandalone(URL url) {
        realUrl = url;
        this.url = url.toExternalForm();
    }
    
    public static void main(String[] args) throws MalformedURLException, InterruptedException, ExecutionException {
        WebhookStandalone webhookStandalone = new WebhookStandalone(args[0]);
        webhookStandalone.content.username = "TEst";
        System.setOut(webhookStandalone.asPrintStream());
        System.setErr(webhookStandalone.asPrintStream());
        for (int i = 0; i < 10; i++) {
            System.out.println("Kys");
        }
        try {
            throw new NullPointerException("Test");
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        Thread.sleep(4000);
        System.out.println("Kys");
    }
    
    public void post(String dat) throws IOException {
        if(dat.length() == 0)throw new IllegalArgumentException("EMPTY");
        // build connection
        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
        // set request properties
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        // enable output and input
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        String doAnother = "";
        if (dat.length() > 1920){
            doAnother = dat.substring(1900);
            dat = dat.substring(0, 1900);
        }
        dat = "```java\n" + dat + "\n```";
        content.content = dat;
        String finalJson = gson.toJson(content);
        conn.getOutputStream().write(finalJson.getBytes());
        conn.getOutputStream().flush();
        conn.getOutputStream().close();
        try {
            conn.getInputStream().close();
        }catch(IOException t){
            if(t.getMessage().contains("code: 400")){
                Log.warn("Malformed JSON, see std out");
                System.out.println(finalJson);
            }
        }
        conn.disconnect();
        if (!doAnother.isEmpty()){
            post(doAnother);
        }
        
    }
    
    public PrintStream asPrintStream() {
        long lastFlush = System.currentTimeMillis();
        final long[] nextFlush = {lastFlush + flushInterval};
        if (printStream == null) printStream = new PrintStream(new OutputStream() {
            StringBuilder sb = new StringBuilder();
            
            @Override
            public void write(int b) throws IOException {
                sb.append((char) b);
            }
            
            @Override
            public void flush() throws IOException {
                if (nextFlush[0] > System.currentTimeMillis()) return;
                String finalS = sb.toString();
                Runnable r = ()-> {
                    try {
                        post(finalS);
                    }catch(IOException t){
                        if(System.err != asPrintStream())
                            t.printStackTrace();
                     
                    }
                };
                executorService.submit(r);
                sb = new StringBuilder();
                nextFlush[0] = System.currentTimeMillis() + flushInterval;
            }
        }, true);
        return printStream;
    }
    
    public static class JSONObject {
        
        private final HashMap<String, Object> map = new HashMap<>();
        
        public void put(String key, Object value) {
            if (value != null){
                map.put(key, value);
            }
        }
        
        public String javaStringLiteral(String str) {
            StringBuilder sb = new StringBuilder("\"");
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '\n'){
                    sb.append("\\n");
                }else if (c == '\r'){
                    sb.append("\\r");
                }else if (c == '"'){
                    sb.append("\\\"");
                }else if (c == '\\'){
                    sb.append("\\\\");
                }else if (c < 0x20){
                    sb.append(String.format("\\%03o", (int) c));
                }else if (c >= 0x80){
                    sb.append(String.format("\\u%04x", (int) c));
                }else{
                    sb.append(c);
                }
            }
            sb.append("\"");
            return sb.toString();
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");
            
            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");
                
                if (val instanceof String){
                    builder.append(javaStringLiteral((String) val));
                }else if (val instanceof Integer){
                    builder.append(Integer.valueOf(String.valueOf(val)));
                }else if (val instanceof Boolean){
                    builder.append(val);
                }else if (val instanceof JSONObject){
                    builder.append(val.toString());
                }else if (val.getClass().isArray()){
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }
                
                builder.append(++i == entrySet.size() ? "}" : ",");
            }
            
            return builder.toString();
        }
        
        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}
