package mk.ukim.finki.mcptoolbackend.service.application.impl;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.model.dto.CreateDonationBatchDto;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplayDonationBatchDto;
import mk.ukim.finki.mcptoolbackend.service.application.DonationApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.springframework.stereotype.Service;

/**
 * TODO(student): Implement this service. Delegate to {@link DonationService}
 * and map with {@code DisplayDonationBatchDto.from(...)}.
 */
@Service
public class DonationApplicationServiceImpl implements DonationApplicationService {
    private final DonationService donationService;

    public DonationApplicationServiceImpl(DonationService donationService) {
        this.donationService = donationService;
    }

    @Override
    public List<DisplayDonationBatchDto> findAll() {
        throw new UnsupportedOperationException("TODO(student): Implement DonationApplicationService.findAll().");
    }

    @Override
    public Optional<DisplayDonationBatchDto> findById(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationApplicationService.findById().");
    }

    @Override
    public Optional<DisplayDonationBatchDto> create(CreateDonationBatchDto request) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationApplicationService.create().");
    }

    @Override
    public Optional<DisplayDonationBatchDto> approve(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationApplicationService.approve().");
    }

    @Override
    public Optional<DisplayDonationBatchDto> submit(Long id) {
        throw new UnsupportedOperationException("TODO(student): Implement DonationApplicationService.submit().");
    }
}
