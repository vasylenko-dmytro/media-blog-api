package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Message;
import util.ConnectionUtil;
import dao.MessageDao;
import exception.DaoException;

public class MessageDaoImpl extends GenericDao<Message> implements MessageDao {


    private static final String FIND_ALL_QUERY = "SELECT * FROM message";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM message WHERE message_id = ?";
    private static final String FIND_BY_POSTED_ID_QUERY = "SELECT * FROM message WHERE posted_by = ?";
    private static final String CREATE_QUERY = "INSERT INTO message (message_text, posted_by, time_posted_epoch) VALUES (?,?,?)";
    private static final String DELETE_QUERY = "DELETE FROM message WHERE message_id = ?";
    private static final String UPDATE_QUERY = "UPDATE message SET message_text = ? WHERE message_id = ?";

    @Override
    public Message findById(int id) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return setMessageFromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            throw new DaoException("Cannot find message by id", e.getCause());
        }
        return null;
    }

    @Override
    public List<Message> findMessagesByAccountId(int id) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_POSTED_ID_QUERY)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(setMessageFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find messages by account id", e.getCause());
        }
        return messages;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_QUERY)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message(
                            resultSet.getInt("message_id"),
                            resultSet.getInt("posted_by"),
                            resultSet.getString("message_text"),
                            resultSet.getInt("time_posted_epoch"));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find all messages", e.getCause());
        }
        return messages;
    }

    @Override
    protected String getCreateQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected String getDeleteQuery() {
        return DELETE_QUERY;
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected void prepareCreateStatement(PreparedStatement preparedStatement, Message message) throws SQLException {
        preparedStatement.setString(1, message.getMessage_text());
        preparedStatement.setInt(2, message.getPosted_by());
        preparedStatement.setLong(3, message.getTime_posted_epoch());
    }

    @Override
    protected void prepareRemoveStatement(PreparedStatement preparedStatement, Message message) throws SQLException {
        preparedStatement.setInt(1, message.getMessage_id());
    }

    @Override
    protected void prepareUpdateStatement(PreparedStatement preparedStatement, Message message) throws SQLException {
        preparedStatement.setString(1, message.getMessage_text());
        preparedStatement.setInt(2, message.getMessage_id());
    }

    @Override
    protected void setEntityId(Message message, int id) {
        message.setMessage_id(id);
    }

    private Message setMessageFromResultSet(ResultSet resultSet) {
        Message message = new Message();
        try {
            message.setMessage_id(resultSet.getInt("message_id"));
            message.setPosted_by(resultSet.getInt("posted_by"));
            message.setMessage_text(resultSet.getString("message_text"));
            message.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
        } catch (SQLException e) {
            throw new DaoException("Cannot extract message from ResultSet", e.getCause());
        }
        return message;
    }
}
