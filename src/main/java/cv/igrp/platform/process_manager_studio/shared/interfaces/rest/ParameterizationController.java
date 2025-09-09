package cv.igrp.platform.process_manager_studio.shared.interfaces.rest;

import cv.igrp.framework.core.data.EnumItem;
import cv.igrp.framework.core.utils.object.EnumUtils;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("parameterization")
@Tag(name = "Parameterization", description = "Parameterization Management")
public class ParameterizationController {


    @GetMapping("/process-definition-state")
    public ResponseEntity<List<EnumItem<String>>> getProcessDefinitionState() {
        return ResponseEntity.ok(EnumUtils.mapEnumToItems(ProcessDefinitionState.class));
    }



}
