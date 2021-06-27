package org.o7.Fire.Glopion.Bootstrapper.UI;

import arc.Core;
import arc.files.Fi;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.async.Threads;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Bootstrapper.BootstrapperUI;
import org.o7.Fire.Glopion.Bootstrapper.Main;
import org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.o7.Fire.Glopion.Bootstrapper.Main.downloadLibrary;

public class DependencyManager extends BaseDialog {
    public DependencyManager() {
        super("Dependency Manager");
        addCloseButton();
        buttons.button("Download All", Icon.left, Main::downloadLibrary).size(210f, 64f);
        buttons.button("Why",Icon.left, ()->{
            Vars.ui.showInfo("Unless you want download 500mb of jar everytime there is small change");
        }).size(210f, 64f);
        shown(this::build);
        onResize(this::build);
    }
    void postBuild(){
        Core.app.post(this::build);
    }
    void build(){
        cont.clear();
        Table table = new Table();
        for(Map.Entry<String, File> s : SharedBootstrapper.downloadFile.entrySet()){
            table.table(t->{
                List<URL> urls = SharedBootstrapper.downloadList.get(s.getKey());
                Seq<URL> seqUrl = Seq.with(urls);
                t.button(s.getKey(),()->{
                    StringBuilder sb = new StringBuilder();
                    sb.append("Name: ").append(s.getKey()).append("\n");
                    sb.append("Size: ").append(SharedBootstrapper.sizeList.get(s.getKey())).append("\n");
                    sb.append("File: ").append(s.getValue().getAbsolutePath()).append("\n");
                    sb.append("URL: ").append("\n");
                    for (URL u : seqUrl)
                        sb.append(" ").append(u).append("\n");
                    new InfoDialog(s.getKey(),sb.toString()).show();
                }).growX();
                if(s.getValue().exists()){
                    t.button(s.getValue().getAbsolutePath(), Icon.cancel, () -> {
                        Vars.ui.showConfirm("Delete", "Are you sure want to delete: " + s.getValue().getAbsolutePath() , () -> {
                            s.getValue().delete();
                            postBuild();
                        });
                    }).tooltip("Delete").disabled(!s.getValue().exists()).growX();
                }else{
                    t.button(SharedBootstrapper.sizeList.get(s.getKey()), Icon.download, () -> {
                        BootstrapperUI.downloadGUI(seqUrl.random().toExternalForm(), new Fi(s.getValue()), () -> {
                            postBuild();
                        });
                    }).tooltip("Download").disabled(s.getValue().exists()).growX();
                }
            
            }).growX().row();
            
        }
        ScrollPane scrollPane = new ScrollPane(table);
        cont.add(scrollPane).growX().growY();
    }
}
