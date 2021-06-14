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


import Atom.Reflect.Reflect;
import arc.Core;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Module.Patch.UIPatch;
import org.o7.Fire.Glopion.Patch.Translation;

public class OzoneMenu extends AtomicDialog {
    
    private Table tB;
    
    public OzoneMenu() {
        super("Glopion HUD", UIPatch.ozoneStyle);
        
    }
    
    public static void showHud() {
        if (!Vars.ui.hudfrag.shown) toggleHUD();
    }
    
    public static void toggleHUD() {
        try {
            Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
        }catch(Throwable ignored){
        }
    }
    
    @Override
    public void hide() {
        super.hide();
        showHud();
    }
    
    public void setup() {
    
        cont.clear();
        cont.top();
        cont.row();
        tB = cont.table().top().growX().get();
        cont.row();
        tB.button(Translation.get("ozone.commandsUI"), Icon.commandRally, () -> {
            Core.app.post(this::hide);
            //GlopionCore.commFrag.toggle();
        }).growX();
        ad(GlopionCore.worldInformation, Icon.chartBar);
        ad(GlopionCore.modsMenu, Icon.file);
        cont.row();
        
    }
    
    void ad(Table t, BaseDialog baseDialog, Drawable d) {
        t.button(Translation.get(baseDialog.getClass().getName()), d, () -> {
            hide();
            baseDialog.show();
        }).growX();
    }
    
    void ad(BaseDialog baseDialog, Drawable d) {
        tB.button(Translation.get(baseDialog.getClass().getName()), d, () -> {
            hide();
            baseDialog.show();
        }).growX();
    }
    
    void ad(BaseDialog baseDialog) {
        tB.button(Translation.get(baseDialog.getClass().getName()), () -> {
            hide();
            baseDialog.show();
        }).growX();
    }
    
    
}
