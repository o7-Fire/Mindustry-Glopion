package org.o7.Fire.Glopion.Brain.Observation;

import mindustry.Vars;
import mindustry.core.World;
import mindustry.gen.Player;
import mindustry.world.Tile;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.o7.Fire.Glopion.Brain.Observation.Spatial.SpatialInformationExtractor;


import java.util.Arrays;

import static mindustry.Vars.world;

public class PlayerObservation implements Encodable, ObservationSpace<PlayerObservation> {
    protected Player player;
    protected static INDArray def = Nd4j.create(1);
    public int radiusObservation, diameterObservation;
    public static SpatialInformationExtractor spatialInformationExtractor = new SpatialInformationExtractor();
    public static float[] spatialMin, spatialMax;
    static {
        generateSpatialRange();
    }
    public final float[] empty = new float[spatialInformationExtractor.size()];
    public static void generateSpatialRange(){
        spatialMax = spatialInformationExtractor.getHigh();
        spatialMin = spatialInformationExtractor.getLow();
  
    }
    protected INDArray low, high;
    protected final float[][][] tileTensor, minValue, maxValue;
    public PlayerObservation(Player player, int radiusObservation){
        this.player = player;
        this.radiusObservation = radiusObservation;
        this.diameterObservation = radiusObservation + radiusObservation;
        tileTensor = generateArrayFloat();
        minValue = generateArrayFloat();
        maxValue = generateArrayFloat();
        fillTensor(spatialMin,minValue);
        fillTensor(spatialMax,maxValue);
        low = Nd4j.create(minValue);
        high = Nd4j.create(maxValue);
    }
    public void fillTensor(float[] arr, float[][][] tensor){
        for (float[][] matrix : tensor){
            for (int i = 0, matrixLength = matrix.length; i < matrixLength; i++) {
               matrix[i] = arr;
            }
        }
    }
    public float[][][] generateArrayFloat(){
        return new float[getShape()[0]][getShape()[1]][getShape()[2]];
    }
    //TODO define this or do magic
    @Override
    public double[] toArray() {
      return null;
    }
    
    @Override
    public boolean isSkipped() {
        return false;
    }
    long lastState = 0;
    public void capture(){
 
       
        long result = 0;
        
        for (int x = -radiusObservation; x < radiusObservation; x++) {
            int currentX = Vars.player.tileX();
            int currentY = Vars.player.tileY();
            for (int y = -radiusObservation; y < radiusObservation; y++) {
                int indexX = x + radiusObservation , indexY = y + radiusObservation ;
                currentX = currentX + x;
                currentY = currentY + y;
                spatialInformationExtractor.read(World.unconv(currentX), World.unconv(currentY), tileTensor[indexX][indexY]);
                result += Arrays.hashCode(tileTensor[indexX][indexY]);
            }
        }
        lastState = result;
    }
    INDArray last = null;
    @Override
    public INDArray getData() {
        long last = lastState;
        capture();
        if(lastState == last && this.last != null)
            return this.last;
        return this.last = Nd4j.create(tileTensor);
    }
    
    @Override
    public Encodable dup() {
        return new PlayerObservation(player, radiusObservation);
    }
    
    @Override
    public String getName() {
        return "Player Observation";
    }
    
    @Override
    public int[] getShape() {
        return new int[]{diameterObservation, diameterObservation, spatialInformationExtractor.size()};
    }
    
    @Override
    public INDArray getLow() {
        return def;
    }
    
    @Override
    public INDArray getHigh() {
        return def;
    }
    
}
