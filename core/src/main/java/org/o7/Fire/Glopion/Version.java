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

package org.o7.Fire.Glopion;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Version {
    public static String desktop = "0.2.0";
    public static String core = "0.6.0";
    
    public static boolean isCore() {
        return desktop.isEmpty();
    }
    
    public static String buildString() {
        return desktop.isEmpty() ? core : core + ":" + desktop;
    }
    
    public static void main(String[] args) throws Throwable {
        Files.write(new File("Version").toPath(), buildString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
    
    
}
