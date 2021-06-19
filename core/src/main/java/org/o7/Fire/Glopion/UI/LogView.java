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

import Atom.Utility.Pool;
import arc.Core;
import arc.input.KeyCode;
import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Patch.AtomicLogger;

public class LogView extends ScrollableDialog {
    
    String see = "";
    
    {
        icon = Icon.fileTextFill;
    }
    
    String lastLog = "";
    int last = 0;
    
    public LogView() {
        this.keyDown((key) -> {
            if (key == KeyCode.enter){
                execute();
            }
        });
        addNavButton("Execute",Icon.rightOpen,this::execute);
    }
    
    ;
    
    @Override
    protected void setup() {
        table.table(t -> {
            t.field(see, s -> {
                see = s;
            }).growX().tooltip("Javascript console");
        }).growX();
        
        table.row();
        //StringBuilder sb = table.add("Log\n").growX().growY().get().getText();
        table.label(this::getLog).growX().growY();
        table.row();
    }
    
    private String getLog() {
        if (last != AtomicLogger.logBuffer.size){
            StringBuilder sb = new StringBuilder();
            for (int i = AtomicLogger.logBuffer.size - 1; i >= 0; i--) {
                sb.append(AtomicLogger.logBuffer.get(i)).append("[white]\n");
            }
            last = AtomicLogger.logBuffer.size;
            lastLog = sb.toString();
        }
        return lastLog;
    }
    /** ???? */
    private void execute() {
        Pool.daemon(()->{
            Core.app.post(()->{
                String eval = Vars.mods.getScripts().runConsole(see);
                AtomicLogger.logBuffer.add(eval);
                AtomicLogger.logBuffer.add(">" + see);
            });
        }).start();
    }
    
}
