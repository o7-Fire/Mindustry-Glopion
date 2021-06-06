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

package org.o7.Fire.Glopion.Module.Patch;

import Atom.Reflect.FieldTool;
import Atom.Reflect.Reflect;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.graphics.LoadRenderer;
import mindustry.graphics.Pal;
import mindustry.input.DesktopInput;
import mindustry.input.MobileInput;
import org.o7.Fire.Glopion.Gen.Callable;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningReport;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Patch.Mindustry.DesktopInputPatched;
import org.o7.Fire.Glopion.Patch.Mindustry.MobileInputPatched;
import org.o7.Fire.Glopion.Patch.Mindustry.NetPatched;

import java.lang.reflect.Field;

public class VarsPatch extends ModsModule {
    public static Table menu;
    
    {
    
    }
    
    @Override
    public void preInit() {
        //java 8 only
        try {
            Field orange = LoadRenderer.class.getDeclaredField("orange"), color = LoadRenderer.class.getDeclaredField("color");
            Color c = new Color(Pal.darkMetal).lerp(Color.black, 0.5f);
            FieldTool.setFinalStatic(color, c);
            FieldTool.setFinalStatic(orange, "[#" + c + "]");
        }catch(Throwable t){
            new WarningReport().setProblem("Failed to patch final field LoadRenderer : " + t.getMessage()).setWhyItsAProblem("No loading screen color hack").setHowToFix("try use java 8, if can").setLevel(WarningReport.Level.warn).report();
        }
    }
    
    
    @Override
    public void init() {
        Log.infoTag("Ozone", "Patching");
        Vars.enableConsole = true;
        Log.debug("Ozone-Debug: @", "Debugs, peoples, debugs");
        if (Vars.ui != null){
            try {
                Vars.ui.chatfrag.addMessage("gay", "no");
            }catch(NoSuchMethodError t){
                System.setProperty("Client-Foo", "1");
            }
        }
        
        if (Vars.control != null){
            if (Vars.control.input instanceof MobileInput){
                Log.debug("its mobile input");
                Vars.control.input = new MobileInputPatched();
            }else if (Vars.control.input instanceof DesktopInput){
                Log.debug("its desktop input");
                Vars.control.input = new DesktopInputPatched();
            }else Log.warn("Vars.control.input not patched");
        }
        try {
            menu = Reflect.getField(Vars.ui.menufrag.getClass(), "container", Vars.ui.menufrag);
        }catch(Throwable ignored){
            menu = Vars.ui.settings.game.table().growX().get();
        }
        if (Vars.net != null){
            Vars.net = new NetPatched(Vars.net);
            InformationCenter.callable = new Callable(Vars.net);
        }
        Log.infoTag("Ozone", "Patching Complete");
    }
    
}
