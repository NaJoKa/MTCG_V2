package mtcg.model;

public class User {
    private int id;
    private String username;
    private String password;
    private int coins;
    private int elo;

    public User(String username, String password, int coins, int elo) {
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.elo = elo;
    }

    public User(int id, String username, String password, int coins, int elo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.elo = elo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public int getElo() { return elo; }
    public void setElo(int elo) { this.elo = elo; }
}
