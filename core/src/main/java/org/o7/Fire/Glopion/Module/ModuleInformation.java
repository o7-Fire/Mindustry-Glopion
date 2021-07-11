package org.o7.Fire.Glopion.Module;

import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.Vars;

import java.util.Arrays;

public class ModuleInformation extends ModsModule{

    
    @Override
    public void registerServerCommands(CommandHandler handler) {
       handler.register("glopion-module-information","[module class name]", "Get Glopion modules information",s->{
           try {
               Class<? extends Module> clz = (Class<? extends Module>) Class.forName(s[0]);
               Module module = ModuleRegisterer.get(clz);
               StringBuilder info = new StringBuilder();
               if(module == null){
                   info.append("Module @ is not registered");
               } else {
                   info.append("Name: ").append(module.getName()).append(System.lineSeparator());
                   if(module instanceof ModsModule){
                       ModsModule mod = (ModsModule) module;
                       info.append("Mods Name: ").append(mod.getName()).append(System.lineSeparator());
                       info.append("Author: ").append(mod.getAuthor()).append(System.lineSeparator());
                       info.append("Dependency: ").append(mod.dependency).append(System.lineSeparator());
                       info.append("Disabled: ").append(mod.disabled()).append(System.lineSeparator());
                       info.append("Description: ").append(mod.getDescription()).append(System.lineSeparator());
                       info.append("Version: ").append(mod.getVersion()).append(System.lineSeparator());
                       info.append("Loaded: ").append(mod.isLoaded()).append(System.lineSeparator());
                       info.append("MinGameVesion: ").append(mod.getMinGameVersion()).append(System.lineSeparator());
                   }
               }
               Log.info(info);
           }catch(Exception e){
               Log.err(e.getMessage());
               return;
           }
       });
    }
}
