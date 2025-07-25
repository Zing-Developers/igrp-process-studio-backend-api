package cv.igrp.platform.process_manager_studio.project.application.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter.MockProcessDefinitionClient;
import cv.igrp.platform.process_manager_studio.project.application.dto.BpmDiagramDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DeployProcessDefinitionCommandHandlerTest {

  @Mock
  private ProcessDefinitionRepository processDefinitionRepository;

  @Mock
  private ProcessDefinitionMapper processDefinitionMapper;

  // 2. Declare a variable for the mock client (without @Mock annotation)
  private IProcessDefinitionAdapter mockProcessDefinitionAdapter;

  // 3. Declare the class under test (without @InjectMocks annotation)
  private DeployProcessDefinitionCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    // Ensure each test starts with a clean mock client instance
    mockProcessDefinitionAdapter = new MockProcessDefinitionClient();

    // 2. Create a new instance of the class under test
    //    Manually inject dependencies via the constructor
    commandHandler = new DeployProcessDefinitionCommandHandler(
        processDefinitionRepository,   // The mock created by Mockito
        processDefinitionMapper,       // The mock created by Mockito
        mockProcessDefinitionAdapter   // The real instance of our mock client
    );
  }

  @Test
  void testHandle() {

    // 1. Input data for the command sent by the user
    ProcessDefinitionId processId = ProcessDefinitionId.generate();
    String bpmnContent = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\n" +
        "<bpmn2:definitions xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xmlns:bpmn2=\\\"http://www.omg.org/spec/BPMN/20100524/MODEL\\\" xmlns:bpmndi=\\\"http://www.omg.org/spec/BPMN/20100524/DI\\\" xmlns:dc=\\\"http://www.omg.org/spec/DD/20100524/DC\\\" xmlns:activiti=\\\"http://activiti.org/bpmn\\\" id=\\\"sample-diagram\\\" targetNamespace=\\\"http://activiti.org/bpmn\\\" xsi:schemaLocation=\\\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\\\">\n" +
        "  <bpmn2:process id=\\\"wqqw\\\" name=\\\"qwqw\\\" isExecutable=\\\"false\\\">\n" +
        "    <bpmn2:startEvent id=\\\"StartEvent_1\\\" />\n" +
        "    <bpmn2:userTask id=\\\"Activity_024pdad\\\" />\n" +
        "  </bpmn2:process>\n" +
        "  <bpmndi:BPMNDiagram id=\\\"BPMNDiagram_1\\\">\n" +
        "    <bpmndi:BPMNPlane id=\\\"BPMNPlane_1\\\" bpmnElement=\\\"wqqw\\\">\n" +
        "      <bpmndi:BPMNShape id=\\\"_BPMNShape_StartEvent_2\\\" bpmnElement=\\\"StartEvent_1\\\">\n" +
        "        <dc:Bounds x=\\\"412\\\" y=\\\"240\\\" width=\\\"36\\\" height=\\\"36\\\" />\n" +
        "      </bpmndi:BPMNShape>\n" +
        "      <bpmndi:BPMNShape id=\\\"Activity_0j5yrqn_di\\\" bpmnElement=\\\"Activity_024pdad\\\">\n" +
        "        <dc:Bounds x=\\\"190\\\" y=\\\"170\\\" width=\\\"100\\\" height=\\\"80\\\" />\n" +
        "      </bpmndi:BPMNShape>\n" +
        "    </bpmndi:BPMNPlane>\n" +
        "  </bpmndi:BPMNDiagram>\n" +
        "</bpmn2:definitions>\n";

    BpmDiagramDTO bpmDiagramDTO = new BpmDiagramDTO(bpmnContent);
    var command = new DeployProcessDefinitionCommand(bpmDiagramDTO, processId.getIdentifier().getValue().toString());

    // 2. Simulate the object that the repository will return
    //    We use `rebuild` to simulate loading an object that ALREADY EXISTS in the database
    var processFromDatabase = ProcessDefinition.rebuild(
        processId,
        ProjectId.generate(),
        "my-process-key",
        null,
        BpmDriagram.of(bpmnContent),
        0,
        new ArrayList<>(),
        ProcessDefinitionState.DRAFT, // The process is in draft state in the DB
        null, null,
        "Test Process", "A process for testing"
    );

    // 3. "Teach" the mock repository: when `findById` is called,
    //    it should return the object we just "rebuilt"
    when(processDefinitionRepository.findById(any(ProcessDefinitionId.class)))
        .thenReturn(Optional.of(processFromDatabase));

    // =================== ACT (Perform the action) ===================
    ResponseEntity<?> response = commandHandler.handle(command);

    // =================== ASSERT (Check the results) ===================
    assertEquals(200, response.getStatusCodeValue());

    ArgumentCaptor<ProcessDefinition> processCaptor = ArgumentCaptor.forClass(ProcessDefinition.class);
    verify(processDefinitionRepository).save(processCaptor.capture());
    ProcessDefinition savedProcess = processCaptor.getValue();

    // Verify that the entity was updated correctly
    assertNotNull(savedProcess.getDeploymentId());
    assertEquals(1, savedProcess.getVersion());
    assertEquals(ProcessDefinitionState.PUBLISHED, savedProcess.getState());

    // Check that the modified object is the same instance that was loaded from the "DB"
    assertSame(processFromDatabase, savedProcess, "The instance of the object should be the same, just modified.");
  }
}
