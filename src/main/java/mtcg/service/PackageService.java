package mtcg.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import mtcg.repository.PackageRepository;
import mtcg.repository.UserRepository;
import mtcg.repository.CardRepository;
import mtcg.model.Card;
import mtcg.model.User;
import java.util.List;

public class PackageService {
    private PackageRepository packageRepo = new PackageRepository();
    private UserRepository userRepo = new UserRepository();
    private CardRepository cardRepo = new CardRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public boolean createPackage(String json) {
        try {
            CardDefinition[] cards = mapper.readValue(json, CardDefinition[].class);
            System.out.println("PackageService: Erstelle Package mit " + cards.length + " Karten.");
            for (CardDefinition cd : cards) {
                packageRepo.addPackageCard(cd);
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean acquirePackage(String username) {
        User user = userRepo.getUser(username);
        if (user == null || user.getCoins() < 5) {
            System.out.println("PackageService: Erwerb fehlgeschlagen: User nicht vorhanden oder nicht genügend Coins.");
            return false;
        }
        List<Card> pkg = packageRepo.acquirePackage();
        if (pkg == null || pkg.isEmpty()) {
            System.out.println("PackageService: Keine Packages verfügbar.");
            return false;
        }
        user.setCoins(user.getCoins() - 5);
        userRepo.updateUser(user);
        for (Card card : pkg) {
            card.setUserId(user.getId());
            cardRepo.addCard(card);
        }
        System.out.println("PackageService: Package von " + username + " erfolgreich erworben.");
        return true;
    }

    public static class CardDefinition {
        @JsonProperty("Id")
        private String id;
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Damage")
        private double damage;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getDamage() { return damage; }
        public void setDamage(double damage) { this.damage = damage; }
    }
}
