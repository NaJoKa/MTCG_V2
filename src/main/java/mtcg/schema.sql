-- Tabelle für User
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       coins INT NOT NULL DEFAULT 20,
                       elo INT NOT NULL DEFAULT 100
);

-- Tabelle für Karten
CREATE TABLE cards (
                       id SERIAL PRIMARY KEY,
                       user_id INT REFERENCES users(id),
                       name VARCHAR(255) NOT NULL,
                       element_type VARCHAR(50) CHECK (element_type IN ('Fire', 'Water', 'Normal')) NOT NULL,
                       damage INT NOT NULL
);

-- Tabelle für Deck
CREATE TABLE deck (
                      user_id INT REFERENCES users(id),
                      card_id INT REFERENCES cards(id),
                      PRIMARY KEY (user_id, card_id)
);

-- Tabelle für Battles
CREATE TABLE battles (
                         id SERIAL PRIMARY KEY,
                         user1_id INT REFERENCES users(id),
                         user2_id INT REFERENCES users(id),
                         winner_id INT REFERENCES users(id)
);

-- Tabelle für Trades
CREATE TABLE trades (
                        id SERIAL PRIMARY KEY,
                        seller_id INT REFERENCES users(id),
                        buyer_id INT REFERENCES users(id),
                        card_id INT REFERENCES cards(id)
);
