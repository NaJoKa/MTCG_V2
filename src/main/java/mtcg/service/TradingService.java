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
            var user = userRepo.getUser(username);
            if (user == null)
                return false;
            // Annahme: CardToTrade ist eine Zahl als String
            CardService dummy = new CardService();
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
        // Hier soll verhindert werden, dass man mit sich selbst handelt
        return tradeRepo.executeTrade(tid, user.getId(), offerCardId);
    }

    public static class TradeDeal {
        private String Id;
        private String CardToTrade;
        private String Type;
        private int MinimumDamage;
        public String getId() { return Id; } public void setId(String id) { this.Id = id; }
        public String getCardToTrade() { return CardToTrade; } public void setCardToTrade(String cardToTrade) { this.CardToTrade = cardToTrade; }
        public String getType() { return Type; } public void setType(String type) { this.Type = type; }
        public int getMinimumDamage() { return MinimumDamage; } public void setMinimumDamage(int minimumDamage) { this.MinimumDamage = minimumDamage; }
    }
}
