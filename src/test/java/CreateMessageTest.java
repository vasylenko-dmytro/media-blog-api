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

public class CreateMessageTest {
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
     * Sending an http request to POST localhost:8080/messages with valid message credentials
     * <p>
     * Expected Response:
     * Status Code: 200
     * Response Body: JSON representation of message object
     */
    @Test
    public void createMessageSuccessful() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/messages")).POST(HttpRequest.BodyPublishers.ofString("{" + "\"posted_by\":1, " + "\"message_text\": \"hello message\", " + "\"time_posted_epoch\": 1669947792}")).header("Content-Type", "application/json").build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        assertEquals(200, status);

        ObjectMapper om = new ObjectMapper();
        Message expectedResult = new Message(2, 1, "hello message", 1669947792);
        System.out.println(response.body());
        Message actualResult = om.readValue(response.body(), Message.class);
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Sending an http request to POST localhost:8080/messages with empty message
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void createMessageMessageTextBlank() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/messages")).POST(HttpRequest.BodyPublishers.ofString("{" + "\"posted_by\":1, " + "\"message_text\": \"\", " + "\"time_posted_epoch\": 1669947792}")).header("Content-Type", "application/json").build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(400, status);
        assertEquals("", response.body());
    }


    /**
     * Sending an http request to POST localhost:8080/messages with message length greater than 255
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void createMessageMessageGreaterThan255() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/messages")).POST(HttpRequest.BodyPublishers.ofString("{" + "\"posted_by\":1, " + "\"message_text\": \"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\", " + "\"time_posted_epoch\": 1669947792}")).header("Content-Type", "application/json").build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(400, status);
        assertEquals("", response.body());
    }


    /**
     * Sending an http request to POST localhost:8080/messages with a user id that doesn't exist in db
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void createMessageUserNotInDb() throws IOException, InterruptedException {
        HttpRequest postMessageRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/messages")).POST(HttpRequest.BodyPublishers.ofString("{" + "\"posted_by\":3, " + "\"message_text\": \"message test\", " + "\"time_posted_epoch\": 1669947792}")).header("Content-Type", "application/json").build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(400, status);
        assertEquals("", response.body());
    }

}
