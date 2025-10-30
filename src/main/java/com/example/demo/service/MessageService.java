package com.example.demo.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Message;
import com.example.demo.dao.MessageDao;

@Service
public class MessageService {

  private final MessageDao messageDao;

  public MessageService(MessageDao messageDao) {
    this.messageDao = messageDao;
  }

  public List<Message> getAllMessages() {
    try {
      return messageDao.findAll();
    } catch (DataAccessException ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch messages", ex);
    }
  }

  public Message createMessage(Message message) {
    // Don't insert if author is not supplied
    if (message.getAuthor() == null) {
      return null;
    }

    try {
      return messageDao.insert(message.getMessage(), message.getAuthor());
    } catch (DataAccessException ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to insert message", ex);
    }
  }
}
