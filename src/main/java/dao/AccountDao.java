package dao;

import Model.Account;

public interface AccountDao extends Dao<Account>{
    void create(Account account);
    void update(Account account);
    void delete(Account account);
    Account findByName(String username);
    Account findByNameAndPass(String username, String password);
}
