package org.rspeer.game.script.process;

import org.rspeer.commons.Time;
import org.rspeer.game.Game;
import org.rspeer.game.script.Script;

public class DefaultScriptProcess extends ScriptProcess {

    private Thread thread;

    public DefaultScriptProcess(Script script) {
        super(script);
    }

    @Override
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void kill() {
        thread.interrupt();
        thread = null;
    }

    @Override
    public void run() {
        while (true) {
            Script.State state = script.getState();

            if (state == Script.State.PAUSED) {
                Time.sleep(100);
                continue;
            } else if (state == Script.State.STOPPED) {
                break;
            }

            Game.getClient().setMouseIdleTime(0);

            int sleep = script.loop();
            if (sleep < 0) {
                script.setState(Script.State.STOPPED);
                return;
            }

            Time.sleep(sleep);
        }
    }
}
