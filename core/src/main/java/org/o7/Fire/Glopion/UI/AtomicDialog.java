package org.o7.Fire.Glopion.UI;

import arc.Core;
import arc.scene.Action;
import arc.scene.Scene;
import arc.scene.style.Drawable;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class AtomicDialog extends BaseDialog {
    private static final AtomicDialog instance = new AtomicDialog() {{addCloseButton();}};
    public Drawable icon = Icon.commandRallySmall;
    
    public AtomicDialog(String title, DialogStyle style) {
        super(title, style);
        onResize(this::setup);
        shown(this::setup);
    }
    
    public AtomicDialog(String title) {
        this(title, Core.scene.getStyle(DialogStyle.class));
    }
    
    public AtomicDialog() {
        this("Null");
        title.setText(this.getClass().getSimpleName());
    }
    
    protected void addRefreshButton() {
        addNavButton("Refresh", Icon.refresh, this::setup);
    }
    
    protected void setup() {
    
    }
    
    public Cell<TextButton> addNavButton(String name, Drawable icon, Runnable doSmth) {
        if (Vars.mobile) return buttons.button(name, icon, doSmth).growX();
        else return buttons.button(name, icon, doSmth).size(210f, 64f);
    }
    
    @Override
    public Dialog show(Scene stage, Action action) {
        try {
            return super.show(stage, action);
        }catch(Throwable t){
            hide();
            Vars.ui.loadfrag.hide();
            instance.cont.reset();
            instance.cont.add(Strings.getFinalMessage(t)).growX().growY();
            return instance.show(stage, action);
        }
    }
    
    
    @Override
    public void addCloseButton() {
        addNavButton("@back", Icon.left, this::hide);
        addCloseListener();
    }
    
    protected String getTitle() {
        return title.getText().toString();
    }
}