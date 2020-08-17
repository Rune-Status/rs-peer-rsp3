package org.rspeer.game.script.situation.trigger;

import org.rspeer.game.Game;
import org.rspeer.game.script.ScriptMeta;
import org.rspeer.game.script.process.ScriptController;
import org.rspeer.game.script.situation.GameSituationScript;
import org.rspeer.game.script.situation.handler.LoginSituation;

@ScriptMeta(name = "Login Situation Trigger", developer = "Lezor", passive = true)
public class LoginSituationTrigger extends GameSituationScript {

    public LoginSituationTrigger(ScriptController controller) {
        super(controller);
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn()
                && Game.getState() != Game.STATE_LOADING_REGION
                && Game.getState() != Game.STATE_HOPPING_WORLD) {
            controller.getPool().setActive(new LoginSituation(controller, this));
            setState(State.PAUSED);
        }

        return 2000;
    }
}
