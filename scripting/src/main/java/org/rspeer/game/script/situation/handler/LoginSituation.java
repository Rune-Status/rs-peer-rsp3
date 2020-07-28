package org.rspeer.game.script.situation.handler;

import org.rspeer.game.script.Script;
import org.rspeer.game.script.ScriptController;
import org.rspeer.game.script.ScriptMeta;
import org.rspeer.game.script.situation.GameSituationScript;

@ScriptMeta(name = "Login Situation Handler")
public class LoginSituation extends GameSituationScript {

    public LoginSituation(ScriptController controller) {
        super(controller);
    }

    @Override
    public int loop() {
        if (false) { //if done
            controller.getPool().setActive(controller.getPool().getPrimary());
        }
        return 0;
    }
}
