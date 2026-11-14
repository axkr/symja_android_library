package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ClassifierFunctionExpr;
import org.matheclipse.core.expression.data.ClassifierModel;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 *
 *
 * <pre>
 * <code>Classify({x1 -&gt; c1, x2 -&gt; c2, ...})
 * Classify({x1, x2, ...} -&gt; {c1, c2, ...})
 * Classify(&lt;|c1 -&gt; {x11, x12, ...}, c2 -&gt; {...}, ...|&gt;)
 * </code>
 * </pre>
 *
 * <p>
 * trains a <code>ClassifierFunction</code> on the examples <code>xi</code> with their classes
 * <code>ci</code>. An example is a number, a string or a symbol, or a vector of them.
 * <code>Classify(data, x)</code> and <code>Classify(data, x, prop)</code> classify <code>x</code>
 * right away.
 *
 * <p>
 * The training data must hold at least two classes. The option <code>Method</code> is
 * <code>Automatic</code> (Markov for strings, logistic
 * regression otherwise), <code>"LogisticRegression"</code>, <code>"Markov"</code> or
 * <code>"NearestNeighbors"</code>, the last one also as <code>{"NearestNeighbors",
 * "NeighborsNumber" -&gt; k, "DistributionSmoothing" -&gt; s}</code>; see {@link ClassifierModel}
 * for the fits.
 */
public class Classify extends AbstractEvaluator {

  /** The method names of the option <code>Method</code>, sorted; not all of them implemented. */
  private static final String[] KNOWN_METHODS = {"ClassDistributions", "DecisionTree",
      "GradientBoostedTrees", "LogisticRegression", "Markov", "NaiveBayes", "NearestNeighbors",
      "NeuralNetwork", "PriorBaseline", "RandomForest", "SupportVectorMachine"};

