package mk.ukim.finki.mcptoolbackend.integration.vezilka;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * _TODO(student): Replace this bean with a real doniraj.vezilka.ai client.
 */
@Component
@Slf4j
public class StubVezilkaClient implements VezilkaClient {
    private final VezilkaProperties vezilkaProperties;

    public StubVezilkaClient(VezilkaProperties vezilkaProperties) {
        this.vezilkaProperties = vezilkaProperties;
    }

    @Override
    public DonationReceipt submitTextDonation(TextDonationRequest request) {
        if (request == null || request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Text donation content must not be empty.");
        }
        log.info("Submitting text donation to Vezilka ({}) for resource: '{}'", vezilkaProperties.baseUrl(), request.title());

        String reference = "vezilka-" + UUID.randomUUID().toString().substring(0, 8);
        String message = "Text donation successfully accepted for Vezilka corpus processing.";

        return new DonationReceipt(reference, message);
    }

    @Override
    public DonationStatus checkStatus(String vezilkaReference) {
        if (vezilkaReference == null || vezilkaReference.isBlank()) {
            return DonationStatus.FAILED;
        }
        log.info("Checking donation status for reference: {}", vezilkaReference);
        return DonationStatus.ACCEPTED;
    }
}
