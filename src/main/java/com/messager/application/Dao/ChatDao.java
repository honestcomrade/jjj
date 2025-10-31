package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDao {
  private final JdbcTemplate jdbcTemplate;

  public ChatDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  // Naively attempts to insert; ignores conflict and does not return id
  public void insertIfAbsent(String name) throws DataAccessException {
    String sql = "INSERT INTO chats(name) VALUES (?) ON CONFLICT(name) DO NOTHING";
    jdbcTemplate.update(sql, name);
  }

  public Long findIdByName(String name) throws DataAccessException {
    String sql = "SELECT id FROM chats WHERE name = ?";
    return jdbcTemplate.query(sql, ps -> ps.setString(1, name), rs -> {
      if (rs.next()) {
        return rs.getLong("id");
      }
      return null;
    });
  }
}
