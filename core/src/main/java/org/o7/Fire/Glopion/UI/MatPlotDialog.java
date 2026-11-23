package org.o7.Fire.Glopion.UI;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.Internal.TextManager;

public class MatPlotDialog extends AtomicDialog implements Experimental {
    public static Interp interp = n -> evaluate("3*n+1", n);
    public float max = 100, min = -100, increment = 1, thicc = Scl.scl(2), offset = Scl.scl(8);
    public int length = 200;
    public String formula = "3*n+1";
    
    public static double normalizePrecision(double x, double min, double max) {
        double average = (double) (min + max) / 2;
        double range = (double) (max - min) / 2;
        return range == 0 ? 0 : (x - average) / range;
    }
    
    public static float evaluate(String expression, float n) {
        if (expression == null || expression.trim().isEmpty()) return 3 * n + 1;
        try {
            return (float) new ExpressionParser(expression, n).parse();
        }catch(RuntimeException e){
            return 3 * n + 1;
        }
    }
    
    @Override
    protected void setup() {
        cont.clear();
        Label formulaLabel = new Label("f(n) = " + formula);
        cont.add(formulaLabel).left().row();
        inputField("Formula", formula, s -> {
            formula = s;
            formulaLabel.setText("f(n) = " + formula);
        });
        inputField("Max", max + "", s -> max = Float.parseFloat(s));
        inputField("Min", min + "", s -> min = Float.parseFloat(s));
        inputField("Increment", increment + "", s -> increment = Float.parseFloat(s));
        cont.add(TextManager.translate("Line Thickness")).left().row();
        cont.slider(0.1f, 10, 0.1f, thicc, s -> thicc = Scl.scl(s)).growX().row();
        cont.add(TextManager.translate("Offset")).left().row();
        cont.slider(0, 1000, 1, offset, s -> offset = Scl.scl(s)).growX().row();
        cont.add("Length: " + ((max - min) / Math.max(0.0001f, increment))).left().row();
        cont.add(new Table() {
            @Override
            public void draw() {
                super.draw();
                float w = Math.max(1f, getWidth());
                float h = Math.max(1f, getHeight());
                float span = Math.max(0.0001f, max - min);
                float stepSize = Math.max(0.0001f, increment);
                int count = Mathf.clamp((int) (span / stepSize) + 1, 2, 1000);
                float step = span / (count - 1);
                float[] values = new float[count];
                float valueMin = Float.POSITIVE_INFINITY;
                float valueMax = Float.NEGATIVE_INFINITY;
                
                for (int i = 0; i < count; i++) {
                    values[i] = evaluate(formula, min + step * i);
                    valueMin = Math.min(valueMin, values[i]);
                    valueMax = Math.max(valueMax, values[i]);
                }
                valueMin = Math.min(valueMin, 0f);
                valueMax = Math.max(valueMax, 0f);
                if (Mathf.equal(valueMin, valueMax)) {
                    valueMin -= 1f;
                    valueMax += 1f;
                }
                
                float padding = Mathf.clamp(Scl.scl(offset), 0f, h * 0.25f);
                float left = x + padding;
                float right = x + w - padding;
                float bottom = y + padding;
                float top = y + h - padding;
                float valueSpan = valueMax - valueMin;
                
                Draw.color(Color.gray);
                Lines.stroke(1f);
                Lines.beginLine();
                for (int i = 0; i <= 8; i++) {
                    float gy = Mathf.lerp(bottom, top, i / 8f);
                    float gx = Mathf.lerp(left, right, i / 8f);
                    Lines.line(left, gy, right, gy);
                    Lines.line(gx, bottom, gx, top);
                }
                Lines.endLine(false);
                
                float zeroY = Mathf.lerp(bottom, top, (0f - valueMin) / valueSpan);
                Draw.color(Color.white);
                Lines.stroke(Math.max(1f, thicc * 0.5f));
                Lines.line(left, zeroY, right, zeroY);
                
                Draw.color(Pal.accent);
                Lines.stroke(thicc);
                Lines.beginLine();
                for (int i = 0; i < count; i++) {
                    float px = Mathf.lerp(left, right, (float) i / (count - 1));
                    float py = Mathf.lerp(bottom, top, (values[i] - valueMin) / valueSpan);
                    Lines.linePoint(px, py);
                }
                Lines.endLine(false);
                
                Fonts.def.draw(Float.toString(valueMax), left, top);
                Fonts.def.draw(Float.toString(valueMin), left, bottom);
                Fonts.def.draw(Float.toString(min), left, bottom - Scl.scl(12f));
                Fonts.def.draw(Float.toString(max), right - Scl.scl(40f), bottom - Scl.scl(12f));
            }
        }).growX().growY();
        setCentered(true);
        if (Core.graphics != null) {
            float w = Math.max(480f, Math.min(Core.graphics.getWidth() * 0.8f, 820f));
            float h = Math.max(360f, Math.min(Core.graphics.getHeight() * 0.8f, 620f));
            setSize(w, h);
        }
    }
    
