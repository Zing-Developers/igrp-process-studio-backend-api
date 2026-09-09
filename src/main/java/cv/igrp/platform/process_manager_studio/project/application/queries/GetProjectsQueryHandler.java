package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.WrapperListaProjectDTO;
import cv.igrp.platform.process_manager_studio.project.domain.filter.ProjectFilter;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.igrp.platform.process_manager_studio.shared.security.AuditUserEnricher;

@Component
public class GetProjectsQueryHandler implements QueryHandler<GetProjectsQuery, ResponseEntity<WrapperListaProjectDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProjectsQueryHandler.class);

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;

  private final AuditUserEnricher auditUserEnricher;

  public GetProjectsQueryHandler(ProjectMapper projectMapper, ProjectRepository projectRepository, AuditUserEnricher auditUserEnricher) {
    this.auditUserEnricher = auditUserEnricher;

    this.projectMapper = projectMapper;
    this.projectRepository = projectRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaProjectDTO> handle(GetProjectsQuery query) {

     ProjectFilter filter = ProjectFilter.builder()
          .appCode(query.getAppCode())
          .pageNumber(Integer.parseInt(query.getPageNumber()))
          .pageSize(Integer.parseInt(query.getPageSize()))
          .build();


    List<ProjectResponseDTO> projects = projectRepository.findAll(filter).stream()
        .map(projectMapper::toResponseDTO)
        .toList();

     var wrapperListaProjectDTO = new WrapperListaProjectDTO();
     wrapperListaProjectDTO.setContent(projects);
     wrapperListaProjectDTO.setPageNumber(filter.getPageNumber());
     wrapperListaProjectDTO.setPageSize(filter.getPageSize());
     wrapperListaProjectDTO.setTotalElements((long) projects.size());

    auditUserEnricher.enrich(wrapperListaProjectDTO.getContent());
    return ResponseEntity.ok(wrapperListaProjectDTO);
  }

}
