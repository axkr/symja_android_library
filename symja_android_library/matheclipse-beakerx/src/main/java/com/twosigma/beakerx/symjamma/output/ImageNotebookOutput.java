package com.twosigma.beakerx.symjamma.output;

public abstract class ImageNotebookOutput extends HTMLFriendlyNotebookOutput {

  public ImageNotebookOutput(String mimeType, String content) {
    super(mimeType, content);
  }

  @Override
  public String toHTML() {
    final String mime = getMime().asString();
    return "<img src=\"data:" + mime + ";base64, " + getData() + "\" />";
  }
}
