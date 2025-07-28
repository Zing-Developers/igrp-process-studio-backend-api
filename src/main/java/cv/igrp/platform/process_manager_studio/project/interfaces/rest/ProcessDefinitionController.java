/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.interfaces.rest;

import cv.igrp.framework.stereotype.IgrpController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.platform.process_manager_studio.project.application.commands.*;
import cv.igrp.platform.process_manager_studio.project.application.queries.*;


import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionRequestDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.BpmDiagramDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.WrapperListaProcessDefinitionDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/projects")
@Tag(name = "ProcessDefinition", description = "process definition endpoint")
public class ProcessDefinitionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDefinitionController.class);

  
  private final CommandBus commandBus;
  private final QueryBus queryBus;

  
  public ProcessDefinitionController(
    CommandBus commandBus, QueryBus queryBus
  ) {
    this.commandBus = commandBus;
    this.queryBus = queryBus;
  }

  @PostMapping(
    value = "{projectId}/process-definitions"
  )
  @Operation(
    summary = "POST method to handle operations for saveProcessDefinition",
    description = "POST method to handle operations for saveProcessDefinition",
    responses = {
      @ApiResponse(
          responseCode = "201",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessDefinitionResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProcessDefinitionResponseDTO> saveProcessDefinition(@Valid @RequestBody ProcessDefinitionRequestDTO saveProcessDefinitionRequest
    , @PathVariable(value = "projectId") String projectId)
  {

      LOGGER.debug("Operation started");

      final var command = new SaveProcessDefinitionCommand(saveProcessDefinitionRequest, projectId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PostMapping(
    value = "process-definitions/{processId}/deploy"
  )
  @Operation(
    summary = "POST method to handle operations for deployProcessDefinition",
    description = "POST method to handle operations for deployProcessDefinition",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessDefinitionResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProcessDefinitionResponseDTO> deployProcessDefinition(@Valid @RequestBody BpmDiagramDTO deployProcessDefinitionRequest
    , @PathVariable(value = "processId") String processId)
  {

      LOGGER.debug("Operation started");

      final var command = new DeployProcessDefinitionCommand(deployProcessDefinitionRequest, processId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PutMapping(
    value = "process-definitions/{processId}/diagram"
  )
  @Operation(
    summary = "PUT method to handle operations for diagramEditorProcessDefinition",
    description = "PUT method to handle operations for diagramEditorProcessDefinition",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessDefinitionResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProcessDefinitionResponseDTO> diagramEditorProcessDefinition(@Valid @RequestBody BpmDiagramDTO diagramEditorProcessDefinitionRequest
    , @PathVariable(value = "processId") String processId)
  {

      LOGGER.debug("Operation started");

      final var command = new DiagramEditorProcessDefinitionCommand(diagramEditorProcessDefinitionRequest, processId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @GetMapping(
    value = "process-definitions/{processId}"
  )
  @Operation(
    summary = "GET method to handle operations for getProcessDefinitionById",
    description = "GET method to handle operations for getProcessDefinitionById",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessDefinitionResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProcessDefinitionResponseDTO> getProcessDefinitionById(
    @PathVariable(value = "processId") String processId)
  {

      LOGGER.debug("Operation started");

      final var query = new GetProcessDefinitionByIdQuery(processId);

      ResponseEntity<ProcessDefinitionResponseDTO> response = queryBus.handle(query);

      LOGGER.debug("Operation finished");

      return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @GetMapping(
    value = "process-definitions"
  )
  @Operation(
    summary = "GET method to handle operations for getProcessDefinition",
    description = "GET method to handle operations for getProcessDefinition",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaProcessDefinitionDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaProcessDefinitionDTO> getProcessDefinition(
    @RequestParam(value = "appCode", required = false) String appCode,
    @RequestParam(value = "processKey", required = false) String processKey,
    @RequestParam(value = "processName", required = false) String processName,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize)
  {

      LOGGER.debug("Operation started");

      final var query = new GetProcessDefinitionQuery(appCode, processKey, processName, pageNumber, pageSize);

      ResponseEntity<WrapperListaProcessDefinitionDTO> response = queryBus.handle(query);

      LOGGER.debug("Operation finished");

      return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PutMapping(
    value = "process-definitions/{processId}"
  )
  @Operation(
    summary = "PUT method to handle operations for updateProcessDefinition",
    description = "PUT method to handle operations for updateProcessDefinition",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessDefinitionResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProcessDefinitionResponseDTO> updateProcessDefinition(@Valid @RequestBody ProcessDefinitionRequestDTO updateProcessDefinitionRequest
    , @PathVariable(value = "processId") String processId)
  {

      LOGGER.debug("Operation started");

      final var command = new UpdateProcessDefinitionCommand(updateProcessDefinitionRequest, processId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

}