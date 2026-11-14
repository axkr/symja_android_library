package org.matheclipse.core.expression.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The measurements of a classifier on a test set, which <code>ClassifierMeasurements(c, test)
 * </code> returns. <code>m(prop)</code> gives a property, <code>m({prop1, prop2, ...})</code> a
 * list of them:
 *
 * <ul>
 * <li><code>"Accuracy"</code> - the fraction of correctly classified test examples
 * <li><code>"Error"</code> - the fraction of misclassified test examples
 * <li><code>"ConfusionMatrix"</code> - the counts of test examples per actual class (row) and
 * decided class (column), the classes of the classifier and of the test set in canonical order; a
 * last column counts the examples left undecided, which is always zero here
 * <li><code>"Recall"</code> - per actual class the fraction classified correctly
 * <li><code>"AccuracyBaseline"</code> - the accuracy of always deciding the most common test class
 * <li><code>"ExampleNumber"</code>, <code>"Properties"</code>
 * </ul>
 *
 * <p>
 * The head of the object is <code>ClassifierMeasurementsObject</code>; calls with the object as
 * head are dispatched to the evaluator of <code>ClassifierMeasurements</code>.
 */
public class ClassifierMeasurementsObjectExpr extends DataExpr<ClassifierFunctionExpr> {

  private static final long serialVersionUID = -2715932204551764081L;

  /** The head <code>ClassifierMeasurementsObject</code> of the objects. */
  public static final IBuiltInSymbol CLASSIFIER_MEASUREMENTS_OBJECT = F.contextFunction(
      Context.SYSTEM, "ClassifierMeasurementsObject", new AbstractCoreFunctionEvaluator() {
        @Override
        public IExpr evaluate(IAST ast, EvalEngine engine) {
          return F.NIL;
        }
      });

  private static final String[] PROPERTIES = {"Accuracy", "AccuracyBaseline", "ConfusionMatrix",
      "Error", "ExampleNumber", "Properties", "Recall"};

  /**
   * Let the parser answer the name <code>ClassifierMeasurementsObject</code> with
   * {@link #CLASSIFIER_MEASUREMENTS_OBJECT}.
   */
  public static void registerHead() {
    ContextPath.registerSystemAlias(Context.SYSTEM_CONTEXT_NAME + "ClassifierMeasurementsObject",
        CLASSIFIER_MEASUREMENTS_OBJECT);
  }

  /** The test set <code>{x1 -&gt; c1, ...}</code>. */
  private final IAST testSet;

  /** The classes of the classifier and of the test set, in canonical order. */
  private final IAST classes;

  /** The class index of each test example and the index of its decided class. */
  private final int[] actual;
  private final int[] decided;

  /**
   * Classify the test set.
   *
   * @param classifier the classifier
   * @param testSet a non-empty list of rules <code>example -&gt; class</code>
   * @return <code>null</code> if an example can't be classified
   */
  public static ClassifierMeasurementsObjectExpr newInstance(ClassifierFunctionExpr classifier,
      IAST testSet, EvalEngine engine) {
    IAST modelClasses = classifier.toData().classes();
    List<IExpr> all = new ArrayList<IExpr>();
    modelClasses.forEach(c -> all.add(c));
    for (int i = 1; i < testSet.size(); i++) {
      IExpr c = testSet.get(i).second();
      if (!all.contains(c)) {
        all.add(c);
      }
    }
    IExpr[] sorted = all.toArray(new IExpr[all.size()]);
    Arrays.sort(sorted);
    IAST classes = F.List(sorted);
    int n = testSet.argSize();
    int[] actual = new int[n];
    int[] decided = new int[n];
    for (int i = 0; i < n; i++) {
      IAST rule = (IAST) testSet.get(i + 1);
      int index = classifier.decide(rule.first(), engine);
      if (index < 0) {
        return null;
      }
      actual[i] = classes.indexOf(rule.second()) - 1;
      decided[i] = classes.indexOf(modelClasses.get(index + 1)) - 1;
    }
    return new ClassifierMeasurementsObjectExpr(classifier, testSet, classes, actual, decided);
  }

