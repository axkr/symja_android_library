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

public class TestReportObjectExpr extends DataExpr<IAssociation> implements Externalizable {

  public static TestReportObjectExpr newInstance(final IAssociation listOfRules) {
    return new TestReportObjectExpr(listOfRules);
  }

  public TestReportObjectExpr() {
    super(S.TestReportObject, null);
  }

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

  @Override
  public String toString() {
    IExpr outcomeRule = fData.getRule("TestResults");
    // IExpr testID = fData.getRule("TestID");
    return "TestReportObject(" + outcomeRule.toString() + ")";
  }
}
