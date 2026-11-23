package org.o7.Fire.Glopion.Patch;

import arc.files.Fi;
import arc.struct.Seq;
import mindustry.core.Platform;
import mindustry.mod.Scripts;
import mindustry.net.Net;
import mindustry.type.Publishable;
import mindustry.ui.FileChooser;
import org.o7.Fire.Glopion.Experimental.Evasion.Identification;
import rhino.Context;

public class UUIDPlatform implements Platform {
    private final Platform delegate;

    public UUIDPlatform(Platform delegate) {
        this.delegate = delegate;
    }

    @Override
    public ClassLoader loadJar(Fi jar, ClassLoader parent) throws Exception {
        return delegate.loadJar(jar, parent);
    }

    @Override
    public void updateLobby() {
        delegate.updateLobby();
    }

    @Override
    public void inviteFriends() {
        delegate.inviteFriends();
    }

    @Override
    public void publish(Publishable pub) {
        delegate.publish(pub);
    }

    @Override
    public void viewListing(Publishable pub) {
        delegate.viewListing(pub);
    }

    @Override
    public void viewListingID(String mapid) {
        delegate.viewListingID(mapid);
    }

    @Override
    public Seq<Fi> getWorkshopContent(Class<? extends Publishable> type) {
        return delegate.getWorkshopContent(type);
    }

    @Override
    public void openWorkshop() {
        delegate.openWorkshop();
    }

    @Override
    public Net.NetProvider getNet() {
        return delegate.getNet();
    }

    @Override
    public Scripts createScripts() {
        return delegate.createScripts();
    }

    @Override
    public Context getScriptContext() {
        return delegate.getScriptContext();
    }

    @Override
    public void updateRPC() {
        delegate.updateRPC();
    }

    @Override
    public String getUUID() {
        return Identification.getUUID();
    }

    @Override
    public void shareFile(Fi file) {
        delegate.shareFile(file);
    }

    @Override
    public void showFileChooser(FileChooser.FileChooserParams params) {
        delegate.showFileChooser(params);
    }

    @Override
    public void hide() {
        delegate.hide();
    }

    @Override
    public void beginForceLandscape() {
        delegate.beginForceLandscape();
    }

    @Override
    public void endForceLandscape() {
        delegate.endForceLandscape();
    }
}
