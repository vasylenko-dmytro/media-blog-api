package dao;

import java.util.List;

import model.Message;

public interface MessageDao extends Dao<Message> {
    void create(Message message);
    void update(Message message);
    void delete(Message message);
    List<Message> findMessagesByAccountId(int id);
}
