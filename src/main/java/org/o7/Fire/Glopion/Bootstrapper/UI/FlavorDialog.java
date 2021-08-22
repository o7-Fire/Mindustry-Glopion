package org.o7.Fire.Glopion.Bootstrapper.UI;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Bootstrapper.BootstrapperUI;
import org.o7.Fire.Glopion.Bootstrapper.Main;
import org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper;

import java.util.Comparator;
import java.util.Map;

import static mindustry.Vars.ui;
import static org.o7.Fire.Glopion.Bootstrapper.Main.flavor;
import static org.o7.Fire.Glopion.Bootstrapper.Main.getFlavorJar;

public class FlavorDialog extends BaseDialog {
    BootstrapperUI bootstrapper;
    public FlavorDialog(BootstrapperUI main) {
        super("Glopion Flavor");
        bootstrapper = main;
        shown(this::build);
        onResize(this::build);
    }
    
    boolean sameModifier = true, sameVersion = true, compatibleBootstrapperVersion = true;
    
    @Override
    public void hide() {
        super.hide();
        bootstrapper.buildUI();
    }
    
    public static final Comparator<Seq<String>> flavorSort = new Comparator<Seq<String>>() {
        public int getScore(Seq<String> s) {
            int score = 0;
            score += Math.min(bootstrapperCompatiblity(s), 0);
            if (isMindustryVersionCompatible(s)) score += 4;
            if (isMindustryModifierCompatible(s)) score += 5;
            if (!isPlatformCompatible(s)) score -= 10;
            return score;
        }
        
        @Override
        public int compare(Seq<String> o1, Seq<String> o2) {
            return Integer.compare(getScore(o1), getScore(o2));
        }
    };
    
    public static boolean isMindustryVersionCompatible(Seq<String> key) {
        return key.contains(Version.buildString());
    }
    
    public static boolean isMindustryModifierCompatible(Seq<String> key) {
        return key.contains(Version.modifier.replace('-', '.'));
    }
    
    public static boolean isPlatformCompatible(Seq<String> key) {
        return key.contains("Desktop") && !Vars.mobile;
    }
    
    public static boolean isBootstrapperCompatible(Seq<String> key) {
        return bootstrapperCompatiblity(key) == 0;
    }
    
    public static long bootstrapperCompatiblity(Seq<String> key) {
        long bootstrapMin = -1;
        int bootstrapIndex = key.indexOf("Bootstrap");
        if (bootstrapIndex != -1){
            try {
                bootstrapMin = Long.parseLong(key.get(bootstrapIndex + 1));
            }catch(Exception e){
                Log.err(e);
            }
        }
        return bootstrapMin - SharedBootstrapper.version;
    }
    
    public static Seq<String> toKey(String str) {
        if (str.startsWith("Note")) throw new IllegalArgumentException("A Note \"" + str + "\"");
        return Seq.with(str.split("-"));
    }
    
    public static boolean isFullyCompatible(Seq<String> key) {
        if (!isBootstrapperCompatible(key)) return false;
        if (!isMindustryModifierCompatible(key)) return false;
        if (!isMindustryVersionCompatible(key)) return false;
        return isPlatformCompatible(key);
    }
    
    void build() {
        
        buttons.clear();
        addCloseButton();
        cont.clear();
        cont.check("Same Mindustry Version", sameVersion, b -> {
            sameVersion = b;
            build();
        }).growX().row();
        cont.check("Same Mindustry Modifier", sameModifier, b -> {
            sameModifier = b;
            build();
        }).growX().row();
        cont.check("Compatible With Bootstrapper Version",compatibleBootstrapperVersion, b-> {
            Vars.ui.showInfo("may cause startup error, or won't load");
            compatibleBootstrapperVersion = b;
            build();
        }).growX().row();
        Table table = new Table(t -> {
            boolean none = true;
    
            for (Map.Entry<Object, Object> o : Main.release.entrySet()) {
                if ((o.getKey() + "").startsWith("Note")) t.add(o.getKey() + ": " + o.getValue()).growX().row();
        
            }
            for (Map.Entry<Object, Object> oo : Main.release.entrySet()) {
                String oKey = String.valueOf(oo.getKey()), oValue = String.valueOf(oo.getValue());
                if ((oKey + "").startsWith("Note")) continue;
                Seq<String> key = toKey(oKey);
                boolean mindustryVersionCompatible = isMindustryVersionCompatible(key);
                boolean mindustryModifierCompatible = isMindustryModifierCompatible(key);
                boolean bootstrapperCompatible = isBootstrapperCompatible(key);
                if (!bootstrapperCompatible && compatibleBootstrapperVersion) continue;
                if (!mindustryModifierCompatible && sameModifier) continue;
                if (!mindustryVersionCompatible && sameVersion) continue;
                Cell<TextButton> c = t.button(oKey + ": " + oValue, () -> {
                    Core.settings.put("glopion-flavor", oKey + "");
                    Fi jar = getFlavorJar(oKey);
                    if (!jar.exists()) BootstrapperUI.downloadConfirm(oValue, jar, () -> {
                        if (jar.exists()){
                            Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
                        }else{
                            ui.showErrorMessage(jar.absolutePath() + " still doesn't exist ??? how");
                        }
                    });
                    build();
                }).growX().disabled(key.contains("Desktop") && Vars.mobile);
                if (Core.settings.get("glopion-flavor", flavor).equals(oKey + "")){
                    c.color(Color.green);
                    c.disabled(true);
                    
                } else if(!bootstrapperCompatible || !mindustryModifierCompatible)c.color(Color.scarlet);
                else if(!mindustryVersionCompatible)c.color(Color.pink);
                else c.color(Color.sky);
                c.row();
                none = false;
            }
            if(none)
                t.add("Not Found").growX().growY().center().color(Color.orange);
        });
        ScrollPane scrollPane = new ScrollPane(table);
        cont.add(scrollPane).growX().growY();
    }
}
