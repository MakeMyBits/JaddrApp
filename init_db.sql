CREATE TABLE contacts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT
);

INSERT INTO contacts (name, phone, email) VALUES ('Alice', '123456789', 'alice@example.com');
INSERT INTO contacts (name, phone, email) VALUES ('Bob', '987654321', 'bob@example.com');
