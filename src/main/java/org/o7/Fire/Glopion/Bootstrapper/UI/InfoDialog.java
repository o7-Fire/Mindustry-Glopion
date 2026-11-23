package org.o7.Fire.Glopion.Bootstrapper.UI;

import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import mindustry.ui.dialogs.BaseDialog;

public class InfoDialog extends BaseDialog {
    final String text;
    
    public InfoDialog(String title, String text){
        super(title);
        this.text = text;
        addCloseButton();
        shown(this::setup);
        onResize(this::setup);
    }
    void setup(){
        cont.clear();
        Table table = new Table();
        table.add(text).growY().growX();
        ScrollPane scrollPane = new ScrollPane(table);
        cont.add(scrollPane).growX().growY();
    }
}
