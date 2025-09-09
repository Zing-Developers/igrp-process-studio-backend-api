package cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn;

public interface BpmnProcessReader {

  ParsedProcess readFromXml(String bpmnXml);

  String sanitizeBpmnXml(String rawXml);
}
