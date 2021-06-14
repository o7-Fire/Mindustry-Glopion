/*******************************************************************************
 * Copyright 2021 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package org.o7.Fire.Glopion.Experimental;

import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;

public class UnlockAllContent implements Experimental {
    @Override
    public void run() {
        boolean ui = Vars.ui != null && Vars.ui.loadfrag != null;
        int i = 1;
        int max = 1;
        for (Seq<Content> ce : Vars.content.getContentMap()) {
            for (Content cc : ce) {
                if (cc instanceof UnlockableContent) max++;
            }
        }
        int finalMax = max;
        if (ui){
            Vars.ui.loadfrag.show("Unlocking");
        
            int finalI1 = i;
            Vars.ui.loadfrag.setProgress((() -> (float) finalMax / finalI1));
        }
        for (Seq<Content> ce : Vars.content.getContentMap()) {
            for (Content cc : ce) {
                if (cc instanceof UnlockableContent){
                    UnlockableContent content = (UnlockableContent) cc;
                    content.alwaysUnlocked = true;
                    content.unlock();
                    Events.fire(new EventType.ResearchEvent(content));
                    i++;
                    int finalI = i;
                    if (ui) Vars.ui.loadfrag.setProgress((() -> (float) finalMax / finalI));
                }
            }
        }
        Log.info("Unlocked @ content", i);
        if (ui) Vars.ui.loadfrag.hide();
    }
}
