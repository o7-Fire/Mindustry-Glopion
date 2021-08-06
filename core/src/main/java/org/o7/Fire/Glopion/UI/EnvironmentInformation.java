/*******************************************************************************
 * Copyright 2021 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.o7.Fire.Glopion.UI;


import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.scene.ui.TextField;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.ui.Styles;
import net.jpountz.lz4.LZ4Factory;
import org.o7.Fire.Glopion.Experimental.Evasion.Identification;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class EnvironmentInformation extends ScrollableDialog {
    
    boolean b;
    
    public EnvironmentInformation() {
        icon = Icon.info;

    }
    
    //non async function first
    protected void setup() {
    
        ad("Player Name", Vars.player.name);
        table.table(t -> {
            t.add("Version.build:" + Version.build).left();
            t.button("-", () -> {
                Version.build--;
                init();
            }).growX();
            t.button("+", () -> {
                Version.build++;
                init();
            }).growX();
        }).growX().row();
        table.table(t -> {
            t.add("Version.revision:" + Version.revision).left();
            t.button("-", () -> {
                Version.revision--;
                init();
            }).growX();
            t.button("+", () -> {
                Version.revision++;
                init();
            }).growX();
        }).growX().row();
        ad("UUID", Core.settings.getString("uuid"), s -> {
            if (Vars.platform.getUUID().length() == s.length()) Core.settings.put("uuid", s);
        });
        ad("Classloader", () -> {
            StringBuilder sb = new StringBuilder();
            try {
                ClassLoader cl = EnvironmentInformation.class.getClassLoader();
                while (cl.getParent() != null) {
                    sb.append(cl.getClass().getSimpleName()).append(" -> ");
                    cl = cl.getParent();
                }
                sb.append(cl.getClass().getSimpleName());
            }catch(Throwable ignored){}
            return sb.toString();
        });
        ad("Current Millis", System.currentTimeMillis());
        ad("Current Nanos", System.nanoTime());
        ad("Current Jar", InformationCenter.getCurrentJar());
        ad("Fastest LZ4 Decompressor", LZ4Factory.fastestInstance().fastDecompressor().getClass().getName());
        ad("Fastest LZ4 Compressor", LZ4Factory.fastestInstance().fastCompressor().getClass().getName());
        try {
            ad("Compilation Time Total (ms)", ManagementFactory.getCompilationMXBean().getTotalCompilationTime());
            ad("isCompilationTimeMonitoringSupported", ManagementFactory.getCompilationMXBean().isCompilationTimeMonitoringSupported());
        }catch(Throwable ignored){}
        uid();
       
        
    }
    
    
    void uid() {
        ad(System.getenv());
        try {
            ArrayList<String> yikes = Identification.getKeys();
            for (String k : yikes) {
                ad(k, Core.settings.getString(k), s -> {
                    if (Identification.getRandomUID().length() == s.length()) Core.settings.put(k, s);
                });
            }
        }catch(Throwable t){
            WarningHandler.handleMindustry(t);
        }
        
        for (Map.Entry<Object, Object> s : System.getProperties().entrySet())
            ad(s.getKey().toString(), s.getValue().toString());
        for (Field f : Sounds.class.getFields()) {
            table.button(f.getName() + ".ogg", Icon.play, () -> {
                try {
                    Sound s = (Sound) Sounds.class.getField(f.getName()).get(null);
                    s.play(100);
                }catch(Throwable e){
                    Vars.ui.showException(e);
                }
            }).growX();
            table.row();
        }
        for (ObjectMap.Entry<String, TextureRegionDrawable> s : Icon.icons.entries()) {
            table.button(s.key, s.value, () -> {}).growX().disabled(true);
            table.row();
        }
        for (Field f : Styles.class.getFields()){
            try {
                Object obj = f.get(null);
                if(obj == null)continue;
                table.button(f.getName() + "-" + f.getType().getSimpleName(),()->{}).disabled(true).growX().center().row();
                table.table(t->{
                    if(f.getType() == Drawable.class){
                        Drawable style = (Drawable) obj;
                        t.image(style).growX().growY().row();
                        t.image(style).growX().growY().disabled(true).row();
                        t.image(style).growX().growY().color(Color.cyan).row();
                        t.image(style).growX().growY().color(Color.cyan).disabled(true).row();
                        
                    }
              
                    if(f.getType() == TextButton.TextButtonStyle.class){
                        TextButton.TextButtonStyle style = (TextButton.TextButtonStyle) obj;
                    
                        t.button("[accent]"+f.getName()+"[white]-.growX()",Icon.book, style,()->{}).growX();
                        t.button("[accent]"+f.getName()+"[white]-.growX().disabled",Icon.book,style, ()->{}).growX().disabled(true).row();
                        t.button("[accent]"+f.getName()+"[white]-.growX().colorCyan",Icon.book,style, ()->{}).growX().color(Color.cyan);
                        t.button("[accent]"+f.getName()+"[white]-.growX().disabled.colorCyan", Icon.book,style,()->{}).growX().disabled(true).color(Color.cyan).row();
                    }
                    if(f.getType() == ImageButton.ImageButtonStyle.class){
                        ImageButton.ImageButtonStyle style = (ImageButton.ImageButtonStyle) obj;
                        t.button(Icon.info, style, ()-> Interface.showInfo("[accent]"+f.getName()+"[white]-.growX()")).growX();
                        t.button(Icon.info, style, ()-> Interface.showInfo("[accent]"+f.getName()+"[white]-.growX().disabled")).growX().disabled(true).row();
                        t.button(Icon.info, style, ()-> Interface.showInfo("[accent]"+f.getName()+"[white]-.growX().colorCyan")).growX().color(Color.cyan);
                        t.button(Icon.info, style, ()-> Interface.showInfo("[accent]"+f.getName()+"[white]-.growX().disabled.colorCyan")).growX().disabled(true).color(Color.cyan).row();
                    }
                    if(f.getType() == Label.LabelStyle.class){
                        Label.LabelStyle style = (Label.LabelStyle) obj;
                        t.labelWrap("[accent]"+f.getName()+"[white]-.growX()").growX().get().setStyle(style);
                        t.labelWrap("[accent]"+f.getName()+"[white]-.growX().disabled").growX().disabled(true).get().setStyle(style);
                        t.row();
                        t.labelWrap("[accent]"+f.getName()+"[white]-.growX().colorCyan").growX().color(Color.cyan).get().setStyle(style);
                        t.labelWrap("[accent]"+f.getName()+"[white]-.growX().disabled.colorCyan").growX().disabled(true).color(Color.cyan).get().setStyle(style);
                        t.row();
                    }
                    if(f.getType() == TextField.TextFieldStyle.class){
                        TextField.TextFieldStyle style = (TextField.TextFieldStyle) obj;
                        t.field("[accent]"+f.getName()+"[white]-.growX()", style,s->{}).growX();
                        t.field("[accent]"+f.getName()+"[white]-.growX().disabled",style, s->{}).growX().disabled(true).row();
                        t.field("[accent]"+f.getName()+"[white]-.growX().colorCyan",style, s->{}).growX().color(Color.cyan);
                        t.field("[accent]"+f.getName()+"[white]-.growX().disabled.colorCyan", style,s->{}).growX().disabled(true).color(Color.cyan).row();
                    }
                }).growX().color(Color.gold).row();
               
            }catch(Exception e){//should not happened etc...
                WarningHandler.handleProgrammerFault(e);
            }
        }
    }
    
    
}
