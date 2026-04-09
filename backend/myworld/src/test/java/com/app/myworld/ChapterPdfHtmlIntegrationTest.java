package com.app.myworld;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.app.myworld.model.Book;
import com.app.myworld.model.Chapter;
import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.BookRepository;
import com.app.myworld.repository.ChapterRepository;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.security.JwtService;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@Import(MailTestConfig.class)
class ChapterPdfHtmlIntegrationTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private ChapterRepository chapterRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();

        chapterRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        deleteUploadDirContents();
    }

    @Test
    void createWithPdfPrecomputesHtmlAndSetsServerPdfUrl() throws Exception {
        Book book = bookRepository.saveAndFlush(createBook());
        String jwt = jwtService.generateToken(createUser(Role.ADMIN, "admin-create"));

        MockMultipartFile pdf = new MockMultipartFile(
                "pdfFile",
                "chapter.pdf",
                "application/pdf",
                createPdfBytes("Hello PDF")
        );

        mockMvc.perform(multipart("/api/chapters")
                        .file(pdf)
                        .param("title", "Chapitre 1")
                        .param("number", "1")
                        .param("bookId", String.valueOf(book.getId()))
                        .param("pdfUrl", "http://evil.test/api/uploads/evil.pdf")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pdfUrl", allOf(
                        startsWith("http://localhost/api/uploads/"),
                        endsWith(".pdf")
                )))
                .andExpect(jsonPath("$.content", allOf(
                        containsString("<p>"),
                        containsString("Hello PDF")
                )));
    }

    @Test
    void patchWithPdfOnlyIsAcceptedAndUpdatesPdfUrlAndContentHtml() throws Exception {
        Book book = bookRepository.saveAndFlush(createBook());
        String jwt = jwtService.generateToken(createUser(Role.ADMIN, "admin-patch"));

        Chapter initial = new Chapter();
        initial.setTitle("Chapitre initial");
        initial.setNumber(1);
        initial.setContent("Texte initial");
        initial.setBook(book);
        initial = chapterRepository.saveAndFlush(initial);

        MockMultipartFile pdf = new MockMultipartFile(
                "pdfFile",
                "updated.pdf",
                "application/pdf",
                createPdfBytes("Updated PDF")
        );

        MockMultipartHttpServletRequestBuilder patch = multipart("/api/chapters/{id}", initial.getId())
                .file(pdf)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        mockMvc.perform(patch.header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pdfUrl", allOf(
                        startsWith("http://localhost/api/uploads/"),
                        endsWith(".pdf")
                )))
                .andExpect(jsonPath("$.content", allOf(
                        containsString("<p>"),
                        containsString("Updated PDF")
                )));
    }

    @Test
    void pdfDownloadReturnsStoredPdfBytes() throws Exception {
        Book book = bookRepository.saveAndFlush(createBook());
        String jwt = jwtService.generateToken(createUser(Role.ADMIN, "admin-download"));

        MockMultipartFile pdf = new MockMultipartFile(
                "pdfFile",
                "download.pdf",
                "application/pdf",
                createPdfBytes("Download PDF")
        );

        MvcResult createResult = mockMvc.perform(multipart("/api/chapters")
                        .file(pdf)
                        .param("title", "Chapitre")
                        .param("number", "1")
                        .param("bookId", String.valueOf(book.getId()))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        long chapterId = JsonPath.parse(responseBody).read("$.id", Number.class).longValue();

        MvcResult downloadResult = mockMvc.perform(get("/api/chapters/{id}/pdf", chapterId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andReturn();

        assertThat(downloadResult.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        byte[] bytes = downloadResult.getResponse().getContentAsByteArray();
        assertThat(bytes).isNotEmpty();
        assertThat(new String(bytes, 0, Math.min(bytes.length, 5))).startsWith("%PDF-");
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

    private static Book createBook() {
        Book book = new Book();
        book.setTitle("Book test");
        book.setNumber(1);
        book.setDescription("desc");
        return book;
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

    private static byte[] createPdfBytes(String text) {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                cs.newLineAtOffset(72, 720);
                cs.showText(text);
                cs.endText();
            }

            doc.save(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create test PDF", ex);
        }
    }
}
