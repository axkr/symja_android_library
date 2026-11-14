package org.matheclipse.core.expression.data;

import java.util.Arrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The trained classifier which <code>Classify(...)</code> returns.
 *
 * <ul>
 * <li><code>c(x)</code> - the decided class of the example <code>x</code>; a list of examples is
 * classified element by element. A string is an example too, not a property name: where the
 * training values are numbers it prints <code>ClassifierFunction::mlincfttp</code>, where they are
 * vectors <code>ClassifierFunction::mlbddataev</code>
 * <li><code>c(x, prop)</code> - <code>"Decision"</code>, <code>"Probabilities"</code> (an
 * association of the classes with their probabilities), <code>{"Probability", class}</code>,
 * <code>"TopProbabilities"</code> (the rules <code>class -&gt; probability</code> of the classes
 * with a probability of at least 1/100, most likely first) and <code>{"TopProbabilities", n}</code>
 * <li><code>Information(c, prop)</code> - the properties <code>"Classes"</code>,
 * <code>"ClassNumber"</code>, <code>"ExampleNumber"</code>, <code>"FeatureNames"</code>,
 * <code>"FeatureNumber"</code>, <code>"FeatureTypes"</code>, <code>"Method"</code>,
 * <code>"MethodOption"</code> and <code>"Properties"</code> of the classifier
 * </ul>
 *
 * The input form is <code>ClassifierFunction(&lt;|"Method" -&gt; {method, suboptions...},
 * "TrainingSet" -&gt; {...}|&gt;)</code>, which evaluates to the same classifier again.
 */
public class ClassifierFunctionExpr extends DataExpr<ClassifierModel> {

  private static final long serialVersionUID = 3167459287346081473L;

  /** The properties of the classifier itself, sorted. */
  private static final String[] PROPERTIES =
      {"Classes", "ClassNumber", "ExampleNumber", "FeatureNames", "FeatureNumber",
          "FeatureTypes", "Method", "MethodOption", "Properties"};

  /** <code>"TopProbabilities"</code> leaves out the classes less probable than this. */
  private static final double TOP_PROBABILITY_THRESHOLD = 0.01;

  /** The properties of the classification of an example, sorted. */
  private static final String[] EXAMPLE_PROPERTIES =
      {"Decision", "Probabilities", "Probability", "TopProbabilities"};

  public static ClassifierFunctionExpr newInstance(ClassifierModel model) {
    return new ClassifierFunctionExpr(model);
  }

  protected ClassifierFunctionExpr(ClassifierModel model) {
    super(S.ClassifierFunction, model);
  }

  /**
   * Evaluate <code>c(...)</code>, where <code>ast.head()</code> is this classifier.
   *
   * @return {@link F#NIL} if the arguments are not a property or classifiable examples
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || ast.argSize() > 2) {
      return F.NIL;
    }
    IExpr property = ast.argSize() == 2 ? ast.arg2() : F.stringx("Decision");
    if (!isExampleProperty(property)) {
      return F.NIL;
    }
    IExpr x = ast.arg1();
    if (fData.isExample(x)) {
      return classifyChecked(x, property, engine);
    }
    if (x.isList() && ((IAST) x).forAll(fData::isExample)) {
      IAST examples = (IAST) x;
      IASTAppendable result = F.ListAlloc(examples.argSize());
      for (int i = 1; i < examples.size(); i++) {
        IExpr value = classifyChecked(examples.get(i), property, engine);
        if (value.isNIL()) {
          return F.NIL;
        }
        result.append(value);
      }
      return result;
    }
    // The data being evaluated is not formatted correctly.
    return Errors.printMessage(S.ClassifierFunction, "mlbddataev", F.CEmptyList, engine);
  }

  /** Classify one example; a non-numeric value where the training values are numbers is reported. */
  private IExpr classifyChecked(IExpr x, IExpr property, EvalEngine engine) {
    IExpr incompatible = fData.incompatibleValue(x, engine);
    if (incompatible.isPresent()) {
      // Incompatible variable type (`1`) and variable value (`2`).
      return Errors.printMessage(S.ClassifierFunction, "mlincfttp",
          F.list(F.stringx(fData.valueType()), incompatible), engine);
    }
    return classify(x, property, engine);
  }

  /** The class index decided for an example, or <code>-1</code> if it can't be classified. */
  public int decide(IExpr x, EvalEngine engine) {
    double[] p = fData.probabilities(x, engine);
    return p == null ? -1 : ClassifierModel.decision(p);
  }

