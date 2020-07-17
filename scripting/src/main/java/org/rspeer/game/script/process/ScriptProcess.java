package org.rspeer.game.script.process;

import org.rspeer.game.script.Script;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ScriptProcess implements Runnable {

    protected final Script script;
    protected final ExecutorService executor;

    protected ScriptProcess(Script script, ExecutorService executor) {
        this.script = script;
        this.executor = executor;
    }

    protected ScriptProcess(Script script) {
        this(script, Executors.newSingleThreadExecutor());
    }

    public abstract void start();

    public abstract void kill();

    public static class Factory {

        public static ScriptProcess provide(Script script) {
            if (script.getMeta().passive()) {
                return new PassiveScriptProcess(script);
            }
            return new DefaultScriptProcess(script);
        }
    }
}
