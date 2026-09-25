package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.analysis.LanguageDetector;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import mk.ukim.finki.mcptoolbackend.model.enums.SearchStatus;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.repository.SearchRunRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.SearchRunService;
import mk.ukim.finki.mcptoolbackend.source.SourceClient;
import mk.ukim.finki.mcptoolbackend.source.FetchedResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * _TODO(student): Implement this service.
 *
 * <p>The dependencies you will need are already injected: {@link SourceClient}
 * to reach the assigned website, {@link LanguageDetector} to score Macedonian
 * content, and the repositories to persist the run and its resources.</p>
 */
@Service
@Slf4j
public class SearchRunServiceImpl implements SearchRunService {
    private final SearchRunRepository searchRunRepository;
    private final ResourceRepository resourceRepository;
    private final SourceClient sourceClient;
    private final LanguageDetector languageDetector;

    public SearchRunServiceImpl(SearchRunRepository searchRunRepository,
                                ResourceRepository resourceRepository,
                                SourceClient sourceClient,
                                LanguageDetector languageDetector) {
        this.searchRunRepository = searchRunRepository;
        this.resourceRepository = resourceRepository;
        this.sourceClient = sourceClient;
        this.languageDetector = languageDetector;
    }

    @Override
    public List<SearchRun> findAll() {
        return searchRunRepository.findAll();
    }

    @Override
    public Optional<SearchRun> findById(Long id) {
        return searchRunRepository.findById(id);
    }

    @Override
    public SearchRun run(String query, int limit) {
        // _TODO(student):
        //  1. Save a new SearchRun(query) with status RUNNING and startedAt = now.
        //  2. sourceClient.search(query, limit) -> for each FetchedResource, build a
        //     Resource, set fetchedAt, wordCount, and
        //     macedonianConfidence = languageDetector.macedonianConfidence(content).
        //  3. Persist the resources, set resultCount, mark the run COMPLETED
        //     (finishedAt = now). On any failure, mark it FAILED and rethrow.

        String safeQuery = (query != null) ? query.trim() : "";
        log.info("Starting new search run for query: '{}' with limit: {}", safeQuery, limit);

        SearchRun searchRun = new SearchRun(safeQuery);
        searchRun.setStatus(SearchStatus.RUNNING);
        searchRun.setStartedAt(LocalDateTime.now());

        SearchRun savedRun = searchRunRepository.save(searchRun);

        try {
            List<FetchedResource> fetchedResources = sourceClient.search(safeQuery, limit);
            List<Resource> resourcesToSave = new ArrayList<>();

            for (FetchedResource hit : fetchedResources) {
                Resource resource = new Resource(
                        savedRun,
                        hit.externalId(),
                        hit.title(),
                        hit.content(),
                        hit.sourceUrl()
                );

                resource.setFetchedAt(LocalDateTime.now());

                String content = hit.content() != null ? hit.content() : "";
                if (content.isBlank()) {
                    resource.setWordCount(0);
                } else {
                    resource.setWordCount(content.trim().split("\\s+").length);
                }

                resource.setMacedonianConfidence(languageDetector.macedonianConfidence(content));
                resourcesToSave.add(resource);
            }

            if (!resourcesToSave.isEmpty()) {
                resourceRepository.saveAll(resourcesToSave);
            }

            savedRun.setFinishedAt(LocalDateTime.now());
            savedRun.setResultCount(resourcesToSave.size());
            savedRun.setStatus(SearchStatus.COMPLETED);
            log.info("Search run #{} completed successfully with {} resources.", savedRun.getId(), resourcesToSave.size());
            return searchRunRepository.save(savedRun);

        } catch (Exception e) {
            log.error("Search run #{} failed: {}", savedRun.getId(), e.getMessage(), e);
            savedRun.setStatus(SearchStatus.FAILED);
            savedRun.setFinishedAt(LocalDateTime.now());
            searchRunRepository.save(savedRun);
            throw new RuntimeException("Search run failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Resource> findResources(Long searchRunId) {
        return resourceRepository.findAllBySearchRunId(searchRunId);
    }
}
