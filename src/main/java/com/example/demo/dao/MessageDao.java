package com.example.demo.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import com.example.demo.Message;

@Repository
public class MessageDao {

  private final JdbcTemplate jdbcTemplate;

  public MessageDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Message> findAll() throws DataAccessException {
    return jdbcTemplate.query("SELECT id, message, author FROM messages",
        (rs, rowNum) -> new Message(rs.getLong("id"), rs.getString("message"), rs.getString("author")));
  }

  public Message insert(String message, String author) throws DataAccessException {
    String sql = "INSERT INTO messages (message, author) VALUES (?, ?) RETURNING id, message, author";
    return jdbcTemplate.queryForObject(sql,
        (rs, rowNum) -> new Message(rs.getLong("id"), rs.getString("message"), rs.getString("author")),
        message, author);
  }
}
