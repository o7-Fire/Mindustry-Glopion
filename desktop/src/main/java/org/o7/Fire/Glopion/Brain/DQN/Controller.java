package org.o7.Fire.Glopion.Brain.DQN;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.Nadam;

public class Controller {
    
    
    
    public static DQNDenseNetworkConfiguration getDQN(){
        return DQNDenseNetworkConfiguration.builder()
                .updater(new Nadam(Math.pow(10, -3.5)))
                .numHiddenNodes(40)
                .numLayers(6)
                .build();
    }
    
    public static QLearningConfiguration getConfig(int stepsPerEpoch, int maxGames){
        return QLearningConfiguration.builder()
                .seed(1L)
                .maxEpochStep(stepsPerEpoch)
                .maxStep(stepsPerEpoch * maxGames)
                .updateStart(0)
                .rewardFactor(1.0)
                .gamma(0.999)
                .errorClamp(1.0)
                .batchSize(16)
                .minEpsilon(0.0)
                .epsilonNbStep(128)
                .expRepMaxSize(128 * 16)
                .build();
    }
  
}
