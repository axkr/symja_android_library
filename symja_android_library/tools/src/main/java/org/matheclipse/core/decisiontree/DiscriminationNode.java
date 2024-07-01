package org.matheclipse.core.decisiontree;

import java.util.List;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class DiscriminationNode implements Comparable<DiscriminationNode> {
  IExpr value;

  DecisionTree decisionTree;

  List<IPatternMatcher> patternDownRules;

  public DiscriminationNode(IExpr value, DecisionTree dn,
      List<IPatternMatcher> patternDownRules) {
    this.value = value;
    this.decisionTree = dn;
    this.patternDownRules = patternDownRules;
  }
  @Override
  public int compareTo(DiscriminationNode o) {
    return value.compareTo(o.value);
  }

  public DecisionTree decisionTree() {
    return decisionTree;
  }

  public List<IPatternMatcher> downRules() {
    return patternDownRules;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DiscriminationNode other = (DiscriminationNode) obj;
    return value.equals(other.value);
  }

  public boolean equalsValue(IExpr expr) {
    return value.equals(expr);
  }

  public IExpr expr() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
