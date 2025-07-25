package cv.igrp.platform.process_manager_studio.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.framework.process.studio.sdk.client.ProcessDefinitionClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.http.HttpClient;

@Configuration
public class ProcessEngineClientConfig {

  @Value("${igrp.process.engine.base-url}")
  private String processEngineBaseUrl;

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
  public IProcessDefinitionAdapter processDefinitionAdapter(ObjectMapper objectMapper) {
    return ProcessDefinitionClient.builder()
        .baseUrl(processEngineBaseUrl)
        .objectMapper(objectMapper) // Reuse the ObjectMapper already configured by Spring.
        .httpClient(HttpClient.newHttpClient()) // Use Java's default HTTP client.
        .build();
  }

  /**
   * It’s good practice to ensure that Spring's ObjectMapper is configured
   * to handle Java 8 date and time types (like LocalDateTime),
   * which are used by your SDK. If Spring Boot doesn’t already do this for you,
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
