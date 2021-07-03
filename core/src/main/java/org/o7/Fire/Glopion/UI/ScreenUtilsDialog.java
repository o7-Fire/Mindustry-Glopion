package org.o7.Fire.Glopion.UI;

import Atom.Time.Time;
import arc.Core;
import arc.files.Fi;
import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.PixmapTextureData;
import arc.scene.Action;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Label;
import arc.util.ScreenUtils;
import mindustry.gen.Icon;

import java.util.concurrent.TimeUnit;

public class ScreenUtilsDialog extends ScrollableDialog {
    boolean flipY = false;
    volatile boolean bullshitInProgress = false;
    int x = 0, y = 0, h = 0, w = 0;
    float xF, yF, wF = 1, hF = 1;
  
    Texture texture = null;
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
        
            if(texture != null){
                texture.dispose();
                texture = null;
            }
            Time time = new Time(TimeUnit.MILLISECONDS);
            Pixmap pixmap = null;
            pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h, flipY);
            Fi f = new Fi("test.png");
            f.delete();
            PixmapIO.writePng(f,pixmap);
            pixmap.dispose();
            texture = new Texture(f);
            t.image(new TextureRegionDrawable(new TextureRegion(texture))).growX().growY().row();
            t.add(time.elapsedS()).growX();
        }).growX().growY().color(Color.gray);
        
    }
    
    
    
    @Override
    public void hide(Action action) {
        if(texture != null){
            texture.dispose();
            texture = null;
        }
        super.hide(action);
    }
}
