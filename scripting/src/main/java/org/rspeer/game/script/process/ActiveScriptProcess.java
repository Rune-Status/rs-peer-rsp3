package org.rspeer.game.script.process;

import org.rspeer.commons.Time;
import org.rspeer.game.Game;
import org.rspeer.game.script.Script;

public class ActiveScriptProcess extends ScriptProcess {

    public ActiveScriptProcess(ScriptPool pool, Script script) {
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

        if (pool.getActive() == pool.getPrimary()) {
            for (ScriptProcess passive : pool.getPassives()) {
                passive.run();
            }
        }

        if (state == Script.State.STOPPED) {
            return;
        }

        Game.getClient().setMouseIdleTime(0);

        int sleep = script.loop();
        if (sleep < 0) {
            script.setState(Script.State.STOPPED);
            return;
        }

        Time.sleep(sleep);
        executor.submit(this);
    }
}
