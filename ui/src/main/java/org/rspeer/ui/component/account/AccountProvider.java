package org.rspeer.ui.component.account;

import org.rspeer.game.GameAccount;

import java.util.LinkedList;
import java.util.List;

public abstract class AccountProvider {

    protected final List<GameAccount> accounts;

    public AccountProvider() {
        accounts = new LinkedList<>();
        load();
    }

    protected abstract void load();

    protected abstract void save();

    public final synchronized void add(GameAccount account) {
        accounts.add(account);
        save();
    }

    public final synchronized void remove(GameAccount account) {
        accounts.remove(account);
        save();
    }
}
