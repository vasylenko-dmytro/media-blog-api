package dao.impl;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.ConnectionUtil;
import dao.Dao;
import exception.DaoException;

public abstract class GenericDao<E> implements Dao<E> {

    protected abstract String getUpdateQuery();
    protected abstract String getDeleteQuery();
    protected abstract String getCreateQuery();
    protected abstract void prepareRemoveStatement(PreparedStatement preparedStatement, E entity) throws SQLException;
    protected abstract void prepareUpdateStatement(PreparedStatement preparedStatement, E entity) throws SQLException;
    protected abstract void prepareCreateStatement(PreparedStatement preparedStatement, E entity) throws SQLException;
    protected abstract void setEntityId(E entity, int id);


    @Override
    public void create(E entity) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(getCreateQuery(), RETURN_GENERATED_KEYS)) {
                prepareCreateStatement(preparedStatement, entity);
                if (preparedStatement.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setEntityId(entity, generatedKeys.getInt(1));
                        }
                    }
                }
            } catch (SQLException exception) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new DaoException(exception.getMessage(), exception.getCause());
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(E entity) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(getUpdateQuery())) {
                prepareUpdateStatement(preparedStatement, entity);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new DaoException(exception.getMessage(), exception.getCause());
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void delete(E entity) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);
            connection.prepareStatement(getDeleteQuery());
            try (PreparedStatement preparedStatement = connection.prepareStatement(getDeleteQuery())) {
                prepareRemoveStatement(preparedStatement, entity);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new DaoException(exception.getMessage(), exception.getCause());
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage(), e.getCause());
        }
    }
}
