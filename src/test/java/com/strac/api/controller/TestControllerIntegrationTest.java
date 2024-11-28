package com.strac.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.strac.api.controller.TestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestController.class)
public class TestControllerIntegrationTest {

    @Autowired
    private TestController testController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Setup MockMvc with the controller
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void testSayHi() throws Exception {
        // Perform a GET request to /test
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())               // Expect HTTP 200 OK
                .andExpect(content().string("Hi!"));      // Expect the content to be "Hi!"
    }
}
