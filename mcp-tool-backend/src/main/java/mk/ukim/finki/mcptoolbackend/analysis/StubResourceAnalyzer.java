package mk.ukim.finki.mcptoolbackend.analysis;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * _TODO(student): Replace this bean with a real analyzer.
 */
@Component
public class StubResourceAnalyzer implements ResourceAnalyzer {
    private static final Pattern SENTENCE_SPLIT = Pattern.compile("[.!?]+(\\s+|$)");
    private static final Pattern WORD_SPLIT = Pattern.compile("[\\s\\p{Punct}&&[^_-]]+");

    private static final Set<String> STOP_WORDS = Set.of(
            "и", "во", "на", "се", "за", "со", "од", "да", "не", "по", "што", "како", "до",
            "или", "ова", "тие", "тој", "таа", "тоа", "сите", "сме", "сте", "беше", "може",
            "при", "е", "ја", "го", "ги", "им", "му", "ни", "ви", "кој", "која", "кое", "кои",
            "биде", "бидат", "само", "исто", "така", "тука", "па", "но", "ако", "бидејќи", "околу",
            "а", "под", "над", "пред", "зад", "позади", "меѓу", "кон", "без", "низ", "поради",
            "согласно", "според", "кај", "против", "вкупно", "веќе", "уште", "било", "биле"
    );

    @Override
    public AnalysisOutcome analyze(String title, String content) {
        String safeContent = (content != null) ? content.trim() : "";
        String safeTitle = (title != null) ? title.trim() : "";

        if (safeContent.isEmpty()) {
            return new AnalysisOutcome(
                    safeTitle.isEmpty() ? "Нема содржина за анализа." : safeTitle,
                    Collections.emptyList(),
                    0,
                    0
            );
        }

        String[] sentences = SENTENCE_SPLIT.split(safeContent);
        int sentenceCount = 0;
        for (String s : sentences) {
            if (!s.isBlank()) {
                sentenceCount++;
            }
        }
        if (sentenceCount == 0 && !safeContent.isBlank()) {
            sentenceCount = 1;
        }

        String[] rawWords = WORD_SPLIT.split(safeContent.toLowerCase());
        int wordCount = 0;
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String word : rawWords) {
            String cleanWord = word.trim();
            if (cleanWord.length() >= 2) {
                wordCount++;
                if (cleanWord.length() >= 4 && !STOP_WORDS.contains(cleanWord)) {
                    frequencyMap.put(cleanWord, frequencyMap.getOrDefault(cleanWord, 0) + 1);
                }
            }
        }

        List<String> keywords = frequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        String summary;
        if (sentences.length > 0 && !sentences[0].isBlank()) {
            StringBuilder sb = new StringBuilder(sentences[0].trim());
            if (sentences.length > 1 && !sentences[1].isBlank() && sb.length() < 180) {
                sb.append(". ").append(sentences[1].trim());
            }
            if (sb.charAt(sb.length() - 1) != '.') {
                sb.append(".");
            }
            summary = sb.toString();
        } else {
            summary = safeContent.length() > 250 ? safeContent.substring(0, 247) + "..." : safeContent;
        }

        return new AnalysisOutcome(summary, keywords, sentenceCount, wordCount);
    }
}
