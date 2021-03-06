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

package org.o7.Fire.Glopion.Patch.Mindustry;

import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.input.DesktopInput;
import mindustry.ui.Styles;
import org.o7.Fire.Glopion.GlopionCore;

public class DesktopInputPatched extends DesktopInput  {
    
    
    @Override
    public void buildPlacementUI(Table table) {
        super.buildPlacementUI(table);
        table.button(Icon.settings, Styles.colori, () -> GlopionCore.glopionHud.show()).tooltip("Glopion");
    }
    
    
}
