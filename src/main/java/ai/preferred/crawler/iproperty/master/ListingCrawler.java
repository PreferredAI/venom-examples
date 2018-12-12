package ai.preferred.crawler.iproperty.master;

import ai.preferred.crawler.EntityCSVStorage;
import ai.preferred.crawler.iproperty.entity.Property;
import ai.preferred.venom.Crawler;
import ai.preferred.venom.Session;
import ai.preferred.venom.SleepScheduler;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.validator.EmptyContentValidator;
import ai.preferred.venom.validator.StatusOkValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ListingCrawler {

  static final Session.Key<EntityCSVStorage<Property>> STORAGE_KEY = new Session.Key<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(ListingCrawler.class);

  public static void main(String[] args) {
    final String filename = "data/iproperty.csv";
    try (final EntityCSVStorage<Property> storage = new EntityCSVStorage<>(filename, Property.class)) {

      final Session session = Session.builder()
          .put(STORAGE_KEY, storage)
          .build();

      try (final Crawler crawler = createCrawler(createFetcher(), session).start()) {
        LOGGER.info("starting crawler...");

        final String startUrl = "https://www.iproperty.com.sg/rent/list/";
        crawler.getScheduler().add(new VRequest(startUrl), new ListingHandler());
      } catch (Exception e) {
        LOGGER.error("Could not run crawler: ", e);
      }

    } catch (IOException e) {
      LOGGER.error("unable to open file: {}, {}", filename, e);
    }
  }

  private static Fetcher createFetcher() {
    return AsyncFetcher.builder()
        .validator(
            EmptyContentValidator.INSTANCE,
            StatusOkValidator.INSTANCE,
            new ListingValidator())
        .build();
  }

  private static Crawler createCrawler(Fetcher fetcher, Session session) {
    return Crawler.builder()
        .fetcher(fetcher)
        .session(session)
        // Just to be polite
        .sleepScheduler(new SleepScheduler(1500, 3000))
        .build();
  }

}
