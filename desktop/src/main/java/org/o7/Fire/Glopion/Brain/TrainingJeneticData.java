package org.o7.Fire.Glopion.Brain;

import Atom.File.FileUtility;
import Atom.Time.Time;
import Atom.Utility.Meth;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import com.aparapi.Kernel;
import com.google.gson.Gson;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrainingJeneticData extends Kernel {
    public static final ArrayList<MachineRecorder> record = new ArrayList<>(), recordTest = new ArrayList<>();
    public static final Gson gson = new Gson();
    public static final ArrayList<Time> measurementEval = new ArrayList<>();
    public static int[][] inputRecord_$constant$, outputRecord_$constant$;
    public static JeneticNetworkPool neuralNetPool;
    public static int[] structure_$constant$;
    public static int parameterSize;
    public static Factory<Genotype<IntegerGene>> geneFactory;
    
    public static void main(String[] args) throws Throwable {
        if (args.length == 0) throw new NullPointerException("No f****ing arguments");
        Random.seed(123);
        File datasets = new File(args[0]);
        System.out.println("Loading: " + datasets.getAbsolutePath());
        if (!datasets.exists()) throw new FileNotFoundException(datasets.getAbsolutePath());
        loadRecord(datasets);
        distributeDataset();
        MachineRecorder sample = recordTest.get(0);
        System.out.println("Input Size: " + sample.getInputSize());
        System.out.println("Output Size: " + sample.getOutputSize());
        structure_$constant$ = new int[]{40, 40, 30, sample.getOutputSize()};
        parameterSize = RawNeuralNet.needRaw(sample.getInputSize(), structure_$constant$);
        neuralNetPool = new JeneticNetworkPool(structure_$constant$);
        geneFactory = Genotype.of(IntegerChromosome.of(Integer.MIN_VALUE, Integer.MAX_VALUE, parameterSize));
        boolean headless = GraphicsEnvironment.isHeadless() && System.console() != null;
        if (headless){
            Pool.parallelAsync = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setName(t.getName() + "-Atomic-Executor");
                t.setDaemon(true);
                return t;
            });
        }
        int[] structure = new int[structure_$constant$.length + 1];
        structure[0] = sample.getInputSize();
        int index=1;
        for (int i : structure_$constant$){
            structure[index] = structure_$constant$[index-1];
            index++;
        }
        System.out.println("Parameters: " + parameterSize);

        training();
    }
    
    public static double eval(Genotype<IntegerGene> h) {
        JeneticNeuralNetwork neuralNetwork = neuralNetPool.obtain();
        neuralNetwork.set(h.chromosome());
        Time time = new Time(TimeUnit.MILLISECONDS);
        ArrayList<Long> error = new ArrayList<>();
        for (MachineRecorder m : record) {
            int[][] input = m.getInput();
            for (int j = 0, inputLength = input.length; j < inputLength; j++) {
                int[] in = input[j];
                error.add((long) neuralNetwork.error(in, m.getOutput()[j]));
            }
        }
        measurementEval.add(time.elapsed());
        return Meth.avg(error);
    }
    
    public static double avg(long... arr) {
        long sum = 0;
        int length = 0;
        for (long l : arr) {
            sum += l;
            length++;
        }
        return (double) sum / length;
    }
    
    public static void saveArray(String name, int... array) {
        if(!name.endsWith(".json"))
            name = name + ".json";
        File file = new File(name).getAbsoluteFile();
        System.out.println("Saving: " + file.getAbsolutePath());
        FileUtility.write(file, gson.toJson(array).getBytes());
    }
    
    public static void saveResult(EvolutionResult<IntegerGene, Double> evolutionResult){
        double score = test(evolutionResult);
        System.out.println("Generation: " + evolutionResult.generation() + ", Fitness: " + evolutionResult.bestFitness() + ", Test Score: " + score);
        String name = "Generation-" + evolutionResult.generation() + "-Fitness-"+ evolutionResult.bestFitness() + "-Score-" + score;
        Chromosome<IntegerGene> chromosome = evolutionResult.bestPhenotype().genotype().chromosome();
        int[] n0 = new int[chromosome.length()];
        for (int i = 0; i < n0.length; i++) {
            n0[i] = chromosome.get(i).intValue();
        }
        saveArray(name,n0);
    }
    
    public static double test(EvolutionResult<IntegerGene, Double> result) {
        JeneticNeuralNetwork neuralNetwork = neuralNetPool.obtain();
        Genotype<IntegerGene> h = result.bestPhenotype().genotype();
        neuralNetwork.set(h.chromosome());
        ArrayList<Long> error = new ArrayList<>();
        for (MachineRecorder m : record) {
            int[][] input = m.getInput();
            for (int j = 0, inputLength = input.length; j < inputLength; j++) {
                int[] in = input[j];
                error.add((long) neuralNetwork.error(in, m.getOutput()[j]));
            }
        }
        return Meth.avg(error);
    }
    
    public static void training() {
        System.out.println("Using: " + (Runtime.getRuntime().availableProcessors() - 1) + " Processor");
        Engine<IntegerGene, Double> engine = Engine//
                .builder(TrainingJeneticData::eval, geneFactory)//
                .optimize(Optimize.MINIMUM)//
                .executor(Pool.parallelAsync)//
                .build();
        List<EvolutionResult<IntegerGene, Double>> arrayListCapped = Collections.synchronizedList(new ArrayListCapped<>(500));
        final double[] integer = new double[]{100000};
        long murdered = engine
                .stream()//
                .limit(Limits.byExecutionTime(Duration.ofHours(3)))//
                //.limit(Limits.bySteadyFitness(50 * 10))//
                .peek(s -> System.out.println("Generation: " + s.generation() + ",Best Fitness: " + s.bestFitness() + ",Worse Fitness: " + s.worstFitness()))//
                .filter(s -> integer[0] <= s.bestFitness())//
                .peek(s ->  integer[0] = (s.bestFitness()))//
                .peek(TrainingJeneticData::saveResult)
                .peek(arrayListCapped::add)//
                .count();
        System.out.println("Finish training, testing thing");
        printMeasure();
        EvolutionResult<IntegerGene, Double>[] survivor = arrayListCapped.toArray(new EvolutionResult[]{});
        HashMap<EvolutionResult<IntegerGene, Double>, Double> score = new HashMap<>();
        for (EvolutionResult<IntegerGene, Double> e : survivor) {
            score.put(e, test(e));
        }
        Arrays.sort(survivor, Comparator.comparing(score::get));
        System.out.println("Fitness n0: " + survivor[0].bestFitness() + ", Test Score: " + score.get(survivor));
        System.out.println("Fitness n100: " + survivor[survivor.length - 1].bestFitness() + ", Test Score: " + score.get(survivor.length - 1));
        EvolutionResult<IntegerGene, Double> n0Result = survivor[0], n100Result = survivor[survivor.length - 1];
        int[] n0 = new int[n0Result.bestPhenotype().genotype().chromosome().length()];
        for (int i = 0; i < n0.length; i++) {
            n0[i] = n0Result.bestPhenotype().genotype().chromosome().get(i).intValue();
        }
        System.out.println("Congratulation here your n0");
        
        n0 = null;
        n0Result = null;
        int[] n100 = new int[n100Result.bestPhenotype().genotype().chromosome().length()];
        for (int i = 0; i < n100.length; i++) {
            n100[i] = n100Result.bestPhenotype().genotype().chromosome().get(i).intValue();
        }
        System.out.println("Congratulation here your n100");
        System.out.println("Total Murdered: " + murdered);
        
    }
    
    public static void printMeasure() {
        
        long[] measure = new long[measurementEval.size()];
        for (int i = 0, measurementSize = measurementEval.size(); i < measurementSize; i++) {
            Time m = measurementEval.get(i);
            measure[i] = m.getSrc();
        }
        System.out.println("Size Sample: " + measure.length);
        System.out.println("Average Evaluation Time: " + TrainingJeneticData.avg(measure) + " ms");
        measurementEval.clear();
    }
    
    public static void distributeDataset() {
        int size = record.size() / 4;
        for (int i = 0; i < size; i++) {
            
            recordTest.add(record.remove(Random.getInt(0, record.size() - 1)));
        }
        
        System.out.println(size + " Dataset for final test");
        System.out.println(record.size() + " Dataset for training");
        MachineRecorder sample = record.get(0);
        int mergedSize = record.size() * sample.getOutput().length;
        inputRecord_$constant$ = new int[mergedSize][sample.getInput()[0].length];
        outputRecord_$constant$ = new int[mergedSize][sample.getOutput()[0].length];
        int index = 0;
        for (int i = 0; i < record.size(); i++) {
            int[][] input = record.get(i).getInput(), output = record.get(i).getOutput();
            for (int j = 0, inputLength = input.length; j < inputLength; j++) {
                int[] vectorInput = input[j], vectorOutpput = output[j];
                inputRecord_$constant$[index] = vectorInput;
                outputRecord_$constant$[index] = vectorOutpput;
                index++;
            }
        }
        System.out.println("Total Dataset Matrix: " + index);
    }
    
    public static void loadRecord(File datasets) {
        File[] records = datasets.listFiles((dir, name) -> name.startsWith("Record-") && name.endsWith(".json"));
        for (File f : records) {
            try {
                MachineRecorder roce = gson.fromJson(Files.readString(f.toPath()), MachineRecorder.class);
                record.add(roce);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        System.out.println("Loaded: " + record.size() + " Set of Dataset");
    }
    
    @Override
    public void run() {
    
    }
}
