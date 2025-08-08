package cv.igrp.platform.process_manager_studio.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.context.annotation.Profile;

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
      log.info("==========================================================");
      log.info("=== Real SDK enabled ===");
      log.info("=== URL processEngineBaseUrl: {} ===", processEngineBaseUrl);
      log.info("==========================================================");

      return ProcessDefinitionClient.builder()
          .baseUrl(processEngineBaseUrl)
          .objectMapper(objectMapper)
          .httpClient(HttpClient.newHttpClient())
          .build();
    } else {
      log.info("==========================================================");
      log.info("=== MOCK SDK enabled ===");
      log.info("==========================================================");

      return new MockProcessDefinitionClient(processDefinitionRepository);
    }
  }
  /**
   * It’s good practice to ensure that Spring's ObjectMapper is configured
   * to handle Java 8 date and time types (like LocalDateTime),
   * which are used by the SDK.
   * this bean ensures that compatibility.
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  /**
   * Bean for XML processing using Jackson's XmlMapper.
   * Useful for handling XML payloads in requests or responses.
   */
  @Bean
  public XmlMapper xmlMapper() {
    return new XmlMapper();
  }

}
