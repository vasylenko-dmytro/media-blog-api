package service;

import java.util.List;

import Model.Message;
import dao.impl.MessageDaoImpl;

public class MessageService {
    private MessageDaoImpl messageDaoImpl;
    
    public MessageService() {
        messageDaoImpl = new MessageDaoImpl();
    }

    public MessageService(MessageDaoImpl messageDaoImpl) {
        this.messageDaoImpl = messageDaoImpl;
    }

    public void create(Message message) {
        messageDaoImpl.create(message);
    }
    
    public List<Message> findAllMessages() {
        return messageDaoImpl.findAll();
    }

    public Message findByMessageId(int id) {
        return messageDaoImpl.findById(id);
    }

    public void updateMessage(Message message) {
        messageDaoImpl.update(message);
    }

    public void deleteMessage(int id) {
        messageDaoImpl.delete(messageDaoImpl.findById(id));
    }

    public List<Message> findAllMessagesByAccountId(int id) {
        return messageDaoImpl.findMessagesByAccountId(id);
    }
}
