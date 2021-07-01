package org.o7.Fire.Glopion.Control;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.input.KeyCode;
import arc.input.KeyboardDevice;
import arc.struct.IntFloatMap;
import arc.struct.IntSet;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.Drawf;

public class MachineKeyboard extends KeyboardDevice {
    protected final IntSet pressed = new IntSet();
    protected final IntSet lastFramePressed = new IntSet();
    protected final IntFloatMap axes = new IntFloatMap();
    protected long nextFlush = 0;
    public MachineKeyboard(){
        Events.run(EventType.Trigger.draw,()->{
            Drawf.circles(Core.input.mouseWorldX(),Core.input.mouseWorldY(),12f / Vars.renderer.getDisplayScale());
        });
    }
    @Override
    public void preUpdate() {
        if(System.currentTimeMillis() > nextFlush){
            axes.clear();
            pressed.clear();
            nextFlush = System.currentTimeMillis() + (6000 / Core.graphics.getFramesPerSecond());
        }
       // Core.app.post(axes::clear);
       // Core.app.post(pressed::clear);
    
    }
    
    @Override
    public void postUpdate(){
        
        lastFramePressed.clear();
        lastFramePressed.addAll(pressed);

    }
    
    @Override
    public boolean isPressed(KeyCode key){
        if(key == KeyCode.anyKey) return pressed.size > 0;
        
        return pressed.contains(key.ordinal());
    }
    
    @Override
    public boolean isTapped(KeyCode key){
        return isPressed(key) && !lastFramePressed.contains(key.ordinal());
    }
    
    @Override
    public boolean isReleased(KeyCode key){
        return !isPressed(key) && lastFramePressed.contains(key.ordinal());
    }
    
    @Override
    public float getAxis(KeyCode keyCode){
        return axes.get(keyCode.ordinal(), 0);
    }
    
    @Override
    public boolean keyDown(KeyCode key){
        pressed.add(key.ordinal());
        return false;
    }
    
    @Override
    public boolean keyUp(KeyCode key){
        pressed.remove(key.ordinal());
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, KeyCode button){
        keyDown(button);
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, KeyCode button){
        if(pointer == 0) keyUp(button);
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY){
        axes.put(KeyCode.scroll.ordinal(), -amountY);
        return false;
    }
    
    @Override
    public String name(){
        return "Keyboard";
    }
    
    @Override
    public DeviceType type(){
        return DeviceType.keyboard;
    }
}
