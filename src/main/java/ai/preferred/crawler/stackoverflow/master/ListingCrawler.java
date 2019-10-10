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
import ai.preferred.venom.storage.FileManager;
import ai.preferred.venom.storage.MysqlFileManager;
import ai.preferred.venom.validator.EmptyContentValidator;
import ai.preferred.venom.validator.StatusOkValidator;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ween Jiann Lee
 */
public class ListingCrawler {

  // Create session keys for things you would like to retrieve from handler
  static final Session.Key<AtomicInteger> LISTING_COUNT_kEY = new Session.Key<>();

  // Create session keys for CSV printer to print from handler
  static final Session.Key<EntityCSVStorage<Listing>> CSV_STORAGE_KEY = new Session.Key<>();

  // You can use this to log to console
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ListingCrawler.class);

  public static void main(String[] args) {

    // Get file to save to
    final String filename = "data/stackoverflow.csv";

    // Define config for MysqlFileManager
    final String host = "localhost";
    final int port = 3306;
    final String database = "filemanager";
    final String table = "responses";
    final String user = "root";
    final String password = "";
    final String dir = "C:\\";
    final String mysqlLocation = "jdbc:mysql://" + host + ":" + port + "/" + database;

    // Start CSV printer
    try (final EntityCSVStorage<Listing> printer = new EntityCSVStorage<>(filename);
         final FileManager fileManager = new MysqlFileManager(mysqlLocation, table, user, password, dir)
    ) {

      final AtomicInteger listingCount = new AtomicInteger();
      final Session session = Session.builder()
          .put(ListingCrawler.LISTING_COUNT_kEY, listingCount)
          .put(CSV_STORAGE_KEY, printer)
          .build();

      // Start crawler and FileManager
      try (Crawler crawler = createCrawler(createFetcher(fileManager), session).start()) {
        LOGGER.info("Starting crawler...");

        final String startUrl = "https://stackoverflow.com/jobs?l=Singapore&d=20&u=Km";

        // pass in URL and handler or use a HandlerRouter
        crawler.getScheduler().add(new VRequest(startUrl), new ListingHandler());
      } catch (Exception e) {
        LOGGER.error("Could not run crawler: ", e);
      }

      // We will retrieve all the listing here
      LOGGER.info("We have found {} listings!", listingCount.get());

    } catch (IOException e) {
      LOGGER.error("Unable to open file: {}, {}", filename, e);
    } catch (Exception e) {
      LOGGER.error("FileManager failed to close.", e);
    }

  }

  private static Fetcher createFetcher(FileManager fileManager) {
    // You can look in builder the different things you can add
    return AsyncFetcher.builder()
        .setValidator(
            new EmptyContentValidator(),
            new StatusOkValidator(),
            new ListingValidator())
        .setFileManager(fileManager)
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
