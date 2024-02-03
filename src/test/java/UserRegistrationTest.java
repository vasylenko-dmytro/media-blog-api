import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import controller.SocialMediaController;
import model.Account;
import util.ConnectionUtil;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRegistrationTest {
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
     * Sending an http request to POST localhost:8080/register
     * when username does not exist in the system
     * <p>
     * Expected Response:
     * Status Code: 200
     * Response Body: JSON representation of user object
     */
    @Test
    public void registerUserSuccessful() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"user\", " +
                        "\"password\": \"password\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        assertEquals(200, status);
        Account expectedAccount = new Account(2, "user", "password");
        Account actualAccount = objectMapper.readValue(response.body(), Account.class);
        assertEquals(expectedAccount, actualAccount);

    }


    /**
     * Sending an http request to POST localhost:8080/register when username already exists in system
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void registerUserDuplicateUsername() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"user\", " +
                        "\"password\": \"password\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response1 = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status1 = response1.statusCode();
        int status2 = response2.statusCode();
        assertEquals(200, status1);
        assertEquals(400, status2);
        assertEquals("", response2.body());

    }

    /**
     * Sending an http request to POST localhost:8080/register when no username provided
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void registerUserUsernameBlank() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"\", " +
                        "\"password\": \"password\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        assertEquals(400, status);
        assertEquals("", response.body());

    }


    /**
     * Sending an http request to POST localhost:8080/register
     * when no password is less than 4 characters
     * <p>
     * Expected Response:
     * Status Code: 400
     * Response Body:
     */
    @Test
    public void registerUserPasswordLengthLessThanFour() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"username\", " +
                        "\"password\": \"pas\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        assertEquals(400, status);
        assertEquals("", response.body());
    }

}
