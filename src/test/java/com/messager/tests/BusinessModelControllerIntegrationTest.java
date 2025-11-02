package com.messager.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void canCreateBusinessModel() {
    BusinessModelCreateRequest request = new BusinessModelCreateRequest();
    request.setName("bm-1");
    request.setType("alpha");
    request.setParentId(1L); // default-parent seeded in V5 migration
    request.setChild1Name("child1-a");
    request.setChild2Name("child2-a");
    request.setGrandchild1Name("grandchild1-a");
    request.setRequestSequence(1);

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
    assertThat(created.getRequestSequence()).isEqualTo(1);
  }

  @Test
  void concurrentRequests_triggerUniqueConstraintViolations() throws Exception {
    final int totalRequests = 100;
    final int batchSize = 10; // 10 threads per batch
    final int batches = totalRequests / batchSize;

    int successCount = 0;
    int failureCount = 0;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(
        batchSize, // core
        batchSize, // max
        30, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(totalRequests));

    try {
      for (int b = 0; b < batches; b++) {
        // Use same child names to force contention on unique constraints
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<ResponseEntity<BusinessModel>>> futures = new ArrayList<>(batchSize);

        for (int i = 0; i < batchSize; i++) {
          final int batchNum = b;
          final int sequenceNum = b * batchSize + i; // unique sequence 0-99
          futures.add(pool.submit(() -> {
            // Wait for gate to maximize concurrent execution
            startGate.await(5, TimeUnit.SECONDS);

            BusinessModelCreateRequest request = new BusinessModelCreateRequest();
            request.setName("bm-concurrent-shared");
            request.setType("shared-type");
            request.setParentId(1L);
            // ALL requests use SAME names to test reuse
            request.setChild1Name("child1-concurrent-shared");
            request.setChild2Name("child2-concurrent-shared");
            request.setGrandchild1Name("grandchild1-concurrent-shared");
            request.setRequestSequence(sequenceNum);

            System.out.println("[Request #" + sequenceNum + "] FIRING with: " +
                "name=" + request.getName() + 
                ", type=" + request.getType() + 
                ", parentId=" + request.getParentId() + 
                ", child1Name=" + request.getChild1Name() + 
                ", child2Name=" + request.getChild2Name() + 
                ", grandchild1Name=" + request.getGrandchild1Name() + 
                ", sequence=" + request.getRequestSequence());

            ResponseEntity<BusinessModel> response = restTemplate.postForEntity("/business-models", request, BusinessModel.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
              BusinessModel result = response.getBody();
              System.out.println("[Request #" + sequenceNum + "] ✓ SUCCESS → BusinessModel[" +
                  "id=" + result.getId() + 
                  ", parentId=" + result.getParentId() + 
                  ", child1Id=" + result.getChild1Id() + 
                  ", child2Id=" + result.getChild2Id() + 
                  ", grandchild1Id=" + result.getGrandchild1Id() + "]");
            } else {
              System.out.println("[Request #" + sequenceNum + "] ✗ FAILED with status: " + response.getStatusCode());
            }
            
            return response;
          }));
        }

        // Open the gate - all threads start at once
        startGate.countDown();

        // Collect results
        for (Future<ResponseEntity<BusinessModel>> future : futures) {
          ResponseEntity<BusinessModel> response = future.get(10, TimeUnit.SECONDS);
          if (response.getStatusCode().is2xxSuccessful()) {
            successCount++;
          } else {
            failureCount++;
          }
        }
      }
    } finally {
      pool.shutdown();
      pool.awaitTermination(10, TimeUnit.SECONDS);
    }

    // Expect some failures due to unique constraint violations under concurrency
    // In ideal naive implementation: 1 success per batch, 9 failures per batch
    // But with retries in service layer, we should see more successes (reuse)
    assertThat(successCount).isGreaterThan(0);
    assertThat(successCount + failureCount).isEqualTo(totalRequests);

    // Query DB to see which request sequences actually succeeded
    List<Integer> successfulSequences = jdbcTemplate.query(
        "SELECT request_sequence FROM business_models WHERE name LIKE 'bm-concurrent-%' ORDER BY request_sequence",
        (rs, row) -> rs.getInt("request_sequence"));

    System.out.println("=== Concurrency Test Results ===");
    System.out.println("Total requests: " + totalRequests);
    System.out.println("Successes: " + successCount);
    System.out.println("Failures: " + failureCount);
    System.out.println("Request sequences that succeeded: " + successfulSequences);
    System.out.println("================================");

    // Analyze pattern: should see exactly 1 from each batch of 10
    System.out.println("\nAnalysis by batch:");
    for (int b = 0; b < batches; b++) {
      final int batchStart = b * batchSize;
      final int batchEnd = batchStart + batchSize;
      long countInBatch = successfulSequences.stream()
          .filter(seq -> seq >= batchStart && seq < batchEnd)
          .count();
      System.out.println("Batch " + b + " (sequences " + batchStart + "-" + (batchEnd - 1) + "): "
          + countInBatch + " succeeded");
    }
  }
}
