package cv.igrp.platform.process_manager_studio.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.framework.process.studio.sdk.client.ProcessDefinitionClient;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter.MockProcessDefinitionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.http.HttpClient;

@Configuration
public class ProcessDefinitionClientConfig {

  private static final Logger log = LoggerFactory.getLogger(ProcessDefinitionClientConfig.class);

  @Value("${igrp.process.engine.base-url}")
  private String processEngineBaseUrl;


  private final ProcessDefinitionRepository processDefinitionRepository;

  public ProcessDefinitionClientConfig(ProcessDefinitionRepository processDefinitionRepository) {
    this.processDefinitionRepository = processDefinitionRepository;
  }
  /**
   * This method creates and configures an instance of ProcessDefinitionClient.
   * Spring will call this method and manage the returned object.
   * When another class (like your CommandHandler) requests an IProcessDefinitionAdapter,
   * Spring will provide this object.
   *
   * @param objectMapper Spring will automatically inject the default ObjectMapper bean.
   * @return A ready-to-use instance of ProcessDefinitionClient.
   */

  @Bean
  @Primary
  public IProcessDefinitionAdapter processDefinitionAdapter(ObjectMapper objectMapper) {
    if (processEngineBaseUrl!=null && !processEngineBaseUrl.isEmpty()) {

      log.info("Process engine base URL: {}", processEngineBaseUrl);
      return ProcessDefinitionClient.builder()
          .baseUrl(processEngineBaseUrl)
          .objectMapper(objectMapper)
          .httpClient(HttpClient.newHttpClient())
          .build();
    } else {
      log.warn("MOCK process engine client enabled — deployments will NOT reach a real engine");
      return new MockProcessDefinitionClient(processDefinitionRepository);
    }
  }
  // No custom ObjectMapper here: a hand-rolled @Primary mapper replaces Spring Boot's
  // auto-configured one for EVERY HTTP response and, without Boot's defaults, java.time
  // serializes as arrays ([2026,9,3,…]) instead of ISO strings. Boot's mapper already
  // registers JavaTimeModule and is what gets injected into processDefinitionAdapter.

}
