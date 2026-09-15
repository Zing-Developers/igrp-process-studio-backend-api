package cv.igrp.platform.process_manager_studio.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Page of GET /email-access-mappings, the platform's page shape plus the rows. Modelled in .igrpstudio/shared/dto. */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class WrapperListaEmailAccessMappingDTO extends PageDTO {

  private List<EmailAccessMappingDTO> content = new ArrayList<>();

}
