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

public class TestResultObjectExpr extends DataExpr<IAssociation> implements Externalizable {

  public static TestResultObjectExpr newInstance(final IAssociation listOfRules) {
    return new TestResultObjectExpr(listOfRules);
  }

  public TestResultObjectExpr() {
    super(S.TestResultObject, null);
  }

  protected TestResultObjectExpr(final IAssociation listOfRules) {
    super(S.TestResultObject, listOfRules);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TestResultObjectExpr) {
      return fData.equals(((TestResultObjectExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hierarchy() {
    return TESTRESULTOBJECT;
  }

  @Override
  public int hashCode() {
    return 53 + fData.hashCode();
  }

  @Override
  public IExpr copy() {
    return new TestResultObjectExpr(fData);
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return F.unaryAST1(S.TestResultObject, fData.normal(false));
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (ASTAssociation) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }

  @Override
  public String toString() {
    IExpr outcomeRule = fData.getRule("Outcome");
    // IExpr inputRule = fData.getRule("Input");
    IExpr expectedOutputRule = fData.getRule("ExpectedOutput");
    IExpr actualOutput = fData.getRule("ActualOutput");
    IExpr testID = fData.getRule("TestID");
    if (outcomeRule.second().isString("Failure")) {
      if (testID.isPresent()) {
        return "TestResultObject(" + outcomeRule.toString() + "," + expectedOutputRule.toString()
            + "," + actualOutput.toString() + "," + testID.toString() + ")";
      }
      return "TestResultObject(" + outcomeRule.toString() + "," + expectedOutputRule.toString()
          + "," + actualOutput.toString() + ")";
    }
    if (testID.isPresent()) {
      return "TestResultObject(" + outcomeRule.toString() + "," + testID.toString() + ")";
    }
    return "TestResultObject(" + outcomeRule.toString() + ")";
  }
}