    @Override
    public void run() {
        show();
    }
    
    private static final class ExpressionParser {
        private final String source;
        private final float n;
        private int pos;
        
        ExpressionParser(String source, float n) {
            this.source = source;
            this.n = n;
        }
        
        double parse() {
            double value = expression();
            skip();
            if (pos != source.length()) throw new IllegalArgumentException("Unexpected token at " + pos);
            return value;
        }
        
        private double expression() {
            double value = term();
            while (true) {
                if (eat('+')) value += term();
                else if (eat('-')) value -= term();
                else return value;
            }
        }
        
        private double term() {
            double value = power();
            while (true) {
                if (eat('*')) value *= power();
                else if (eat('/')) value /= power();
                else if (eat('%')) value %= power();
                else return value;
            }
        }
        
        private double power() {
            double value = unary();
            if (eat('^')) value = Math.pow(value, power());
            return value;
        }
        
        private double unary() {
            skip();
            if (pos >= source.length()) throw new IllegalArgumentException("Missing value");
            char c = source.charAt(pos);
            if (c == '+') {
                pos++;
                return unary();
            }
            if (c == '-') {
                pos++;
                return -unary();
            }
            if (c == '(') {
                pos++;
                double value = expression();
                if (!eat(')')) throw new IllegalArgumentException("Missing )");
                return value;
            }
            if (Character.isDigit(c) || c == '.') return number();
            
            String name = identifier();
            if (name.equalsIgnoreCase("n") || name.equalsIgnoreCase("x")) return n;
            if (name.equalsIgnoreCase("pi")) return Math.PI;
            if (name.equalsIgnoreCase("e")) return Math.E;
            if (!eat('(')) throw new IllegalArgumentException("Unknown symbol: " + name);
            
            java.util.List<Double> args = new java.util.ArrayList<>();
            if (!eat(')')) {
                do {
                    args.add(expression());
                }while(eat(','));
                if (!eat(')')) throw new IllegalArgumentException("Missing )");
            }
            return call(name, args);
        }
        
        private double number() {
            int start = pos;
            while (pos < source.length() && (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) pos++;
            if (pos < source.length() && (source.charAt(pos) == 'e' || source.charAt(pos) == 'E')) {
                int mark = pos++;
                if (pos < source.length() && (source.charAt(pos) == '+' || source.charAt(pos) == '-')) pos++;
                while (pos < source.length() && Character.isDigit(source.charAt(pos))) pos++;
                if (mark + 1 == pos) pos = mark;
            }
            return Double.parseDouble(source.substring(start, pos));
        }
        
        private String identifier() {
            int start = pos;
            while (pos < source.length()) {
                char c = source.charAt(pos);
                if (!Character.isLetterOrDigit(c) && c != '_') break;
                pos++;
            }
            return source.substring(start, pos);
        }
        
        private void skip() {
            while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) pos++;
        }
        
        private boolean eat(char c) {
            skip();
            if (pos < source.length() && source.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }
        
        private double call(String name, java.util.List<Double> args) {
            switch (name.toLowerCase(java.util.Locale.ROOT)) {
                case "sin": return Math.sin(arg(args, 0, 1));
                case "cos": return Math.cos(arg(args, 0, 1));
                case "tan": return Math.tan(arg(args, 0, 1));
                case "abs": return Math.abs(arg(args, 0, 1));
                case "sqrt": return Math.sqrt(arg(args, 0, 1));
                case "log": return Math.log(arg(args, 0, 1));
                case "log10": return Math.log10(arg(args, 0, 1));
                case "exp": return Math.exp(arg(args, 0, 1));
                case "floor": return Math.floor(arg(args, 0, 1));
                case "ceil": return Math.ceil(arg(args, 0, 1));
                case "round": return Math.round(arg(args, 0, 1));
                case "min": return Math.min(arg(args, 0, 2), arg(args, 1, 2));
                case "max": return Math.max(arg(args, 0, 2), arg(args, 1, 2));
                case "pow": return Math.pow(arg(args, 0, 2), arg(args, 1, 2));
                default: throw new IllegalArgumentException("Unknown function: " + name);
            }
        }
        
        private double arg(java.util.List<Double> args, int index, int expected) {
            if (args.size() != expected) throw new IllegalArgumentException("Expected " + expected + " arguments");
            return args.get(index);
        }
    }
}
