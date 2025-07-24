/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.igrp.platform.process_manager_studio.project.application.commands.DeployProcessDefinitionCommand;
import cv.igrp.platform.process_manager_studio.project.application.commands.DiagramEditorProcessDefinitionCommand;
import cv.igrp.platform.process_manager_studio.project.application.commands.SaveProcessDefinitionCommand;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionRequestDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    value = "{projectId}/process-definitions/processes"
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
    value = "{projectId}/processes/{processId}/deploy",
  consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE
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

  public ResponseEntity<ProcessDefinitionResponseDTO> deployProcessDefinition(
    @RequestParam(value = "file") MultipartFile file, @PathVariable(value = "projectId") String projectId,@PathVariable(value = "processId") String processId)
  {

      LOGGER.debug("Operation started");

      final var command = new DeployProcessDefinitionCommand(file, projectId, processId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PutMapping(
    value = "{projectId}/processes/{processId}/diagram",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
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

  public ResponseEntity<ProcessDefinitionResponseDTO> diagramEditorProcessDefinition(
    @RequestParam(value = "file") MultipartFile file, @PathVariable(value = "projectId") String projectId,@PathVariable(value = "processId") String processId)
  {

      LOGGER.debug("Operation started");

      final var command = new DiagramEditorProcessDefinitionCommand(file, projectId, processId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

}
