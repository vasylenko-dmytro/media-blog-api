import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
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

public class RetrieveAllMessagesForUserTest {
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
     * Sending an http request to GET localhost:8080/accounts/1/messages (messages exist for user)
     * <p>
     * Expected Response:
     * Status Code: 200
     * Response Body: JSON representation of a list of messages
     */
    @Test
    public void getAllMessagesFromUserMessageExists() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/accounts/1/messages"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(200, status);

        List<Message> expectedResult = new ArrayList<>();
        expectedResult.add(new Message(1, 1, "test message 1", 1669947792));
        List<Message> actualResult = objectMapper.readValue(response.body(), new TypeReference<>() {
        });
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Sending an http request to GET localhost:8080/accounts/1/messages (messages does NOT exist for user)
     * <p>
     * Expected Response:
     * Status Code: 200
     * Response Body:
     */
    @Test
    public void getAllMessagesFromUserNoMessagesFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/accounts/2/messages"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(200, status);

        List<Message> actualResult = objectMapper.readValue(response.body(), new TypeReference<>() {
        });
        assertTrue(actualResult.isEmpty());
    }

}
