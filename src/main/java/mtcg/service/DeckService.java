package mtcg.service;

import mtcg.repository.DeckRepository;
import mtcg.repository.CardRepository;
import mtcg.repository.UserRepository;
import mtcg.model.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;

public class DeckService {
    private DeckRepository deckRepo = new DeckRepository();
    private CardRepository cardRepo = new CardRepository();
    private UserRepository userRepo = new UserRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public boolean configureDeck(String username, String json) {
        try {
            int[] cardIds = mapper.readValue(json, int[].class);
            if (cardIds.length != 4)
                return false;
            int userId = userRepo.getUser(username).getId();
            List<Card> cards = cardRepo.getCardsByUserId(userId);
            for (int id : cardIds) {
                boolean found = false;
                for (Card c : cards) {
                    if (c.getId() == id) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    return false;
            }
            List<Integer> ids = new ArrayList<>();
            for (int id : cardIds) ids.add(id);
            deckRepo.setDeck(userId, ids);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getDeckJson(String username, boolean plain) {
        int userId = userRepo.getUser(username).getId();
        List<Card> deck = deckRepo.getDeck(userId);
        try {
            if (plain) {
                StringBuilder sb = new StringBuilder();
                for (Card c : deck) {
                    sb.append("ID: ").append(c.getId())
                            .append(", Name: ").append(c.getName())
                            .append(", Element: ").append(c.getElementType())
                            .append(", Damage: ").append(c.getDamage()).append("\n");
                }
                return sb.toString();
            } else {
                return mapper.writeValueAsString(deck);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }
}
