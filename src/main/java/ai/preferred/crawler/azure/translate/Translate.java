package ai.preferred.crawler.azure.translate;

import ai.preferred.crawler.EntityCSVReader;
import ai.preferred.crawler.EntityCSVStorage;
import ai.preferred.crawler.iproperty.entity.Title;
import ai.preferred.venom.Crawler;
import ai.preferred.venom.Session;
import ai.preferred.venom.SleepScheduler;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.job.LazyPriorityJobQueue;
import ai.preferred.venom.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Ween Jiann Lee
 */
public class Translate {

  /**
   * Create session keys for CSV printer to print from handler
   */
  static final Session.Key<EntityCSVStorage<Title>> STORAGE_KEY = new Session.Key<>();

  /**
   * Your Azure translation API key
   */
  private static final String TRANSLATE_KEY = "xxxxxxxxxx";

  /**
   * Path to the input file
   */
  private static final String INPUT_FILEPATH = "data/translate_input.csv";

  /**
   * Path to the completed file
   */
  private static final String COMPLETED_FILEPATH = "data/titles_old.csv";

  /**
   * Path to the output file (will be created if not exist, all data will be overridden)
   */
  private static final String OUTPUT_FILEPATH = "data/translate_output.csv";

  /**
   * You can use this to log to console
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Translate.class);

  private static Fetcher fetcher(Map<String, String> headers) {
    return AsyncFetcher.builder()
        .setHeaders(headers)
        .build();
  }

  private static Crawler crawler(Session session, Fetcher fetcher, Iterator<Request> iterator) {
    return Crawler.builder()
        .setSession(session)
        .setFetcher(fetcher)
        // Set the sleep time long enough to keep within Azure translate API rate limits
        .setSleepScheduler(new SleepScheduler(1000))
        .setMaxTries(5)
        // This will create jobs and send all responses to TranslateHandler
        .setJobQueue(new LazyPriorityJobQueue(iterator, new TranslationHandler()))
        .build();
  }

  public static void main(String[] args) {
    try (
        final EntityCSVReader<Title> input = new EntityCSVReader<>(INPUT_FILEPATH, Title.class);
        final EntityCSVStorage<Title> storage = new EntityCSVStorage<>(OUTPUT_FILEPATH);
        final EntityCSVReader<Title> completed = new EntityCSVReader<>(COMPLETED_FILEPATH, Title.class)
    ) {

      final Session session = Session.builder()
          .put(STORAGE_KEY, storage)
          .build();

      Map<String, String> headers = new HashMap<>();
      headers.put("Content-Type", "application/json");
      headers.put("Ocp-Apim-Subscription-Key", TRANSLATE_KEY);

      final Set<String> completedIds = new HashSet<>();
      completed.forEachRemaining(e -> completedIds.add(e.getId()));
      crawler(session, fetcher(headers), new TranslateIterator(input, completedIds)).startAndClose();

    } catch (Exception e) {
      LOGGER.error("An error occurred in the crawler", e);
    }
  }
}