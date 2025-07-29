package cv.igrp.platform.process_manager_studio.project.application.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ArtifactVariable;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component
public class DiagramEditorProcessDefinitionCommandHandler implements CommandHandler<DiagramEditorProcessDefinitionCommand, ResponseEntity<ProcessDefinitionResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(DiagramEditorProcessDefinitionCommandHandler.class);


  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

   public DiagramEditorProcessDefinitionCommandHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper) {

     this.processDefinitionRepository = processDefinitionRepository;
     this.processDefinitionMapper = processDefinitionMapper;
   }


  @IgrpCommandHandler
   public ResponseEntity<ProcessDefinitionResponseDTO> handle(DiagramEditorProcessDefinitionCommand command) {

     var contentDto = command.getBpmdiagram().getContent(); // todo, method to clean xml, remove space, and enter space.

    LOGGER.debug("content : {}", contentDto);

     byte[] bpmnBytes = contentDto.getBytes(StandardCharsets.UTF_8);

     var processDefinitionId = ProcessDefinitionId.of(command.getProcessId());

     var processDefinition = processDefinitionRepository.findById(processDefinitionId)
         .orElseThrow(() ->
             IgrpResponseStatusException.notFound("Process Definition not found with id: " + processDefinitionId.getIdentifier().getValue()));


     try (InputStream inputStream = new ByteArrayInputStream(bpmnBytes)) {
       BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);

       Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);

       if (processes.isEmpty()) {
         throw IgrpResponseStatusException.badRequest("No process found in BPMN file.");
       }


       Process process = processes.iterator().next();

       String processKey  = process.getId(); // processKey
       String processName = process.getName();

       LOGGER.debug("Process ID: {}", processKey);
       LOGGER.debug("Process Name: {}", processName);

       // Verifica estado DRAF
       if (processDefinition.isDraft()) {
         LOGGER.debug("processDefinition is draft: {}", processDefinition);
         processDefinition.cleanArtifacts(); // Limpa artifacts antigos
         processDefinition.updateBpmnContent(BpmDriagram.of(contentDto)); // Atualiza o conteúdo do BPMN
       } else {
         processDefinition = ProcessDefinition.create(processDefinition.getProjectId(), processDefinition.getProcessKey(), BpmDriagram.of(contentDto));
         LOGGER.debug("processDefinition new: {}", processDefinition);
       }

       Collection<UserTask> userTasks = process.getChildElementsByType(UserTask.class);

       // processing user tasks
       for (UserTask userTask : userTasks) {
         String taskId = userTask.getId();
         String taskName = userTask.getName();

         var artifact = ProjectArtifact.create(processDefinition.getId(), taskId, taskName);
         processDefinition.addArtifact(artifact);

         // Extrair variáveis do user task
         ExtensionElements extensionElements = userTask.getExtensionElements();

         if (extensionElements != null) {
           Collection<CamundaFormData> formDataList = extensionElements.getElementsQuery()
               .filterByType(CamundaFormData.class)
               .list();

           if (formDataList != null && !formDataList.isEmpty()) {
             for (CamundaFormData formData : formDataList) {
               Collection<CamundaFormField> formFields = formData.getCamundaFormFields();

               for (CamundaFormField field : formFields) {
                 String varKey = field.getCamundaId();
                 String varName = field.getCamundaLabel();
                 String varType = field.getCamundaType();
                 String varDefault = field.getCamundaDefaultValue();
                 boolean isRequired = "true".equalsIgnoreCase(field.getAttributeValue("required"));

                 var variable = ArtifactVariable.create(
                     artifact.getId(),
                     varKey,
                     varName,
                     varType,
                     varDefault,
                     isRequired
                 );

                 artifact.addVariable(variable);
               }
             }
           }
         }
       }

       processDefinitionRepository.save(processDefinition);

       var response = processDefinitionMapper.toResponseDTO(processDefinition);
       return ResponseEntity.ok(response);

     } catch (IOException e) {
       LOGGER.error("Error reading BPMN file", e);
       throw IgrpResponseStatusException.internalServerError("Error processing BPMN file.");
     }

   }

}
