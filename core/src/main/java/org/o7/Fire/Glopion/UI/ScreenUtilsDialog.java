package org.o7.Fire.Glopion.UI;

import Atom.Time.Time;
import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.scene.Action;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.util.ScreenUtils;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.BorderImage;
import org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper;
import org.o7.Fire.Glopion.Experimental.Experimental;

import java.util.concurrent.TimeUnit;

public class ScreenUtilsDialog extends ScrollableDialog implements Experimental {
    boolean flipY = false;
    int x = 0, y = 0, h = Core.graphics.getHeight(), w = Core.graphics.getWidth();
    float xF, yF, wF = 1, hF = 1;
    
    Texture texture = null;
    BorderImage borderImage = null;
    
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
    
            texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.linear);
            final TextureRegionDrawable region = new TextureRegionDrawable(new TextureRegion(texture));
            if (borderImage != null){
                borderImage.invalidateHierarchy();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Screenshot Time: ");
            sb.append(time.elapsedS()).append("\n");
            sb.append("Byte Length: ").append(w * h * 4).append(" (").append(SharedBootstrapper.humanReadableByteCountSI(w * h * 4)).append(")");
            t.add(sb.toString()).growX().row();
            t.add(borderImage = new BorderImage() {
                {
                    border(Pal.accent);
                    setDrawable(Tex.nomap);
                    pad = Scl.scl(4f);
                }
    
                @Override
                public void draw() {
                    super.draw();
                    setDrawable(region);
                }
            }).row();
            Fi f = new Fi("test.png");
            f.delete();
            PixmapIO.writePng(f, pixmap);
            pixmap.dispose();
        }).growX().growY().color(Color.gray);
        borderImage.clear();
    }
    
    
    @Override
    public void hide(Action action) {
        if (texture != null){
            texture.dispose();
            texture = null;
        }
        super.hide(action);
    }
    
    @Override
    public void run() {
        show();
    }
}
