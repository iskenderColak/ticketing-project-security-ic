package com.icolak.service.impl;

import com.icolak.dto.ProjectDTO;
import com.icolak.dto.UserDTO;
import com.icolak.entity.Project;
import com.icolak.entity.User;
import com.icolak.enums.Status;
import com.icolak.mapper.MapperUtil;
import com.icolak.mapper.ProjectMapper;
import com.icolak.repository.ProjectRepository;
import com.icolak.service.ProjectService;
import com.icolak.service.TaskService;
import com.icolak.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MapperUtil mapperUtil;
    private final UserService userService;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, MapperUtil mapperUtil, UserService userService, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.mapperUtil = mapperUtil;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        return projectRepository.findAll(Sort.by("projectCode")).stream()
                .map(projectMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO findByProjectCode(String projectCode) {
        return projectMapper.convertToDto(projectRepository.findByProjectCode(projectCode));
    }

    @Override
    public void save(ProjectDTO dto) {

        dto.setProjectStatus(Status.OPEN);
        Project project = projectMapper.convertToEntity(dto);
        projectRepository.save(project);
    }

    @Override
    public void deleteByProjectCode(String projectCode) {

    }

    @Override
    public ProjectDTO update(ProjectDTO project) {

        Project dbProject = projectRepository.findByProjectCode(project.getProjectCode());
        Project convertedProject = projectMapper.convertToEntity(project);
        convertedProject.setId(dbProject.getId());
        convertedProject.setProjectStatus(dbProject.getProjectStatus());
        projectRepository.save(convertedProject);

        return project;
    }

    @Override
    public void delete(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setIsDeleted(true);
        // After we delete a project we change the project code
        // to be able to create a new project with the same project code
        // For example we can do like that, it depends on you
        project.setProjectCode(project.getProjectCode() + "-" + project.getId()); // SP00-1

        projectRepository.save(project);
        // When we delete a project, we delete all the tasks related to that project
        taskService.deleteByProject(projectMapper.convertToDto(project));
    }

    @Override
    public void complete(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
        // When we complete a project, we complete all the tasks related to that project
        taskService.completeByProject(projectMapper.convertToDto(project));
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        UserDTO currentUserDTO = userService.findByUserName("harold@manager.com"); // login manager
        User user = mapperUtil.convert(currentUserDTO, new User());
        // We bring all the projects belong to log in manager
        List<Project> projectList = projectRepository.findAllByAssignedManager(user);

        return projectList.stream().map(project -> {
            ProjectDTO projectDTO = mapperUtil.convert(project, new ProjectDTO());
            projectDTO.setUnfinishedTaskCounts(taskService.totalNonCompletedTask(project.getProjectCode()));
            projectDTO.setCompleteTaskCounts(taskService.totalCompletedTask(project.getProjectCode()));

            return projectDTO;
            }
        ).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager) {
        List<Project> projects = projectRepository
                .findAllByProjectStatusIsNotAndAssignedManager(Status.COMPLETE, mapperUtil.convert(assignedManager, new User()));
        return projects.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }
}
