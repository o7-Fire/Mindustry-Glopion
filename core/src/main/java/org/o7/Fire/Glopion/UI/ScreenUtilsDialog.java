package org.o7.Fire.Glopion.UI;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.scene.ui.Label;
import arc.util.ScreenUtils;

public class ScreenUtilsDialog extends ScrollableDialog {
    boolean flipY = false;
    volatile boolean bullshitInProgress = false;
    int x = 0, y = 0, h = 0, w = 0;
    float xF, yF, wF = 1, hF = 1;
    
    @Override
    protected void init() {
        if (bullshitInProgress) return;
        super.init();
    }
    
    @Override
    protected void setup() {
        table.check("Flip Y", flipY, s -> flipY = s).row();
        table.table(t -> {
            Label sb = t.label(() -> "X: " + x).get();
            t.slider(0, 1, 0.01f, xF, s -> {
                xF = s;
                x = (int) (Core.graphics.getBackBufferWidth() * xF);
                sb.setText("X: " + x);
            }).growX();
        }).growX().row();
        table.table(t -> {
            Label sb = t.label(() -> "Y: " + y).get();
            t.slider(0, 1, 0.01f, yF, s -> {
                yF = s;
                y = (int) (Core.graphics.getBackBufferHeight() * yF);
                sb.setText("Y: " + y);
            }).growX();
        }).growX().row();
        table.table(t -> {
            Label sb = t.label(() -> "Height: " + h).get();
            t.slider(0, 1, 0.01f, hF, s -> {
                hF = s;
                h = (int) (Core.graphics.getBackBufferHeight() * hF);
                sb.setText("Height: " + h);
            }).growX();
        }).growX().row();
        table.table(t -> {
            Label sb = t.label(() -> "Width: " + w).get();
            t.slider(0, 1, 0.01f, wF,s -> {
                wF = s;
                w = (int) (Core.graphics.getBackBufferWidth() * wF);
                sb.setText("Width: " + w);
            }).growX();
        }).growX().row();
        
        table.table(t -> {
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h, flipY);
            Fi f = new Fi("test.png");
            f.delete();
            PixmapIO.writePng(f, pixmap);
            pixmap.dispose();
            Label label = t.labelWrap(f.absolutePath()).growY().growX().color(Color.gray).get();
            //t.image(pixmap);
            
        }).growX().growY().color(Color.gray);
        
    }
}
