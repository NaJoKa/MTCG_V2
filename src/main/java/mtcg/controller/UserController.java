package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.UserService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URI;

public class UserController {
    private UserService userService = new UserService();

    public UserController(HttpServer server) {
        server.createContext("/users", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                // Registrierung: POST /users
                if ("POST".equalsIgnoreCase(method) && path.equals("/users")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    boolean success = userService.registerUserFromJson(body);
                    String response = success ? "{\"message\":\"Success\"}" : "{\"message\":\"User already exists\"}";
                    int code = success ? 201 : 400;
                    exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else if (path.startsWith("/users/")) {
                    // Profil anzeigen (GET) und bearbeiten (PUT)
                    String username = path.substring("/users/".length());
                    if ("GET".equalsIgnoreCase(method)) {
                        String response = userService.getUserProfile(username);
                        if (response == null) {
                            response = "{\"message\":\"User not found\"}";
                            exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
                        } else {
                            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        }
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                        os.close();
                    } else if ("PUT".equalsIgnoreCase(method)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        boolean success = userService.updateUserProfile(username, body);
                        String response = success ? "{\"message\":\"Profile updated\"}" : "{\"message\":\"Update failed\"}";
                        int code = success ? 200 : 400;
                        exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                        os.close();
                    } else {
                        exchange.sendResponseHeaders(405, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
        });
    }
}
