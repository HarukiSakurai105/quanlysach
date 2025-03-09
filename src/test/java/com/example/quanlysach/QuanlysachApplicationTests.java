package com.example.quanlysach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class QuanlysachApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testListBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    void testShowForm() throws Exception {
        mockMvc.perform(get("/books/form"))
                .andExpect(status().isOk());
    }
}
