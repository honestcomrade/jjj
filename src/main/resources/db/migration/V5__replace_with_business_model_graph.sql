-- Squash existing schema and create the BusinessModel graph with uniqueness enforcing reuse

-- Drop legacy tables if present
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS chats CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS business_models CASCADE;
DROP TABLE IF EXISTS parents CASCADE;
DROP TABLE IF EXISTS child1 CASCADE;
DROP TABLE IF EXISTS child2 CASCADE;
DROP TABLE IF EXISTS grandchild1 CASCADE;

-- Parents
CREATE TABLE parents (
  id SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL UNIQUE
);

-- Child1: unique per parent by (parent_id, name)
CREATE TABLE child1 (
  id SERIAL PRIMARY KEY,
  parent_id BIGINT NOT NULL REFERENCES parents(id) ON DELETE RESTRICT,
  name VARCHAR(200) NOT NULL,
  CONSTRAINT uq_child1_parent_name UNIQUE (parent_id, name)
);

-- Child2: unique per parent by (parent_id, name)
CREATE TABLE child2 (
  id SERIAL PRIMARY KEY,
  parent_id BIGINT NOT NULL REFERENCES parents(id) ON DELETE RESTRICT,
  name VARCHAR(200) NOT NULL,
  CONSTRAINT uq_child2_parent_name UNIQUE (parent_id, name)
);

-- Grandchild1: unique per child1 by (child1_id, name)
CREATE TABLE grandchild1 (
  id SERIAL PRIMARY KEY,
  child1_id BIGINT NOT NULL REFERENCES child1(id) ON DELETE RESTRICT,
  name VARCHAR(200) NOT NULL,
  CONSTRAINT uq_grandchild1_child1_name UNIQUE (child1_id, name)
);

-- Business Models: references all (no unique constraint - allows duplicate combinations)
CREATE TABLE business_models (
  id SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(100) NOT NULL,
  parent_id BIGINT NOT NULL REFERENCES parents(id) ON DELETE RESTRICT,
  child1_id BIGINT NOT NULL REFERENCES child1(id) ON DELETE RESTRICT,
  child2_id BIGINT NOT NULL REFERENCES child2(id) ON DELETE RESTRICT,
  grandchild1_id BIGINT NOT NULL REFERENCES grandchild1(id) ON DELETE RESTRICT
);

-- Seed a default parent to satisfy the requirement that parent always exists
INSERT INTO parents(name) VALUES ('default-parent') ON CONFLICT (name) DO NOTHING;
