package org.matheclipse.core.expression.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealVector;
import org.hipparchus.linear.SingularValueDecomposition;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A trained classifier: the training examples with their classes, their numeric encoding and the
 * fitted parameters of the method.
 *
 * <p>
 * An example is a scalar or a vector of fixed length. Every component (every <i>column</i> of the
 * training set) is either numeric - all its training values are real numbers - or nominal. A
 * numeric column is standardized with the mean and the (population) standard deviation of its
 * training values; a nominal column is encoded as one indicator per distinct training value (a value not
 * seen in training encodes as all zeros).
 *
 * <p>
 * Methods:
 * <ul>
 * <li><code>"LogisticRegression"</code>: multinomial logistic regression with the last class as
 * reference: a weight vector and an intercept for each other class, the log odds against the last
 * class. They are fitted by maximizing the likelihood with an L2 penalty
 * <code>(10^-6/2)*Sum(w^2)</code> on the weights (not on the intercepts), so that well separated
 * classes get probabilities close to, but not exactly, 0 and 1. The objective is strictly convex;
 * it is minimized by Newton steps with a halving line search, the step solved with the
 * pseudo-inverse of the Hessian, or with L-BFGS when there are more than
 * {@link #MAX_NEWTON_PARAMETERS} parameters.
 * <li><code>"NearestNeighbors"</code>: the <code>k</code> nearest training examples in the
 * encoded space (between equal distances the earlier training example); a class which
 * <code>m</code> of them have gets the probability proportional to <code>m + s*prior</code> with
 * the class prior <code>(examples of the class + 1)/(n + K)</code> for <code>n</code> training
 * examples and <code>K</code> classes. <code>s</code> is the suboption
 * <code>"DistributionSmoothing"</code>, by default <code>1/2</code>; <code>k</code> the suboption
 * <code>"NeighborsNumber"</code>, by default the <code>k</code> up to 25 of the least
 * leave-one-out cross-entropy (each training example classified by the others), the smaller one
 * between equal values.
 * <li><code>"Markov"</code>: for string examples (feature type <code>"Text"</code>), the default
 * for them. Per class the characters of its training strings are counted; a string's log
 * likelihood is the sum over its characters of <code>Log((count + 1/10)/(total + V/10))</code>
 * with <code>V</code> the number of distinct training characters, plus the log of the class prior
 * <code>(examples of the class + 1)/(examples + K)</code>.
 * </ul>
 * The probabilities are normalized from the log probabilities: shifted by their maximum,
 * exponentiated and multiplied by the reciprocal of their sum.
 * The decision is the class of greatest probability; between equal probabilities the last class
 * in canonical order.
 */
public final class ClassifierModel implements Serializable {

  private static final long serialVersionUID = -6047651187251457823L;

  public static final String LOGISTIC_REGRESSION = "LogisticRegression";

  public static final String MARKOV = "Markov";

  public static final String NEAREST_NEIGHBORS = "NearestNeighbors";

  /** The supported methods, sorted. */
  public static final String[] METHODS = {LOGISTIC_REGRESSION, MARKOV, NEAREST_NEIGHBORS};

  /** The default of the suboption <code>"DistributionSmoothing"</code> of nearest neighbors. */
  public static final double DEFAULT_DISTRIBUTION_SMOOTHING = 0.5;

  /** The additive smoothing of the character counts of the Markov method. */
  private static final double MARKOV_SMOOTHING = 0.1;

  /** The weight of the L2 penalty of the logistic regression. */
  private static final double L2_REGULARIZATION = 1.0e-6;

  private static final int MAX_NEWTON_ITERATIONS = 500;

  /**
   * Above this number of free parameters ((classes - 1) * (features + 1)) the logistic regression
   * is fitted with L-BFGS instead of Newton steps, whose dense Hessian has the square of it as
   * entries.
   */
  static final int MAX_NEWTON_PARAMETERS = 200;

  /** The number of steps L-BFGS remembers. */
  private static final int LBFGS_MEMORY = 10;

  private static final int MAX_LBFGS_ITERATIONS = 2000;

  /** The largest number of neighbors the automatic choice tries. */
  private static final int MAX_AUTOMATIC_NEIGHBORS = 25;

  /** The automatic choice of the number of neighbors leaves out at most this many examples. */
  private static final int MAX_CROSS_VALIDATION_QUERIES = 1000;

  /** Relative tolerance below which two probabilities or distances count as equal. */
  private static final double TIE_TOLERANCE = 1.0e-12;

  /** One component of the examples. */
  private static final class Column implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The distinct training values of a nominal column, sorted; <code>null</code> if numeric. */
    IExpr[] values;
    double mean;
    double scale = 1.0;
    /** The first index of the column in the encoded vector. */
    int offset;

    int width() {
      return values == null ? 1 : values.length;
    }
  }

  private final String method;

  /** The training set <code>{x1 -&gt; c1, x2 -&gt; c2, ...}</code>. */
  private final IAST trainingSet;

  /** The distinct classes, in canonical order. */
  private final IAST classes;

  /** The length of a vector example, or <code>-1</code> for scalar examples. */
  private final int vectorLength;

  private final Column[] columns;

  private final int encodedWidth;

  private final double[][] encoded;

  private final int[] labels;

  /** The weights of the logistic regression, per class the intercept first. */
  private double[][] weights;

  /** The number of nearest neighbors. */
  private int neighbors;

  /** The suboption <code>"DistributionSmoothing"</code> of nearest neighbors. */
  private double distributionSmoothing = DEFAULT_DISTRIBUTION_SMOOTHING;

  /** <code>true</code> if the examples are strings, whose feature type is <code>"Text"</code>. */
  private final boolean text;

  /** The Markov method: per character its count in the training strings of each class. */
  private HashMap<Integer, int[]> characterCounts;

  /** The Markov method: the number of characters of the training strings of each class. */
  private int[] characterTotals;

  /** The number of training examples of each class. */
  private int[] classCounts;

  private ClassifierModel(String method, IAST trainingSet, IAST classes, int vectorLength,
      Column[] columns, int encodedWidth, double[][] encoded, int[] labels, boolean text) {
    this.text = text;
    this.method = method;
    this.trainingSet = trainingSet;
    this.classes = classes;
    this.vectorLength = vectorLength;
    this.columns = columns;
    this.encodedWidth = encodedWidth;
    this.encoded = encoded;
    this.labels = labels;
  }

  /**
   * <code>true</code> if the examples of a training set are strings, which the Markov method
   * classifies.
   */
  public static boolean isTextTrainingSet(IAST trainingSet) {
    return trainingSet.argSize() > 0 && trainingSet.forAll(rule -> rule.first().isString());
  }

  /**
   * Train a classifier.
   *
   * @param method one of {@link #METHODS}, or <code>null</code> for the automatic choice: Markov
   *        for strings, logistic regression otherwise
   * @param neighborsNumber the number of nearest neighbors, or <code>0</code> for the default
   * @param distributionSmoothing the suboption <code>"DistributionSmoothing"</code> of nearest
   *        neighbors
   * @param trainingSet a non-empty list of rules <code>example -&gt; class</code>
   * @param engine the evaluation engine
   * @return <code>null</code> if the examples are neither all scalars nor all vectors of one
   *         length, hold fewer than two classes, or the method is Markov and the examples
   *         aren't strings
   */
  public static ClassifierModel train(String method, int neighborsNumber,
      double distributionSmoothing, IAST trainingSet, EvalEngine engine) {
    final int n = trainingSet.argSize();
    if (n == 0) {
      return null;
    }
    int vectorLength = exampleShape(trainingSet);
    if (vectorLength == Integer.MIN_VALUE) {
      return null;
    }
    final boolean text = isTextTrainingSet(trainingSet);
    if (method == null) {
      method = text ? MARKOV : LOGISTIC_REGRESSION;
    } else if (MARKOV.equals(method) && !text) {
      return null;
    }
    final int m = vectorLength < 0 ? 1 : vectorLength;
    IExpr[][] cells = new IExpr[n][];
    for (int i = 0; i < n; i++) {
      cells[i] = components(trainingSet.get(i + 1).first(), vectorLength);
    }

    // the columns
    Column[] columns = new Column[m];
    int offset = 0;
    for (int j = 0; j < m; j++) {
      Column column = new Column();
      double[] numbers = new double[n];
      boolean numeric = true;
      for (int i = 0; i < n; i++) {
        numbers[i] = numericValue(cells[i][j], engine);
        if (Double.isNaN(numbers[i])) {
          numeric = false;
          break;
        }
      }
      if (numeric) {
        double mean = 0.0;
        for (double x : numbers) {
          mean += x;
        }
        mean /= n;
        double variance = 0.0;
        for (double x : numbers) {
          variance += (x - mean) * (x - mean);
        }
        double sd = Math.sqrt(variance / n);
        column.mean = mean;
        column.scale = sd > 0.0 ? sd : 1.0;
      } else {
        List<IExpr> distinct = new ArrayList<IExpr>();
        for (int i = 0; i < n; i++) {
          if (!distinct.contains(cells[i][j])) {
            distinct.add(cells[i][j]);
          }
        }
        IExpr[] values = distinct.toArray(new IExpr[distinct.size()]);
        Arrays.sort(values);
        column.values = values;
      }
      column.offset = offset;
      offset += column.width();
      columns[j] = column;
    }

    // the classes
    List<IExpr> distinctClasses = new ArrayList<IExpr>();
    for (int i = 1; i <= n; i++) {
      IExpr c = trainingSet.get(i).second();
      if (!distinctClasses.contains(c)) {
        distinctClasses.add(c);
      }
    }
    IExpr[] classArray = distinctClasses.toArray(new IExpr[distinctClasses.size()]);
    Arrays.sort(classArray);
    IASTAppendable classes = F.ListAlloc(classArray.length);
    for (IExpr c : classArray) {
      classes.append(c);
    }
    if (classArray.length < 2) {
      return null;
    }
    int[] labels = new int[n];
    double[][] encoded = new double[n][];
    for (int i = 0; i < n; i++) {
      labels[i] = Arrays.binarySearch(classArray, trainingSet.get(i + 1).second());
      encoded[i] = encode(cells[i], columns, offset, engine);
    }

    ClassifierModel model = new ClassifierModel(method, trainingSet, classes, vectorLength,
        columns, offset, encoded, labels, text);
    model.classCounts = new int[classArray.length];
    for (int label : labels) {
      model.classCounts[label]++;
    }
    if (LOGISTIC_REGRESSION.equals(method)) {
      model.fitLogisticRegression(engine);
    } else if (MARKOV.equals(method)) {
      model.countCharacters();
    } else {
      model.distributionSmoothing = distributionSmoothing;
      model.neighbors = neighborsNumber > 0 ? Math.min(n, neighborsNumber)
          : model.crossValidatedNeighbors(engine);
    }
    return model;
  }

  /**
   * The length of the vector examples of the training set.
   *
   * @return <code>-1</code> if all examples are scalars, {@link Integer#MIN_VALUE} if they are
   *         mixed or of different lengths
   */
  private static int exampleShape(IAST trainingSet) {
    IExpr first = trainingSet.arg1().first();
    int vectorLength = first.isList() ? first.argSize() : -1;
    if (vectorLength == 0) {
      return Integer.MIN_VALUE;
    }
    for (int i = 1; i < trainingSet.size(); i++) {
      if (!isExample(trainingSet.get(i).first(), vectorLength)) {
        return Integer.MIN_VALUE;
      }
    }
    return vectorLength;
  }

  private static boolean isExample(IExpr x, int vectorLength) {
    if (vectorLength < 0) {
      return !x.isList() && !x.isAssociation();
    }
    return x.isList() && x.argSize() == vectorLength && ((IAST) x).forAll(y -> !y.isList());
  }

  private static IExpr[] components(IExpr x, int vectorLength) {
    if (vectorLength < 0) {
      return new IExpr[] {x};
    }
    IExpr[] result = new IExpr[vectorLength];
    for (int j = 0; j < vectorLength; j++) {
      result[j] = ((IAST) x).get(j + 1);
    }
    return result;
  }

  /** The value of a real number, or <code>NaN</code> if <code>x</code> is not one. */
  private static double numericValue(IExpr x, EvalEngine engine) {
    double value = Double.NaN;
    if (x.isReal()) {
      value = x.evalf();
    } else if (x.isNumericFunction(true)) {
      value = engine.evalDoubleNaN(x);
    }
    return Double.isFinite(value) ? value : Double.NaN;
  }

  /**
   * The encoded vector of an example.
   *
   * @return <code>null</code> if a numeric component isn't a real number
   */
  private static double[] encode(IExpr[] cells, Column[] columns, int width, EvalEngine engine) {
    double[] result = new double[width];
    for (int j = 0; j < columns.length; j++) {
      Column column = columns[j];
      if (column.values == null) {
        double x = numericValue(cells[j], engine);
        if (Double.isNaN(x)) {
          return null;
        }
        result[column.offset] = (x - column.mean) / column.scale;
      } else {
        int index = Arrays.binarySearch(column.values, cells[j]);
        if (index >= 0 && column.values[index].equals(cells[j])) {
          result[column.offset + index] = 1.0;
        }
      }
    }
    return result;
  }

  /**
   * The first component of the example <code>x</code> which is no real number where the training
   * values are numeric.
   *
   * @return {@link F#NIL} if every numeric component of <code>x</code> is a real number
   */
  public IExpr incompatibleValue(IExpr x, EvalEngine engine) {
    if (MARKOV.equals(method)) {
      return x.isString() ? F.NIL : x;
    }
    IExpr[] cells = components(x, vectorLength);
    for (int j = 0; j < columns.length; j++) {
      if (columns[j].values == null && Double.isNaN(numericValue(cells[j], engine))) {
        return cells[j];
      }
    }
    return F.NIL;
  }

  /** The type of the values {@link #incompatibleValue(IExpr, EvalEngine)} reports. */
  public String valueType() {
    return MARKOV.equals(method) ? "Text" : "Numerical";
  }

  /** <code>true</code> if <code>x</code> is one example, not a list of examples. */
  public boolean isExample(IExpr x) {
    return isExample(x, vectorLength);
  }

  /**
   * The probabilities of the classes for an example, in the order of {@link #classes()}.
   *
   * @return <code>null</code> if the example can't be encoded, for example a symbol where the
   *         training values are numbers
   */
  public double[] probabilities(IExpr x, EvalEngine engine) {
    if (!isExample(x)) {
      return null;
    }
    if (MARKOV.equals(method)) {
      return x.isString() ? markovProbabilities(x.toString()) : null;
    }
    double[] z = encode(components(x, vectorLength), columns, encodedWidth, engine);
    if (z == null) {
      return null;
    }
    int k = classes.argSize();
    if (k == 1) {
      return new double[] {1.0};
    }
    if (LOGISTIC_REGRESSION.equals(method)) {
      return softmax(weights, z);
    }
    return nearestNeighborProbabilities(z, k);
  }

  /** The index of the decided class for the given probabilities. */
  public static int decision(double[] probabilities) {
    int best = 0;
    for (int c = 1; c < probabilities.length; c++) {
      // between equal probabilities the later class
      if (probabilities[c] >= probabilities[best] * (1.0 - TIE_TOLERANCE) - TIE_TOLERANCE) {
        best = c;
      }
    }
    return best;
  }

  /**
   * Probabilities from log probabilities: shifted by the maximum, exponentiated and multiplied by
   * the reciprocal of their sum.
   */
  private static double[] normalizeLogProbabilities(double[] logp) {
    double max = Double.NEGATIVE_INFINITY;
    for (double v : logp) {
      max = Math.max(max, v);
    }
    double[] p = new double[logp.length];
    double total = 0.0;
    for (int c = 0; c < logp.length; c++) {
      p[c] = Math.exp(logp[c] - max);
      total += p[c];
    }
    double reciprocal = 1.0 / total;
    for (int c = 0; c < p.length; c++) {
      p[c] *= reciprocal;
    }
    return p;
  }

  private void countCharacters() {
    int k = classes.argSize();
    characterCounts = new HashMap<Integer, int[]>();
    characterTotals = new int[k];
    for (int i = 0; i < labels.length; i++) {
      String example = trainingSet.get(i + 1).first().toString();
      final int label = labels[i];
      example.codePoints().forEach(ch -> {
        characterCounts.computeIfAbsent(ch, key -> new int[k])[label]++;
        characterTotals[label]++;
      });
    }
  }

  private double[] markovProbabilities(String x) {
    int k = classes.argSize();
    int n = labels.length;
    int alphabet = characterCounts.size();
    double[] logp = new double[k];
    for (int c = 0; c < k; c++) {
      logp[c] = Math.log((classCounts[c] + 1.0) / (n + k));
    }
    x.codePoints().forEach(ch -> {
      int[] counts = characterCounts.get(ch);
      for (int c = 0; c < k; c++) {
        double count = counts == null ? 0.0 : counts[c];
        logp[c] += Math.log((count + MARKOV_SMOOTHING)
            / (characterTotals[c] + MARKOV_SMOOTHING * alphabet));
      }
    });
    return normalizeLogProbabilities(logp);
  }

  /**
   * The <code>count</code> training examples nearest to <code>z</code> by increasing distance,
   * between equal distances the earlier one first. A max-heap of the <code>count</code> nearest
   * found so far makes it <code>O(n*Log(count))</code>.
   *
   * @param skip a training example to leave out, or <code>-1</code>
   * @param count the number of examples wanted, at most the number of training examples (less one
   *        if one is left out)
   */
  private int[] nearest(double[] z, int skip, int count) {
    final int n = encoded.length;
    // the heap: its root is the farthest of the nearest found so far
    double[] heapDistance = new double[count];
    int[] heapIndex = new int[count];
    int size = 0;
    for (int i = 0; i < n; i++) {
      if (i == skip) {
        continue;
      }
      double sum = 0.0;
      for (int j = 0; j < encodedWidth; j++) {
        double d = encoded[i][j] - z[j];
        sum += d * d;
      }
      double distance = Math.sqrt(sum);
      if (size < count) {
        // sift up
        int child = size++;
        while (child > 0) {
          int parent = (child - 1) >>> 1;
          if (!farther(distance, i, heapDistance[parent], heapIndex[parent])) {
            break;
          }
          heapDistance[child] = heapDistance[parent];
          heapIndex[child] = heapIndex[parent];
          child = parent;
        }
        heapDistance[child] = distance;
        heapIndex[child] = i;
      } else if (farther(heapDistance[0], heapIndex[0], distance, i)) {
        // replace the root and sift down
        int parent = 0;
        while (true) {
          int child = 2 * parent + 1;
          if (child >= size) {
            break;
          }
          if (child + 1 < size && farther(heapDistance[child + 1], heapIndex[child + 1],
              heapDistance[child], heapIndex[child])) {
            child++;
          }
          if (!farther(heapDistance[child], heapIndex[child], distance, i)) {
            break;
          }
          heapDistance[parent] = heapDistance[child];
          heapIndex[parent] = heapIndex[child];
          parent = child;
        }
        heapDistance[parent] = distance;
        heapIndex[parent] = i;
      }
    }
    // take the farthest off the heap until it is empty
    int[] result = new int[size];
    for (int last = size - 1; last >= 0; last--) {
      result[last] = heapIndex[0];
      double distance = heapDistance[last];
      int index = heapIndex[last];
      int parent = 0;
      while (true) {
        int child = 2 * parent + 1;
        if (child >= last) {
          break;
        }
        if (child + 1 < last && farther(heapDistance[child + 1], heapIndex[child + 1],
            heapDistance[child], heapIndex[child])) {
          child++;
        }
        if (!farther(heapDistance[child], heapIndex[child], distance, index)) {
          break;
        }
        heapDistance[parent] = heapDistance[child];
        heapIndex[parent] = heapIndex[child];
        parent = child;
      }
      heapDistance[parent] = distance;
      heapIndex[parent] = index;
    }
    return result;
  }

  /**
   * <code>true</code> if the example <code>i1</code> at distance <code>d1</code> comes after the
   * example <code>i2</code> at distance <code>d2</code>: farther, or as far and later.
   */
  private static boolean farther(double d1, int i1, double d2, int i2) {
    int byDistance = Double.compare(d1, d2);
    return byDistance > 0 || (byDistance == 0 && i1 > i2);
  }

  /**
   * The log probabilities of the classes for the counts of the classes among the neighbors.
   *
   * @param classCounts the number of training examples per class
   * @param n the number of training examples
   */
  private double[] nearestNeighborLogProbabilities(double[] counts, int[] classCounts, int n) {
    int k = counts.length;
    double[] logp = new double[k];
    for (int c = 0; c < k; c++) {
      double prior = (classCounts[c] + 1.0) / (n + k);
      logp[c] = Math.log(counts[c] + distributionSmoothing * prior);
    }
    return logp;
  }

  private double[] nearestNeighborProbabilities(double[] z, int k) {
    int[] nearest = nearest(z, -1, neighbors);
    double[] counts = new double[k];
    for (int i : nearest) {
      counts[labels[i]] += 1.0;
    }
    return normalizeLogProbabilities(
        nearestNeighborLogProbabilities(counts, classCounts, encoded.length));
  }

  /**
   * The number of neighbors up to {@link #MAX_AUTOMATIC_NEIGHBORS} with the least leave-one-out
   * cross-entropy, the smaller one between equal values.
   */
  private int crossValidatedNeighbors(EvalEngine engine) {
    final int n = encoded.length;
    final int k = classes.argSize();
    final int maxNeighbors = Math.min(n - 1, MAX_AUTOMATIC_NEIGHBORS);
    if (maxNeighbors < 2) {
      return 1;
    }
    double[] crossEntropy = new double[maxNeighbors + 1];
    final int queries = Math.min(n, MAX_CROSS_VALIDATION_QUERIES);
    for (int i = 0; i < queries; i++) {
      engine.checkTimeLimit();
      int[] order = nearest(encoded[i], i, maxNeighbors);
      int[] reducedCounts = classCounts.clone();
      reducedCounts[labels[i]]--;
      double[] counts = new double[k];
      for (int m = 1; m <= maxNeighbors; m++) {
        counts[labels[order[m - 1]]] += 1.0;
        double[] p = normalizeLogProbabilities(
            nearestNeighborLogProbabilities(counts, reducedCounts, n - 1));
        crossEntropy[m] -= Math.log(p[labels[i]]);
      }
    }
    int best = 1;
    for (int m = 2; m <= maxNeighbors; m++) {
      if (crossEntropy[m] < crossEntropy[best] * (1.0 - TIE_TOLERANCE) - TIE_TOLERANCE) {
        best = m;
      }
    }
    return best;
  }

  private static double[] softmax(double[][] weights, double[] z) {
    int k = weights.length;
    double[] logits = new double[k];
    for (int c = 0; c < k; c++) {
      double s = weights[c][0];
      for (int j = 0; j < z.length; j++) {
        s += weights[c][j + 1] * z[j];
      }
      logits[c] = s;
    }
    return normalizeLogProbabilities(logits);
  }

  /** The penalized negative log-likelihood of the parameters. */
  private double objective(double[][] w, EvalEngine engine) {
    double loss = 0.0;
    for (int i = 0; i < encoded.length; i++) {
      engine.checkTimeLimit();
      double[] p = softmax(w, encoded[i]);
      loss -= Math.log(Math.max(p[labels[i]], Double.MIN_NORMAL));
    }
    for (double[] row : w) {
      for (int j = 1; j < row.length; j++) {
        loss += 0.5 * L2_REGULARIZATION * row[j] * row[j];
      }
    }
    return loss;
  }

  private void fitLogisticRegression(EvalEngine engine) {
    final int k = classes.argSize();
    final int d = encodedWidth + 1;
    final int n = encoded.length;
    double[][] w = new double[k][d];
    weights = w;
    if (k == 1) {
      return;
    }
    // the last class is the reference; its weights stay zero
    final int free = k - 1;
    final int size = free * d;
    if (size > MAX_NEWTON_PARAMETERS) {
      // the Hessian would be too large: a limited-memory quasi-Newton fit of the same objective
      weights = fitLogisticRegressionLBFGS(free, d, engine);
      return;
    }
    double loss = objective(w, engine);
    for (int iteration = 0; iteration < MAX_NEWTON_ITERATIONS; iteration++) {
      engine.checkTimeLimit();
      double[] gradient = new double[size];
      double[][] hessian = new double[size][size];
      for (int i = 0; i < n; i++) {
        engine.checkTimeLimit();
        double[] p = softmax(w, encoded[i]);
        double[] x = new double[d];
        x[0] = 1.0;
        System.arraycopy(encoded[i], 0, x, 1, encodedWidth);
        for (int a = 0; a < free; a++) {
          double residual = p[a] - (labels[i] == a ? 1.0 : 0.0);
          for (int j = 0; j < d; j++) {
            gradient[a * d + j] += residual * x[j];
          }
          for (int b = 0; b < free; b++) {
            double factor = p[a] * ((a == b ? 1.0 : 0.0) - p[b]);
            if (factor == 0.0) {
              continue;
            }
            for (int j = 0; j < d; j++) {
              double fx = factor * x[j];
              for (int l = 0; l < d; l++) {
                hessian[a * d + j][b * d + l] += fx * x[l];
              }
            }
          }
        }
      }
      for (int a = 0; a < free; a++) {
        for (int j = 1; j < d; j++) {
          gradient[a * d + j] += L2_REGULARIZATION * w[a][j];
          hessian[a * d + j][a * d + j] += L2_REGULARIZATION;
        }
      }
      RealVector step = new SingularValueDecomposition(new Array2DRowRealMatrix(hessian, false))
          .getSolver().solve(new ArrayRealVector(gradient, false));
      double t = 1.0;
      double[][] candidate = new double[k][d];
      double candidateLoss = Double.POSITIVE_INFINITY;
      for (int halving = 0; halving < 40; halving++) {
        for (int a = 0; a < free; a++) {
          for (int j = 0; j < d; j++) {
            candidate[a][j] = w[a][j] - t * step.getEntry(a * d + j);
          }
        }
        candidateLoss = objective(candidate, engine);
        if (candidateLoss <= loss) {
          break;
        }
        t *= 0.5;
      }
      if (!(candidateLoss <= loss)) {
        break;
      }
      double change = t * step.getLInfNorm();
      double improvement = loss - candidateLoss;
      w = candidate;
      loss = candidateLoss;
      if (change < 1.0e-10 || improvement <= 1.0e-15 * (1.0 + Math.abs(loss))) {
        break;
      }
    }
    weights = w;
  }

  /** The weights of the classes for the free parameters <code>theta</code>, row by row. */
  private static double[][] unflatten(double[] theta, int k, int d) {
    double[][] w = new double[k][d];
    for (int a = 0; a < k - 1; a++) {
      System.arraycopy(theta, a * d, w[a], 0, d);
    }
    return w;
  }

  /**
   * The penalized negative log-likelihood of the free parameters and, into
   * <code>gradient</code>, its gradient.
   */
  private double objectiveAndGradient(double[] theta, double[] gradient, int free, int d,
      EvalEngine engine) {
    double[][] w = unflatten(theta, free + 1, d);
    Arrays.fill(gradient, 0.0);
    double loss = 0.0;
    for (int i = 0; i < encoded.length; i++) {
      engine.checkTimeLimit();
      double[] p = softmax(w, encoded[i]);
      loss -= Math.log(Math.max(p[labels[i]], Double.MIN_NORMAL));
      for (int a = 0; a < free; a++) {
        double residual = p[a] - (labels[i] == a ? 1.0 : 0.0);
        gradient[a * d] += residual;
        for (int j = 1; j < d; j++) {
          gradient[a * d + j] += residual * encoded[i][j - 1];
        }
      }
    }
    for (int a = 0; a < free; a++) {
      for (int j = 1; j < d; j++) {
        double v = theta[a * d + j];
        loss += 0.5 * L2_REGULARIZATION * v * v;
        gradient[a * d + j] += L2_REGULARIZATION * v;
      }
    }
    return loss;
  }

  /**
   * Minimize the objective of the logistic regression with L-BFGS: the last
   * {@link #LBFGS_MEMORY} steps approximate the inverse Hessian (two-loop recursion), the step
   * length is halved until it decreases the objective sufficiently (Armijo). Memory grows with the
   * number of parameters, not with its square.
   */
  private double[][] fitLogisticRegressionLBFGS(int free, int d, EvalEngine engine) {
    final int size = free * d;
    double[] x = new double[size];
    double[] g = new double[size];
    double f = objectiveAndGradient(x, g, free, d, engine);
    double[][] sHistory = new double[LBFGS_MEMORY][];
    double[][] yHistory = new double[LBFGS_MEMORY][];
    double[] rho = new double[LBFGS_MEMORY];
    double[] alpha = new double[LBFGS_MEMORY];
    int stored = 0;
    int newest = -1;
    double[] xNew = new double[size];
    double[] gNew = new double[size];
    for (int iteration = 0; iteration < MAX_LBFGS_ITERATIONS; iteration++) {
      engine.checkTimeLimit();
      double gMax = 0.0;
      for (double v : g) {
        gMax = Math.max(gMax, Math.abs(v));
      }
      if (gMax <= 1.0e-9 * (1.0 + Math.abs(f))) {
        break;
      }
      // the two-loop recursion: direction = -H*g
      double[] q = g.clone();
      for (int m = 0; m < stored; m++) {
        int slot = Math.floorMod(newest - m, LBFGS_MEMORY);
        alpha[slot] = rho[slot] * dot(sHistory[slot], q);
        axpy(-alpha[slot], yHistory[slot], q);
      }
      double scale = 1.0 / Math.max(1.0, Math.sqrt(dot(g, g)));
      if (stored > 0) {
        double[] sLast = sHistory[newest];
        double[] yLast = yHistory[newest];
        scale = dot(sLast, yLast) / dot(yLast, yLast);
      }
      for (int i = 0; i < size; i++) {
        q[i] *= scale;
      }
      for (int m = stored - 1; m >= 0; m--) {
        int slot = Math.floorMod(newest - m, LBFGS_MEMORY);
        double beta = rho[slot] * dot(yHistory[slot], q);
        axpy(alpha[slot] - beta, sHistory[slot], q);
      }
      double[] direction = q;
      for (int i = 0; i < size; i++) {
        direction[i] = -direction[i];
      }
      double slope = dot(g, direction);
      if (!(slope < 0.0)) {
        // not a descent direction: steepest descent, the history forgotten
        for (int i = 0; i < size; i++) {
          direction[i] = -g[i];
        }
        slope = -dot(g, g);
        stored = 0;
      }
      double t = 1.0;
      double fNew = Double.POSITIVE_INFINITY;
      for (int halving = 0; halving < 60; halving++) {
        for (int i = 0; i < size; i++) {
          xNew[i] = x[i] + t * direction[i];
        }
        fNew = objectiveAndGradient(xNew, gNew, free, d, engine);
        if (fNew <= f + 1.0e-4 * t * slope) {
          break;
        }
        t *= 0.5;
      }
      if (!(fNew <= f)) {
        break;
      }
      double[] step = new double[size];
      double[] change = new double[size];
      for (int i = 0; i < size; i++) {
        step[i] = xNew[i] - x[i];
        change[i] = gNew[i] - g[i];
      }
      double curvature = dot(step, change);
      if (curvature > 1.0e-12 * Math.sqrt(dot(step, step) * dot(change, change))) {
        newest = (newest + 1) % LBFGS_MEMORY;
        sHistory[newest] = step;
        yHistory[newest] = change;
        rho[newest] = 1.0 / curvature;
        stored = Math.min(stored + 1, LBFGS_MEMORY);
      }
      double improvement = f - fNew;
      System.arraycopy(xNew, 0, x, 0, size);
      System.arraycopy(gNew, 0, g, 0, size);
      f = fNew;
      if (improvement <= 1.0e-15 * (1.0 + Math.abs(f))) {
        break;
      }
    }
    return unflatten(x, free + 1, d);
  }

  private static double dot(double[] u, double[] v) {
    double sum = 0.0;
    for (int i = 0; i < u.length; i++) {
      sum += u[i] * v[i];
    }
    return sum;
  }

  /** <code>v = v + a*u</code> */
  private static void axpy(double a, double[] u, double[] v) {
    for (int i = 0; i < u.length; i++) {
      v[i] += a * u[i];
    }
  }

  /** The distinct classes, in canonical order. */
  public IAST classes() {
    return classes;
  }

  public String method() {
    return method;
  }

  /** The training set <code>{x1 -&gt; c1, x2 -&gt; c2, ...}</code>. */
  public IAST trainingSet() {
    return trainingSet;
  }

  /** The names <code>"f1", "f2", ...</code> of the components of the examples. */
  public IAST featureNames() {
    return F.mapRange(1, columns.length + 1, j -> F.stringx("f" + j));
  }

  /**
   * The association of the feature names with their types <code>"Numerical"</code>,
   * <code>"Nominal"</code> or <code>"Text"</code>.
   */
  public IExpr featureTypes() {
    IAST names = featureNames();
    return F.assoc(F.mapRange(0, columns.length, j -> F.Rule(names.get(j + 1), F.stringx(
        text ? "Text" : columns[j].values == null ? "Numerical" : "Nominal"))));
  }

  /**
   * The method with its suboptions, for example <code>{"NearestNeighbors", "NeighborsNumber" -&gt;
   * k, "DistributionSmoothing" -&gt; s}</code>.
   */
  public IAST methodSpecification() {
    if (NEAREST_NEIGHBORS.equals(method)) {
      return F.list(F.stringx(method), F.Rule(F.stringx("NeighborsNumber"), F.ZZ(neighbors)),
          F.Rule(F.stringx("DistributionSmoothing"), F.num(distributionSmoothing)));
    }
    if (LOGISTIC_REGRESSION.equals(method)) {
      return F.list(F.stringx(method),
          F.Rule(F.stringx("L2Regularization"), F.num(L2_REGULARIZATION)));
    }
    return F.list(F.stringx(method),
        F.Rule(F.stringx("AdditiveSmoothing"), F.num(MARKOV_SMOOTHING)));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ClassifierModel) {
      ClassifierModel other = (ClassifierModel) obj;
      return method.equals(other.method) && trainingSet.equals(other.trainingSet)
          && neighbors == other.neighbors
          && distributionSmoothing == other.distributionSmoothing;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 31 * (31 * method.hashCode() + trainingSet.hashCode()) + neighbors;
  }

  @Override
  public String toString() {
    return method;
  }
}
