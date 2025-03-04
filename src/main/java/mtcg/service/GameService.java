package mtcg.service;

import mtcg.model.Card;
import mtcg.model.User;
import mtcg.repository.UserRepository;
import mtcg.repository.CardRepository;
import mtcg.repository.DeckRepository;
import mtcg.repository.BattleRepository;
import mtcg.repository.TradeRepository;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class GameService {
    private UserRepository userRepo = new UserRepository();
    private CardRepository cardRepo = new CardRepository();
    private DeckRepository deckRepo = new DeckRepository();
    private BattleRepository battleRepo = new BattleRepository();
    // Für diese einfache Implementierung wird Trading über TradingService abgewickelt
    // Battle-Logik:
    public String startBattle(String username, String opponentUsername) {
        User user1 = userRepo.getUser(username);
        User user2 = userRepo.getUser(opponentUsername);
        if (user1 == null || user2 == null) {
            System.out.println("GameService: Einer der User existiert nicht.");
            return "Einer der User existiert nicht.";
        }
        List<Card> deck1 = deckRepo.getDeck(user1.getId());
        List<Card> deck2 = deckRepo.getDeck(user2.getId());
        if (deck1.size() != 4 || deck2.size() != 4){
            System.out.println("GameService: Beide Spieler müssen ein Deck mit 4 Karten haben.");
            return "Beide Spieler müssen ein Deck mit 4 Karten haben.";
        }
        System.out.println("GameService: Battle startet zwischen " + username + " und " + opponentUsername);
        StringBuilder log = new StringBuilder();
        int rounds = 0;
        while (!deck1.isEmpty() && !deck2.isEmpty() && rounds < 100) {
            rounds++;
            Card card1 = deck1.remove((int)(Math.random() * deck1.size()));
            Card card2 = deck2.remove((int)(Math.random() * deck2.size()));
            System.out.println("Runde " + rounds + ": " + card1.getName() + " vs. " + card2.getName());
            // Unique Feature: Sphinx gewinnt automatisch, falls sie gespielt wird und der Gegner nicht ebenfalls Sphinx
            if (card1.getName().equalsIgnoreCase("Sphinx") && !card2.getName().equalsIgnoreCase("Sphinx")) {
                log.append("Round ").append(rounds).append(": ").append(card1.getName()).append(" (Sphinx) gewinnt automatisch gegen ").append(card2.getName()).append("\n");
                deck1.add(card2);
            } else if (card2.getName().equalsIgnoreCase("Sphinx") && !card1.getName().equalsIgnoreCase("Sphinx")) {
                log.append("Round ").append(rounds).append(": ").append(card2.getName()).append(" (Sphinx) gewinnt automatisch gegen ").append(card1.getName()).append("\n");
                deck2.add(card1);
            } else {
                int dmg1 = calculateDamage(card1, card2);
                int dmg2 = calculateDamage(card2, card1);
                if (dmg1 > dmg2) {
                    log.append("Round ").append(rounds).append(": ").append(card1.getName()).append(" gewinnt gegen ").append(card2.getName()).append("\n");
                    deck1.add(card2);
                } else if (dmg2 > dmg1) {
                    log.append("Round ").append(rounds).append(": ").append(card2.getName()).append(" gewinnt gegen ").append(card1.getName()).append("\n");
                    deck2.add(card1);
                } else {
                    log.append("Round ").append(rounds).append(": Unentschieden zwischen ").append(card1.getName()).append(" und ").append(card2.getName()).append("\n");
                }
            }
        }
        String winner;
        if (deck1.size() > deck2.size()) {
            winner = user1.getUsername();
            user1.setElo(user1.getElo() + 3);
            user2.setElo(user2.getElo() - 5);
        } else if (deck2.size() > deck1.size()) {
            winner = user2.getUsername();
            user2.setElo(user2.getElo() + 3);
            user1.setElo(user1.getElo() - 5);
        } else {
            winner = "Unentschieden";
        }
        battleRepo.saveBattle(user1.getId(), user2.getId(), winner);
        userRepo.updateUser(user1);
        userRepo.updateUser(user2);
        log.append("Gewinner: ").append(winner);
        System.out.println("Battle beendet. Gewinner: " + winner);
        return log.toString();
    }

    public int calculateDamage(Card attacker, Card defender) {
        int baseDamage = attacker.getDamage();
        if (attacker.getElementType().equalsIgnoreCase("Water") && defender.getElementType().equalsIgnoreCase("Fire"))
            return baseDamage * 2;
        else if (attacker.getElementType().equalsIgnoreCase("Fire") && defender.getElementType().equalsIgnoreCase("Water"))
            return baseDamage / 2;
        return baseDamage;
    }

        // Zeigt alle Karten (Stack) eines Users an
        public List<Card> showStack(String username) {
            User user = userRepo.getUser(username);
            if (user == null) {
                return new ArrayList<>(); // Leere Liste, falls der User nicht existiert
            }
            return cardRepo.getCardsByUserId(user.getId());
        }

    }
