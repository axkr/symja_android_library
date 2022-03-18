package org.matheclipse.core.images;

import org.matheclipse.core.mathcell.BasePlotExample;

public class PNGImageExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Image(RandomReal(1.0, {100, 100, 3}))";
  }

  public static void main(String[] args) {
    PNGImageExample p = new PNGImageExample();
    p.generateHTML();
  }
}
