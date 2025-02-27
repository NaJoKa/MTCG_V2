package mtcg.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import mtcg.controller.UserController;
import mtcg.controller.SessionController;
import mtcg.controller.PackageController;
import mtcg.controller.TransactionController;
import mtcg.controller.CardController;
import mtcg.controller.DeckController;
import mtcg.controller.StatsController;
import mtcg.controller.ScoreboardController;
import mtcg.controller.BattleController;
import controller.TradingController;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);

        new UserController(server);
        new SessionController(server);
        new PackageController(server);
        new TransactionController(server);
        new CardController(server);
        new DeckController(server);
        new StatsController(server);
        new ScoreboardController(server);
        new BattleController(server);
        new TradingController(server);

        server.setExecutor(null);
        server.start();
        System.out.println("Server läuft auf Port 10001...");
    }
}
