package org.o7.Fire.Glopion.UI;

import arc.Core;
import arc.func.Cons;
import arc.scene.Action;
import arc.scene.Scene;
import arc.scene.event.EventListener;
import arc.scene.event.VisibilityListener;
import arc.scene.style.Drawable;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Internal.TextManager;

public class AtomicDialog extends BaseDialog {
    public static AtomicDialog instance;
    public Drawable icon = Icon.commandRallySmall;
    
    public AtomicDialog(String title, DialogStyle style) {
        super(title, style);
        showSetup();
    }
    
    public static void showTest(Dialog d, Runnable afterShown) {
        Log.debug("Testing Dialog: @", d.title.getText());
        EventListener eventListener = new VisibilityListener() {
            @Override
            public boolean shown() {
                Core.app.post(() -> d.removeListener(this));
                Runnable r = () -> Core.app.post(() -> {
                    Core.app.post(() -> {
                        d.hide();
                        Core.app.post(afterShown::run);
                    });
                });
                
                if (d instanceof AtomicDialog){
                    //probaly have hidden agenda
                    AtomicDialog atomicDialog = (AtomicDialog) d;
                    Core.app.post(() -> atomicDialog.test(r));
                }else{
                    r.run();
                }
                
                return false;
            }
        };
        d.addListener(eventListener);
        d.show();
    }
    
    protected void test(Runnable after) {
        after.run();//no hidden agenda
    }
    
    public void showSetup() {
        onResize(this::setup);
        shown(this::setup);
        addRefreshAndClose();
    }
    
    public void addRefreshAndClose() {
        addCloseButton();
        addNavButton("Refresh", Icon.refresh, this::setup);
    }
    
    public void inputField(String title, String value, Cons<String> s) {
        inputField(cont, title, value, s);
    }
    
    public void inputField(Table table, String title, String value, Cons<String> s) {
        table.add(TextManager.translate(title)).left().row();
        table.field(value, e -> {
            try {
                s.get(e);
            }catch(NumberFormatException ex){
                //what to do with it
            }
            
        }).growX().row();
    }
    
    public void refreshDesktop() {
        if (!Vars.mobile) setup();
    }
    
    protected void addRefreshButton() {
        addNavButton("Refresh", Icon.refresh, this::setup);
    }
    
    public AtomicDialog(String title) {
        this(title, Core.scene.getStyle(DialogStyle.class));
    }
    
    public AtomicDialog() {
        this("Null");
    }
    
    
    protected void setup()  {
    
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
        if(title.getText().toString().equals("Null")) title.setText(TextManager.get(this.getClass().getName()));
        return title.getText().toString();
    }
}
