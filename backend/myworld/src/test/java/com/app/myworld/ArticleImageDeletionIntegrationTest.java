package com.app.myworld;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.ArticleRepository;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.security.JwtService;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@Import(MailTestConfig.class)
class ArticleImageDeletionIntegrationTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private UserRepository userRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();

        articleRepository.deleteAll();
        userRepository.deleteAll();

        deleteUploadDirContents();
    }

    @Test
    void updateWithNewImageDeletesOldImageFromDisk() throws Exception {
        User adminAuthor = createUser(Role.ADMIN, "admin-article-update");
        String jwt = jwtService.generateToken(adminAuthor);

        MockMultipartFile firstImage = new MockMultipartFile(
                "imageFile",
                "article.png",
                "image/png",
                "first-image".getBytes()
        );

        MvcResult createResult = mockMvc.perform(multipart("/api/articles")
                        .file(firstImage)
                        .param("title", "Article test")
                        .param("content", "content")
                        .param("userId", String.valueOf(adminAuthor.getId()))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.urlImage", startsWith("http://localhost/api/uploads/")))
                .andExpect(jsonPath("$.urlImage", endsWith(".png")))
                .andReturn();

        String createBody = createResult.getResponse().getContentAsString();
        long articleId = JsonPath.parse(createBody).read("$.id", Number.class).longValue();
        String firstUrlImage = JsonPath.parse(createBody).read("$.urlImage", String.class);
        Path firstUpload = uploadPathFromUrl(firstUrlImage);
        assertThat(Files.exists(firstUpload)).isTrue();

        MockMultipartFile updatedImage = new MockMultipartFile(
                "imageFile",
                "article-updated.png",
                "image/png",
                "updated-image".getBytes()
        );

        MockMultipartHttpServletRequestBuilder patch = multipart("/api/articles/{id}", articleId)
                .file(updatedImage)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        MvcResult patchResult = mockMvc.perform(patch.header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urlImage", startsWith("http://localhost/api/uploads/")))
                .andExpect(jsonPath("$.urlImage", endsWith(".png")))
                .andReturn();

        String patchBody = patchResult.getResponse().getContentAsString();
        String updatedUrlImage = JsonPath.parse(patchBody).read("$.urlImage", String.class);
        Path updatedUpload = uploadPathFromUrl(updatedUrlImage);

        assertThat(Files.exists(updatedUpload)).isTrue();
        assertThat(Files.exists(firstUpload)).isFalse();
    }

    @Test
    void deleteRemovesStoredImageFile() throws Exception {
        User adminAuthor = createUser(Role.ADMIN, "admin-article-delete");
        String jwt = jwtService.generateToken(adminAuthor);

        MockMultipartFile image = new MockMultipartFile(
                "imageFile",
                "todelete.png",
                "image/png",
                "to-delete".getBytes()
        );

        MvcResult createResult = mockMvc.perform(multipart("/api/articles")
                        .file(image)
                        .param("title", "Article to delete")
                        .param("content", "content")
                        .param("userId", String.valueOf(adminAuthor.getId()))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isCreated())
                .andReturn();

        String body = createResult.getResponse().getContentAsString();
        long articleId = JsonPath.parse(body).read("$.id", Number.class).longValue();
        String urlImage = JsonPath.parse(body).read("$.urlImage", String.class);
        Path uploadFile = uploadPathFromUrl(urlImage);
        assertThat(Files.exists(uploadFile)).isTrue();

        mockMvc.perform(delete("/api/articles/{id}", articleId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        assertThat(Files.exists(uploadFile)).isFalse();
    }

    private User createUser(Role role, String username) {
        User user = User.builder()
                .email(username + "@test.local")
                .username(username)
                .password(passwordEncoder.encode("password"))
                .role(role)
                .isActive(true)
                .tokenVersion(0)
                .build();

        return userRepository.saveAndFlush(user);
    }

    private void deleteUploadDirContents() throws IOException {
        if (uploadDir == null || uploadDir.isBlank()) {
            return;
        }

        Path root = Path.of(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            return;
        }

        try (var stream = Files.list(root)) {
            stream.forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        }
    }

    private Path uploadPathFromUrl(String url) {
        if (url == null) {
            throw new IllegalArgumentException("url is null");
        }
        String uploadsSegment = "/api/uploads/";
        int idx = url.indexOf(uploadsSegment);
        if (idx < 0) {
            throw new IllegalArgumentException("url does not contain uploads segment");
        }
        String after = url.substring(idx + uploadsSegment.length());
        int slash = after.indexOf('/');
        String filename = (slash >= 0 ? after.substring(0, slash) : after).strip();
        if (filename.isEmpty()) {
            throw new IllegalArgumentException("empty upload filename");
        }
        return Path.of(uploadDir).toAbsolutePath().normalize().resolve(filename).normalize();
    }
}
