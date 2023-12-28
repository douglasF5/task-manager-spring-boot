package com.douglasf.taskmanagerapp.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.douglasf.taskmanagerapp.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity createTask(@RequestBody TaskModel taskData, HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    taskData.setUserId((UUID) userId);

    var currentDate = LocalDateTime.now();
    var taskStartDate = taskData.getStartDate();
    var taskEndDate = taskData.getEndDate();

    if (taskEndDate.isAfter(taskStartDate)) {
      taskData.setEndDate(taskStartDate);
    }

    if (currentDate.isAfter(taskData.getStartDate())) {
      taskData.setStartDate(currentDate);
    }

    if (currentDate.isAfter(taskData.getEndDate())) {
      taskData.setEndDate(currentDate);
    }

    var savedTask = this.taskRepository.save(taskData);

    return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
  }

  @GetMapping("/")
  public List<TaskModel> listTasks(HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    var tasksQueryData = this.taskRepository.findByUserId((UUID) userId);
    return tasksQueryData;
  }

  @PutMapping("/{taskId}")
  public ResponseEntity updateTask(@RequestBody TaskModel taskData, HttpServletRequest request,
      @PathVariable UUID taskId) {

    /** Check if the task exists */
    var taskQueryData = this.taskRepository.findById(taskId).orElse(null);
    if (taskQueryData == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found.");
    }

    /** Check if the task belongs to a specific user */
    var userId = request.getAttribute("userId");
    if (!taskQueryData.getUserId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("No task with this \"Id: " + taskQueryData.getId() + "\" found for this user.");
    }

    /** Override null fields and update the task */
    Utils.mergeNonNullProperties(taskData, taskQueryData);
    var updatedTask = this.taskRepository.save(taskQueryData);

    return ResponseEntity.ok().body(updatedTask);
  }
}
