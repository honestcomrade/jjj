package com.messager.tests;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.messager.application.Models.BusinessModel;
import com.messager.application.Models.dto.BusinessModelCreateRequest;

@SpringBootTest(classes = MessageApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BusinessModelControllerIntegrationTest {

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
  void canCreateBusinessModel() {
    BusinessModelCreateRequest request = new BusinessModelCreateRequest();
    request.setName("bm-1");
    request.setType("alpha");
    request.setParentId(1L); // default-parent seeded in V5 migration
    request.setChild1Name("child1-a");
    request.setChild2Name("child2-a");
    request.setGrandchild1Name("grandchild1-a");

    ResponseEntity<BusinessModel> response = restTemplate.postForEntity("/business-models", request,
        BusinessModel.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    BusinessModel created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("bm-1");
    assertThat(created.getType()).isEqualTo("alpha");
    assertThat(created.getParentId()).isEqualTo(1L);
    assertThat(created.getChild1Id()).isNotNull();
    assertThat(created.getChild2Id()).isNotNull();
    assertThat(created.getGrandchild1Id()).isNotNull();
  }
}
