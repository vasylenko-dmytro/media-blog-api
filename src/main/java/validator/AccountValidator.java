package validator;

import Model.Account;
import service.AccountService;

public class AccountValidator {
    private AccountService service;

    public AccountValidator(AccountService service) {
        this.service = service;
    }

    public boolean isUsernameBlank(Account account) {
        return account.getUsername().isBlank();
    }

    public boolean isLengthPasswordWeak(Account account) {
        return account.getPassword().length() < 4;
    }

    public boolean isUsernameExist(Account account) {
        return service.findAccountByUsername(account.getUsername()) != null; 
    }
}
