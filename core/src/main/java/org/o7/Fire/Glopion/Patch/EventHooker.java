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

import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import org.o7.Fire.Glopion.Event.EventExtended;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.Module;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;
import org.o7.Fire.Glopion.Module.WorldModule;

import java.util.ArrayList;
import java.util.Map;

//Note: don't google "Hooker"
public class EventHooker extends ModsModule {
    public static ArrayList<Runnable> drawc = new ArrayList<>();
    
    
    public static void resets() {
        for (Map.Entry<Class<? extends ModsModule>, ModsModule> m : ModuleRegisterer.modules.entrySet()) {
            try {
                m.getValue().reset();
            }catch(Throwable t){
                WarningHandler.handleMindustry(t);
                Interface.showError(t);
            }
        }
        
    }
    
    @Override
    public void reset() throws Throwable {
        drawc.clear();
    }
    
    @Override
    public void start() {
        super.start();
        Vars.loadLogger();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Events.fire(EventExtended.Shutdown.class, new EventExtended.Shutdown());
        }));
        Events.on(EventType.ConfigEvent.class, s -> {
            ModuleRegisterer.invokeAllAs(WorldModule.class, worldModule -> worldModule.onTileConfig(s.player, s.tile, s.value));
        });
        Events.run(EventType.Trigger.update, () -> {
            ModuleRegisterer.invokeAll(Module::update);
        });
        Events.on(EventType.ClientLoadEvent.class, s -> {
            Core.settings.getBoolOnce("GlopionDisclaimer", () -> {
                Vars.ui.showCustomConfirm("[royal]Glopion[white]-[red]Warning", "Use this mods at your own risk", "Accept", "Accept", () -> { }, () -> { });
            });
            Core.settings.getBoolOnce("CrashReport1", () -> Vars.ui.showConfirm("Anonymous Data Reporter", "We collect your anonymous data e.g crash-log, to make your experience much worse", () -> {
            }));
            if (System.getProperty("Ozone-Foo") != null){
                Core.settings.getBoolOnce("OzoneFoo", () -> {
                    Vars.ui.showText("[royal]Ozone[white]-[red]Warning", "Foo client with ozone ????, are you a savage");
                });
            }
            //GlopionCore.moduleRegisterer.postInit();
            //SharedBoot.finishStartup();
            // setOzoneLogger();
        });
        Events.run(EventType.Trigger.draw, () -> {
            for (Runnable r : drawc) {
                r.run();
            }
        });
        Events.run(EventExtended.Game.Start, () -> {
            Log.debug("Server: " + InformationCenter.getCurrentServerIP() + ":" + InformationCenter.getCurrentServerPort());
            resets();
            ModuleRegisterer.invokeAllAs(WorldModule.class, WorldModule::onWorldLoad);
        });
        Events.run(EventExtended.Game.Stop, () -> {
            resets();
            ModuleRegisterer.invokeAllAs(WorldModule.class, WorldModule::onWoldUnload);
        });
        Events.run(EventExtended.Connect.Disconnected, () -> {
        
        });
        Events.on(EventType.StateChangeEvent.class, s -> {
            if (s.from.equals(GameState.State.playing) && s.to.equals(GameState.State.menu))
                Events.fire(EventExtended.Game.Stop);
            else if (s.from.equals(GameState.State.menu) && s.to.equals(GameState.State.playing))
                Events.fire(EventExtended.Game.Start);
            
        });
        
        Events.on(EventType.ClientPreConnectEvent.class, s -> {
            Events.fire(EventExtended.Connect.Connected);
        });
        Events.on(EventType.CommandIssueEvent.class, s -> {
        });
        Events.on(EventType.DepositEvent.class, s -> {
            if (s.player == null) return;
        });
        Events.on(EventType.WithdrawEvent.class, s -> {
            if (s.player == null) return;
        });
        
        Events.on(EventType.UnitCreateEvent.class, s -> {
        });
        Events.on(EventType.UnitChangeEvent.class, s -> {
            if (Vars.state.getState().equals(GameState.State.menu)) return;//spammy af
        });
        Events.on(EventType.UnitDestroyEvent.class, s -> {
            if (s.unit.getPlayer() != null) ;
            else ;
        });
        Events.on(EventType.UnitDrownEvent.class, s -> {
            if (s.unit.getPlayer() != null) ;
            else ;
        });
        
        Events.on(EventType.BlockBuildEndEvent.class, s -> {
            if (s.unit.getPlayer() == null) return;//boring
            if (s.breaking) ;
            else ;
        });
        Events.on(EventType.PlayerJoin.class, s -> {
            if (s.player == null) return;
        });
        Events.on(EventType.BlockDestroyEvent.class, s -> {
        });
        Events.on(EventType.BlockBuildBeginEvent.class, s -> {
            if (s.breaking) ;
            else ;
        });
        Events.on(EventType.PlayerLeave.class, s -> {
            if (s.player == null) return;//boring
        });
        Events.on(EventType.ConfigEvent.class, s -> {
            if (s.player != null) ;
            else ;
        });
    }
}
