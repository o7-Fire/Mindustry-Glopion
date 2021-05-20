package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.files.Fi;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Floatc;
import arc.func.Intc;
import arc.scene.ui.SettingsDialog;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
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
        ui.showCustomConfirm(Main.url, Main.jar.absolutePath() + "\n doesn't exist\n Do you want download", "Yes", "No", Bootstrapper::downloadGUI, () -> Vars.mods.setEnabled(Vars.mods.getMod(Main.class), false));
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
        Main.runOnUI(() -> {
            SettingsDialog.SettingsTable st = new SettingsDialog.SettingsTable();
            st.button("Purge Local Glopion", Main.jar::delete);
            if (!Vars.mobile) st.checkPref("glopion-deep-patch", "Deep Patch", false);
            st.checkPref("glopion-auto-update", "Force Update", true);
            ui.settings.game.row().table(t -> {
                t.add("Glopion Bootstrapper Settings").growX().center().row();
                t.add(st).growX().growY();
            }).growX();
        });
    }
}
