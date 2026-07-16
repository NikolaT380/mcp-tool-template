package mk.ukim.finki.mcptoolbackend.source;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real client for YOUR assigned website.
 */
@Component
public class StubSourceClient implements SourceClient {
    private final SourceProperties sourceProperties;

    public StubSourceClient(SourceProperties sourceProperties) {
        this.sourceProperties = sourceProperties;
    }

    @Override
    public List<FetchedResource> search(String query, int limit) {
        throw new UnsupportedOperationException("TODO(student): Implement SourceClient.search() for " + sourceProperties.name() + ".");
    }

    @Override
    public FetchedResource fetch(String url) {
        throw new UnsupportedOperationException("TODO(student): Implement SourceClient.fetch().");
    }
}
