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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package org.o7.Fire.Glopion.Internal.Shared;

import Atom.Reflect.Reflect;
import arc.util.Log;
import mindustry.Vars;
import org.o7.Fire.Glopion.Internal.Interface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

//what the fuck are you trying to do, handling error or making error reporting app
public class WarningHandler {
    public static ArrayList<WarningReport> listOfProblem = new ArrayList<>();
    public static HashSet<String> handled = new HashSet<>();
    public static final HashSet<Throwable> errorList = new HashSet<>();
    public static boolean isLoaded() {//safest class
        return System.getProperty("Mindustry.Ozone.Loaded") != null;
    }
    
    
    public static void handleOnce(Consumer<Throwable> c, Throwable t) {
        String s = t.toString();
        if (handled.contains(s)) return;
        handled.add(s);
        c.accept(t);
    }
    
    /**
     * Mindustry or Runtime Related Error e.g {@link RuntimeException}, {@link java.io.IOException}
     */
    public static void handleMindustry(Throwable t) {
        String s = "Glopion-Handler";
        try { s = Reflect.getCallerClassStackTrace().toString(); }catch(Throwable ignored){}
        handleMindustry(t, s);
    }
    
    /**
     * User Related Error e.g {@link NumberFormatException}
     */
    public static void handleMindustryUserFault(Throwable t) {
        String s;
        if (t instanceof RuntimeException){
            while (t.getCause() != null) t = t.getCause();
            t = new Throwable(t);//idiot outcry
        }
        s = t.getClass().getSimpleName();
        handleTest(t);
        handleStealthMindustry(t, s);
        try {
            Vars.ui.showException(t);
        }catch(Throwable ignored){}
    }
    
    /**
     * JVM Related don't try to catch it, e.g {@link OutOfMemoryError}
     */
    public static void handleJava(Throwable t) {
        if (t instanceof VirtualMachineError) throw new RuntimeException(t);
    
    }
    
    /**
     * Used during testing
     */
    public static void handleTest(Throwable t) {
        handleJava(t);
        errorList.add(t);
    }
    
    /**
     * Skill Issue, e.g {@link NoSuchFieldError}
     */
    public static void handleProgrammerFault(Throwable t) {
        handleTest(t);
        try {
            // Sentry.captureException(t);
        }catch(Throwable ignored){}
        try {
            if(Reflect.debug || Vars.headless || Vars.mobile)
                t.printStackTrace();
            if (Reflect.debug){
                handleStealthMindustry(t, "Glopion-Debug");
            }
        }catch(Throwable ignored){}
        
    }
    
    /**
     * Try log it, don't care much
     */
    public static void handleStealthMindustry(Throwable t, String s) {
        try {
            Log.errTag(s, t.getMessage());
        }catch(Throwable ignored){}
        
    }
    
    /**
     * @see #handleMindustry(Throwable)
     */
    public static void handleMindustry(Throwable t, String stacktrace) {
        handleTest(t);
        handleProgrammerFault(t);
        
    }
    
    //what is this
    public static void handle(WarningReport wr) {
        //Pool.daemon(()->{
        if (listOfProblem.contains(wr)) return;
        if (!wr.level.equals(WarningReport.Level.debug) || Reflect.debug){
            
            //try {Sentry.addBreadcrumb(wr.headlines(), wr.level.name());}catch(Throwable ignored){}
            try {Interface.toast(wr.headlines()); }catch(Throwable ignored){}
            listOfProblem.add(wr);
            try {
                Log.logger.log(Log.LogLevel.values()[wr.level.ordinal()], wr.headlines());
            }catch(Throwable ignored){ System.out.println(wr.headlines());}
            
        }
        //}).start();
        
    }
    
    public static void handle(Throwable t) {
        handle(t, false);
    }
    
    public static void handle(Throwable t, boolean silent) {
        //Pool.daemon(()->{
        if (Reflect.debug) t.printStackTrace();
        WarningHandler.handleMindustry(t);
        if (!silent){
            try { Log.err(t);}catch(Throwable ignored){}
        }
        try {
            listOfProblem.add(new WarningReport(t).setLevel(silent ? WarningReport.Level.warn : WarningReport.Level.err));
        }catch(Throwable ignored){}
        //if (t.getClass() == RuntimeException.class) if (SharedBoot.test) throw (RuntimeException) t;
        //}).start();
        
    }
}
