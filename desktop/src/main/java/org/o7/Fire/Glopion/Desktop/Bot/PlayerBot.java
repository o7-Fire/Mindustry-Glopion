package org.o7.Fire.Glopion.Desktop.Bot;

import mindustry.gen.Player;
import org.o7.Fire.Glopion.Desktop.BotControl;

public class PlayerBot extends Player {
    public final BotControl.Mode mode;
    public final BotControl.Type type;
    public final BotControl botControl;
    
    public PlayerBot(BotControl botControl) {
        mode = botControl.currentMode;
        type = botControl.currentType;
        this.botControl = botControl;
    }
    
    
}
