package com.app.myworld.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.util.HtmlUtils;

/**
 * Utilitaire qui convertit du texte brut en un sous-ensemble HTML simple et sûr.
 * <p>
 * Objectif : préserver la <b>structure du texte</b> (paragraphes et lignes de dialogue) plutôt que la mise en
 * page visuelle exacte.
 * C'est particulièrement utile après extraction depuis un PDF : on récupère généralement un flux de texte
 * avec des retours à la ligne, mais sans balises sémantiques explicites.
 * <p>
 * Règles de sortie (heuristiques actuelles) :
 * <ul>
 *   <li>Lignes vides ⇒ séparation de paragraphe (on « vide » le paragraphe courant).</li>
 *   <li>Lignes ressemblant à du dialogue (ex : commençant par « — » ou « - ») ⇒ 1 ligne = 1 {@code <p>}.</li>
 *   <li>Les lignes non-dialogue sont concaténées dans un même paragraphe, séparées par des espaces.</li>
 *   <li>Gestion simple des césures : si une ligne se termine par {@code '-'}, on supprime le tiret et on
 *       colle la ligne suivante sans espace.</li>
 * </ul>
 * <p>
 * Sécurité : le texte est échappé HTML via {@link HtmlUtils#htmlEscape(String)} avant d'être injecté dans
 * le HTML ; le résultat peut donc être rendu en sécurité via Angular {@code [innerHTML]} (pas d'injection de
 * scripts/balises).
 * <p>
 * Limitations (volontaires) :
 * <ul>
 *   <li>On n'essaie pas d'inférer des titres/listes/citations au-delà de l'heuristique « dialogue ».</li>
 *   <li>La détection de paragraphes repose sur les lignes vides et la règle de dialogue (pas d'analyse
 *       police/indentation ici).</li>
 *   <li>La gestion des césures est volontairement simple et peut être incorrecte dans certains cas.</li>
 * </ul>
 */
final class TextToHtmlFormatter {

    private TextToHtmlFormatter() {
    }

    /**
     * Convertit {@code rawText} en une suite de blocs HTML {@code <p>}.
     * <p>
     * L'intention est de conserver une structure lisible :
     * <ul>
     *   <li>des paragraphes pour le texte narratif</li>
     *   <li>un paragraphe par ligne pour les dialogues</li>
     * </ul>
     * <p>
     * Important : cette méthode ne renvoie jamais du HTML brut contrôlé par l'utilisateur. Tout est échappé.
     */
    static String formatToHtmlParagraphs(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        // Normalise les fins de ligne Windows/Mac « classique » vers Unix afin que le reste de
        // l'algorithme ne manipule qu'un seul séparateur "\n".
        String normalized = rawText.replace("\r\n", "\n").replace("\r", "\n");

        // Conserve les lignes vides de fin en utilisant limit = -1.
        // Cela rend le « flush » de paragraphe cohérent quand l'entrée se termine par des lignes vides.
        String[] lines = normalized.split("\n", -1);

        // On construit le HTML comme une liste de blocs <p>...</p>, puis on les joint avec '\n' à la fin.
        List<String> htmlParagraphs = new ArrayList<>();

        // Tampon pour le paragraphe narratif courant (plusieurs lignes non vides fusionnées).
        StringBuilder currentParagraph = new StringBuilder();

        // Indique si le tampon courant se termine par un tiret.
        // Sert à une petite correction de césure quand les PDFs coupent les mots en fin de ligne.
        boolean previousLineEndedWithHyphen = false;

        for (String line : lines) {
            // Défensif : l'extraction PDF ne devrait pas produire de lignes nulles, mais ça coûte peu de protéger.
            // strip() retire les espaces et les tabulations.
            String trimmed = line == null ? "" : line.strip();

            if (trimmed.isEmpty()) {
                // Ligne vide ⇒ séparation de paragraphe.
                // On « flush » le texte narratif accumulé en un <p>, puis on réinitialise l'état.
                flushParagraph(htmlParagraphs, currentParagraph);
                previousLineEndedWithHyphen = false;
                continue;
            }

            if (isDialogueLine(trimmed)) {
                // Ligne de dialogue ⇒ doit rester seule dans son propre paragraphe.
                // On flush aussi tout paragraphe narratif en cours avant d'ajouter la ligne de dialogue.
                flushParagraph(htmlParagraphs, currentParagraph);
                
                // htmlParagraphs.add(wrapP(trimmed)); // PB -> il peut y avoir des dialogues sur plusieurs lignes -> à changer pour ne pas traiter indépendémment
                
                // previousLineEndedWithHyphen = false;
                // continue;
            }

            if (currentParagraph.isEmpty()) {
                // Première ligne d'un paragraphe narratif.
                currentParagraph.append(trimmed);
            } else {
                if (previousLineEndedWithHyphen && currentParagraph.charAt(currentParagraph.length() - 1) == '-') {
                    // Correction de césure :
                    //   "exem-" + "ple" devient "exemple".
                    // N'est appliquée que si le tampon se termine réellement par '-' et que l'état l'indique.
                    currentParagraph.setLength(currentParagraph.length() - 1);
                    currentParagraph.append(trimmed);
                } else {
                    // Cas par défaut : fusionne les lignes dans un même paragraphe, séparées par un seul espace.
                    currentParagraph.append(' ').append(trimmed);
                }
            }

            // Met à jour l'état pour l'itération suivante.
            previousLineEndedWithHyphen = currentParagraph.length() > 0
                    && currentParagraph.charAt(currentParagraph.length() - 1) == '-';
        }

        // Flush du dernier paragraphe narratif restant en fin d'entrée.
        flushParagraph(htmlParagraphs, currentParagraph);

        // Joint les paragraphes avec des retours à la ligne pour garder une sortie lisible
        // (HTML ignore les espaces/retours entre blocs).
        return String.join("\n", htmlParagraphs);
    }

    /**
     * Ajoute le tampon du paragraphe courant comme un bloc {@code <p>} (après échappement), puis le vide.
     * Si le tampon est vide, ne fait rien.
     */
    private static void flushParagraph(List<String> out, StringBuilder paragraph) {
        if (paragraph.isEmpty()) {
            return;
        }
        out.add(wrapP(paragraph.toString()));
        paragraph.setLength(0);
    }

    /**
     * Heuristique de détection des lignes de dialogue.
     * <p>
     * Beaucoup de textes français préfixent les dialogues avec un cadratin « — ».
     * On supporte aussi le demi-cadratin « – » et des motifs simples comme « - ».
     * <p>
     * Heuristique volontairement conservatrice : si on renvoie true, la ligne doit rester seule dans son {@code <p>}.
     */
    private static boolean isDialogueLine(String trimmedLine) {
        // Les dialogues français sont souvent préfixés par un cadratin.
        return trimmedLine.startsWith("—")
                || trimmedLine.startsWith("–")
                || trimmedLine.startsWith("- ")
                || trimmedLine.startsWith("-\t");
    }

    /**
     * Encapsule le texte dans {@code <p>} après l'avoir échappé en HTML.
     * <p>
     * Note : {@link HtmlUtils#htmlEscape(String)} échappe des caractères comme {@code <}, {@code >}, {@code &},
     * et peut aussi convertir certaines ponctuations Unicode en entités nommées (ex : « — » ⇒ {@code &mdash;}).
     * C'est attendu et sans danger.
     */
    private static String wrapP(String text) {
        String safe = HtmlUtils.htmlEscape(text);
        return "<p>" + safe + "</p>";
    }
}
