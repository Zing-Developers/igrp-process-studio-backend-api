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
import java.util.List;

@Audited
@Getter
@Setter
@ToString
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_project_artifact")
public class ProjectArtifactEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;


    @NotBlank(message = "taskKey is mandatory")
    @Column(name="task_key", nullable = false)
    private String taskKey;


    @Column(name="name")
    private String name;




  @OneToMany(mappedBy = "projectArtifactId", fetch = FetchType.LAZY)
private List<ArtifactVariableEntity> variables;   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "proces_definition_id")
   private ProcessDefinitionEntity procesDefinitionId;


}
