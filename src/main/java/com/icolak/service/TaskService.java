package com.icolak.service;

import com.icolak.dto.ProjectDTO;
import com.icolak.dto.TaskDTO;
import com.icolak.dto.UserDTO;
import com.icolak.enums.Status;

import java.util.List;

public interface TaskService {

    List<TaskDTO> listAllTasks();
    TaskDTO findByTaskId(Long id);
    void save(TaskDTO task);
    TaskDTO update(TaskDTO task);
    void delete(Long id);
    int totalNonCompletedTask(String projectCode);
    int totalCompletedTask(String projectCode);
    void deleteByProject(ProjectDTO projectDTO);
    void completeByProject(ProjectDTO convertToDto);
    List<TaskDTO> listAllTasksByStatusIsNot(Status status);
    List<TaskDTO> listAllTasksByStatus(Status status);
    List<TaskDTO> listAllNonCompletedByAssignedEmployee(UserDTO assignedEmployee);
}
