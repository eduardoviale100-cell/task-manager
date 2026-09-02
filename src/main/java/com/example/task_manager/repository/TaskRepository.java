package com.example.task_manager.repository;

import com.example.task_manager.model.Task;
import com.example.task_manager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Método para cumplir con el Reto Fase 2 (filtrar tareas por estado)
    List<Task> findByStatus(TaskStatus status);
}