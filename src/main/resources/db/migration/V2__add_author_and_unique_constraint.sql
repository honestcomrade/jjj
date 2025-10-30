-- Add new column 'author' with default
ALTER TABLE IF EXISTS messages
  ADD COLUMN IF NOT EXISTS author VARCHAR(100) NOT NULL DEFAULT 'anonymous';

-- Add unique constraint across (message, author)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'uq_messages_message_author'
  ) THEN
    ALTER TABLE messages
      ADD CONSTRAINT uq_messages_message_author UNIQUE (message, author);
  END IF;
END $$;
