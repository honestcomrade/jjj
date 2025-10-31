package com.messager.application.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
  private Long id;
  private String name;

  public Chat(String name) {
    this.name = name;
  }
}
