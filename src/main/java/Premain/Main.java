package Premain;

import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.Mod;

public class Main extends Mod {
	boolean stat;
	@Override
	public void init() {
		//do you like lambda ?
		Core.settings.getBoolOnce("Test", ()->{
			Events.on(EventType.ClientLoadEvent.class, s ->{
				new Dialog("Test"){{
					if (Vars.mobile) buttons.button("Close", this::hide).growX();//mobile friendly on portrait
					else buttons.button("Close", this::hide).size(210f, 64f);//what with this magic number
					//garbage here
					cont.button("Yeet", Icon.image, ()->{
						Vars.ui.showInfo("Yeet");
					}).growX();//stretch
				}}.show();
			});
		});//sike how to color bracket
		Vars.ui.settings.main.table(t ->{
			t.check("Boolean [red][Test]", s->{
				stat = s;//assign to global ?
				Vars.ui.showInfo(stat+"");//toString()
			});
		}).growX();//stretch
	}
}
