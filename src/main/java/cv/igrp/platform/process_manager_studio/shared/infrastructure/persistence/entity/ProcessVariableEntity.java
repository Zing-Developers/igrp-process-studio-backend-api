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
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_process_variable")
public class ProcessVariableEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

  
    @NotBlank(message = "name is mandatory")
    @Column(name="name", nullable = false)
    private String name;

  
    @Column(name="type")
    private String type;

  
    @Column(name="default_value")
    private String defaultValue;

  
    @Column(name="is_required")
    private boolean isRequired;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "proces_definition_id")
   private ProcessDefinitionEntity procesDefinitionId;


}