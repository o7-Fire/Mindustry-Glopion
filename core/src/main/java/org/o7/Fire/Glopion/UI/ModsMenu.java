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


import arc.scene.style.Drawable;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import org.o7.Fire.Glopion.Experimental.Evasion.Identification;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Patch.Translation;

import java.util.ArrayList;

public class ModsMenu extends ScrollableDialog {
    static ArrayList<Dialog> dialogs = new ArrayList<>();
    static ObjectMap<Dialog, Drawable> dialogDrawableHashMap = new ObjectMap<>();
    TextButton.TextButtonStyle textButtonStyle;
    
    public ModsMenu() {
        super("Mods Menu");
        textButtonStyle = Styles.clearPartialt;
        addNavButton("o7-Discord", Icon.discord, () -> {
            Interface.openLink("https://discord.o7fire.tk");
        });
    
    }
    
    public static void add(Dialog dialog) {
        dialogs.add(dialog);
    }
    
    public static void add(Dialog dialog, Drawable d) {
        add(dialog);
        dialogDrawableHashMap.put(dialog, d);
    }
    
    public void setup() {
        table.clear();
        generic();
        table.button("Reset UID", Icon.refresh, textButtonStyle, () -> Vars.ui.showConfirm("Reset UID", "Reset all uuid and usid", () -> {
            Vars.ui.loadfrag.show("Changing ID");
            Identification.changeID(() -> {
                Vars.ui.loadfrag.hide();
                try {
                    Vars.ui.showInfo("Changed " + Identification.getKeys().size() + " ID");
                }catch(Throwable t){
                    WarningHandler.handleMindustry(t);
                }
            });
    
        })).growX().row();
    
    }
    
    void generic() {
        for (Dialog o : dialogs)
            ad(o);
    }
    
    public void ad(Dialog d) {
        if (d instanceof AtomicDialog){
            AtomicDialog dialog = (AtomicDialog) d;
            table.button(dialog.getTitle(), dialog.icon, textButtonStyle, dialog::show).growX();
        }else{
            String title = d.title.getText().toString();
            if (title.isEmpty()) title = Translation.get(d.getClass().getCanonicalName());
            title = Translation.colorized(title);
            table.button(title, dialogDrawableHashMap.get(d, Icon.book), d::show).growX();
        }
        table.row();
    }
}
