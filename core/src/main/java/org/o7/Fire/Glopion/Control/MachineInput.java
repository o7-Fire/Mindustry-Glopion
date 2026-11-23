package org.o7.Fire.Glopion.Control;

import arc.Core;
import arc.Events;
import arc.Input;
import arc.input.KeyCode;
import arc.input.KeyboardDevice;
import arc.math.Mathf;
import org.o7.Fire.Glopion.Event.EventExtended;

public class MachineInput extends Input {

    private final MachineKeyboard machineKeyboard;

    public MachineInput(Input input){
        keyboard = machineKeyboard = new MachineKeyboard();
        inputMultiplexer.setProcessors(machineKeyboard);
        Events.run(EventExtended.InputUpdate.Pre, this::update);
        Events.run(EventExtended.InputUpdate.Post, this::postUpdate);
    }
    int mouseX, mouseY;
    @Override
    public int mouseX() {
        return mouseX;
    }
    
    @Override
    public int mouseX(int pointer) {
        mouseX += pointer;
        return mouseX = Mathf.clamp(mouseX,0, Core.graphics.getWidth() );
    }
  
    void update() {
        machineKeyboard.preUpdate();
    }
    
    void postUpdate() {
        machineKeyboard.postUpdate();
    }
    @Override
    public int deltaX() {
        return 0;
    }
    
    @Override
    public int deltaX(int pointer) {
        return 0;
    }
    
    @Override
    public int mouseY() {
        return mouseY;
    }
    
    @Override
    public int mouseY(int pointer) {
        mouseY += pointer;
        return mouseY = Mathf.clamp(mouseY,0, Core.graphics.getHeight());
    }
    
    @Override
    public int deltaY() {
        return 0;
    }
    
    @Override
    public int deltaY(int pointer) {
        return 0;
    }
    
    @Override
    public boolean isTouched() {
        return this.keyDown(KeyCode.mouseLeft) || this.keyDown(KeyCode.mouseRight);
    }
    
    @Override
    public boolean justTouched() {
        return this.keyTap(KeyCode.mouseLeft) || this.keyTap(KeyCode.mouseRight);
    }
    
    @Override
    public boolean isTouched(int pointer) {
        return false;
    }
    
    @Override
    public long getCurrentEventTime() {
        return 0;
    }
}
