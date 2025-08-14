/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.igrp.platform.process_manager_studio.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.UUID;

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




  @OneToMany(mappedBy = "projectArtifactId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
private List<ArtifactVariableEntity> variables;   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "proces_definition_id")
   private ProcessDefinitionEntity procesDefinitionId;


}
