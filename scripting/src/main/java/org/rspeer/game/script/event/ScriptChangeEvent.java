package org.rspeer.game.script.event;

import org.rspeer.event.Event;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.loader.ScriptSource;

public class ScriptChangeEvent extends Event<Script> {

    private final Script.State state;
    private final Script.State previousState;

    public ScriptChangeEvent(Script script, Script.State newState) {
        super(script);
        this.state = newState;
        this.previousState = script.getState();
    }

    public Script.State getPreviousState() {
        return previousState;
    }

    public Script.State getState() {
        return state;
    }
}
