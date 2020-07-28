package org.rspeer.game.script.process;

import org.rspeer.game.script.Script;

import java.util.HashSet;
import java.util.Set;

public class ScriptPool {

    private final ScriptProcess primary;
    private final Set<ScriptProcess> passives;

    private ScriptProcess active;

    public ScriptPool(ScriptProcess primary) {
        this.primary = primary;
        this.active = primary;
        this.passives = new HashSet<>();
    }

    public ScriptProcess getPrimary() {
        return primary;
    }

    public ScriptProcess getActive() {
        return active;
    }

    public void setActive(Script script) {
        setActive(ScriptProcess.Factory.provide(script));
    }

    //TODO pause prev process and run passed one
    public void setActive(ScriptProcess active) {
        this.active = active;
    }

    public void setState(Script.State state) {
        active.script.setState(state);
    }

    public void addPassive(Script... scripts) {
        for (Script script : scripts) {
            if (!script.getMeta().passive()) {
                throw new IllegalArgumentException();
            }
            passives.add(ScriptProcess.Factory.provide(script));
        }
    }

    public void kill() {
        active.kill();
        primary.kill();
        passives.forEach(ScriptProcess::kill);

        active = null;
    }
}
