package org.o7.Fire.Glopion.UI;

import Atom.Utility.Pool;
import arc.util.Strings;
import org.bytedeco.javacv.FrameGrabber;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;

public class OpenCVDialog extends ScrollableDialog {
    String select = "VideoInput";
    int maxRow = 5;
    
    @Override
    protected void setup() {
        table.add("Frame Grabber:").left().row();
        table.table(t -> {
            int i = 0;
            for (String s : FrameGrabber.list) {
                t.button(s, () -> {
                    select = s;
                    init();
                }).growX().disabled(s.equals(select));
                i++;
                if (i % maxRow == 0) t.row();
                
            }
        }).growX().row();
        Pool.submit(() -> {
            String canon = "org.bytedeco.javacv." + select + "FrameGrabber";
            Class<?> pogger = Interface.getClass(canon);
            if (pogger == null){//shit happend sometime
                table.add("Can't find: " + canon);
                return;
            }
            table.add(pogger.getSimpleName() + ":").left().row();
            table.table(t -> {
                
                try {
                    for (String s : (String[]) pogger.getMethod("getDeviceDescriptions").invoke(null)) {
                        t.add(s).row();
                    }
                }catch(Exception e){
                    WarningHandler.handleMindustry(e);
                    t.add(Strings.getFinalMessage(e));
                }
            }).growX();
        });
        
        
    }
    
    
}
