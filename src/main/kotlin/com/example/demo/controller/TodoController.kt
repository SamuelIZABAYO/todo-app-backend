package com.example.demo.controller

import com.example.demo.persistence.TodoEntity
import com.example.demo.service.TodoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/todo")
class TodoController(
    private val todoService: TodoService
) {
    @PostMapping("/create")
    fun putTodo(
        @RequestBody todoRequest: TodoRequest
    ): ResponseEntity<Unit> {
        val todo = todoRequest.id?.let {
            todoService.getById(it).apply {
                task = todoRequest.task
                completed = todoRequest.completed
            }
        } ?: todoRequest.toEntity()
        todoService.createOrUpdate(todo)
        return ResponseEntity.status(200).build()
    }

    @GetMapping("/all")
    fun getAll() = ResponseEntity
        .ok()
        .body(todoService.getAll().toResponse())

    @GetMapping("/{id}")
    fun getOneTodo(
        @PathVariable id: Int
    ): TodoResponse =
        todoService.getById(id).toResponse()

    @GetMapping("/incomplete")
    fun getIncompleteTodo(): List<TodoResponse> = todoService.getByCompleted(completed = false).toResponse()

    @DeleteMapping("/delete_completed")
    fun removeAllCompleted() {
        return todoService.removeAllCompleted()
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Int) {
        todoService.removeById(id)
    }
}

data class TodoRequest(
    val id: Int? = null,
    val task: String,
    val completed: Boolean = false
) {
    fun toEntity() = TodoEntity(
        task = task,
        completed = completed
    )
}

data class TodoResponse(
    val id: Int,
    val task: String,
    val completed: Boolean,
    val created: Date,
    val lastUpdated: Date
)

fun TodoEntity.toResponse() =
    TodoResponse(id, task, completed, createdDate, modifiedDate)

fun List<TodoEntity>.toResponse() =
    map { it.toResponse() }
