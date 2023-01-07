package org.matheclipse.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class JSONPod {

  @JsonProperty("title")
  String title;
  @JsonProperty("scanner")
  String scanner;
  @JsonProperty("error")
  String error;
  @JsonProperty("numsubpods")
  int numsubpods;
  @JsonProperty("subpods")
  JSONSubPod[] subpods;


  public JSONPod() {

  }

  public String getError() {
    return error;
  }

  public int numberOfSubpods() {
    return numsubpods;
  }

  public String getScanner() {
    return scanner;
  }

  public JSONSubPod[] getSubpods() {
    return subpods;
  }

  public String getTitle() {
    return title;
  }
}
