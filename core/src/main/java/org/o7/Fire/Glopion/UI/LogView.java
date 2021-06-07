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

import arc.Events;
import arc.input.KeyCode;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Event.EventExtended;

public class LogView extends ScrollableDialog {
    
    volatile String see = "";
    
    {
        icon = Icon.fileTextFill;
    }
    
    public LogView() {
        this.keyDown((key) -> {
            if (key == KeyCode.enter){
                execute();
            }
        });
        cont.table(t -> {
            t.field("", s -> {
                see = s;
            }).growX().tooltip("Javascript console");
            t.button(Icon.add, this::execute);
        }).growX();
        
        cont.row();
        StringBuilder sb = cont.add("Log\n").growX().growY().get().getText();
        Events.on(EventExtended.Log.class, s -> {
            sb.append(s.result).append("[white]\n");
        });
        cont.row();
    }
    
    @Override
    protected void setup() {
    
    }
    
    
    private void execute() {
        Log.info(">" + see);
        Log.info(Vars.mods.getScripts().runConsole(see));
    }
    
}
