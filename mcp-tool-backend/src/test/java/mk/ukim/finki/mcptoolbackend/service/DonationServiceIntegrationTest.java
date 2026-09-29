package mk.ukim.finki.mcptoolbackend.service;

import java.util.List;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;
import mk.ukim.finki.mcptoolbackend.model.enums.SearchStatus;
import mk.ukim.finki.mcptoolbackend.repository.DonationBatchRepository;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.repository.SearchRunRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * _TODO(student): Implement an integration test for the donation workflow
 * (create -> approve -> submit) with a stubbed or fake VezilkaClient. Remove
 * the {@code @Disabled} once implemented.
 */
//@Disabled("_TODO(student): implement the donation workflow integration test")

@SpringBootTest
@Testcontainers
public class DonationServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("mcptool_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DonationService donationService;

    @Autowired
    private DonationBatchRepository donationBatchRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SearchRunRepository searchRunRepository;

    @Test
    void testDonationWorkflow() {
        // _TODO(student): create a batch, approve it, submit it, and assert the
        //  resulting status transitions and the stored Vezilka reference.

        SearchRun run = new SearchRun("тест");
        run.setStatus(SearchStatus.COMPLETED);
        run = searchRunRepository.save(run);

        Resource resource = new Resource(
                run,
                "ext-123",
                "Наслов",
                "Содржина",
                "https://www.izvor.net.mk/article.php?id=123"
        );
        resource = resourceRepository.save(resource);

        DonationBatch draftBatch = donationService.create(List.of(resource.getId()));
        assertThat(draftBatch).isNotNull();
        assertThat(draftBatch.getId()).isNotNull();
        assertThat(draftBatch.getStatus()).isEqualTo(DonationStatus.DRAFT);
        assertThat(draftBatch.getResources()).hasSize(1);

        DonationBatch approvedBatch = donationService.approve(draftBatch.getId());
        assertThat(approvedBatch.getStatus()).isEqualTo(DonationStatus.APPROVED);

        DonationBatch submittedBatch = donationService.submit(approvedBatch.getId());
        assertThat(submittedBatch.getStatus()).isEqualTo(DonationStatus.SUBMITTED);
        assertThat(submittedBatch.getSubmittedAt()).isNotNull();
        assertThat(submittedBatch.getVezilkaReference()).isNotNull();
        assertThat(submittedBatch.getVezilkaReference()).startsWith("vezilka-");

        DonationBatch fromDb = donationBatchRepository.findById(submittedBatch.getId()).orElseThrow();
        assertThat(fromDb.getStatus()).isEqualTo(DonationStatus.SUBMITTED);
        assertThat(fromDb.getVezilkaReference()).isEqualTo(submittedBatch.getVezilkaReference());
    }
}
