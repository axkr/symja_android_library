package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.hipparchus.distribution.continuous.ChiSquaredDistribution;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.DecompositionSolver;
import org.hipparchus.linear.QRDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.special.Beta;
import org.hipparchus.special.Erf;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The <code>FittedModel</code> of a generalized linear model, which <code>GeneralizedLinearModelFit
 * </code>, <code>LogitModelFit</code> and <code>ProbitModelFit</code> return.
 *
 * <p>
 * The mean <code>mu</code> of the response is the inverse of a link function of the linear
 * combination <code>eta</code> of the basis functions, and its variance a function
 * <code>V(mu)</code> of the mean which depends on the exponential family of the response. The
 * parameters are the maximum likelihood estimates, found by iteratively reweighted least squares.
 */
public class GeneralizedFittedModelExpr
    extends AbstractFittedModelExpr<GeneralizedFittedModelExpr.Fit> implements Externalizable {

  private static final long serialVersionUID = 2263815236358290466L;

  private static final String[] PROPERTIES = {"AIC", "AnscombeResiduals", "BasisFunctions",
      "BestFit", "BestFitParameters", "BIC", "CovarianceMatrix", "DesignMatrix",
      "DevianceResiduals", "EstimatedDispersion", "ExponentialFamily", "FitResiduals",
      "IndependentVariables", "LogLikelihood", "NullDegreesOfFreedom", "NullDeviance",
      "ParameterConfidenceIntervals", "ParameterConfidenceRegion", "ParameterErrors",
      "ParameterPValues", "ParameterTable", "ParameterTableEntries", "ParameterZStatistics",
      "PearsonResiduals", "PredictedResponse", "ResidualDegreesOfFreedom", "ResidualDeviance",
      "Response"};

  /** The exponential families of the response. */
  public static final String[] FAMILIES =
      {"Binomial", "Gamma", "Gaussian", "InverseGaussian", "Poisson"};

  /** The most iterations of the reweighted least squares. */
  private static final int MAX_ITERATIONS = 200;

  /** A model, the data it is fitted to and its parameters. */
  public static final class Fit {
    final String family;
    final String link;
    final IAST basisFunctions;
    final IAST variables;
    final double[][] design;
    final double[] response;
    double[] parameters;

    public Fit(String family, String link, IAST basisFunctions, IAST variables, double[][] design,
        double[] response) {
      this.family = family;
      this.link = link;
      this.basisFunctions = basisFunctions;
      this.variables = variables;
      this.design = design;
      this.response = response;
    }
  }

  /** The canonical link function of an exponential family. */
  public static String canonicalLink(String family) {
    switch (family) {
      case "Binomial":
        return "LogitLink";
      case "Poisson":
        return "LogLink";
      case "Gamma":
        return "ReciprocalLink";
      case "InverseGaussian":
        return "InverseSquareLink";
      default:
        return "IdentityLink";
    }
  }

  /**
   * Whether a response value is in the range of the family: a probability for the binomial
   * family, a count which is not negative for Poisson, and a positive value for the gamma and
   * inverse Gaussian families.
   */
  public static boolean isResponse(String family, double y) {
    switch (family) {
      case "Binomial":
        return 0.0 <= y && y <= 1.0;
      case "Poisson":
        return y >= 0.0;
      case "Gamma":
      case "InverseGaussian":
        return y > 0.0;
      default:
        return !Double.isNaN(y);
    }
  }

  /**
   * Fit the model by iteratively reweighted least squares.
   *
   * @return the model, or <code>null</code> if the iteration doesn't reach a finite estimate
   */
  public static GeneralizedFittedModelExpr fit(Fit fit) {
    final int n = fit.response.length;
    final int p = fit.design[0].length;
    double[] mu = new double[n];
    double[] eta = new double[n];
    for (int i = 0; i < n; i++) {
      mu[i] = startValue(fit.family, fit.response[i]);
      eta[i] = link(fit.link, mu[i]);
    }
    double[] beta = new double[p];
    for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
      double[] next = weightedLeastSquares(fit, mu, eta, null);
      double change = 0.0;
      double size = 0.0;
      for (int j = 0; j < p; j++) {
        if (!Double.isFinite(next[j])) {
          return null;
        }
        change = Math.max(change, Math.abs(next[j] - beta[j]));
        size = Math.max(size, Math.abs(next[j]));
      }
      beta = next;
      for (int i = 0; i < n; i++) {
        eta[i] = 0.0;
        for (int j = 0; j < p; j++) {
          eta[i] += fit.design[i][j] * beta[j];
        }
        mu[i] = inverseLink(fit.link, eta[i]);
      }
      if (iteration > 0 && change <= 1.0e-14 * (1.0 + size)) {
        break;
      }
    }
    fit.parameters = beta;
    return new GeneralizedFittedModelExpr(fit);
  }

  /**
   * One step of the iteration: the least squares solution of <code>X.beta == z</code> with the
   * weights <code>w = mu'(eta)^2/V(mu)</code> and the working response
   * <code>z = eta + (y - mu)/mu'(eta)</code>.
   *
   * @param covariance if not <code>null</code>, receives <code>Inverse(Transpose(X).W.X)</code>
   */
  private static double[] weightedLeastSquares(Fit fit, double[] mu, double[] eta,
      double[][][] covariance) {
    final int n = fit.response.length;
    final int p = fit.design[0].length;
    double[][] weighted = new double[n][p];
    double[] z = new double[n];
    for (int i = 0; i < n; i++) {
      double derivative = inverseLinkDerivative(fit.link, eta[i], mu[i]);
      double root = Math.abs(derivative) / Math.sqrt(variance(fit.family, mu[i]));
      z[i] = root * (eta[i] + (fit.response[i] - mu[i]) / derivative);
      for (int j = 0; j < p; j++) {
        weighted[i][j] = root * fit.design[i][j];
      }
    }
    RealMatrix matrix = new Array2DRowRealMatrix(weighted, false);
    DecompositionSolver solver = n >= p ? new QRDecomposition(matrix, 1.0e-12).getSolver() : null;
    // the QR decomposition is the more accurate, the pseudo-inverse is for dependent columns
    RealMatrix inverse = solver != null && solver.isNonSingular() ? solver.getInverse()
        : LinearAlgebra.machinePseudoInverse(matrix);
    if (covariance != null) {
      covariance[0] = inverse.multiply(inverse.transpose()).getData();
    }
    return inverse.operate(z);
  }

  private static double startValue(String family, double y) {
    switch (family) {
      case "Binomial":
        return (y + 0.5) / 2.0;
      case "Poisson":
        return y + 0.1;
      default:
        return y;
    }
  }

  /** The variance function <code>V(mu)</code> of the family. */
  private static double variance(String family, double mu) {
    switch (family) {
      case "Binomial":
        return mu * (1.0 - mu);
      case "Poisson":
        return mu;
      case "Gamma":
        return mu * mu;
      case "InverseGaussian":
        return mu * mu * mu;
      default:
        return 1.0;
    }
  }

  private static double link(String link, double mu) {
    switch (link) {
      case "LogitLink":
        return Math.log(mu / (1.0 - mu));
      case "ProbitLink":
        return -Math.sqrt(2.0) * Erf.erfcInv(2.0 * mu);
      case "LogLink":
        return Math.log(mu);
      case "ReciprocalLink":
        return 1.0 / mu;
      case "InverseSquareLink":
        return 1.0 / (mu * mu);
      default:
        return mu;
    }
  }

  private static double inverseLink(String link, double eta) {
    switch (link) {
      case "LogitLink":
        return 1.0 / (1.0 + Math.exp(-eta));
      case "ProbitLink":
        return 0.5 * Erf.erfc(-eta / Math.sqrt(2.0));
      case "LogLink":
        return Math.exp(eta);
      case "ReciprocalLink":
        return 1.0 / eta;
      case "InverseSquareLink":
        return 1.0 / Math.sqrt(eta);
      default:
        return eta;
    }
  }

  /** The derivative of the mean by the linear predictor. */
  private static double inverseLinkDerivative(String link, double eta, double mu) {
    switch (link) {
      case "LogitLink":
        return mu * (1.0 - mu);
      case "ProbitLink":
        return Math.exp(-0.5 * eta * eta) / Math.sqrt(2.0 * Math.PI);
      case "LogLink":
        return mu;
      case "ReciprocalLink":
        return -mu * mu;
      case "InverseSquareLink":
        return -0.5 * mu * mu * mu;
      default:
        return 1.0;
    }
  }

  /** The inverse link function of the linear predictor <code>eta</code> as an expression. */
  private static IExpr inverseLink(String link, IExpr eta) {
    switch (link) {
      case "LogitLink":
        return F.Power(F.Plus(F.C1, F.Exp(F.Negate(eta))), F.CN1);
      case "ProbitLink":
        return F.Times(F.C1D2, F.Erfc(F.Times(F.CN1, F.Power(F.C2, F.CN1D2), eta)));
      case "LogLink":
        return F.Exp(eta);
      case "ReciprocalLink":
        return F.Power(eta, F.CN1);
      case "InverseSquareLink":
        return F.Power(eta, F.CN1D2);
      default:
        return eta;
    }
  }

  /** The contribution of one point to the deviance. */
  private static double unitDeviance(String family, double y, double mu) {
    switch (family) {
      case "Binomial":
        return 2.0 * (xLogY(y, y / mu) + xLogY(1.0 - y, (1.0 - y) / (1.0 - mu)));
      case "Poisson":
        return 2.0 * (xLogY(y, y / mu) - (y - mu));
      case "Gamma":
        return 2.0 * (-Math.log(y / mu) + (y - mu) / mu);
      case "InverseGaussian":
        return (y - mu) * (y - mu) / (mu * mu * y);
      default:
        return (y - mu) * (y - mu);
    }
  }

  /** <code>x*Log(y)</code>, which is <code>0</code> for <code>x == 0</code>. */
  private static double xLogY(double x, double y) {
    return x == 0.0 ? 0.0 : x * Math.log(y);
  }

  /** The Anscombe residual, the residual of a transformation which makes it nearly normal. */
  private static double anscombe(String family, double y, double mu) {
    switch (family) {
      case "Binomial":
        return (incompleteBeta(y) - incompleteBeta(mu)) / Math.pow(mu * (1.0 - mu), 1.0 / 6.0);
      case "Poisson":
        return 1.5 * (Math.pow(y, 2.0 / 3.0) - Math.pow(mu, 2.0 / 3.0)) / Math.pow(mu, 1.0 / 6.0);
      case "Gamma":
        return 3.0 * (Math.cbrt(y) - Math.cbrt(mu)) / Math.cbrt(mu);
      case "InverseGaussian":
        return (Math.log(y) - Math.log(mu)) / Math.sqrt(mu);
      default:
        return y - mu;
    }
  }

  /** The incomplete beta function <code>Beta(z, 2/3, 2/3)</code>. */
  private static double incompleteBeta(double z) {
    final double a = 2.0 / 3.0;
    return Beta.regularizedBeta(z, a, a) * Math.exp(Beta.logBeta(a, a));
  }

  /** The logarithm of the likelihood of one point, given the dispersion. */
  private static double logLikelihood(String family, double y, double mu, double dispersion) {
    switch (family) {
      case "Binomial":
        return xLogY(y, mu) + xLogY(1.0 - y, 1.0 - mu);
      case "Poisson":
        return xLogY(y, mu) - mu - Gamma.logGamma(y + 1.0);
      case "Gamma": {
        double shape = 1.0 / dispersion;
        return shape * Math.log(shape * y / mu) - shape * y / mu - Math.log(y)
            - Gamma.logGamma(shape);
      }
      case "InverseGaussian":
        return -0.5 * Math.log(2.0 * Math.PI * dispersion * y * y * y)
            - (y - mu) * (y - mu) / (2.0 * dispersion * mu * mu * y);
      default:
        return -0.5 * (Math.log(2.0 * Math.PI * dispersion) + (y - mu) * (y - mu) / dispersion);
    }
  }

  /** No-argument constructor required for {@link Externalizable} deserialization. */
  public GeneralizedFittedModelExpr() {
    super(null);
  }

  public GeneralizedFittedModelExpr(Fit fit) {
    super(fit);
  }

  /** The linear predictor, the combination of the basis functions. */
  private IExpr linearPredictor() {
    Fit fit = toData();
    IASTAppendable sum = F.PlusAlloc(fit.parameters.length);
    for (int j = 0; j < fit.parameters.length; j++) {
      sum.append(F.Times(F.num(fit.parameters[j]), fit.basisFunctions.get(j + 1)));
    }
    return EvalEngine.get().evaluate(sum);
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    IExpr bestFit =
        EvalEngine.get().evaluate(inverseLink(toData().link, linearPredictor()));
    return bestFit.isAST() ? (IAST) bestFit : F.Plus(bestFit);
  }

  private double[] predicted() {
    Fit fit = toData();
    double[] mu = new double[fit.response.length];
    for (int i = 0; i < mu.length; i++) {
      double eta = 0.0;
      for (int j = 0; j < fit.parameters.length; j++) {
        eta += fit.design[i][j] * fit.parameters[j];
      }
      mu[i] = inverseLink(fit.link, eta);
    }
    return mu;
  }

  private int residualDegreesOfFreedom() {
    return toData().response.length - toData().parameters.length;
  }

  /** Whether some basis function is a non-zero constant at all the data points. */
  private boolean hasConstant() {
    double[][] x = toData().design;
    for (int j = 0; j < x[0].length; j++) {
      boolean same = x[0][j] != 0.0;
      for (int i = 1; i < x.length && same; i++) {
        same = x[i][j] == x[0][j];
      }
      if (same) {
        return true;
      }
    }
    return false;
  }

  /** The dispersion: <code>1</code> for binomial and Poisson data, else the Pearson estimate. */
  private double dispersion() {
    Fit fit = toData();
    if (fit.family.equals("Binomial") || fit.family.equals("Poisson")) {
      return 1.0;
    }
    double[] mu = predicted();
    double sum = 0.0;
    for (int i = 0; i < mu.length; i++) {
      double r = fit.response[i] - mu[i];
      sum += r * r / variance(fit.family, mu[i]);
    }
    return sum / residualDegreesOfFreedom();
  }

  private double deviance(double[] mu) {
    Fit fit = toData();
    double sum = 0.0;
    for (int i = 0; i < mu.length; i++) {
      sum += unitDeviance(fit.family, fit.response[i], mu[i]);
    }
    return sum;
  }

  /**
   * The deviance of the model without basis functions but the constant, whose mean is the mean of
   * the data, or of the model with a zero linear predictor if there is no constant.
   */
  private double nullDeviance() {
    Fit fit = toData();
    double mean = hasConstant() ? Arrays.stream(fit.response).average().orElse(0.0)
        : inverseLink(fit.link, 0.0);
    double[] mu = new double[fit.response.length];
    Arrays.fill(mu, mean);
    return deviance(mu);
  }

  private double logLikelihood() {
    Fit fit = toData();
    double[] mu = predicted();
    double dispersion = dispersion();
    double sum = 0.0;
    for (int i = 0; i < mu.length; i++) {
      sum += logLikelihood(fit.family, fit.response[i], mu[i], dispersion);
    }
    return sum;
  }

  /** The covariance matrix of the parameters, the dispersion times the inverse information. */
  private double[][] covarianceMatrix() {
    Fit fit = toData();
    double[] mu = predicted();
    double[] eta = new double[mu.length];
    for (int i = 0; i < mu.length; i++) {
      eta[i] = link(fit.link, mu[i]);
    }
    double[][][] covariance = new double[1][][];
    weightedLeastSquares(fit, mu, eta, covariance);
    double dispersion = dispersion();
    double[][] result = covariance[0];
    for (double[] row : result) {
      for (int j = 0; j < row.length; j++) {
        row[j] *= dispersion;
      }
    }
    return result;
  }

  /**
   * One row <code>{estimate, standard error, z, p}</code> per parameter, with the two-tailed
   * p-value of the standard normal distribution.
   */
  private IExpr[][] parameterStatistics() {
    double[] beta = toData().parameters;
    double[][] covariance = covarianceMatrix();
    IExpr[][] rows = new IExpr[beta.length][];
    for (int i = 0; i < beta.length; i++) {
      double error = Math.sqrt(covariance[i][i]);
      double z = beta[i] / error;
      rows[i] = new IExpr[] {F.num(beta[i]), F.num(error), F.num(z),
          F.num(Erf.erfc(Math.abs(z) / Math.sqrt(2.0)))};
    }
    return rows;
  }

  /** One value per data point. */
  private interface PointFunction {
    double apply(String family, double y, double mu);
  }

  private IAST residuals(PointFunction function) {
    Fit fit = toData();
    double[] mu = predicted();
    return F.mapRange(0, mu.length, i -> F.num(function.apply(fit.family, fit.response[i], mu[i])));
  }

  @Override
  protected String[] propertyNames() {
    return PROPERTIES;
  }

  @Override
  protected IExpr property(String name, EvalEngine engine) {
    Fit fit = toData();
    final int n = fit.response.length;
    final int p = fit.parameters.length;
    switch (name) {
      case "AIC":
        return F.num(-2.0 * logLikelihood() + 2.0 * (p + 1));
      case "AnscombeResiduals":
        return residuals(GeneralizedFittedModelExpr::anscombe);
      case "BasisFunctions":
        return fit.basisFunctions;
      case "BestFit":
        return normal(false);
      case "BestFitParameters":
        return F.mapRange(0, p, i -> F.num(fit.parameters[i]));
      case "BIC":
        return F.num(-2.0 * logLikelihood() + Math.log(n) * (p + 1));
      case "CovarianceMatrix":
        return matrixList(covarianceMatrix());
      case "DesignMatrix":
        return matrixList(fit.design);
      case "DevianceResiduals":
        return residuals((family, y, mu) -> Math.signum(y - mu)
            * Math.sqrt(Math.max(0.0, unitDeviance(family, y, mu))));
      case "EstimatedDispersion":
        return F.num(dispersion());
      case "ExponentialFamily":
        return F.stringx(fit.family);
      case "FitResiduals":
        return residuals((family, y, mu) -> y - mu);
      case "IndependentVariables":
        return fit.variables;
      case "LogLikelihood":
        return F.num(logLikelihood());
      case "NullDegreesOfFreedom":
        return F.ZZ(hasConstant() ? n - 1 : n);
      case "NullDeviance":
        return F.num(nullDeviance());
      case "ParameterConfidenceIntervals": {
        IExpr[][] rows = parameterStatistics();
        final double q = -Math.sqrt(2.0) * Erf.erfcInv(1.95);
        return F.mapRange(0, p, i -> {
          double estimate = rows[i][0].evalf();
          double halfWidth = q * rows[i][1].evalf();
          return F.List(F.num(estimate - halfWidth), F.num(estimate + halfWidth));
        });
      }
      case "ParameterConfidenceRegion":
        // the radii are those of p times the quantile of the chi-square distribution
        return parameterEllipsoid(fit.parameters, covarianceMatrix(),
            p * new ChiSquaredDistribution(p).inverseCumulativeProbability(0.95), engine);
      case "ParameterErrors":
        return parameterColumn(parameterStatistics(), 1);
      case "ParameterPValues":
        return parameterColumn(parameterStatistics(), 3);
      case "ParameterTable":
        return parameterTable(parameterStatistics(), i -> fit.basisFunctions.get(i + 1),
            "z‐Statistic");
      case "ParameterTableEntries": {
        IExpr[][] rows = parameterStatistics();
        return F.mapRange(0, rows.length, i -> F.List(rows[i]));
      }
      case "ParameterZStatistics":
        return parameterColumn(parameterStatistics(), 2);
      case "PearsonResiduals":
        return residuals((family, y, mu) -> (y - mu) / Math.sqrt(variance(family, mu)));
      case "PredictedResponse": {
        double[] mu = predicted();
        return F.mapRange(0, n, i -> F.num(mu[i]));
      }
      case "ResidualDegreesOfFreedom":
        return F.ZZ(residualDegreesOfFreedom());
      case "ResidualDeviance":
        return F.num(deviance(predicted()));
      case "Response":
        return F.mapRange(0, n, i -> F.num(fit.response[i]));
      default:
        return F.NIL;
    }
  }

  @Override
  protected IAST fitVariables() {
    return toData().variables;
  }

  @Override
  public IExpr copy() {
    return new GeneralizedFittedModelExpr(fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof GeneralizedFittedModelExpr) {
      Fit a = toData();
      Fit b = ((GeneralizedFittedModelExpr) obj).toData();
      return a.family.equals(b.family) && a.link.equals(b.link)
          && a.basisFunctions.equals(b.basisFunctions) && a.variables.equals(b.variables)
          && Arrays.deepEquals(a.design, b.design) && Arrays.equals(a.response, b.response);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return fData == null ? 467 : 467 + Arrays.hashCode(fData.response);
  }

  @Override
  public String toString() {
    return "FittedModel[" + EvalEngine.get().evaluate(normal(false)) + "]";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    Fit fit = toData();
    output.writeObject(fit.family);
    output.writeObject(fit.link);
    output.writeObject(fit.basisFunctions);
    output.writeObject(fit.variables);
    output.writeObject(fit.design);
    output.writeObject(fit.response);
    output.writeObject(fit.parameters);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    Fit fit = new Fit((String) in.readObject(), (String) in.readObject(), (IAST) in.readObject(),
        (IAST) in.readObject(), (double[][]) in.readObject(), (double[]) in.readObject());
    fit.parameters = (double[]) in.readObject();
    fData = fit;
  }
}
