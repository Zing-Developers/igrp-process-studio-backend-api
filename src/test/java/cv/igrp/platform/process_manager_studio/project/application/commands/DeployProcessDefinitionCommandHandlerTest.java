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

  // 2. DECLARE UMA VARIÁVEL PARA O NOSSO MOCK CLIENT (sem a anotação @Mock)
  private IProcessDefinitionAdapter mockProcessDefinitionAdapter;

  // 3. DECLARE A CLASSE QUE VAMOS TESTAR (sem a anotação @InjectMocks)
  private DeployProcessDefinitionCommandHandler commandHandler;

    @BeforeEach
    void setUp() {
      // TODO: initialize mock dependencies if needed

      //    Isto garante que cada teste começa com o mock "limpo".
      mockProcessDefinitionAdapter = new MockProcessDefinitionClient();

      // 2. CRIE UMA NOVA INSTÂNCIA DA CLASSE QUE ESTAMOS A TESTAR
      //    Injetamos as dependências manualmente através do construtor.
      commandHandler = new DeployProcessDefinitionCommandHandler(
          null, // Passamos null para o ProjectRepository, pois assumimos que não é usado neste fluxo. Se for, teríamos de o "mockar" também.
          processDefinitionRepository,   // O mock criado pelo Mockito.
          processDefinitionMapper,       // O mock criado pelo Mockito.
          mockProcessDefinitionAdapter   // A nossa instância REAL do mock client.
      );
    }

    @Test
    void testHandle() {

      // 1. Dados de entrada para o comando que o utilizador envia.
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
      var command = new DeployProcessDefinitionCommand(bpmDiagramDTO,processId.getIdentifier().getValue().toString() );

      // 2. Simular o objeto que o repositório vai retornar.
      //    Usamos o método `rebuild` porque estamos a simular a reconstituição
      //    de um objeto que JÁ EXISTE na base de dados, que é exatamente o propósito deste método.
      var processFromDatabase = ProcessDefinition.rebuild(
          processId,
          ProjectId.generate(),
          "my-process-key",
          null,
          BpmDriagram.of(bpmnContent),
          0,
          new ArrayList<>(),
          ProcessDefinitionState.DRAFT, // O processo está em rascunho na BD.
          null, null,
          "Test Process", "A process for testing"
      );

      // 3. "Ensinar" o mock do repositório: quando `findById` for chamado,
      //    deve retornar o objeto que acabámos de "reconstruir".
      when(processDefinitionRepository.findById(any(ProcessDefinitionId.class)))
          .thenReturn(Optional.of(processFromDatabase));

      // =================== ACT (Executar a ação) ===================
      ResponseEntity<?> response = commandHandler.handle(command);

      // =================== ASSERT (Verificar os resultados) ===================
      assertEquals(200, response.getStatusCodeValue());

      ArgumentCaptor<ProcessDefinition> processCaptor = ArgumentCaptor.forClass(ProcessDefinition.class);
      verify(processDefinitionRepository).save(processCaptor.capture());
      ProcessDefinition savedProcess = processCaptor.getValue();

      // Verificar se a entidade foi atualizada corretamente.
      assertNotNull(savedProcess.getDeploymentId());
      assertEquals(1, savedProcess.getVersion());
      assertEquals(ProcessDefinitionState.PUBLISHED, savedProcess.getState());

      // Verificar se o objeto modificado é a mesma instância que foi carregada da "BD".
      assertSame(processFromDatabase, savedProcess, "A instância do objeto deve ser a mesma, apenas modificada.");

    }
}
