package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Floatc;
import arc.func.Intc;
import arc.graphics.Color;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.ui.Bar;
import mindustry.ui.dialogs.BaseDialog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static mindustry.Vars.ui;
import static org.o7.Fire.Glopion.Bootstrapper.Main.*;
public class Bootstrapper extends Mod {
    private static void download(String furl, Fi dest, Intc length, Floatc progressor, Boolp canceled, Runnable done, Cons<Throwable> error) {
        Threads.daemon(() -> {
            try {
                dest.parent().mkdirs();
                dest.delete();
                HttpURLConnection con = (HttpURLConnection) new URL(furl).openConnection();
                BufferedInputStream in = new BufferedInputStream(con.getInputStream());
                OutputStream out = dest.write(false, 4096);
                
                byte[] data = new byte[4096];
                long size = con.getContentLength();
                long counter = 0;
                length.get((int) size);
                int x;
                while ((x = in.read(data, 0, data.length)) >= 0 && !canceled.get()) {
                    counter += x;
                    progressor.get((float) counter / (float) size);
                    out.write(data, 0, x);
                }
                out.close();
                in.close();
    
                if (!canceled.get()){
                    done.run();
                    Log.infoTag("Downloader", furl + " has been downloaded to " + dest.absolutePath());
                }
            }catch(Throwable e){
                error.get(e);
            }
        });
    }
    
    Properties release = new Properties();
    
    //merge this
    public static void downloadUI(String url) {
        ui.showCustomConfirm(url, Main.jar.absolutePath() + "\n doesn't exist\n Do you want download", "Yes", "No", () -> Bootstrapper.downloadGUI(url), Main::disable);
    }
    
    public static void downloadGUI(String url) {
        
        try {
            boolean[] cancel = {false};
            float[] progress = {0};
            int[] length = {0};
            
            
            BaseDialog dialog = new BaseDialog("Downloading");
            download(url, Main.jar, i -> length[0] = i, v -> progress[0] = v, () -> cancel[0], () -> {
                if (Main.jar.exists()){
                    Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
                }else{
                    ui.showErrorMessage(Main.jar.absolutePath() + " still doesn't exist ??? how");
                }
            }, e -> {
                dialog.hide();
                ui.showException(e);
            });
            
            dialog.cont.add(new Bar(() -> length[0] == 0 ? "Downloading: " + url : (int) (progress[0] * length[0]) / 1024 / 1024 + "/" + length[0] / 1024 / 1024 + " MB", () -> Pal.accent, () -> progress[0])).width(400f).height(70f);
            dialog.buttons.button("@cancel", Icon.cancel, () -> {
                cancel[0] = true;
                dialog.hide();
            }).size(210f, 64f);
                dialog.setFillParent(false);
                dialog.show();
            }catch(Exception e){
                ui.showException(e);
            }
        
        
    }

    @Override
    public void init() {
    
        baseURL = baseURL.endsWith("/") ? baseURL : baseURL + "/";
        Core.net.httpGet(baseURL + "release.properties", suc -> {
            try {
                release.load(suc.getResultAsStream());
                String url;
                if (release.getProperty(flavor) != null) url = release.getProperty(flavor);
                else{
                    Log.warn("@ Flavor doesn't exist @", flavor, release);
                    runOnUI(() -> ui.showInfo(flavor + " Flavor doesn't exist"));
                    return;
                }
                if (Main.downloadThing || Core.settings.getBool("glopion-auto-update", true)){
                    Log.infoTag("Glopion-Bootstrapper", "");
                    Log.infoTag("Glopion-Bootstrapper", "Downloading: " + url);
        
                    boolean b = !Core.settings.getBoolOnce("glopion-prompt-" + flavor) || downloadThing;
                    if (!Vars.headless && b){
                        Main.runOnUI(() -> Bootstrapper.downloadUI(url));
                    }else{
                        boolean[] cancel = {false};
                        float[] progress = {0};
                        int[] length = {0};
                        download(url, Main.jar, i -> length[0] = i, v -> progress[0] = v, () -> cancel[0], () -> {
                            runOnUI(() -> ui.showInfoFade(url + " has been downloaded"));
                        }, Main::handleException);
                    }
                }
            }catch(IOException e){
                handleException(e);
            }
        }, Log::err);
    
    
        Cell<Table> t = ui.settings.game.row().table().growX();
        Main.runOnUI(() -> buildUI(t.get()));
        Events.on(EventType.ResizeEvent.class, s -> buildUI(t.get()));
    }
    
