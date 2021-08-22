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


import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import org.o7.Fire.Glopion.Experimental.Evasion.Identification;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.TextManager;

import java.util.Comparator;

public class ModsMenu extends ScrollableDialog {
    static Seq<Dialog> dialogs = new Seq<>();
    static ObjectMap<Dialog, Drawable> dialogDrawableHashMap = new ObjectMap<>();
    TextButton.TextButtonStyle textButtonStyle;
    
    public ModsMenu() {
        super("Mods Menu");
        textButtonStyle = Styles.clearPartialt;
        
        
    }
    
    protected void test(Seq<Dialog> dialoggg, Runnable finalRun) {
        if (dialoggg.size == 0){
            finalRun.run();
            return;
        }
        Dialog d = dialoggg.remove(0);
        AtomicDialog.showTest(d, () -> test(dialoggg, finalRun));
    }
    
    @Override
    protected void test(Runnable after) {
        Seq<Dialog> d = new Seq<>(dialogs);
        test(d, after);
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
    children.sort(new Comparator<Element>() {
        @Override
        public int compare(Element o1, Element o2) {
            if(o1.name == null || o2.name == null)return 0;
            return Strings.stripColors(o1.name).compareTo(Strings.stripColors(o2.name));
        }
    });
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
            if (title.isEmpty()) title = TextManager.get(d.getClass().getCanonicalName());
            else title = TextManager.get(title);
            table.button(title, dialogDrawableHashMap.get(d, Icon.book), d::show).growX();
        }
        table.row();
    }
}
