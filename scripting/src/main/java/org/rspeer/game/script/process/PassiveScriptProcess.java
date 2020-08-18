package org.rspeer.game.script.process;

import org.rspeer.commons.Time;
import org.rspeer.game.script.Script;

//TODO block interactions from this process?
public class PassiveScriptProcess extends ScriptProcess {

    public PassiveScriptProcess(ScriptPool pool, Script script) {
        super(pool, script);
    }

    @Override
    public void start() {
        executor.submit(this);
    }

    @Override
    public void kill() {
        script.setState(Script.State.STOPPED);
        executor.shutdown();
    }

    @Override
    public void run() {
        Script.State state = script.getState();
        if (state == Script.State.PAUSED) {
            Time.sleep(100);
            executor.submit(this);
            return;
        }

        if (state == Script.State.STOPPED) {
            return;
        }

        int sleep = script.loop();
        if (sleep < 0) {
            script.setState(Script.State.STOPPED);
            stateCallback.accept(Script.State.STOPPED);
            return;
        }

        Time.sleep(sleep);
        executor.submit(this);
    }
}
