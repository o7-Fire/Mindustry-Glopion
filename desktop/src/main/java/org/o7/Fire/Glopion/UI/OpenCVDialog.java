package org.o7.Fire.Glopion.UI;

import Atom.Utility.Pool;
import arc.Core;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import arc.util.Buffers;
import arc.util.Strings;
import org.bytedeco.javacv.FrameGrabber;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

public class OpenCVDialog extends ScrollableDialog {
    String select = "VideoInput";
    int maxRow = 5;
    ByteBuffer pixels = Buffers.newByteBuffer(Core.graphics.getBackBufferWidth() * Core.graphics.getBackBufferHeight() * 4);
    Mat mat, grayMat;
    
    @Override
    protected synchronized void setup() {
        
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
            }).growX().row();
        });
        
        
        int w = Core.graphics.getBackBufferWidth();
        int h = Core.graphics.getBackBufferHeight();
        Gl.pixelStorei(Gl.packAlignment, 1);
        final int numBytes = w * h * 4;
        if (numBytes != pixels.capacity()){
            pixels = Buffers.newByteBuffer(numBytes);
        }else{
            pixels.clear();
        }
        if (mat != null){
            mat.release();
            mat = null;
            
        }
        if (grayMat != null){
            grayMat.release();
            grayMat = null;
        }
        Gl.readPixels(0, 0, w, h, Gl.rgba, Gl.unsignedByte, pixels);//4 channel unsigned bit 8
        mat = new Mat(w, h, CvType.CV_8UC4, pixels);//CV_<bit-depth>{U|S|F}C<number_of_channels>
        grayMat = new Mat(w, h, CvType.CV_8UC4);
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGBA2GRAY);
        byte[] lines = new byte[(int) (grayMat.total() * grayMat.channels())];
        Core.app.post(() -> {
            table.add(new Table() {
                @Override
                public void draw() {
                    super.draw();
                    for (int w = 0; w < grayMat.width(); w++) {
                        for (int h = 0; h < grayMat.height(); h++) {
                            double[] d = grayMat.get(w, h);
                            Draw.color((float) d[0], (float) d[1], (float) d[2], (float) d[3]);
                            Draw.rect(Core.atlas.white(), x + w, y + h);
                        }
                    }
                }
            }).growX().row();
        });
        
        
    }
    
    
}
