package org.o7.Fire.Glopion.Brain.DQN;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.RmsProp;

public class Controller {
    
    public static QLearning.QLConfiguration.QLConfigurationBuilder getConfig(){
        return QLearning.QLConfiguration.builder().seed(123)                //Random seed (for reproducability)
                .maxEpochStep(200)        // Max step By epoch
                .maxStep(15000)           // Max step
                .expRepMaxSize(150000)    // Max size of experience replay
                .batchSize(128)            // size of batches
                .targetDqnUpdateFreq(500) // target update (hard)
                .updateStart(120)          // num step noop warmup
                .rewardFactor(0.01)       // reward scaling
                .gamma(0.99)              // gamma
                .errorClamp(1.0)          // /td-error clipping
                .minEpsilon(0.1f)         // min epsilon
                .epsilonNbStep(1000)      // num step for eps greedy anneal
                .doubleDQN(true);
    }
    public static void main(String[] args) {
        QLearning.QLConfiguration.QLConfigurationBuilder Config = getConfig();
        DQNFactoryStdDense.Configuration Net = DQNFactoryStdDense.Configuration.builder()//
                .l2(0)//
                .updater(new RmsProp(0.000025))//
                .numHiddenNodes(1500)//h
                .numLayer(5)//why yes
                .build();
    }
}
