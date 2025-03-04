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
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }
                String auth = exchange.getRequestHeaders().getFirst("Authorization");
                if (auth == null || !auth.endsWith("-mtcgToken")) {
                    String response = "{\"message\":\"Unauthorized\"}";
                    exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                    return;
                }
                System.out.println("ScoreboardController: Scoreboard wird abeefragt");
                String response = userService.getScoreboardJson();
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        });
    }
}
