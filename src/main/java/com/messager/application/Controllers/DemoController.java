package com.messager.application.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.messager.application.Models.Post;
import com.messager.application.Services.ConcurrentDemoService;

import java.util.Map;

@RestController
public class DemoController {

  private final ConcurrentDemoService demoService;

  @Autowired
  public DemoController(ConcurrentDemoService demoService) {
    this.demoService = demoService;
  }

  // Body example: { "chatName":"room-1", "topicName":"topic-a", "text":"hello" }
  @PostMapping("/demo/posts")
  public Post createPostNaive(@RequestBody Map<String, String> body) {
    String chatName = body.get("chatName");
    String topicName = body.get("topicName");
    String text = body.get("text");
    return demoService.createPostNaive(chatName, topicName, text);
  }
}
