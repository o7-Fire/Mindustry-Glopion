package org.o7.Fire.Glopion.Patch;

import Atom.Translation.GoogleTranslate;
import Atom.Translation.Translator;
import Atom.Utility.Pool;
import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.core.NetClient;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Patch.Mindustry.ChatFragmentPatched;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static mindustry.Vars.netServer;

public class TranslateChat extends ModsModule implements Administration.ChatFilter {
    public static final HashMap<String, Locale> locales = new HashMap<>();
    public static Translator translator = null;
    public static Locale auto = new Locale("auto");
    
    public static String getMeta() {
        return "[" + TranslateChat.translator.getClass().getSimpleName() + "][" + TranslateChat.getTarget().getLanguage() + "]";
    }
    
    public static void startProvider() {
        if (translator == null){
            translator = new GoogleTranslate();
        }
    }
    
    public static void translate(String s, Locale to, Consumer<String> onFinished) {
        if (s == null) throw new NullPointerException("No text to translate");
        startProvider();
        s = Strings.stripColors(Strings.stripGlyphs(s)).trim();
        String finalS = s;
        Pool.submit(() -> {
            try {
                String translated = translator.translate(auto, to, finalS).get();
                if (translated == null && !GlopionCore.test){
                    return;
                }
                if (translated.equals(finalS)) return;
                onFinished.accept(translated);
            }catch(InterruptedException e){
            
            }catch(ExecutionException e){
                WarningHandler.handleMindustry(e);
            }
            
        });
    }
    
    public static Locale getTarget() {
        String code = Core.settings.getString("TranslateChatTarget", Interface.getLocale().getLanguage());
        if (!locales.containsKey(code)) locales.put(code, new Locale(code));//creating object every tick isn't good
        return locales.get(code);
    }
    
    @Override
    public boolean disabled() {
        return !(GlopionCore.translateChatSettings || GlopionCore.interceptChatThenTranslateSettings || GlopionCore.rebroadcastTranslatedMessageSettings || GlopionCore.test);
    }
    
    @Override
    public void test() {
        super.test();
        testCompleted = false;
        translate("[red]Colorize [white]the word ", new Locale("la"), s -> {
            assert s != null;
            assert s.equals("Colorize verbum");
            testCompleted = true;
        });
    }
    
    @Override
    public void start() {
        super.start();
        Log.infoTag("Translation", "Target Lang:" + getTarget().getLanguage());
        startProvider();
        Log.infoTag("Translation", "Provider: " + translator.getClass().getCanonicalName());
        if (GlopionCore.interceptChatThenTranslateSettings){
            Log.infoTag("Translation", "Chat Filter Hook");
            netServer.admins.addChatFilter(this);
        }
        if (GlopionCore.rebroadcastTranslatedMessageSettings){
            Log.infoTag("Translation", "PlayerChatEvent Hook");
            Events.on(EventType.PlayerChatEvent.class, playerChatEvent -> {
                if (playerChatEvent != null && playerChatEvent.message != null){
                    if (playerChatEvent.message.startsWith(netServer.clientCommands.getPrefix())) return;
                    translate(playerChatEvent.message, getTarget(), message -> {
                        message = netServer.admins.filterMessage(playerChatEvent.player, message);
                        if (message == null) return;
                        message = getMeta() + " " + message;
                        Log.info("&fi@: @", "&lc" + playerChatEvent.player.name, "&lw" + message);
                        //Resend to everyone
                        //Call.sendMessage(message, NetClient.(playerChatEvent.player.id(), playerChatEvent.player.name), playerChatEvent.player);
                    });
                }
            });
        }
        if (Vars.ui != null){
            try {
                Log.infoTag("Translation", "ChatFragment Hook");
                Vars.ui.chatfrag = new ChatFragmentPatched(Vars.ui.chatfrag);
            }catch(LinkageError l){
                WarningHandler.handleMindustry(l);
                Log.errTag("Translation", "Failed to hook into chat fragment");
            }
        }
    }

    @Override
    public String filter(Player player, final String message) {
        startProvider();
        String id = "000000";
        if (player != null) id = player.hashCode() + "";
        if (message.startsWith(id)){
            return message.substring(id.length());
        }
        String finalId = id;
        Pool.submit(() -> {
            String translated = null;
            try {
                translated = translator.translate(auto, getTarget(), message).get();
            }catch(InterruptedException e){
            
            }catch(ExecutionException e){
                WarningHandler.handleMindustry(e);
            }
            if (translated == null) translated = message;
            translated = finalId + translated;
            String finalTranslated = translated;
            Core.app.post(() -> NetClient.sendChatMessage(player, finalTranslated));
        });
        return null;
    }
}
