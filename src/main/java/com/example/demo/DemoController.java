package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping("/createTable")
  public String createTable() {
    jdbcTemplate
        .execute("CREATE TABLE IF NOT EXISTS messages (id SERIAL PRIMARY KEY, message VARCHAR(255))");
    jdbcTemplate.update("INSERT INTO messages (message) VALUES (?)", "Hello from database!");
    return "Table created and message inserted!";
  }

  @PostMapping("/messages")
  public Message addMessage(@RequestBody Message message) {
    return jdbcTemplate.queryForObject(
        "INSERT INTO messages (message) VALUES (?) RETURNING id, message",
        (rs, rowNum) -> new Message(rs.getLong("id"), rs.getString("message")),
        message.getMessage());
  }

  @GetMapping("/messages")
  public List<Message> getMessages() {
    return jdbcTemplate.query("SELECT * FROM messages",
        (rs, rowNum) -> new Message(rs.getLong("id"), rs.getString("message")));
  }

  @GetMapping("/health")
  public String healthCheck() {
    return "OK";
  }
}
