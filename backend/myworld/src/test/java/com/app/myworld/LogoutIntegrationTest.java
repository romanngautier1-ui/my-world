package com.app.myworld;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.security.JwtService;

@SpringBootTest
@Import(MailTestConfig.class)
class LogoutIntegrationTest {

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
    void logoutInvalidatesCurrentJwt() throws Exception {
        User user = User.builder()
            .email("logout@test.local")
            .username("logout")
            .password(passwordEncoder.encode("password"))
            .role(Role.USER)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .tokenVersion(0)
            .build();

        User saved = userRepository.saveAndFlush(user);

        String jwtV0 = jwtService.generateToken(saved);

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtV0))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + jwtV0))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtV0))
            .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));
    }
}
