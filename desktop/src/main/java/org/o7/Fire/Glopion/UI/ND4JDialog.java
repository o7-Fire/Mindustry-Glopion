package org.o7.Fire.Glopion.UI;

import Atom.Utility.Pool;
import arc.graphics.Color;
import arc.util.Strings;
import org.nd4j.common.config.ND4JClassLoading;
import org.nd4j.linalg.cpu.nativecpu.CpuBackend;
import org.nd4j.linalg.factory.Environment;
import org.nd4j.linalg.factory.Nd4jBackend;

import java.util.*;

public class ND4JDialog extends ScrollableDialog {
    List<Nd4jBackend> backends = new ArrayList<>();
    
    public ND4JDialog() {
        ServiceLoader<Nd4jBackend> loader = ND4JClassLoading.loadService(Nd4jBackend.class);
        try {
            for (Nd4jBackend nd4jBackend : loader) {
                backends.add(nd4jBackend);
            }
        }catch(ServiceConfigurationError serviceError){
            // a fatal error due to a syntax or provider construction error.
            // backends mustn't throw an exception during construction.
            serviceError.printStackTrace();
        }
    }
    
    @Override
    protected void setup() {
        table.add("ND4J Backend").center().row();
        for (Nd4jBackend backend : backends) {
            table.table(t -> {
                Pool.daemon(() -> {
                    try {
                        t.button(backend.getClass().getSimpleName(), () -> {}).disabled(true).growX().row();
                        t.button(backend.isAvailable() ? "Available" : "Unavailable", () -> {}).disabled(true).growX().row();
                        Environment e = backend.getEnvironment();
                        t.button(e.isCPU() ? "is a CPU" : "Not a CPU", () -> {}).disabled(true).growX().row();
                        t.button("Max Thread: " + e.maxThreads(), () -> {}).disabled(true).growX().row();
                        t.button("Max Master Thread: " + e.maxMasterThreads(), () -> {}).disabled(true).growX().row();
                    }catch(Throwable e){
                        t.add(Strings.getFinalMessage(e));
                    }
                }).start();
        
            }).growX().left().color(Color.gray).row();
        }
        
        
    }
}
