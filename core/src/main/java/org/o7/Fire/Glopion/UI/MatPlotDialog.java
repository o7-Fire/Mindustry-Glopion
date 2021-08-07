package org.o7.Fire.Glopion.UI;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.Internal.TextManager;

public class MatPlotDialog extends AtomicDialog implements Experimental {
    public static Interp interp = n -> 3 * n + 1;
    public float max = 100, min = -100, increment = 1, thicc = Scl.scl(2), offset = Scl.scl(8);
    public int length = 200;
    public String formula = "3*n+1";
    
    public static double normalizePrecision(double x, double min, double max) {
        double average = (double) (min + max) / 2;
        double range = (double) (max - min) / 2;
        double normalized_x = (double) (x - average) / range;
        return normalized_x;
    }
    
    @Override
    protected void setup() {
        cont.clear();
        inputField("Formula", formula, s -> {
            formula = s;
        });
        
        cont.add(Vars.mods.getScripts().runConsole("org.o7.Fire.Glopion.UI.MatPlotDialog.interp = n => " + formula + ";org.o7.Fire.Glopion.UI.MatPlotDialog.interp.apply(2);")).left().row();
        inputField("Max", max + "", s -> {
            max = Float.parseFloat(s);
        });
        inputField("Min", min + "", s -> {
            min = Float.parseFloat(s);
        });
        inputField("Increment", increment + "", s -> {
            increment = Float.parseFloat(s);
        });
        cont.add(TextManager.translate("Line Thickness")).left().row();
        cont.slider(0.1f, 10, 0.1f, thicc, s -> {
            thicc = Scl.scl(s);
        }).growX().row();
        cont.add(TextManager.translate("Offset")).left().row();
        cont.slider(0, 1000, 1, offset, s -> {
            offset = Scl.scl(s);
        }).growX().row();
        /*
        cont.add(TextManager.translate("Length")).left().row();
        cont.slider(1, 1000, 1, length, s -> {
            length = (int) s;
            length = Math.max(1,length);
        }).growX().row();
        
         */
        cont.add("Length: " + ((max - min) / increment)).left().row();
        cont.add(new Table() {
            float currentLengthCal = min;
            boolean lengthCal = false;
            float[] cacheCalculation = new float[(int) ((max - min) / increment)];
            float[] cacheReal = new float[cacheCalculation.length];
            //Timer timer = new Timer(TimeUnit.MILLISECONDS, 500);
            float globalMin, globalMax;
            
            @Override
            public void draw() {
                super.draw();
                //if (timer.get()) cacheY = Array.randomFloat(length);
                Draw.color(Pal.accent);
                Lines.stroke(thicc);
                Lines.beginLine();
                float maxVal = 0, minVal = 0, lastYDraw = 0;
                for (int i = 0; i < cacheCalculation.length; i++) {
                    try {
                        float yy = cacheCalculation[i];
                        if (yy == 0.0f){
                            cacheCalculation[i] = interp.apply(i);
                            cacheReal[i] = cacheCalculation[i];
                        }
                        
                        yy = yy * y;
                        yy = yy + offset + y;
                        yy = yy * Scl.scl(2);
                        float xx = (float) i / cacheCalculation.length;
                        xx = x * xx * 200;//magic value
                        if (i % (cacheCalculation.length / 10) == 0){//magic value
                            Draw.color(Color.white);
                            if (lastYDraw == 0 || !Mathf.equal(lastYDraw, yy, x * 2 * thicc)){
                                Lines.line(x, yy, x * 200, yy);
                                Fonts.def.draw(cacheReal[i] + "", x, yy);
                                lastYDraw = yy;
                            }
                            Lines.line(xx, yy, xx, y);
                            Fonts.def.draw(i + "", xx, y);
                            Draw.color(Pal.accent);
                        }
                        maxVal = Math.max(cacheCalculation[i], maxVal);
                        minVal = Math.min(cacheCalculation[i], minVal);
                        Lines.linePoint(xx, yy);
                    }catch(ArithmeticException gay){}
                }
                
                
                if (maxVal > 1 || minVal < -1){
                    globalMax = maxVal;
                    globalMin = minVal;
                    for (int i = 0; i < cacheCalculation.length; i++) {
                        cacheCalculation[i] = (float) normalizePrecision(cacheCalculation[i], minVal, maxVal);
                    }
                }
              /*
              if(!lengthCal){
                  if(currentLengthCal < max){
                      currentLengthCal += increment;
                      length++;
                  }else {
                      lengthCal = true;
                     
                  }
              }else {
                  float f = min;
                  if(cacheX != null){
                      for (int i = 0; i < length; i++) {
                          Lines.linePoint(cacheX[i], cacheY[i]);
                      }
                  }else {
                      cacheX = new float[length];
                      cacheY = new float[length];
                      for (int i = 0; i < length; i++){
                          float xp = x+i*thicc;
                          float yp = y + interp.apply(min+(increment*i)) * length * thicc;
                          cacheX[i] = xp;
                          cacheY[i] = yp;
                          Lines.linePoint(xp, yp);
                      }
                          //Lines.linePoint(x + il * thicc + offset, y + interp.apply(il / sigs) * length * thicc + offset);
                  }
                 
    
              }
              
               */
                Lines.endLine(false);
            }
        }).growX().growY();
    }
    
    @Override
    public void run() {
        show();
    }
}
