package org.o7.Fire.Glopion.Brain.MDP;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.o7.Fire.Glopion.Brain.Observation.PlayerObservation;

public class PlayerDiscreteMDP implements MDP<PlayerObservation, Integer, DiscreteSpace> {
    
    @Override
    public ObservationSpace<PlayerObservation> getObservationSpace() {
        return null;
    }
    
    @Override
    public DiscreteSpace getActionSpace() {
        return null;
    }
    
    @Override
    public PlayerObservation reset() {
        return null;
    }
    
    @Override
    public void close() {
    
    }
    
    @Override
    public StepReply<PlayerObservation> step(Integer integer) {
        return null;
    }
    
    @Override
    public boolean isDone() {
        return false;
    }
    
    @Override
    public MDP<PlayerObservation, Integer, DiscreteSpace> newInstance() {
        return null;
    }
}
