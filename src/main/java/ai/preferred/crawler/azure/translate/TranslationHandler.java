package ai.preferred.crawler.azure.translate;

import ai.preferred.crawler.EntityCSVStorage;
import ai.preferred.crawler.azure.entity.Title;
import ai.preferred.venom.Handler;
import ai.preferred.venom.Session;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.response.VResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ween Jiann Lee
 */
public class TranslationHandler implements Handler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TranslationHandler.class);

  @Override
  public void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker) {
    // Get the CSV printer we created
    final EntityCSVStorage<Title> storage = session.get(Translate.STORAGE_KEY);

    // Retrieve the titles that were sent for translation in this request
    final LinkedList<Title> originalTitles;
    if (request instanceof TitleTranslateRequest) {
      final TitleTranslateRequest titleTranslateRequest = (TitleTranslateRequest) request;
      originalTitles = new LinkedList<>(titleTranslateRequest.getTitles());
    } else {
      LOGGER.error("Wrong request type, unable to proceed. Quitting...");
      return;
    }

    // Get the response body
    final String html = response.getHtml();
    final JSONArray translatedArr = new JSONArray(html);

    final List<Title> translatedTitles = new ArrayList<>();
    translatedArr.forEach(translationJson -> {
      final String translatedStr = ((JSONObject) translationJson)
          .getJSONArray("translations")
          .getJSONObject(0)
          .getString("text");
      final Title originalTitle = originalTitles.poll();
      assert originalTitle != null;

      final Title title = new Title();
      title.setId(originalTitle.getId());
      title.setCategory(originalTitle.getCategory());
      title.setTitle(translatedStr);
      translatedTitles.add(title);

      LOGGER.info("Translated {} to {}.", originalTitle.getTitle(), translatedStr);
    });

    // Use this wrapper for every IO task, this maintains CPU utilisation to speed up requests
    worker.executeBlockingIO(() -> {
      for (final Title t : translatedTitles) {
        LOGGER.info("storing title: {}", t.getTitle());
        try {
          // Store row to CSV
          storage.append(t);
        } catch (IOException e) {
          LOGGER.error("Unable to store listing.", e);
        }
      }
    });

  }
}
