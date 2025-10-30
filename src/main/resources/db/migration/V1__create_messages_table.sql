-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    message VARCHAR(255) NOT NULL
);

-- Insert initial test data
INSERT INTO messages (message) VALUES ('Hello from database!');
