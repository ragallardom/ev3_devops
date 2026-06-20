package com.example.taskflow.service;

import com.example.taskflow.model.Task;
import com.example.taskflow.model.TaskStatus;
import com.example.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        // Order tasks so recently updated/created or todo tasks display neatly
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public @NonNull Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }

    @Transactional
    public @NonNull Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title is required");
        }
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        return taskRepository.save(task);
    }

    @Transactional
    public @NonNull Task updateTask(Long id, Task updatedTaskDetails) {
        Task existingTask = getTaskById(id);

        if (updatedTaskDetails.getTitle() == null || updatedTaskDetails.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title is required");
        }

        existingTask.setTitle(updatedTaskDetails.getTitle());
        existingTask.setDescription(updatedTaskDetails.getDescription());
        existingTask.setStatus(updatedTaskDetails.getStatus());
        existingTask.setPriority(updatedTaskDetails.getPriority());
        existingTask.setDueDate(updatedTaskDetails.getDueDate());

        return taskRepository.save(existingTask);
    }

    @Transactional
    public @NonNull Task updateTaskStatus(Long id, TaskStatus status) {
        Task existingTask = getTaskById(id);
        existingTask.setStatus(status);
        return taskRepository.save(existingTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }
}
