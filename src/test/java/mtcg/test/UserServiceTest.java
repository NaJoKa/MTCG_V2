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
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
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
    void testRegisterUserSuccess() {
        assertTrue(userService.registerUserFromJson("{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"));
    }

    @Test
    @Order(2)
    void testRegisterUserDuplicate() {
        userService.registerUserFromJson("{\"Username\":\"altenhof\", \"Password\":\"markus\"}");
        assertFalse(userService.registerUserFromJson("{\"Username\":\"altenhof\", \"Password\":\"markus\"}"));
    }

    @Test
    @Order(3)
    void testLoginUserSuccess() {
        userService.registerUserFromJson("{\"Username\":\"admin\", \"Password\":\"istrator\"}");
        assertNotNull(userService.loginUserAndGetToken("{\"Username\":\"admin\", \"Password\":\"istrator\"}"));
    }

    @Test
    @Order(4)
    void testLoginUserFailure() {
        assertNull(userService.loginUserAndGetToken("{\"Username\":\"kienboec\", \"Password\":\"wrong\"}"));
    }

}