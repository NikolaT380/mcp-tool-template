package mk.ukim.finki.mcptoolbackend.analysis;

import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real analyzer.
 */
@Component
public class StubResourceAnalyzer implements ResourceAnalyzer {
    @Override
    public AnalysisOutcome analyze(String title, String content) {
        throw new UnsupportedOperationException("TODO(student): Implement ResourceAnalyzer.analyze().");
    }
}
