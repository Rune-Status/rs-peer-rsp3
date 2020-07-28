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
import org.rspeer.game.script.process.ScriptPool;
import org.rspeer.game.script.process.ScriptProcess;
import org.rspeer.game.script.situation.trigger.LoginSituationTrigger;

public class ScriptController {

    private ScriptPool pool;
    private ScriptSource source;

    private boolean reload;

    @Inject
    public ScriptController(@Named("BotDispatcher") EventDispatcher dispatcher) {
        dispatcher.subscribe(this);
    }

    public void start(ScriptProvider provider, ScriptSource source) {
        if (pool != null) {
            throw new IllegalStateException("A script is already running");
        }

        this.source = source;

        Script script = provider.define(source);
        script.setState(Script.State.STARTING);
        script.setState(Script.State.RUNNING);

        pool = new ScriptPool(ScriptProcess.Factory.provide(script));
        pool.getActive().start();
        pool.addPassive(new LoginSituationTrigger(this));
    }

    public void stop() {
        if (pool != null) {
            pool.setState(Script.State.STOPPED);
            pool.kill();
            pool = null;
        }
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
            if (pool != null) {
                stop();
            }

            if (!reload) {
                source = null;
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
            }
        }
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }
}
