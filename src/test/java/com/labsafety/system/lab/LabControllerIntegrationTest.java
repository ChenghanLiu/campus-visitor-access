package com.labsafety.system.lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labsafety.system.auth.LoginRequest;
import com.labsafety.system.auth.RegisterRequest;
import com.labsafety.system.lab.dto.CreateLabRequest;
import com.labsafety.system.lab.dto.UpdateLabRequest;
import com.labsafety.system.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LabControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void adminCanCreateUpdateDeleteLab_studentCannotCreate() throws Exception {
        String adminToken = registerAndGetToken("admin1", "admin1@test.com", "ADMIN");
        String studentToken = registerAndGetToken("student1", "student1@test.com", "STUDENT");

        // student cannot create
        CreateLabRequest create = new CreateLabRequest();
        create.setName("Lab A");
        create.setType("CHEM");
        create.setLocation("Building A - 101");
        create.setCapacity(30);
        create.setOpenTime(LocalTime.of(8, 0));
        create.setCloseTime(LocalTime.of(22, 0));
        create.setManagerName("Alice");
        create.setManagerPhone("123");
        create.setManagerEmail("alice@test.com");
        create.setDetails("General chemistry lab");

        mockMvc.perform(post("/api/labs")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isForbidden());

        // admin create
        String createdBody = mockMvc.perform(post("/api/labs")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Lab A"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long labId = extractId(createdBody);

        // list should include it (authenticated)
        mockMvc.perform(get("/api/labs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        // admin update
        UpdateLabRequest update = new UpdateLabRequest();
        update.setName("Lab A2");
        update.setType("CHEM");
        update.setLocation("Building A - 102");
        update.setCapacity(40);
        update.setOpenTime(LocalTime.of(9, 0));
        update.setCloseTime(LocalTime.of(21, 0));
        update.setManagerName("Bob");
        update.setManagerPhone("456");
        update.setManagerEmail("bob@test.com");
        update.setDetails("Updated");

        mockMvc.perform(put("/api/labs/" + labId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lab A2"))
                .andExpect(jsonPath("$.location").value("Building A - 102"));

        // admin delete
        mockMvc.perform(delete("/api/labs/" + labId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // get should now 4xx
        mockMvc.perform(get("/api/labs/" + labId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is4xxClientError());
    }

    private String registerAndGetToken(String username, String email, String role) throws Exception {
        RegisterRequest register = new RegisterRequest(username, email, "password123", role);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest(username, "password123");

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractToken(body);
    }

    private String extractToken(String body) {
        int idx = body.indexOf("\"token\"");
        if (idx < 0) {
            throw new AssertionError("Token field not found in response: " + body);
        }
        int colon = body.indexOf(':', idx);
        int firstQuote = body.indexOf('"', colon + 1);
        int secondQuote = body.indexOf('"', firstQuote + 1);
        return body.substring(firstQuote + 1, secondQuote);
    }

    private Long extractId(String body) {
        int idx = body.indexOf("\"id\"");
        if (idx < 0) {
            throw new AssertionError("id field not found in response: " + body);
        }
        int colon = body.indexOf(':', idx);
        int end = body.indexOf(',', colon + 1);
        if (end < 0) {
            end = body.indexOf('}', colon + 1);
        }
        String num = body.substring(colon + 1, end).trim();
        return Long.parseLong(num);
    }
}
