package org.o7.Fire.Glopion.UI.Element;

import Atom.Utility.Random;
import arc.graphics.Color;
import arc.scene.ui.layout.Cell;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.UI.ScrollableDialog;

public class PlotTableExperiment extends ScrollableDialog implements Experimental {
    @Override
    public void run() {
        show();
    }
    
    @Override
    protected void setup() {
        
        for (int i = 0; i < Random.getInt(100); i++) {
            Cell c = table.add(new PlotTable()).color(Color.white).growY().growX();
            if (i % 3 == 0) c.row();
            
        }
        
        
    }
}
