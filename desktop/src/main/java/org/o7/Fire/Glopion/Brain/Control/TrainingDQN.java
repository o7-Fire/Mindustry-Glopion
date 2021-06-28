package org.o7.Fire.Glopion.Brain.Control;

import arc.Core;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.o7.Fire.Glopion.Brain.MDP.PlayerDiscreteMDP;
import org.o7.Fire.Glopion.Brain.Observation.PlayerObservation;
import org.o7.Fire.Glopion.Brain.State.NativeSingleplayer;
import org.o7.Fire.Glopion.Control.NativeControl;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.GlopionCore;

import java.io.IOException;

import static org.o7.Fire.Glopion.Brain.DQN.Controller.getConfig;
import static org.o7.Fire.Glopion.Brain.DQN.Controller.getDQN;

public class TrainingDQN implements Experimental {
    volatile boolean alreadyDoIt = false;
    public static String path = "mindustry-player-dqn.zip";
    @Override
    public void run() {
        if(alreadyDoIt){
            Vars.ui.showInfo("Training underway");
            return;
        }
        alreadyDoIt = true;
        Threads.daemon("DQN Training",()->{
        DQN dqn = null;
        try {
            dqn = DQN.load(path);
            Log.info("Loaded: " + path);
        }catch(IOException e){
        
        }
       
        final QLearningConfiguration Qconifg = getConfig(250 * 4 * 120, 5);
        final DQNDenseNetworkConfiguration conf = getDQN();
        final NativeSingleplayer nativeSingleplayer = new NativeSingleplayer();
        final PlayerDiscreteMDP mdp = new PlayerDiscreteMDP(nativeSingleplayer, new NativeControl(Core.input), nativeSingleplayer);
        final QLearningDiscreteDense<NativeSingleplayer> network;
        if(dqn != null){
            network = new QLearningDiscreteDense(mdp,dqn,Qconifg);
        }else {
            network = new QLearningDiscreteDense(mdp, conf, Qconifg);
        }
        
            //start the training
            try {
            network.train();
            mdp.close();
        
                network.getPolicy().save(path);
                Log.info("Saved: " + path);
            }catch(Exception e){
                e.printStackTrace();
            }
            alreadyDoIt = false;
        });
    
    }
}
