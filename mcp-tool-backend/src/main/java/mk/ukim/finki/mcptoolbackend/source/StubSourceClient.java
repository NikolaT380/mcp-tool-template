package mk.ukim.finki.mcptoolbackend.source;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * _TODO(student): Replace this bean with a real client for YOUR assigned website.
 */
@Component
@Slf4j
public class StubSourceClient implements SourceClient {
    private final SourceProperties sourceProperties;

    public StubSourceClient(SourceProperties sourceProperties) {
        this.sourceProperties = sourceProperties;
    }

    @Override
    public List<FetchedResource> search(String query, int limit) {
        int maxResults = (limit > 0) ? Math.min(limit, 50) : 10;
        List<FetchedResource> results = new ArrayList<>();
        String baseUrl = (sourceProperties.baseUrl() != null && !sourceProperties.baseUrl().isBlank())
                ? sourceProperties.baseUrl()
                : "https://www.izvor.net.mk";

        try {
            String searchUrl = (query == null || query.isBlank())
                ? baseUrl + "/results.php"
                : baseUrl + "/results.php?q=" + URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);

            log.info("Fetching resources from izvor.net.mk: {}", searchUrl);

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(12000)
                    .get();

            Elements articleLinks = doc.select("a[href*='article.php?id=']");
            log.info("Found {} article links on results page.", articleLinks.size());

            for (Element link : articleLinks) {
                if (results.size() >= maxResults) {
                    break;
                }
                String href = link.attr("href");
                String title = link.text().trim();

                if (title.isBlank() || title.equalsIgnoreCase("view") || title.equalsIgnoreCase("details")) {
                    continue;
                }
                String externalId = href;
                if (href.contains("id=")) {
                    externalId = href.substring(href.indexOf("id=") + 3);
                    if (externalId.contains("&")) {
                        externalId = externalId.substring(0, externalId.indexOf("&"));
                    }
                }

                final String currentExtId = externalId;
                if (results.stream().anyMatch(r -> r.externalId().equals(currentExtId))) {
                    continue;
                }

                String fullUrl = href.startsWith("http") ? href : baseUrl + "/" + href.replaceFirst("^/", "");

                String content = "";
                try {
                    Document articleDoc = Jsoup.connect(fullUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .timeout(6000)
                            .get();

                    Element abstractP = articleDoc.selectFirst("h3:contains(Abstract) + p");
                    if (abstractP != null && !abstractP.text().isBlank()) {
                        content = abstractP.text().trim();
                    }
                } catch (Exception ex) {
                    log.debug("Could not fetch abstract directly for {}, falling back to title.", fullUrl);
                }

                if (content.isBlank()) {
                    content = title;
                }

                results.add(new FetchedResource(
                        externalId,
                        title,
                        content,
                        fullUrl,
                        LocalDateTime.now()
                ));
            }

        } catch (Exception e) {
            log.error("Error scraping search results from {}: {}", baseUrl, e.getMessage());
        }

        return results;
    }

    @Override
    public FetchedResource fetch(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Resource URL must not be empty");
        }

        try {
            log.info("Fetching single article page: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(12000)
                    .get();

            String title = doc.title();
            Element heading = doc.selectFirst("article.card h2, h2, h1");
            if (heading != null && !heading.text().isBlank()) {
                title = heading.text().trim();
            }

            Element abstractP = doc.selectFirst("h3:contains(Abstract) + p");
            String content = (abstractP != null && !abstractP.text().isBlank())
                ? abstractP.text().trim()
                : (heading != null ? heading.text().trim() : doc.title());

            String externalId = url;
            if (url.contains("id=")) {
                externalId = url.substring(url.indexOf("id=") + 3);
                if (externalId.contains("&")) {
                    externalId = externalId.substring(0, externalId.indexOf("&"));
                }
            }

            return new FetchedResource(
                    externalId,
                    title,
                    content,
                    url,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            log.error("Failed to fetch full article at {}: {}", url, e.getMessage());
            throw new RuntimeException("Could not fetch article from " + url, e);
        }
    }
}
