package org.o7.Fire.Glopion.Desktop;

import arc.util.CommandHandler;
import arc.util.Log;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

import java.util.Arrays;

public class ServerBot extends ModsModule {
    
    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("glopion-bot", "<bot-type> <mode>", "spawn bot", s -> {
            BotControl.Type type = null;
            BotControl.Mode mode = null;
            try {
                type = BotControl.Type.valueOf(s[0]);
            }catch(IllegalArgumentException h){
                Log.err("No such type, available type @", Arrays.toString(BotControl.Type.values()));
                return;
            }
            try {
                mode = BotControl.Mode.valueOf(s[1]);
            }catch(IllegalArgumentException h){
                Log.err("No such mode, available mode @", Arrays.toString(BotControl.Mode.values()));
                return;
            }
            BotControl botControl = (BotControl) ModuleRegisterer.get(BotControl.class);
            if (botControl == null){
                Log.err("Bot Control Module Gone");
                return;
            }
            botControl.control(mode, type);
        });
    }
}
