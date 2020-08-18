package org.rspeer.environment.arguments;

import org.rspeer.game.GameAccount;

public class AccountArgument implements DataArgument<GameAccount> {

    @Override
    public GameAccount getValue(String raw) {
        return null;
    }

    @Override
    public String getKey() {
        return "account";
    }
}
