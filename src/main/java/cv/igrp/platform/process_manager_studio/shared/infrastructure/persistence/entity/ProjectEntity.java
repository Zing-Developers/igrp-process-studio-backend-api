/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.igrp.platform.process_manager_studio.shared.config.AuditEntity;
import jakarta.persistence.*;
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
@Table(name = "t_project")
public class ProjectEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;


    @Column(name="code", unique = true)
    private String code;


    @Column(name="name")
    private String name;


    @Column(name="description")
    private String description;


    @Column(name="active")
    private boolean active;


    @Column(name="app_code")
    private String appCode;




  @OneToMany(mappedBy = "projectId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
private List<ProcessDefinitionEntity> processdefinitions;
}
