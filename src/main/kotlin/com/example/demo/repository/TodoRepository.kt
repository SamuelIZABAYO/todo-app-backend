package com.example.demo.repository

import com.example.demo.persistence.TodoEntity
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional


interface TodoRepository : JpaRepository<TodoEntity, Int> {
    fun findAllByCompleted(completed: Boolean): List<TodoEntity>
    @Transactional
    fun deleteAllByCompleted(completed: Boolean)
}
