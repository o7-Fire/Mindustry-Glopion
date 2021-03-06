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

package org.o7.Fire.Glopion.Internal.Shared;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class InfoBox {
    
    public static void write(Throwable t) {
        File f = new File("crash-report-" + Thread.currentThread().getStackTrace()[2].getClassName() + ".txt");
        try {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            t.printStackTrace(writer);
            FileOutputStream fs = new FileOutputStream(f);
            fs.write(out.toString().getBytes());
            fs.close();
        }catch(Throwable ignored){}
    }
    
    public static void box(String title, String message, int type) {
        try {
            requireDisplay();
            JOptionPane.showMessageDialog(null, message, title, type);
        }catch(Throwable ignored){}
    }
    
    public static void requireDisplay() {
        try {
            if (GraphicsEnvironment.isHeadless())
                throw new RuntimeException(new IllegalStateException("This operation require GraphicsEnvironment"));
        }catch(Throwable ignored){
            throw new RuntimeException(new IllegalStateException("This operation require GraphicsEnvironment"));
        }
    }
    
    public static void infoBox(String title, String message) {
        box(title, message, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void errorBox(String infoMessage, String titleBar) {
        box(titleBar, infoMessage, JOptionPane.ERROR_MESSAGE);
    }
}
