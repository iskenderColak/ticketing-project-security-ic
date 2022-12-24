package com.icolak.service;

import com.icolak.dto.ProjectDTO;
import com.icolak.dto.UserDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectDTO> listAllProjects();
    ProjectDTO findByProjectCode(String projectCode);
    void save(ProjectDTO project);
    void deleteByProjectCode(String projectCode);
    ProjectDTO update(ProjectDTO project);
    void delete(String projectCode);
    void complete(String projectCode);
    List<ProjectDTO> listAllProjectDetails();
    List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager);
}
