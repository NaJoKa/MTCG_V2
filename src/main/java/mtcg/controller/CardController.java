package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.CardService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CardController {
    private CardService cardService = new CardService();

    public CardController(HttpServer server) {
        System.out.println("Registrierung Endpunkt /cards");
        server.createContext("/cards", new HttpHandler() {
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
                String username = auth.substring(7, auth.indexOf("-mtcgToken"));
                System.out.println("CardController: " + username + " fragt Karten ab");
                String response = cardService.getCardsJson(username);
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        });
    }
}
