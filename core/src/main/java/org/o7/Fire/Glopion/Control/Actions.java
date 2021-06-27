package org.o7.Fire.Glopion.Control;

//Human and machine friendly
//index for machine
//Name for human
//No real operation here
/** implement {@link mindustry.net.Administration.ActionType} */
public enum Actions {
    MoveVertical,
    MoveHorizontal,
    Rotate,
    Shooting,
    Mining,
    Boosting,
    Building;
    public void doSomething(MachineControl control, float value){
        ControlInterpreter.interpret(this,control,value);
    }
}
