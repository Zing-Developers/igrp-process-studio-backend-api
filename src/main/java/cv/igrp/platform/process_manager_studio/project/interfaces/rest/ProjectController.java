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


import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectRequestDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.WrapperListaProjectDTO;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/projects")
@Tag(name = "Project", description = "management")
public class ProjectController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

  
  private final CommandBus commandBus;
  private final QueryBus queryBus;

  
  public ProjectController(
    CommandBus commandBus, QueryBus queryBus
  ) {
    this.commandBus = commandBus;
    this.queryBus = queryBus;
  }

  @PostMapping(
  )
  @Operation(
    summary = "POST method to handle operations for createProject",
    description = "POST method to handle operations for createProject",
    responses = {
      @ApiResponse(
          responseCode = "201",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProjectResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO createProjectRequest
    )
  {

      LOGGER.debug("Operation started");

      final var command = new CreateProjectCommand(createProjectRequest);

       ResponseEntity<ProjectResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @GetMapping(
    value = "{projectId}"
  )
  @Operation(
    summary = "GET method to handle operations for getProjectById",
    description = "GET method to handle operations for getProjectById",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProjectResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProjectResponseDTO> getProjectById(
    @PathVariable(value = "projectId") String projectId)
  {

      LOGGER.debug("Operation started");

      final var query = new GetProjectByIdQuery(projectId);

      ResponseEntity<ProjectResponseDTO> response = queryBus.handle(query);

      LOGGER.debug("Operation finished");

      return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @GetMapping(
  )
  @Operation(
    summary = "GET method to handle operations for getProjects",
    description = "GET method to handle operations for getProjects",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaProjectDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaProjectDTO> getProjects(
    @RequestParam(value = "appCode", required = false) String appCode,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize)
  {

      LOGGER.debug("Operation started");

      final var query = new GetProjectsQuery(appCode, pageNumber, pageSize);

      ResponseEntity<WrapperListaProjectDTO> response = queryBus.handle(query);

      LOGGER.debug("Operation finished");

      return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PatchMapping(
    value = "{projectId}/ativar"
  )
  @Operation(
    summary = "PATCH method to handle operations for enableProject",
    description = "PATCH method to handle operations for enableProject",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> enableProject(
    @PathVariable(value = "projectId") String projectId)
  {

      LOGGER.debug("Operation started");

      final var command = new EnableProjectCommand(projectId);

       ResponseEntity<Map<String, ?>> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PatchMapping(
    value = "{projectId}/desativar"
  )
  @Operation(
    summary = "PATCH method to handle operations for disableProject",
    description = "PATCH method to handle operations for disableProject",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> disableProject(
    @PathVariable(value = "projectId") String projectId)
  {

      LOGGER.debug("Operation started");

      final var command = new DisableProjectCommand(projectId);

       ResponseEntity<Map<String, ?>> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

  @PutMapping(
    value = "{projectId}"
  )
  @Operation(
    summary = "PUT method to handle operations for updateProject",
    description = "PUT method to handle operations for updateProject",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProjectResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ProjectResponseDTO> updateProject(@Valid @RequestBody ProjectRequestDTO updateProjectRequest
    , @PathVariable(value = "projectId") String projectId)
  {

      LOGGER.debug("Operation started");

      final var command = new UpdateProjectCommand(updateProjectRequest, projectId);

       ResponseEntity<ProjectResponseDTO> response = commandBus.send(command);

       LOGGER.debug("Operation finished");

        return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(response.getBody());
  }

}