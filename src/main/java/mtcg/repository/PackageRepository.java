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
        System.out.println("PackageRepository: Karte " + cd.getName() + " zum aktuellen Package hinzugefügt.");
        if (currentPackage.size() == 5) {
            packages.add(new ArrayList<>(currentPackage));
            System.out.println("PackageRepository: Ein Package wurde erstellt.");
            currentPackage.clear();
        }
    }

    public List<Card> acquirePackage() {
        if (packages.isEmpty()) {
            System.out.println("PackageRepository: Keine Packages vorhanden.");
            return null;
        }
        System.out.println("PackageRepository: Ein Package wird erworben.");
        return packages.remove(0);
    }
}
