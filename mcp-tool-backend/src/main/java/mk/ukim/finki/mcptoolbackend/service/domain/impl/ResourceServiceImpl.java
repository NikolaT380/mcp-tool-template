package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.analysis.LanguageDetector;
import mk.ukim.finki.mcptoolbackend.analysis.ResourceAnalyzer;
import mk.ukim.finki.mcptoolbackend.analysis.AnalysisOutcome;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.domain.ResourceAnalysis;
import mk.ukim.finki.mcptoolbackend.model.dto.ResourceFilterDto;
import mk.ukim.finki.mcptoolbackend.model.exception.ResourceNotFoundException;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * _TODO(student): Implement this service.
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
    @Transactional(readOnly = true)
    public Page<Resource> findAll(ResourceFilterDto filter, Pageable pageable) {
        Specification<Resource> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                if (filter.searchRunId() != null) {
                    predicates.add(cb.equal(root.get("searchRun").get("id"), filter.searchRunId()));
                }
                if (filter.minMacedonianConfidence() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("macedonianConfidence"), filter.minMacedonianConfidence()));
                }
                if (filter.analyzed() != null) {
                    if (filter.analyzed()) {
                        predicates.add(cb.isNotNull(root.get("analysis")));
                    } else {
                        predicates.add(cb.isNull(root.get("analysis")));
                    }
                }
                if (filter.donated() != null) {
                    if (filter.donated()) {
                        predicates.add(cb.isNotNull(root.get("donationBatch")));
                    } else {
                        predicates.add(cb.isNull(root.get("donationBatch")));
                    }
                }
                if (filter.search() != null && !filter.search().isBlank()) {
                    String pattern = "%" + filter.search().trim().toLowerCase() + "%";
                    Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                    Predicate contentLike = cb.like(cb.lower(root.get("content")), pattern);
                    predicates.add(cb.or(titleLike, contentLike));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return resourceRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Resource> deleteById(Long id) {
        Optional<Resource> existing = resourceRepository.findById(id);
        existing.ifPresent(resourceRepository::delete);
        return existing;
    }

    @Override
    @Transactional
    public Resource analyze(Long id) {
        // _TODO(student):
        //  1. Load the resource (or throw ResourceNotFoundException).
        //  2. AnalysisOutcome outcome = resourceAnalyzer.analyze(title, content).
        //  3. Create/update a ResourceAnalysis from the outcome; set its
        //     macedonianConfidence from languageDetector; set analyzedAt = now.
        //  4. Persist and return the resource.

        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        AnalysisOutcome outcome = resourceAnalyzer.analyze(resource.getTitle(), resource.getContent());
        double confidence = languageDetector.macedonianConfidence(resource.getContent());
        resource.setMacedonianConfidence(confidence);
        if (outcome.wordCount() != null) {
            resource.setWordCount(outcome.wordCount());
        }

        ResourceAnalysis analysis = (resource.getAnalysis() != null) ? resource.getAnalysis() : new ResourceAnalysis();
        analysis.setResource(resource);
        analysis.setSummary(outcome.summary());
        analysis.setKeywords(outcome.keywords() != null ? String.join(", ", outcome.keywords()) : "");
        analysis.setSentenceCount(outcome.sentenceCount());
        analysis.setMacedonianConfidence(confidence);
        analysis.setAnalyzedAt(LocalDateTime.now());

        resource.setAnalysis(analysis);

        return resourceRepository.save(resource);
    }
}
