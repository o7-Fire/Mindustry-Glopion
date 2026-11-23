package org.o7.Fire.Glopion.Control;

import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.input.KeyCode;
import arc.input.KeyboardDevice;
import arc.struct.IntFloatMap;
import org.o7.Fire.Glopion.Event.EventExtended;

import java.util.HashSet;

public class GlopionControl extends KeyboardDevice {
    public final HashSet<KeyCode> pressed = new HashSet<>(), tapped = new HashSet<>(), released = new HashSet<>();
    public final IntFloatMap axes = new IntFloatMap();
    
    public GlopionControl(){
        if(Core.app == null) return;
        Core.app.addListener(new ApplicationListener(){
            @Override
            public void update(){
                preUpdate();
                Core.app.post(GlopionControl.this::postUpdate);
            }
        });
    }
    
    public void preUpdate() {
        Events.fire(EventExtended.InputUpdate.Pre);
    }
    
    @Override
    public void postUpdate() {
        pressed.clear();
        axes.clear();
        tapped.clear();
        released.clear();
        Events.fire(EventExtended.InputUpdate.Post);
    }
    
    @Override
    public boolean isPressed(KeyCode key) {
        return pressed.contains(key) || Core.input.keyDown(key);
    }
    
    @Override
    public boolean isTapped(KeyCode key) {
        return tapped.contains(key)  || Core.input.keyTap(key);
    }
    
    @Override
    public boolean isReleased(KeyCode key) {
        return released.contains(key) || Core.input.keyRelease(key);
    }
    
    @Override
    public float getAxis(KeyCode keyCode) {
        float o = Core.input.axis(keyCode);
        if(o != 0)return o;
        return axes.get(keyCode.ordinal(), 0);
    }
}