    public void buildUI(Table t) {
        t.reset();
        t.add("Glopion Bootstrapper Settings").growX().center().row();
        t.check("Force Update", Core.settings.getBool("glopion-auto-update", true), b -> Core.settings.put("glopion-auto-update", b)).row();
        t.button("Glopion Flavor [accent]" + Core.settings.getString("glopion-flavor", flavor), () -> {
            new BaseDialog("Glopion Flavor") {
                {
                    addCloseButton();
                    build();
                }
            
                @Override
                public void addCloseListener() {
                    super.addCloseListener();
                    buildUI(t);
                }
            
                void build() {
                    cont.clear();
                    Table table = new Table(t -> {
                        TreeMap<String, String> map = new TreeMap<>();
                        for (Map.Entry<Object, Object> o : release.entrySet()) {
                            if ((o.getKey() + "").startsWith("Note"))
                                t.add(o.getKey() + ": " + o.getValue()).growX().row();
                            else map.put(o.getKey() + "", o.getValue() + "");
                        }
                        for (Map.Entry<String, String> o : map.entrySet()) {
                            Cell<TextButton> c = t.button(o.getKey() + ": " + o.getValue(), () -> {
                                Core.settings.put("glopion-flavor", o.getKey() + "");
                                build();
                            }).growX().disabled(o.getKey().startsWith("Desktop") && Vars.mobile);
                            if (Core.settings.get("glopion-flavor", flavor).equals(o.getKey() + ""))
                                c.color(Color.green);
                            else if (o.getKey().contains(Version.buildString())) c.color(Color.blue);
                            else c.color(Color.crimson);
                            c.row();
                        }
                    });
                    ScrollPane scrollPane = new ScrollPane(table);
                    cont.add(scrollPane).growX().growY();
                }
            }.show();
        }).disabled(s -> release.isEmpty()).growX().row();
        t.button("Provider URL", () -> {
            ui.showTextInput("Provider URL", "URL", Core.settings.getString("glopion-url", baseURL), s -> {
                s = s.endsWith("/") ? s : s + "/";
                ui.loadfrag.show("Checking");
                String finalS = s;
                Core.net.httpGet(s + "release.properties", suc -> {
                    ui.loadfrag.hide();
                    Properties sike = new Properties();
                    try {
                        sike.load(suc.getResultAsStream());
                    }catch(IOException e){
                        ui.showException(e);
                        e.printStackTrace();
                        return;
                    }
                    if (sike.size() < 2){
                        ui.showErrorMessage("Empty ??\n" + suc.getResultAsString());
                        return;
                    }
                    ui.showInfoFade("Loaded: " + sike.size() + " flavor");
                    release = sike;
                    Core.settings.put("glopion-url", finalS);
                    buildUI(t);
                }, e -> {
                    ui.showException(e);
                    ui.loadfrag.hide();
                });
            });
        }).growX().row();
        t.button("Refresh", () -> buildUI(t)).growX().row();
        t.button("Purge Local Glopion", Main.jar::delete).growX().row();
        t.button("Reset Bootstrapper Configuration", () -> {
            Core.settings.remove("glopion-auto-update");
            Core.settings.remove("glopion-url");
            Core.settings.remove("glopion-flavor");
            ui.showInfoFade("Bootstrapper Configuration Reseted");
            buildUI(t);
        }).growX().row();
    
    
    }
}
