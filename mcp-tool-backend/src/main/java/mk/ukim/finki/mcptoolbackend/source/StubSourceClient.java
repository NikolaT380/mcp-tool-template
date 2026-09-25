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
        String baseUrl = sourceProperties.baseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://www.izvor.net.mk";
        }

        try {
            String searchUrl;
            if (query == null || query.isBlank()) {
                searchUrl = baseUrl + "/results.php";
            } else {
                searchUrl = baseUrl + "/results.php?q=" + URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            }

            log.info("Fetching resources from izvor.net.mk: {}", searchUrl);

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(12000)
                    .get();

            Elements articleLinks = doc.select("a[href*='article.php?id=']");

            for (Element link : articleLinks) {
                if (results.size() >= maxResults) {
                    break;
                }
                String href = link.attr("href");
                String fullUrl = href.startsWith("http") ? href : baseUrl + "/" + href.replaceFirst("^/", "");
                String title = link.text().trim();

                String externalId = href;
                if (href.contains("id=")) {
                    externalId = href.substring(href.indexOf("id=") + 3);
                    if (externalId.contains("&")) {
                        externalId = externalId.substring(0, externalId.indexOf("&"));
                    }
                }

                final String currentExtId = externalId;
                if (title.isBlank() || results.stream().anyMatch(r -> r.externalId().equals(currentExtId))) {
                    continue;
                }

                Element parent = link.parent();
                String contentSnippet = "";
                if (parent != null && parent.parent() != null) {
                    contentSnippet = parent.parent().text();
                }
                if (contentSnippet.isBlank() || contentSnippet.length() < 30) {
                    contentSnippet = title;
                }

                results.add(new FetchedResource(
                        externalId,
                        title,
                        contentSnippet,
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
            Element heading = doc.selectFirst("h1, h2, .article-title");
            if (heading != null && !heading.text().isBlank()) {
                title = heading.text().trim();
            }

            Element contentElem = doc.selectFirst(".abstract, .article-content, #abstract, main, article");
            String content = (contentElem != null) ? contentElem.text() : doc.body().text();

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
