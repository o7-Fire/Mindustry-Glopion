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

package org.o7.Fire.Glopion.Internal;

import Atom.Annotation.MethodFuzzer;
import Atom.Exception.GetRealException;
import Atom.Exception.ShouldNotHappenedException;
import Atom.Reflect.FieldTool;
import Atom.Reflect.OS;
import Atom.Reflect.Reflect;
import Atom.Utility.TestingUtility;
import arc.Core;
import arc.net.Client;
import mindustry.Vars;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import org.jetbrains.annotations.Nullable;
import org.o7.Fire.Glopion.Gen.Callable;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Module.ModsModule;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Set;

public class InformationCenter extends ModsModule {
    public static String currentServerIP = "";
    public static int currentServerPort = 0;
    public static Callable callable;
    
    public static boolean canModifyPrivateFinalField() {
        return canModifyField("privateStaticFinal");
    }
    
    public static boolean canModifyPublicFinalField() {
        return canModifyField("publicStaticFinal");
    }
    
    public static boolean canModifyPublicField() {
        return canModifyField("publicStatic");
    }
    
    public static boolean canModifyPrivateField() {
        return canModifyField("privateStatic");
    }
    
    protected static boolean canModifyField(String field) {
        Field f = Reflect.findDeclaredField(InformationCenter0.class, ff -> ff.getName().equals(field)).get(0);
        if (f == null) throw new ShouldNotHappenedException("Failed to look for " + field + " field");
        FieldTool.trySetAccessible(f);
        try {
            boolean b = f.getBoolean(null);
            f.setBoolean(null, !b);
            boolean b1 = f.getBoolean(null);
            return b != b1;
        }catch(IllegalAccessException e){
            return false;
        }
    }
    
    public static boolean canAccessPublicField() {
        return canAccessField("publicStaticFinal");
    }
    
    public static boolean canAccessPrivateField() {
        return canAccessField("privateStaticFinal");
    }
    
    protected static boolean canAccessField(String field) {
        Field f = Reflect.findDeclaredField(InformationCenter0.class, ff -> ff.getName().equals(field)).get(0);
        if (f == null) throw new ShouldNotHappenedException("Failed to look for " + field + " field");
        FieldTool.trySetAccessible(f);
        try {
            boolean b = f.getBoolean(null);
            return true;
        }catch(IllegalAccessException e){
            return false;
        }
    }
    
    @MethodFuzzer(skip = true)
    public static <T> Set<Class<? extends T>> getExtendedClass(Class<T> c) {
        if (OS.isAndroid || OS.isIos){//Dalvik bad
            throw new GetRealException("Mobile doesn't support Reflect.getExtendedClass()");
        }
        return Atom.Reflect.ExternalReflection.getExtendedClass(GlopionCore.class.getPackage().getName(),
                c,
                InformationCenter.class.getClassLoader());
    }
    
    public static Callable getCallableMain() {
        if (callable == null) callable = new Callable(Vars.net);
        return callable;
    }
    
    public static File getCurrentJar() {
        
        File f = null;
        try {
            f = Vars.mods.getMod(GlopionCore.mainClass).file.file();
        }catch(Throwable ignored){}
        if (f == null){
            try {
                f = Reflect.getCurrentJar(InformationCenter.class);
            }catch(Throwable ignored){}
        }
        if (f == null) {
            try {
                f = new File(InformationCenter.class.getClassLoader()
                        .getResource(InformationCenter.class.getCanonicalName().replace('.', '/') + ".class")
                        .getFile());
            } catch (Throwable ignored) {
            }
        }
        if (f == null) return null;
        return f.getAbsoluteFile();

    }

    public static File fooClasspathLocation;

    static {
        try {
            fooClasspathLocation = getClasspathLocation(mindustry.client.Client.class.getCanonicalName());
        } catch (Throwable ignored) {
        }
    }

    public static File getClasspathLocation(String canocialName) {
        File f = null;
        try {
            f = Reflect.getCurrentJar(Class.forName(canocialName, false, InformationCenter.class.getClassLoader()));
        } catch (Throwable ignored) {
        }
        if (f == null) {
            try {
                f = new File(InformationCenter.class.getClassLoader()
                        .getResource(canocialName.replace('.', '/') + ".class").getFile());
            } catch (Throwable ignored) {
            }
        }
        if (f == null) return null;
        return f.getAbsoluteFile();
    }


    public static int getCurrentServerPort() {
        try {
            return getCurrentClientNet().getRemoteAddressTCP().getPort();
        } catch (Throwable t) {
            return currentServerPort;
        }
    }
    
    public static String getCurrentServerIP() {
        try {
            return getCurrentClientNet().getRemoteAddressTCP().getAddress().getHostAddress();
        }catch(Throwable t){
            return currentServerIP;
        }
        
    }
    
    public static @Nullable Client getCurrentClientNet() {
        try {
            Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
            if (!(n instanceof ArcNetProvider)) return null;
            ArcNetProvider arc = (ArcNetProvider) n;
            return Reflect.getField(arc.getClass(), "client", arc);
        }catch(Throwable ignored){}
        return null;
    }
    
    @Override
    @MethodFuzzer(skip = true)
    public void test() {
        testCompleted = false;
        getCallableMain();
        assert getCurrentJar() != null : "Current Jar is null";
        
        try {
            TestingUtility.methodFuzzer(this, this.getClass().getDeclaredMethods(), true);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        Core.app.post(() -> {
            testCompleted = true;
        });
        
    }
}
