package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.UserService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ScoreboardController {
    private UserService userService = new UserService();

    public ScoreboardController(HttpServer server) {
        System.out.println("Registrierung endpunkt /scoreboard");
        server.createContext("/scoreboard", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                }
            }
        );
    }
}
