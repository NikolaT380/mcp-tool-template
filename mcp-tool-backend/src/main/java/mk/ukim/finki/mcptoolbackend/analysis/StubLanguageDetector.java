package mk.ukim.finki.mcptoolbackend.analysis;

import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real Macedonian-language detector.
 */
@Component
public class StubLanguageDetector implements LanguageDetector {
    @Override
    public double macedonianConfidence(String text) {
        throw new UnsupportedOperationException("TODO(student): Implement LanguageDetector.macedonianConfidence().");
    }
}
