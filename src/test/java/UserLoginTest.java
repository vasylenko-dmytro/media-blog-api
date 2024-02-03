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

public class UserLoginTest {

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
     * Sending an http request to POST localhost:8080/login with valid username and password
     * <p>
     * Expected Response:
     * Status Code: 200
     * Response Body: JSON representation of user object
     */
    @Test
    public void loginSuccessful() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"testuser1\", " +
                        "\"password\": \"password\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(200, status);
        ObjectMapper om = new ObjectMapper();
        Account expectedResult = new Account(1, "testuser1", "password");
        Account actualResult = om.readValue(response.body(), Account.class);
        assertEquals(expectedResult, actualResult);

    }

    /**
     * Sending an http request to POST localhost:8080/login with invalid username
     * <p>
     * Expected Response:
     * Status Code: 401
     * Response Body:
     */
    @Test
    public void loginInvalidUsername() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"testuser404\", " +
                        "\"password\": \"password\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(401, status);
        assertEquals("", response.body());

    }

    /**
     * Sending an http request to POST localhost:8080/login with invalid password
     * <p>
     * Expected Response:
     * Status Code: 401
     * Response Body:
     */
    @Test
    public void loginInvalidPassword() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString("{" +
                        "\"username\": \"testuser1\", " +
                        "\"password\": \"pass123\" }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        assertEquals(401, status);
        assertEquals("", response.body());
    }

}
