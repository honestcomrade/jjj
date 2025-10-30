package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.MessageService;

@RestController
public class DemoController {

  private final MessageService messageService;

  @Autowired
  public DemoController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping("/messages")
  public Message addMessage(@RequestBody Message message) {
    return messageService.createMessage(message);
  }

  @GetMapping("/messages")
  public List<Message> getMessages() {
    return messageService.getAllMessages();
  }
}
