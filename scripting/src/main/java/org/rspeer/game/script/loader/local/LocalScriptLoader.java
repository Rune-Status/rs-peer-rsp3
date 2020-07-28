package org.rspeer.game.script.loader.local;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.rspeer.event.EventDispatcher;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.loader.ScriptBundle;
import org.rspeer.game.script.loader.ScriptProvider;
import org.rspeer.game.script.loader.ScriptSource;

public class LocalScriptLoader implements ScriptProvider {

    private final Path root;

    public LocalScriptLoader(Path root) {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ScriptBundle load() {
        ScriptBundle bundle = new ScriptBundle();
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                return bundle;
            }
        }

        try (URLClassLoader rootLoader = URLClassLoader.newInstance(new URL[]{ root.toUri().toURL() })) {
            Files.find(root,
                       Integer.MAX_VALUE,
                       (path, attr) -> attr.isRegularFile() &&
                                       (path.toString().endsWith(".jar") || path.toString().endsWith(".class")))
                 .map(Path::toFile)
                 .forEach(file -> {
                     if (file.getName().endsWith(".class")) {
                         try {
                             String raw = file.getPath();
                             raw = raw.substring(root.toString().length() + 1);
                             raw = raw.substring(0, raw.length() - ".class".length());
                             raw = raw.replace(File.separatorChar, '.');
                             Class<?> clazz = rootLoader.loadClass(raw);
                             if (test(clazz)) {
                                 bundle.add(new ScriptSource((Class<? extends Script>) clazz));
                             }
                         } catch (ClassNotFoundException e) {
                             e.printStackTrace();
                         }
                     } else if (file.getName().endsWith(".jar")) {
                         try (JarFile jar = new JarFile(file);
                              URLClassLoader jarLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()})) {
                             Enumeration<JarEntry> elems = jar.entries();
                             while (elems.hasMoreElements()) {
                                 JarEntry entry = elems.nextElement();
                                 if (entry.getName().endsWith(".class")) {
                                     String name = entry.getName();
                                     name = name.substring(0, name.length() - ".class".length());
                                     name = name.replace('/', '.');
                                     Class<?> clazz = jarLoader.loadClass(name);
                                     if (test(clazz)) {
                                         bundle.add(new ScriptSource((Class<? extends Script>) clazz));
                                     }
                                 }

                             }
                         } catch (Throwable e) {
                             e.printStackTrace();
                         }
                     }
                 });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    @Override
    public Script define(ScriptSource source, EventDispatcher environmentDispatcher) {
        try {
            Script script = source.getTarget().getDeclaredConstructor().newInstance();
            script.setEnvironmentDispatcher(environmentDispatcher);
            script.setSource(source);
            return script;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
    }
}
