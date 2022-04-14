package com.example.demo.controller

import com.example.demo.persistence.TodoEntity
import com.example.demo.repository.TodoRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@AutoConfigureMockMvc
@SpringBootTest
class TodoControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val todoRepository: TodoRepository
) {

    @BeforeEach
    fun clearDatabase() {
        todoRepository.deleteAll()
    }

    private fun aTodo(
        task: String = "a sample task",
        completed: Boolean = false
    ) = TodoEntity(
        task = task,
        completed = completed
    )

    private val testData = listOf(
        aTodo(task = "cleaning up", completed = true),
        aTodo(task = "cooking a meal"),
        aTodo(task = "coding kotlin"),
    )

    @Test
    fun `can put todo`() {
        mockMvc.put("/todo") {
            contentType = MediaType.APPLICATION_JSON
            content = TodoRequest(task = "test").asJsonString()
        }.andExpect {
            status { isOk() }
        }
    }

    private fun TodoRequest.asJsonString() =
        jacksonObjectMapper().writeValueAsString(this)

    @Test
    fun `can remove todo`() {
        val persistedTestData = todoRepository.saveAll(testData)

        mockMvc.delete("/todo/${persistedTestData.last().id}")
            .andExpect {
                status { isOk() }
            }
        Assertions.assertThat(todoRepository.findAll().map { it.task })
            .containsExactly(
                persistedTestData[0].task,
                persistedTestData[1].task,
            )
    }

    @Test
    fun `can get all todo`() {
        val responseBody = mockMvc.get("/todo/all")
            .andExpect {
                status { isOk() }
            }.andReturn().response.let {
                jacksonObjectMapper().readValue<List<TodoResponse>>(it.contentAsString)
            }
        Assertions.assertThat(responseBody.size).isGreaterThanOrEqualTo(3)
    }

    @Test
    fun `can get incomplete todo`() {
        val responseBody = mockMvc.get("/todo/incomplete")

            .andExpect {
                status { isOk() }
            }.andReturn().response.let {
                jacksonObjectMapper().readValue<List<TodoResponse>>(it.contentAsString)
            }
        Assertions.assertThat(responseBody.size).isGreaterThanOrEqualTo(0)
    }

    @Test
    fun `can a todo by id`() {
        val persistedTestData: MutableList<TodoEntity> = todoRepository.saveAll(testData)

        mockMvc.get("/todo/${persistedTestData[1].id}")
            .andExpect {
                status { isOk() }
                content {
                    jsonPath("task", Matchers.containsString("cooking a meal"))
                }
            }
    }

    @Test
    fun `can delete completed tasks`() {
        val persistedTestData = todoRepository.saveAll(testData)
    }
}
