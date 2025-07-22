/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity;

import cv.igrp.platform.process_manager_studio.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import java.util.List;


@Getter
@Setter
@ToString
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


    @Column(name="bpmn_diagram_url")
    private String bpmnDiagramUrl;


    @Column(name="version")
    private Integer version;


    @Column(name="rejected_reason")
    private String rejectedReason;




  @OneToMany(mappedBy = "procesDefinitionId", fetch = FetchType.LAZY)
private List<ProjectArtifactEntity> artifacts;   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "project_id")
   private ProjectEntity projectId;


}
