import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import controller.SocialMediaController;
import model.Message;
import util.ConnectionUtil;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateMessageTextTest {
    SocialMediaController socialMediaController;
    HttpClient webClient;
    ObjectMapper objectMapper;
    Javalin app;

    /**
     * Before every test, reset the database, restart the Javalin app, and create a new webClient
     * and ObjectMapper for interacting locally on the web.
     */
    @BeforeEach
    public void setUp() throws InterruptedException {
        ConnectionUtil.resetTestDatabase();
        socialMediaController = new SocialMediaController();
        app = socialMediaController.startAPI();
        webClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        app.start(8080);
        Thread.sleep(1000);
    }

    @AfterEach
    public void tearDown() {
        app.stop();
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/1
     * (message id exists in db) with successful message text
     * <p>
     * Expected Response:
     * Status Code: 200
     * Response Body: JSON representation of the message that was updated
     */
    @Test
    public void updateMessageSuccessful() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/1"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{" +
                        "\"message_text\": \"updated message\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(200, status);

        ObjectMapper om = new ObjectMapper();
        Message expectedResult = new Message(1, 1, "updated message", 1669947792);

        Message actualResult = om.readValue(response.body(), Message.class);
        assertEquals(expectedResult, actualResult);
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/1 (message id does NOT exist in db)
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void updateMessageMessageNotFound() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/2"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{" +
                        "\"message_text\": \"updated message\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(400, status);
        assertTrue(response.body().isEmpty());
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/1
     * (message text to update is an empty string)
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void updateMessageMessageStringEmpty() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/1"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{" +
                        "\"message_text\": \"\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(400, status);
        assertTrue(response.body().isEmpty());
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/1 (message text is too long)
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void updateMessageMessageTooLong() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/1"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{" +
                        "\"message_text\": \"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(400, status);
        assertTrue(response.body().isEmpty());
    }

}
