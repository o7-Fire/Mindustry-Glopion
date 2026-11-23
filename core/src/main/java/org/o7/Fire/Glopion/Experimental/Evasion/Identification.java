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

package org.o7.Fire.Glopion.Experimental.Evasion;

import Atom.String.WordGenerator;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import arc.Core;
import arc.graphics.Color;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.serialization.Base64Coder;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Packets;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.TextManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.maxNameLength;

@SuppressWarnings("Unchecked")
public class Identification {
    private static final long ROTATION_INTERVAL_MS = 20L * 60 * 1000;
    private static final String ROTATION_TIME_KEY = "glopion-uuid-rotation";
    private static final int UID_BYTES = 8;
    private static final int UID_LENGTH = Base64Coder.encode(new byte[UID_BYTES]).length;

    public static HashMap<String, Object> getValue() throws NoSuchFieldException, IllegalAccessException {
        Field f = Core.settings.getClass().getDeclaredField("values");
        f.setAccessible(true);
        return (HashMap<String, Object>) f.get(Core.settings);
    }
    
    public static Packets.ConnectPacket randomConnectPacket() {
        Packets.ConnectPacket c = new Packets.ConnectPacket();
        Player p = null;
        if (Groups.player.size() > 2) p = Random.getRandom(Groups.player);
        c.name = p == null ? (Random.getBool() ? Utility.capitalizeEnforce(WordGenerator.newWord(Random.getInt(5, maxNameLength)).toString()) : WordGenerator.newWord(Random.getInt(5, maxNameLength)).toString()) : p.name + TextManager.getRandomHexColor();
        c.locale = null;//lol no
        c.mods = new Seq<>();
        c.mobile = Random.getBool();
        c.versionType = Version.type;
        c.color = p == null ? Color.valueOf(Random.getRandomHexColor()).rgba() : p.color.rgba();
        c.usid = Identification.getRandomUID();
        c.uuid = Identification.getRandomUID();
        return c;
    }
    
    public static String getUUID(String player) {
        String key = "uuid" + player;
        String s = Core.settings.getString(key, null);
        if (s == null || s.length() != UID_LENGTH) {
            s = getRandomUID();
            Core.settings.put(key, s);
        }
        return s;
    }
    
    public static String getUUID() {
        String s = Core.settings.getString("uuid", null);
        long now = Time.millis();
        long last = Core.settings.getLong(ROTATION_TIME_KEY, 0L);
        if (s == null || s.length() != UID_LENGTH || now - last >= ROTATION_INTERVAL_MS) {
            s = getRandomUID();
            Core.settings.put("uuid", s);
            Core.settings.put(ROTATION_TIME_KEY, now);
        }
        return s;
    }
    
    public static void setUUID(String s) {
        if (s == null || s.length() != UID_LENGTH) return;
        Core.settings.put("uuid", s);
        Core.settings.put(ROTATION_TIME_KEY, Time.millis());
    }
    
    public static ArrayList<String> getKeys() throws NoSuchFieldException, IllegalAccessException {
        HashMap<String, Object> values = getValue();
        ArrayList<String> yikes = new ArrayList<>();
        for (String s : values.keySet()) {
            if (s.startsWith("usid-") || s.startsWith("uuid")) yikes.add(s);
        }
        return yikes;
    }
    
    public static void changeID(Runnable onfinished) {
        Pool.daemon(() -> {
            try {
                changeID();
            }catch(Throwable t){
                WarningHandler.handleMindustry(t);
                Core.app.post(() -> {
                    if (Vars.ui != null) Vars.ui.showException(t);
                });
            }
            Core.app.post(() -> {
                if (onfinished != null) onfinished.run();
            });
        }).start();
    }
    
    public static void changeID() throws NoSuchFieldException, IllegalAccessException {
        ArrayList<String> yikes = getKeys();
        AtomicInteger i = new AtomicInteger();
        Core.app.post(() -> {
            if (Vars.ui == null || Vars.ui.loadfrag == null) return;
            Vars.ui.loadfrag.setProgress(() -> (float) i.get() / yikes.size());
        });
        for (String s : yikes) {
            Core.app.post(() -> {
                if (Vars.ui == null || Vars.ui.loadfrag == null) return;
                Vars.ui.loadfrag.setText(i.get() + "/" + yikes.size());
            });
            Core.settings.put(s, getRandomUID());
            i.getAndIncrement();
        }
        Core.settings.put(ROTATION_TIME_KEY, Time.millis());
        
    }
    
    public static String getRandomUID() {
        byte[] bytes = new byte[UID_BYTES];
        (new Rand()).nextBytes(bytes);
        return new String(Base64Coder.encode(bytes));
    }
    
    public static String getUsid(String ip, int port) {
        return getUsid(ip + ":" + port);
    }
    
    public static String getUsid(String ip) {
        if (ip.contains("/")){
            ip = ip.substring(ip.indexOf("/") + 1);
        }
        
        if (Core.settings.getString("usid-" + ip, null) != null){
            return Core.settings.getString("usid-" + ip, null);
        }else{
            String result = getRandomUID();
            Core.settings.put("usid-" + ip, result);
            return result;
        }
    }
}
