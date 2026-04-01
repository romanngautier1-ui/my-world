package com.app.myworld.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TextToHtmlFormatterTest {

    @Test
    void formatsParagraphsSeparatedByBlankLines() {
        String text = "Premier paragraphe sur\nplusieurs lignes.\n\nDeuxieme paragraphe.";

        String html = TextToHtmlFormatter.formatToHtmlParagraphs(text);

        assertThat(html)
                .contains("<p>Premier paragraphe sur plusieurs lignes.</p>")
                .contains("<p>Deuxieme paragraphe.</p>");
    }

    @Test
    void keepsDialogueLinesAsSeparateParagraphs() {
        String text = "Intro.\n\n— Bonjour.\n— Comment ca va ?\n\nFin.";

        String html = TextToHtmlFormatter.formatToHtmlParagraphs(text);

        assertThat(html).contains("<p>&mdash; Bonjour.</p>");
        assertThat(html).contains("<p>&mdash; Comment ca va ?</p>");
    }

    @Test
    void escapesHtml() {
        String text = "<script>alert(1)</script>";

        String html = TextToHtmlFormatter.formatToHtmlParagraphs(text);

        assertThat(html).contains("&lt;script&gt;alert(1)&lt;/script&gt;");
        assertThat(html).doesNotContain("<script>");
    }
}
