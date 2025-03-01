package mtcg.repository;

import mtcg.model.Card;
import mtcg.service.PackageService.CardDefinition;
import java.util.ArrayList;
import java.util.List;

public class PackageRepository {
    private static List<List<Card>> packages = new ArrayList<>();
    private List<Card> currentPackage = new ArrayList<>();

    public void addPackageCard(CardDefinition cd) {
        // Erzeuge eine Karte ohne Besitzer; setze default Element "Normal"
        Card card = new Card(0, 0, cd.getName(), "Normal", (int) cd.getDamage());
        currentPackage.add(card);
        if (currentPackage.size() == 5) {
            packages.add(new ArrayList<>(currentPackage));
            currentPackage.clear();
        }
    }

    public List<Card> acquirePackage() {
        if (packages.isEmpty()) return null;
        return packages.remove(0);
    }
}
