package com.messager.application.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.messager.application.Models.Message;
import com.messager.application.Services.MessageService;

@RestController
public class MessageController {

  private final MessageService messageService;

  @Autowired
  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  // Back-compat: body may include chatId or default will be used
  @PostMapping("/messages")
  public Message addMessage(@RequestBody Message message) {
    return messageService.createMessage(message);
  }

  // Preferred: specify chat in the path
  @PostMapping("/chats/{chatId}/messages")
  public Message addMessageToChat(@PathVariable("chatId") Long chatId, @RequestBody Message message) {
    message.setChatId(chatId);
    return messageService.createMessage(message);
  }

  @GetMapping("/messages")
  public List<Message> getMessages() {
    return messageService.getAllMessages();
  }
}
