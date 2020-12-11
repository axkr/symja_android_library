package com.twosigma.beakerx.symjamma.output;

public class JSONNotebookOutput extends NotebookOutput {

  public JSONNotebookOutput(String content) {
    super("application/json", content);
  }
}
