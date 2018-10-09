package ai.preferred.crawler.iproperty.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Property {

  public static List<Object> getHeader() {
    final List<Object> result = new ArrayList<>();
    for (final Field field : Property.class.getDeclaredFields()) {
      result.add(field.getName());
    }
    return result;
  }

  private String url;
  private String title;
  private String price;
  private String address;
  private String type;
  private String area;
  private String psf;
  private Integer numBeds;
  private Integer numBaths;
  private String carpark;

  public void setUrl(String url) {
    this.url = url;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public void setPsf(String psf) {
    this.psf = psf;
  }

  public void setNumBeds(Integer numBeds) {
    this.numBeds = numBeds;
  }

  public void setNumBaths(Integer numBaths) {
    this.numBaths = numBaths;
  }

  public void setCarpark(String carpark) {
    this.carpark = carpark;
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return title;
  }

  public String getPrice() {
    return price;
  }

  public String getType() {
    return type;
  }

  public String getAddress() {
    return address;
  }

  public String getArea() {
    return area;
  }

  public Integer getNumBaths() {
    return numBaths;
  }

  public Integer getNumBeds() {
    return numBeds;
  }

  public String getCarpark() {
    return carpark;
  }

  public String getPsf() {
    return psf;
  }

  public List<Object> asList() {
    try {
      final List<Object> result = new ArrayList<>();
      for (final Field field : getClass().getDeclaredFields()) {
        result.add(field.get(this));
      }
      return result;
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to convert this entity to a list!", e);
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

}
