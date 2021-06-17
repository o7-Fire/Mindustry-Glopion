package org.o7.Fire.Glopion.Experimental;

import arc.Events;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.BlockUnitUnit;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;

import java.io.IOException;

public class RelayChatToWebhook implements Experimental {
    
    @Override
    public void run() {
        Interface.showInfo("Warning! Anything you type into chat (commands,etc) will get logged too");
        Interface.showInput("Webhook URL", s->{
            try {
                WebhookStandalone standalone = new WebhookStandalone(s);
                standalone.post("Test");
                Events.on(EventType.PlayerChatEvent.class,chat->{
                    standalone.content.username = chat.player.name;
                    if (Vars.player.unit() instanceof BlockUnitUnit){
                        standalone.content.avatar_url = "https://github.com/Anuken/Mindustry/raw/master/core/assets-raw/blocks/turrets/" +  Vars.player.unit().blockOn().name + ".png";
                    }else{
                        standalone.content.avatar_url = "https://github.com/Anuken/Mindustry/raw/master/core/assets-raw/sprites/units/" + Vars.player.unit().type().name + ".png";
                    }
                    try {
                        standalone.post(Strings.stripGlyphs(Strings.stripColors(chat.message)));
                    }catch(IOException e){
                        WarningHandler.handleMindustry(e);
                    }
                });
            }catch(Throwable e){
                WarningHandler.handleMindustryUserFault(e);
            }
        });
    }
}
