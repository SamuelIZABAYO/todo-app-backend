package com.example.demo.controller

import com.example.demo.persistence.TodoEntity
import com.example.demo.service.TodoService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todo")
class TodoController(
    private val todoService: TodoService
) {
    @PutMapping
    fun putTodo(
        @RequestBody todoRequest: TodoRequest
    ) {
        todoService.createOrUpdate((todoRequest.toEntity()))
    }

    @GetMapping("/all")
    fun getAll(): List<TodoResponse> = todoService.getAll().toResponse()

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
)

fun TodoEntity.toResponse() =
    TodoResponse(id, task, completed)

fun List<TodoEntity>.toResponse() =
    map { it.toResponse() }
