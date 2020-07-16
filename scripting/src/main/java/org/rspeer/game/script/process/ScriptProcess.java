package org.rspeer.game.script.process;

import org.rspeer.game.script.Script;
import org.rspeer.game.script.passive.PassiveScript;

public abstract class ScriptProcess implements Runnable {

    protected final Script script;

    protected ScriptProcess(Script script) {
        this.script = script;
    }

    public abstract void start();

    public abstract void kill();

    public static class Factory {

        public static ScriptProcess provide(Script script) {
            if (script instanceof PassiveScript) {
                return new PassiveScriptProcess(script);
            }
            return new DefaultScriptProcess(script);
        }
    }
}
