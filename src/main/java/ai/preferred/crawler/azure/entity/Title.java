package ai.preferred.crawler.azure.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class allows you to store your entities. Define the
 * properties of your entities in this class.
 */
public class Title {

  private String id;

  private String title;

  private String category;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
