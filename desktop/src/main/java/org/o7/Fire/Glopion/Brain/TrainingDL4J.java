package org.o7.Fire.Glopion.Brain;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.jfree.data.general.Dataset;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.io.File;
import java.io.FileNotFoundException;

import static org.o7.Fire.Glopion.Brain.TrainingJeneticData.recordTest;

public class TrainingDL4J {
    public static void main(String[] args)throws Throwable {
        File datasets = new File(args[0]);
        System.out.println("Loading: " + datasets.getAbsolutePath());
        if (!datasets.exists()) throw new FileNotFoundException(datasets.getAbsolutePath());
        TrainingJeneticData.loadRecord(datasets);
        MachineRecorder sample = recordTest.get(0);
        MultiLayerConfiguration multiLayerConfiguration = new NeuralNetConfiguration.Builder().//
                weightInit(WeightInit.XAVIER_UNIFORM)//
                .activation(Activation.RELU)//
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)//
                .updater(new Sgd(0.05))//
                .list()//
                .layer(new DenseLayer.Builder().nIn(sample.getInputSize()).nOut(250).build())//
                .layer(new DenseLayer.Builder().nOut(250).nOut(250).build())//
                .layer(new DenseLayer.Builder().nOut(250).nOut(250).build())//
                .layer(new OutputLayer.Builder().nOut(sample.getOutputSize()).nIn(250).build())//
                .backpropType(BackpropType.Standard)//
                .build();
       
    }
}
