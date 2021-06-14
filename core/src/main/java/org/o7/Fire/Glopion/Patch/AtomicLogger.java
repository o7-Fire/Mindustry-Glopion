package org.o7.Fire.Glopion.Patch;

import Atom.Reflect.Reflect;
import Atom.Utility.Utility;
import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.ColorCodes;
import arc.util.Log;
import arc.util.OS;
import mindustry.Vars;
import mindustry.game.EventType;
import org.o7.Fire.Glopion.Event.EventExtended;
import org.o7.Fire.Glopion.Module.ModsModule;

import java.io.Writer;

public class AtomicLogger extends ModsModule {
    public static Writer writer;
    public static Seq<String> logBuffer = new Seq<>();
    public static String[] tags = {"[green][D][]", "[royal][I][]", "[yellow][W][]", "[scarlet][E][]", ""};
    public static String[] color = {"[green]", "[royal]", "[yellow]", "[scarlet]", "[white]"};
    public static String[] stags = {"&lc&fb[D]", "&lb&fb[I]", "&ly&fb[W]", "&lr&fb[E]", ""};
    
    public void preInit() {
        
        if (Reflect.debug) Log.level = Log.LogLevel.debug;
        if (writer != null){
            try {
                Core.settings.setAppName(Vars.appName);
                writer = Core.settings.getDataDirectory().child(Utility.getDate().replace(' ', '/') + "-Atomic-Logger.txt").writer(false);
            }catch(Exception ignored){}
        }
        Log.logger = (level, text) -> {
            String raw = text + "";
            int i = Reflect.callerOffset() + 3;
            StackTraceElement st = null;
            try {
                st = Thread.currentThread().getStackTrace()[i];
            }catch(IndexOutOfBoundsException ignored){
                //ok java
            }
            if (st != null){
                Class<?> cl = null;
                try {
                    cl = this.getClass().getClassLoader().loadClass(st.getClassName());
                }catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                while (cl == Log.class) {
                    try {
                        i++;
                        st = Thread.currentThread().getStackTrace()[i];
                        cl = this.getClass().getClassLoader().loadClass(st.getClassName());
                    }catch(IndexOutOfBoundsException mmm){
                        st = null;
                        break;
                    }catch(Throwable t){
                        break;
                    }
                }
            }
            String threadInfo = Thread.currentThread().getName() + (Thread.currentThread().getThreadGroup() != null ? "-" + Thread.currentThread().getThreadGroup().getName() : "");
            String stackTrace = st == null || !Reflect.debug ? "" : "-" + st;//if st is not null and is debug
            text = String.format("[%s] [%s%s] %s", Utility.getDate(), threadInfo, stackTrace, text);
            try {
                writer.write("[" + Character.toUpperCase(level.name().charAt(0)) + "] " + Log.removeColors(text) + System.lineSeparator());
                writer.flush();
            }catch(Exception ignored){ }
            
            String result = text;
            String rawText = Log.format(stags[level.ordinal()] + "&fr " + text);
            System.out.println(rawText);
            
            result = tags[level.ordinal()] + " " + result;
            logBuffer.add(result);
            Events.fire(EventExtended.Log.class, new EventExtended.Log(result));
            if (!OS.isWindows){
                for (String code : ColorCodes.values) {
                    result = result.replace(code, "");
                }
            }
            
            if (Vars.ui != null && Vars.ui.scriptfrag != null){
                Vars.ui.scriptfrag.addMessage(Log.removeColors(result));
            }
            
        };
        Events.on(EventType.ClientLoadEvent.class, e -> {
            logBuffer.each(a -> {
                if (Vars.ui != null && Vars.ui.scriptfrag != null) Vars.ui.scriptfrag.addMessage(a);
                
            });
        });
        
    }
    
    @Override
    public String getDescription() {
        return "Disable this to disable detailed log";
    }
}
