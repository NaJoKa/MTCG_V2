package mtcg.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import mtcg.service.GameService;
import mtcg.service.UserService;
import mtcg.service.PackageService;
import mtcg.service.DeckService;
import mtcg.service.TradingService;
import mtcg.model.Card;
import mtcg.repository.UserRepository;
import mtcg.repository.CardRepository;
import mtcg.repository.DeckRepository;

import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTest {
    private GameService gameService;
    private UserService userService;
    private PackageService packageService;
    private CardRepository cardRepo;
    private UserRepository userRepo;
    private DeckRepository deckRepo;
    private DeckService deckService;
    private TradingService tradingService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        userService = new UserService();
        packageService = new PackageService();
        cardRepo = new CardRepository();
        userRepo = new UserRepository();
        deckRepo = new DeckRepository();
        deckService = new DeckService();
        tradingService = new TradingService();
    }

    @Test
    @Order(1)
    void testBuyPackageSuccess() {
        userService.registerUserFromJson("{\"Username\":\"buyer1\", \"Password\":\"pass\"}");
        assertTrue(packageService.acquirePackage("buyer1") == false); // Da noch kein Package vorhanden – Test für Moneysituation
    }

    @Test
    @Order(2)
    void testCreatePackage() {
        // Admin erstellt ein Package
        assertTrue(packageService.createPackage("[{\"Id\":\"1\",\"Name\":\"WaterGoblin\",\"Damage\":10.0}," +
                "{\"Id\":\"2\",\"Name\":\"Dragon\",\"Damage\":50.0}," +
                "{\"Id\":\"3\",\"Name\":\"WaterSpell\",\"Damage\":20.0}," +
                "{\"Id\":\"4\",\"Name\":\"Ork\",\"Damage\":45.0}," +
                "{\"Id\":\"5\",\"Name\":\"FireSpell\",\"Damage\":25.0}]"));
    }

    @Test
    @Order(3)
    void testAcquirePackageSuccess() {
        // Admin Package erstellen und dann von buyer2 erwerben
        userService.registerUserFromJson("{\"Username\":\"buyer2\", \"Password\":\"pass\"}");
        // Stelle sicher, dass buyer2 genug Coins hat (20 Coins Startwert)
        assertTrue(packageService.acquirePackage("buyer2"));
    }

    @Test
    @Order(4)
    void testShowStack() {
        List<Card> stack = gameService.showStack("buyer2");
        assertNotNull(stack);
    }

    @Test
    @Order(5)
    void testConfigureDeckSuccess() {
        List<Card> stack = gameService.showStack("buyer2");
        assertTrue(stack.size() >= 4);
        int[] ids = new int[4];
        for (int i = 0; i < 4; i++) {
            ids[i] = stack.get(i).getId();
        }
        String json = "[" + ids[0] + "," + ids[1] + "," + ids[2] + "," + ids[3] + "]";
        assertTrue(deckService.configureDeck("buyer2", json));
    }

    @Test
    @Order(6)
    void testConfigureDeckFailureWrongNumber() {
        List<Card> stack = gameService.showStack("buyer2");
        if (stack.size() < 3) fail("Nicht genügend Karten");
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            ids[i] = stack.get(i).getId();
        }
        String json = "[" + ids[0] + "," + ids[1] + "," + ids[2] + "]";
        assertFalse(deckService.configureDeck("buyer2", json));
    }

    @Test
    @Order(7)
    void testBattleNormal() {
        userService.registerUserFromJson("{\"Username\":\"battle1\", \"Password\":\"p1\"}");
        userService.registerUserFromJson("{\"Username\":\"battle2\", \"Password\":\"p2\"}");
        packageService.createPackage("[{\"Id\":\"6\",\"Name\":\"Goblin\",\"Damage\":20.0}," +
                "{\"Id\":\"7\",\"Name\":\"Ork\",\"Damage\":25.0}," +
                "{\"Id\":\"8\",\"Name\":\"Knight\",\"Damage\":30.0}," +
                "{\"Id\":\"9\",\"Name\":\"Dragon\",\"Damage\":40.0}," +
                "{\"Id\":\"10\",\"Name\":\"Wizard\",\"Damage\":35.0}]");
        packageService.acquirePackage("battle1");
        packageService.acquirePackage("battle2");
        // Konfiguriere jeweils ein Deck (hier wird vereinfacht, indem die ersten 4 Karten genommen werden)
        List<Card> stack1 = gameService.showStack("battle1");
        List<Card> stack2 = gameService.showStack("battle2");
        if (stack1.size() < 4 || stack2.size() < 4) fail("Nicht genügend Karten");
        int[] deck1 = new int[4], deck2 = new int[4];
        for (int i = 0; i < 4; i++) {
            deck1[i] = stack1.get(i).getId();
            deck2[i] = stack2.get(i).getId();
        }
        String json1 = "[" + deck1[0] + "," + deck1[1] + "," + deck1[2] + "," + deck1[3] + "]";
        String json2 = "[" + deck2[0] + "," + deck2[1] + "," + deck2[2] + "," + deck2[3] + "]";
        assertTrue(deckService.configureDeck("battle1", json1));
        assertTrue(deckService.configureDeck("battle2", json2));
        String log = gameService.startBattle("battle1", "battle2");
        assertTrue(log.contains("Round"));
    }

    @Test
    @Order(8)
    void testBattleSphinxAutomaticWin() {
        userService.registerUserFromJson("{\"Username\":\"sphinx1\", \"Password\":\"p\"}");
        userService.registerUserFromJson("{\"Username\":\"sphinx2\", \"Password\":\"p\"}");
        int id1 = userRepo.getUser("sphinx1").getId();
        int id2 = userRepo.getUser("sphinx2").getId();
        // Für sphinx1: Sphinx + 3 andere Karten
        cardRepo.addCard(new Card(0, id1, "Sphinx", "Normal", 30));
        for (int i = 0; i < 3; i++)
            cardRepo.addCard(new Card(0, id1, "Goblin", "Fire", 20));
        // Für sphinx2: 4 normale Karten
        for (int i = 0; i < 4; i++)
            cardRepo.addCard(new Card(0, id2, "Ork", "Water", 25));
        List<Card> deck1 = cardRepo.getCardsByUserId(id1);
        List<Card> deck2 = cardRepo.getCardsByUserId(id2);
        int[] d1 = new int[4], d2 = new int[4];
        for (int i = 0; i < 4; i++) {
            d1[i] = deck1.get(i).getId();
            d2[i] = deck2.get(i).getId();
        }
        String json1 = "[" + d1[0] + "," + d1[1] + "," + d1[2] + "," + d1[3] + "]";
        String json2 = "[" + d2[0] + "," + d2[1] + "," + d2[2] + "," + d2[3] + "]";
        deckRepo.setDeck(id1, Arrays.asList(d1[0], d1[1], d1[2], d1[3]));
        deckRepo.setDeck(id2, Arrays.asList(d2[0], d2[1], d2[2], d2[3]));
        String log = gameService.startBattle("sphinx1", "sphinx2");
        assertTrue(log.contains("Sphinx"));
        assertTrue(log.contains("Gewinner"));
    }

    @Test
    @Order(9)
    void testCreateTradeSuccess() {
        userService.registerUserFromJson("{\"Username\":\"trader1\", \"Password\":\"p\"}");
        int uid = userRepo.getUser("trader1").getId();
        Card card = new Card(0, uid, "Dragon", "Fire", 40);
        cardRepo.addCard(card);
        assertTrue(tradingService.createTradingDeal("trader1", "{\"Id\":\"1\",\"CardToTrade\":\"" + card.getId() + "\",\"Type\":\"monster\",\"MinimumDamage\":30}"));
    }

    @Test
    @Order(10)
    void testShowScoreboard() {
        List<String> sb = userRepo.getScoreboard();
        assertNotNull(sb);
        assertTrue(sb.size() > 0);
    }

    @Test
    @Order(11)
    void testEditProfileSuccess() {
        userService.registerUserFromJson("{\"Username\":\"profile1\", \"Password\":\"old\"}");
        assertTrue(userService.updateUserProfile("profile1", "{\"Password\":\"new\"}"));
    }

    @Test
    @Order(12)
    void testEditProfileFailure() {
        assertFalse(userService.updateUserProfile("nonexistent", "{\"Password\":\"nop\"}"));
    }

    @Test
    @Order(13)
    void testCalculateDamageNormal() {
        Card c1 = new Card(1, 1, "Goblin", "Normal", 30);
        Card c2 = new Card(2, 2, "Ork", "Normal", 20);
        int dmg = gameService.calculateDamage(c1, c2);
        assertEquals(30, dmg);
    }

    @Test
    @Order(14)
    void testCalculateDamageWaterVsFire() {
        Card c1 = new Card(1, 1, "Mermaid", "Water", 30);
        Card c2 = new Card(2, 2, "Dragon", "Fire", 30);
        int dmg = gameService.calculateDamage(c1, c2);
        assertEquals(60, dmg);
    }

    @Test
    @Order(15)
    void testCalculateDamageFireVsWater() {
        Card c1 = new Card(1, 1, "Dragon", "Fire", 30);
        Card c2 = new Card(2, 2, "Mermaid", "Water", 30);
        int dmg = gameService.calculateDamage(c1, c2);
        assertEquals(15, dmg);
    }

    @Test
    @Order(16)
    void testBattleRoundsLimit() {
        userService.registerUserFromJson("{\"Username\":\"limit1\", \"Password\":\"p\"}");
        userService.registerUserFromJson("{\"Username\":\"limit2\", \"Password\":\"p\"}");
        packageService.createPackage("[{\"Id\":\"11\",\"Name\":\"Goblin\",\"Damage\":20.0}," +
                "{\"Id\":\"12\",\"Name\":\"Ork\",\"Damage\":25.0}," +
                "{\"Id\":\"13\",\"Name\":\"Knight\",\"Damage\":30.0}," +
                "{\"Id\":\"14\",\"Name\":\"Dragon\",\"Damage\":40.0}," +
                "{\"Id\":\"15\",\"Name\":\"Wizard\",\"Damage\":35.0}]");
        packageService.acquirePackage("limit1");
        packageService.acquirePackage("limit2");
        List<Card> s1 = gameService.showStack("limit1");
        List<Card> s2 = gameService.showStack("limit2");
        int[] d1 = new int[4], d2 = new int[4];
        for (int i = 0; i < 4; i++) {
            d1[i] = s1.get(i).getId();
            d2[i] = s2.get(i).getId();
        }
        deckRepo.setDeck(userRepo.getUser("limit1").getId(), Arrays.asList(d1[0], d1[1], d1[2], d1[3]));
        deckRepo.setDeck(userRepo.getUser("limit2").getId(), Arrays.asList(d2[0], d2[1], d2[2], d2[3]));
        String log = gameService.startBattle("limit1", "limit2");
        assertTrue(log.contains("Round") || log.contains("Gewinner"));
    }
}
