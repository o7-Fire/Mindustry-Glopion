package org.o7.Fire.Glopion.Brain.Classification;

import Atom.Encoding.EncoderJson;
import Atom.Struct.FunctionalPoolObject;
import arc.struct.Seq;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NodeNSFWJSResult implements ClassificationResult, Serializable {
    public static double threshold = 0.65f;
    public final Seq<Item> arrayList = new Seq<>(6);
    public final HashMap<String, Double> map = new HashMap<>();
    
    public NodeNSFWJSResult(String jason) {
        for (Map.Entry<String, String> s : EncoderJson.jsonToMap(JsonParser.parseString(jason)).entrySet())
            try {
                map.put(s.getKey(), Double.parseDouble(s.getValue()));
                arrayList.add(new Item(Label.valueOf(s.getKey()), Double.parseDouble(s.getValue())));
            }catch(Exception e){}
        arrayList.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Double.compare(o1.value, o2.value);
            }
        });
    }
    
    @Override
    public boolean isNsfw() {
        for (int i = 0; i < arrayList.size; i++) {
            if (arrayList.get(i).label.isNsfw){
                if (arrayList.get(i).value > threshold) return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean is(String s) {
        if (!map.containsKey(s)) return false;
        return map.get(s) > threshold;
    }
    
    public boolean contain(String s) {
        return map.containsKey(s);
    }
    
    @Override
    public double get(String s) {
        if (!map.containsKey(s)) return Double.NaN;
        return map.get(s);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = FunctionalPoolObject.StringBuilder.obtain();
        for (int i = 0; i < arrayList.size; i++) {
            Item item = arrayList.get(i);
            sb.append(item.label.name()).append(": ").append(item.value).append("\n");
        }
        String s = sb.toString();
        FunctionalPoolObject.StringBuilder.free(sb);
        return s;
    }
    
    public enum Label {
        Drawing, Hentai(true), Sexy(true), Neutral, Porn(true), Anime, ArtificialProvocative(true), DigitalDrawing, Digital, NaturallyProvocative(true), Disturbing(true), SeductiveArt, SexuallyProvocative(true), SeductivePorn(true), PornSeductive(true), HentaiClips(true), SoftPorn(true), Doujin18(true), R34(true);
        public boolean isNsfw;
        
        Label(boolean nsfw) {
            isNsfw = nsfw;
        }
        
        Label() {
            this(false);
        }
    }
    
    public class Item {
        public final Label label;
        public final double value;
        
        public Item(Label label, double value) {
            this.label = label;
            this.value = value;
        }
    }
}
