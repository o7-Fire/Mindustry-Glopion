package org.o7.Fire.Glopion.Control;

import mindustry.Vars;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

import static org.o7.Fire.Glopion.Control.MachineRecorder.toInt;

public class StateCalculatorReader implements DataOutput {
    public int state = 0;
    public void reset(){
        state = 0;
    }
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
