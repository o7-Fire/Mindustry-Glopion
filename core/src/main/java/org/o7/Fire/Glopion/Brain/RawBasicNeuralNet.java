package org.o7.Fire.Glopion.Brain;

import java.util.Arrays;

public class RawBasicNeuralNet implements RawNeuralNet {
	public int[] raw;
	public int[] output;
	public NeuralFunction function;
	

	
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
	
	protected int hashCode = 0;
	
	@Override
	public int hashCode() {
		if (hashCode != 0) return hashCode;
		return hashCode = Arrays.hashCode(raw);
	}
}
