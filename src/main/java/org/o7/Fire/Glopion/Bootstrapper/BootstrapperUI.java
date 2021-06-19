package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.Net;
import arc.files.Fi;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Floatc;
import arc.func.Intc;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Strings;
import arc.util.async.Threads;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.ui.Bar;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Bootstrapper.UI.FlavorDialog;
import org.o7.Fire.Glopion.Bootstrapper.UI.ProviderURLDialog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import static mindustry.Vars.ui;
import static org.o7.Fire.Glopion.Bootstrapper.Main.*;

public class BootstrapperUI extends Mod {
    public FlavorDialog flavorDialog;
    public ProviderURLDialog providerURLDialog;
    public Properties release = new Properties();
    public Cell<Table> tableCell = null;
    public Table t = null;
    
    public static void download(String url, Fi dest, Runnable done, Cons<Throwable> err) {
        boolean[] cancel = {false};
        float[] progress = {0};
        int[] length = {0};
        download(url, dest, i -> length[0] = i, v -> progress[0] = v, () -> cancel[0], done, err);
    }
    
    public static void download(String furl, Fi dest, Intc length, Floatc progressor, Boolp canceled, Runnable done, Cons<Throwable> error) {
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
    
    public static void downloadConfirm(String url, Fi jar, Runnable done) {
        ui.showCustomConfirm(url, jar.absolutePath() + "\n doesn't exist\n Do you want download", "Yes", "No", () -> BootstrapperUI.downloadGUI(url, jar, done), Main::disable);
    }
    
    public static void downloadGUI(String url, Fi jar, Runnable done) {
        
        try {
            boolean[] cancel = {false};
            float[] progress = {0};
            int[] length = {0};
            
            
            BaseDialog dialog = new BaseDialog("Downloading");
            download(url, jar, i -> length[0] = i, v -> progress[0] = v, () -> cancel[0], () -> {
                done.run();
                dialog.hide();
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
    
    public void fetchRelease(Cons<Net.HttpResponse> succ) {
        baseURL = baseURL.endsWith("/") ? baseURL : baseURL + "/";
        Core.net.httpGet(baseURL + "release.properties", suc -> {
            try {
                release.clear();
                release.load(suc.getResultAsStream());
                if (release.getProperty(flavor) == null){
                    Log.warn("@ Flavor doesn't exist @", flavor, release);
                    runOnUI(() -> ui.showInfo(flavor + " Flavor doesn't exist"));
                    return;
                }else{
                    runOnUI(() -> ui.showInfoFade("Fetched: [green]" + release.size() + " [white]Flavor"));
                }
                succ.get(suc);
            }catch(IOException e){
                handleException(e);
            }
        }, Log::err);
    }
    
    public void tryDownload() {
        String url = release.getProperty(flavor);
        if (url == null) return;
        String path = flavor.replace('-', '/') + ".jar";
        jar = Core.files.cache(path);
        if (!jar.exists() || Core.settings.getBool("glopion-auto-update", false)){
            Log.infoTag("Glopion-Bootstrapper", "");
            Log.infoTag("Glopion-Bootstrapper", "Downloading: " + url);
            boolean b = !Core.settings.getBoolOnce("glopion-prompt-" + flavor) || !jar.exists();
            if (!Vars.headless && b){
                //sometime jar already exist
                Main.runOnUI(() -> BootstrapperUI.downloadConfirm(url, jar, () -> {
                    if (Main.jar.exists()){
                        Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
                    }else{
                        ui.showErrorMessage(jar.absolutePath() + " still doesn't exist ??? how");
                    }
                    
                }));
            }else{
                long size = jar.length();
                
                download(url, Main.jar, () -> {
                    if (downloadThing || size != jar.length())
                        runOnUI(() -> ui.showInfoFade(url + " has been downloaded"));
                }, Main::handleException);
            }
        }
    }

    public void downloadIfNotExist() {
        String url = release.getProperty(flavor);
        if (url == null) return;
        jar = Core.files.cache(flavor.replace('-', '/') + ".jar");
        if (!jar.exists()) tryDownload();
    }
    
    @Override
    public void init() {
        providerURLDialog = new ProviderURLDialog(this);
        flavorDialog = new FlavorDialog(this);
        fetchRelease(suc -> tryDownload());
        tableCell = ui.settings.game.row().table().growX();
        t = tableCell.get();
        Main.runOnUI(this::buildUI);
        Events.on(EventType.ResizeEvent.class, s -> buildUI());
    }
    
    public void buildUI() {
        t.reset();
        t.add("Glopion Bootstrapper Settings").growX().center().row();
        t.check("Force Update", Core.settings.getBool("glopion-auto-update", false), b -> Core.settings.put("glopion-auto-update", b)).row();
        t.button("Glopion Flavor [accent]" + Core.settings.getString("glopion-flavor", flavor), flavorDialog::show).disabled(s -> release.isEmpty()).growX().row();
        t.button("Provider URL", providerURLDialog::show).growX().row();
        t.button("Refresh", () -> {
            buildUI();
            fetchRelease(suc -> {
                buildUI();
                downloadIfNotExist();
            });
        }).growX().row();
        t.button("Error: " + error.size(), () -> {
            new BaseDialog("Error") {
                {
                    addCloseButton();
                    cont.clear();
                    if (error.isEmpty()){
                        cont.add("None").growX().growY();
                    }else{
                        for (Throwable t : error) {
                            cont.add(Strings.neatError(t, false)).growX().growY().row();
                        }
                    }
                }
            }.show();
        }).growX().row();
        t.button("Purge Local Glopion", () -> {
            jar.delete();
            downloadIfNotExist();
        }).growX().row();
        t.button("Reset Bootstrapper Configuration", () -> {
            Core.settings.remove("glopion-auto-update");
            Core.settings.remove("glopion-url");
            Core.settings.remove("glopion-flavor");
            ui.showInfoFade("Bootstrapper Configuration Reseted");
            buildUI();
        }).growX().row();
        if (loaded != null){
            t.button("Loaded: [accent]" + loaded.getClass().getSimpleName(), () -> new BaseDialog("Loaded Glopion") {
                {
                    addCloseButton();
                    cont.add(info + "\nURL: " + release.getProperty(flavor)).growX().growY();
                }
            }.show()).growX().row();
        }
        
    }
}
