package com.example.demo;

public class Message {
  private Long id;
  private String message;
  private String author;

  // Default constructor required for JSON deserialization
  public Message() {
  }

  public Message(Long id, String message) {
    this.id = id;
    this.message = message;
    this.author = "anonymous";
  }

  public Message(Long id, String message, String author) {
    this.id = id;
    this.message = message;
    this.author = author;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }
}