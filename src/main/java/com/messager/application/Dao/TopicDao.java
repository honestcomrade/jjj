package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.Topic;

@Repository
public class TopicDao {
  private final JdbcTemplate jdbcTemplate;

  public TopicDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Topic insert(Long chatId, String name) throws DataAccessException {
    String sql = "INSERT INTO topics(chat_id, name) VALUES (?, ?) RETURNING id, chat_id, name";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Topic(
        rs.getLong("id"),
        rs.getLong("chat_id"),
        rs.getString("name")), chatId, name);
  }

  public Long findIdByName(Long chatId, String name) throws DataAccessException {
    String sql = "SELECT id FROM topics WHERE chat_id = ? AND name = ?";
    return jdbcTemplate.query(sql, ps -> {
      ps.setObject(1, chatId);
      ps.setString(2, name);
    }, rs -> rs.next() ? rs.getLong("id") : null);
  }
}
