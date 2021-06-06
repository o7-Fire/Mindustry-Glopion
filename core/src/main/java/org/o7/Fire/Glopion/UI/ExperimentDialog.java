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

package org.o7.Fire.Glopion.UI;

import mindustry.Vars;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Experimental.Experimental;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;

public class ExperimentDialog extends ScrollableDialog {
    {
        icon = Icon.production;
    }
    
    @Override
    protected void setup() {
        try {
            for (Class<? extends Experimental> c : InformationCenter.getExtendedClass(Experimental.class)) {
                table.button(c.getSimpleName(), () -> {
                    try {
                        c.getDeclaredConstructor().newInstance().run();
                    }catch(Throwable t){
                        Vars.ui.showException(t);
                        WarningHandler.handleMindustry(t);
                    }
                }).growX().row();
                
            }
        }catch(Throwable t){
            ad(t.toString());
        }
    }
}
