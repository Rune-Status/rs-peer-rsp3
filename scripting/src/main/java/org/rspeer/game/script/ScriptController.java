package org.rspeer.game.script;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.rspeer.event.EventDispatcher;
import org.rspeer.event.Subscribe;
import org.rspeer.game.script.event.ScriptChangeEvent;
import org.rspeer.game.script.loader.ScriptBundle;
import org.rspeer.game.script.loader.ScriptLoaderProvider;
import org.rspeer.game.script.loader.ScriptProvider;
import org.rspeer.game.script.loader.ScriptSource;
import org.rspeer.game.script.process.ScriptProcess;

public class ScriptController {

    private ScriptPool pool;
    private ScriptSource source;
    private ScriptProcess process;

    private boolean reload;

    @Inject
    public ScriptController(@Named("BotDispatcher") EventDispatcher environmentDispatcher) {
        environmentDispatcher.subscribe(this);
    }

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

    @Subscribe
    public void notify(ScriptChangeEvent e) {
        if (e.getState() == Script.State.STOPPED) {
            if (process != null) {
                stop();
            }

            if (!reload) {
                source = null;
                process = null;
                return;
            }

            reload = false;

            ScriptProvider loader = new ScriptLoaderProvider().get();
            ScriptBundle bundle = loader.load();
            ScriptSource reloaded = bundle.findShallow(source);

            if (reloaded != null) {
                start(loader, reloaded);
            } else {
                source = null;
                process = null;
            }
        }
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }
}
