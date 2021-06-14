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
import Atom.Utility.Random;
import arc.Core;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class ScrollableDialog extends AtomicDialog {
    protected Table table = new Table();
    protected ScrollPane scrollPane = new ScrollPane(table);
    protected ArrayList<Runnable> onInit = new ArrayList<>();
    
    
    public ScrollableDialog() {
        this("Null");
    }
    
    public ScrollableDialog(String title, DialogStyle style) {
        super(title, style);
        
    }
    public void showSetup(){
        onResize(this::init);
        shown(this::init);
    }
    public void addRefreshAndClose(){
        addCloseButton();
        addNavButton("Refresh", Icon.refresh, this::init);
    }
    public ScrollableDialog(String title) {
        this(title, Core.scene.getStyle(DialogStyle.class));
    }
    
    public void onInit(Runnable r) {
        onInit.add(r);
    }
    
    
    protected abstract void setup();
    
    protected void init() {
        if (scrollPane != null){
            float x = scrollPane.getScrollPercentX();
            scrollPane = new ScrollPane(table);
            scrollPane.setScrollPercentX(x);
        }else scrollPane = new ScrollPane(table);
        cont.clear();
        table.clear();
        try {
            setup();
        }catch(VirtualMachineError v){
            throw new RuntimeException(v);
        }catch(Throwable t){
            table.add(t.toString()).growX().growY().color(Color.red);
            
        }
        for (Runnable r : onInit)
            r.run();
        table.row();
        cont.add(scrollPane).growX().growY();
    }
    
    protected void ad(Map<?, ?> map) {
        for (Map.Entry<?, ?> s : map.entrySet())
            ad(s.getKey(), s.getValue());
    }
    
    protected void ad(String name, Drawable i, BaseDialog bd) {
        table.button(name, i, bd::show).growX();
        table.row();
    }
    
    protected void ad(AtomicDialog od) {
        ad(od.icon, od);
    }
    
    protected void ad(Drawable i, BaseDialog od) {
        ad(od.title.getText().toString(), i, od);
    }
    
    protected void ad(BaseDialog bd) {
        ad(bd.title.getText().toString(), bd);
    }
    
    protected void ad(String name, BaseDialog od) {
        table.button(name, Icon.info, od::show).growX();
        table.row();
    }
    
    protected void ad(String Object, Callable<Object> callable) {
        Pool.submit(() -> {
            try {
                ad(Object, callable.call());
            }catch(Throwable t){
                WarningHandler.handleProgrammerFault(t);
            }
        });
    }
    
    protected void ad(Object text) {
        table.add(String.valueOf(text)).growX();
        table.row();
    }
    
    protected void ad(Object title, Object value) {
        ad(title, value, null);
    }
    
    protected void ad(Object title, Object value, Consumer<String> cs) {
        value = String.valueOf(value);
        title = String.valueOf(title);
        if (GlopionCore.colorPatchSettings) title = "[" + Random.getRandomHexColor() + "]" + title;
        Label l = new Label(title + ":");
        table.add(l).growX();
        String finalValue = String.valueOf(value);
        table.row();
        table.field(finalValue, s -> {
            Core.app.setClipboardText(finalValue);
            Interface.toast("Copied");
            if (cs != null) cs.accept(s);
        }).expandX().growX();
        table.row();
    }
}
