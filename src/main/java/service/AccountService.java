package service;

import Model.Account;
import dao.impl.AccountDaoImpl;

public class AccountService {
    private AccountDaoImpl accountDaoImpl;
    
    public AccountService() {
        accountDaoImpl = new AccountDaoImpl();
    }

    public AccountService(AccountDaoImpl accountDaoImpl) {
        this.accountDaoImpl = accountDaoImpl;
    }

    public void create(Account account) {
        accountDaoImpl.create(account);
    }

    public Account login(Account account) {
        return accountDaoImpl.findByNameAndPass(account.getUsername(), account.getPassword());
    }

    public Account findAccountByUsername(String username) {
        return accountDaoImpl.findByName(username);
    }
}
