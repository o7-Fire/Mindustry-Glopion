package org.o7.Fire.Glopion.Control;

import Atom.Reflect.Reflect;
import arc.Core;
import arc.Input;
import arc.input.KeyBind;
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
    public static final HashSet<KeyBind> block = new HashSet<>(Arrays.asList(
            Binding.menu, Binding.pause, Binding.schematicMenu, Binding.toggleMenus, Binding.minimap, Binding.planetMap,//
            Binding.console, Binding.mouseMove, Binding.pan, Binding.chatMode, Binding.chat, Binding.chatScroll, Binding.chatHistoryNext,//
            Binding.chatHistoryPrev, Binding.playerList, Binding.toggleBlockStatus, Binding.togglePowerLines, Binding.screenshot,//
            Binding.fullscreen,Binding.minimap,Binding.research,Binding.blockInfo//
    ));
    protected IntFloatMap floatMap = null;
    public NativeControl(Input input){
        this.input = input;
        try {
            floatMap = Reflect.getField(KeyboardDevice.class,"axes",input.getKeyboard());;
        }catch(Exception e){
            Log.err("Failed to get Keyboard Axis: " + e);
        }
        ArrayList<KeyCode> keyCodes = new ArrayList<>();
        
        for(KeyBind b : KeyBind.all){
            if(block.contains(b))continue;
            KeyBind.KeybindValue value = b.defaultValue;
            if(value instanceof KeyCode){
               keyCodes.add((KeyCode) value);
            }
            if(value instanceof KeyBind.Axis){
                KeyBind.Axis code = (KeyBind.Axis) value;
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
        if(KeyBind.all.size > index && index >= 0)
        return KeyBind.all.get(index).defaultValue instanceof KeyBind.Axis;
        return false;
    }
    @Override
    public float low(int index) {
        if(isAxis(index))
            return -1;
        return 0;
    }
    public static int mouse(int index){
        return index - KeyBind.all.size;
    }
    public static boolean isMouse(int index){
        return  (KeyBind.all.size <= index);
    }
    @Override
    public float high(int index) {
        if(KeyBind.all.size > index)
            return 1;
        if(KeyBind.all.size == index)
            return Core.graphics.getHeight();
        return Core.graphics.getWidth();
    }
    protected KeyCode[] keyCodes;
    @Override
    public int getSize() {
        return 1 + keyCodes.length + 4;
    }
    public static KeyCode getKey(KeyBind b, float data){
        KeyBind.KeybindValue value = b.defaultValue;
        if(value instanceof KeyCode){
            return (KeyCode) value;
        }
        if(value instanceof KeyBind.Axis){
            KeyBind.Axis code = (KeyBind.Axis) value;
            if(code.key != null)
                return code.key;
            if(data > 0.5){
                if(code.max != null)
                    return code.max;
                throw new NullPointerException(b.name + " max axis is null");
            }
            if(code.min != null)
                return code.min;
            throw new NullPointerException(b.name + " min axis is null");
        }
        throw new IllegalArgumentException("Can't find KeyCode: " + b.name + " " + data);
    }
    public void mouseX(int increment){
        Log.infoTag("Input","Mouse X:" + increment);
       input.mouseX(input.mouseX()+increment);
    }
    public void mouseY(int increment){
        Log.infoTag("Input","Mouse Y:" + increment);
       input.mouseY(input.mouseY()+increment);
    }
    @Override
    public void rawInput(int index) {
        index--;
        if(index < 0)return;
        if(index >= getSize())throw new IndexOutOfBoundsException(getSize() + " requested: " + index);
        int mouse = index - keyCodes.length ;
            switch (mouse) {
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
            }
        KeyCode keyCode = keyCodes[index];
       
        boolean axis = keyCode.axis;
        if(axis){
            float increment = ((index % 2) == 0) ? -1 : 1;
           floatMap.put(keyCode.ordinal(),increment);
            Log.infoTag("Input","Axis: "+keyCode.name() + " " + increment);
            return;
        }
        Log.infoTag("Input",keyCode.name());
        input.getKeyboard().keyDown(keyCode);
    }
}
