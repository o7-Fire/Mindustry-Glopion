package org.o7.Fire.Glopion.Experimental;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;

public class LockAllContent implements Experimental {
    @Override
    public void run() {
        
        boolean ui = Vars.ui != null && Vars.ui.loadfrag != null;
        int i = 1;
        int max = 1;
        for (Seq<Content> ce : Vars.content.getContentMap()) {
            for (Content cc : ce) {
                if (cc instanceof UnlockableContent) max++;
            }
        }
        int finalMax = max;
        if (ui){
            Vars.ui.loadfrag.show("Locking");
            
            int finalI1 = i;
            Vars.ui.loadfrag.setProgress((() -> (float) finalMax / finalI1));
        }
        for (Seq<Content> ce : Vars.content.getContentMap()) {
            for (Content cc : ce) {
                if (cc instanceof UnlockableContent){
                    UnlockableContent content = (UnlockableContent) cc;
                    content.clearUnlock();
                    i++;
                    int finalI = i;
                    if (ui){
                        Vars.ui.loadfrag.setText(content.name);
                        Vars.ui.loadfrag.setProgress((() -> (float) finalMax / finalI));
                    }
                }
            }
        }
        Log.info("Unlocked @ content", i);
        if (ui) Vars.ui.loadfrag.hide();
    }
}
