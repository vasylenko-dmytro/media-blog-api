package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Account;
import util.ConnectionUtil;
import dao.AccountDao;
import exception.DaoException;

public class AccountDaoImpl extends GenericDao<Account> implements AccountDao {

    private static final String FIND_ALL_QUERY = "SELECT * FROM account";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM account WHERE account_id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM account WHERE username = ?";
    private static final String FIND_BY_NAME_AND_PASS_QUERY = "SELECT * FROM account WHERE username = ? AND password = ?";
    private static final String CREATE_QUERY = "INSERT INTO account (username, password) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE account SET username = ? WHERE account_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM account WHERE account_id = ?";

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_QUERY)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    accounts.add(setAccountFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find all accounts", e.getCause());
        }
        return accounts;
    }

    @Override
    public Account findById(int id) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return setAccountFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find by username", e.getCause());
        }
        return null;
    }

    @Override
    public Account findByName(String username) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME_QUERY)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return setAccountFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find by username", e.getCause());
        }
        return null;
    }

    @Override
    public Account findByNameAndPass(String username, String password) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME_AND_PASS_QUERY)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return setAccountFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find by username and password", e.getCause());
        }
        return null;
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected String getDeleteQuery() {
        return DELETE_QUERY;
    }

    @Override
    protected String getCreateQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected void prepareRemoveStatement(PreparedStatement preparedStatement, Account account) throws SQLException {
        preparedStatement.setInt(1, account.getAccount_id());
    }

    @Override
    protected void prepareUpdateStatement(PreparedStatement preparedStatement, Account account) throws SQLException {
        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setInt(1, account.getAccount_id());
    }

    @Override
    protected void prepareCreateStatement(PreparedStatement preparedStatement, Account account) throws SQLException {
        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setString(2, account.getPassword());
    }

    @Override
    protected void setEntityId(Account account, int id) {
        account.setAccount_id(id);
    }

    private Account setAccountFromResultSet(ResultSet resultSet) {
        Account account = new Account();
        try {
            account.setAccount_id(resultSet.getInt("account_id"));
            account.setUsername(resultSet.getString("username"));
            account.setPassword(resultSet.getString("password"));
        } catch (SQLException e) {
            throw new DaoException("Cannot extract account from ResultSet", e.getCause());
        }
        return account;
    }
}
