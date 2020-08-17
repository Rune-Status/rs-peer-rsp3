package org.rspeer.game.script.process;

import org.rspeer.game.script.Script;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ScriptPool {

    private final Set<ScriptProcess> passives;

    private ScriptProcess primary;
    private ScriptProcess active;

    public ScriptPool() {
        this.passives = new HashSet<>();
    }

    public ScriptProcess getPrimary() {
        return primary;
    }

    public void setPrimary(ScriptProcess primary) {
        if (this.primary != null) {
            throw new IllegalStateException();
        }
        this.primary = primary;
        this.active = primary;
    }

    public ScriptProcess getActive() {
        return active;
    }

    public void setActive(Script script) {
        setActive(ScriptProcess.Factory.provide(this, script));
    }

    //TODO pause prev process and run passed one
    public void setActive(ScriptProcess active) {
        setState(Script.State.PAUSED);

        this.active = active;
        if (active.script.getState() == Script.State.STOPPED) {
            setState(Script.State.STARTING);
            setState(Script.State.RUNNING);
        } else if (active.script.getState() == Script.State.PAUSED) {
            setState(Script.State.RUNNING);
        }
        active.start();
    }

    public void setState(Script.State state) {
        active.script.setState(state);
    }

    public Set<ScriptProcess> getPassives() {
        return Collections.unmodifiableSet(passives);
    }

    public void addPassive(Script... scripts) {
        for (Script script : scripts) {
            if (!script.getMeta().passive()) {
                throw new IllegalArgumentException();
            }
            ScriptProcess process = ScriptProcess.Factory.provide(this, script);
            passives.add(process);
            script.setState(Script.State.STARTING);
            script.setState(Script.State.RUNNING);
            process.start();
        }
    }

    public void kill() {
        active.kill();
        primary.kill();
        passives.forEach(ScriptProcess::kill);

        active = null;
    }
}
