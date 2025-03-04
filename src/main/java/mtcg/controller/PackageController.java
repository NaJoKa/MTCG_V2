package mtcg.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mtcg.service.PackageService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PackageController {
    private PackageService packageService = new PackageService();

    public PackageController(HttpServer server) {
        System.out.println("Registrierung Endpunkt /packages");
        server.createContext("/packages", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    // Nur Admin darf Packages erstellen
                    String auth = exchange.getRequestHeaders().getFirst("Authorization");
                    if (auth == null || !auth.equals("Bearer admin-mtcgToken")) {
                        String response = "{\"message\":\"Unauthorized\"}";
                        exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                        os.close();
                        return;
                    }
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("PackageController: Package erstellen " + body);
                    boolean success = packageService.createPackage(body);
                    String response = success ? "{\"message\":\"Package created\"}" : "{\"message\":\"Package creation failed\"}";
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
