package com.example.demo.repository

import com.example.demo.persistence.TodoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<TodoEntity, Int> {
    fun findAllByCompleted(completed: Boolean): List<TodoEntity>
    fun deleteAllByCompleted(completed: Boolean)
}
