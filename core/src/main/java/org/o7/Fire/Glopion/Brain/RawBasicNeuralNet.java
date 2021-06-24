package org.o7.Fire.Glopion.Brain;

import Atom.Struct.PoolObject;
import arc.util.pooling.Pool;

import java.util.Arrays;

public class RawBasicNeuralNet implements RawNeuralNet, Pool.Poolable, PoolObject.Object {
	public int[] raw;
	public int[] output;
	public NeuralFunction function = NeuralFunction.Identity;
	
	public RawBasicNeuralNet(int[] structure, int inputSize){
		this.output = structure;
		raw = new int[RawNeuralNet.needRaw(inputSize, structure)];
		NeuralFunction.assignRandom(raw);
	}
	
	public RawBasicNeuralNet(int[] raw, int[] structure) {
		this.raw = raw;
		this.output = structure;
	}
	
	public RawBasicNeuralNet setFunction(NeuralFunction function) {
		this.function = function;
		return this;
	}
	
	@Override
	public int activation(int d) {
		return function.process(d);
	}
	
	@Override
	public int size() {
		return output.length;
	}
	
	@Override
	public int getOutput(int index) {
		return output[index];
	}
	
	@Override
	public int getRaw(int index) {
		return raw[index];
	}
	
	@Override
	public void reset() {
		NeuralFunction.assignRandom(raw);
		hashCode = 0;
	}
	
	protected int hashCode = 0;
	
	@Override
	public int hashCode() {
		if (hashCode != 0) return hashCode;
		return hashCode = Arrays.hashCode(raw);
	}
}
