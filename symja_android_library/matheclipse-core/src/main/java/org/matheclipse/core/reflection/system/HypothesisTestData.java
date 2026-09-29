package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>HypothesisTestData(...)["property"]</code> - a property of the tests
 * {@link DistributionFitTest} ran: <code>"AllTests"</code>, <code>"AutomaticTest"</code>,
 * <code>"FittedDistribution"</code>, <code>"PValue"</code>, <code>"TestStatistic"</code>,
 * <code>"TestData"</code> and the tables <code>"PValueTable"</code>,
 * <code>"TestStatisticTable"</code> and <code>"TestDataTable"</code>. A second argument names the
 * test: <code>h("PValue", "Kuiper")</code>. Any other property is
 * <code>Missing("NotAvailable", property)</code>.
 */
public class HypothesisTestData extends AbstractEvaluator {

  private static final String[] PROPERTIES = {"AllTests", "AutomaticTest", "FittedDistribution",
      "PValue", "PValueTable", "TestData", "TestDataTable", "TestStatistic", "TestStatisticTable"};

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // h(property) or h(property, test), where h = HypothesisTestData(<|...|>)
    IExpr head = ast.head();
    if (!head.isAST(S.HypothesisTestData, 2) || !head.first().isAssociation() || ast.argSize() < 1
        || ast.argSize() > 2) {
      return F.NIL;
    }
    return property((IAST) head, ast.arg1(), ast.isAST2() ? ast.arg2() : F.NIL);
  }

  /**
   * One property of a <code>HypothesisTestData</code> object.
   *
   * @param test the test it is about, or {@link F#NIL} for all of them or the automatic one
   */
  static IExpr property(IAST object, IExpr property, IExpr test) {
    IAssociation fields = (IAssociation) object.arg1();
    IAssociation testData = (IAssociation) fields.getValue(F.stringx("TestData"));
    IExpr automatic = fields.getValue(F.stringx("AutomaticTest"));
    IExpr chosen = test.isPresent() ? test : automatic;
    if (!property.isString()) {
      return F.Missing(S.NotAvailable, property);
    }
    switch (property.toString()) {
      case "Properties":
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      case "AllTests":
        return F.mapRange(1, testData.argSize() + 1, i -> testData.getRule(i).first())
            .filter(name -> validTest(fields, name))[0];
      case "AutomaticTest":
        return automatic;
      case "FittedDistribution":
        return fields.getValue(F.stringx("FittedDistribution"));
      case "TestData":
        return testEntry(testData, chosen, 0);
      case "TestStatistic":
        return testEntry(testData, chosen, 1);
      case "PValue":
        return testEntry(testData, chosen, 2);
      case "TestDataTable":
        return table(fields, testData, automatic, test, true, true);
      case "TestStatisticTable":
        return table(fields, testData, automatic, test, true, false);
      case "PValueTable":
        return table(fields, testData, automatic, test, false, true);
      default:
        return F.Missing(S.NotAvailable, property);
    }
  }

  /** Whether a property name is one of the properties this object answers. */
  static boolean isProperty(IExpr property) {
    return property.isString() && (property.isString("Properties")
        || java.util.Arrays.asList(PROPERTIES).contains(property.toString()));
  }

  /**
   * Whether the test is valid for the sample: Mathematica leaves Cramer-von Mises out below 7
   * points, from <code>"AllTests"</code> as from the tables.
   */
  private static boolean validTest(IAssociation fields, IExpr name) {
    int sampleSize = fields.getValue(F.stringx("SampleSize")).toIntDefault();
    return !(name.isString("CramerVonMises") && sampleSize < 7);
  }

  /** <code>{statistic, p}</code> (part 0), the statistic (1) or the p-value (2) of a test. */
  private static IExpr testEntry(IAssociation testData, IExpr test, int part) {
    IExpr entry = testData.getValue(test);
    if (!entry.isList2()) {
      return F.Missing(S.NotAvailable, test);
    }
    return part == 0 ? entry : entry.get(part);
  }

  private static IExpr label(IExpr test) {
    switch (test.toString()) {
      case "AndersonDarling":
        return F.stringx("Anderson\u2010Darling");
      case "CramerVonMises":
        return F.stringx("Cram\u00E9r\u2010von Mises");
      case "KolmogorovSmirnov":
        return F.stringx("Kolmogorov\u2010Smirnov");
      case "PearsonChiSquare":
        return F.stringx("Pearson \u03C7\u00B2");
      case "WatsonUSquare":
        return F.stringx("Watson U\u00B2");
      default:
        return test;
    }
  }

  /**
   * A table of the tests: the automatic test when no test is named, every test valid for the sample
   * with <code>All</code>, or the named one.
   */
  private static IExpr table(IAssociation fields, IAssociation testData, IExpr automatic,
      IExpr test, boolean statistic, boolean pValue) {
    IASTAppendable rows = F.ListAlloc();
    IASTAppendable header = F.ListAlloc(3);
    header.append(F.stringx(""));
    if (statistic) {
      header.append(F.stringx("Statistic"));
    }
    if (pValue) {
      header.append(F.stringx("P\u2010Value"));
    }
    rows.append(header);
    for (IExpr name : testData.keys()) {
      if (test == S.All) {
        if (!validTest(fields, name)) {
          continue;
        }
      } else if (!(test.isPresent() ? test : automatic).equals(name)) {
        continue;
      }
      IExpr entry = testData.getValue(name);
      IASTAppendable row = F.ListAlloc(3);
      row.append(label(name));
      if (statistic) {
        row.append(entry.first());
      }
      if (pValue) {
        row.append(entry.second());
      }
      rows.append(row);
    }
    if (rows.argSize() == 1) {
      return F.Missing(S.NotAvailable, test);
    }
    IAST grid = F.function(
        S.Grid, rows, F.Rule(S.Alignment, F.List(S.Left, S.Automatic)), F
            .Rule(S.Dividers,
                F.List(F.List(F.Rule(F.C2, F.GrayLevel(F.num(0.7)))),
                    F.List(F.Rule(F.C2, F.GrayLevel(F.num(0.7)))))),
        F.Rule(S.Spacings, S.Automatic));
    return F.binaryAST2(S.Style, grid, F.stringx("DialogStyle"));
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
