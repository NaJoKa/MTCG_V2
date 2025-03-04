package mtcg.service;

import mtcg.repository.UserRepository;
import mtcg.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
public class UserService {
    private UserRepository userRepo = new UserRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public boolean registerUserFromJson(String json) {
        try {
            RegistrationData data = mapper.readValue(json, RegistrationData.class);
            System.out.println("UserReservice: Registrierung: " + data.getUsername());
            if (userRepo.userExists(data.getUsername()))
                return false;
            User user = new User(data.getUsername(), data.getPassword(), 20, 100);
            userRepo.createUser(user);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loginUserAndGetToken(String json) {
        try {
            RegistrationData data = mapper.readValue(json, RegistrationData.class);
            System.out.println("UserService: Login-Versuch: " + data.getUsername());
            if (userRepo.validateUser(data.getUsername(), data.getPassword()))
                return data.getUsername() + "-mtcgToken";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserProfile(String username) {
        try {
            User user = userRepo.getUser(username);
            if (user == null) return null;
            return mapper.writeValueAsString(user);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserProfile(String username, String json) {
        try {
            User user = userRepo.getUser(username);
            if (user == null) return false;
            // Für dieses Beispiel wird nur das Passwort aktualisiert
            ProfileData data = mapper.readValue(json, ProfileData.class);
            System.out.println("UserService: Profil wird aktualsiert für: " + user.getUsername());
            if (data.getPassword() != null && !data.getPassword().isEmpty())
                user.setPassword(data.getPassword());
            userRepo.updateUser(user);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getStats(String username) {
        try {
            User user = userRepo.getUser(username);
            if (user == null)
                return "{\"message\":\"User not found\"}";
            return "{\"coins\":" + user.getCoins() + ", \"elo\":" + user.getElo() + "}";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "{\"message\":\"Error\"}";
    }

    public String getScoreboardJson() {
        try {
            return mapper.writeValueAsString(userRepo.getScoreboard());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }

    // Hilfsklassen für JSON-Mapping
    public static class RegistrationData {
        @JsonProperty("Username")
        private String Username;
        @JsonProperty("Password")
        private String Password;
        public String getUsername() { return Username; }
        public void setUsername(String username) { this.Username = username; }
        public String getPassword() { return Password; }
        public void setPassword(String password) { this.Password = password; }
    }

    public static class ProfileData {
        private String Password;
        public String getPassword() { return Password; } public void setPassword(String password) { this.Password = password; }
    }
}
