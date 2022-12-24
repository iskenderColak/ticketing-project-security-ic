package com.icolak.service.impl;

import com.icolak.dto.ProjectDTO;
import com.icolak.dto.TaskDTO;
import com.icolak.dto.UserDTO;
import com.icolak.entity.User;
import com.icolak.mapper.UserMapper;
import com.icolak.repository.UserRepository;
import com.icolak.service.ProjectService;
import com.icolak.service.TaskService;
import com.icolak.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, @Lazy ProjectService projectService, @Lazy TaskService taskService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
    }
/*
    // This below method lists only the users whose isDeleted field is false
    //Instead of this method we use @Where(clause = "is_deleted=false") annotation
    at the top of entity class which is covering all the queries in the repository
    working with that entity

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAll(Sort.by("firstName"));
        return userList.stream()
        //        .filter(user -> user.getIsDeleted() == false)
                .filter(user -> !user.getIsDeleted())
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }
*/
    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAll(Sort.by("firstName"));
        return userList.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        return userMapper.convertToDto(userRepository.findByUserName(username));
    }

    @Override
    public void save(UserDTO user) {
        userRepository.save(userMapper.convertToEntity(user));
    }

    @Override
    public void deleteByUserName(String username) {
        //userRepository.delete(userRepository.findByUserName(username));
        userRepository.deleteByUserName(username);
    }

    @Override
    public UserDTO update(UserDTO user) {
        //Find current user
        User dbUser = userRepository.findByUserName(user.getUserName()); // has id
        //Map update userDto to entity object
        User convertedUser = userMapper.convertToEntity(user); // does not have id
        //Set id to the converted object
        convertedUser.setId(dbUser.getId());
        //Save the updated user in the db
        userRepository.save(convertedUser);

        return findByUserName(user.getUserName());
    }

    // Soft deleting --> we change isDeleted field of the user but don't remove from the table
    //By @Where annotation at the top of the User entity we can remove the user from the UI
    @Override
    public void delete(String username) {

        User dbUser = userRepository.findByUserName(username);

        if (checkIfUserCanBeDeleted(dbUser)) {
            dbUser.setIsDeleted(true);
            userRepository.save(dbUser);
        }
    }

    @Override
    public List<UserDTO> listAllUsersByRole(String description) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(description);
        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user) {

        switch(user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService
                        .listAllNonCompletedByAssignedManager(userMapper.convertToDto(user));
                return projectDTOList.size() == 0;
            case "Employee":
                List<TaskDTO> taskDTOList = taskService
                        .listAllNonCompletedByAssignedEmployee(userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            default:
                return true;
        }
    }
}
