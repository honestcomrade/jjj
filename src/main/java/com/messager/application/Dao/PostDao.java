package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.Post;

@Repository
public class PostDao {
  private final JdbcTemplate jdbcTemplate;

  public PostDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Post insert(Long topicId, String text) throws DataAccessException {
    String sql = "INSERT INTO posts(topic_id, text) VALUES (?, ?) RETURNING id, topic_id, text";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Post(
        rs.getLong("id"),
        rs.getLong("topic_id"),
        rs.getString("text")), topicId, text);
  }
}
