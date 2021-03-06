package org.o7.Fire.Glopion.Commands;

import arc.util.pooling.Pool;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Module.ModsModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class CommandsHandler extends ModsModule {
    public static HashMap<Class<? extends CommandsClass>, CommandsPool> poolMap = new HashMap<>();
    public static HashSet<Class<? extends CommandsClass>> commandsList = new HashSet<>();
    
    @Override
    public void preInit() throws Throwable {
        try {
            commandsList.addAll(InformationCenter.getExtendedClass(CommandsClass.class));
            commandsList.remove(CommandsClass.class);
            for (Class<? extends CommandsClass> c : Collections.unmodifiableSet(commandsList)) {
                if (c.getCanonicalName() == null) commandsList.remove(c);
            }
        }catch(Throwable ignored){}
    }
    
    @Override
    public void start() {
        super.start();
        
    }
 //wtf volas was here a long ago
    public static class CommandsPool extends Pool<CommandsClass> {
        Class<? extends CommandsClass> aClass;
        
        public CommandsPool(Class<? extends CommandsClass> clazz) {
            aClass = clazz;
        }
        
        @Override
        protected CommandsClass newObject() {
            try {
                return aClass.getDeclaredConstructor().newInstance();
            }catch(Throwable e){
                throw new RuntimeException(e);
            }
        }
    }
}
