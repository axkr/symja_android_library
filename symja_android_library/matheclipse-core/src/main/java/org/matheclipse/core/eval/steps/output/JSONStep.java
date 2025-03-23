package org.matheclipse.core.eval.steps.output;

import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.Nonnull;

/**
 * Represents a specific step in solving process. Extra information can be added to improve visual
 * representation.
 */
public class JSONStep {

  /**
   * Localization key
   */

  private String stepKey;

  /**
   * Explain how to calculate the {@link #expression}
   */

  private String step;

  /**
   * The result after calculate with the explanation
   */

  private String expression;

  /**
   * If possible, specific the input before apply the step
   */

  private String prevExpression;


  private List<JSONStep> subSteps;

  public JSONStep() {

  }


  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }


  public String getPrevExpression() {
    return prevExpression;
  }

  public void setPrevExpression(String prevExpression) {
    this.prevExpression = prevExpression;
  }


  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }


  public String getStepKey() {
    return stepKey;
  }

  public void setStepKey(String stepKey) {
    this.stepKey = stepKey;
  }

  public void setSubSteps(List<JSONStep> subSteps) {
    this.subSteps = subSteps;
  }

  public void addSubSteps(@Nonnull List<JSONStep> subSteps) {
    if (this.subSteps == null) {
      this.subSteps = new ArrayList<>();
    }
    this.subSteps.addAll(subSteps);
  }


  public List<JSONStep> getSubSteps() {
    return subSteps;
  }

  @Override
  public String toString() {
    if (subSteps != null) {
      if (subSteps.size() == 1 && subSteps.get(0).getSubSteps() != null) {
        // jump to sub- sub-step
        return subSteps.get(0).toString();
      }
      StringBuilder buf = new StringBuilder();
      buf.append(" Substeps{\n");
      for (int i = 0; i < subSteps.size(); i++) {
        JSONStep msStep = subSteps.get(i);
        buf.append(msStep.toString());
      }
      buf.append(" }\n");
      return buf.toString();
    }
    return "Step{\n" + "stepKey='" + stepKey + "'\n" + ", step='" + step + "'\n" + ", expression='"
        + expression + "'\n" + ", prevExpression='" + prevExpression + "'\n" + "}\n";
  }

}
