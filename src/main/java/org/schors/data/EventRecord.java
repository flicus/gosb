package org.schors.data;

public class EventRecord {
  private String date;
  private String value;

  public EventRecord() {
  }

  public EventRecord(String date, String value) {
    this.date = date;
    this.value = value;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "EventRecord [date=" + date + ", value=" + value + "]";
  }

}
