package org.o7.Fire.Glopion.Brain;

import Atom.Utility.Pool;
import com.google.gson.Gson;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainingJeneticData {
    public static final ArrayList<MachineRecorder> record = new ArrayList<>(), recordTest = new ArrayList<>();
    public static final Gson gson = new Gson();
    public static JeneticNetworkPool neuralNetPool;
    public static int[] structure;
    public static int biasSize;
    public static Factory<Genotype<IntegerGene>> geneFactory;
    public static void main(String[] args) throws Throwable {
        if(args.length == 0)throw new NullPointerException("No f****ing arguments");
        File datasets = new File(args[0]);
        if(!datasets.exists())
            throw new FileNotFoundException(datasets.getAbsolutePath());
        loadRecord(datasets);
        distributeDataset();
        MachineRecorder sample = recordTest.get(0);
        System.out.println("Input Size: " + sample.getInputSize());
        System.out.println("Output Size" + sample.getOutputSize());
        structure = new int[]{40, 40, 30, sample.getOutputSize()};
        biasSize = RawNeuralNet.needRaw(sample.getInputSize(),structure);
        neuralNetPool = new JeneticNetworkPool( structure);
        geneFactory = Genotype.of(IntegerChromosome.of(Integer.MIN_VALUE,Integer.MAX_VALUE, biasSize));
        training();
    }
    public static int eval(Genotype<IntegerGene> h){
        JeneticNeuralNetwork neuralNetwork = neuralNetPool.obtain();
        neuralNetwork.set(h.chromosome());
        int error = 0;
        for (MachineRecorder m : record) {
            int[][] input = m.getInput();
            for (int j = 0, inputLength = input.length; j < inputLength; j++) {
                int[] in = input[j];
                error += neuralNetwork.error(in, m.getOutput()[j]);
            }
        }
        return error;
    }
    public static int test(EvolutionResult<IntegerGene, Integer> result){
        JeneticNeuralNetwork neuralNetwork = neuralNetPool.obtain();
        Genotype<IntegerGene> h = result.bestPhenotype().genotype();
        neuralNetwork.set(h.chromosome());
        int error = 0;
        for (MachineRecorder m : record) {
            int[][] input = m.getInput();
            for (int j = 0, inputLength = input.length; j < inputLength; j++) {
                int[] in = input[j];
                error += neuralNetwork.error(in, m.getOutput()[j]);
            }
        }
        return error;
    }
    public static void training(){
        System.out.println("Using: " + (Runtime.getRuntime().availableProcessors() - 1) + " Processor");
        Engine<IntegerGene, Integer> engine = Engine
                .builder(TrainingJeneticData::eval, geneFactory)
                .executor(Pool.parallelAsync)
                .build();
        List<EvolutionResult<IntegerGene, Integer>> arrayListCapped = Collections.synchronizedList(new ArrayListCapped<>(100));
        AtomicInteger integer = new AtomicInteger(0);
        long murdered = engine.stream()
                .limit(Limits.bySteadyFitness(10))
                .filter(s-> integer.get() <= s.bestFitness())
                .peek(s -> integer.set(s.bestFitness()))
                .peek(arrayListCapped::add).count();
        System.out.println("Finish training, testing thing");
        EvolutionResult<IntegerGene, Integer>[] survivor = arrayListCapped.toArray(new EvolutionResult[]{});
        HashMap<EvolutionResult<IntegerGene, Integer>, Integer> score = new HashMap<>();
        for (EvolutionResult<IntegerGene, Integer> e : survivor){
            score.put(e,test(e));
        }
        Arrays.sort(survivor,  Comparator.comparing(score::get));
        System.out.println("Fitness n0: " + survivor[0].bestFitness());
        System.out.println("Fitness n100: " + survivor[survivor.length-1].bestFitness());
        EvolutionResult<IntegerGene, Integer> n0Result = survivor[0], n100Result = survivor[survivor.length - 1];
        int[] n0 = new int[n0Result.bestPhenotype().genotype().chromosome().length()];
        for (int i = 0; i < n0.length; i++) {
            n0[i] = n0Result.bestPhenotype().genotype().chromosome().get(i).intValue();
        }
        System.out.println("Congratulation here your n0");
        System.out.println(Arrays.toString(n0));
        n0 = null;
        n0Result = null;
        int[] n100 = new int[n100Result.bestPhenotype().genotype().chromosome().length()];
        for (int i = 0; i < n100.length; i++) {
            n100[i] = n100Result.bestPhenotype().genotype().chromosome().get(i).intValue();
        }
        System.out.println("Congratulation here your n100");
        System.out.println(Arrays.toString(n100));
        System.out.println("Total Murdered: " + murdered);
    }
    public static void distributeDataset(){
        int size = record.size() / 4;
        for (int i = 0; i < size; i++) {
            recordTest.add(record.remove(0));
        }
        System.out.println(size + " Dataset for final test");
        System.out.println(record.size() + " Dataset for training");
    }
    public static void loadRecord(File datasets){
        File[] records = datasets.listFiles((dir, name) -> name.startsWith("Record-") && name.endsWith(".json"));
        for (File f : records){
            try {
                record.add(gson.fromJson(Files.readString(f.toPath()), MachineRecorder.class));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        System.out.println("Loaded: " + record.size() + " Dataset");
    }
}
