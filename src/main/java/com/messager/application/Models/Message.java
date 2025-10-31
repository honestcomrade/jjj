package com.messager.application.Models;

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
  private Long chatId;

  // Constructor without ID (for creating new messages before DB insert)
  public Message(String message, String author) {
    this.message = message;
    this.author = author;
  }

  // Constructor without ID including chatId
  public Message(String message, String author, Long chatId) {
    this.message = message;
    this.author = author;
    this.chatId = chatId;
  }
}