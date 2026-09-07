package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiPredicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.SymbolicArrayUtil;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbolicArray;
import org.matheclipse.core.interfaces.ITensorAccess;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.external.fastutil.ints.IntList;
import org.matheclipse.parser.trie.Trie;

public class TensorFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ArrayReduce.setEvaluator(new ArrayReduce());
      S.ArrayReshape.setEvaluator(new ArrayReshape());
      S.Ordering.setEvaluator(new Ordering());
      S.HodgeDual.setEvaluator(new HodgeDual());
      S.KroneckerProduct.setEvaluator(new KroneckerProduct());
      S.LeviCivitaTensor.setEvaluator(new LeviCivitaTensor());
      S.ListConvolve.setEvaluator(new ListConvolve());
      S.ListCorrelate.setEvaluator(new ListCorrelate());
      S.SymbolicDeltaProductArray.setEvaluator(new SymbolicDeltaProductArray());
      S.SymbolicOnesArray.setEvaluator(new SymbolicOnesArray());
      S.SymbolicIdentityArray.setEvaluator(new SymbolicIdentityArray());
      S.SymbolicZerosArray.setEvaluator(new SymbolicZerosArray());
      S.TensorDimensions.setEvaluator(new TensorDimensions());
      S.TensorRank.setEvaluator(new TensorRank());
      S.TensorSymmetry.setEvaluator(new TensorSymmetry());

      S.AffineTransform.setEvaluator(new AffineTransform());
      S.GeometricTransformation.setEvaluator(new GeometricTransformation());
      S.ReflectionTransform.setEvaluator(new ReflectionTransform());
      S.ScalingTransform.setEvaluator(new ScalingTransform());
      S.RotationTransform.setEvaluator(new RotationTransform());
      S.ShearingTransform.setEvaluator(new ShearingTransform());
      S.TransformationFunction.setEvaluator(new TransformationFunction());
      S.TranslationTransform.setEvaluator(new TranslationTransform());
    }
  }

  /**
   * Embed the <code>dim x dim</code> linear part <code>linear</code> and the translation vector
   * <code>translation</code> into the <code>(dim+1) x (dim+1)</code> homogeneous transformation
   * matrix <code>{{linear, translation}, {0, ..., 0, 1}}</code>.
   *
   * @param linear the <code>dim x dim</code> linear part of the transformation
   * @param translation the translation vector or <code>null</code> if the transformation doesn't
   *        translate
   * @param dim the dimension of the transformation
   */
  private static IAST homogeneousMatrix(IAST linear, IAST translation, int dim) {
    return F.mapRange(0, dim + 1, i -> {
      if (i == dim) {
        // last row {0, ..., 0, 1}
        return F.mapRange(0, dim + 1, j -> j == dim ? F.C1 : F.C0);
      }
      IAST row = (IAST) linear.get(i + 1);
      return F.mapRange(0, dim + 1, j -> j == dim //
          ? (translation == null ? F.C0 : translation.get(i + 1)) //
          : row.get(j + 1));
    });
  }

  /**
   * The squared euclidean norm <code>Sum(Abs(vector_k)^2)</code> of <code>vector</code>.
   *
   * @param vector a vector with <code>dim</code> elements
   * @param dim the number of elements of <code>vector</code>
   * @param engine the evaluation engine
   */
  private static IExpr squaredNorm(IAST vector, int dim, EvalEngine engine) {
    return engine.evaluate(F.mapRange(S.Plus, 0, dim, k -> F.Sqr(F.Abs(vector.get(k + 1)))));
  }

  /**
   * The <code>dim x dim</code> matrix of a scaling by the factor <code>s</code> along the direction
   * <code>direction</code>:
   * <code>KroneckerDelta(i,j) + (s-1)*direction_i*Conjugate(direction_j) / magnitude</code>.
   *
   * <p>
   * The entries are built as a single fraction, so that for example the symbolic diagonal entry is
   * printed as <code>1/2*(1+s)</code> instead of <code>1/2+s/2</code>.
   *
   * @param s the scaling factor
   * @param direction the direction vector along which is scaled
   * @param magnitude the squared norm of <code>direction</code>; must not be zero
   * @param dim the number of elements of <code>direction</code>
   */
  private static IAST directionalScalingMatrix(IExpr s, IAST direction, IExpr magnitude, int dim) {
    return F.mapRange(0, dim, i -> F.mapRange(0, dim, j -> {
      IExpr entry =
          F.Times(F.Plus(F.CN1, s), direction.get(i + 1), F.Conjugate(direction.get(j + 1)));
      return F.Divide(i == j ? F.Plus(magnitude, entry) : entry, magnitude);
    }));
  }

  /**
   * Move the fixed point of <code>transform</code> from the origin to the point <code>p</code> with
   * <code>TranslationTransform(p) . transform . TranslationTransform(-p)</code>. The {@link S#Dot}
   * operator fuses the three {@link S#TransformationFunction}s into a single one.
   *
   * @param transform the transformation which leaves the origin fixed
   * @param p the point which should be left fixed
   * @param dim the dimension of <code>transform</code>; <code>p</code> has to be a vector with
   *        <code>dim</code> elements, otherwise the homogeneous matrices wouldn't be composable
   */
  private static IExpr centeredTransform(IExpr transform, IExpr p, int dim) {
    if (p.isVector() == dim) {
      return F.Dot(F.TranslationTransform(p), transform, F.TranslationTransform(F.Negate(p)));
    }
    return F.NIL;
  }

  private static class AffineTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int[] dims = arg1.isMatrix(false);
      if (dims != null) {
        if (dims[0] != dims[1] || dims[0] == 0) {
          return F.NIL;
        }
        // AffineTransform(m) maps the vector r to m.r
        return F
            .TransformationFunction(homogeneousMatrix((IAST) arg1.normal(false), null, dims[0]));
      }
      if (arg1.isList2()) {
        // AffineTransform({m, v}) maps the vector r to m.r+v
        IExpr m = arg1.first();
        IExpr v = arg1.second();
        int[] mDims = m.isMatrix(false);
        if (mDims != null && mDims[0] == mDims[1] && mDims[0] > 0 && v.isVector() == mDims[0]) {
          return F.TransformationFunction(
              homogeneousMatrix((IAST) m.normal(false), (IAST) v.normal(false), mDims[0]));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }
  }

  private static final class ArrayReduce extends AbstractEvaluator {
    private IExpr arrayReduce(IExpr f, ITensorAccess array, int[] levels, EvalEngine engine) {
      ITensorAccess currentArray = array;
      Arrays.sort(levels);
      IntList dimensions = LinearAlgebraUtil.dimensions(array, S.List, Integer.MAX_VALUE, false);
      int iDepth = dimensions.size();
      int length = levels.length;

      for (int i = length - 1; i >= 0; i--) {
        int level = levels[i];
        currentArray =
            arrayReduce(f, currentArray, dimensions, level, engine, i == 0);
        if (currentArray.isNIL()) {
          return F.NIL;
        }
        dimensions = LinearAlgebraUtil.dimensions(currentArray, S.List, --iDepth, false);
        dimensions = dimensions.subList(0, iDepth);
      }
      return currentArray;
    }

    /**
     * Reduce {@code array} along {@code level} by rotating that level outermost, applying {@code f}
     * to each slice and rotating back.
     *
     * @param dimensions the dimensions of the array, or <code>null</code> to compute them here
     * @param doMap apply {@code f} to each slice; otherwise flatten the slices together
     * @return the reduced array, or {@link F#NIL} if a sub-evaluation did not produce one
     */
    private ITensorAccess arrayReduce(IExpr f, ITensorAccess array, IntList dimensions, int level,
        EvalEngine engine, boolean doMap) {
      int iDepth = dimensions == null ? LinearAlgebraUtil.arrayDepth(array) : dimensions.size();
      IAST range = IAST.range(iDepth + 1);
      IAST rotateRight = range.rotateRight(F.NIL, level);
      if (dimensions == null) {
        dimensions = LinearAlgebraUtil.dimensions(array, S.List, iDepth, false);
      }
      IExpr transposed = LinearAlgebra.transpose(array, rotateRight, dimensions, x -> x,
          F.Transpose(array, rotateRight), engine);
      if (transposed.isNIL()) {
        // transpose already reported why; do not carry a NIL into the map below
        return F.NIL;
      }
      IExpr normalized = ((ITensorAccess) transposed).normal(false);
      if (!normalized.isAST()) {
        return F.NIL;
      }
      IAST reduced = (IAST) normalized;
      if (doMap) {
        IExpr temp = F.Map(f, reduced, F.List(F.ZZ(iDepth - 1))).eval(engine);
        if (temp.isAST()) {
          reduced = (IAST) temp;
        } else {
          return F.NIL;
        }
      } else {
        // flatten lists
        VisitorLevelSpecification levelSpec = new VisitorLevelSpecification(
            x -> F.Apply(S.Sequence, x), iDepth - 1, false);
        IExpr flattened = reduced.accept(levelSpec);
        if (!flattened.isAST()) {
          return F.NIL;
        }
        reduced = (IAST) flattened;
      }
      if (level == 1) {
        return reduced;
      }
      IAST rotateLeft = IAST.range(iDepth).rotateLeft(F.NIL, level - 1);
      dimensions = LinearAlgebraUtil.dimensions(reduced, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() < iDepth - 1) {
        // if the dimensions are less than the depth, we can not rotate
        return F.NIL;
      }
      dimensions = dimensions.subList(0, iDepth - 1);

      return (IAST) LinearAlgebra.transpose(reduced, rotateLeft, dimensions, x -> x,
          F.Transpose(reduced, rotateLeft), engine);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      if (arg2.isList() || arg2.isSparseArray()) {
        final IExpr f = ast.arg1();
        ITensorAccess tensor = (ITensorAccess) ast.arg2();
        final IntList dims = LinearAlgebraUtil.dimensions(tensor, S.List);
        IExpr arg3 = ast.arg3();
        if (arg3.isList()) {
          int[] ni = Validate.checkListOfInts(ast, arg3, 1, dims.size(), engine);
          if (ni == null) {
            return F.NIL;
          }
          return arrayReduce(f, tensor, ni, engine);
        }
        int n = arg3.toIntDefault();
        if (F.isPresent(n) && n > 0) {
          if (n == 1 && dims.size() == 1) {
            return tensor;
          }
          return arrayReduce(f, tensor, null, n, engine, true);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }


  /**
   *
   *
   * <pre>
   * ArrayReshape(list - of - values, list - of - dimension)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>list-of-values</code> elements reshaped as nested list with dimensions
   * according to the <code>list-of-dimension</code>.
   *
   * </blockquote>
   *
   * <pre>
   * ArrayReshape(list - of - values, list - of - dimension, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Use <code>expr</code> to fill up elements, if there are too little elements in the <code>
   * list-of-values</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * A list of non-negative integers is expected at position 2. The optional third argument
   * <code>x</code> is used to fill up the structure:
   *
   * <pre>
   * &gt;&gt; ArrayReshape({a, b, c, d, e, f}, {2, 3, 3, 2}, x)
   * {{{{a,b},{c,d},{e,f}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}},{{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}}}
   * </pre>
   *
   * <p>
   * Ignore unnecessary elements
   *
   * <pre>
   * &gt;&gt; ArrayReshape(Range(1000), {3, 2, 2})
   * {{{1,2},{3,4}},{{5,6},{7,8}},{{9,10},{11,12}}}
   * </pre>
   */
  private static final class ArrayReshape extends AbstractEvaluator {
    static class Reshaper {
      final IAST list;
      final int[] dimension;
      final IExpr padding;
      int listPosition;

      public Reshaper(IAST list, int[] dimension, IExpr padding) {
        this.list = list;
        this.dimension = dimension;
        this.padding = padding;
        listPosition = 1;
      }

      /**
       * @param dimensionIndex the dimension[dimensionIndex] which should be used on this recursion
       *        level.
       * @return
       */
      public IAST recursiveCall(int dimensionIndex) {
        int dim = dimension[dimensionIndex];
        if (dimension.length == dimensionIndex + 1) {
          return F.mapRange(0, dim, i -> {
            if (list.size() <= listPosition) {
              return padding;
            }
            return list.get(listPosition++);
          });
        } else {
          return F.mapRange(0, dim, i -> recursiveCall(dimensionIndex + 1));
        }
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isList()) {
        // TODO "native" implementation for SparseArray
        arg1 = arg1.normal(false);
      }

      if (arg1.isList() && ast.arg2().isList()) {
        IAST list = (IAST) arg1;
        IAST dims = (IAST) ast.arg2();
        if (dims.size() == 1) {
          if (list.isEmpty()) {
            return F.C0;
          }
          if (list.size() > 1) {
            return list.arg1();
          }
        }
        int[] dimension = Validate.checkListOfInts(ast, dims, 1, Integer.MAX_VALUE, engine);
        if (dimension == null) {
          return F.NIL;
        }
        final IExpr padding = ast.isAST3() ? ast.arg3() : F.C0;
        return new Reshaper(list, dimension, padding).recursiveCall(0);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }


  private static class HodgeDual extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // Normalize to a dense nested list
      IExpr tensorExpr = ast.arg1().normal(false);
      if (!tensorExpr.isList()) {
        return F.NIL;
      }
      IAST tensor = (IAST) tensorExpr;

      final IntList dims = LinearAlgebraUtil.dimensions(tensor);
      final int totalRank = dims.size();
      if (totalRank == 0) {
        return F.NIL;
      }

      if (ast.isAST1()) {
        // HodgeDual[tensor] — all slots must have the same dimension
        int n = dims.getInt(0);
        for (int i = 1; i < totalRank; i++) {
          if (dims.getInt(i) != n) {
            // Tensor dimensions are not all equal
            return F.NIL;
          }
        }
        int[] contractedSlots = new int[totalRank];
        for (int i = 0; i < totalRank; i++) {
          contractedSlots[i] = i;
        }
        return hodgeDualSlots(tensor, contractedSlots, new int[0], dims, n, engine);

      } else if (ast.isAST2()) {
        // HodgeDual[tensor, dim] — dualize all slots with dimension == dim
        int dim = ast.arg2().toIntDefault();
        if (F.isNotPresent(dim) || dim <= 0) {
          return F.NIL;
        }
        int[] contractedSlots = findMatchingSlots(dims, dim);
        if (contractedSlots.length == 0) {
          // No slots with the given dimension; return tensor unchanged
          return tensor;
        }
        int[] spectatorSlots = findNonMatchingSlots(dims, dim);
        return hodgeDualSlots(tensor, contractedSlots, spectatorSlots, dims, dim, engine);

      } else { // isAST3
        // HodgeDual[tensor, dim, slots] — dualize only the given slots
        int dim = ast.arg2().toIntDefault();
        if (F.isNotPresent(dim) || dim <= 0) {
          return F.NIL;
        }
        IExpr slotsExpr = ast.arg3();
        if (!slotsExpr.isList()) {
          return F.NIL;
        }
        IAST slotsList = (IAST) slotsExpr;
        int r = slotsList.argSize();
        int[] contractedSlots = new int[r];
        boolean[] isContracted = new boolean[totalRank];
        for (int i = 0; i < r; i++) {
          int s = slotsList.get(i + 1).toIntDefault();
          if (F.isNotPresent(s)) {
            return F.NIL;
          }
          s--; // convert to 0-based
          if (s < 0 || s >= totalRank || dims.getInt(s) != dim) {
            return F.NIL;
          }
          contractedSlots[i] = s;
          isContracted[s] = true;
        }
        int spectatorCount = totalRank - r;
        int[] spectatorSlots = new int[spectatorCount];
        int j = 0;
        for (int i = 0; i < totalRank; i++) {
          if (!isContracted[i]) {
            spectatorSlots[j++] = i;
          }
        }
        return hodgeDualSlots(tensor, contractedSlots, spectatorSlots, dims, dim, engine);
      }
    }

    /**
     * Core computation: contract the given {@code contractedSlots} (0-based) using the Levi-Civita
     * tensor of the given {@code dim}. Spectator slots remain as the trailing indices of the
     * result.
     */
    private static IExpr hodgeDualSlots(IAST tensor, int[] contractedSlots, int[] spectatorSlots,
        IntList dims, int dim, EvalEngine engine) {
      int r = contractedSlots.length;
      int dualRank = dim - r;
      if (dualRank < 0) {
        return F.NIL;
      }

      // Build Levi-Civita tensor as a dense nested list
      IExpr lct = engine.evaluate(F.LeviCivitaTensor(F.ZZ(dim), S.List));
      if (!lct.isList()) {
        return F.NIL;
      }
      IAST leviCivita = (IAST) lct;

      int[] spectatorDims = new int[spectatorSlots.length];
      for (int i = 0; i < spectatorSlots.length; i++) {
        spectatorDims[i] = dims.getInt(spectatorSlots[i]);
      }

      int[] freeIdx = new int[dualRank];
      int[] spectIdx = new int[spectatorSlots.length];
      int[] contrIdx = new int[r];

      IExpr result = buildHodgeDualResult(tensor, leviCivita, contractedSlots, spectatorSlots,
          spectatorDims, dim, r, dualRank, freeIdx, 0, spectIdx, contrIdx, engine);
      if (result.isNIL()) {
        return F.NIL;
      }

      // Divide by r! to compensate for antisymmetrization
      if (r > 1) {
        result = engine.evaluate(F.Divide(result, F.Factorial(F.ZZ(r))));
      }
      return result;
    }

    /**
     * Recursively build the result tensor. Outer indices are the {@code dualRank} new "free" (Hodge
     * dual) indices; inner indices are the spectator indices.
     */
    private static IExpr buildHodgeDualResult(IAST tensor, IAST lct, int[] contractedSlots,
        int[] spectatorSlots, int[] spectatorDims, int dim, int r, int dualRank, int[] freeIdx,
        int freeLevel, int[] spectIdx, int[] contrIdx, EvalEngine engine) {

      if (freeLevel < dualRank) {
        IASTAppendable list = F.ListAlloc(dim);
        for (int j = 0; j < dim; j++) {
          freeIdx[freeLevel] = j;
          IExpr elem = buildHodgeDualResult(tensor, lct, contractedSlots, spectatorSlots,
              spectatorDims, dim, r, dualRank, freeIdx, freeLevel + 1, spectIdx, contrIdx, engine);
          if (elem.isNIL()) {
            return F.NIL;
          }
          list.append(elem);
        }
        return list;
      }

      // All free indices are fixed; now iterate spectator indices
      return buildSpectatorResult(tensor, lct, contractedSlots, spectatorSlots, spectatorDims, dim,
          r, dualRank, freeIdx, spectIdx, 0, contrIdx, engine);
    }

    private static IExpr buildSpectatorResult(IAST tensor, IAST lct, int[] contractedSlots,
        int[] spectatorSlots, int[] spectatorDims, int dim, int r, int dualRank, int[] freeIdx,
        int[] spectIdx, int spectLevel, int[] contrIdx, EvalEngine engine) {

      if (spectLevel == spectatorDims.length) {
        // Leaf: compute the contraction over all contracted index combinations
        return computeContraction(tensor, lct, contractedSlots, spectatorSlots, spectIdx, dim, r,
            dualRank, freeIdx, contrIdx, 0, engine);
      }
      IASTAppendable list = F.ListAlloc(spectatorDims[spectLevel]);
      for (int k = 0; k < spectatorDims[spectLevel]; k++) {
        spectIdx[spectLevel] = k;
        IExpr elem = buildSpectatorResult(tensor, lct, contractedSlots, spectatorSlots,
            spectatorDims, dim, r, dualRank, freeIdx, spectIdx, spectLevel + 1, contrIdx, engine);
        if (elem.isNIL()) {
          return F.NIL;
        }
        list.append(elem);
      }
      return list;
    }

    /**
     * Recursively sum over the contracted indices, accumulating
     * {@code T[contractedIdx..., spectIdx...] * ε[contractedIdx..., freeIdx...]}.
     */
    private static IExpr computeContraction(IAST tensor, IAST lct, int[] contractedSlots,
        int[] spectatorSlots, int[] spectIdx, int dim, int r, int dualRank, int[] freeIdx,
        int[] contrIdx, int contrLevel, EvalEngine engine) {

      if (contrLevel == r) {
        // Look up ε[contrIdx..., freeIdx...]
        int[] lctIdx = new int[r + dualRank]; // equals dim
        System.arraycopy(contrIdx, 0, lctIdx, 0, r);
        System.arraycopy(freeIdx, 0, lctIdx, r, dualRank);
        IExpr lctVal = getNestedElement(lct, lctIdx);
        if (lctVal.isZero()) {
          return F.C0;
        }

        // Look up T[...] with the contracted and spectator indices mapped back to
        // their original positions in the tensor
        int totalRank = contractedSlots.length + spectatorSlots.length;
        int[] tensorIdx = new int[totalRank];
        for (int k = 0; k < contractedSlots.length; k++) {
          tensorIdx[contractedSlots[k]] = contrIdx[k];
        }
        for (int k = 0; k < spectatorSlots.length; k++) {
          tensorIdx[spectatorSlots[k]] = spectIdx[k];
        }
        IExpr tVal = getNestedElement(tensor, tensorIdx);
        if (tVal.isZero()) {
          return F.C0;
        }
        return engine.evaluate(F.Times(tVal, lctVal));
      }

      IASTAppendable plus = F.PlusAlloc(dim);
      boolean allZero = true;
      for (int i = 0; i < dim; i++) {
        contrIdx[contrLevel] = i;
        IExpr term = computeContraction(tensor, lct, contractedSlots, spectatorSlots, spectIdx, dim,
            r, dualRank, freeIdx, contrIdx, contrLevel + 1, engine);
        if (!term.isZero()) {
          plus.append(term);
          allZero = false;
        }
      }
      return allZero ? F.C0 : engine.evaluate(plus.oneIdentity0());
    }

    /**
     * Navigate a nested IAST list using a 0-based multi-index array. Returns {@link F#C0} if any
     * level is not a list or the index is out of range.
     */
    private static IExpr getNestedElement(IAST tensor, int[] indices) {
      IExpr current = tensor;
      for (int idx : indices) {
        if (!current.isList()) {
          return F.C0;
        }
        int astIdx = idx + 1; // IAST is 1-based
        if (astIdx >= ((IAST) current).size()) {
          return F.C0;
        }
        current = ((IAST) current).get(astIdx);
      }
      return current;
    }

    /** Collect the 0-based positions of all slots whose dimension equals {@code dim}. */
    private static int[] findMatchingSlots(IntList dims, int dim) {
      return findSlots(dims, dim, true);
    }

    /** Collect the 0-based positions of all slots whose dimension does NOT equal {@code dim}. */
    private static int[] findNonMatchingSlots(IntList dims, int dim) {
      return findSlots(dims, dim, false);
    }

    /**
     * Collect the 0-based positions of the slots whose dimension equals {@code dim} when
     * {@code matching} is <code>true</code>, and of those where it differs otherwise.
     */
    private static int[] findSlots(IntList dims, int dim, boolean matching) {
      int count = 0;
      for (int i = 0; i < dims.size(); i++) {
        if ((dims.getInt(i) == dim) == matching) {
          count++;
        }
      }
      int[] slots = new int[count];
      int j = 0;
      for (int i = 0; i < dims.size(); i++) {
        if ((dims.getInt(i) == dim) == matching) {
          slots[j++] = i;
        }
      }
      return slots;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>KroneckerProduct(t1, t2, ...)</code>. Unlike <code>TensorProduct</code> this symbol is
   * neither <code>Flat</code> nor <code>OneIdentity</code> in WMA, so it deliberately does not
   * override {@link #setUp}.
   */
  private static class KroneckerProduct extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IBuiltInSymbol headSymbol = S.KroneckerProduct;
      int argSize = ast.argSize();
      // expectedArgSize() is >= 2
      if (ast.arg1().isList() && ast.arg2().isList()) {
        try {
          IAST tensor1 = (IAST) ast.arg1();
          IntList dim1 = LinearAlgebraUtil.dimensions(tensor1, S.List, Integer.MAX_VALUE, true);
          if (dim1.size() > 0) {
            for (int i = 2; i < ast.size(); i++) {
              IAST tensor2 = (IAST) ast.get(i);
              IntList dim2 = LinearAlgebraUtil.dimensions(tensor2, S.List, Integer.MAX_VALUE, true);
              if (dim1.size() == dim2.size()) {
                IExpr temp = org.matheclipse.core.reflection.system.TensorProduct
                    .tensorProduct(tensor1, tensor2, dim1.size(), engine);
                if (temp.isList()) {
                  int r = 2;
                  if (dim2.size() > r) {
                    r = dim2.size();
                  }
                  IExpr flattened = S.ArrayFlatten.of(engine, temp, F.ZZ(r)).normal(false);
                  tensor1 = flattened.isList() ? (IAST) flattened : F.NIL;
                  if (tensor1.isList()) {
                    dim1 = LinearAlgebraUtil.dimensions(tensor1, S.List);
                    if (dim1.size() > 0) {
                      if (i < argSize) {
                        if (ast.get(i + 1).isList()) {
                          continue;
                        }
                      } else {
                        return tensor1;
                      }
                    }
                  }
                  IASTAppendable result = F.ast(headSymbol, ast.size() - i);
                  result.append(temp);
                  result.appendAll(ast, i + 1, ast.size());
                  return result;
                }
              }
              if (i == 2) {
                return F.NIL;
              }

              IASTAppendable result = F.ast(headSymbol, ast.size() - i);
              result.append(tensor1);
              result.appendAll(ast, i, ast.size());
              return result;
            }
            return tensor1;
          }
        } catch (IllegalArgumentException iae) {
          // print message: Nonrectangular tensor encountered
          return Errors.printMessage(ast.topHead(), "rect", F.list(ast), engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

  }

  private static class LeviCivitaTensor extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int n = arg1.toMachineInt();
      if (n <= 0) {
        if (!arg1.isInteger()) {
          return F.NIL;
        }
        // Positive machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }

      // Factorial growth is fast; prevent OutOfMemory errors for extremely large n
      IInteger maxElements = AbstractIntegerSym.factorial(n);
      if (maxElements.isGT(F.ZZ(Config.MAX_AST_SIZE))) {
        ASTElementLimitExceeded.throwIt(Config.MAX_AST_SIZE);
      }

      // Prepare the Sparse Array components
      final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
      int[] dimension = new int[n];
      for (int i = 0; i < n; i++) {
        dimension[i] = n;
      }

      // Generate permutations and populate the Trie using Heap's Algorithm
      generateLeviCivitaTrie(n, trie);

      SparseArrayExpr sparseArray = new SparseArrayExpr(trie, dimension, F.C0, false);

      // Handle the optional dense list format request: LeviCivitaTensor(n, List)
      if (ast.isAST2() && ast.second() == S.List) {
        return sparseArray.normal(false);
      }

      return sparseArray;
    }

    /**
     * Uses Heap's Algorithm to generate all permutations of 1..n, keeping track of the
     * permutation's parity (sign) to populate the Levi-Civita sparse tensor.
     */
    private void generateLeviCivitaTrie(int n, Trie<int[], IExpr> trie) {
      int[] elements = new int[n];
      for (int i = 0; i < n; i++) {
        elements[i] = i + 1; // 1-based indexing for the tensor coordinates
      }

      int[] c = new int[n];
      int sign = 1;

      // Add the initial permutation (even parity)
      trie.put(elements.clone(), F.C1);

      int i = 0;
      while (i < n) {
        if (c[i] < i) {
          if (i % 2 == 0) {
            swap(elements, 0, i);
          } else {
            swap(elements, c[i], i);
          }

          // Every swap flips the sign of the permutation
          sign = -sign;
          trie.put(elements.clone(), sign == 1 ? F.C1 : F.CN1);

          c[i] += 1;
          i = 0;
        } else {
          c[i] = 0;
          i += 1;
        }
      }
    }

    private void swap(int[] arr, int i, int j) {
      int temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * ListConvolve(kernel - list, tensor - list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create the convolution of the <code>kernel-list</code> with <code>tensor-list</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ListConvolve({x, y}, {a, b, c, d, e, f})
   * {b*x+a*y,c*x+b*y,d*x+c*y,e*x+d*y,f*x+e*y}
   * </pre>
   */
  private static class ListConvolve extends AbstractEvaluator {
    /**
     * See: <a href=
     * "https://github.com/idsc-frazzoli/tensor/blob/master/src/main/java/ch/ethz/idsc/tensor/alg/ListConvolve.java">tensor/alg/ListConvolve.java</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        // normalize both arguments, as ListCorrelate does, so that a SparseArray kernel is
        // accepted rather than left unevaluated
        IExpr k = ast.arg1().normal(false);
        IExpr t = ast.arg2().normal(false);
        if (k.isAST() && t.isAST()) {
          IAST kernel = (IAST) k;
          IAST tensor = (IAST) t;
          IntList kernelDims = LinearAlgebraUtil.dimensions(kernel);
          IntList tensorDims = LinearAlgebraUtil.dimensions(tensor);
          if (kernelDims.size() > 0 && kernelDims.size() == tensorDims.size()) {
            int kernelSize = kernel.size();
            int tensorSize = tensor.size();
            if (kernelSize <= tensorSize) {
              IAST reversed = nestedReverseRecursive(kernel, kernelDims, 0);
              return ListCorrelate.listCorrelate(reversed, tensor, S.Plus, S.Times);
            }
          }
        }

      }
      return F.NIL;
    }

    /**
     * Reverse <code>kernel</code> on all nested levels.
     *
     * @param rootKernelDimensions the dimensions of the root kernel
     * @param dimensionLevel the current level within <code>rootKernelDimensions</code>
     * @return the reversed kernel AST
     */
    private static IAST nestedReverseRecursive(IAST kernel, IntList rootKernelDimensions,
        int dimensionLevel) {

      int argSize = kernel.argSize(); //

      // Pre-allocate the exact size to reduce garbage collection and resizing overhead
      IASTAppendable reversedList = F.ast(kernel.head(), argSize);

      if (dimensionLevel == rootKernelDimensions.size() - 1) {
        // Stop recursion: reverse the dense leaf list efficiently in a single pass
        for (int i = argSize; i >= 1; i--) {
          reversedList.append(kernel.get(i));
        }
      } else {
        // Intermediate levels: reverse order and recurse downwards
        for (int i = argSize; i >= 1; i--) {
          IAST reversed = nestedReverseRecursive((IAST) kernel.get(i).normal(false),
              rootKernelDimensions, dimensionLevel + 1);
          reversedList.append(reversed);
        }
      }

      return reversedList;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * ListCorrelate(kernel - list, tensor - list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create the correlation of the <code>kernel-list</code> with <code>tensor-list</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ListCorrelate({x, y}, {a, b, c, d, e, f})
   * {a*x+b*y,b*x+c*y,c*x+d*y,d*x+e*y,e*x+f*y}
   * </pre>
   */
  private static class ListCorrelate extends AbstractEvaluator {
    /**
     * See: <a href=
     * "https://github.com/idsc-frazzoli/tensor/blob/master/src/main/java/ch/ethz/idsc/tensor/alg/ListCorrelate.java">tensor/alg/ListCorrelate.java</a>
     *
     * @return correlation of kernel with tensor
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr k = ast.arg1().normal(false);
        IExpr t = ast.arg2().normal(false);
        if (k.isAST() && t.isAST()) {
          IAST kernel = (IAST) k;
          IAST tensor = (IAST) t;
          IntList kernelDims = LinearAlgebraUtil.dimensions(kernel);
          IntList tensorDims = LinearAlgebraUtil.dimensions(tensor);
          if (kernelDims.size() > 0 && kernelDims.size() == tensorDims.size()) {
            return listCorrelate(kernel, tensor, S.Plus, S.Times);
          }
        }

      }
      return F.NIL;
    }

    /**
     * The "valid"-mode correlation of {@code kernel} with {@code tensor}: entry {@code o} of the
     * result is the sum over every kernel index {@code q} of
     * {@code kernel[[q]] * tensor[[o + q]]}. Works for any rank; when the kernel has fewer levels
     * than the tensor, the trailing levels are carried along as whole sub-tensors.
     *
     * @return {@link F#NIL} if the ranks do not fit or the kernel is larger than the tensor
     */
    public static IExpr listCorrelate(IAST kernel, IAST tensor, final ISymbol plusFunction,
        final ISymbol timesFunction) {
      IntList kernelDimension = LinearAlgebraUtil.dimensions(kernel);
      IntList tensorDimension = LinearAlgebraUtil.dimensions(tensor);
      final int rank = kernelDimension.size();
      if (rank == 0 || rank > tensorDimension.size()) {
        return F.NIL;
      }
      int[] kernelDim = new int[rank];
      int[] outputDim = new int[rank];
      int windowSize = 1;
      for (int i = 0; i < rank; i++) {
        kernelDim[i] = kernelDimension.getInt(i);
        outputDim[i] = tensorDimension.getInt(i) - kernelDim[i] + 1;
        if (outputDim[i] <= 0) {
          return F.NIL;
        }
        windowSize *= kernelDim[i];
      }
      return correlateLevel(kernel, tensor, kernelDim, outputDim, new int[rank], 0, windowSize,
          plusFunction, timesFunction);
    }

    /**
     * Builds the result list for axis {@code level}, or the sum over one kernel window once every
     * axis carries an offset.
     */
    private static IExpr correlateLevel(IAST kernel, IAST tensor, int[] kernelDim, int[] outputDim,
        int[] offset, int level, int windowSize, ISymbol plusFunction, ISymbol timesFunction) {
      if (level == outputDim.length) {
        IASTAppendable sum = F.ast(plusFunction, windowSize);
        correlateWindow(kernel, tensor, kernelDim, offset, new int[kernelDim.length], 0, sum,
            timesFunction);
        return sum;
      }
      IASTAppendable result = F.ListAlloc(outputDim[level]);
      for (int i = 0; i < outputDim[level]; i++) {
        offset[level] = i;
        result.append(correlateLevel(kernel, tensor, kernelDim, outputDim, offset, level + 1,
            windowSize, plusFunction, timesFunction));
      }
      return result;
    }

    /**
     * Appends {@code kernel[[q]] * tensor[[offset + q]]} for every kernel index {@code q}, the last
     * axis varying fastest, which is the term order the rank 1 and rank 2 forms have always used.
     */
    private static void correlateWindow(IAST kernel, IAST tensor, int[] kernelDim, int[] offset,
        int[] index, int level, IASTAppendable sum, ISymbol timesFunction) {
      if (level == kernelDim.length) {
        IExpr kernelElement = kernel;
        IExpr tensorElement = tensor;
        for (int d = 0; d < index.length; d++) {
          kernelElement = ((IAST) kernelElement).get(index[d] + 1);
          tensorElement = ((IAST) tensorElement).get(offset[d] + index[d] + 1);
        }
        sum.append(F.binaryAST2(timesFunction, kernelElement, tensorElement));
        return;
      }
      for (int i = 0; i < kernelDim[level]; i++) {
        index[level] = i;
        correlateWindow(kernel, tensor, kernelDim, offset, index, level + 1, sum, timesFunction);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

  }


  /**
   *
   *
   * <pre>
   * Ordering(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculate the permutation list of the elements in the sorted <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ordering(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculate the first <code>n</code> indexes of the permutation list of the elements in the
   * sorted <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ordering(list, -n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculate the last <code>n</code> indexes of the permutation list of the elements in the sorted
   * <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ordering(list, n, head)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculate the first <code>n</code> indexes of the permutation list of the elements in the
   * sorted <code>list</code> using comparator operation <code>head</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Ordering({1,3,4,2,5,9,6})
   * {1,4,2,3,5,7,6}
   *
   * &gt;&gt; Ordering({1,3,4,2,5,9,6}, All, Greater)
   * {6,7,5,3,2,4,1}
   * </pre>
   */
  private static class Ordering extends AbstractEvaluator {

    /**
     * See <a href="https://stackoverflow.com/a/4859279/24819">Get the indices of an array after
     * sorting?</a>
     */
    private static class ArrayIndexComparator implements Comparator<Integer> {
      protected final IAST ast;

      public ArrayIndexComparator(IAST ast) {
        this.ast = ast;
      }

      public Integer[] createIndexArray() {
        final int argSize = ast.argSize();
        Integer[] indexes = new Integer[argSize];
        for (int i = 1; i <= argSize; i++) {
          indexes[i - 1] = i;
        }
        return indexes;
      }

      @Override
      public int compare(Integer index1, Integer index2) {
        return ast.get(index1).compareTo(ast.get(index2));
      }
    }

    private static class PredicateComparator extends ArrayIndexComparator {
      final Comparator<IExpr> comparator;

      public PredicateComparator(IAST ast, Comparator<IExpr> comparator) {
        super(ast);
        this.comparator = comparator;
      }

      @Override
      public int compare(Integer index1, Integer index2) {
        return comparator.compare(ast.get(index1), ast.get(index2));
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST list = (IAST) ast.arg1();
        ArrayIndexComparator comparator;
        if (ast.size() >= 4) {
          // use the 3rd argument as a head for the comparator operation:
          IExpr comparatorFunction = ast.arg3();
          comparator =
              new PredicateComparator(list, new Predicates.IsBinaryFalse(comparatorFunction));
        } else if (list.exists(x -> x.isQuantity())) {
          // quantities order by magnitude, not canonically - see Comparators.QuantityComparator
          comparator =
              new PredicateComparator(list, new Comparators.QuantityComparator(engine));
        } else {
          // use the default IExpr#compareTo() method
          comparator = new ArrayIndexComparator(list);
        }
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);
        int n = indexes.length;
        if (ast.size() >= 3) {
          IExpr arg2 = ast.arg2();
          if (arg2 != S.All) {
            if (!arg2.isReal()) {
              // neither All nor a number of elements; do not silently fall back to All
              return Errors.printMessage(ast.topHead(), "int", F.list(ast, F.C2), engine);
            }
            n = ((IReal) arg2).toIntDefault();
            if (F.isNotPresent(n)) {
              return Errors.printMessage(ast.topHead(), "int", F.list(ast, F.C2), engine);
            }
          }
        }
        // n outside -Length(list) .. Length(list) is clamped by F.tensorList, as in WMA
        return F.tensorList(n, indexes);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

  }


  /**
   * The dimensions of <code>tensor</code>, evaluated with the assumptions given by the
   * <code>Assumptions</code> option of <code>ast</code> installed in the engine. Shared by
   * <code>TensorDimensions</code> and <code>TensorRank</code>.
   *
   * @return the dimensions list, or {@link F#NIL} if the shape is not known
   */
  private static IExpr dimensionsUnderAssumptions(IAST ast, IExpr tensor, EvalEngine engine) {
    IAssumptions oldAssumptions = engine.getAssumptions();
    OptionArgs options = null;
    if (ast.size() > 2) {
      options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
    }
    try {
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        IAssumptions assumptions =
            org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        if (assumptions != null) {
          engine.setAssumptions(assumptions);
        }
      }
      return SymbolicArrayUtil.tensorDimensions(tensor, engine);
    } finally {
      engine.setAssumptions(oldAssumptions);
    }
  }

  private static class TensorDimensions extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // SymbolicArrayUtil looks through the arithmetic and array operations, so the shape of
      // a.b or Transpose(a) follows from the shapes of the symbolic arrays inside
      return dimensionsUnderAssumptions(ast, ast.arg1(), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // 1_2 rather than 1_1, so that the Assumptions option registered in setUp can be given, as
      // it can for TensorRank
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.list(F.Rule(S.Assumptions, S.$Assumptions)));
    }
  }


  private static class TensorSymmetry extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAssumptions oldAssumptions = engine.getAssumptions();
      OptionArgs options = null;
      IExpr sameTest = S.SameQ;
      if (ast.size() > 2) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
        IExpr option = options.getOption(S.SameTest);
        if (option.isPresent()) {
          sameTest = option;
        }
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);

      IExpr arg1 = ast.arg1();
      if (arg1 instanceof IArraySymbol) {
        // IArraySymbol#getSymmetry() answers S.None when no symmetry was declared; TensorSymmetry
        // reports the absence of a symmetry as an empty list of generators
        IExpr symmetry = ((IArraySymbol) arg1).getSymmetry();
        return symmetry.isNone() ? F.CEmptyList : symmetry;
      }
      arg1 = arg1.normal(false);
      if (arg1.isAST()) {
        IAST tensor = (IAST) arg1;
        final IntList dims = LinearAlgebraUtil.dimensions(tensor, tensor.head());
        final int dimsSize = dims.size();
        if (dimsSize > 0) {
          if (dimsSize == 2 && dims.getInt(0) == dims.getInt(1)) {
            // square matrix
            int rowColumnSize = dims.getInt(0) + 1;
            if (rowColumnSize == 2) {
              if (tensor.getPart(1, 1).isZero()) {
                return F.ZeroSymmetric(F.CEmptyList);
              }
              return F.Symmetric(F.list(F.C1, F.C2));
            }
            return tensorSymmetrySquareMatrix(tensor, rowColumnSize, sameTest, engine);
          }
        }
      }

      // no assumptions are installed here: tensorProperties() reads the map out of the argument
      // directly, so there is nothing to restore afterwards
      {
        Map<IExpr, IAST> tensorProperties = tensorProperties(oldAssumptions, assumptionExpr);
        if (tensorProperties != null) {
          IAST tensorArg1 = tensorProperties.get(arg1);
          if (tensorArg1 != null) {
            if (tensorArg1.isAST(S.Vectors)) {
              return F.CEmptyList;
            }
            if (tensorArg1.isAST(S.Arrays) || tensorArg1.isAST(S.Matrices)) {
              // the stored domain is Arrays(dimensions, elementDomain[, symmetry]); a declared
              // symmetry is always its last argument
              if (tensorArg1.last().isAST()) {
                IAST symmetry = (IAST) tensorArg1.last();
                if (symmetry.isAST(S.Symmetric, 2) //
                    || symmetry.isAST(S.Antisymmetric, 2) || symmetry.isAST(S.ZeroSymmetric, 2)) {
                  return symmetry;
                }
              }
              return F.CEmptyList;
            }
          }
        }

      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * @param squareMatrix
     * @param rowColumnSize the row and column size of the square matrix
     * @param engine the evaluation engine
     * @return
     */
    private static IExpr tensorSymmetrySquareMatrix(IAST squareMatrix, int rowColumnSize,
        IExpr sameTest, EvalEngine engine) {
      IExpr temp = isZeroSymmetricSquareMatrix(squareMatrix, rowColumnSize);
      if (temp.isPresent()) {
        return temp;
      }
      // the two hypotheses are independent: a matrix whose upper triangle happens to satisfy one of
      // them in some row says nothing about the other
      BiPredicate<IExpr, IExpr> same = sameTest(sameTest, engine);
      if (isSymmetricSquareMatrix(squareMatrix, rowColumnSize, same)) {
        return F.Symmetric(F.list(F.C1, F.C2));
      }
      if (isAntiSymmetricSquareMatrix(squareMatrix, rowColumnSize, same)) {
        return F.Antisymmetric(F.list(F.C1, F.C2));
      }
      return F.CEmptyList;
    }

    /**
     * The equality predicate for the <code>SameTest</code> option. {@link S#Automatic} is the
     * registered default and means {@link S#SameQ}, which is tested structurally rather than by
     * evaluating <code>SameQ</code>.
     */
    private static BiPredicate<IExpr, IExpr> sameTest(IExpr sameTest, EvalEngine engine) {
      if (sameTest == S.SameQ || sameTest == S.Automatic) {
        return (a, b) -> a.equals(b);
      }
      return (a, b) -> engine.evalTrue(sameTest, a, b);
    }

    /** Tests <code>m[[i,j]] == m[[j,i]]</code> over the upper triangle. */
    private static boolean isSymmetricSquareMatrix(IAST squareMatrix, int rowColumnSize,
        BiPredicate<IExpr, IExpr> same) {
      for (int i = 1; i < rowColumnSize; i++) {
        for (int j = i + 1; j < rowColumnSize; j++) {
          if (!same.test(squareMatrix.getPart(i, j), squareMatrix.getPart(j, i))) {
            return false;
          }
        }
      }
      return true;
    }

    /**
     * Tests <code>m[[i,j]] == -m[[j,i]]</code> over the upper triangle. The diagonal is included,
     * because that relation forces <code>m[[i,i]]</code> to vanish.
     */
    private static boolean isAntiSymmetricSquareMatrix(IAST squareMatrix, int rowColumnSize,
        BiPredicate<IExpr, IExpr> same) {
      for (int i = 1; i < rowColumnSize; i++) {
        for (int j = i; j < rowColumnSize; j++) {
          if (!same.test(squareMatrix.getPart(i, j), squareMatrix.getPart(j, i).negate())) {
            return false;
          }
        }
      }
      return true;
    }

    /**
     * @param squareMatrix
     * @param rowColumnSize the row and column size of the square matrix
     * @return
     */
    private static IExpr isZeroSymmetricSquareMatrix(IAST squareMatrix, int rowColumnSize) {
      boolean isZero = true;
      for (int i = 1; i < rowColumnSize; i++) {
        for (int j = 1; j < rowColumnSize; j++) {
          if (!squareMatrix.getPart(i, j).isZero()) {
            isZero = false;
            break;
          }
        }
        if (!isZero) {
          break;
        }
      }
      if (isZero) {
        return F.ZeroSymmetric(F.List());
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.list(F.Rule(S.Assumptions, S.$Assumptions), //
          F.Rule(S.SameTest, S.Automatic)));
    }
  }


  private static class TensorRank extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // a QuantityArray ranks as the array it stands for, not as its two arguments
      IExpr arg1 = QuantityFunctions.normalizeQuantityArray(ast.arg1());

      IExpr dimensions = dimensionsUnderAssumptions(ast, arg1, engine);
      if (dimensions.isPresent()) {
        return F.ZZ(((IAST) dimensions).argSize());
      }

      if (arg1.isNumericFunction()) {
        if (engine.evalN(arg1).isNumber()) {
          return F.C0;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Assumptions, S.$Assumptions)));
    }
  }


  private static class ReflectionTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int dim = arg1.isVector();
      if (dim > 0) {
        IAST v = (IAST) arg1.normal(false);
        IExpr magnitude = squaredNorm(v, dim, engine);
        if (magnitude.isZero()) {
          // Direction vector `1` has zero magnitude.
          return Errors.printMessage(S.ReflectionTransform, "idir", F.list(arg1), engine);
        }
        // a reflection normal to `v` is a scaling by the factor -1 along `v`
        IAST matrix =
            homogeneousMatrix(directionalScalingMatrix(F.CN1, v, magnitude, dim), null, dim);
        if (ast.isAST2()) {
          // ReflectionTransform(v, p) - the mirror goes through the point `p`
          return centeredTransform(F.TransformationFunction(matrix), ast.arg2(), dim);
        }
        return F.TransformationFunction(matrix);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }
  }


  private static class RotationTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr phi = ast.arg1();
      if (phi.isList()) {
        // RotationTransform({u, v}) - rotate the vector `u` into the direction of the vector `v`
        int pairDim = VectorAnalysisFunctions.vectorPairDimension(phi);
        if (pairDim <= 0 || ast.size() > 3) {
          return F.NIL;
        }
        IAST linear = VectorAnalysisFunctions.rotationMatrixPlane(F.NIL, phi, engine);
        if (linear.isNIL()) {
          return F.NIL;
        }
        IAST matrix = homogeneousMatrix(linear, null, pairDim);
        if (ast.isAST2()) {
          // RotationTransform({u, v}, p) - rotate around the point `p`
          return centeredTransform(F.TransformationFunction(matrix), ast.arg2(), pairDim);
        }
        return F.TransformationFunction(matrix);
      }
      if (ast.isAST1()) {
        // TransformationFunction({{Cos(phi), -Sin(phi), 0}, {Sin(phi), Cos(phi), 0}, {0, 0, 1}})
        return F.TransformationFunction(F.list(F.list(F.Cos(phi), F.Negate(F.Sin(phi)), F.C0),
            F.list(F.Sin(phi), F.Cos(phi), F.C0), F.list(F.C0, F.C0, F.C1)));
      }

      IExpr arg2 = ast.arg2();
      int dim = arg2.isVector();
      if (dim == 3) {
        // RotationTransform(phi, w) - rotate around the axis `w` through the origin; the 3x3
        // rotation has to be embedded in a 4x4 homogeneous matrix
        IAST linear = VectorAnalysisFunctions.rotationMatrix3D(phi, (IAST) arg2.normal(false), //
            engine);
        if (linear.isNIL()) {
          // Direction vector `1` has zero magnitude.
          return Errors.printMessage(S.RotationTransform, "idir", F.list(arg2), engine);
        }
        IAST matrix = homogeneousMatrix(linear, null, 3);
        if (ast.isAST3()) {
          // RotationTransform(phi, w, p) - rotate around the axis `w` through the point `p`
          return centeredTransform(F.TransformationFunction(matrix), ast.arg3(), 3);
        }
        return F.TransformationFunction(matrix);
      }
      if (ast.isAST2() && dim == 2) {
        // RotationTransform(phi, p) - rotate around the point `p`
        return centeredTransform(F.RotationTransform(phi), arg2, 2);
      }
      int pairDim = VectorAnalysisFunctions.vectorPairDimension(arg2);
      if (pairDim > 0) {
        // RotationTransform(phi, {u, v}) - rotate from the direction of `u` towards the direction
        // of `v` in the plane they span
        IAST linear = VectorAnalysisFunctions.rotationMatrixPlane(phi, arg2, engine);
        if (linear.isNIL()) {
          return F.NIL;
        }
        IAST matrix = homogeneousMatrix(linear, null, pairDim);
        if (ast.isAST3()) {
          // RotationTransform(phi, {u, v}, p) - rotate around the point `p`
          return centeredTransform(F.TransformationFunction(matrix), ast.arg3(), pairDim);
        }
        return F.TransformationFunction(matrix);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }
  }


  private static class ScalingTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr s = ast.arg1();
      int scalingDim = s.isVector();
      if (scalingDim > 0) {
        // ScalingTransform({s1, s2, ...}) - scale along the coordinate axes
        // TransformationFunction(DiagonalMatrix(Join(s, {1})))
        IExpr scaling = F.TransformationFunction(F.DiagonalMatrix(F.Join(s, F.list(F.C1))));
        if (ast.isAST1()) {
          return scaling;
        }
        if (ast.isAST2()) {
          // ScalingTransform({s1, s2, ...}, p) - leave the point `p` fixed
          return centeredTransform(scaling, ast.arg2(), scalingDim);
        }
        return F.NIL;
      }
      if (ast.size() >= 3) {
        // ScalingTransform(s, v) - scale by the factor `s` along the direction `v`
        IExpr direction = ast.arg2();
        int dim = direction.isVector();
        if (dim > 0) {
          IAST v = (IAST) direction.normal(false);
          IExpr magnitude = squaredNorm(v, dim, engine);
          if (magnitude.isZero()) {
            // Direction vector `1` has zero magnitude.
            return Errors.printMessage(S.ScalingTransform, "idir", F.list(direction), engine);
          }
          IAST matrix =
              homogeneousMatrix(directionalScalingMatrix(s, v, magnitude, dim), null, dim);
          if (ast.isAST3()) {
            // ScalingTransform(s, v, p) - leave the point `p` fixed
            return centeredTransform(F.TransformationFunction(matrix), ast.arg3(), dim);
          }
          return F.TransformationFunction(matrix);
        }
      }
      return F.NIL;

    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }
  }
  private static class SymbolicDeltaProductArray extends AbstractFunctionEvaluator
      implements ISymbolicArray {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NONTHREADABLE);
    }

    @Override
    public IAST getDimensions(IAST ast) {
      if (ast.isAST2() && ast.first().isList()) {
        return (IAST) ast.first();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

  }

  private static class SymbolicIdentityArray extends AbstractFunctionEvaluator
      implements ISymbolicArray {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NONTHREADABLE);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <code>SymbolicIdentityArray({n1,...,nk})</code> stands for the
     * <code>n1 x ... x nk x n1 x ... x nk</code> array whose entry is <code>1</code> where all
     * index pairs agree, so its dimensions are the argument written twice - which is the shape
     * {@link S#Normal} builds as well.
     * </p>
     */
    @Override
    public IAST getDimensions(IAST ast) {
      if (ast.isAST1() && ast.first().isList()) {
        IAST dimensions = (IAST) ast.first();
        return SymbolicArrayUtil.joinDimensions(dimensions, dimensions);
      }
      return F.NIL;
    }
  }

  /**
   * An inert one-argument symbolic array whose shape is its own first argument, such as
   * <code>SymbolicOnesArray({2,3})</code>. It never evaluates; it exists so that the shape is
   * known to {@link ISymbolicArray#getDimensions(IAST)} and so that arithmetic does not thread it
   * into the elements of a list.
   */
  private abstract static class SymbolicShapeArray extends AbstractFunctionEvaluator
      implements ISymbolicArray {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NONTHREADABLE);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IAST getDimensions(IAST ast) {
      if (ast.isAST1() && ast.first().isList()) {
        return (IAST) ast.first();
      }
      return F.NIL;
    }
  }

  /** <code>SymbolicOnesArray({d1,d2,...})</code> */
  private static final class SymbolicOnesArray extends SymbolicShapeArray {
  }

  /** <code>SymbolicZerosArray({d1,d2,...})</code> */
  private static final class SymbolicZerosArray extends SymbolicShapeArray {
  }

  private static class ShearingTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr phi = ast.arg1();
      IExpr direction = ast.arg2();
      IExpr normal = ast.arg3();
      int dim = direction.isVector();
      if (dim <= 0 || normal.isVector() != dim) {
        return F.NIL;
      }
      IAST u = (IAST) direction.normal(false);
      IAST n = (IAST) normal.normal(false);
      IExpr normU = engine.evaluate(F.Norm(u));
      if (normU.isZero()) {
        // Direction vector `1` has zero magnitude.
        return Errors.printMessage(S.ShearingTransform, "idir", F.list(direction), engine);
      }
      IExpr normN = engine.evaluate(F.Norm(n));
      if (normN.isZero()) {
        // Direction vector `1` has zero magnitude.
        return Errors.printMessage(S.ShearingTransform, "idir", F.list(normal), engine);
      }
      // KroneckerDelta(i,j) + Tan(phi)*u_i*Conjugate(n_j) / (Norm(u)*Norm(n))
      IExpr tan = F.Tan(phi);
      IExpr scale = F.Times(normU, normN);
      IAST linear = F.mapRange(0, dim, i -> F.mapRange(0, dim, j -> {
        IExpr entry = F.Divide(F.Times(tan, u.get(i + 1), F.Conjugate(n.get(j + 1))), scale);
        return i == j ? F.Plus(F.C1, entry) : entry;
      }));
      IAST matrix = homogeneousMatrix(linear, null, dim);
      if (ast.size() == 5) {
        // ShearingTransform(phi, u, n, p) - leave the point `p` fixed
        return centeredTransform(F.TransformationFunction(matrix), ast.arg4(), dim);
      }
      return F.TransformationFunction(matrix);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }
  }


  /**
   * <code>GeometricTransformation(g, tf)</code> - the graphics primitive <code>g</code> with the
   * transformation <code>tf</code> applied to it.
   *
   * <p>
   * A {@link S#TransformationFunction} second argument is rewritten as the pair
   * <code>{linear, translation}</code>, which is the form the result is displayed in. The
   * primitive itself is left alone: the transformation is applied when the graphic is rendered, not
   * here.
   */
  private static class GeometricTransformation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST2()) {
        return F.NIL;
      }
      IExpr transformation = affinePair(ast.arg2());
      // NIL when nothing was rewritten, which is also what stops this from re-evaluating
      return transformation.isNIL() ? F.NIL : ast.setAtCopy(2, transformation);
    }

    /**
     * The <code>{linear, translation}</code> pair of a {@link S#TransformationFunction}, or of every
     * such function in a list of transformations.
     *
     * @return {@link F#NIL} if there is nothing to rewrite
     */
    private static IExpr affinePair(IExpr transformation) {
      if (transformation.isAST(S.TransformationFunction, 2)) {
        return splitHomogeneous(transformation.first());
      }
      if (transformation.isList()) {
        IAST list = (IAST) transformation;
        IASTAppendable result = F.NIL;
        for (int i = 1; i < list.size(); i++) {
          IExpr pair = affinePair(list.get(i));
          if (pair.isPresent()) {
            if (result.isNIL()) {
              result = list.copyAppendable();
            }
            result.set(i, pair);
          }
        }
        return result;
      }
      return F.NIL;
    }

    /**
     * Split the <code>(dim+1) x (dim+1)</code> homogeneous matrix
     * <code>{{linear, translation}, {0, ..., 0, 1}}</code> back into its two parts - the inverse of
     * {@link TensorFunctions#homogeneousMatrix(IAST, IAST, int)}.
     *
     * @return {@link F#NIL} if the matrix does not have that shape, in which case the
     *         transformation is projective and has no <code>{linear, translation}</code> form
     */
    private static IExpr splitHomogeneous(IExpr expr) {
      if (!expr.isList()) {
        return F.NIL;
      }
      IAST matrix = (IAST) expr;
      int dim = matrix.argSize() - 1;
      if (dim < 1) {
        return F.NIL;
      }
      for (int i = 1; i < matrix.size(); i++) {
        if (!matrix.get(i).isList() || matrix.get(i).argSize() != dim + 1) {
          return F.NIL;
        }
      }
      IAST lastRow = (IAST) matrix.get(dim + 1);
      for (int j = 1; j <= dim; j++) {
        if (!lastRow.get(j).isZero()) {
          return F.NIL;
        }
      }
      if (!lastRow.get(dim + 1).isOne()) {
        return F.NIL;
      }
      IASTAppendable linear = F.ListAlloc(dim);
      IASTAppendable translation = F.ListAlloc(dim);
      for (int i = 1; i <= dim; i++) {
        IAST row = (IAST) matrix.get(i);
        linear.append(row.copyFrom(1, dim + 1));
        translation.append(row.get(dim + 1));
      }
      return F.list(linear, translation);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class TransformationFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.head().isAST(S.TransformationFunction, 2)) {
        if (ast.isAST(S.TransformationFunction, 2) && ast.arg1().isList()) {
          // The wrapped matrix should be printed on a single line, so clear the matrix formatting
          // flag which `F.matrix()`, `DiagonalMatrix`, `Dot` or `Inverse` may have set.
          IAST matrix = (IAST) ast.arg1();
          matrix.clearFlag(Flag.IS_MATRIX);
        }
        return F.NIL;
      }

      IAST operator = (IAST) ast.head();
      IExpr m = operator.arg1();
      if (!ast.isAST1()) {
        // `1` called with `2` arguments; 1 argument is expected.
        return Errors.printMessage(S.TransformationFunction, "argx",
            F.List(ast, F.ZZ(ast.argSize())), engine);
      }
      int dim = ast.arg1().isVector();
      if (dim > 0) {
        IAST v = (IAST) ast.arg1().normal(false);
        // Take(m . Join(v, {1}), Length(v))
        return F.Take(F.Dot(m, F.Join(v, F.list(F.C1))), F.ZZ(dim));
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }


  }


  private static class TranslationTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim = ast.arg1().isVector();

      if (dim > 0) {
        IAST v = (IAST) ast.arg1().normal(false);
        int len = dim + 1;

        IAST matrix = F.matrix((i, j) -> {
          if (i == j) {
            return F.C1;
          }
          if (j == dim && i < len) {
            return v.get(i + 1);
          }
          return F.C0;
        }, len, len);

        return F.TransformationFunction(matrix);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }

  }

  private static Map<IExpr, IAST> tensorProperties(IAssumptions oldAssumptions,
      IExpr assumptionExpr) {
    if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
      IAssumptions assumptions =
          org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
      if (assumptions != null) {
        return assumptions.getTensorsMap();
      }
    } else {
      if (oldAssumptions != null) {
        return oldAssumptions.getTensorsMap();
      }
    }
    return null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private TensorFunctions() {}
}
