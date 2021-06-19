package org.o7.Fire.Glopion.Bootstrapper.UI;

import arc.Core;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Bootstrapper.BootstrapperUI;

import java.io.IOException;
import java.util.Properties;

import static mindustry.Vars.ui;
import static org.o7.Fire.Glopion.Bootstrapper.Main.baseURL;

public class ProviderURLDialog extends BaseDialog {
    BootstrapperUI bootstrapper;
    public ProviderURLDialog(BootstrapperUI main) {
        super("Provider URL");
        bootstrapper = main;
    }
    String s = Core.settings.getString("glopion-url", baseURL);
    
    {
        addCloseButton();
        cont.field(s, ss -> s = ss).growX().row();
        cont.button("Ok", this::confirm).growX();
    }
    
    void confirm() {
        s = s.endsWith("/") ? s : s + "/";
        ui.loadfrag.show("Checking");
        String finalS = s;
        Core.net.httpGet(s + "release.properties", suc -> {
            ui.loadfrag.hide();
            Properties temp = new Properties();
            try {
                temp.load(suc.getResultAsStream());
            }catch(IOException e){
                ui.showException(e);
                e.printStackTrace();
                return;
            }
            if (temp.size() < 2){
                ui.showErrorMessage("Empty/None/404 ??\n" + temp + " \n" + suc.getResultAsString());
                return;
            }
            ui.showInfoFade("Loaded: " + temp.size() + " flavor");
            bootstrapper.release = temp;
            Core.settings.put("glopion-url", finalS);
            bootstrapper.buildUI();
        }, e -> {
            ui.showException(e);
            ui.loadfrag.hide();
        });
    }
}
