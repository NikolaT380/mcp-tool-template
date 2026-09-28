package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.DonationReceipt;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.TextDonationRequest;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.VezilkaClient;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;
import mk.ukim.finki.mcptoolbackend.model.exception.DonationBatchNotFoundException;
import mk.ukim.finki.mcptoolbackend.model.exception.InvalidDonationStateException;
import mk.ukim.finki.mcptoolbackend.repository.DonationBatchRepository;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * _TODO(student): Implement this service.
 *
 * <p>Enforce the DRAFT -> APPROVED -> SUBMITTED lifecycle (throw
 * {@code InvalidDonationStateException} on illegal transitions), and on
 * {@code submit} build a {@code TextDonationRequest} per resource and call
 * {@link VezilkaClient}. Store the returned reference on the batch.</p>
 */
@Service
@Slf4j
public class DonationServiceImpl implements DonationService {
    private final DonationBatchRepository donationBatchRepository;
    private final ResourceRepository resourceRepository;
    private final VezilkaClient vezilkaClient;

    public DonationServiceImpl(DonationBatchRepository donationBatchRepository,
                               ResourceRepository resourceRepository,
                               VezilkaClient vezilkaClient) {
        this.donationBatchRepository = donationBatchRepository;
        this.resourceRepository = resourceRepository;
        this.vezilkaClient = vezilkaClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationBatch> findAll() {
        return donationBatchRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DonationBatch> findById(Long id) {
        return donationBatchRepository.findById(id);
    }

    @Override
    @Transactional
    public DonationBatch create(List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            throw new IllegalArgumentException("Resource ids list cannot be empty.");
        }

        List<Resource> resources = resourceRepository.findAllByIdIn(resourceIds);

        DonationBatch batch = new DonationBatch(DonationStatus.DRAFT);
        DonationBatch savedBatch = donationBatchRepository.save(batch);

        for (Resource resource : resources) {
            resource.setDonationBatch(savedBatch);
        }
        resourceRepository.saveAll(resources);
        savedBatch.setResources(new ArrayList<>(resources));

        return savedBatch;
    }

    @Override
    @Transactional
    public DonationBatch approve(Long id) {
        DonationBatch batch = donationBatchRepository.findById(id).orElseThrow(() -> new DonationBatchNotFoundException(id));

        if (batch.getStatus() != DonationStatus.DRAFT) {
            throw new InvalidDonationStateException(id, batch.getStatus());
        }

        batch.setStatus(DonationStatus.APPROVED);
        return donationBatchRepository.save(batch);
    }

    @Override
    @Transactional
    public DonationBatch submit(Long id) {
        DonationBatch batch = donationBatchRepository.findById(id).orElseThrow(() -> new DonationBatchNotFoundException(id));

        if (batch.getStatus() != DonationStatus.APPROVED) {
            throw new InvalidDonationStateException(id, batch.getStatus());
        }

        String determinedReference = batch.getResources().stream()
                .map(resource -> new TextDonationRequest(
                        resource.getTitle(),
                        resource.getContent(),
                        resource.getSourceUrl()
                ))
                .map(vezilkaClient::submitTextDonation)
                .map(DonationReceipt::reference)
                .reduce((first, second) -> second)
                .orElse("vezilka-batch-" + id);

        batch.setVezilkaReference(determinedReference);
        batch.setStatus(DonationStatus.SUBMITTED);
        batch.setSubmittedAt(LocalDateTime.now());

        return donationBatchRepository.save(batch);
    }

    @Override
    @Transactional
    public void refreshSubmittedStatuses() {
        List<DonationBatch> submittedBatches = donationBatchRepository.findAllByStatus(DonationStatus.SUBMITTED);

        for (DonationBatch batch : submittedBatches) {
            if (batch.getVezilkaReference() != null && !batch.getVezilkaReference().isBlank()) {
                try {
                    DonationStatus currentStatus = vezilkaClient.checkStatus(batch.getVezilkaReference());
                    if (currentStatus != null && currentStatus != batch.getStatus()) {
                        batch.setStatus(currentStatus);
                        donationBatchRepository.save(batch);
                    }
                } catch (Exception e) {
                    log.warn("Failed to refresh status for batch {}: {}", batch.getId(), e.getMessage());
                }
            }
        }
    }
}
