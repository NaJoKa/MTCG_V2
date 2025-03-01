package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.UserService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SessionController {
    private UserService userService = new UserService();

    public SessionController(HttpServer server) {
        server.createContext("/sessions", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    String token = userService.loginUserAndGetToken(body);
                    String response;
                    if (token != null) {
                        response = "{\"message\":\"" + token + "\"}";
                        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    } else {
                        response = "{\"message\":\"Login failed\"}";
                        exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                    }
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        });
    }
}
