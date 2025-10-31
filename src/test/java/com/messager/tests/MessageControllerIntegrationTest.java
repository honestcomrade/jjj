package com.messager.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.messager.application.MessageApplication;
import com.messager.application.Models.Message;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MessageApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class MessageControllerIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
      .withDatabaseName("test")
      .withUsername("sa")
      .withPassword("sa");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void withMessageGet_returnsMessages() {
    ResponseEntity<Message[]> messagesResp = restTemplate.getForEntity("/messages", Message[].class);
    assertThat(messagesResp.getStatusCode().is2xxSuccessful());
    Message[] messages = messagesResp.getBody();
    assertThat(messages).isNotNull();
    assertThat(messages).extracting("message").contains("Hello from database!");
  }

  @Test
  void withValidMessage_canPost_ReturnSuccess() {
    Message toPost = new Message("Test message from test", "footman");
    Message created = restTemplate.postForObject("/messages", toPost, Message.class);
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getMessage()).isEqualTo("Test message from test");

    Message[] messages = restTemplate.getForObject("/messages", Message[].class);
    assertThat(messages).extracting("message").contains("Test message from test");
  }

  @Test
  void uniqueConstraintViolationReturns500() {
    Message first = new Message("Unique pair message", "bob");
    // First insert should succeed
    ResponseEntity<Message> firstResp = restTemplate.postForEntity("/messages", first, Message.class);
    assertThat(firstResp.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(firstResp.getBody()).isNotNull();

    // Second insert with the same (message, author) should trigger unique violation
    ResponseEntity<String> secondResp = restTemplate.postForEntity("/messages", first, String.class);
    assertThat(secondResp.getStatusCode().value()).isEqualTo(500);
  }

}
