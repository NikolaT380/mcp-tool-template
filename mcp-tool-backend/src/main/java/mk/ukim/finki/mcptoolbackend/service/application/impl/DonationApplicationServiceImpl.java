package mk.ukim.finki.mcptoolbackend.service.application.impl;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.model.dto.CreateDonationBatchDto;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplayDonationBatchDto;
import mk.ukim.finki.mcptoolbackend.service.application.DonationApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.springframework.stereotype.Service;

/**
 * _TODO(student): Implement this service. Delegate to {@link DonationService}
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
        return DisplayDonationBatchDto.from(donationService.findAll());
    }

    @Override
    public Optional<DisplayDonationBatchDto> findById(Long id) {
        return donationService.findById(id).map(DisplayDonationBatchDto::from);
    }

    @Override
    public Optional<DisplayDonationBatchDto> create(CreateDonationBatchDto request) {
        if (request == null || request.resourceIds() == null || request.resourceIds().isEmpty()) {
            return Optional.empty();
        }
        try {
            DonationBatch batch = donationService.create(request.resourceIds());
            return Optional.of(DisplayDonationBatchDto.from(batch));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<DisplayDonationBatchDto> approve(Long id) {
        try {
            DonationBatch batch = donationService.approve(id);
            return Optional.of(DisplayDonationBatchDto.from(batch));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<DisplayDonationBatchDto> submit(Long id) {
        try {
            DonationBatch batch = donationService.submit(id);
            return Optional.of(DisplayDonationBatchDto.from(batch));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
