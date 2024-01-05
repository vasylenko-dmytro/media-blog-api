package Controller;

import java.util.List;

import Model.Account;
import Model.Message;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.AccountService;
import service.MessageService;
import validator.AccountValidator;
import validator.MessageValidator;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private MessageService messageService = new MessageService();

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerAccount);
        app.post("/login", this::login);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::retrieveAllMessages);
        app.get("/messages/{id}", this::findMessageById); 
        app.get("/accounts/{account_id}/messages", this::findMessagesByAccountId); 
        app.patch("/messages/{id}", this::updateMessage);
        app.delete("/messages/{id}", this::deleteMessage);

        return app;
    }

    /**
     * Register of the account
     * 
     * @param context  manages information about HTTP request and response.
     */
    private void registerAccount(Context context) {
        AccountService service = new AccountService();
        AccountValidator validator = new AccountValidator(service);
        Account newAccount = context.bodyAsClass(Account.class);
        try {
            if (validator.isUsernameBlank(newAccount) 
            || validator.isLengthPasswordWeak(newAccount) 
            || validator.isUsernameExist(newAccount)) {
                context.status(400);
                return;
            }
            service.create(newAccount);
            context.status(200).json(newAccount);
        } catch (Exception e) {
            context.status(500);
            context.result(String.format("Internal server error: %s", e.getMessage()));
        }
    }
    
    /**
     * Login of the verified Account
     * 
     * @param context   manages information about HTTP request and response.
     * @return          Account object
     */
    public Account login(Context context) {
        Account account = context.bodyAsClass(Account.class);
        AccountService service = new AccountService();
        Account authAccount = service.login(account);
        if (authAccount != null) {
            context.status(200).json(authAccount);
        } else {
            context.status(401);
        }
        return authAccount;
    }

    /**
     * Create Message object
     * 
     * @param context   manages information about HTTP request and response.
     */
    private void createMessage(Context context) {
        Message message = context.bodyAsClass(Message.class);
        MessageValidator validator = new MessageValidator(messageService);
        try {
            if (validator.isPosterAccountMissing(message) 
            || validator.isMessageOverLength(message)
            || validator.isMessageTextBlank(message)) {
                context.status(400);
                return;
            }
            messageService.create(message);
            context.status(200).json(message);
        } catch (Exception e) {
            context.status(500);
            context.result(String.format("Internal server error: %s", e.getMessage()));
        }
    }

    /**
     * Retrieve all messages
     * 
     * @param context   manages information about HTTP request and response.
     * @return          list of messages from db.    
     */
    private List<Message> retrieveAllMessages(Context context) {
        List<Message> listOfMessages = messageService.findAllMessages();
        context.status(200).jsonStream(listOfMessages);
        
        return listOfMessages;
     
    }
 
    /**
     * Get all messages by ID
     * 
     * @param context   manages information about HTTP request and response.
     */
    private void findMessageById(Context context) {
        int id = Integer.parseInt(context.pathParam("id"));
        Message foundMessage = messageService.findByMessageId(id);
        if (foundMessage != null) {
            context.json(foundMessage); 
            context.status(200);
        }
    }

    /**
     * Update message object
     * 
     * @param context   manages information about HTTP request and response.
     */
    private void updateMessage(Context context) {
        int id = Integer.parseInt(context.pathParam("id"));
        Message message = context.bodyAsClass(Message.class);
        Message existingMessage = messageService.findByMessageId(id);
        MessageValidator validator = new MessageValidator(messageService);
        if(existingMessage == null) {
            context.status(400);
            return;
        }
        existingMessage.setMessage_text(message.getMessage_text());
        try {
            if (validator.isMessageOverLength(existingMessage) 
            || validator.isMessageTextBlank(existingMessage) ) {
                context.status(400);
                return;
        }

        messageService.updateMessage(existingMessage);
        context.status(200).json(existingMessage); 
        } catch (Exception e) {
            context.status(500);
            context.result(String.format("Internal server error: %s", e.getMessage()));
        }
    }
    
   /**
     * Remove Message
     * 
     * @param context   manages information about HTTP request and response.
     */
     private void deleteMessage(Context context){
        int id = Integer.parseInt(context.pathParam("id"));
        Message foundMessage = messageService.findByMessageId(id);
        if (foundMessage != null) {
            messageService.deleteMessage(id);
            context.status(200).json(foundMessage);
        } else {
            context.status(200).result("");
        }
    }
    
    /**
     * Get Messages by Account ID
     * 
     * @param context   manages information about HTTP request and response.
     */
    public void findMessagesByAccountId(Context context){
        int id = Integer.parseInt(context.pathParam("account_id"));
        List<Message> listOfMessages = messageService.findAllMessagesByAccountId(id);
        context.status(200).json(listOfMessages);
    }
}