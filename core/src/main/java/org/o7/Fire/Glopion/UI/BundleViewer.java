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
import arc.scene.ui.Label;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.TextManager;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

public class BundleViewer extends ScrollableDialog {
    String see = "";
    
    {
        icon = Icon.bookOpen;
    }
    
    @Override
    public void showSetup() {
        super.showSetup();
        addNavButton(TextManager.translate("Save To File"), Icon.save, () -> {
            try {
                Vars.ui.showInfo(TextManager.dumpBundle().getAbsolutePath());
            }catch(IOException e){
                WarningHandler.handleMindustryUserFault(e);
            }
            try {
                Properties properties = new Properties();
                for (ObjectMap.Entry<String, String> s : Core.bundle.getProperties().entries()) {
                    properties.setProperty(s.key, s.value);
                }
                Vars.ui.showInfo(TextManager.dumpBundle("bundle", properties).getAbsolutePath());
            }catch(IOException e){
                WarningHandler.handleMindustryUserFault(e);
            }
        });
    }
    
    @Override
    protected void setup() {
        cont.table(t -> {
            t.button(Icon.cancel, () -> {
                see = "";
                init();
            }).tooltip(TextManager.translate("Clear"));
            t.button(Icon.zoom, () -> {
                Vars.ui.showTextInput(TextManager.translate("Search"), "", see, s -> {
                    try {
                        see = s;
                        init();
                    }catch(Throwable te){
                        Vars.ui.showException(te);
                    }
                });
            }).tooltip(TextManager.translate("Search"));//refresh button in disguise
        }).growX();
        cont.row();
        ad(TextManager.bundle);
        ad(Core.bundle.getProperties());
    }
    
    void ad(ObjectMap<String, String> map) {
        for (ObjectMap.Entry<String, String> s : map.entries()) {
            if (see.isEmpty() || (see.contains(s.key.toLowerCase()) || see.contains(s.value.toLowerCase()))){
                ad(s.key, s.value, ss -> {
                    map.put(s.key, ss);
                });
            }
        }
    }
    
    protected void ad(Object title, Object value, Consumer<String> changed) {
        if (value == null) value = "null";
        //if (BaseSettings.colorPatch) title = "[" + Random.getRandomHexColor() + "]" + title;
        Label l = new Label(title + ":");
        table.add(l).growX();
        String finalValue = String.valueOf(value);
        table.row();
        Object finalTitle = title;
        table.button(finalValue, () -> {
            try {
                Vars.ui.showTextInput(TextManager.translate("Change to"), String.valueOf(finalTitle), finalValue, s -> {
                    changed.accept(s);
                });
            }catch(Throwable t){
                WarningHandler.handleMindustryUserFault(t);
            }
        }).growX().tooltip(TextManager.translate("Raw"));
        table.row();
    }
}
