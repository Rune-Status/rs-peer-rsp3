package org.rspeer.game.script.loader;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import org.rspeer.event.EventDispatcher;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.ScriptMeta;

public interface ScriptProvider extends Predicate<Class<?>> {

    ScriptBundle load();

    default ScriptBundle predefined() {
        return new ScriptBundle();
    }

    Script define(ScriptSource source, EventDispatcher environmentDispatcher);

    @Override
    default boolean test(Class<?> clazz) {
        return !Modifier.isAbstract(clazz.getModifiers())
               && Script.class.isAssignableFrom(clazz)
               && clazz.isAnnotationPresent(ScriptMeta.class);
    }
}
