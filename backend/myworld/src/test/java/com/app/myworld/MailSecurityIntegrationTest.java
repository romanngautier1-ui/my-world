package com.app.myworld;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.security.JwtService;

@SpringBootTest
@Import(MailTestConfig.class)
class MailSecurityIntegrationTest {

    private MockMvc mockMvc;
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
            .build();
        userRepository.deleteAll();
    }

    @Test
    void sendIsUnauthorizedWithoutJwt() throws Exception {
        mockMvc.perform(post("/api/mails/send")
                .contentType(APPLICATION_JSON)
                .content("{\"recipient\":\"a@test.local\",\"subject\":\"s\",\"message\":\"m\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void sendIsForbiddenForNonAdmin() throws Exception {
        String jwt = jwtService.generateToken(createUser(Role.USER, "user1"));

        mockMvc.perform(post("/api/mails/send")
                .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .content("{\"recipient\":\"a@test.local\",\"subject\":\"s\",\"message\":\"m\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void receiveIsPublic() throws Exception {
        mockMvc.perform(post("/api/mails/receive")
                .contentType(APPLICATION_JSON)
                .content("{\"recipient\":\"user@test.local\",\"subject\":\"Hello\",\"message\":\"Hi\"}"))
            .andExpect(status().isOk());
    }

    private User createUser(Role role, String username) {
        User user = User.builder()
            .email(username + "@test.local")
            .username(username)
            .password(passwordEncoder.encode("password"))
            .role(role)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .tokenVersion(0)
            .build();

        return userRepository.saveAndFlush(user);
    }
}
