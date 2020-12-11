package com.twosigma.beakerx.symjamma.output;

public abstract class HTMLFriendlyNotebookOutput extends NotebookOutput {

  public HTMLFriendlyNotebookOutput(String mimeTypeObj, String content) {
    super(mimeTypeObj, content);
  }

  public abstract String toHTML();
}
