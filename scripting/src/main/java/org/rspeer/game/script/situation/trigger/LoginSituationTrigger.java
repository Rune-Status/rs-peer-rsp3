package org.rspeer.game.script.situation.trigger;

import org.rspeer.game.script.ScriptController;
import org.rspeer.game.script.ScriptMeta;
import org.rspeer.game.script.situation.GameSituationScript;
import org.rspeer.game.script.situation.handler.LoginSituation;

@ScriptMeta(name = "Login Situation Trigger", passive = true)
public class LoginSituationTrigger extends GameSituationScript {

    public LoginSituationTrigger(ScriptController controller) {
        super(controller);
    }

    @Override
    public int loop() {
        if (false) { //if logged out
            controller.getPool().setActive(new LoginSituation(controller));
        }
        return 0;
    }

}
