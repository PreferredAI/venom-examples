package ai.preferred.crawler.azure.translate;

import ai.preferred.crawler.iproperty.entity.Title;
import ai.preferred.venom.request.VRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Ween Jiann Lee
 */
public class TitleTranslateRequest extends VRequest {

  /**
   * This keeps track of the original titles that are being translated.
   */
  private final List<Title> titles;

  public TitleTranslateRequest(String url, List<Title> titles) {
    super(url);
    this.titles = titles;
  }

  public TitleTranslateRequest(String url, Map<String, String> headers, List<Title> titles) {
    super(url, headers);
    this.titles = titles;
  }

  private TitleTranslateRequest(Builder builder) {
    super(builder);
    this.titles = builder.titles;
  }

  public List<Title> getTitles() {
    return titles;
  }

  public static class Builder extends VRequest.Builder<Builder> {

    private List<Title> titles;

    private Builder(Method method, String url) {
      super(method, url);
    }

    public static Builder post(String url) {
      return new Builder(Method.POST, url);
    }

    public Builder setTitles(List<Title> titles) {
      this.titles = titles;
      return this;
    }

    public TitleTranslateRequest build() {
      return new TitleTranslateRequest(this);
    }
  }
}
