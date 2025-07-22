/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.igrp.platform.process_manager_studio.project.application.commands.CreateProcessDefinitionCommand;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    value = "{projectId}/process-definitions"
  )
  @Operation(
    summary = "POST method to handle operations for createProcessDefinition",
    description = "POST method to handle operations for createProcessDefinition",
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

  public ResponseEntity<ProcessDefinitionResponseDTO> createProcessDefinition(
      @RequestParam(value = "file") MultipartFile file , @PathVariable(value = "projectId") String projectId)
  {

      LOGGER.debug("Operation started");

      final var command = new CreateProcessDefinitionCommand(file, projectId);

       ResponseEntity<ProcessDefinitionResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

}
