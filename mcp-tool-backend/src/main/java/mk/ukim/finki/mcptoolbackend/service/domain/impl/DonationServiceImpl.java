package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.VezilkaClient;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.repository.DonationBatchRepository;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.springframework.stereotype.Service;

/**
 * TODO(student): Implement this service.
 *
 * <p>Enforce the DRAFT -> APPROVED -> SUBMITTED lifecycle (throw
 * {@code InvalidDonationStateException} on illegal transitions), and on
 * {@code submit} build a {@code TextDonationRequest} per resource and call
 * {@link VezilkaClient}. Store the returned reference on the batch.</p>
 */
@Service
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
    public List<DonationBatch> findAll() {
        throw new UnsupportedOperationException("TODO(student): Implement DonationService.findAll().");
    }

    @Override
    public Optional<DonationBatch> findById(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationService.findById().");
    }

    @Override
    public DonationBatch create(List<Long> resourceIds) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationService.create().");
    }

    @Override
    public DonationBatch approve(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationService.approve().");
    }

    @Override
    public DonationBatch submit(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationService.submit().");
    }

    @Override
    public void refreshSubmittedStatuses() {
        throw new UnsupportedOperationException("TODO(student): Implement DonationService.refreshSubmittedStatuses().");
    }
}
