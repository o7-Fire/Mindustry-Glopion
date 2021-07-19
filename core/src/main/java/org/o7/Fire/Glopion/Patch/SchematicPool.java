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

import Atom.Utility.Cache;
import Atom.Utility.Encoder;
import Atom.Utility.Pool;
import mindustry.Vars;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import org.o7.Fire.Glopion.Internal.Repo;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.Shared.WarningReport;
import org.o7.Fire.Glopion.Module.ModsModule;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Future;
//TODO don't
public class SchematicPool extends ModsModule {
    
    
    @Override
    public void loadAsync() {
        Repo rc = Repo.getRepo();
        assert rc != null;
        URL u = rc.getResource("src/schematic-pool.txt");
        if (u == null){
            try {
                Vars.schematics.all().add(Schematics.readBase64("bXNjaAF4nBXHMQ7CMAwF0B+oxMAJumXrBrdhtxy3WGqcKD8Dx0d920NCumEJqYZVJbaZd4+SOfRN/VqV6Uo8i1GH9+ktgIRHNVIOw5XLkrB+2il82c8yZ+t5GHuL4nHc/3xKHH4="));
            }catch(Throwable t){
                WarningHandler.handleProgrammerFault(t);
            }
            throw new RuntimeException("Can't find src/schematic-pool.txt");
        }
        try {
            ArrayList<Future<Schematic>> future = new ArrayList<>();
            for (String s : Encoder.readString(u.openStream()).split("\n"))
                future.add(Pool.submit(() -> {
                    try {
                        URL neu = Cache.http(new URL(s));
                        return Schematics.read(neu.openStream());
                    }catch(Throwable e){
                        try {
                            new File(Cache.http(new URL(s)).getFile()).delete();
                        }catch(Throwable ignored){}
                        while (e.getCause() != null) e = e.getCause();
                        new WarningReport(e).setWhyItsAProblem("Schematic gone").setHowToFix("Ask volas").setLevel(WarningReport.Level.warn).report();
                    }
                    return null;
                }));
            int i = 0;
            for (Future<Schematic> s : future) {
                try {
                    Schematic se = s.get();
                    if (se != null){
                        se.removeSteamID();
                        synchronized (Vars.schematics.all()) {
                            Vars.schematics.all().add(se);
                            i++;
                        }
                    }
                    
                }catch(Throwable ignored){}
            }
            new WarningReport("Loaded: " + i + " remote schematics").setWhyItsAProblem("Its from volas cdn").setLevel(WarningReport.Level.info).report();
        }catch(Throwable t){
            WarningHandler.handleMindustry(t);
        }
    }
    
    public void start() {
        super.start();
    }
}
