package org.rspeer.game.script.situation;

import org.rspeer.game.script.Script;
import org.rspeer.game.script.process.ScriptController;
import org.rspeer.game.script.ScriptMeta;

public abstract class GameSituationScript extends Script {

    protected final ScriptController controller;

    protected GameSituationScript(ScriptController controller) {
        this.controller = controller;
    }

    @Override
    public void onStart(String... args) {
        ScriptMeta meta = getMeta();
        if (meta != null && !meta.passive()) {
            System.out.println("Starting GameSituationScript: " + meta.name() + " by " + meta.developer());
        }
    }

    protected void complete() {
        controller.getPool().setActive(controller.getPool().getPrimary());
    }
}
