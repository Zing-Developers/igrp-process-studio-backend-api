package cv.igrp.platform.process_manager_studio;


import com.fasterxml.jackson.databind.ObjectMapper;
import cv.igrp.framework.process.management.integration.core.model.BpmnSourceType;
import cv.igrp.framework.process.management.integration.core.model.IgrpProcessDefinitionRepresentation;
import cv.igrp.framework.process.studio.sdk.client.ProcessDefinitionClient;
import cv.igrp.framework.process.management.integration.core.model.ProcessDefinitionRepresentation;
import cv.igrp.framework.process.studio.sdk.client.dto.ProcessDefinitionResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class ProcessDefinitionClientTest  {

  private MockWebServer mockWebServer;
  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterEach
  public void tearDown() throws Exception {
    mockWebServer.shutdown();
  }

  @Test
  public void testDeploy() throws Exception {
    // Simula a resposta da API
    ProcessDefinitionResponse response = new ProcessDefinitionResponse();
    response.setId("some-id");
    response.setKey("dynamicProcess");
    response.setName("Dynamic Process");
    response.setDescription("Description");
    response.setVersion("1");
    response.setBpmnXml("<bpmn>...</bpmn>");
    response.setBpmnSourceType(BpmnSourceType.INLINE_XML);
    response.setResourceName("dynamicProcess.bpmn20.xml");
    response.setDeployed(true);
    response.setDeploymentId("deployment-id");
    response.setDeployedAt(LocalDateTime.now());


    String responseJson = objectMapper.writeValueAsString(response);

    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(responseJson));

    String baseUrl = mockWebServer.url("/").toString();

    ProcessDefinitionClient client = ProcessDefinitionClient.builder()
        .baseUrl(baseUrl)
        .objectMapper(objectMapper)
        .httpClient(HttpClient.newHttpClient())
        .build();

    ProcessDefinitionRepresentation input = IgrpProcessDefinitionRepresentation.builder()
        .key("processo-chave")
        .name("Meu Processo")
        .description("Descrição")
        .bpmnXml("<bpmn></bpmn>")
        .build();

    ProcessDefinitionRepresentation result = client.deploy(input);

    assertEquals("proc123", result.getId());
    assertEquals("processo-chave", result.getKey());
    assertTrue(result.isDeployed());
  }


}
