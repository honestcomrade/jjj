-- Add topics and posts tables to demonstrate parent/child/grandchild relationships

CREATE TABLE IF NOT EXISTS topics (
  id SERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chats(id) ON DELETE RESTRICT,
  name VARCHAR(200) NOT NULL,
  CONSTRAINT uq_topics_chat_name UNIQUE (chat_id, name)
);

CREATE TABLE IF NOT EXISTS posts (
  id SERIAL PRIMARY KEY,
  topic_id BIGINT NOT NULL REFERENCES topics(id) ON DELETE RESTRICT,
  text VARCHAR(255) NOT NULL
);
