package mtcg.model;

public class Card {
    private int id;
    private int userId;
    private String name;
    private String elementType;
    private int damage;

    public Card(int id, int userId, String name, String elementType, int damage) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.elementType = elementType;
        this.damage = damage;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public String getElementType() { return elementType; }
    public int getDamage() { return damage; }
}
