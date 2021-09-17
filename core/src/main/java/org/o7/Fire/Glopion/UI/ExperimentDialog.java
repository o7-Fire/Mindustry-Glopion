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

import Atom.Struct.FunctionalPoolObject;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Experimental.*;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.TextManager;
import org.o7.Fire.Glopion.UI.Element.PlotTableExperiment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

public class ExperimentDialog extends ScrollableDialog {
    {
        icon = Icon.production;
    }
    
    protected TreeSet<Class<? extends Experimental>> experimental = new TreeSet<>(new Comparator<Class<? extends Experimental>>() {
        @Override
        public int compare(Class<? extends Experimental> o1, Class<? extends Experimental> o2) {
            return o1.getSimpleName().compareTo(o2.getSimpleName());
        }
    });
    protected HashMap<Class<? extends Experimental>, Experimental> cache = new HashMap<>();
    public ExperimentDialog() {
        experimental.addAll(Arrays.asList(RelayChatToWebhook.class, LogToDiscordWebhook.class, ThreadStackTrace.class, OutOfMemory.class, LockAllContent.class, SwingBox.class, UnlockAllContent.class));
        if (!Vars.mobile) experimental.addAll(InformationCenter.getExtendedClass(Experimental.class));
        experimental.add(EffectsDialog.class);//added in 0.7.2
        experimental.add(PlotTableExperiment.class);
        StringBuilder sb = FunctionalPoolObject.StringBuilder.obtain();
        sb.append("Experimental: [");
        for (Class<?> c : experimental) {
            sb.append(c.getSimpleName()).append(".class").append(", ");
        }
        sb.append("]");
        Log.debug(sb.toString());
        FunctionalPoolObject.StringBuilder.free(sb);
    }
    
    
    @Override
    protected void setup() {
        try {
            for (Class<? extends Experimental> c : experimental) {
                table.button(TextManager.get(c.getName()), () -> {
                    try {
                        if (cache.containsKey(c)){
                            cache.get(c).run();
                        }
    
                        Experimental e = c.getDeclaredConstructor().newInstance();
                        e.run();
                        if (e.reusable()) cache.put(c, e);
    
                    }catch(Throwable t){
                        WarningHandler.handleMindustryUserFault(t);
                    }
                }).growX().row();
                
            }
        }catch(Throwable t){
            WarningHandler.handleProgrammerFault(t);
            ad(t.toString());
        }
    }
}
