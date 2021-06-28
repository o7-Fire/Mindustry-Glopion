package org.o7.Fire.Glopion.Control;

import Atom.Reflect.Reflect;
import arc.Core;
import arc.Input;
import arc.KeyBinds;
import arc.input.KeyCode;
import arc.input.KeyboardDevice;
import arc.struct.IntFloatMap;
import arc.util.Log;
import mindustry.input.Binding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class NativeControl implements InterfaceControl {
    protected final Input input;
    public static final HashSet<Binding> block = new HashSet<>(Arrays.asList(Binding.menu, Binding.pause, Binding.schematic_menu, Binding.toggle_menus, Binding.minimap, Binding.planet_map));
    protected IntFloatMap floatMap = null;
    public NativeControl(Input input){
        this.input = input;
        Field f = Reflect.getField(KeyboardDevice.class,"axes",input.getKeyboard());
        try {
            floatMap = (IntFloatMap) f.get(input.getKeyboard());
        }catch(Exception e){
            Log.err("Failed to get Keyboard Axis: " + e);
        }
        ArrayList<KeyCode> keyCodes = new ArrayList<>();
        
        for(Binding b : Binding.values()){
            if(block.contains(b))continue;
            KeyBinds.KeybindValue value = b.defaultValue(null);
            if(value instanceof KeyCode){
               keyCodes.add((KeyCode) value);
            }
            if(value instanceof KeyBinds.Axis){
                KeyBinds.Axis code = (KeyBinds.Axis) value;
                if(code.key != null){
                    if(code.key.axis)
                        keyCodes.add(code.key);
                    keyCodes.add(code.key);
                }
                if(code.max != null)
                    keyCodes.add(code.max);
                if(code.min != null)
                    keyCodes.add(code.min);
            }
            this.keyCodes =  keyCodes.toArray(new KeyCode[0]);
        }
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
    protected KeyCode[] keyCodes;
    @Override
    public int getSize() {
        return 1 + keyCodes.length + 4;//no op + value + mouse x,y,x-,y-
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
    public void mouseX(int increment){
        input.mouseX(input.mouseX()+increment);
    }
    public void mouseY(int increment){
        input.mouseY(input.mouseY()+increment);
    }
    @Override
    public void rawInput(int index) {
        index--;//eliminated from equation
        if(index < 0)return;//no op
        if(index >= getSize())throw new IndexOutOfBoundsException(getSize() + " requested: " + index);
        int mouse = getSize() - index;
        if(4 > mouse){
            switch (mouse){
                case 0:
                    mouseX(1);
                    return;
                case 1:
                    mouseX(-1);
                    return;
                case 2:
                    mouseY(1);
                    return;
                case 3:
                    mouseY(-1);
                    return;
                default:
                    throw new IllegalStateException("mouse index: " + mouse + ", index: " + index);
            }
        }
        KeyCode keyCode = keyCodes[index];
        boolean axis = keyCode.axis;
        if(keyCode.axis){
            float increment = ((index % 2) == 0) ? -1 : 1;//assume thing happends
            floatMap.put(keyCode.ordinal(),increment);
            return;
        }
        input.getKeyboard().keyDown(keyCode);
    }
}
