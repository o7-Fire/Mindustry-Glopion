package org.o7.Fire.Glopion.Control;

import arc.Core;
import arc.Input;
import arc.KeyBinds;
import arc.input.KeyCode;
import mindustry.input.Binding;

import java.util.Arrays;
import java.util.HashSet;

public class NativeControl implements InterfaceControl {
    protected final Input input;
    public static final HashSet<Binding> block = new HashSet<>(Arrays.asList(Binding.menu, Binding.pause));
    public NativeControl(Input input){
        this.input = input;
    }
    public static boolean isAxis(int index){
        if(Binding.values().length > index)
        return Binding.values()[index].defaultValue(null).getClass() == KeyBinds.Axis.class;
        return false;
    }
    @Override
    public float low(int index) {
        if(isAxis(index))
            return -1;
        return 0;
    }
    public static int mouse(int index){
        return index - Binding.values().length;
    }
    public static boolean isMouse(int index){
        return  (Binding.values().length <= index);
    }
    @Override
    public float high(int index) {
        if(Binding.values().length > index)
            return 1;
        if(Binding.values().length == index)
            return Core.graphics.getHeight();
        return Core.graphics.getWidth();
    }
    
    @Override
    public int sizeInput() {
        return Binding.values().length + 2;
    }
    public static KeyCode getKey(Binding b, float data){
        KeyBinds.KeybindValue value = b.defaultValue(null);
        if(value instanceof KeyCode){
            return (KeyCode) value;
        }
        if(value instanceof KeyBinds.Axis){
            KeyBinds.Axis code = (KeyBinds.Axis) value;
            if(code.key != null)
                return code.key;
            if(data > 0.5){
                if(code.max != null)
                    return code.max;
                throw new NullPointerException(b.name() + " max axis is null");
            }
            if(code.min != null)
                return code.min;
            throw new NullPointerException(b.name() + " min axis is null");
        }
        throw new IllegalArgumentException("Can't find KeyCode: " + b.name() + " " + data);
    }
    @Override
    public void rawInput(float data, int index) {
        if(isMouse(index)){
            if(mouse(index) == 0){
                input.mouseX((int) data);
            }else {
                input.mouseY((int) data);
            }
        }
        Binding binding = Binding.values()[index];
        if(block.contains(binding))return;
        boolean axis = isAxis(index);
        KeyCode keyCode = getKey(binding,index);
        if(keyCode.axis){
            //hijack main input or switch
        }
        input.getKeyboard().keyDown(keyCode);
    }
}
