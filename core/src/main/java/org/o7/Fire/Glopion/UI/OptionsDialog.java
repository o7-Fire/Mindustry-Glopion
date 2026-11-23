package org.o7.Fire.Glopion.UI;

import Atom.Reflect.Reflect;
import arc.Core;
import arc.func.Cons2;
import arc.func.Func;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.TextManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;

public class OptionsDialog extends ScrollableDialog {
    public static final HashSet<Class<?>> classSettings = new HashSet<>();
    public static Cons2<String, String> saveSettings = (k, v) -> Core.settings.put(k, v);
    protected final ObjectMap<String, String> temp = new ObjectMap<>();
    
    public static ArrayList<Field> getSettingsField() {
        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> c : classSettings) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getType().isPrimitive() || f.getType() == String.class){
                    if (f.getName().endsWith("Settings") && Modifier.isPublic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())){
                        fields.add(f);
                    }
                }
            }
        }
        return fields;
    }
    
    public static String getQualifiedName(Field f) {
        return f.getDeclaringClass().getCanonicalName() + "." + f.getName();
    }
    
    public static void load(Func<String, String> h) {
        int loaded = 0;
        for (Field f : getSettingsField()) {
            if (f.getDeclaringClass().getCanonicalName() == null){
                Log.warn("Cryptic Class Alert @ from field @ with type @", f.getDeclaringClass(), f.getName(), f.getType());
            }
            String qualifiedName = f.getDeclaringClass().getCanonicalName() + "." + f.getName();
            String dat = h.get(qualifiedName);
            if (dat == null){
                Log.debug("@ is not found", qualifiedName);
                continue;
            }
            
            try {
                Object data = Reflect.parseStringToPrimitive(dat, f.getType());
                
                if (data == null){
                    Log.debug("@ is null after parse to @", dat, f.getType());
                    continue;
                }
                f.set(null, data);
                loaded++;
            }catch(Exception e){
                WarningHandler.handleProgrammerFault(e);
            }
        }
        Log.debug("loaded @ settings", loaded);
    }
    
    
    @Override
    protected void setup() {
        Table tt = table.table().growX().get();
        table.row();
        for (Field f : getSettingsField()) {
            if (f.getType() == boolean.class){
            
            
                try {
                    boolean b = f.getBoolean(null);
                    tt.check("", b, be -> {
                        try {
                            f.set(null, be);
                            saveSettings.get(getQualifiedName(f), String.valueOf(be));
                            arc.Events.fire(new Events.BooleanSettingsChanged(be, f));
                        }catch(IllegalAccessException e){
                            WarningHandler.handleProgrammerFault(e);
                            Vars.ui.showException(e);
                        }
                    });
                    tt.add(TextManager.get(f.getName())).growX().row();
                }catch(Exception e){
                    tt.add(e.toString()).growX().color(Color.red).row();
                    WarningHandler.handleProgrammerFault(e);
                }
            
            
            }else{
                table.table(t -> {
                    t.add(TextManager.get(f.getName())).growX().row();
                    
                    try {
                        temp.put(getQualifiedName(f), f.get(null) + "");
                        t.field(temp.get(getQualifiedName(f)), s -> {
                            temp.put(getQualifiedName(f), s);
                        }).growX();
                        t.button(Icon.save, () -> {
                            try {
                                Object data = Reflect.parseStringToPrimitive(temp.get(getQualifiedName(f)), f.getType());
                                if (data == null)
                                    throw new NullPointerException("Yikes can't parse: " + temp.get(getQualifiedName(f)));
                                f.set(null, data);
                                saveSettings.get(getQualifiedName(f), String.valueOf(data));
                                arc.Events.fire(new Events(f, String.valueOf(data)));
                            }catch(Exception e){
                                Vars.ui.showException(e);
                            }
                        }).tooltip("Save");
                    }catch(IllegalAccessException e){
                        t.add(e.toString()).color(Color.red).growX();
                        WarningHandler.handleMindustry(e);
                    }
                }).growX().row();
            }
        }
    }
    
    public static class Events {
        public final Field field;
        public final String value;
        
        public Events(Field field, String value) {
            this.field = field;
            this.value = value;
        }
        
        public static class BooleanSettingsChanged extends Events {
            public final boolean newSettings;
            
            public BooleanSettingsChanged(boolean newSettings, Field field) {
                super(field, newSettings + "");
                this.newSettings = newSettings;
            }
        }
    }
}
