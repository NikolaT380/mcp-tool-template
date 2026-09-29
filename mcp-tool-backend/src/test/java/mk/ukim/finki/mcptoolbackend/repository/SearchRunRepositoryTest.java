package mk.ukim.finki.mcptoolbackend.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import mk.ukim.finki.mcptoolbackend.config.JpaConfig;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import mk.ukim.finki.mcptoolbackend.model.enums.SearchStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * _TODO(student): Implement repository tests for search runs, following the
 * working {@link UserRepositoryTest} example (real Postgres via Testcontainers,
 * migrated by Flyway). Remove the {@code @Disabled} once you add assertions.
 */
//@Disabled("_TODO(student): implement SearchRun repository tests")

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
@Transactional
@Testcontainers
public class SearchRunRepositoryTest {
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
    private SearchRunRepository searchRunRepository;

    @Test
    void testFindAllByOrderByCreatedAtDesc() throws InterruptedException {
        // _TODO(student): save a few SearchRuns and assert the ordering.

        SearchRun first = new SearchRun("query1");
        first.setStatus(SearchStatus.COMPLETED);
        first.setResultCount(5);
        searchRunRepository.saveAndFlush(first);

        Thread.sleep(100);

        SearchRun second = new SearchRun("query2");
        second.setStatus(SearchStatus.COMPLETED);
        second.setResultCount(10);
        searchRunRepository.saveAndFlush(second);

        Thread.sleep(100);

        SearchRun third = new SearchRun("query3");
        third.setStatus(SearchStatus.COMPLETED);
        third.setResultCount(15);
        searchRunRepository.saveAndFlush(third);

        List<SearchRun> results = searchRunRepository.findAllByOrderByCreatedAtDesc();

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getQuery()).isEqualTo("query3");
        assertThat(results.get(1).getQuery()).isEqualTo("query2");
        assertThat(results.get(2).getQuery()).isEqualTo("query1");
    }
}
