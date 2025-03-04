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
import mtcg.controller.TradingController;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);
        System.out.println("Server instanziert");
        new UserController(server);
        System.out.println("UserController neue Instanz");
        new SessionController(server);
        System.out.println("SessionController neue Instanz");
        new PackageController(server);
        System.out.println("PackageController neue Instanz");
        new TransactionController(server);
        System.out.println("TransactionController neue Instanz");
        new CardController(server);
        System.out.println("CardController neue Instanz");
        new DeckController(server);
        System.out.println("DeckController neue Instanz");
        new StatsController(server);
        System.out.println("StatsController neue Instanz");
        new ScoreboardController(server);
        System.out.println("ScoreboardController instanziert");
        new BattleController(server);
        System.out.println("BattleController instanziert");
        new TradingController(server);
        System.out.println("TradingController instanziert");

        server.setExecutor(null);
        server.start();
        System.out.println("Server läuft auf Port 10001...");
    }
}
