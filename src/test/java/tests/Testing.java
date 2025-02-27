package tests;

import org.junit.jupiter.api.Test;
import mctgserver.service.UserService;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private final UserService userService = new UserService();

    @Test
    void testRegisterUser() {
        String response = userService.registerUser("testuser", "testpass");
        assertTrue(response.contains("Success") || response.contains("User already exists"));
    }
}
