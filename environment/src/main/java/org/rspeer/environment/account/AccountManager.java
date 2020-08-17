package org.rspeer.environment.account;

public class AccountManager {

    private final AccountProvider provider;

    public AccountManager(AccountProvider provider) {
        this.provider = provider;
        provider.load();
    }


}
