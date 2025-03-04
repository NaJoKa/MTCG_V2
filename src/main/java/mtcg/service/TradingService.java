package mtcg.service;

import mtcg.repository.TradeRepository;
import mtcg.repository.UserRepository;
import mtcg.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;

public class TradingService {
    private TradeRepository tradeRepo = new TradeRepository();
    private UserRepository userRepo = new UserRepository();
    private CardRepository cardRepo = new CardRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public boolean createTradingDeal(String username, String json) {
        try {
            TradeDeal deal = mapper.readValue(json, TradeDeal.class);
            System.out.println("TradingService: Erstelle Handelsangebot für " + username + ": " + json);
            var user = userRepo.getUser(username);
            if (user == null)
                return false;
            // Hier wird angenommen, dass CardToTrade eine Zahl als String ist
            var card = cardRepo.getCardById(Integer.parseInt(deal.getCardToTrade()));
            if (card == null || card.getUserId() != user.getId())
                return false;
            return tradeRepo.createTrade(user.getId(), card.getId(), deal.getMinimumDamage());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getTradingsJson(String username) {
        try {
            List<String> deals = tradeRepo.getAllTrades();
            return mapper.writeValueAsString(deals);
        } catch(Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public boolean deleteTradingDeal(String username, String tradeId) {
        try {
            int tid = Integer.parseInt(tradeId);
            return tradeRepo.deleteTrade(tid, userRepo.getUser(username).getId());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeTrade(String username, String tradeId, String offeredCardId) {
        var user = userRepo.getUser(username);
        if (user == null)
            return false;
        int tid = Integer.parseInt(tradeId);
        int offerCardId = Integer.parseInt(offeredCardId);
        System.out.println("TradingService: " + username + " führt Trade " + tradeId + " mit angebotener Karte " + offerCardId + " aus.");
        return tradeRepo.executeTrade(tid, user.getId(), offerCardId);
    }

    public static class TradeDeal {
        private String id;
        private String cardToTrade;
        private String type;
        private int minimumDamage;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
        public String getCardToTrade() { return cardToTrade; } public void setCardToTrade(String cardToTrade) { this.cardToTrade = cardToTrade; }
        public String getType() { return type; } public void setType(String type) { this.type = type; }
        public int getMinimumDamage() { return minimumDamage; } public void setMinimumDamage(int minimumDamage) { this.minimumDamage = minimumDamage; }
    }
}