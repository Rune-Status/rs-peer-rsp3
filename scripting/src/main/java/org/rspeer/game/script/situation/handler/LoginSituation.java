package org.rspeer.game.script.situation.handler;

import org.rspeer.game.Game;
import org.rspeer.game.GameAccount;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.process.ScriptController;
import org.rspeer.game.script.ScriptMeta;
import org.rspeer.game.script.situation.GameSituationScript;

@ScriptMeta(name = "Login Situation Handler", developer = "Lezor")
public class LoginSituation extends GameSituationScript {

    private final Script trigger;

    public LoginSituation(ScriptController controller, Script trigger) {
        super(controller);
        this.trigger = trigger;
    }

    @Override
    public int loop() {
        if (Game.isLoggedIn()
                || Game.getState() == Game.STATE_LOADING_REGION
                || Game.getState() == Game.STATE_HOPPING_WORLD) {
            complete();
            trigger.setState(State.RUNNING);
            return -1;
        }

        GameAccount account = controller.getActive().getAccount();
        if (account != null && account.validate()) {
            Game.getClient().setUsername(account.getDetails().getUsername());
            Game.getClient().setPassword(account.getDetails().getPassword());
            Game.getClient().setGameState(20);
        }
        return 2000;
    }
}
