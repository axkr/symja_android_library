package org.matheclipse.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class JSONSubPod {

  @JsonProperty("plaintext")
  String plaintext;

  @JsonProperty("sinput")
  String sinput;

  @JsonProperty("mathml")
  String mathml;

  @JsonProperty("latex")
  String latex;

  @JsonProperty("markdown")
  String markdown;

  @JsonProperty("mathcell")
  String mathcell;

  @JsonProperty("jsxgraph")
  String jsxgraph;

  @JsonProperty("plotly")
  String plotly;

  @JsonProperty("html")
  String html;

  @JsonProperty("visjs")
  String visjs;

  public JSONSubPod() {

  }

  /**
   * 
   * @return if available get the requested <code>html</code> data. If not available return an empty
   *         string.
   */
  public String getHtml() {
    return html != null ? html : "";
  }

  /**
   * 
   * @return if available get the requested <code>jsxgraph</code> data. If not available return an
   *         empty string.
   */
  public String getJsxgraph() {
    return jsxgraph != null ? jsxgraph : "";
  }

  /**
   * 
   * @return if available get the requested <code>latex</code> data. If not available return an
   *         empty string.
   */
  public String getLatex() {
    return latex != null ? latex : "";
  }

  /**
   * 
   * @return if available get the requested <code>markdown</code> data. If not available return an
   *         empty string.
   */
  public String getMarkdown() {
    return markdown != null ? markdown : "";
  }

  /**
   * 
   * @return if available get the requested <code>mathcell</code> data. If not available return an
   *         empty string.
   */
  public String getMathcell() {
    return mathcell != null ? mathcell : "";
  }

  /**
   * 
   * @return if available get the requested <code>mathml</code> data. If not available return an
   *         empty string.
   */
  public String getMathml() {
    return mathml != null ? mathml : "";
  }

  /**
   * 
   * @return if available get the requested <code>plaintext</code> data. If not available return an
   *         empty string.
   */
  public String getPlaintext() {
    return plaintext != null ? plaintext : "";
  }

  /**
   * 
   * @return if available get the requested <code>plotly</code> data. If not available return an
   *         empty string.
   */
  public String getPlotly() {
    return plotly != null ? plotly : "";
  }

  /**
   * 
   * @return if available get the requested <code>sinput</code> (Symja input) data. If not available
   *         return an empty string.
   */
  public String getSinput() {
    return sinput != null ? sinput : "";
  }

  /**
   * 
   * @return if available get the requested <code>visjs</code> data. If not available return an
   *         empty string.
   */
  public String getVisjs() {
    return visjs != null ? visjs : "";
  }
}
