package org.o7.Fire.Glopion.Control;

import Atom.File.FileUtility;
import Atom.Struct.PoolObject;
import Atom.Utility.Meth;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import arc.Core;
import arc.func.Floatp;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.ScreenUtils;
import arc.util.Strings;
import arc.util.io.Writes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.AirBlock;
import org.o7.Fire.Glopion.Module.Module;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;
import org.o7.Fire.Glopion.Module.WorldModule;
import org.o7.Fire.Glopion.Patch.Translation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class MachineRecorder implements Module, WorldModule, Serializable {
    public static final VectorOutput measure = new VectorOutput(new int[0]) {
        @Override
        public void write(int b) throws IOException {
            index++;
        }
    };
    public static final Writes measureWriter = new Writes(measure);
    protected static final long delay = 250;//human visual response time
    protected static final long delaySave = 12 * 1000;
    protected static final int temporaryArraySize = (int) (delaySave / delay);
    public static int maxView = 6;
    public static HashMap<Integer, String> color = new HashMap<>();
    public static PoolObject<Seq<Entityc>> poolEntity = new SeqPool<>();
    public static int precision = 100;
    public static StateCalculatorReader machineReader = new StateCalculatorReader();
    public static Writes writes = new Writes(machineReader);
    public static int environmentInformationSize = 200;
    protected static File workingDir = new File("cache/" + Utility.getDate() + "-Player-Recorder");
    protected static Gson gson = new GsonBuilder().create();

    static {
        Log.info("Recorder Path: @", workingDir.getAbsolutePath());
    }

    protected transient final VectorOutput vectorWriter = new VectorOutput(new int[0]);
    protected transient final Writes vectorWrites = new Writes(vectorWriter);
    protected transient Player player;
    protected int[][] input, output;
    protected int inputSize, outputSize;
    protected transient int loggingIndex, fileIndex;
    protected transient long nextCapture = System.currentTimeMillis() + delaySave;
    protected transient boolean sensoryCapture = true, stop = false;
    
    public MachineRecorder() {
    
    }
    
    public MachineRecorder(Player p) {
        player = p;
        nextCapture  = System.currentTimeMillis() + delaySave + Random.getInt(10000);
        //resetArray();
    }
    
    public static void visualizeColorized(int[] vector, StringBuilder sb) {
        for (int y : vector) {
            if (!color.containsKey(y)) color.put(y, Translation.getRandomHexColor());
            sb.append(color.get(y)).append("■");
        }
        sb.append("[white]\n");
    }

    public static StringBuilder visualizeColorized(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] x : matrix) {
            visualizeColorized(x, sb);
        }
        return sb;
    }
    
    public static String visualize(int[][] matrix) {
        String h = Arrays.deepToString(matrix).replace("],", System.getProperty("line.separator"));
        h = " " + h.substring(1, h.length() - 2);
        return h;
    }
    
    public static int toInt(double f) {
        return (int) (f * precision);
    }
    
    public static int toInt(float f) {
        return (int) (f * precision);
    }
    
    public static int toInt(boolean o) {
        return o ? 1 : 0;
    }
    
    public static int hashState(int[] h, int state) {
        for (int element : h)
            state = 3 * state + element;
        
        return state;
    }
    
    public static int tileState(Tile t) {
        if (t == null) return 0;
        int state = 0;
        state = hashState(new int[]{t.getTeamID(),//
                toInt(t.getFlammability()),//
                toInt(t.breakable()),//
                toInt(t.passable()), //
                toInt(t.dangerous()), //
                toInt(t.synthetic()), //
                toInt(t.solid()), //
                toInt(t.staticDarkness())//
        }, 0);
        
        if (t.build != null){
            machineReader.state = state;
            t.build.writeAll(writes);
            return machineReader.state;
        }
        
        if (!(t.overlay() instanceof AirBlock)){
            state = hashState(new int[]{t.overlayID()}, state);
        }else{
            state = hashState(new int[]{t.floorID()}, state);
        }
        return state;
    }
    
    public static int renderTile(Tile t) {
        if (t == null) return 0;
        Seq<Entityc> seq1 = poolEntity.obtain();
        try {
            if (seq1.isEmpty()) Groups.bullet.intersect(t.worldx(), t.worldy(), 2, 2, seq1::add);
        }catch(Exception ignored){}
        try {
            if (seq1.isEmpty()) Groups.unit.intersect(t.worldx(), t.worldy(), 1, 1, seq1::add);
        }catch(Exception ignored){}
        int render = 0;
        if (!seq1.isEmpty()){
            Entityc entity = seq1.remove(0);
            machineReader.state = 0;
            entity.write(writes);
            render = machineReader.state;
        }
        
        poolEntity.free(seq1);
        if (render != 0) return render;
        return tileState(t);
    }
    
    public static int[] worldDataToVisualVector(Tile[][] rawMatrix) {
        int[] matrix = new int[rawMatrix.length * rawMatrix.length];
        int pointer = 0;
        for (Tile[] x : rawMatrix) {
            for (Tile y : x) {
                matrix[pointer++] = renderTile(y);
            }
        }
        return matrix;
    }
    
    public static String visualize(Object[][] matrix) {
        return " " + Arrays.deepToString(matrix).replace("],", System.getProperty("line.separator")).substring(1);
    }
    public float[] worldDataVector = new float[getSize()], compiledVector = null;
    public int getSize(){
        return diameter*diameter;
    }
    public int compiledIndex = getSize();
    public int getCompiledSize(){
        return getSize()+12;
    }
    
    public void enviromentInformation(){
        ScreenUtils.getFrameBufferPixels(false);
        compiledIndex = getSize();
        incrementAssignCompiledIndex(()-> player.unit().vel().getX());
        incrementAssignCompiledIndex(()-> player.unit().vel().getY());
        incrementAssignCompiledIndex(()-> player.unit().closestEnemyCore().tile().dst(player.tileOn()));
        incrementAssignCompiledIndex(()-> player.unit().closestCore().tile().dst(player.tileOn()));
        incrementAssignCompiledIndex(()-> player.unit().closestEnemyCore().tile().dst(player.tileOn()));
    }
    public void incrementAssignCompiledIndex(Floatp fc){
        compiledVector[compiledIndex] = 0;
        compiledIndex++;
        try {
            compiledVector[compiledIndex] = fc.get();
        }catch(Throwable ignored){}
    }
    public float[] compiledVector(){
        if(compiledVector == null)
            compiledVector = new float[getCompiledSize()];
        worldDataToVisualFlat(getWorldData(maxView),compiledVector);
        enviromentInformation();
        return compiledVector;
    }
    public float[] worldDataToVisualFlat(Tile[][] rawMatrix) {
        worldDataToVisualFlat(rawMatrix, worldDataVector);
        return worldDataVector;
    }
    public static void worldDataToVisualFlat(Tile[][] rawMatrix, float[] vector) {
        int index = 0;
        for (int j = 0, rawMatrixLength = rawMatrix.length; j < rawMatrixLength; j++) {
            Tile[] x = rawMatrix[j];
            try {
                for (int i = 0, xLength = x.length; i < xLength; i++) {
                    Tile y = x[i];
                    vector[index] = Meth.normalize(Integer.MAX_VALUE, Integer.MIN_VALUE, renderTile(y));
                    index++;
                }
            }catch(ArrayIndexOutOfBoundsException e){
                throw new ArrayIndexOutOfBoundsException("Vector length is: " + vector.length + ", while matrix length is: " + rawMatrix.length + ", " + x.length +". " + e.getMessage());
            }
        
        }
    }
    public static float[][] worldDataToVisualF(Tile[][] rawMatrix) {
        float[][] matrix = new float[rawMatrix.length][rawMatrix.length];
        int xPointer = 0, yPointer = 0;
        for (Tile[] x : rawMatrix) {
            for (Tile y : x) {
                matrix[xPointer][yPointer++] = Meth.normalize(Integer.MAX_VALUE, Integer.MIN_VALUE, renderTile(y));
            }
            yPointer = 0;
            xPointer++;
        }
        return matrix;
    }
    public static int[][] worldDataToVisual(Tile[][] rawMatrix) {
        int[][] matrix = new int[rawMatrix.length][rawMatrix.length];
        int xPointer = 0, yPointer = 0;
        for (Tile[] x : rawMatrix) {
            for (Tile y : x) {
                matrix[xPointer][yPointer++] = renderTile(y);
            }
            yPointer = 0;
            xPointer++;
        }
        return matrix;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
    
    public File getCurrentFile() {
        return new File(workingDir, "Record-" + fileIndex + "-" + player.id + "-" + Strings.stripColors(Vars.state.map.name().replace("-", "")) + "-" + (Vars.state.rules.pvp ? "PvP" : (Vars.state.rules.attackMode ? "Attack" : "Survival")) + ".json");
    }
    
    public void resetArray() {
        //EventType.BlockBuildBeginEvent
        loggingIndex = 0;
        inputSize = captureSensory().length;
        outputSize = captureAction().length;
        input = new int[temporaryArraySize][inputSize];
        output = new int[temporaryArraySize][outputSize];
    }
    
    public int getEnvironmentInformationSize() {
        if (environmentInformationSize != 0) return environmentInformationSize;
        int size = 0;
        size = size + Vars.content.items().size;
        size++;//tile this
        size++;//distance to core
        size++;//velocity x
        size++;//velocity y
        //player
        player.writeSync(measureWriter);
        size = size + measure.index;
        measure.reset();
        //player unit
        player.unit().writeSync(measureWriter);
        size = size + measure.index;
        measure.reset();
        //player tile
        /*
        player.tileOn().build.writeAll(measureWriter);
        size = size + measure.index;
        measure.reset();
         */
        //item core
        
        return size;
    }

    public int[] getEnvironmentInformation() {
        int[] vector = new int[getEnvironmentInformationSize()];
        getEnvironmentInformation(vector);
        return vector;
    }
    
    public void getEnvironmentInformation(int[] vector) {
        vectorWriter.reset(vector);
        int index = vectorWriter.index;
        
        for (Item item : Vars.content.items()) {
            vector[index] = player.closestCore().items().get(item);
            index++;
        }
         /*
        vector[index++] = (int) (player.unit().rotation()*10);
        vector[index++] = toInt(player.shooting);
        vector[index++] = player.team().id;
        vector[index++] = toInt(player.boosting);
        */
        vector[index++] = (int) player.closestCore().tile().dst(player.tileOn());
        //vector[index++] = toInt(player.isBuilder());
        //vector[index++] = player.unit().type == null ? 0 : player.unit().type.id;
        vector[index++] = tileState(player.tileOn());
        vector[index++] = toInt(player.unit().vel().getX());
        vector[index++] = toInt(player.unit().vel().getY());
        vectorWriter.index = index;
        player.writeSync(vectorWrites);//Player
        player.unit().writeSync(vectorWrites);//Player Unit
        //if(player.tileOn().build != null) player.tileOn().build.writeAll(writes);//Player tile on build
    }
    
    public int[] getCompiledEnvironmentInformation() {
        return getCompiledEnvironmentInformation(getWorldData(maxView));
    }
    
    public int[] getCompiledEnvironmentInformation(Tile[][] worldView) {
        int size = getEnvironmentInformationSize();
        for (Tile[] tiles : worldView) {
            size = size + tiles.length;
        }
        int[] vector = new int[size];
        int index = getEnvironmentInformationSize() - 1;
        for (Tile[] tiles : worldView) {
            for (Tile t : tiles) {
                vector[index] = renderTile(t);
                index++;
            }
        }
        getEnvironmentInformation(vector);
        return vector;
    }
    
    public float[][] getStubData(int radius) {
        int diameter = radius + radius;
        float[][] matrix = new float[diameter][diameter];
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix.length; y++) {
                matrix[x][y] = Float.parseFloat(x + "." + y);
            }
        }
        return matrix;
    }
    int diameter = maxView + maxView;
    Tile[][] matrix = new Tile[diameter][diameter];
    public Tile[][] getWorldData(int radius) {
        int x = Vars.player.tileX(), y = Vars.player.tileY();
        for (int yPointer = radius; yPointer > -radius; yPointer--) {
            for (int xPointer = radius; xPointer > -radius; xPointer--) {
                
                matrix[xPointer + radius - 1][yPointer + radius - 1] = Vars.world.tile(x + xPointer, y + yPointer);
                
            }
        }
        return matrix;
    }
    
    public int[] captureSensory() {
        return getCompiledEnvironmentInformation();
    }
    
    public int[] captureAction() {
        
        return new int[]{(int) (player.unit().vel.y * precision), /** {@link Actions#MoveVertical} */
                (int) (player.unit().vel.x * precision), /** {@link Actions#MoveHorizontal} */
                (int) (player.unit().rotation * precision), /** {@link Actions#Rotate} */
                player.shooting ? 1 : 0, /** {@link Actions#Shooting} */
                player.unit().mining() ? 1 : 0, /** {@link Actions#Mining} */
                player.boosting() ? 1 : 0, /** {@link Actions#Boosting} */
                player.unit().isBuilding() ? 1 : 0, /** {@link Actions#Building} */};
    }
    
    public boolean timePass() {
        if (System.currentTimeMillis() > nextCapture){
            nextCapture = System.currentTimeMillis() + delay;
            return true;
        }
        return false;
    }
    
    @Override
    public void onPlayerLeave(Player player) {
        if (player.id == this.player.id){
            stop = true;
            Log.info("player @ leave, stopping logging", player.name);
        }
    }
    
    @Override
    public int hashCode() {
        return player.id;
    }
    
    @Override
    public void onDisconnect() {
        stop = true;
        
    }
    
    public int[][] getInput() {
        return input;
    }
    
    public int[][] getOutput() {
        return output;
    }
    
    public int getInputSize() {
        return inputSize;
    }
    
    public int getOutputSize() {
        return outputSize;
    }
    
    @Override
    public void update() {
        if (!Vars.state.isPlaying()) stop = true;
        if (Groups.player.getByID(player.id) == null) onPlayerLeave(player);
        
        if (timePass() || stop){
            if (!stop){
                if (sensoryCapture){
                    input[loggingIndex] = captureSensory();
                }else{
                    output[loggingIndex] = captureAction();
                    loggingIndex++;
                }
                sensoryCapture = !sensoryCapture;
            }
            if (loggingIndex >= temporaryArraySize - 1 || (stop && input[0] != null)){//last array just achieved
                String s = toString();
                Pool.submit(() -> {
                    Log.debug("Saving @", getCurrentFile().getAbsolutePath());
                    FileUtility.write(getCurrentFile(), s.getBytes());
                });
                fileIndex++;
                resetArray();
            }
        }
        if (stop) ModuleRegisterer.modulesSet.remove(this);
        
    }
    
    private static class SeqPool<T> extends PoolObject<Seq<T>> {
        @Override
        protected void reset(Seq<T> object) {
            object.clear();
        }
        
        @Override
        protected Seq<T> newObject() {
            return new Seq<>(5);
        }
    }
    
    
}
