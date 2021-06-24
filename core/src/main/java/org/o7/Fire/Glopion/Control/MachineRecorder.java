package org.o7.Fire.Glopion.Control;

import Atom.File.FileUtility;
import Atom.Struct.PoolObject;
import Atom.Utility.Pool;
import Atom.Utility.Utility;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Writes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.AirBlock;
import org.jetbrains.annotations.NotNull;
import org.o7.Fire.Glopion.Module.Module;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;
import org.o7.Fire.Glopion.Module.WorldModule;
import org.o7.Fire.Glopion.Patch.Translation;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class MachineRecorder implements Module, WorldModule, Serializable {
    protected static final long delay = 280;//human visual response time
    protected static final long delaySave = 4 * 1000;
    protected static final int temporaryArraySize = (int) (delaySave / delay);
    public static int maxView = 12;
    public static HashMap<Integer, String> color = new HashMap<>();
    public static PoolObject<Seq<Entityc>> poolEntity = new SeqPool<>();
    public static int precision = 100;
    protected static File workingDir = new File("cache/" + Utility.getDate() + "-Player-Recorder");
    protected static Gson gson = new GsonBuilder().create();

    static {
        Log.info("Recorder Path: @", workingDir.getAbsolutePath());
    }
    
    protected transient Player player;
    protected int[][] input, output;
    protected int inputSize, outputSize;
    protected transient int loggingIndex, fileIndex;
    protected transient long nextCapture = System.currentTimeMillis() + delaySave;
    protected transient boolean sensoryCapture = true, stop = false;
    
    public MachineRecorder(Player p) {
        player = p;
        resetArray();
    }
    public static void visualizeColorized(int[] vector, StringBuilder sb){
            for (int y : vector) {
                if (!color.containsKey(y)) color.put(y, Translation.getRandomHexColor());
                sb.append(color.get(y)).append("■");
            }
        sb.append("[white]\n");
    }
    public static StringBuilder visualizeColorized(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] x : matrix) {
           visualizeColorized(x,sb);
        }
        return sb;
    }
    
    public static String visualize(int[][] matrix) {
        String h = Arrays.deepToString(matrix).replace("],", System.lineSeparator());
        h = " " + h.substring(1, h.length() - 2);
        return h;
    }
    public static int toInt(double f){
        return (int) (f * 100);
    }
    public static int toInt(float f){
        return (int) (f*100);
    }
    public static int toInt(boolean o){
        return o ? 1 : 0;
    }
    public static int hashState(int[] h, int state){
        for (int element : h)
            state = 3 * state + element;
    
        return state;
    }
    private static class StateReader implements DataOutput {
        public int state = 0;
        
        @Override
        public void write(int b) throws IOException {
            state = 3 * state + b;
        }
    
        @Override
        public void write(@NotNull byte[] b) throws IOException {
            for (byte b1 : b)
                write(b1);
        }
    
        @Override
        public void write(@NotNull byte[] b, int off, int len) throws IOException {
            
            Objects.checkFromIndexSize(off, len, b.length);
            for (int i = 0 ; i < len ; i++) {
                write(b[off + i]);
            }
        }
    
        @Override
        public void writeBoolean(boolean v) throws IOException {
            write(v ? 1 : 0);
        }
    
        @Override
        public void writeByte(int v) throws IOException {
            write(v);
        }
    
        @Override
        public void writeShort(int v) throws IOException {
            write(v);
        }
    
        @Override
        public void writeChar(int v) throws IOException {
            write(v);
        }
    
        @Override
        public void writeInt(int v) throws IOException {
            write(v);
        }
    
        @Override
        public void writeLong(long v) throws IOException {
            write((int) v);
        }
    
        @Override
        public void writeFloat(float v) throws IOException {
            write(toInt(v));
        }
    
        @Override
        public void writeDouble(double v) throws IOException {
            write(toInt(v));
        }
    
        @Override
        public void writeBytes(@NotNull String s) throws IOException {
            write(s.getBytes(Vars.charset));
        }
    
        @Override
        public void writeChars(@NotNull String s) throws IOException {
            write(s.getBytes(Vars.charset));
        }
    
        @Override
        public void writeUTF(@NotNull String s) throws IOException {
            write(s.getBytes(Vars.charset));
        }
    }
    public static StateReader machineReader = new StateReader();
    public static Writes writes = new Writes(machineReader);
    public static int tileState(Tile t){
        if(t == null)return 0;
        int state = 0;
        state = hashState(new int[]{
                t.getTeamID(),//
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
            state = hashState(new int[]{t.overlayID()},state);
        }else {
            state = hashState(new int[]{t.floorID()},state);
        }
        return state;
    }
    public static int renderTile(Tile t) {
        if (t == null) return 0;
        Seq<Entityc> seq1 = poolEntity.obtain();
        if (seq1.isEmpty()) Groups.bullet.intersect(t.worldx(), t.worldy(), 2, 2, seq1::add);
        if (seq1.isEmpty()) Groups.unit.intersect(t.worldx(), t.worldy(), 1, 1, seq1::add);
        int render = 0;
        if (!seq1.isEmpty()) {
            Entityc entity = seq1.remove(0);
            machineReader.state = 0;
            entity.write(writes);
            render =  machineReader.state;
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
        return " " + Arrays.deepToString(matrix).replace("],", System.lineSeparator()).substring(1);
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
        return new File(workingDir, "Record-" + fileIndex + "-" + player.id + ".json");
    }
    
    public void resetArray() {
        //EventType.BlockBuildBeginEvent
        loggingIndex = 0;
        inputSize = captureSensory().length;
        outputSize = captureAction().length;
        input = new int[temporaryArraySize][inputSize];
        output = new int[temporaryArraySize][outputSize];
    }
    public static int getEnvironmentInformationSize(){
        int size = 0;
        size = size + Vars.content.items().size;
        size++;//player unit rotation
        size++;//shooting
        size++;//team
        size++;//boosting
        size++;//distance to core
        size++;//builder
        size++;//unit type id
        size++;//tile on
        size++;//velocity x
        size++;//velocity y
        return size;
    }
    public int[] getEnvironmentInformation(){
        int[] vector = new int[getEnvironmentInformationSize()];
        getEnvironmentInformation(vector);
        return vector;
    }
    
    public void getEnvironmentInformation(int[] vector){
        int index = 0;
        for (Item item : Vars.content.items()) {
            vector[index] = player.closestCore().items().get(item);
            index++;
        }
        vector[index++] = (int) (player.unit().rotation()*10);
        vector[index++] = toInt(player.shooting);
        vector[index++] = player.team().id;
        vector[index++] = toInt(player.boosting);
        vector[index++] = (int) player.closestCore().tile().dst(player.tileOn());
        vector[index++] = toInt(player.isBuilder());
        vector[index++] = player.unit().type == null ? 0 : player.unit().type.id;
        vector[index++] = tileState(player.tileOn());
        vector[index++] = toInt(player.unit().vel().getX());
        vector[index++] = toInt(player.unit().vel().getY());
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
        int index = getEnvironmentInformationSize()-1;
        getEnvironmentInformation(vector);
        for (Tile[] tiles : worldView) {
            for (Tile t : tiles) {
                vector[index] = renderTile(t);
                index++;
            }
        }
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
    
    public Tile[][] getWorldData(int radius) {
        int diameter = radius + radius;
        Tile[][] matrix = new Tile[diameter][diameter];
        int x = Vars.player.tileX(), y = Vars.player.tileY();
        for (int yPointer = radius; yPointer > -radius; yPointer--) {
            for (int xPointer = radius; xPointer > -radius; xPointer--) {
                
                matrix[xPointer + radius - 1][yPointer + radius - 1] = Vars.world.tile(x + xPointer, y + yPointer);
                
            }
        }
        return matrix;
    }
    
    public int[] captureSensory() {
        return worldDataToVisualVector(getWorldData(maxView));
    }
    
    public int[] captureAction() {
        
        return new int[]{(int) (player.unit().vel.y * precision), /** {@link Control#MoveVertical} */
                (int) (player.unit().vel.x * precision), /** {@link Control#MoveHorizontal} */
                (int) (player.unit().rotation * precision), /** {@link Control#Rotate} */
                player.shooting ? 1 : 0, /** {@link Control#Shooting} */
                player.unit().mining() ? 1 : 0, /** {@link Control#Mining} */
                player.boosting() ? 1 : 0, /** {@link Control#Boosting} */
                player.unit().isBuilding() ? 1 : 0, /** {@link Control#Building} */};
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
    
    @Override
    public void update() {
        if (!Vars.state.isPlaying()) return;
        
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
    
    private static class ArrayPool extends PoolObject<int[]> {
        int size;
        
        public ArrayPool(int size) {
            this.size = size;
        }
        
        @Override
        protected int[] newObject() {
            return new int[size];
        }
    }
}
