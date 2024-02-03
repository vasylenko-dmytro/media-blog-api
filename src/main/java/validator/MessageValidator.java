package validator;

import model.Message;
import service.MessageService;

public class MessageValidator {
    private MessageService service;

    public MessageValidator(MessageService service) {
        this.service = service;
    }

    public boolean isMessageTextBlank(Message message) {
        return message.getMessage_text().isBlank();
    }

    public boolean isMessageOverLength(Message message) {
        return message.getMessage_text().length() > 255;
    }

    public boolean isPosterAccountMissing(Message message) {
        return service.findByMessageId(message.getPosted_by()) == null;
    }
}
