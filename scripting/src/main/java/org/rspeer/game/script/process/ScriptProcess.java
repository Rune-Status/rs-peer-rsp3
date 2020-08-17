package org.rspeer.game.script.process;

import org.rspeer.game.script.Script;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ScriptProcess implements Runnable {

    protected final ScriptPool pool;
    protected final Script script;
    protected final ExecutorService executor;

    protected ScriptProcess(ScriptPool pool, Script script, ExecutorService executor) {
        this.pool = pool;
        this.script = script;
        this.executor = executor;
    }

    protected ScriptProcess(ScriptPool pool, Script script) {
        this(pool, script, Executors.newSingleThreadExecutor());
    }

    public abstract void start();

    public abstract void kill();

    public static class Factory {

        public static ScriptProcess provide(ScriptPool pool, Script script) {
            if (script.getMeta().passive()) {
                return new PassiveScriptProcess(pool, script);
            }
            return new ActiveScriptProcess(pool, script);
        }
    }
}
