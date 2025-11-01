package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.expression.ASTAssociation;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents a test report object, which is a data expression wrapping an association of test
 * results. This class is externalizable for serialization purposes.
 */
public class TestReportObjectExpr extends DataExpr<IAssociation> implements Externalizable {

  /**
   * Factory method to create a new instance of {@code TestReportObjectExpr}.
   *
   * @param listOfRules The association containing the test report data.
   * @return A new {@code TestReportObjectExpr} instance.
   */
  public static TestReportObjectExpr newInstance(final IAssociation listOfRules) {
    return new TestReportObjectExpr(listOfRules);
  }

  public TestReportObjectExpr() {
    super(S.TestReportObject, null);
  }

  /**
   * Protected constructor to create a {@code TestReportObjectExpr} with the given association.
   *
   * @param listOfRules The association containing the test report data.
   */
  protected TestReportObjectExpr(final IAssociation listOfRules) {
    super(S.TestReportObject, listOfRules);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TestReportObjectExpr) {
      return fData.equals(((TestReportObjectExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hierarchy() {
    return TESTREPORTOBJECT;
  }

  @Override
  public int hashCode() {
    return 53 + fData.hashCode();
  }

  @Override
  public IExpr copy() {
    return new TestReportObjectExpr(fData);
  }

  /**
   * Returns the normal form of this expression, which is an AST with the head {@code
   * TestReportObject} and the normalized data as its argument.
   *
   * @param nilIfUnevaluated if true, return {@code null} if the expression remains unevaluated.
   * @return The normalized AST or {@code null}.
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return F.unaryAST1(S.TestReportObject, fData.normal(false));
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (ASTAssociation) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }

  /**
   * Returns a string representation of the test report object, focusing on the "TestResults" rule.
   *
   * @return a string in the format "TestReportObject(...)".
   */
  @Override
  public String toString() {
    IExpr outcomeRule = fData.getRule("TestResults");
    return "TestReportObject(" + outcomeRule.toString() + ")";
  }
}
