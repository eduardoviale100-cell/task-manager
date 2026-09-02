package com.example.task_manager.service;

import com.example.task_manager.exception.ResourceNotFoundException;
import com.example.task_manager.model.Task;
import com.example.task_manager.model.TaskStatus;
import com.example.task_manager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Listar todas las tareas
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Buscar tarea por ID (Lanza ResourceNotFoundException si no existe)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la tarea con el ID: " + id));
    }

    // Crear una nueva tarea
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Actualizar una tarea existente
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la tarea con el ID: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());

        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }

        return taskRepository.save(task);
    }

    // Eliminar una tarea por ID
    public void deleteTask(Long id) {
        // Opcional: Validar si existe antes de eliminar para lanzar la excepción 404
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. No se encontró la tarea con el ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Filtrar tareas por estado (Reto Fase 2)
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}