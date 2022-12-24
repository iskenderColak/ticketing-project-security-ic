package com.icolak.repository;

import com.icolak.entity.Project;
import com.icolak.entity.Task;
import com.icolak.entity.User;
import com.icolak.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "SELECT COUNT(*) " +
            "FROM tasks t JOIN projects p ON t.project_id = p.id " +
            "WHERE p.project_code=?1 AND t.task_status='COMPLETE'", nativeQuery = true)
    int totalCompletedTasks(String projectCode);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectCode = ?1 AND t.taskStatus <> 'COMPLETE'")
    int totalNonCompletedTasks(String projectCode);

    List<Task> findAllByProject(Project project);

    List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status, User assignedEmployee);

    List<Task> findAllByTaskStatusAndAssignedEmployee(Status status, User assignedEmployee);
}
