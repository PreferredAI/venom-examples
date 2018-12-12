package ai.preferred.crawler.stackoverflow.master.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Paper {

  private final String name;

  private final String url;

  public Paper(String name, String url) {
    this.name = name;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
