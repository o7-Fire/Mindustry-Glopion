package org.o7.Fire.Glopion.Brain;



import java.io.Serializable;

//FUCKING RAW
public interface RawNeuralNet extends Serializable {
	
	static int needRaw(int input, int[] output) {
		int index = 0;
		for (int i : output) {//layer
			for (int i1 = 0; i1 < i; i1++) {//node
				for (int i2 = 0; i2 < input; i2++) {//input summation
					index++;//weight
				}
				index++;//bias
			}
			input = i;//previous output == new input
		}
		
		return index;
	}
	
	int activation(int d);
	
	default int[] process(int[] input) {
		final int[] index = new int[]{0};
		for (int i = 0; i < size(); i++) {
			int outputSize = getOutput(i);
			input = subProcess(input, outputSize, index);
		}
		return input;
	}
	
	//so slow
	default int[] subProcess(int[] array, int outputSize, int[] index) {
		int[] output = new int[outputSize];
		for (int i = 0; i < outputSize; i++) {//for node
			int node = 0;
			for (int v : array) {//node input summation
				node += v * getRaw(index[0]);//weight
				index[0] = index[0] + 1;
			}
			node += getRaw(index[0]);//bias
			index[0] = index[0] + 1;
			output[i] = activation(node);//activation
		}
		return output;
	}
	
	int size();
	
	int getOutput(int index);
	
	int getRaw(int index);
	
	void reset();
	
	default int error(int[] input, int[] expected) {
		int[] output = process(input);
		return NeuralFunction.loss(output, expected);
	}
}
