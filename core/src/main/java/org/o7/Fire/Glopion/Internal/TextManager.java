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

package org.o7.Fire.Glopion.Internal;

import Atom.Exception.GetRealException;
import Atom.Reflect.Reflect;
import Atom.Utility.Random;
import arc.Core;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Strings;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//TODO decentralize text id translation
public class TextManager extends ModsModule {
    
    public static final Map<String, String> bundle = Collections.synchronizedMap(new TreeMap<>());
    public static final List<String> normalSinglet = new ArrayList<>(Arrays.asList("Run"));
    public static final List<String> singlet1 = new ArrayList<>(Arrays.asList("String", "Integer", "Float", "Byte", "Double", "Short", "Char", "Long", "Boolean", "Commands", "Settings"));
    public static final Map<String, String> generalSettings = new HashMap<>();
    public static final Map<String, String> commands = new HashMap<>();
    
    static {
        try {
            Log.debug("Searching for @.properties", totalPrefix());
            Log.debug("Locale @", Interface.getLocale().getLanguage());
            loadBundle(Repository.readProperties(totalPrefix() + ".properties"));
        }catch(Exception e){
            WarningHandler.handleProgrammerFault(e);
        }
    }
    
    {
        try {
            dependency.addAll(InformationCenter.getExtendedClass(TextManager.class));
            
        }catch(GetRealException ignored){
        
        }catch(Throwable t){
            WarningHandler.handleMindustry(t);
        }
    }
    
    public static File dumpBundle() throws IOException {
        Properties properties = new Properties();
        for (Map.Entry<String, String> s : bundle.entrySet()) {
            properties.setProperty(s.getKey(), s.getValue());
        }
        return dumpBundle(prefix(), properties);
    }
    
    public static File dumpBundle(String prefix, Properties properties) throws IOException {
        File where = new File(prefix + "_" + Interface.getLocale().getLanguage() + ".properties");
        properties.store(new FileWriter(where), Interface.getLocale().getLanguage() + " Turn off colorization if needed");
        Log.infoTag("Glopion-TextManager", "Bundle dumped " + where.getAbsolutePath());
        return where;
    }
    
    public static void loadBundle(Properties properties) throws IOException {
        for (Object s : properties.keySet()) {
            if (s instanceof String){
                String ss = (String) s;
                registerWords(ss, properties.getProperty(ss, ss));
            }
        }
    }
    
    /**
     * Convert string to current {@link Interface#getLocale()}
     *
     * @return "Colorize the word to" -> "Colorize verbum"
     */
    public static String translate(String val) {
        String key = val.toLowerCase().replace(' ', '-').trim();
        if (bundle.containsKey(key)) return bundle.get(key);
        if (Core.bundle != null && Core.bundle.getOrNull(key) != null){
            return Core.bundle.get(key);
        }
        registerWords(key, val);
        return bundle.get(key);
    }
    
    public static String getRandomHexColor() {
        return "[" + Random.getRandomHexColor() + "]";
    }
    
    public static boolean isColorized(String s) {
        return Strings.stripColors(s).length() != s.length();
    }
    
    public static String get(String key, String def) {
        if (bundle.containsKey(key)) return bundle.get(key);
        if (Core.bundle != null && Core.bundle.getOrNull(key) != null){
            return Core.bundle.get(key);
        }
        if (def != null){
            return def;
        }
        Class<?> s = Interface.getClass(key);
        if (s != null){
            if (Reflect.debug && Reflect.DEBUG_TYPE != Reflect.DebugType.UserPreference)
                registerWords(s.getName(), "[" + s.getSimpleName() + "]-" + s.getCanonicalName());
            else registerWords(s.getName(), s.getSimpleName());
            return bundle.get(key);
        }
        if (!key.contains(".")){
            registerWords(key, key);
            return bundle.get(key);
        }
        return Core.bundle == null ? key : Core.bundle.get(key);
    }
    
    public static String get(String key) {
        return get(key, null);
    }
    
    public static void registerWords(String key, String value) {
        value = colorized(value);
        bundle.put(key, value);
    }
    
    public static String colorized(String s) {
        if (!GlopionCore.colorPatchSettings) return s;
        return getRandomHexColor() + s + "[white]";
    }
    
    public static String add(String id, String text) {
        String s = Thread.currentThread().getStackTrace()[2].getClassName() + text.toLowerCase().replaceAll(" ", ".");
        registerWords(s, text);
        s = text;
        return s;
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
        try {
        
            loadBundle(Repository.readProperties(totalPrefix() + ".properties"));
        }catch(Exception e){
            WarningHandler.handleProgrammerFault(e);
        }
    }
    
    public static void registerWords(String s) {
        registerWords(s, s);
    }
    
    private static String totalPrefix() {
        return prefix() + "_" + Interface.getLocale().getLanguage();
    }
    
    private static String prefix() {
        return "glopion-bundle";
    }
    
    @Override
    public String getDescription() {
        return ">Load bundle\n>Colorize the word\n>Crash\n>Leave";
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
        for (ObjectMap.Entry<String, String> s : modified.entries()) {
            modified.put(s.key, colorized(s.value));
        }
        arc.Core.bundle.setProperties(modified);
        //for (Map.Entry<String, CommandsCenter.Command> c : CommandsCenter.commandsList.entrySet())
        //   c.getValue().description = CommandsCenter.getTranslation(c.getKey());
    }
    
    @Override
    public void start() {
        super.start();
        register();
        for (Map.Entry<Class<? extends ModsModule>, ModsModule> s : ModuleRegisterer.modulesMods.entrySet())
            registerWords(s.getValue().getName());
    }
    
}
