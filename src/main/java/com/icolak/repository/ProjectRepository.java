package com.icolak.repository;

import com.icolak.entity.Project;
import com.icolak.entity.User;
import com.icolak.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByProjectCode(String projectCode);
    List<Project> findAllByAssignedManager(User manager);
    List<Project> findAllByProjectStatusIsNotAndAssignedManager(Status status, User assignedManager);
}
