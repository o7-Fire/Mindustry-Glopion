package org.o7.Fire.Glopion.Bootstrapper.UI;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Bootstrapper.BootstrapperUI;

import java.util.Map;
import java.util.TreeMap;

import static org.o7.Fire.Glopion.Bootstrapper.Main.flavor;

public class FlavorDialog extends BaseDialog {
    BootstrapperUI bootstrapper;
    public FlavorDialog(BootstrapperUI main) {
        super("Glopion Flavor");
        bootstrapper = main;
        shown(this::build);
    }
    boolean showIncompatible = false;
 
    
    @Override
    public void hide() {
        super.hide();
        bootstrapper.buildUI();
    }
    
    void build() {
        buttons.clear();
        addCloseButton();
        if(showIncompatible){
            this.buttons.button("Hide Incompatible", Icon.book, () -> {
                showIncompatible = false;
                build();
            }).size(210.0F, 64.0F);
        }else {
            this.buttons.button("Show Incompatible", Icon.bookOpen, () -> {
                showIncompatible = true;
                build();
            }).size(210.0F, 64.0F);
        }
        cont.clear();
        
        Table table = new Table(t -> {
            boolean none = true;
            TreeMap<String, String> map = new TreeMap<>();
            for (Map.Entry<Object, Object> o : bootstrapper.release.entrySet()) {
                if ((o.getKey() + "").startsWith("Note"))
                    t.add(o.getKey() + ": " + o.getValue()).growX().row();
                else map.put(o.getKey() + "", o.getValue() + "");
            }
            for (Map.Entry<String, String> o : map.entrySet()) {
                boolean compatible = o.getKey().contains(Version.buildString());
                if (!compatible && !showIncompatible) continue;
                Cell<TextButton> c = t.button(o.getKey() + ": " + o.getValue(), () -> {
                    Core.settings.put("glopion-flavor", o.getKey() + "");
                    build();
                }).growX().disabled(o.getKey().startsWith("Desktop") && Vars.mobile);
                if (Core.settings.get("glopion-flavor", flavor).equals(o.getKey() + "")){
                    c.color(Color.green);
                    c.disabled(true);
                }else if (compatible) c.color(Color.sky);
                else c.color(Color.coral);
                c.row();
                none = false;
            }
            if(none)
                t.add("No Compatible Flavor").growX().growY().center().color(Color.orange);
        });
        ScrollPane scrollPane = new ScrollPane(table);
        cont.add(scrollPane).growX().growY();
    }
}
