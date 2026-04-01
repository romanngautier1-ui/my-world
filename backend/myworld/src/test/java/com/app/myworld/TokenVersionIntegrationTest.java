package com.app.myworld;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.security.JwtService;

@SpringBootTest
@Import(MyworldApplicationTests.TestMailConfig.class)
class TokenVersionIntegrationTest {

    private MockMvc mockMvc;
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
        userRepository.deleteAll();
    }

    @Test
    void oldJwtIsRejectedAfterTokenVersionIncrement() throws Exception {
        User user = User.builder()
            .email("tokenversion@test.local")
            .username("tokenversion")
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
            .andExpect(status().isOk());

        saved.setTokenVersion(saved.getTokenVersion() + 1);
        User updated = userRepository.saveAndFlush(saved);

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtV0))
            .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));

        String jwtV1 = jwtService.generateToken(updated);

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtV1))
            .andExpect(status().isOk());
    }
}
