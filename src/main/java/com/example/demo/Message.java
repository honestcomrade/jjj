package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  private Long id;
  private String message;
  private String author;

  // Constructor without ID (for creating new messages before DB insert)
  public Message(String message, String author) {
    this.message = message;
    this.author = author;
  }
}