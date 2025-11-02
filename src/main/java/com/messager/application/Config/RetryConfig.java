package com.messager.application.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
  
  private static final Logger log = LoggerFactory.getLogger(RetryConfig.class);
  
  @Bean
  public RetryListener retryListener() {
    return new RetryListener() {
      @Override
      public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        // Called before first attempt - always proceed
        return true;
      }

      @Override
      public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        String message = String.format("⚠️  RETRY TRIGGERED [Attempt %d]: %s - %s", 
                 context.getRetryCount(), 
                 throwable.getClass().getSimpleName(),
                 throwable.getMessage());
        System.err.println(message);
        log.warn(message);
        
        // Log full stack trace at DEBUG level for detailed troubleshooting
        log.debug("Retry exception details:", throwable);
      }

      @Override
      public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (context.getRetryCount() > 0) {
          if (throwable == null) {
            String message = String.format("✓ Operation succeeded after %d retry attempt(s)", context.getRetryCount());
            System.out.println(message);
            log.info(message);
          } else {
            String message = String.format("❌ Operation failed after %d retry attempt(s): %s", 
                     context.getRetryCount(), 
                     throwable.getClass().getSimpleName());
            System.err.println(message);
            log.error(message);
          }
        }
      }
    };
  }
}
