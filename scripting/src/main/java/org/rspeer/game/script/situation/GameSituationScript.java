package org.rspeer.game.script.situation;

import org.rspeer.game.script.Script;
import org.rspeer.game.script.ScriptController;

public abstract class GameSituationScript extends Script {

    protected final ScriptController controller;

    protected GameSituationScript(ScriptController controller) {
        this.controller = controller;
    }
}
