package com.github.MASIJUN99.mlog.sample.model;

import java.io.Serializable;

public class TestModel implements Serializable {

  private Long id;

  private String key;

  private String value;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