  public Classify() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.head() != S.Classify || ast.argSize() < 1) {
      return F.NIL;
    }
    int end = ast.size();
    while (end > 2 && ast.get(end - 1).isRuleAST() && isOptionName(ast.get(end - 1).first())) {
      end--;
    }
    if (end - 1 > 3) {
      return F.NIL;
    }
    // null is the automatic choice of the method
    String method = null;
    int neighborsNumber = 0;
    double distributionSmoothing = ClassifierModel.DEFAULT_DISTRIBUTION_SMOOTHING;
    IExpr methodOption = F.NIL;
    for (int i = end; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (isMethodName(option.first())) {
        methodOption = option;
        IExpr value = option.second();
        IAST suboptions = F.CEmptyList;
        if (value.isList() && value.argSize() > 0) {
          // {"method", suboptions...}
          suboptions = ((IAST) value).rest();
          value = value.first();
        }
        if (value == S.Automatic) {
          method = null;
          continue;
        }
        if (!value.isString() || Arrays.binarySearch(KNOWN_METHODS, value.toString()) < 0) {
          // "`1`" is not an available method. Possible methods include ...
          return Errors.printMessage(S.Classify, "elmntavl", F.list(value), engine);
        }
        if (Arrays.binarySearch(ClassifierModel.METHODS, value.toString()) < 0) {
          // `1` currently not supported in `2`.
          return Errors.printMessage(S.Classify, "unsupported", F.list(option, S.Classify),
              engine);
        }
        method = value.toString();
        for (IExpr suboption : suboptions) {
          if (!suboption.isRuleAST()) {
            return Errors.printMessage(S.Classify, "bdmtd", F.list(option), engine);
          }
          String name = suboption.first().isString() ? suboption.first().toString() : "";
          if (name.equals("NeighborsNumber") && suboption.second() != S.Automatic) {
            neighborsNumber = suboption.second().toIntDefault();
            if (neighborsNumber <= 0) {
              return Errors.printMessage(S.Classify, "bdmtd", F.list(option), engine);
            }
          } else if (name.equals("DistributionSmoothing")) {
            distributionSmoothing = engine.evalDoubleNaN(suboption.second());
            if (!(distributionSmoothing >= 0.0)) {
              return Errors.printMessage(S.Classify, "bdmtd", F.list(option), engine);
            }
          }
        }
      }
    }
    IAST trainingSet = trainingSet(ast.arg1());
    if (trainingSet.isNIL()) {
      return F.NIL;
    }
    if (trainingSet.forAll(rule -> rule.second().equals(trainingSet.arg1().second()))) {
      // The dataset should contain at least two different classes.
      return Errors.printMessage(S.Classify, "mincnb", F.CEmptyList, engine);
    }
    if (ClassifierModel.MARKOV.equals(method)
        && !ClassifierModel.isTextTrainingSet(trainingSet)) {
      // the Markov method classifies strings only
      return Errors.printMessage(S.Classify, "bdmtd", F.list(methodOption), engine);
    }
    ClassifierModel model = ClassifierModel.train(method, neighborsNumber,
        distributionSmoothing, trainingSet, engine);
    if (model == null) {
      return F.NIL;
    }
    ClassifierFunctionExpr classifier = ClassifierFunctionExpr.newInstance(model);
    if (end > 2) {
      // Classify(data, x) or Classify(data, x, prop)
      IASTAppendable call = F.ast(classifier, end - 2);
      for (int i = 2; i < end; i++) {
        call.append(ast.get(i));
      }
      return engine.evaluate(call);
    }
    return classifier;
  }

  private static boolean isOptionName(IExpr lhs) {
    return lhs.isSymbol() || isMethodName(lhs);
  }

  private static boolean isMethodName(IExpr lhs) {
    return lhs == S.Method || (lhs.isString() && lhs.toString().equals("Method"));
  }

  /**
   * The examples with their classes as a list <code>{x1 -&gt; c1, x2 -&gt; c2, ...}</code>.
   *
   * @param data <code>{x1 -&gt; c1, ...}</code>, <code>{x1, x2, ...} -&gt; {c1, c2, ...}</code> or
   *        <code>&lt;|c1 -&gt; {x11, x12, ...}, ...|&gt;</code>
   * @return {@link F#NIL} if <code>data</code> has none of these forms or holds no example
   */
  public static IAST trainingSet(IExpr data) {
    if (data.isAssociation()) {
      IAssociation association = (IAssociation) data;
      IASTAppendable result = F.ListAlloc();
      for (int i = 1; i < association.size(); i++) {
        IAST rule = association.getRule(i);
        if (!rule.second().isList()) {
          return F.NIL;
        }
        IExpr c = rule.first();
        ((IAST) rule.second()).forEach(x -> result.append(F.Rule(x, c)));
      }
      return result.argSize() > 0 ? result : F.NIL;
    }
    if (data.isRuleAST()) {
      IExpr examples = data.first();
      IExpr classes = data.second();
      if (examples.isList() && classes.isList() && examples.argSize() == classes.argSize()
          && examples.argSize() > 0) {
        return F.mapRange(1, examples.size(),
            i -> F.Rule(((IAST) examples).get(i), ((IAST) classes).get(i)));
      }
      return F.NIL;
    }
    if (data.isList() && data.argSize() > 0 && ((IAST) data).forAll(x -> x.isRuleAST())) {
      return F.mapRange(1, data.size(),
          i -> F.Rule(((IAST) data).get(i).first(), ((IAST) data).get(i).second()));
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(IPatternMatcher.SET, "bdmtd",
        F.stringx("Value of option `1` is not valid."));
    newSymbol.putMessage(IPatternMatcher.SET, "elmntavl", F.stringx(
        "\"`1`\" is not an available method. Possible methods include \"ClassDistributions\", "
            + "\"DecisionTree\", \"GradientBoostedTrees\", \"LogisticRegression\", \"Markov\", "
            + "\"NaiveBayes\", \"NearestNeighbors\", \"NeuralNetwork\", \"PriorBaseline\", "
            + "\"RandomForest\", \"SupportVectorMachine\", and Automatic."));
    newSymbol.putMessage(IPatternMatcher.SET, "mincnb",
        F.stringx("The dataset should contain at least two different classes."));
  }
}
