package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.PackageService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TransactionController {
    private PackageService packageService = new PackageService();

    public TransactionController(HttpServer server) {
        server.createContext("/transactions/packages", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    String auth = exchange.getRequestHeaders().getFirst("Authorization");
                    if (auth == null || !auth.endsWith("-mtcgToken")) {
                        String response = "{\"message\":\"Unauthorized\"}";
                        exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                        os.close();
                        return;
                    }
                    String token = auth.substring(7, auth.indexOf("-mtcgToken"));
                    boolean success = packageService.acquirePackage(token);
                    String response = success ? "{\"message\":\"Package acquired\"}" : "{\"message\":\"Not enough money or no package available\"}";
                    int code = success ? 201 : 400;
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
