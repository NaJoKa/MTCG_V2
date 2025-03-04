package mtcg.service;

import mtcg.repository.CardRepository;
import mtcg.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardService {
    private CardRepository cardRepo = new CardRepository();
    private UserRepository userRepo = new UserRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public String getCardsJson(String username) {
        int userId = userRepo.getUser(username).getId();
        System.out.println("CardService: Hole Karten für UserID: " + userId);
        try {
            return mapper.writeValueAsString(cardRepo.getCardsByUserId(userId));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }
}