package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.analysis.LanguageDetector;
import mk.ukim.finki.mcptoolbackend.analysis.ResourceAnalyzer;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.dto.ResourceFilterDto;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * TODO(student): Implement this service.
 *
 * <p>Build a {@code Specification<Resource>} from the non-null fields of the
 * {@link ResourceFilterDto} and call {@code resourceRepository.findAll(spec,
 * pageable)}. For {@code analyze}, delegate to {@link ResourceAnalyzer} and
 * {@link LanguageDetector}.</p>
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceAnalyzer resourceAnalyzer;
    private final LanguageDetector languageDetector;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               ResourceAnalyzer resourceAnalyzer,
                               LanguageDetector languageDetector) {
        this.resourceRepository = resourceRepository;
        this.resourceAnalyzer = resourceAnalyzer;
        this.languageDetector = languageDetector;
    }

    @Override
    public Page<Resource> findAll(ResourceFilterDto filter, Pageable pageable) {
        throw new UnsupportedOperationException("TODO(student): Implement ResourceService.findAll() with a Specification.");
    }

    @Override
    public Optional<Resource> findById(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement ResourceService.findById().");
    }

    @Override
    public Optional<Resource> deleteById(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement ResourceService.deleteById().");
    }

    @Override
    public Resource analyze(Long id) {
        // TODO(student):
        //  1. Load the resource (or throw ResourceNotFoundException).
        //  2. AnalysisOutcome outcome = resourceAnalyzer.analyze(title, content).
        //  3. Create/update a ResourceAnalysis from the outcome; set its
        //     macedonianConfidence from languageDetector; set analyzedAt = now.
        //  4. Persist and return the resource.
        throw new UnsupportedOperationException("TODO(student): Implement ResourceService.analyze().");
    }
}
