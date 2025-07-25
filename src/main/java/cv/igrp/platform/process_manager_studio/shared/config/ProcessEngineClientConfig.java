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

  @Value("${igrp.process.engine.base-url}" )
  private String processEngineBaseUrl;


  /**
   * Este método cria e configura uma instância do ProcessDefinitionClient.
   * O Spring irá chamar este método e gerir o objeto retornado.
   * Quando outra classe (como o seu CommandHandler) pedir um IProcessDefinitionAdapter,
   * o Spring vai entregar este objeto.
   *
   * @param objectMapper O Spring vai injetar automaticamente o seu bean ObjectMapper padrão.
   * @return Uma instância pronta a usar do ProcessDefinitionClient.
   */
  @Bean
  public IProcessDefinitionAdapter processDefinitionAdapter(ObjectMapper objectMapper) {
    return ProcessDefinitionClient.builder()
        .baseUrl(processEngineBaseUrl)
        .objectMapper(objectMapper) // Reutilizamos o ObjectMapper já configurado pelo Spring.
        .httpClient(HttpClient.newHttpClient( )) // Usamos o cliente HTTP padrão do Java.
        .build();
  }

  /**
   * É uma boa prática garantir que o ObjectMapper do Spring está configurado
   * para lidar com os tipos de data e hora do Java 8 (como LocalDateTime),
   * que são usados pelo seu SDK. Se o Spring Boot já não o fizer por si,
   * este bean garante essa compatibilidade.
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  @Bean
  public XmlMapper xmlMapper() {
    return new XmlMapper();
  }


}
