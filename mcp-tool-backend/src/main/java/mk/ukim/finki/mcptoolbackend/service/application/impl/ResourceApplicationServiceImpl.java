package mk.ukim.finki.mcptoolbackend.service.application.impl;

import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplayResourceDto;
import mk.ukim.finki.mcptoolbackend.model.dto.ResourceFilterDto;
import mk.ukim.finki.mcptoolbackend.service.application.ResourceApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * _TODO(student): Implement this service. Build a {@code Pageable} (e.g.
 * {@code PageRequest.of(page, size)}), delegate to {@link ResourceService},
 * and map with {@code DisplayResourceDto.from(...)} (use {@code Page.map}).
 */
@Service
public class ResourceApplicationServiceImpl implements ResourceApplicationService {
    private final ResourceService resourceService;

    public ResourceApplicationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Page<DisplayResourceDto> findAll(ResourceFilterDto filter, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = (size > 0) ? Math.min(size, 100) : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resourcePage = resourceService.findAll(filter, pageable);
        return resourcePage.map(DisplayResourceDto::from);
    }

    @Override
    public Optional<DisplayResourceDto> findById(Long id) {
        return resourceService.findById(id).map(DisplayResourceDto::from);
    }

    @Override
    public Optional<DisplayResourceDto> deleteById(Long id) {
        return resourceService.deleteById(id).map(DisplayResourceDto::from);
    }

    @Override
    public Optional<DisplayResourceDto> analyze(Long id) {
        try {
            Resource resource = resourceService.analyze(id);
            return Optional.of(DisplayResourceDto.from(resource));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
