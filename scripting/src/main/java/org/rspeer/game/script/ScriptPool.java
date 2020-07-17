package org.rspeer.game.script;

//TODO name?
public class ScriptPool {

    private final Script primary;

    private Script active;

    public ScriptPool(Script primary) {
        this.primary = primary;
    }

    public Script getPrimary() {
        return primary;
    }

    public Script getActive() {
        return active;
    }

    public void setActive(Script active) {
        this.active = active;
    }
}
