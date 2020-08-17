package org.rspeer.environment;

import org.rspeer.environment.account.AccountManager;
import org.rspeer.environment.account.AccountProvider;
import org.rspeer.environment.account.JsonAccountProvider;
import org.rspeer.game.GameAccount;
import org.rspeer.game.component.tab.Skill;

public class AccountManagerTest {

    public static void main(String[] args) {
        AccountProvider prvdr = new JsonAccountProvider();
        AccountManager mngr = new AccountManager(prvdr);

        GameAccount test = new GameAccount(
                new GameAccount.Details("test", "u thought", ""),
                new GameAccount.Preferences(50, Skill.ATTACK)
        );

        prvdr.add(test);
     }
}
