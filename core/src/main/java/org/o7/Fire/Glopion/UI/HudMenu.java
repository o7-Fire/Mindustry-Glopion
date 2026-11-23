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


import arc.Core;
import arc.scene.Action;
import arc.scene.Scene;
import arc.scene.style.Drawable;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.TextManager;

public class HudMenu extends AtomicDialog {
    
    private Table tB;
    
    public HudMenu() {
        super("Glopion HUD", Core.scene.getStyle(Dialog.DialogStyle.class));
        setCentered(true);
        setSize(420f, 190f);
    }
    
    public static void showHud() {
        if (Vars.ui != null && Vars.ui.hudfrag != null && !Vars.ui.hudfrag.shown) Vars.ui.hudfrag.shown = true;
    }
    
    public static void toggleHUD() {
        if (Vars.ui != null && Vars.ui.hudfrag != null) Vars.ui.hudfrag.shown = !Vars.ui.hudfrag.shown;
    }
    
    @Override
    public Dialog show(Scene stage, Action action) {
        setCentered(true);
        setSize(420f, 190f);
        return super.show(stage, action);
    }
    
    @Override
    public void hide() {
        super.hide();
        showHud();
    }
    
    private void open(BaseDialog dialog) {
        hide(null);
        showHud();
        if (dialog != null) dialog.show();
    }
    
    public void setup() {
        cont.clear();
        cont.center();
        cont.row();
        tB = cont.table().center().growX().pad(12f).get();
        if (GlopionCore.worldInformation != null) ad(GlopionCore.worldInformation, Icon.chartBar);
        if (GlopionCore.modsMenu != null) ad(GlopionCore.modsMenu, Icon.file);
        setCentered(true);
        setSize(420f, 190f);
    }
    
    void ad(BaseDialog baseDialog, Drawable d) {
        tB.button(TextManager.get(baseDialog.getClass().getName()), d, () -> open(baseDialog)).growX().pad(6f);
    }
    
    void ad(BaseDialog baseDialog) {
        tB.button(TextManager.get(baseDialog.getClass().getName()), () -> open(baseDialog)).growX().pad(6f);
    }
}
