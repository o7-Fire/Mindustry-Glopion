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

package org.o7.Fire.Glopion.Patch;

import Atom.Reflect.Reflect;
import Atom.Utility.Random;
import arc.struct.ObjectMap;
import arc.util.Strings;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.o7.Fire.Glopion.Internal.Interface.registerWords;

//TODO decentralize text id translation
public class Translation extends ModsModule {
    public static final ObjectMap<String, String> bundle = new ObjectMap<>();
    public static final ArrayList<String> normalSinglet = new ArrayList<>(Arrays.asList("Run"));
    public static final ArrayList<String> singlet1 = new ArrayList<>(Arrays.asList("String", "Integer", "Float", "Long", "Boolean", "Commands", "Settings"));
    public static final HashMap<String, String> generalSettings = new HashMap<>();
    public static final HashMap<String, String> commands = new HashMap<>();
    
    {
        try {
            dependency.addAll(InformationCenter.getExtendedClass(Translation.class));
        }catch(Throwable ignored){}
    }
    
    @Override
    public String getDescription() {
        return "Colorize the word, don't actually do translate ";
    }
    
    public static String getRandomHexColor() {
        return "[" + Random.getRandomHexColor() + "]";
    }
    
    public static boolean isColorized(String s) {
        return Strings.stripColors(s).length() != s.length();
    }
    
    public static String get(String key) {
        
        return Interface.getBundle(key, null);
    }
    
    public static String colorized(String s) {
        if (!GlopionCore.colorPatchSettings) return s;
        if (isColorized(s)) return s;
        return getRandomHexColor() + s + "[white]";
    }
    
    public static String add(String id, String text) {
        String s = Thread.currentThread().getStackTrace()[2].getClassName() + text.toLowerCase().replaceAll(" ", ".");
        registerWords(s, text);
        s = text;
        if (GlopionCore.colorPatchSettings) s = getRandomHexColor() + s + "[white]";
        return s;
    }
    
    public static void registerWords(String s, String s1){
        Interface.registerWords(s, s1);
    }
    public static void registerWords(String s){
       registerWords(s,s);
    }
    public static void register() {
        registerWords("Ozone");
        registerWords("Glopion");
        registerWords("Update");
        registerWords("ozone.menu", "Ozone Menu");
        registerWords("ozone.hud", "Ozone HUD");
        registerWords("ozone.javaEditor", "Java Executor");
        registerWords("ozone.javaEditor.reformat", "Reformat");
        registerWords("ozone.javaEditor.run", "Run");
        registerWords("ozone.commandsUI", "Commands GUI");
        registerWords("colorPatchSettings", "Translation Colorized");
        registerWords("blockDebugSettings", "Enable Block Debug Info");
        commands.put("help", "help desk");
        commands.put("chaos-kick", "vote everyone everytime everywhere -Nexity");
        commands.put("task-move", "move using current unit with pathfinding algorithm");
        commands.put("info-pos", "get current info pos");
        commands.put("info-pathfinding", "get Pathfinding overlay");
        commands.put("force-exit", "you want to crash ?");
        commands.put("task-deconstruct", "deconstruct your block with AI");
        commands.put("send-colorize", "send Colorized text");
        commands.put("info-unit", "get current unit info");
        commands.put("random-kick", "random kick someone");
        commands.put("clear-pathfinding-overlay", "Clear every pathfinding overlay");
        commands.put("shuffle-sorter", "shuffle every block that look like sorter, note: item source too");
        commands.put("javac", "run single line of code, like \nVars.player.unit().moveAt(new Vec2(100, 100));");
        commands.put("task-clear", "clear all bot task");
        commands.put("message-log", "see message log, recommend to export it instead");
        commands.put("shuffle-configurable", "shuffle every configurable block, literally almost everything");
        for (Map.Entry<String, String> s : commands.entrySet()) {
            registerWords("ozone.commands." + s.getKey(), s.getValue());
        }
        
        for (Map.Entry<String, String> s : generalSettings.entrySet()) {
            registerWords("setting." + s.getKey() + ".name", s.getValue());
        }

        
        for (String s : singlet1) registerWords(s, "[" + s + "]");
        for (String s : normalSinglet) registerWords(s);
        
    }
    
    public static String add(String text) {
        return add(Thread.currentThread().getStackTrace()[2].toString() + text.toLowerCase().replaceAll(" ", "."), text);
        
    }
    
    public static void addSettings(Map<String, String> map) {
        for (Map.Entry<String, String> s : map.entrySet())
            registerWords(Reflect.getCallerClass() + "." + s.getKey(), s.getValue());
    }
    
    @Override
    public void postInit() throws Throwable {
        ObjectMap<String, String> modified = arc.Core.bundle.getProperties();
        for (ObjectMap.Entry<String, String> s : modified.entries())
            if (GlopionCore.colorPatchSettings) modified.put(s.key, getRandomHexColor() + s.value + "[white]");
        
        for (ObjectMap.Entry<String, String> s : Interface.bundle) {
            modified.put(s.key, s.value);
        }
        
        arc.Core.bundle.setProperties(modified);
        //for (Map.Entry<String, CommandsCenter.Command> c : CommandsCenter.commandsList.entrySet())
        //   c.getValue().description = CommandsCenter.getTranslation(c.getKey());
    }
    
    @Override
    public void start() {
        super.start();
        register();
        for (Map.Entry<Class<? extends ModsModule>, ModsModule> s : ModuleRegisterer.modules.entrySet())
            registerWords(s.getValue().getName());
    }
    
}
