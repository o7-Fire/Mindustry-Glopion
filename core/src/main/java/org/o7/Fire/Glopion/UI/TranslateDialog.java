package org.o7.Fire.Glopion.UI;

import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import arc.Core;
import arc.scene.ui.TextButton;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.Internal.Annotation.SinceWhen;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Patch.TranslateChat;

import java.util.Locale;

@SinceWhen(since = 0.70f)
public class TranslateDialog extends ScrollableDialog implements Experimental {
    Locale fromLocale = Locale.ENGLISH, toLocale = Interface.getLocale();
    String text = "What";
    
    @Override
    protected void setup() {
        TranslateChat.startProvider();
        table.add("Provider: " + TranslateChat.translator.getClass().getSimpleName()).left().row();
        table.table(t -> {
            t.add("From").growX();
            t.add("To").growX();
            t.row();
            t.field(fromLocale.getLanguage(), s -> {
                fromLocale = new Locale(s);
                refreshDesktop();
            }).left().growX();
            
            t.field(toLocale.getLanguage(), s -> {
                toLocale = new Locale(s);
                refreshDesktop();
            }).right().growX();
        }).growX().row();
        table.table(t -> {
            t.add("Text:").growX();
            t.add("Translated:").growX();
            t.row();
            t.field(text, s -> {
                text = s;
                refreshDesktop();
            }).growX();
            TextButton bs = t.button("Loading.", () -> {
            
            }).growX().disabled(true).update(tx -> {
                tx.setText("Loading" + Utility.repeatThisString(".", Random.getInt(1, 5)));
            }).get();
            Pool.submit(() -> {
                String asshad;
                try {
                    asshad = TranslateChat.translator.translate(fromLocale, toLocale, text).get();
                }catch(Exception e){
                    WarningHandler.handleMindustry(e);
                    asshad = e.getMessage();
                }
                
                String finalAsshad = asshad;
                Core.app.post(() -> {
                    bs.update(() -> {});
                    bs.setText(finalAsshad);
                    bs.setDisabled(false);
                    bs.changed(() -> {
                        Interface.copyToClipboard(finalAsshad);
                    });
                });
            });
            
            
        }).growX().row();
        
    }
    
    @Override
    public void run() {
        show();
    }
}
