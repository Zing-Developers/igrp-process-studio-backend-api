package cv.igrp.platform.process_manager_studio.shared.config;

import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter.MockProcessDefinitionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * This configuration class is ONLY used in development/test environments.
 * It defines beans that take precedence over production beans.
 */
@Profile("development")
@Configuration
public class TestBeanConfiguration {

  /**
   * This method creates a "bean" (an object managed by Spring)
   * of our MockProcessDefinitionClient.
   *
   * The @Primary annotation is the most important part. It tells Spring:
   * "If you find another bean of type IProcessDefinitionAdapter, ignore it.
   * Use THIS one as the primary."
   */
  @Bean
  @Primary
  public IProcessDefinitionAdapter mockProcessDefinitionAdapter() {
    // Print a message to indicate that this configuration is being used
    System.out.println("==========================================================");
    System.out.println("=== LOCAL TEST MODE ACTIVE: Real SDK is disabled. ===");
    System.out.println("==========================================================");

    // Return our test stub
    return new MockProcessDefinitionClient();
  }
}