  private ClassifierMeasurementsObjectExpr(ClassifierFunctionExpr classifier, IAST testSet,
      IAST classes, int[] actual, int[] decided) {
    super(S.ClassifierMeasurements, classifier);
    this.testSet = testSet;
    this.classes = classes;
    this.actual = actual;
    this.decided = decided;
  }

  /**
   * Evaluate <code>m(prop)</code> or <code>m({prop1, prop2, ...})</code>.
   *
   * @return {@link F#NIL} if a property isn't known
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() != 1) {
      return F.NIL;
    }
    return property(ast.arg1());
  }

  /** The value of a property or of a list of properties, or {@link F#NIL} if one isn't known. */
  public IExpr property(IExpr name) {
    if (name.isList()) {
      IAST names = (IAST) name;
      IASTAppendable result = F.ListAlloc(names.argSize());
      for (int i = 1; i < names.size(); i++) {
        IExpr value = property(names.get(i));
        if (value.isNIL()) {
          return F.NIL;
        }
        result.append(value);
      }
      return result;
    }
    if (!name.isString()) {
      return F.NIL;
    }
    final int n = actual.length;
    final int k = classes.argSize();
    switch (name.toString()) {
      case "Accuracy":
        return F.num((double) correct() / n);
      case "Error":
        return F.num((double) (n - correct()) / n);
      case "ConfusionMatrix": {
        int[][] counts = confusion();
        // the last column counts the undecided examples
        return F.mapRange(0, k, i -> F.mapRange(0, k + 1, j -> F.ZZ(j < k ? counts[i][j] : 0)));
      }
      case "Recall": {
        int[][] counts = confusion();
        IASTAppendable rules = F.ListAlloc(k);
        for (int i = 0; i < k; i++) {
          int total = 0;
          for (int j = 0; j < k; j++) {
            total += counts[i][j];
          }
          if (total > 0) {
            rules.append(F.Rule(classes.get(i + 1), F.num((double) counts[i][i] / total)));
          }
        }
        return F.assoc(rules);
      }
      case "AccuracyBaseline": {
        int[] frequency = new int[k];
        int most = 0;
        for (int i = 0; i < n; i++) {
          most = Math.max(most, ++frequency[actual[i]]);
        }
        return F.num((double) most / n);
      }
      case "ExampleNumber":
        return F.ZZ(n);
      case "Properties":
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      default:
        return F.NIL;
    }
  }

  private int correct() {
    int count = 0;
    for (int i = 0; i < actual.length; i++) {
      if (actual[i] == decided[i]) {
        count++;
      }
    }
    return count;
  }

  private int[][] confusion() {
    int k = classes.argSize();
    int[][] counts = new int[k][k];
    for (int i = 0; i < actual.length; i++) {
      counts[actual[i]][decided[i]]++;
    }
    return counts;
  }

  @Override
  public IExpr copy() {
    return new ClassifierMeasurementsObjectExpr(fData, testSet, classes, actual, decided);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ClassifierMeasurementsObjectExpr) {
      ClassifierMeasurementsObjectExpr other = (ClassifierMeasurementsObjectExpr) obj;
      return fData.equals(other.fData) && testSet.equals(other.testSet);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 613 + 31 * fData.hashCode() + testSet.hashCode();
  }

  @Override
  public ISymbol head() {
    return CLASSIFIER_MEASUREMENTS_OBJECT;
  }

  @Override
  public ISymbol topHead() {
    // calls m(prop) are evaluated by ClassifierMeasurements
    return S.ClassifierMeasurements;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return F.unaryAST1(CLASSIFIER_MEASUREMENTS_OBJECT, summary());
  }

  private IExpr summary() {
    return F.assoc(F.list(F.Rule(F.stringx("Method"), F.stringx(fData.toData().method())),
        F.Rule(F.stringx("ExampleNumber"), F.ZZ(actual.length))));
  }

  /** A summary: the method of the classifier and the number of test examples. */
  @Override
  public String toString() {
    boolean relaxed = EvalEngine.get().isRelaxedSyntax();
    return (relaxed ? "ClassifierMeasurementsObject(" : "ClassifierMeasurementsObject[")
        + summary().toString() + (relaxed ? ")" : "]");
  }
}
