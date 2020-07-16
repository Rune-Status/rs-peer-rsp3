package org.rspeer.game.script;

import org.rspeer.game.script.loader.ScriptProvider;
import org.rspeer.game.script.loader.ScriptSource;
import org.rspeer.game.script.process.ScriptProcess;

public class ScriptController {

    private ScriptPool pool;
    private ScriptSource source;
    private ScriptProcess process;

    public void start(ScriptProvider provider, ScriptSource source) {
        if (pool != null) {
            throw new IllegalStateException("A script is already running");
        }

        this.source = source;
        pool = new ScriptPool(provider.define(source));
        pool.getActive().setState(Script.State.STARTING);
        pool.getActive().setState(Script.State.RUNNING);

        process = ScriptProcess.Factory.provide(pool.getActive());
        process.start();
    }

    public void stop() {
        if (pool != null) {
            pool.getActive().setState(Script.State.STOPPED);
            pool = null;
        }

        process.kill();
        process = null;
        source = null;
    }

    public ScriptSource getSource() {
        return source;
    }

    public ScriptPool getPool() {
        return pool;
    }
}
