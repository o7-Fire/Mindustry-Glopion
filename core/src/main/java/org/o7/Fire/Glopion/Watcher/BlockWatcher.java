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

package org.o7.Fire.Glopion.Watcher;

import Atom.Reflect.FieldTool;
import Atom.Time.Timer;
import Atom.Utility.Random;
import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.input.KeyCode;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.logic.LogicDisplay;
import org.o7.Fire.Glopion.Brain.Classification.NodeNSFWJS;
import org.o7.Fire.Glopion.Commands.Pathfinding;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.GraphicsModule;
import org.o7.Fire.Glopion.Module.ModsModule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BlockWatcher extends ModsModule implements GraphicsModule {
    private static Tile target = null;
    
    public static void stub() {
    
    }
    
    public void start() {
        super.start();
        try {
            GlopionCore.imageClassifier = new NodeNSFWJS(new URL(GlopionCore.nsfwJsUrlSettings));
            Log.infoTag("Image Classifier", "Provider: " + GlopionCore.nsfwJsUrlSettings);
        }catch(MalformedURLException e){
            WarningHandler.handleMindustry(e);
            Log.err("Failed to register Image Classifier");
        }
    }
    
    final HashSet<LogicDisplay.LogicDisplayBuild> logicDisplay = new HashSet<>();
    Timer timer = new Timer(TimeUnit.MILLISECONDS, 100);
    Future<ArrayList<Building>> taskLogicDisplayBuild = null;
    HashMap<byte[], byte[]> hashMapLiterally = new HashMap<>();
    
    
    protected void onTaskLogicDisplayDone() {
        try {
            for (Building b : taskLogicDisplayBuild.get()) {
                if (b instanceof LogicDisplay.LogicDisplayBuild) logicDisplay.add((LogicDisplay.LogicDisplayBuild) b);
            }
        }catch(InterruptedException | ExecutionException e){
            WarningHandler.handleMindustry(e);
        }finally{
            taskLogicDisplayBuild = null;
        }
    }
    
    public void update() {
        
        if (Vars.state.isPlaying()){
            
            if (taskLogicDisplayBuild.isDone()){
                onTaskLogicDisplayDone();
            }
            if (GlopionCore.blockDebugSettings){
                if (Core.input.keyDown(KeyCode.controlLeft))
                    if (Core.input.keyDown(KeyCode.mouseLeft)) target = Interface.getMouseTile();
                
                if (target != null){
                    StringBuilder sb = new StringBuilder();
                    if (target.build != null){
                        sb.append(FieldTool.getFieldDetails(target.build).replace("\n", "[white]\n"));
                        //if (target.build instanceof LogicBlock.LogicBuild) sb.append("CodeHash=").append(((LogicBlock.LogicBuild) target.build).code.hashCode()).append("[white]\n");
                    }else sb.append(FieldTool.getFieldDetails(target).replace("\n", "[white]\n"));
                    sb.append("SafetyIndex:").append(Pathfinding.isSafe(target)).append("[white]\n");
                    Vars.ui.hudfrag.setHudText(sb.toString());
                }
                
            }
        }
    }
    
    @Override
    public void draw() {
        if (!Vars.state.isPlaying()) return;
        if (!timer.get()) return;
        
        if (taskLogicDisplayBuild == null && logicDisplay.size() == 0)
            taskLogicDisplayBuild = Interface.getBuilds(s -> s instanceof LogicDisplay.LogicDisplayBuild);
        if (logicDisplay.size() != 0){
            LogicDisplay.LogicDisplayBuild yes = Random.getRandom(logicDisplay);
            logicDisplay.remove(yes);
            if (yes.buffer != null){
                Draw.blend(Blending.disabled);
                Draw.draw(Draw.z(), () -> {
                    if (yes.buffer != null){
                        Draw.rect(Draw.wrap(yes.buffer.getTexture()),
                                0,
                                0,
                                (float) yes.buffer.getWidth(),
                                (float) (-yes.buffer.getHeight()));
                    }
                    
                });
                Draw.blend();
            }
            
        }
    }
}
