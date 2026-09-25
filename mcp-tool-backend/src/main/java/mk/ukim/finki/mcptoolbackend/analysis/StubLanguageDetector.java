package mk.ukim.finki.mcptoolbackend.analysis;

import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * _TODO(student): Replace this bean with a real Macedonian-language detector.
 */
@Component
public class StubLanguageDetector implements LanguageDetector {

    private static final Set<Character> MK_SPECIFIC_LETTERS = Set.of(
            'ѓ', 'Ѓ', 'ѕ', 'Ѕ', 'ј', 'Ј', 'љ', 'Љ', 'њ', 'Њ', 'ќ', 'Ќ', 'џ', 'Џ'
    );

    private static final Set<Character> NON_MK_CYRILLIC_LETTERS = Set.of(
            'ы', 'Ы', 'э', 'Э', 'ъ', 'Ъ', 'ё', 'Ё', 'щ', 'Щ', 'ћ', 'Ћ', 'ђ', 'Ђ',
            'і', 'І', 'є', 'Є', 'ю', 'Ю', 'я', 'Я', 'ў', 'Ў', 'ґ', 'Ґ', 'ї', 'Ї'
    );

    @Override
    public double macedonianConfidence(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }

        int totalLetters = 0;
        int cyrillicLetters = 0;
        int mkSpecificCount = 0;
        int nonMkCount = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                totalLetters++;
                if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                    cyrillicLetters++;
                    if (MK_SPECIFIC_LETTERS.contains(c)) {
                        mkSpecificCount++;
                    } else if (NON_MK_CYRILLIC_LETTERS.contains(c)) {
                        nonMkCount++;
                    }
                }
            }
        }

        if (totalLetters == 0) {
            return 0.0;
        }

        double cyrillicRatio = (double) cyrillicLetters / totalLetters;
        if (cyrillicRatio < 0.3) {
            return 0.0;
        }

        double confidence = cyrillicRatio * 0.8;
        if (mkSpecificCount > 0) {
            confidence += Math.min(0.2, mkSpecificCount * 0.04);
        }
        if (nonMkCount > 0) {
            confidence -= Math.min(0.3, nonMkCount * 0.06);
        }

        return Math.max(0.0, Math.min(1.0, Math.round(confidence * 100.0) / 100.0));
    }
}
