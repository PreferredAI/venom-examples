package ai.preferred.crawler.azure.translate;

import ai.preferred.crawler.EntityCSVReader;
import ai.preferred.crawler.azure.entity.Title;
import ai.preferred.venom.request.Request;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ween Jiann Lee
 */
public class TranslateIterator implements Iterator<Request> {

  private final static String TRANSLATE_API_URI = "https://api.cognitive.microsofttranslator.com";

  private final static String TRANSLATE_API_PATH = "/translate?api-version=3.0";

  private final EntityCSVReader<Title> titleEntityCSVReader;

  private final List<Title> titlesBuffer = new LinkedList<>();

  private final List<RequestBody> requestBuffer = new LinkedList<>();

  TranslateIterator(final EntityCSVReader<Title> titleEntityCSVReader) {
    this.titleEntityCSVReader = titleEntityCSVReader;
  }

  /**
   * Update buffer. Returns true if buffer is updated
   * and false otherwise.
   *
   * @return if buffer is updated
   */
  private boolean updateBuffer() {
    if (!isBufferEmpty()) {
      return false;
    }

    for (int i = 0; i < 25 && titleEntityCSVReader.hasNext(); i++) {
      final Title title = titleEntityCSVReader.next();
      titlesBuffer.add(title);
      requestBuffer.add(new RequestBody(title.getTitle()));
    }

    return !isBufferEmpty();
  }

  private void flushBuffer() {
    this.titlesBuffer.clear();
    this.requestBuffer.clear();
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean isBufferEmpty() {
    return titlesBuffer.isEmpty() || requestBuffer.isEmpty();
  }

  @Override
  public synchronized boolean hasNext() {
    return !isBufferEmpty() || updateBuffer();
  }

  @Override
  public synchronized Request next() {
    @SuppressWarnings("UnnecessaryLocalVariable")
    final List<Title> titles = titlesBuffer;
    final String content = new Gson().toJson(requestBuffer);
    flushBuffer();

    return TitleTranslateRequest.Builder.post(TRANSLATE_API_URI + TRANSLATE_API_PATH + "&to=en")
        .setTitles(titles)
        .setBody(content)
        .addHeader("X-ClientTraceId", java.util.UUID.randomUUID().toString())
        .build();
  }

  private static class RequestBody {

    final String Text;

    public RequestBody(String text) {
      this.Text = text;
    }
  }
}
