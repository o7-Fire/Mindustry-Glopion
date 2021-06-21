package org.o7.Fire.Glopion.Desktop.Control;

//Human and machine friendly
//index for machine
//Name for human
//No real operation here
public enum Control {
    NoOp,
    MoveVertical,
    MoveHorizontal,
    Rotate,
    Shoot,
    Mine,
    Respawn,
    ControlEntity,
    ControlBuilding,
    TapTile,
    RotateBlock,
    Command;
    public void doSomething(MachineControl control, float value){
        ControlInterpreter.interpret(this,control,value);
    }
}
