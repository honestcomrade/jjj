package com.messager.application.Dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.Message;

import org.springframework.dao.DataAccessException;

@Repository
public class MessageDao {

  private final JdbcTemplate jdbcTemplate;

  public MessageDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Message> findAll() throws DataAccessException {
    return jdbcTemplate.query("SELECT id, message, author, chat_id FROM messages",
        (rs, rowNum) -> new Message(
            rs.getLong("id"),
            rs.getString("message"),
            rs.getString("author"),
            rs.getObject("chat_id", Long.class)));
  }

  public Message insert(String message, String author, Long chatId) throws DataAccessException {
    String sql = "INSERT INTO messages (message, author, chat_id) VALUES (?, ?, COALESCE(?, 1)) RETURNING id, message, author, chat_id";
    return jdbcTemplate.queryForObject(sql,
        (rs, rowNum) -> new Message(
            rs.getLong("id"),
            rs.getString("message"),
            rs.getString("author"),
            rs.getObject("chat_id", Long.class)),
        message, author, chatId);
  }
}
