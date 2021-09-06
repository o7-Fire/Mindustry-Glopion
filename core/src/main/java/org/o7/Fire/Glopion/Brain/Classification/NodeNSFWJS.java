package org.o7.Fire.Glopion.Brain.Classification;

import Atom.Encoding.Encoder;
import Atom.Encoding.EncoderJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public class NodeNSFWJS implements ImageClassifier {
    protected final String mainURL;
    protected final URL blobURL;
    protected final String auth;
    
    public NodeNSFWJS(URL api) throws MalformedURLException {
        this(api, null);
    }
    
    public NodeNSFWJS(URL api, String auth) throws MalformedURLException {
        mainURL = api.toString().endsWith("/") ? api.toString() : api.toString() + "/";
        blobURL = new URL(mainURL + "api/json/graphical/classification");
        this.auth = auth;
    }
    
    public String post(byte[] postData) throws IOException {
        URL realUrl = blobURL;
        // build connection
        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
        // set request properties
        conn.setRequestProperty("charset", Charset.defaultCharset().name());
        conn.setRequestProperty("connection", "Keep-Alive");
        // enable output and input
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        
        conn.getOutputStream().write(postData);
        conn.getOutputStream().flush();
        byte[] b = Encoder.readAllBytes(conn.getInputStream());
        return new String(b);
    }
    
    @Override
    public ClassificationResult classify(Image image) throws IOException {
        String resp = post(image.getData());
        JsonElement element = JsonParser.parseString(resp);
        Map<String, String> h = EncoderJson.jsonToMap(element);
        if (h.containsKey("err")) throw new RuntimeException(h.get("err"));
        return new NodeNSFWJSResult(resp);
    }
}
