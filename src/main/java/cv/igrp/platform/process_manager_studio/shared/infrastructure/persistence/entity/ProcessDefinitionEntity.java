/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity;

import cv.igrp.platform.process_manager_studio.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Audited
@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_process_definition")
public class ProcessDefinitionEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;


    @NotBlank(message = "processKey is mandatory")
    @Column(name="process_key", nullable = false)
    private String processKey;


    @Column(name="title")
    private String title;


    @Column(name="description")
    private String description;


    @Column(name="bpmn_diagram_url")
    private String bpmnDiagramUrl;


    @Lob
    @Column(name="bpm_file_content", columnDefinition="TEXT")
    private String bpmFileContent;


    @Column(name="version")
    private Integer version;


    @Column(name="is_latest")
    private boolean isLatest;


    @Enumerated(EnumType.STRING)
    @Column(name="state")
    private ProcessDefinitionState state;


    @Column(name="deployment_id")
    private String deploymentId;


    @Column(name="deployment_date")
    private LocalDateTime deploymentDate;




  @OneToMany(mappedBy = "procesDefinitionId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
private List<ProcessArtifactEntity> artifacts;


  @OneToMany(mappedBy = "procesDefinitionId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
private List<ProcessVariableEntity> processVariables;   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "project_id")
   private ProjectEntity projectId;


}
