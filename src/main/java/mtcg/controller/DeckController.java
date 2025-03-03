package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.DeckService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URI;

public class DeckController {
    private DeckService deckService = new DeckService();

    public DeckController(HttpServer server) {
        server.createContext("/deck", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String auth = exchange.getRequestHeaders().getFirst("Authorization");
                if (auth == null || !auth.endsWith("-mtcgToken")) {
                    String response = "{\"message\":\"Unauthorized\"}";
                    exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                    return;
                }
                String username = auth.substring(7, auth.indexOf("-mtcgToken"));
                if ("GET".equalsIgnoreCase(method)) {
                    URI uri = exchange.getRequestURI();
                    String query = uri.getQuery();
                    boolean plain = query != null && query.contains("format=plain");
                    String response = deckService.getDeckJson(username, plain);
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else if ("PUT".equalsIgnoreCase(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    boolean success = deckService.configureDeck(username, body);
                    String response = success ? "{\"message\":\"Deck configured\"}" : "{\"message\":\"Deck configuration failed\"}";
                    int code = success ? 200 : 400;
                    exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
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
