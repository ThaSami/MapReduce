package com.atypon.workflow;

import com.atypon.utility.FilesUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WorkflowManager {
  @Getter private List<Workflow> workflows;
  private WorkflowParser parser;

  public WorkflowManager(String rootPath) {
    parser = new XmlWorkflowParser();
    this.workflows = createWorkflows(rootPath);
  }

  private List<Workflow> createWorkflows(String rootPath) {
    List<String> workflowAbsPaths = FilesUtil.getFilesAbsPathInDirectory(rootPath);
    List<Workflow> workflows = new ArrayList<>();

    for (String workflow : workflowAbsPaths) {
      String data = FilesUtil.readFileAsString(workflow);
      workflows.add(parser.parse(data));
    }
    return workflows;
  }
}
