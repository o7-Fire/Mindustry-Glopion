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

import Atom.Reflect.Reflect;
import Atom.Utility.Digest;
import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.MobileButton;
import mindustry.ui.Styles;
import mindustry.ui.fragments.MenuFragment;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.TextManager;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.UI.*;

import static mindustry.Vars.ui;

public class UIPatch extends ModsModule {
    public static Dialog.DialogStyle ozoneStyle;
    public static Table settingsTable;
    {
        dependency.add(VarsPatch.class);
        dependency.add(TextManager.class);
    }
    
    private void onResize() {
        
        if (VarsPatch.menu != null){
            if (Vars.testMobile) try {
                Reflect.getMethod(MenuFragment.class, "buildMobile", ui.menufrag).invoke(ui.menufrag);
            }catch(Throwable ignored){}
            if (Vars.mobile || Vars.testMobile){
                if (Core.graphics.isPortrait()) VarsPatch.menu.row();
                VarsPatch.menu.add(new MobileButton(Icon.info, TextManager.get("Glopion-Menu"), () -> GlopionCore.modsMenu.show()));
            }else{
    
                VarsPatch.menu.button(TextManager.get("Glopion-Menu"), Icon.file, GlopionCore.modsMenu::show).growX().bottom();
            }
        }
        if(settingsTable != null){
            settingsTable.clear();
            settingsTable.add("Glopion").growX().center().row();
            if (Vars.mobile || Vars.testMobile){
                if (Core.graphics.isPortrait()) settingsTable.row();
                settingsTable.add(new MobileButton(Icon.info, TextManager.get("Glopion-Menu"), () -> GlopionCore.modsMenu.show()));
            }else{
    
                settingsTable.button(TextManager.get("Glopion-Menu"), Icon.file, GlopionCore.modsMenu::show).growX().bottom();
            }
        }
    }
    
    
    @Override
    public void start() {
        super.start();
        if(Vars.headless)return;
        ozoneStyle = new Dialog.DialogStyle() {
            {
                stageBackground = Styles.none;
                titleFont = Fonts.def;
                background = Tex.windowEmpty;
                titleFontColor = Pal.accent;
            }
        };
        //GlopionCore.commFrag = new CommandsListFrag();
        AtomicDialog.instance = new AtomicDialog() {{addCloseButton();}};
        ui.research.title.setText(TextManager.get("techtree"));
        ModsMenu.add(ui.database, Icon.book);
        ModsMenu.add(ui.research, Icon.tree);
        ModsMenu.add(new BundleViewer());
        ModsMenu.add(GlopionCore.worldInformation = new WorldInformation());
        ModsMenu.add(new OzonePlaySettings());
        ModsMenu.add(new EnvironmentInformation());//mmm
        ModsMenu.add(new LogView());
        ModsMenu.add(new ExperimentDialog());
        ModsMenu.add(new OptionsDialog());
    
        GlopionCore.modsMenu = new ModsMenu();
        GlopionCore.glopionHud = new HudMenu();
        Cell<Table> h = ui.settings.game.row().table().growX();
        h.row();
        settingsTable = h.get();
        //GlopionCore.commFrag.build(Vars.ui.hudGroup);
        ui.logic.buttons.button("Show Hash", Icon.list, () -> {
            new ScrollableDialog("Hash Code") {
                @Override
                protected void setup() {
                    String src = ui.logic.canvas.save();
                    int hash = src.hashCode();
                    long lhash = Digest.longHash(src);
                    table.button(hash + "", () -> {
                        Interface.copyToClipboard(hash + "");
                    }).tooltip("Copy").growY();
                    table.button(lhash + "", () -> {
                        Interface.copyToClipboard(lhash + "");
                    }).tooltip("Copy").growY();
                }
            }.show();
        }).size(210f, 64f);
        
        Events.on(EventType.ResizeEvent.class, c -> {
            onResize();
        });
        onResize();
    }
    
}
