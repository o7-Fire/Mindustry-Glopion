package org.o7.Fire.Glopion.Brain.MDP;

import mindustry.gen.Player;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.o7.Fire.Glopion.Brain.Observation.PlayerObservation;
import org.o7.Fire.Glopion.Brain.State.StateController;
import org.o7.Fire.Glopion.Control.InterfaceControl;

public class PlayerDiscreteMDP implements MDP<PlayerObservation, Integer, DiscreteSpace> {

    protected final PlayerObservation observation;
    protected final InterfaceControl interfaceControl;
    protected final DiscreteSpace discreteSpace;
    protected final StateController state;
    public PlayerDiscreteMDP(PlayerObservation observation, InterfaceControl interfaceControl, StateController stateController){
        this.state = stateController;
        this.observation = observation;
        this.interfaceControl = interfaceControl;
        this.discreteSpace = new DiscreteSpace(interfaceControl.getSize());
        
    }
 
    @Override
    public ObservationSpace<PlayerObservation> getObservationSpace() {
        return observation;
    }
    
    @Override
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }
    
    @Override
    public PlayerObservation reset() {
        state.reset();
        return observation;
    }
    
    @Override
    public void close() {
       state.close();
    }
    
    @Override
    public StepReply<PlayerObservation> step(Integer action) {
        interfaceControl.rawInput(action);
        state.nextStep();
        return new StepReply<>(observation,state.reward(),state.isDone(),state.info());
    }
    
    @Override
    public boolean isDone() {
        return state.isDone();
    }
    
    @Override
    public MDP<PlayerObservation, Integer, DiscreteSpace> newInstance() {
        System.out.println("Copying Instance: " + this.getClass().getSimpleName());
        return new PlayerDiscreteMDP(observation,interfaceControl, state);
    }
}
