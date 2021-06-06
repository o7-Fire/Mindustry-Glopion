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


import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Experimental.Evasion.Identification;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Patch.Translation;

import java.util.ArrayList;

public class ModsMenu extends ScrollableDialog {
    static ArrayList<AtomicDialog> dialogs = new ArrayList<>();
    
    public ModsMenu() {
        super("Mods Menu");
        addNavButton("o7-Discord", Icon.discord, () -> {
            Interface.openLink("https://discord.o7fire.tk");
        });
        
    }
    
    public static void add(AtomicDialog dialog) {
        dialogs.add(dialog);
    }
    
    
    public void setup() {
        table.clear();
        table.button("@mods", Icon.book, Vars.ui.mods::show).growX();// a sacrifice indeed
        table.row();
        table.row();
        generic();
        table.button("Reset UID", Icon.refresh, () -> Vars.ui.showConfirm("Reset UID", "Reset all uuid and usid", () -> {
            Vars.ui.loadfrag.show("Changing ID");
            Identification.changeID(() -> {
                Vars.ui.loadfrag.hide();
                try {
                    Vars.ui.showInfo("Changed " + Identification.getKeys().size() + " ID");
                }catch(Throwable ignored){}
            });
            
        })).growX().row();
        
    }
    
    void generic() {
        for (AtomicDialog o : dialogs)
            ad(o);
    }
    
    public void ad(AtomicDialog dialog) {
        table.button(Translation.colorized(dialog.getTitle()), dialog.icon, dialog::show).growX();
        table.row();
    }
}
