package org.o7.Fire.Glopion.UI.Element;

import Atom.Math.Array;
import Atom.Utility.Random;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import mindustry.graphics.Pal;

public class PlotTable extends Table {
    protected Color color = Pal.accent;
    protected float thickness = Scl.scl(2);
    protected boolean unsigned = true;
    protected float[][] points;
    
    public PlotTable() {
        this(randomPoints());
    }
    
    public PlotTable(float[][] points) {
        this.points = points;
    }
    
    public static float[][] randomPoints() {
        float[][] floats = new float[Random.getInt(2, 100)][2];
        Array.random(floats);
        return floats;
    }
    
    @Override
    public void draw() {
        super.draw();
        Draw.color(color);
        Lines.stroke(thickness);
        Lines.beginLine();
        for (int i = 0; i < points.length; i++) {
            float xx = points[i][0];
            float yy = points[i][1];
            xx = Math.max(xx * x, xx);
            yy = Math.max(yy * y, yy);
            if (unsigned){
                xx += x;
                yy += y;
            }
            Lines.linePoint(points[i][0], points[i][1]);
        }
        Lines.endLine(false);
    }
}
