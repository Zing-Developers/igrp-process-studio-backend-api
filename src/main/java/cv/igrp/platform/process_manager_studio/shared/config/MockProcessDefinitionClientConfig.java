package cv.igrp.platform.process_manager_studio.shared.config;

import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter.MockProcessDefinitionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("development")
@Configuration
public class MockProcessDefinitionClientConfig {

  private static final Logger log = LoggerFactory.getLogger(MockProcessDefinitionClientConfig.class);
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
  public IProcessDefinitionAdapter mockProcessDefinitionAdapter(ProcessDefinitionRepository processDefinitionRepository) {
    // Print a message to indicate that this configuration is being used
    log.info("==========================================================");
    log.info("LOCAL TEST MODE ACTIVE: Real SDK is disabled. ===");
    log.info("==========================================================");

    // Return mock
    return new MockProcessDefinitionClient(processDefinitionRepository);
  }
}
