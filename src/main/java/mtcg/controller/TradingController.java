package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.TradingService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URI;

public class TradingController {
    private TradingService tradingService = new TradingService();

    public TradingController(HttpServer server) {
        server.createContext("/tradings", new HttpHandler() {
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
                    String response = tradingService.getTradingsJson(username);
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    URI uri = exchange.getRequestURI();
                    String path = uri.getPath();
                    if (path.equals("/tradings")) {
                        boolean success = tradingService.createTradingDeal(username, body);
                        String response = success ? "{\"message\":\"Trade created\"}" : "{\"message\":\"Trade creation failed\"}";
                        int code = success ? 201 : 400;
                        exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                        os.close();
                    } else {
                        // Wenn ein Trade ausgeführt werden soll: POST /tradings/{id} mit offeredCardId als Body
                        String[] parts = path.split("/");
                        if (parts.length == 3) {
                            String tradeId = parts[2];
                            String offeredCardId = body.replaceAll("\"", "").trim();
                            boolean success = tradingService.executeTrade(username, tradeId, offeredCardId);
                            String response = success ? "{\"message\":\"Trade successful\"}" : "{\"message\":\"Trade failed\"}";
                            int code = success ? 200 : 400;
                            exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            os.close();
                        } else {
                            exchange.sendResponseHeaders(404, -1);
                        }
                    }
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    String[] parts = exchange.getRequestURI().getPath().split("/");
                    if (parts.length == 3) {
                        String tradeId = parts[2];
                        boolean success = tradingService.deleteTradingDeal(username, tradeId);
                        String response = success ? "{\"message\":\"Trade deleted\"}" : "{\"message\":\"Trade deletion failed\"}";
                        int code = success ? 200 : 400;
                        exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                        os.close();
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        });
    }
}
