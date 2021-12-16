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

/* o7 Inc 2021 Copyright
  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package org.o7.Fire.Glopion.Internal;


import Atom.Exception.ShouldNotHappenedException;
import Atom.Reflect.Reflect;
import Atom.String.WordGenerator;
import Atom.Struct.Filter;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.net.ValidateException;
import mindustry.server.ServerControl;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.sandbox.ItemSource;
import org.jetbrains.annotations.NotNull;
import org.o7.Fire.Glopion.GlopionCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Future;

import static arc.Core.settings;
import static mindustry.Vars.player;
import static mindustry.Vars.ui;

public class Interface {
    @Deprecated
    public static final ObjectMap<String, String> bundle = new ObjectMap<>();
    public static Map<Integer, ArrayList<Building>> buildingCache = Collections.synchronizedMap(new WeakHashMap<>());
    public static String fancyBoxBorder = "-+", fancyBoxAcceptDeclineSeparator = " | ", fancyBoxIgnore = "-ALBEITIGNORETHISLINE";
    public static boolean fancyBoxDebug = true;
    private static long lastToast = 0;
    
    public static void openLink(String url) {
        showConfirm("Open URL", "Are you sure want to open\n \"" + url + "\"", () -> {
            Pool.submit(() -> {
                if (!Core.app.openURI(url)){
                    ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(url);
                }
            });
        });
    }
    
    public static void toast(String text) {
        if (Vars.ui == null){
            Events.on(EventType.ClientLoadEvent.class, se -> toast(text));
            return;
        }
        Table table = new Table();
        table.touchable = Touchable.disabled;
        table.setFillParent(true);
        table.actions(Actions.fadeOut(4.0F, Interp.fade), Actions.remove());
        table.bottom().add(text).style(Styles.outlineLabel).padBottom(80);
        Core.scene.add(table);
    }
    
    public static void showInput(String key, String title, String about, Cons<String> s) {
        if (Vars.headless){
            getConsoleInputAsync(title, about, se -> {
                Core.settings.put(key, se);
                s.get(se);
            });
        }else{
            Runnable e = () -> Vars.ui.showTextInput(title, about, 1000, Core.settings.getString(key, ""), se -> {
                Core.settings.put(key, se);
                s.get(se);
            });
            runOnUI(e);
        }
    }
    
    public static void getConsoleInputAsync(String title, String about, Cons<String> s) {
        getConsoleInputAsync(title, about, null, s);
    }
    
    public static ServerControl serverControl = null;
    
    public static void getConsoleInputAsync(String title, String about, final Map<String, String> allowedDescrption, Cons<String> s) {
        getConsoleInputAsync(fancyBox(title, about), allowedDescrption, s);
    }
    
    public static ServerControl getServerControl() {
        if (serverControl != null) return serverControl;
        try {
            for (ApplicationListener e : Core.app.getListeners()) {
                if (e instanceof ServerControl){
                    return serverControl = (ServerControl) e;
                }
            }
        }catch(Exception e){}
        return null;
    }
    
    public static void getConsoleInputAsync(String prompt, final Map<String, String> allowedDescription, Cons<String> s) {
        if (Vars.headless) {
            if (allowedDescription == null) throw new NullPointerException("need allowedDescription in server version");
            for (final Map.Entry<String, String> a : allowedDescription.entrySet()) {
                getServerControl().handler.register(a.getKey(), a.getValue(), ss -> {
                    s.get(a.getKey());
                    for (String b : allowedDescription.keySet())
                        getServerControl().handler.removeCommand(b);
                });
            }
            Log.info(System.lineSeparator() + prompt);
            return;
        }
        Pool.submit(() -> {
            
            String ss = null;
            while (ss == null) {
                try {
                    System.out.println(prompt);
                    if (allowedDescription != null) {
                        String[] allowed = new String[allowedDescription.size()];
                        int i = 0;
                        for (String sss : allowedDescription.keySet()) {
                            allowed[i++] = sss;
                        }
                        ss = getConsoleInput(allowed);
                    } else ss = getConsoleInput();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            String finalSs = ss;
            Core.app.post(() -> {
                s.get(finalSs);
            });
            
        });
    }
    
    public static void showInput(String about, Cons<String> s) {
        showInput(Reflect.getCallerClassStackTrace().toString(), about, s);
        
    }
    
    public static void showInput(String key, String about, Cons<String> s) {
        showInput(key, about, about, s);
    }
    
    public static Tile getMouseTile() {
        return Vars.world.tileWorld(Vars.player.mouseX, Vars.player.mouseY);
    }
    
    public static void showToast(String text) {
        showToast(Icon.ok, text, 1000);
    }
    
    public static void showToast(String text, long duration) {
        showToast(Icon.ok, text, duration);
    }
    
    public static void showToast(Drawable icon, String text, long duration) {
        if (Vars.ui == null){
            Events.on(EventType.ClientLoadEvent.class, se -> showToast(icon, text, duration));
            return;
        }
        if (!Vars.state.isMenu()){
            scheduleToast(() -> {
                Sounds.message.play();
                Table table = new Table(Tex.button);
                table.update(() -> {
                    if (Vars.state.isMenu() || !Vars.ui.hudfrag.shown){
                        table.remove();
                    }
                    
                });
                table.margin(12.0F);
                table.image(icon).pad(3.0F);
                table.add(text).wrap().width(280.0F).get().setAlignment(1, 1);
                table.pack();
                Table container = Core.scene.table();
                container.top().add(table);
                container.setTranslation(0.0F, table.getPrefHeight());
                container.actions(Actions.translateBy(0.0F, -table.getPrefHeight(), 1.0F, Interp.fade), Actions.delay(2.5F), Actions.run(() -> {
                    container.actions(Actions.translateBy(0.0F, table.getPrefHeight(), 1.0F, Interp.fade), Actions.remove());
                }));
            }, duration);
        }
    }
    
    private static void scheduleToast(Runnable run, long duration) {
        long since = Time.timeSinceMillis(lastToast);
        if (since > duration){
            lastToast = Time.millis();
            run.run();
        }else{
            Time.runTask((float) (duration - since) / 1000.0F * 60.0F, run);
            lastToast += duration;
        }
        
    }
    
    public static void showInfo(String s) {
        runOnUI(() -> Vars.ui.showInfo(s));
    }
    
    public static void runOnUI(Runnable r) {
        if (Vars.headless) return;//futile
        if (Vars.ui == null || Core.scene == null) Events.on(EventType.ClientLoadEvent.class, s -> r.run());
        r.run();
    }
    
    public static void showWarning(String title, String description) {
        runOnUI(() -> Vars.ui.showErrorMessage(title + "\n" + description));
        if (Vars.headless){
            Log.warn("[@]: @", title, description);
        }
    }
    
    @Deprecated
    public static String getBundle(String key, String def) {
        return TextManager.get(key, def);
    }
    
    public static void dropItem() {
        try {
            Call.dropItem(Mathf.random(120f));
        }catch(ValidateException ignored){}
    }
    
    public static Player searchPlayer(String s) {
        Player target = null;
        if (s == null) return null;
        try {//try ID search
            int id = Integer.parseInt(s);
            target = Groups.player.find(f -> f.id == id);
        }catch(NumberFormatException ignored){}
        if (target == null)// if still not found
            target = Groups.player.find(f -> f.name().equals(s) || f.name.startsWith(s));
        return target;
    }
    
    public static boolean depositItem(Building tile) {
        if (tile == null || !tile.isValid() || tile.items == null || !tile.interactable(player.team()) || player.unit().item() == null)
            return false;
        int amount = Math.min(1, tile.getMaximumAccepted(player.unit().item()));
        if (amount > 0){
            int accepted = tile.acceptStack(player.unit().item(), Vars.player.unit().stack.amount, player.unit());
            try {
                Call.transferItemTo(player.unit(), player.unit().item(), accepted, player.unit().x, player.unit().y, tile);
            }catch(ValidateException e){
                return false;
            }
            return true;
        }
        return false;
    }
    
    public static boolean withdrawItem(Building tile, Item item) {
        if (tile == null || !tile.isValid() || tile.items == null || !tile.items.has(item) || !tile.interactable(player.team()))
            return false;
        int amount = Math.min(1, player.unit().maxAccepted(item));
        if (amount > 0){
            try {
                Call.requestItem(player, tile, item, amount);
            }catch(ValidateException e){
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Deprecated
    public synchronized static void registerWords(String key, String value) {
        TextManager.registerWords(key, value);
    }
    
    @Deprecated
    public synchronized static void registerWords(String key) {
        registerWords(key, key);
    }
    
    public static Future<ArrayList<Tile>> getGroupTiles(Tile main, Filter<Tile> filter) {
        if (!Vars.state.getState().equals(GameState.State.playing)) return null;
        return Pool.submit(() -> {
            ArrayList<Tile> mains = new ArrayList<>(), mainsBackup = new ArrayList<>();
            ArrayList<Tile> group = new ArrayList<>();
            mains.add(main);
            group.add(main);
            while (!mains.isEmpty()) {
                for (Tile t : mains) {
                    for (int i = 0; i < 4; i++) {
                        Tile h = t.nearby(i);
                        if (h == null) continue;
                        if (group.contains(h)) continue;
                        if (filter.accept(h)){
                            group.add(h);
                            mainsBackup.add(h);
                        }
                    }
                }
                mains.clear();
                mains.addAll(mainsBackup);
                mainsBackup.clear();
            }
            return group;
        });
    }
    
    public static Future<Building> getBuild(Filter<Building> buildFilter) {
        return Pool.submit(() -> Random.getRandom(Objects.requireNonNull(getBuilds(buildFilter)).get()));
    }
    
    @SafeVarargs
    public static Future<ArrayList<Building>> getBuildingBlock(Team team, Class<? extends Block>... list) {
        return getBuildingBlock(team, false, list);
    }
    
    @SafeVarargs
    public static ArrayList<Building> getBuildingBlockSync(Team team, Class<? extends Block>... list) {
        return getBuildingBlockSync(team, true, list);
    }
    
    @SafeVarargs
    public static @NotNull ArrayList<Building> getBuildingBlockSync(Team team, boolean cache, Class<? extends Block>... list) {
        ArrayList<Building> arr = new ArrayList<>();
        try {
            int hash = Arrays.hashCode(list);
            if (cache && buildingCache.containsKey(hash)) return buildingCache.get(hash);
            ArrayList<Tile> t = Interface.getTiles(f -> {
                if (f == null) return false;
                if (!f.interactable(team)) return false;
                if (f.build == null) return false;
                for (Class<? extends Block> l : list) {
                    if (l.isInstance(f.build.block)) return true;
                }
                return false;
            }).get();
            for (Tile te : t)
                arr.add(te.build);
            if (!arr.isEmpty()) buildingCache.put(hash, arr);
            return arr;
        }catch(Throwable ignored){}
        return arr;
    }
    
    @SafeVarargs
    public static Future<ArrayList<Building>> getBuildingBlock(Team team, boolean cache, Class<? extends Block>... list) {
        if (!Vars.state.getState().equals(GameState.State.playing)) return null;
        return Pool.submit(() -> getBuildingBlockSync(team, cache, list));
    }
    
    public static Future<Building> getRandomSorterLikeShit() {
        return Interface.getBuild(build -> {
            if (build == null) return false;
            return build.interactable(Vars.player.team()) && (build.block() instanceof Sorter || build.block() instanceof ItemSource);
        });
    }
    
    public static ArrayList<Building> getBuildsSync(Filter<Building> buildingFilter) {
        ArrayList<Building> list = new ArrayList<>();
        for (Building t : Groups.build) {
            if (!buildingFilter.accept(t)) continue;
            list.add(t);
        }
        return list;
    }
    
    public static Future<ArrayList<Building>> getBuilds(Filter<Building> buildingFilter) {
        if (!Vars.state.getState().equals(GameState.State.playing)) return null;
        return Pool.submit(() -> getBuildsSync(buildingFilter));
    }
    
    public static ArrayList<Tile> getTilesSync(Filter<Tile> filter) {
        ArrayList<Tile> list = new ArrayList<>();
        for (Tile t : Vars.world.tiles) {
            if (!filter.accept(t)) continue;
            list.add(t);
        }
        return list;
    }
    
    public static Locale getLocale() {
        Locale locale;
        String loc = settings.getString("locale");
        if (loc.equals("default")){
            locale = Locale.getDefault();
        }else{
            Locale lastLocale;
            if (loc.contains("_")){
                String[] split = loc.split("_");
                lastLocale = new Locale(split[0], split[1]);
            }else{
                lastLocale = new Locale(loc);
            }
            
            locale = lastLocale;
        }
        return locale;
    }
    
    public static Future<ArrayList<Tile>> getTiles(Filter<Tile> filter) {
        if (!Vars.state.getState().equals(GameState.State.playing)) return null;
        return Pool.submit(() -> getTilesSync(filter));
    }
    
    public static Future<Tile> getTile(Filter<Tile> filter) {
        if (!Vars.state.getState().equals(GameState.State.playing)) return null;
        return Pool.submit(() -> Random.getRandom(Objects.requireNonNull(getTiles(filter)).get()));
    }
    
    public static void copyToClipboard(Object s) {
        try {
            Core.app.setClipboardText(String.valueOf(s));
            toast("Copied");
        }catch(Throwable t){
            toast(t.getMessage());
        }
    }
    
    public static void showError(Throwable t) {
        runOnUI(() -> ui.showException(Reflect.getCallerClass(), t));
    }
    
    public static void oneTime(Runnable r) {
        Core.settings.getBoolOnce(Reflect.getCallerClassStackTrace().toString(), r);
    }
    
    public static void oneTimeInfo(String h) {
        Core.settings.getBoolOnce(Reflect.getCallerClassStackTrace().toString(), () -> {
            showInfo(h);
        });
    }
    
    public static <T> Class<T> getClass(String key) {
        try {
            return (Class<T>) Class.forName(key, false, Interface.class.getClassLoader());
        }catch(ClassNotFoundException e){
            return null;
        }
    }
    
    public static String getConsoleInputOrNull() {
        try {
            return getConsoleInput();
        }catch(IOException e){
            return null;
        }
    }
    
    public static String getConsoleInput(String... allowedWord) {
        HashSet<String> allowed = new HashSet<>(Arrays.asList(allowedWord));
        String that = null;
        try {
            that = getConsoleInput();
        }catch(IOException e){
            that = e.getMessage();
        }
        while (!allowed.contains(that)) {
            try {
                System.out.println("Try again");
                that = getConsoleInput();
            }catch(IOException e){
                that = e.getMessage();
            }
        }
        return that;
    }
    
    public static String getConsoleInput() throws IOException {
        System.out.print(">");
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
    public static void main(String[] args) {
        System.err.println(fancyBox("\tYeet\n" + WordGenerator.newWord(120) + "\nyes" + " no"));
        for (int i = 0; i < 5; i++) {
            System.err.println(fancyBox(WordGenerator.randomWord().toString(), WordGenerator.randomWord().append("\n").append(WordGenerator.newWord(50)).append("\n").append(WordGenerator.newWord(60)).toString(), WordGenerator.randomWord().append(" | ").append(WordGenerator.randomWord()).toString()));
        }
        for (int i = 0; i < 5; i++) {
            System.err.println(fancyBox(WordGenerator.randomWord().toString(), WordGenerator.randomWord().append("\n").append(WordGenerator.newWord(50)).append("\n").append(WordGenerator.newWord(60)).toString(), WordGenerator.randomString(), WordGenerator.randomString()));
            
        }
        
        System.out.println(getConsoleInputOrNull());
        System.out.println(getConsoleInput("yes", "no"));
    }
    
    public static String fancyBox(String title, String text) {
        return fancyBox(title, text, "");
    }
    
    public static String fancyBox(String title, String text, String bottomText) {
        int center = measureStringMax(text, bottomText);
        center = center / 2;
        
        return fancyBox(Utility.repeatThisString(" ", Math.max(0, center - (title.length() / 2))) + title + "\n\n" + text + "\n\n" + bottomText);
    }
    
    //for debugging
    public static String fancyBoxDebug(int i, String text) {
        if (!fancyBoxDebug) return text;
        String stack = Reflect.getCallerClassStackTrace().toString();
        stack = stack + fancyBoxIgnore;
        if (i < 0){
            return text + "\n" + i + stack;
        }
        return text + "\n" + Utility.repeatThisString(" ", i) + "|" + stack;
    }
    
    public static int measureStringMax(String... texts) {
        int center = 0;
        for (String text : texts) {
            for (String s : text.split("\n")) {
                if (s.endsWith(fancyBoxIgnore)) continue;
                center = Math.max(s.length(), center);
            }
        }
        return center;
    }
    
    public static String fancyBox(String title, String text, String accept, String decline) {
        final String fancyBoxAcceptDeclineSeparator = Interface.fancyBoxAcceptDeclineSeparator;
        String bottomText = accept + fancyBoxAcceptDeclineSeparator + decline;//yes
        int center = measureStringMax(text) / 2;//to calculate center of title
        int bottomTextRepeat = Math.max(0, center - (title.length() / 2));//0 to title start index
        
        bottomTextRepeat += title.length() / 2;//add half of the string length
        
        bottomTextRepeat = bottomTextRepeat - accept.length();//compensate for this text
        
        bottomTextRepeat -= Math.ceil((double) fancyBoxAcceptDeclineSeparator.length() / 2);//compensate for this text by half
        
        bottomTextRepeat = Math.max(0, bottomTextRepeat);//extra safety
        bottomText = Utility.repeatThisString(" ", bottomTextRepeat) + bottomText;//self centered
        return fancyBox(title, text, bottomText);
    }
    
    public static String fancyBox(String output) {
        final String fancyBoxBorder = Interface.fancyBoxBorder;
        int repeatLength = measureStringMax(output);//to cover all text
        repeatLength = (int) Math.ceil((double) repeatLength / fancyBoxBorder.length());//if fancyBoxBorder is not a single character its problematic. single char: 20/1 or double char: 20/2 to fill the border
        String border = Utility.repeatThisString(fancyBoxBorder, repeatLength);//make repeating string
        return '\n' + border + '\n' + output + '\n' + border;//fill top and bottom
    }

    public static void showConfirm(String title, String text, String accept, String decline, Runnable confirm, Runnable no) {
        
        if (Vars.headless) {
            if (GlopionCore.test) {
                if (Random.getBool()) {
                    confirm.run();
                } else {
                    no.run();
                }
                return;
            }
            String keyMain = title.hashCode() + "-";
            String keyAccept = keyMain + accept.toLowerCase().replace(" ", "-");
            String keyDecline = keyMain + decline.toLowerCase().replace(" ", "-");
            StringBuilder prompt = new StringBuilder(fancyBox(title, text, keyAccept, keyDecline));
            ArrayList<String> h = new ArrayList<>(Arrays.asList(prompt.toString().split("\n")));
            h.add(h.size(), System.lineSeparator() + "Type the choice above in the console to confirm");
            prompt = new StringBuilder();
            for (String s : h) {
                prompt.append(s).append(System.lineSeparator());
            }
            Cons<String> consumer = s -> {
                if (s.equals(keyAccept)) {
                    confirm.run();
                    Log.info("Confirmed: " + accept);
                    return;
                } else if (s.equals(keyDecline)) {
                    no.run();
                    Log.info("Confirmed: " + decline);
                    return;
                }
                throw new ShouldNotHappenedException("User manage to bypass consoleInput: \"" + s + '"');
            };
            HashMap<String, String> ll = new HashMap<>();
            ll.put(keyAccept, title + ": " + accept);
            ll.put(keyDecline, title + ": " + decline);
            getConsoleInputAsync(prompt.toString(), ll, consumer);

        }else{
            runOnUI(() -> ui.showCustomConfirm(title, text, accept, decline, confirm, no));
        }
        
    }
    
    
    public static void showConfirm(String s, String s1, String accept, String accept1) {
        showConfirm(s, s1, accept, accept1, () -> {}, () -> {});
    }
    
    public static void showConfirm(String title, String text, Runnable accepted) {
        showConfirm(title, text, TextManager.translate("Accept"), TextManager.translate("Decline"), accepted, () -> {});
    }
    
    public static void showInfo(String title, String desc) {
        runOnUI(() -> Vars.ui.showInfoText(title, desc));
        if (Vars.headless){
            Log.info(fancyBox("\t" + title + "\n" + desc));
        }
    }
    
    public static void showConfirmOnce(String title, String text, String accept, String decline) {
        showConfirmOnce(title, text, accept, decline, () -> {});
    }
    
    public static void showConfirmOnce(String title, String text, String accept, String decline, Runnable accepted) {
        showConfirmOnce(title, text, accept, decline, accepted, () -> {});
    }
    
    public static void showConfirmOnce(String title, String text, String accept, String decline, Runnable accepted, Runnable declined) {
        String key = TextManager.toKey(text);
        boolean runned = settings.getBool(key, false);
        
        if (!runned){
            Runnable finalAccepted = accepted;
            accepted = () -> {
                settings.put(key, true);
                finalAccepted.run();
            };
            Runnable finalDeclined = declined;
            declined = () -> {
                settings.put(key, true);
                finalDeclined.run();
            };
            showConfirm(title, text, accept, decline, accepted, declined);
        }
    }
    
    
    public void reset() {
        buildingCache.clear();
    }
}





