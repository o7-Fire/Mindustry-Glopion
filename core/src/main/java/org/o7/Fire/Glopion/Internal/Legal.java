package org.o7.Fire.Glopion.Internal;

import arc.Core;
import org.o7.Fire.Glopion.Internal.Annotation.SinceWhen;
import org.o7.Fire.Glopion.Module.ModsModule;

@SinceWhen(since = 6.2f)
public class Legal extends ModsModule {
    @Override
    public void postInit() throws Throwable {
        Core.settings.getBoolOnce("GlopionDisclaimer", () -> {
            Interface.showConfirm("[royal]Glopion[white]-[red]Warning", "Use this mods at your own risk", "Accept", "Accept");
        });
        Core.settings.getBoolOnce("CrashReport1", () -> Interface.showConfirm("Anonymous Data Reporter", "We collect your anonymous data e.g crash-log, to make your experience much worse", () -> {
        }));
        if (System.getProperty("gay-shit-no-offense") != null){
            Core.settings.getBoolOnce("gay-shit-no-offense", () -> {
                Interface.showInfo("[royal]Glopion[white]-[red]Warning", "something wrong, i can feel it");
            });
        }
    }
}
