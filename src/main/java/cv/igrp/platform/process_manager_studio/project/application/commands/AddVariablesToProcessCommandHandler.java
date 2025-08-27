package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessVariable;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessVariableMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessVariableId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessVariableResponseDTO;

import java.util.List;

@Component
public class AddVariablesToProcessCommandHandler implements CommandHandler<AddVariablesToProcessCommand, ResponseEntity<List<ProcessVariableResponseDTO>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AddVariablesToProcessCommandHandler.class);

  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessVariableMapper processVariableMapper;


   public AddVariablesToProcessCommandHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessVariableMapper processVariableMapper) {

     this.processDefinitionRepository = processDefinitionRepository;
     this.processVariableMapper = processVariableMapper;
   }

   @IgrpCommandHandler
   public ResponseEntity<List<ProcessVariableResponseDTO>> handle(AddVariablesToProcessCommand command) {

      var processVariableRequestDTOList = command.getProcessvariablerequest();
      var processId = ProcessDefinitionId.of(command.getProcessId());

      var process = processDefinitionRepository.findById(processId).orElseThrow(
          () -> IgrpResponseStatusException.notFound("Process Definition not found with id: " + processId.identifier().value())

      );

     if (processVariableRequestDTOList!= null && !processVariableRequestDTOList.isEmpty()) {

       List<ProcessVariable> incomingVariables = processVariableRequestDTOList.stream()
           .map(dto ->
               ProcessVariable.create(
               null,
               dto.getName(),
               dto.getType(),
               dto.getDefaultValue(),
               dto.isRequired(),
               processId
           ))
           .toList();

       process.syncProcessVariables(incomingVariables);

     }

      processDefinitionRepository.save(process);

      return ResponseEntity.ok(processVariableMapper.toDTO(process.getProcessVariables()));
   }

}
