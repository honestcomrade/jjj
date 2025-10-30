package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
public class DemoController {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  // Removed the /createTable endpoint; schema is managed by Flyway migrations.

  @PostMapping("/messages")
  public Message addMessage(@RequestBody Message message) {
    try {
      String sql = "INSERT INTO messages (message, author) VALUES (?, COALESCE(?, 'anonymous')) RETURNING id, message, author";
      return jdbcTemplate.queryForObject(
          sql,
          (rs, rowNum) -> new Message(rs.getLong("id"), rs.getString("message"), rs.getString("author")),
          message.getMessage(), message.getAuthor());
    } catch (DataAccessException ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to insert message", ex);
    }
  }

  @GetMapping("/messages")
  public List<Message> getMessages() {
    try {
      return jdbcTemplate.query("SELECT id, message, author FROM messages",
          (rs, rowNum) -> new Message(rs.getLong("id"), rs.getString("message"), rs.getString("author")));
    } catch (DataAccessException ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch messages", ex);
    }
  }

  @GetMapping("/health")
  public String healthCheck() {
    return "OK";
  }
}
