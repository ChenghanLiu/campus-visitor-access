package com.labsafety.system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labsafety.system.auth.LoginRequest;
import com.labsafety.system.auth.RegisterRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RbacSecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void studentTokenShouldNotAccessTeacherOrAdminEndpoints() throws Exception {
        String token = registerAndGetToken("student2", "student2@test.com", "STUDENT");

        mockMvc.perform(get("/api/reservations/pending")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/labs")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
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

        // Response shape: {"token":"..."} or {"jwt":"..."} etc.
        // Minimal parsing to avoid coupling:
        int idx = body.indexOf("\"token\"");
        if (idx < 0) {
            throw new AssertionError("Token field not found in response: " + body);
        }
        int colon = body.indexOf(':', idx);
        int firstQuote = body.indexOf('"', colon + 1);
        int secondQuote = body.indexOf('"', firstQuote + 1);
        return body.substring(firstQuote + 1, secondQuote);
    }
}
