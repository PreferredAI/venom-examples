/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.preferred.crawler.stackoverflow.master;

import ai.preferred.crawler.EntityCSVStorage;
import ai.preferred.crawler.stackoverflow.entity.Listing;
import ai.preferred.venom.Crawler;
import ai.preferred.venom.Session;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.validator.EmptyContentValidator;
import ai.preferred.venom.validator.StatusOkValidator;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Ween Jiann Lee
 */
public class ListingCrawler {

  // Create session keys for things you would like to retrieve from handler
  static final Session.Key<ArrayList<Listing>> JOB_LIST_KEY = new Session.Key<>();

  // Create session keys for CSV printer to print from handler
  static final Session.Key<EntityCSVStorage<Listing>> CSV_STORAGE_KEY = new Session.Key<>();

  // You can use this to log to console
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ListingCrawler.class);

  public static void main(String[] args) {

    // Get file to save to
    final String filename = "data/stackoverflow.csv";

    // Start CSV printer
    try (final EntityCSVStorage<Listing> printer = new EntityCSVStorage<>(filename, Listing.class)) {

      // Let's init the session, this allows us to retrieve the array list in the handler
      final ArrayList<Listing> jobListing = new ArrayList<>();
      final Session session = Session.builder()
          .put(JOB_LIST_KEY, jobListing)
          .put(CSV_STORAGE_KEY, printer)
          .build();

      // Start crawler
      try (final Crawler crawler = createCrawler(createFetcher(), session).start()) {
        LOGGER.info("Starting crawler...");

        final String startUrl = "https://stackoverflow.com/jobs?l=Singapore&d=20&u=Km";

        // pass in URL and handler or use a HandlerRouter
        crawler.getScheduler().add(new VRequest(startUrl), new ListingHandler());
      } catch (Exception e) {
        LOGGER.error("Could not run crawler: ", e);
      }

      // We will retrieve all the listing here
      LOGGER.info("We have found {} listings!", jobListing.size());

    } catch (IOException e) {
      LOGGER.error("unable to open file: {}, {}", filename, e);
    }

  }

  private static Fetcher createFetcher() {
    // You can look in builder the different things you can add
    return AsyncFetcher.builder()
        .setValidator(
            new EmptyContentValidator(),
            new StatusOkValidator(),
            new ListingValidator())
        .build();
  }

  private static Crawler createCrawler(Fetcher fetcher, Session session) {
    // You can look in builder the different things you can add
    return Crawler.builder()
        .setFetcher(fetcher)
        .setSession(session)
        .build();
  }
}
