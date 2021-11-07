package org.schors.gos;

public class ParseResult {
  private String[] rec7;
  private String[] rec5;
  private String[] rec3;
  private String[] rec2;

  public ParseResult() {
  }

  public String[] getRec7() {
    return rec7;
  }

  public void setRec7(String[] rec7) {
    this.rec7 = rec7;
  }

  public String[] getRec5() {
    return rec5;
  }

  public void setRec5(String[] rec5) {
    this.rec5 = rec5;
  }

  public String[] getRec3() {
    return rec3;
  }

  public void setRec3(String[] rec3) {
    this.rec3 = rec3;
  }

  public String[] getRec2() {
    return rec2;
  }

  public void setRec2(String[] rec2) {
    this.rec2 = rec2;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Вторник:\n")
      .append("Защита: ").append(rec2[3].trim()).append(", ").append(rec2[2].trim()).append("\n")
      .append("Атака: ").append(rec2[1].trim()).append(", ").append(rec2[0].trim()).append("\n")
      .append("Среда: \n")
      .append("Защита: ").append(rec3[3].trim()).append(", ").append(rec3[2].trim()).append("\n")
      .append("Атака: ").append(rec3[1].trim()).append(", ").append(rec3[0].trim()).append("\n")
      .append("Пятница: \n")
      .append("Защита: ").append(rec5[3].trim()).append(", ").append(rec5[2].trim()).append("\n")
      .append("Атака: ").append(rec5[1].trim()).append(", ").append(rec5[0].trim()).append("\n")
      .append("Воскресенье: \n")
      .append("Защита: ").append(rec7[3].trim()).append(", ").append(rec7[2].trim()).append("\n")
      .append("Атака: ").append(rec7[1].trim()).append(", ").append(rec7[0].trim()).append("\n");

    return sb.toString();
  }
}
