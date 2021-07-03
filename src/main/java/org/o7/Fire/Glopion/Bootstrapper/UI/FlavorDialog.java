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
import org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper;

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
    
    void build() {
      
        buttons.clear();
        addCloseButton();
        cont.clear();
        cont.check("Same Mindustry Version",sameVersion, b-> {
            sameVersion = b;
            build();
        }).growX().row();
        cont.check("Same Mindustry Modifier",sameModifier, b-> {
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
          
            for (Map.Entry<Object, Object> o : bootstrapper.release.entrySet()) {
                if ((o.getKey() + "").startsWith("Note"))
                    t.add(o.getKey() + ": " + o.getValue()).growX().row();
            
            }
            for (Map.Entry<Object, Object> oo : bootstrapper.release.entrySet()) {
              String oKey = String.valueOf(oo.getKey()), oValue = String.valueOf(oo.getValue());
                if ((oKey + "").startsWith("Note"))continue;
                Seq<String> key = Seq.with(oKey.split("-"));
                long bootstrapMin = Long.MAX_VALUE-1;
                int bootstrapIndex = key.indexOf("Bootstrap");
                if(bootstrapIndex != -1){
                    try {
                        bootstrapMin = Long.parseLong(key.get(bootstrapIndex + 1));
                    }catch(Exception e){
                        Log.err(e);
                    }
                }
                boolean mindustryVersionCompatible =  key.contains(Version.buildString());
                boolean mindustryModifierCompatible = key.contains(Version.modifier.replace('-','.'));
                boolean bootstrapperCompatible = bootstrapMin < SharedBootstrapper.version;
                if (!bootstrapperCompatible && compatibleBootstrapperVersion) continue;
                if (!mindustryModifierCompatible && sameModifier) continue;
                if (!mindustryVersionCompatible && sameVersion) continue;
                Cell<TextButton> c = t.button(oKey + ": " + oValue, () -> {
                    Core.settings.put("glopion-flavor", oKey + "");
                    Fi jar = getFlavorJar(oKey);
                    if(!jar.exists()) BootstrapperUI.downloadConfirm(oValue, jar, ()->{
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
