package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FindFormula extends AbstractFunctionOptionEvaluator {

  private static IBuiltInSymbol[] TARGET_FUNCTIONS =
      {S.Power, S.Sin, S.Cos, S.Tan, S.Cot, S.Log, S.Sqrt, S.Csc, S.Sec, S.Abs, S.Exp};

  /**
   * A simple wrapper to hold a model template. It stores the symbolic expression, the list of
   * parameters to find, and a human-readable name.
   */
  private static class ModelTemplate implements Comparable<ModelTemplate> {
    final String name;
    final IAST model;
    final IAST params; // List of parameters, e.g., {a, b}
    double aic = Double.POSITIVE_INFINITY;
    IExpr fittedExpression = null;
    IAST rules = null;

    private ModelTemplate(String name, IAST model, IAST params) {
      this.name = name;
      this.model = model;
      this.params = params;
    }

    @Override
    public int compareTo(ModelTemplate o) {
      return aic > o.aic ? 1 : aic < o.aic ? -1 : 0;
    }
  }

  /**
   * A simple wrapper to hold the result of a successful fit.
   */
  private static class FittedResult {
    final ModelTemplate model;
    final IExpr fittedExpression;
    final double aic;
    final IAST rules;

    FittedResult(ModelTemplate model, IExpr fittedExpression, double aic, IAST rules) {
      this.model = model;
      this.fittedExpression = fittedExpression;
      this.aic = aic;
      this.rules = rules;
    }

    @Override
    public String toString() {
      return String.format("Model: %s\nFormula: %s\nAIC: %.4f\nRules: %s", model.name,
          fittedExpression.toString(), aic, rules.toString());
    }


  }

  public FindFormula() {
    // empty constructor
  }


  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IExpr data = ast.arg1();
    IExpr x = ast.arg2();
    if (x.isVariable()) {
      int maxParameters = 3;
      IExpr arg4 = S.All;

      int[] isMatrix = data.isMatrix();
      if (isMatrix != null && isMatrix[1] == 2 && data.isList()) {
        IAST matrix = (IAST) data;
        double[][] doubleMatrix = matrix.toDoubleMatrix(true);
        if (doubleMatrix != null) {
          if (ast.argSize() > 2) {
            maxParameters = ast.arg3().toIntDefault();
            if (maxParameters < 0) {
              // Positive machine-sized integer expected at position `2` in `1`.
              return Errors.printMessage(S.FindFormula, "intpm", F.List(F.C3, ast), engine);
            }
            if (ast.argSize() > 3) {
              arg4 = ast.arg4();
            }
          }
          return generateModels(matrix, doubleMatrix, x, maxParameters, engine);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Generates a product model of the form: f(#1 * x) * f(#2 * x) * ... * f(#n * x).
   * 
   * @param numParams the number of {@link S#Slot}s to use
   * @param var the independent variable
   * @param functionSymbol the function to apply to the variable
   * @param model the existing model to build upon
   * @return
   */
  private static ModelTemplate generateTimesModel(int numParams, IExpr var,
      IBuiltInSymbol functionSymbol, ModelTemplate model) {
    if (numParams < 1) {
      return null;
    }
    IASTAppendable params = F.ListAlloc(numParams);
    if (model != null) {
      params.appendArgs(model.params);
    }
    params.append(F.Slot(numParams));

    IASTAppendable product = F.TimesAlloc(numParams);
    if (model != null) {
      product.appendArgs(model.model);
    }
    if (functionSymbol == S.Power) {
      if (numParams == 1) {
        product.append(F.Times(params.get(numParams), F.C1));
      } else {
        product.append(F.Times(params.get(numParams), F.Power(var, F.ZZ(numParams - 1))));
      }
    } else {
      IExpr term = F.unaryAST1(functionSymbol, F.Times(params.get(numParams), var));
      product.append(term);
    }
    return new ModelTemplate("ProductF", product, params);
  }

  /**
   * 
   * Generates a polynomial model of the form: #1 + #2*f(x) + #3*f(x)^2 + ... + #n*f(x)^(n-1).
   * 
   * @param numParams the number of {@link S#Slot}s to use
   * @param var the independent variable
   * @param functionSymbol the function to apply to the variable
   * @param model the existing model to build upon
   * @return
   */
  private static ModelTemplate generatePlusModel(int numParams, IExpr var,
      IBuiltInSymbol functionSymbol, ModelTemplate model) {
    if (numParams < 1) {
      return null;
    }
    IASTAppendable params = F.ListAlloc(numParams);
    if (model != null) {
      params.appendArgs(model.params);
    }
    params.append(F.Slot(numParams));

    IASTAppendable sum = F.PlusAlloc(numParams);
    if (model != null) {
      sum.appendArgs(model.model);
    }
    if (functionSymbol == S.Power) {
      if (numParams == 1) {
        sum.append(F.Times(params.get(numParams), F.C1));
      } else {
        sum.append(F.Times(params.get(numParams), F.Power(var, F.ZZ(numParams - 1))));
      }
    } else {
      IExpr term = F.unaryAST1(functionSymbol, F.Times(params.get(numParams), var));
      sum.append(term);
    }
    return new ModelTemplate("SumF", sum, params);
  }

  /**
   * Generates a polynomial model of the form: #1 + #2*x + #3*x^2 + ... + #n*x^(n-1).
   * 
   * @param numParams the number of {@link S#Slot}s to use
   * @param var the independent variable
   * @return the polynomial model template
   */
  private static ModelTemplate generatePolynomialModel(int numParams, IExpr var) {
    if (numParams < 1) {
      return null;
    }
    // Use p1, p2, ... as parameter names to avoid conflict and allow for more params
    // IAST params = F.List();

    IASTAppendable params = F.ListAlloc(numParams);
    for (int i = 1; i <= numParams; i++) {
      params.append(F.Slot(i));
    }
    params.append(F.Slot(numParams));

    IASTAppendable sum = F.PlusAlloc(numParams);
    // #1
    sum.append(params.get(1));
    // #2*x + #3*x^2 + ...
    for (int i = 2; i <= numParams; i++) {
      IExpr term = F.Times(params.get(i), F.Power(var, F.ZZ(i - 1)));
      sum.append(term);
    }
    return new ModelTemplate("PolynomialX", sum, params);
  }

  /**
   * Generates models up to maxParameters {@link S#Slot}s and finds the best fitting one.
   * 
   * @param dataList
   * @param matrix
   * @param var
   * @param maxParameters
   * @param engine
   * @return
   */
  private static IExpr generateModels(IAST dataList, double[][] matrix, IExpr var,
      int maxParameters, EvalEngine engine) {
    ArrayList<ModelTemplate> modelLibrary = new ArrayList<>();
    List<ModelTemplate> tempLibrary = null;
    ModelTemplate polyModel = null;
    for (int j = 1; j <= maxParameters; j++) {
      int takeBestN = 3;
      if (j != 1) {
        ArrayList<ModelTemplate> library = new ArrayList<>();
        for (int k = 0; k < tempLibrary.size(); k++) {
          polyModel = tempLibrary.get(k);
          for (int i = 0; i < TARGET_FUNCTIONS.length; i++) {
            ModelTemplate newModel = generatePlusModel(j, var, TARGET_FUNCTIONS[i], polyModel);
            if (newModel != null) {
              library.add(newModel);
            }
          }
        }
        for (int k = 0; k < tempLibrary.size(); k++) {
          polyModel = tempLibrary.get(k);
          for (int i = 0; i < TARGET_FUNCTIONS.length; i++) {
            ModelTemplate newModel = generateTimesModel(j, var, TARGET_FUNCTIONS[i], polyModel);
            if (newModel != null) {
              library.add(newModel);
            }
          }
        }
        library.add(generatePolynomialModel(j, var));// one extra polynomial model
        tempLibrary = library;
        takeBestN = Math.min(2, tempLibrary.size());
      } else {
        tempLibrary = new ArrayList<>();
        for (int i = 0; i < TARGET_FUNCTIONS.length; i++) {
          ModelTemplate newModel = generatePlusModel(j, var, TARGET_FUNCTIONS[i], polyModel);
          if (newModel != null) {
            tempLibrary.add(newModel);
          }
        }
        for (int i = 0; i < TARGET_FUNCTIONS.length; i++) {
          ModelTemplate newModel = generateTimesModel(j, var, TARGET_FUNCTIONS[i], polyModel);
          if (newModel != null) {
            tempLibrary.add(newModel);
          }
        }
        tempLibrary.add(generatePolynomialModel(j, var));// one extra polynomial model
        takeBestN = Math.min(3, tempLibrary.size());
      }
      findFormulaParallelOptimized(dataList, matrix, var, tempLibrary, engine);
      tempLibrary.sort((a, b) -> a.aic > b.aic ? 1 : a.aic < b.aic ? -1 : 0);
      tempLibrary = tempLibrary.subList(0, takeBestN);
      modelLibrary.addAll(tempLibrary);
    }
    if (modelLibrary.size() > 0) {
      modelLibrary.sort((a, b) -> a.aic > b.aic ? 1 : a.aic < b.aic ? -1 : 0);
      return modelLibrary.get(0).fittedExpression;
    }
    return F.NIL;
  }

  private static FittedResult findFormulaParallelOptimized(IAST dataList, double[][] matrix,
      IExpr var, List<ModelTemplate> modelLibrary, EvalEngine engine) {

    final int length = dataList.argSize();
    final int threads = Math.max(1, Runtime.getRuntime().availableProcessors());
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    // ThreadLocal EvalEngine reuse to avoid repeated copyInit allocations
    final ThreadLocal<EvalEngine> threadEngine = ThreadLocal.withInitial(engine::copyInit);
    final AtomicReference<Double> bestBicRef = new AtomicReference<>(Double.POSITIVE_INFINITY);
    List<Future<?>> futures = new ArrayList<>(modelLibrary.size());

    for (ModelTemplate template : modelLibrary) {
      futures.add(pool.submit(() -> {
        // Skip if already evaluated by another task
        if (template.fittedExpression != null) {
          return;
        }

        IExpr modelWithVar = template.model;

        try {
          EvalEngine subEngine = threadEngine.get();
          IAST findFit = F.FindFit(dataList, modelWithVar, template.params, var);
          IExpr fitResult = subEngine.evalQuiet(findFit);

          if (fitResult.isList() && fitResult.size() > 0 && ((IAST) fitResult).get(1).isRule()) {
            IAST fitRules = (IAST) fitResult;
            IExpr fittedExpr = F.subst(modelWithVar, fitRules);

            // Compute RSS with early bail-out using current best BIC
            double rss = 0.0;
            int k = template.params.size();
            // derive a current max RSS threshold from bestBicRef if available
            Double currentBestBic = bestBicRef.get();
            Double maxRssThreshold = null;
            if (!currentBestBic.isInfinite()) {
              // invert BIC = n * ln(RSS/n) + k * ln(n)
              // => ln(RSS/n) <= (bestBic - k*ln(n)) / n
              double rhs = (currentBestBic - k * Math.log(length)) / length;
              maxRssThreshold = Math.exp(rhs) * length;
            }

            for (double[] point : matrix) {
              try {
                IExpr exprWithX = F.subst(fittedExpr, e -> e.equals(var), F.num(point[0]));
                double yFitted = exprWithX.evalf();
                double residual = yFitted - point[1];
                rss += residual * residual;

                if (maxRssThreshold != null && rss > maxRssThreshold) {
                  // can't beat current best BIC; early stop
                  return;
                }
              } catch (ArgumentTypeException ate) {
                // invalid evaluation for this model -> discard
                return;
              }
            }

            if (rss <= 1e-10) {
              rss = Config.DEFAULT_CHOP_DELTA;
            }
            double bic = length * Math.log(rss / length) + k * Math.log(length);

            // publish results atomically: update template and possibly bestBicRef
            synchronized (template) {
              // Only set if better than previously set for this template (rare)
              if (template.fittedExpression == null || bic < template.aic) {
                template.aic = bic;
                template.fittedExpression = fittedExpr;
                template.rules = fitRules;
              }
            }
            // update global best bic if improved
            bestBicRef.getAndUpdate(prev -> bic < prev ? bic : prev);
          }
        } catch (RuntimeException rex) {
          // Respect interrupt exceptions
          Errors.rethrowsInterruptException(rex);
        }
      }));
    }

    // wait for completion
    for (Future<?> f : futures) {
      try {
        f.get();
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        break;
      } catch (ExecutionException ee) {
        // ignore per-task exceptions (they were handled above)
      }
    }

    pool.shutdownNow();

    // pick best result
    return modelLibrary.stream().filter(m -> m.fittedExpression != null)
        .min(ModelTemplate::compareTo)
        .map(m -> new FittedResult(m, m.fittedExpression, m.aic, m.rules)).orElse(null);
  }


  /**
   * Finds the best-fitting formula for the given data.
   * 
   * @param dataList
   * @param matrix
   * @param var
   * @param modelLibrary
   * @param engine
   * @return
   */
  private static FittedResult findFormulaParallel(IAST dataList, double[][] matrix, IExpr var,
      List<ModelTemplate> modelLibrary, EvalEngine engine) {

    int length = dataList.argSize();

    modelLibrary.parallelStream().forEach(template -> {
      if (template.fittedExpression != null) {
        return;
      }

      IExpr modelWithVar = F.subst(template.model, e -> e.equals(S.x), var);

      try {
        EvalEngine subEngine = engine.copyInit();
        IAST findFit = F.FindFit(dataList, modelWithVar, template.params, var);
        IExpr fitResult = subEngine.evalQuiet(findFit);

        if (fitResult.isList() && fitResult.size() > 0 && ((IAST) fitResult).get(1).isRule()) {
          IAST fitRules = (IAST) fitResult;
          IExpr fittedExpr = F.subst(modelWithVar, fitRules);
          double rss = 0.0;

          for (double[] point : matrix) {
            IExpr exprWithX = F.subst(fittedExpr, e -> e.equals(var), F.num(point[0]));
            try {
              double yFitted = exprWithX.evalf();
              double yActual = point[1];
              double residual = yFitted - yActual;
              rss += residual * residual;
            } catch (ArgumentTypeException ate) {
              rss = Double.NaN;
              break;
            }
          }

          if (Double.isNaN(rss)) {
            return;
          }

          int k = template.params.size();
          if (rss <= 1e-10) {
            rss = Config.DEFAULT_CHOP_DELTA;
          }
          // AIC = Akaike Information Criterion
          // double aic = length * Math.log(rss / length) + 2 * k;
          // BIC = Bayesian Information Criterion
          double bic = length * Math.log(rss / length) + k * Math.log(length);
          synchronized (template) {
            template.aic = bic;
            template.fittedExpression = fittedExpr;
            template.rules = fitRules;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // ignore - take next model
      }
    });

    return modelLibrary.stream().filter(m -> m.fittedExpression != null)
        .min(ModelTemplate::compareTo)
        .map(m -> new FittedResult(m, m.fittedExpression, m.aic, m.rules)).orElse(null);
  }

  /**
   * Finds the best-fitting formula for the given data.
   * 
   * @param dataList
   * @param matrix
   * @param var
   * @param modelLibrary
   * @param engine
   * @return
   */
  private static FittedResult findFormulaSequential(IAST dataList, double[][] matrix, IExpr var,
      List<ModelTemplate> modelLibrary, EvalEngine engine) {
    FittedResult bestResult = null;
    double minAIC = Double.POSITIVE_INFINITY;
    int length = dataList.argSize();
    int t = 0;
    while (t < modelLibrary.size()) {
      ModelTemplate template = modelLibrary.get(t);

      // Check if this is the new best model
      if (template.fittedExpression != null && template.aic < minAIC) {
        minAIC = template.aic;
        bestResult =
            new FittedResult(template, template.fittedExpression, template.aic, template.rules);
        // System.out.printf("TEMP best: %-12s (AIC: %.4f) -> %s\n", template.name, minAIC,
        // template.fittedExpression.toString());
        t++;
        continue;
      }

      IExpr modelWithVar = template.model;

      try {
        IAST findFit = F.FindFit(dataList, modelWithVar, template.params, var);
        IExpr fitResult = engine.evalQuiet(findFit);

        // Check if FindFit was successful. A successful fit returns a List of Rules.
        if (fitResult.isList() && fitResult.size() > 0 && ((IAST) fitResult).get(1).isRule()) {
          IAST fitRules = (IAST) fitResult;

          // Calculate RSS (Residual Sum of Squares)
          // https://en.wikipedia.org/wiki/Residual_sum_of_squares
          // Get the fitted expression
          IExpr fittedExpr = F.subst(modelWithVar, fitRules);
          double rss = 0.0;

          for (double[] point : matrix) {
            // Plug the x-value into the fitted expression
            IExpr exprWithX = F.subst(fittedExpr, e -> e.equals(var), F.num(point[0]));

            // Evaluate it numerically
            try {
              double yFitted = exprWithX.evalf();
              double yActual = point[1];

              double residual = yFitted - yActual;
              rss += residual * residual;
            } catch (ArgumentTypeException ate) {

            }
          }

          // Calculate AIC (Akaike Information Criterion)
          // https://en.wikipedia.org/wiki/Akaike_information_criterion
          int k = template.params.size(); // Number of parameters
          if (rss <= 1e-10) {
            rss = Config.DEFAULT_CHOP_DELTA; // Avoid log(0)
          }
          // AIC = n * log(RSS/n) + 2*k
          // double aic = length * Math.log(rss / length) + 2 * k;
          // template.aic = aic;
          // BIC = Bayesian Information Criterion
          double bic = length * Math.log(rss / length) + k * Math.log(length);
          template.aic = bic;
          template.fittedExpression = fittedExpr;
          template.rules = fitRules;
          // System.out.printf(">Not best: %-12s (AIC: %.4f) -> %s\n", template.name, aic,
          // fittedExpr.toString());

          // Check if this is the new best model
          if (template.aic < minAIC) {
            minAIC = template.aic;
            bestResult = new FittedResult(template, fittedExpr, template.aic, fitRules);
            // System.out.printf(" New best: %-12s (AIC: %.4f) -> %s\n", template.name, minAIC,
            // fittedExpr.toString());
          }
        }
        t++;
      } catch (RuntimeException rex) {
        // ignore - take next model
        // rex.printStackTrace();
        modelLibrary.remove(t);
      }
    }
    return bestResult;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, S.TargetFunctions, S.All);
  }
}
