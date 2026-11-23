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
        float[][] floats = new float[Random.getInt(15, 60)][2];
        Array.random(floats);
        return floats;
    }
    
    @Override
    public void draw() {
        super.draw();
        if (points == null || points.length == 0) return;
        
        Draw.color(color);
        Lines.stroke(thickness);
        Lines.beginLine();
        float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        for (float[] point : points) {
            if (point == null || point.length < 2) continue;
            minX = Math.min(minX, point[0]);
            maxX = Math.max(maxX, point[0]);
            minY = Math.min(minY, point[1]);
            maxY = Math.max(maxY, point[1]);
        }
        if (Float.isInfinite(minX)) return;
        if (Float.isInfinite(minY)) return;
        
        float spanX = maxX - minX;
        float spanY = maxY - minY;
        if (spanX <= 0) spanX = 1f;
        if (spanY <= 0) spanY = 1f;
        float padding = Scl.scl(4f);
        float plotWidth = Math.max(1f, getWidth() - padding * 2);
        float plotHeight = Math.max(1f, getHeight() - padding * 2);
        
        for (float[] point : points) {
            if (point == null || point.length < 2) continue;
            float px = unsigned ? x + padding + (point[0] - minX) / spanX * plotWidth : point[0];
            float py = unsigned ? y + padding + (point[1] - minY) / spanY * plotHeight : point[1];
            Lines.linePoint(px, py);
        }
        Lines.endLine(false);
    }
}
