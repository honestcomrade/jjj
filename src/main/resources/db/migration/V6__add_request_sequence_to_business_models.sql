-- Add request_sequence column to track the order of concurrent requests
ALTER TABLE business_models
ADD COLUMN request_sequence INTEGER;
