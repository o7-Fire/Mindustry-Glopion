/*
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
 */

import Atom.Encoding.Encoder;
import Atom.File.FileUtility;
import Atom.Utility.Utility;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.configuration.PrettyPrinterConfiguration;
import mindustry.gen.Call;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//imagine writing this for 4 hour
public class GenerateCallNet {
    
    public static void main(String[] args) throws Throwable {
        String version = "a2a4302c8c";
        String callable = new String(GenerateCallNet.class.getClassLoader().getResourceAsStream("Callable.java").readAllBytes());
        URL sourceJar = new URL("https://jitpack.io/com/github/Anuken/Mindustry/core/" + version + "/core-" + version + "-sources.jar");
        DownloadPatch.repo.addRepo(FileUtility.convertToURLJar(sourceJar));
        File b = new File("core/src/main/java/");
        String name = "Callable";//+version.replace(".","dot");
        File gen = new File(b, "org/o7/Fire/Glopion/Gen/" + name + ".java");
        String path = Call.class.getName().replace('.', '/') + ".java";
        System.out.println(path);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{sourceJar}, GenerateCallNet.class.getClassLoader());
        
        CompilationUnit base = StaticJavaParser.parse(callable), callCU = StaticJavaParser.parse(Encoder.readString(classLoader.getResource(path).openStream()));
        String packages = gen.getParentFile().getAbsolutePath().replaceFirst(b.getAbsolutePath() + "/", "").replace('/', '.');
        ClassOrInterfaceDeclaration clazz = base.getClassByName("Callable").get(), callClazz = callCU.getClassByName("Call").get();
        clazz.setName(name);
        clazz.getConstructors().forEach(constructorDeclaration -> constructorDeclaration.setName(name));
        MethodDeclaration method = clazz.getMethodsByName("base").get(0);
        Statement post = StaticJavaParser.parseStatement("post();");
        base.getPackageDeclaration().get().setName(packages);
        //for(ImportDeclaration i : callCU.getImports()) base.addImport(i);
        for (Method met : Call.class.getDeclaredMethods()) {
            MethodDeclaration me = null;
            for (MethodDeclaration md : callClazz.getMethodsByName(met.getName())) {
                if (md.getParameters().size() == met.getParameterCount()){
                    me = md;
                    break;
                }
            }
            assert me != null;
            if (!me.hasModifier(Modifier.Keyword.PUBLIC) || !me.hasModifier(Modifier.Keyword.STATIC)) continue;
            MethodDeclaration m = me.clone();
            callClazz.remove(m);
            m.removeBody();
            m.removeModifier(Modifier.Keyword.STATIC);
            
            m.setBody(method.getBody().get().clone());
            BlockStmt body = m.getBody().get();
            ArrayList<String> param = new ArrayList<>();
            
            for (int i = 0; i < met.getParameterCount(); i++) {
                Parameter p = m.getParameter(i);
                Type t = StaticJavaParser.parseType(met.getParameters()[i].getType().getTypeName().replace('$', '.'));
                m.setParameter(i, new Parameter(t, p.getName().toString()));
            }
            for (Parameter p : m.getParameters()) {
                param.add(p.getName().toString());
            }
            Statement call = StaticJavaParser.parseStatement(Call.class.getName() + "." + m.getName() + "(" + Utility.joiner(param, ", ") + ");");
            System.out.println("Generating: " + call.toString());
            body.addStatement(call);
            body.addStatement(post);
            clazz.addMember(m);
        }
        String comment = "\n" + Utility.getDate() + "\nGenerated for Mindustry: " + version + "\n";
        base.setComment(new BlockComment(comment));
        gen.getParentFile().mkdirs();
        System.out.println(gen.getAbsoluteFile().getAbsolutePath());
        FileUtility.write(gen, base.toString(new PrettyPrinterConfiguration()).getBytes(StandardCharsets.UTF_8));
    }
}
