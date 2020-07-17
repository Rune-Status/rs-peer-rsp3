package org.rspeer.game.script;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptMeta {

    String name();

    String desc() default "";

    String developer() default "";

    double version() default 1.0;

    boolean passive() default false;
}
