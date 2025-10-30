package com.example.demo;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DemoControllerIntegrationTest {

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
  void healthCheck() {
    ResponseEntity<String> resp = restTemplate.getForEntity("/health", String.class);
    assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(resp.getBody()).isEqualTo("OK");
  }

  @Test
  void getMessages() {
    ResponseEntity<Message[]> messagesResp = restTemplate.getForEntity("/messages", Message[].class);
    assertThat(messagesResp.getStatusCode().is2xxSuccessful());
    Message[] messages = messagesResp.getBody();
    assertThat(messages).isNotNull();
    assertThat(messages).extracting("message").contains("Hello from database!");
  }

  @Test
  void postMessage() {
    Message toPost = new Message(null, "Test message from test");
    Message created = restTemplate.postForObject("/messages", toPost, Message.class);
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getMessage()).isEqualTo("Test message from test");

    Message[] messages = restTemplate.getForObject("/messages", Message[].class);
    assertThat(messages).extracting("message").contains("Test message from test");
  }

}