  private static boolean isExampleProperty(IExpr property) {
    if (property.isString()) {
      String name = property.toString();
      return Arrays.binarySearch(EXAMPLE_PROPERTIES, name) >= 0 && !name.equals("Probability");
    }
    if (property.isList() && property.argSize() == 2 && property.first().isString()) {
      String name = property.first().toString();
      if (name.equals("Probability")) {
        return true;
      }
      if (name.equals("TopProbabilities")) {
        int n = property.second().toIntDefault();
        return n > 0;
      }
    }
    return false;
  }

  private IExpr classify(IExpr x, IExpr property, EvalEngine engine) {
    double[] p = fData.probabilities(x, engine);
    if (p == null) {
      return F.NIL;
    }
    IAST classes = fData.classes();
    String name = property.isString() ? property.toString() : property.first().toString();
    switch (name) {
      case "Decision":
        return classes.get(ClassifierModel.decision(p) + 1);
      case "Probabilities": {
        IASTAppendable rules = F.ListAlloc(p.length);
        for (int c = 0; c < p.length; c++) {
          rules.append(F.Rule(classes.get(c + 1), F.num(p[c])));
        }
        return F.assoc(rules);
      }
      case "Probability": {
        int index = classes.indexOf(property.second());
        return index > 0 ? F.num(p[index - 1]) : F.C0;
      }
      case "TopProbabilities": {
        int n = property.isList() ? property.second().toIntDefault() : p.length;
        Integer[] order = new Integer[p.length];
        for (int c = 0; c < p.length; c++) {
          order[c] = c;
        }
        // between equal probabilities the later class first, as the decision takes it
        Arrays.sort(order, (a, b) -> {
          int byProbability = Double.compare(p[b], p[a]);
          return byProbability != 0 ? byProbability : Integer.compare(b, a);
        });
        IASTAppendable rules = F.ListAlloc(p.length);
        for (int c = 0; c < p.length && rules.argSize() < n; c++) {
          if (p[order[c]] >= TOP_PROBABILITY_THRESHOLD) {
            rules.append(F.Rule(classes.get(order[c] + 1), F.num(p[order[c]])));
          }
        }
        return rules;
      }
      default:
        return F.NIL;
    }
  }

  /**
   * The value of a property of the classifier, as <code>Information(c, name)</code> gives it.
   *
   * @return {@link F#NIL} if the classifier has no such property
   */
  public IExpr information(String name) {
    switch (name) {
      case "Classes":
        return fData.classes();
      case "ClassNumber":
        return F.ZZ(fData.classes().argSize());
      case "ExampleNumber":
        return F.ZZ(fData.trainingSet().argSize());
      case "FeatureNames":
        return fData.featureNames();
      case "FeatureNumber":
        return F.ZZ(fData.featureNames().argSize());
      case "MethodOption":
        return F.Rule(S.Method, fData.methodSpecification());
      case "FeatureTypes":
        return fData.featureTypes();
      case "Method":
        return F.stringx(fData.method());
      case "Properties":
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      default:
        return F.NIL;
    }
  }

  @Override
  public IExpr copy() {
    return new ClassifierFunctionExpr(fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ClassifierFunctionExpr) {
      return fData.equals(((ClassifierFunctionExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 607 + fData.hashCode();
  }

  /** <code>ClassifierFunction(&lt;|"Method" -&gt; ..., "TrainingSet" -&gt; {...}|&gt;)</code> */
  @Override
  public IAST fullForm() {
    return F.unaryAST1(S.ClassifierFunction,
        F.assoc(F.list(F.Rule(F.stringx("Method"), fData.methodSpecification()),
            F.Rule(F.stringx("TrainingSet"), fData.trainingSet()))));
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return fullForm();
  }

  /** A summary: the method, the classes and the number of training examples. */
  @Override
  public String toString() {
    boolean relaxed = EvalEngine.get().isRelaxedSyntax();
    IAST summary = F.list(F.Rule(F.stringx("Method"), F.stringx(fData.method())),
        F.Rule(F.stringx("Classes"), fData.classes()),
        F.Rule(F.stringx("ExampleNumber"), F.ZZ(fData.trainingSet().argSize())));
    return (relaxed ? "ClassifierFunction(" : "ClassifierFunction[") + F.assoc(summary).toString()
        + (relaxed ? ")" : "]");
  }
}
