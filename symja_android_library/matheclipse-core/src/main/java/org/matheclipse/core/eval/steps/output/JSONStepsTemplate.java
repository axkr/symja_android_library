package org.matheclipse.core.eval.steps.output;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * This class represents list of steps to solve a problem
 */
public class JSONStepsTemplate {

  private List<JSONStep> steps = new ArrayList<>();

  private String templateName;

  public JSONStepsTemplate() {

  }

  public JSONStepsTemplate(List<JSONStep> steps, String templateName) {
    this.steps = steps;
    this.templateName = templateName;
  }

  public void add(JSONStep step) {
    this.steps.add(step);
  }

  public void addAll(@Nonnull JSONStepsTemplate step) {
    this.steps.addAll(step.steps);
  }

  public int size() {
    return this.steps.size();
  }

  @Nonnull
  public List<JSONStep> getSteps() {
    return steps;
  }

  public String getTemplateName() {
    return templateName;
  }

  @Override
  public String toString() {
    return "TemplateSteps{" + "templateName='" + templateName + '\'' + ", steps=" + steps + '}';
  }
}
