package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Floatc;
import arc.func.Intc;
import arc.scene.ui.SettingsDialog;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.ui.Bar;
import mindustry.ui.dialogs.BaseDialog;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static mindustry.Vars.ui;

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
    
    public static void downloadUI() {
        ui.showCustomConfirm(Main.url, Main.jar.absolutePath() + "\n doesn't exist\n Do you want download", "Yes", "No", Bootstrapper::downloadGUI, Main::disable);
    }
    
    public static void downloadGUI() {
        
        try {
            boolean[] cancel = {false};
            float[] progress = {0};
            int[] length = {0};
            
            
            BaseDialog dialog = new BaseDialog("Downloading");
            download(Main.url, Main.jar, i -> length[0] = i, v -> progress[0] = v, () -> cancel[0], () -> {
                if (Main.jar.exists()){
                        Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
                    }else{
                        ui.showErrorMessage(Main.jar.absolutePath() + " still doesn't exist ??? how");
                    }
                }, e -> {
                    dialog.hide();
                    ui.showException(e);
                });
                
                dialog.cont.add(new Bar(() -> length[0] == 0 ? "Downloading: " + Main.url : (int) (progress[0] * length[0]) / 1024 / 1024 + "/" + length[0] / 1024 / 1024 + " MB", () -> Pal.accent, () -> progress[0])).width(400f).height(70f);
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
    
        if (Main.downloadThing || Core.settings.getBool("glopion-auto-update", false)){
            Log.infoTag("Glopion-Bootstrapper", "");
            Log.infoTag("Glopion-Bootstrapper", "Downloading: " + Main.url);
            boolean b = !Core.settings.getBoolOnce("glopion-prompt-" + Main.url);
            if (!Vars.headless && b){
                Main.runOnUI(Bootstrapper::downloadUI);
            }else{
                boolean[] cancel = {false};
                float[] progress = {0};
                int[] length = {0};
                download(Main.url, Main.jar, i -> length[0] = i, v -> progress[0] = v, () -> cancel[0], () -> {}, Main::handleException);
            }
    
        }
        Cell<Table> t = ui.settings.game.row().table().growX();
        Main.runOnUI(() -> buildUI(t.get()));
        Events.on(EventType.ResizeEvent.class, s -> buildUI(t.get()));
    }
    
    public void buildUI(Table t) {
        t.reset();
        SettingsDialog.SettingsTable st = new SettingsDialog.SettingsTable();
        if (!Vars.mobile) st.checkPref("glopion-deep-patch", "Deep Patch", false);
        st.row();
        st.checkPref("glopion-auto-update", "Force Update", true);
        st.row();
    
        t.add("Glopion Bootstrapper Settings").growX().center().row();
        t.add("Glopion Flavor:").growX().row();
        t.field(Main.flavor, s -> Core.settings.put("glopion-flavor", s)).growX().row();
        t.add("Provider URL:").growX().row();
        t.field(Main.baseURL, s -> Core.settings.put("glopion-url", s)).growX().row();
        t.button("Purge Local Glopion", Main.jar::delete).growX().row();
        t.button("Reset Bootstrapper Configuration", () -> {
            Core.settings.remove("glopion-auto-update");
            Core.settings.remove("glopion-url");
            Core.settings.remove("glopion-deep-patch");
            Core.settings.remove("glopion-flavor");
            ui.showInfoFade("Bootstrapper Configuration Reseted");
            buildUI(t);
        }).growX().row();
        t.add(st).growX().growY();
        
    }
}
