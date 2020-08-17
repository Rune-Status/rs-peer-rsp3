package org.rspeer.game.script;

import org.rspeer.commons.Configuration;
import org.rspeer.commons.Executor;
import org.rspeer.game.GameAccount;

import java.nio.file.Path;

//TODO make this better, add random handling (login screen, welcome screen etc)
public abstract class Script {

    private GameAccount account = null;
    private State state = State.STOPPED;

    public static Path getDataDirectory() {
        return Configuration.Paths.DATA_LOCATION;
    }

    public abstract int loop();

    public void onStart(String... args) {

    }

    public void onFinish() {

    }

    public State getState() {
        return state;
    }

    public final void setState(State state) {
        this.state = state;
        if (state == State.STOPPED) {
            Executor.execute(this::onFinish);
        } else if (state == State.STARTING) {
            onStart();
        }
    }

    public final ScriptMeta getMeta() {
        return getClass().getAnnotation(ScriptMeta.class);
    }

    public GameAccount getAccount() {
        return account;
    }

    public void setAccount(GameAccount account) {
        this.account = account;
    }

    public enum State {
        STARTING,
        RUNNING,
        PAUSED,
        STOPPED
    }
}
