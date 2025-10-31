-- Intentionally squash-and-recreate schema for chats/messages without backfilling

-- Drop old tables if present
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS chats CASCADE;

-- Create chats table
CREATE TABLE chats (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Seed a couple of default chats
INSERT INTO chats (id, name) VALUES (1, 'default') ON CONFLICT DO NOTHING;
INSERT INTO chats (id, name) VALUES (2, 'general') ON CONFLICT DO NOTHING;
SELECT setval(pg_get_serial_sequence('chats', 'id'), (SELECT GREATEST(MAX(id), 1) FROM chats), true);

-- Create messages table with chat relationship and uniqueness per chat
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL DEFAULT 'anonymous',
    chat_id BIGINT NOT NULL REFERENCES chats(id) ON DELETE RESTRICT,
    CONSTRAINT uq_messages_chat_message UNIQUE (chat_id, message)
);

-- Insert initial demo data
INSERT INTO messages (message, author, chat_id) VALUES ('Hello from database!', 'system', 1);
