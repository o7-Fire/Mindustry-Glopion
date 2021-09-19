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
import Atom.Utility.Random;
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
import mindustry.gen.Call;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//imagine writing this for 4 hour
public class GenerateCallNet {
    public static String methodToStringStub(Method met) {
        StringBuilder sb = new StringBuilder();
        sb.append(java.lang.reflect.Modifier.toString(met.getModifiers())).append(" ").append(met.getReturnType().getCanonicalName()).append(" ").append(met.getName());
        
        sb.append(Arrays.stream(met.getParameters()).map(java.lang.reflect.Parameter::toString).collect(Collectors.joining(", ", "(", ")")));
        if (met.getExceptionTypes().length > 0){
            sb.append(Arrays.stream(met.getExceptionTypes()).map(Class::getCanonicalName).collect(Collectors.joining(",", " throws ", "")));
        }
        sb.append("{").append("return");
        if (!met.getReturnType().isPrimitive()){
            sb.append(" null");
        }else if (met.getReturnType() == void.class){
        
        }else{
            sb.append(Random.getRandom(met.getReturnType()));
        }
        return sb.append(";").append("}").toString();
        
    }
    
    public static MethodDeclaration convertToParser(Method m) {
        String s = methodToStringStub(m);
        return StaticJavaParser.parseMethodDeclaration(s);
    }
    
    public static void main(String[] args) throws Throwable {
        List<String> strings = Arrays.asList(args);
        String version = "v130";
        try {
            version = strings.get(0);
        }catch(IndexOutOfBoundsException gay){}
        System.out.println(version);
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
            String[] signatures = new String[met.getParameterCount()];
    
            java.lang.reflect.Parameter[] parameters = met.getParameters();
            for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                java.lang.reflect.Parameter s = parameters[i];
                signatures[i] = s.getType().getSimpleName();
            }
            try {
                me = callClazz.getMethodsBySignature(met.getName(), signatures).get(0);
            }catch(IndexOutOfBoundsException ignored){
        
            }
            if (me == null){
                List<MethodDeclaration> methodDeclarations = callClazz.getMethodsByName(met.getName());
        
                if (methodDeclarations.size() == 1){
                    me = methodDeclarations.get(0);
                }else if (!methodDeclarations.isEmpty()){
                    System.err.println("Searching for: " + met);
                    for (MethodDeclaration m : methodDeclarations) {
                        System.err.println("Ambiguous method: " + m.getSignature());
                    }
                }else{
                    System.err.println("No declaration in source for method: " + met);
                    me = convertToParser(met);
                    System.err.println("Generating stub:\n" + me.toString());
                }
            }
            if (me == null) throw new AssertionError("me is null: " + met);
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
            body = body.getStatements().get(0).asSynchronizedStmt().getBody();//get inside sync statement
            body.addStatement(call);
            body.addStatement(post);
            clazz.addMember(m);
        }
        String comment = "\n" + Utility.getDate() + "\nGenerated for Mindustry: " + version + "\n";
        base.setComment(new BlockComment(comment));
        
        gen.getParentFile().mkdirs();
        gen.delete();
        System.out.println(gen.getAbsoluteFile().getAbsolutePath());
        FileUtility.write(gen, base.toString().getBytes(StandardCharsets.UTF_8));
    }
}
