package org.o7.Fire.Glopion.Brain.Control;

import arc.Core;
import arc.Input;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.async.Threads;
import mindustry.Vars;
import org.deeplearning4j.rl4j.learning.IEpochTrainer;
import org.deeplearning4j.rl4j.learning.ILearning;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.listener.TrainingListener;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.util.IDataManager;
import org.o7.Fire.Glopion.Brain.MDP.PlayerDiscreteMDPScreen;
import org.o7.Fire.Glopion.Brain.State.NativeSingleplayerScreen;
import org.o7.Fire.Glopion.Brain.State.NativeSingleplayerTensor;
import org.o7.Fire.Glopion.Control.MachineInput;
import org.o7.Fire.Glopion.Control.NativeControl;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.Internal.Interface;

import java.io.IOException;

import static org.o7.Fire.Glopion.Brain.DQN.Controller.getConfig;
import static org.o7.Fire.Glopion.Brain.DQN.Controller.getDQN;

public class TrainingDQN implements Experimental {
    volatile boolean alreadyDoIt = false;
    public static String path = "mindustry-player-dqn.zip";
    @Override
    public void run() {
        if(alreadyDoIt){
            Interface.showInfo("Training Underway");
            return;
        }
        if(!Vars.state.isPlaying()){
            Interface.showInfo("Go play a map");
            return;
        }
        alreadyDoIt = true;
        int speed = 4;
        Input original = Core.input;
        MachineInput machineInput = new MachineInput(original);
        Core.input = machineInput;
        Threads.daemon("DQN Training",()->{
        DQN dqn = null;
        try {
            dqn = DQN.load(path);
            Log.info("Loaded: " + path);
        }catch(IOException e){
        
        }
      
        Time.setDeltaProvider(() ->{
            float result = Core.graphics.getDeltaTime() * 60f * speed;
            return (Float.isNaN(result) || Float.isInfinite(result)) ? speed : Mathf.clamp(result, 0.0001f, (60f * speed) / 10f);
        });
        final QLearningConfiguration Qconifg = getConfig((int) (NativeSingleplayerScreen.undelta() * 20 * 30), 5);
        final DQNDenseNetworkConfiguration conf = getDQN();
        final NativeSingleplayerScreen nativeSingleplayer = new NativeSingleplayerScreen();
        final PlayerDiscreteMDPScreen mdp = new PlayerDiscreteMDPScreen(nativeSingleplayer, new NativeControl(Core.input), nativeSingleplayer);
        final QLearningDiscreteDense<NativeSingleplayerTensor> network;
        if(dqn != null){
            network = new QLearningDiscreteDense(mdp,dqn,Qconifg);
        }else {
            network = new QLearningDiscreteDense(mdp, conf, Qconifg);
        }
        
            //start the training
            try {
                network.addListener(new TrainingListener() {
                    @Override
                    public ListenerResponse onTrainingStart() {
                        Vars.ui.hudfrag.showToast("Training Started");
                        return null;
                    }
    
                    @Override
                    public void onTrainingEnd() {
        
                    }
    
                    @Override
                    public ListenerResponse onNewEpoch(IEpochTrainer trainer) {
                        Vars.ui.hudfrag.showToast("New Epoch: " + trainer.getEpochCount());
                        try {
                            network.getPolicy().save(path);
                        }catch(IOException e){
                        }
                        return null;
                    }
    
                    @Override
                    public ListenerResponse onEpochTrainingResult(IEpochTrainer trainer, IDataManager.StatEntry statEntry) {
                        Vars.ui.hudfrag.showToast("Epoch Reward: " + statEntry.getReward());
                        return null;
                    }
    
                    @Override
                    public ListenerResponse onTrainingProgress(ILearning learning) {
                        return null;
                    }
                });
            network.train();
            mdp.close();
                network.getPolicy().save(path);
                Log.info("Saved: " + path);
            }catch(Exception e){
                e.printStackTrace();
            }
            Time.setDeltaProvider(()-> {
                float result = Core.graphics.getDeltaTime() * 60f;
                return (Float.isNaN(result) || Float.isInfinite(result)) ? 1f : Mathf.clamp(result, 0.0001f, 60f / 10f);
            });
            Core.app.post(()->Core.input = original);
            alreadyDoIt = false;
        });
      
    }
}
