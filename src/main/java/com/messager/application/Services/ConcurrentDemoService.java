package com.messager.application.Services;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.messager.application.Dao.ChatDao;
import com.messager.application.Dao.PostDao;
import com.messager.application.Dao.TopicDao;
import com.messager.application.Models.Post;
import com.messager.application.Models.Topic;

@Service
public class ConcurrentDemoService {
  private final ChatDao chatDao;
  private final TopicDao topicDao;
  private final PostDao postDao;

  public ConcurrentDemoService(ChatDao chatDao, TopicDao topicDao, PostDao postDao) {
    this.chatDao = chatDao;
    this.topicDao = topicDao;
    this.postDao = postDao;
  }

  // Intentionally naive and racy: may cause NOT NULL violation on chat_id under
  // concurrency
  public Post createPostNaive(String chatName, String topicName, String text) {
    try {
      // Attempt to create the chat if absent, but don't get the id or ensure
      // visibility
      chatDao.insertIfAbsent(chatName);

      // Single lookup; if concurrent insert isn't visible yet, this returns null
      Long chatId = chatDao.findIdByName(chatName);

      // Proceed without retry; may throw NOT NULL violation on chat_id
      Topic topic = topicDao.insert(chatId, topicName);
      return postDao.insert(topic.getId(), text);
    } catch (DataAccessException ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create post (naive)", ex);
    }
  }
}
