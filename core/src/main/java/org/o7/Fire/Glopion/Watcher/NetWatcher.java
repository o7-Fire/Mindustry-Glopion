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

package org.o7.Fire.Glopion.Watcher;

import arc.util.Log;
import mindustry.Vars;
import mindustry.net.Net;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Net.LoggableNet;

public class NetWatcher extends LoggableNet {
    
    public NetWatcher(Net net) {
        super(net);
        Object provider = Vars.platform.getNet();
        Log.infoTag("NetWatcher-Watcher", provider.getClass().getName());
        Interface.showInfo("NetWatcher Watcher: " + provider.getClass().getName());
        
    }
    
    
}
