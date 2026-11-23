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

package org.o7.Fire.Glopion.Module;

import arc.assets.Loadable;
import arc.files.Fi;
import arc.util.Disposable;
import mindustry.mod.Mod;
import org.o7.Fire.Glopion.Patch.EventHooker;

public interface Module extends Loadable, Disposable {
    
    /**
     * On {@link Class#forName(String)} Invoke
     *
     * @see mindustry.mod.Mods#loadMod(Fi, boolean)
     */
    default void preInit() throws Throwable {
    
    }
    
    /**
     * Every Frame or {@link mindustry.game.EventType.Trigger Trigger Update}
     *
     * @see org.o7.Fire.Glopion.Patch.EventHooker
     */
    default void update() {
    
    }
    
    /**
     * User triggered reset, world reset {@link EventHooker#resets()}
     */
    default void reset() throws Throwable {
    
    }
    
    /**
     * When {@link Mod#init()} is invoked
     */
    default void init() throws Throwable {
    
    }
    
    /**
     * When {@link mindustry.game.EventType.ServerLoadEvent ServerLoadEvent} or {@link mindustry.game.EventType.ClientLoadEvent ClientLoadEvent} is called
     */
    default void postInit() throws Throwable {
    
    }
    /**
     * Called after {@link Module#init} and if the enviroment is test
     */
    default void test() {
    
    }
    
    default void onShutdown() {
    
    }
    
    @Override
    default void dispose() {
    
    }
    
    @Override
    default boolean isDisposed() {
        return true;
    }
}