package org.o7.Fire.Glopion.Experimental;

import arc.Events;
import mindustry.Vars;
import org.o7.Fire.Glopion.Event.EventExtended;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;

import java.io.PrintStream;

public class LogToDiscordWebhook implements Experimental {
    
    @Override
    public void run() {
        Interface.showInput("Webhook URL",s->{
            try {
                WebhookStandalone standalone = new WebhookStandalone(s);
                standalone.content.username = ""+Vars.player.name;
                PrintStream a = standalone.asPrintStream();
                standalone.post(standalone.content.username);
                Events.on(EventExtended.Log.class, a::println);
            }catch(Throwable e){
                WarningHandler.handleMindustryUserFault(e);
            }
        });
    }
}
