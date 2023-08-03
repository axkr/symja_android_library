package org.matheclipse.core.expression;

import java.util.concurrent.atomic.AtomicBoolean;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

public class BuiltinUsage {
  private static final String[] USAGE = new String[ID.Zeta + 10];

  private static AtomicBoolean IS_INITIALIZED = new AtomicBoolean();

  public static synchronized void init() {
    IS_INITIALIZED.set(true);

    USAGE[ID.$IterationLimit] =
        "specifies the maximum number of times a reevaluation of an expression may happen";

    USAGE[ID.$MaxMachineNumber] = "largest normalized positive machine number";
    USAGE[ID.$MinMachineNumber] = "smallest normalized positive machine number";

    USAGE[ID.$RecursionLimit] =
        "specifies the maximum allowable recursion depth after which a calculation is terminated";

    USAGE[ID.Abort] = "generate an abort";
    USAGE[ID.Abs] = "absolute value of a number";
    USAGE[ID.AbsoluteTime] = "get absolute time in seconds";
    USAGE[ID.AbsoluteTiming] = "get total wall-clock time to run a Mathics command";
    USAGE[ID.Accumulate] = "accumulates the values of $list$, returning a new list";
    USAGE[ID.Algebraics] = "domain of the Algebraic numbers";
    USAGE[ID.All] = "option value that specify using everything";
    USAGE[ID.AllTrue] = "all the elements are True";
    USAGE[ID.Alphabet] = "lowercase letters in an alphabet";
    USAGE[ID.And] = "logic conjunction";
    // USAGE[ID.AnglePath] = "form a path from a sequence of \"turtle-like\" turns and motions";
    USAGE[ID.AnyTrue] = "some of the elements are True";
    USAGE[ID.AngleVector] = "create a vector at a specified angle";
    USAGE[ID.Apart] = "partial fraction decomposition";
    USAGE[ID.ArcCos] = "inverse cosine function";
    USAGE[ID.ArcCosh] = "inverse hyperbolic cosine function";
    USAGE[ID.ArcCot] = "inverse cotangent function";
    USAGE[ID.ArcCoth] = "inverse hyperbolic cotangent function";
    USAGE[ID.ArcCsc] = "inverse cosecant function";
    USAGE[ID.ArcCsch] = "inverse hyperbolic cosecant function";
    USAGE[ID.ArcSec] = "inverse secant function";
    USAGE[ID.ArcSech] = "inverse hyperbolic secant function";
    USAGE[ID.ArcSin] = "inverse sine function";
    USAGE[ID.ArcSinh] = "inverse hyperbolic sine function";
    USAGE[ID.ArcTan] = "inverse tangent function";
    USAGE[ID.ArcTanh] = "inverse hyperbolic tangent function";
    USAGE[ID.Arg] = "phase of a complex number";
    USAGE[ID.Attributes] = "find the attributes of a symbol";
    USAGE[ID.Append] = "add an element at the end of an expression";
    USAGE[ID.AppendTo] = "add an element at the end of an stored list or expression";
    USAGE[ID.Association] = "an association between keys and values";
    USAGE[ID.AssociationQ] = "test if an expression is a valid association";
    USAGE[ID.Assuming] = "set assumptions during the evaluation";
    USAGE[ID.Assumptions] = "assumptions used to simplify expressions";

    USAGE[ID.BaseForm] = "print with all numbers given in a base";
    USAGE[ID.BaseDecode] = "decode a base64 string";
    USAGE[ID.BaseEncode] = "encode an element as a base64 string";
    USAGE[ID.Beta] = "Euler's Beta function";
    USAGE[ID.Boole] = "translate 'True' to 1, and 'False' to 0";
    USAGE[ID.Booleans] = "domain of boolean values";
    USAGE[ID.BooleanQ] = "test whether the expression evaluates to a boolean constant";
    USAGE[ID.Binomial] = "binomial coefficients";
    USAGE[ID.Break] = "exit a 'For', 'While', or 'Do' loop";
    USAGE[ID.ByteArray] = "array of bytes";

    USAGE[ID.C] = "n-th intertermined constant in the solution of a differential equation";
    USAGE[ID.Cancel] = "cancel common factors in rational expressions";
    USAGE[ID.Catch] = "handle an exception raised by a 'Throw'";
    USAGE[ID.Catenate] = "catenate elements from a list of lists";
    USAGE[ID.CentralMoment] = "central moments of distributions and data";
    USAGE[ID.Condition] = "expression defined under condition";
    USAGE[ID.Characters] = "list the characters in a string";
    USAGE[ID.CharacterRange] = "range of characters with successive character codes";
    USAGE[ID.Check] = "discard the result if the evaluation produced messages";
    USAGE[ID.ClearAttributes] = "clear the attributes of a symbol";
    USAGE[ID.Coefficient] = "coefficient of a monomial in a polynomial expression";
    USAGE[ID.CoefficientList] = "list of coefficients defining a polynomial";
    USAGE[ID.Collect] = "collect terms with a variable at the same power";
    USAGE[ID.Conjugate] = "complex conjugation";
    USAGE[ID.Constant] = "attribute that indicates that a symbol is a (numerical) constant";
    USAGE[ID.ConstantArray] = "form a constant array";
    USAGE[ID.Continue] = "continue with the next iteration in a 'For', 'While' or 'Do' loop";
    USAGE[ID.ContinuedFraction] = "continued fraction expansion";
    USAGE[ID.ContainsOnly] = "test if all the elements of a list appear into another list";
    USAGE[ID.Complement] = "find the complement with respect to a universal set";
    USAGE[ID.Complex] = "head for complex numbers";
    USAGE[ID.ComplexExpand] = "expand a complex expression of real variables";
    USAGE[ID.Complexes] = "the domain of Complex numbers";
    USAGE[ID.CompoundExpression] = "execute expressions in sequence";
    USAGE[ID.Conjugate] = "complex conjugate value";
    USAGE[ID.Context] = "give the name of the context of a symbol";
    USAGE[ID.Correlation] = "Pearson's correlation of a pair of datasets";
    USAGE[ID.CoprimeQ] = "test whether elements are coprime";
    USAGE[ID.Cos] = "cosine function";
    USAGE[ID.Cosh] = "hyperbolic cosine function";
    USAGE[ID.Cot] = "cotangent function";
    USAGE[ID.Coth] = "hyperbolic cotangent function";
    USAGE[ID.Csc] = "cosecant function";
    USAGE[ID.Csch] = "hyperbolic cosecant function";
    USAGE[ID.Count] = "count the number of occurrences of a pattern";
    USAGE[ID.Covariance] = "covariance matrix for a pair of datasets";
    USAGE[ID.CubeRoot] = "real-valued cube root";

    USAGE[ID.D] = "partial derivative of a function";
    USAGE[ID.Definition] = "give values of a symbol in a form that can be stored in a package";
    USAGE[ID.Delete] = "delete elements from a list at given positions";
    USAGE[ID.DeleteCases] = "delete all occurrences of a pattern";
    USAGE[ID.DeleteDuplicates] = "delete duplicate elements in a list";
    USAGE[ID.Denominator] = "denominator of an expression";
    USAGE[ID.Depth] = "get maximum number of indices to specify any part";
    USAGE[ID.Derivative] = "symbolic and numerical derivative functions";
    USAGE[ID.Diagonal] = "gives a list with the diagonal elements of a given matrix";
    USAGE[ID.DiagonalMatrix] = "give a diagonal matrix with the elements of a given list";
    USAGE[ID.DiceDissimilarity] = "Dice dissimilarity";
    USAGE[ID.DigitCharacter] = "digit 0-9";
    USAGE[ID.DigitQ] = "test whether all the characters are digits";
    USAGE[ID.DirectedInfinity] = "infinite quantity with a defined direction in the complex plane";
    USAGE[ID.DiscretePlot] = "discrete plot of a one-parameter function";
    USAGE[ID.DisjointQ] = "test whether two lists do not have common elements";
    USAGE[ID.Divide] = "divide";
    USAGE[ID.Divisors] = "integer divisors";
    USAGE[ID.Do] = "evaluate an expression looping over a variable";
    USAGE[ID.Drop] = "remove a number of elements from a list";
    USAGE[ID.DSolve] = "differential equation analytical solver";

    USAGE[ID.EditDistance] = "edit distance";
    USAGE[ID.Element] = "check whether the element belongs to the domain";
    USAGE[ID.EllipticE] = "elliptic integral of the second kind E(ϕ|m)";
    USAGE[ID.EllipticF] = "elliptic integral F(ϕ|m)";
    USAGE[ID.EllipticK] = "elliptic integral of the first kind K(m)";
    USAGE[ID.EllipticPi] = "elliptic integral of the third kind P(n|m)";
    USAGE[ID.EndOfLine] = "a string pattern matching EOL";
    USAGE[ID.Equal] = "numerical equality";
    USAGE[ID.Equivalent] = "logic equivalence";
    USAGE[ID.EulerPhi] = "Euler totient function";
    USAGE[ID.Evaluate] =
        "forces evaluation of $expr$, even if it occurs inside a held argument or a 'Hold' form";
    USAGE[ID.EvenQ] = "test whether elements are even numbers";
    USAGE[ID.Exp] = "exponential function";
    USAGE[ID.Expand] = "expand out products and powers";
    USAGE[ID.ExpandAll] = "expand products and powers, including negative integer powers";
    USAGE[ID.Exponent] = "maximum power in which a form appears in a polynomial";
    USAGE[ID.Export] = "export elements to a file";
    USAGE[ID.ExportString] = "export elements to a string";
    USAGE[ID.Extract] = "extract elements that appear at a list of positions";

    USAGE[ID.Factor] = "factor sums into product and powers";
    USAGE[ID.Factorial] = "factorial";
    USAGE[ID.Factorial2] = "semi-factorial";
    USAGE[ID.FactorInteger] = "list of prime factors and exponents";
    USAGE[ID.FactorTermsList] = "a polynomial as a list of factors";
    // USAGE[ID.Failure] = "a failure at the level of the interpreter";
    USAGE[ID.False] = "boolean constant for False";
    USAGE[ID.FileFormat] = "determine the file format of a file";
    USAGE[ID.FindMaximum] = "local maximum optimization";
    USAGE[ID.FindMinimum] = "local minimum optimization";
    USAGE[ID.FindRoot] = "looks for a root of an equation or a zero of a numerical expression";
    USAGE[ID.FindClusters] = "divide data into lists of similar elements";
    USAGE[ID.First] = "first element of a list or expression";
    USAGE[ID.FirstCase] = "first element that matches a pattern";
    USAGE[ID.FirstPosition] = "position of the first element matching a pattern";
    USAGE[ID.FixedPoint] = "nest until a fixed point is reached returning the last expression";
    USAGE[ID.FixedPointList] = "nest until a fixed point is reached return a list";
    USAGE[ID.Flat] = "attribute for functions that must be flattened";
    USAGE[ID.Flatten] = "flatten out any sequence of levels in a nested list";
    USAGE[ID.Fold] = "iterative application of a binary operation over elements of a list";
    USAGE[ID.FoldList] =
        "list of the results of applying a binary operation interatively over elements of a list";
    USAGE[ID.For] = "a 'For' loop";
    USAGE[ID.FractionalPart] = "fractional part of a number";
    USAGE[ID.FreeQ] = "test whether an expression is free of subexpressions matching a pattern";
    USAGE[ID.FromCharacterCode] = "convert from a list of character codes to a string";
    USAGE[ID.FromContinuedFraction] =
        "reconstructs a number from its continued fraction representation";
    USAGE[ID.FullForm] = "get the underlying M-Expression representation";
    USAGE[ID.FullSimplify] = "apply a full set of simplification transformations";

    USAGE[ID.Gamma] = "complete and incomplete gamma functions";
    USAGE[ID.Gather] = "gather sublists of identical elements";
    USAGE[ID.GatherBy] = "gather based on values of a function applied to elements";
    USAGE[ID.GCD] = "greatest common divisor";
    USAGE[ID.General] = "general-purpose messages";
    USAGE[ID.Greater] = "greater than";
    USAGE[ID.GreaterEqual] = "greater than or equal to";
    USAGE[ID.Gudermannian] = "Gudermannian function gd(z)";

    USAGE[ID.HammingDistance] = "Hamming distance";
    USAGE[ID.Haversine] = "haversine function";
    USAGE[ID.Hold] = "prevents $expr$ from being evaluated";
    USAGE[ID.HoldAll] = "attribute specifying that all the arguments should be left unevaluated";
    USAGE[ID.HoldAllComplete] =
        "attribute specifying that all the arguments should be left unevaluated, even if includes sequences, or upvalues";
    USAGE[ID.HoldComplete] =
        "prevents $expr$ from being evaluated, and also prevents 'sequence' objects from being spliced into argument lists";
    USAGE[ID.HoldFirst] = "attribute specifying that the first argument should be left unevaluated";
    USAGE[ID.HoldForm] = "is equivalent to 'Hold[$expr$]', but prints as $expr$";
    USAGE[ID.HoldRest] =
        "attribute specifying that all but the first argument should be left unevaluated";

    USAGE[ID.IdentityMatrix] = "give the identity matrix with a given dimension";
    USAGE[ID.Import] = "import elements from a file";
    USAGE[ID.ImportString] = "import elements from a string";
    USAGE[ID.InverseHaversine] = "inverse haversine function";
    USAGE[ID.I] = "represents the imaginary number 'Sqrt(-1)'";
    USAGE[ID.If] = "test if a condition is true, false, or of unknown truth value";
    USAGE[ID.Im] = "returns the imaginary component of the complex number $z$";
    USAGE[ID.Implies] = "logic implication";
    USAGE[ID.Inequality] = "chain of inequalities";
    USAGE[ID.Infix] = "infix form";
    USAGE[ID.Information] = "get information about all assignments for a symbol";
    USAGE[ID.Input] = "the name of the current input stream";
    USAGE[ID.InputForm] = "plain-text input format";
    USAGE[ID.Insert] = "insert an element at a given position";
    USAGE[ID.Integer] = "head for integer numbers";
    USAGE[ID.Integers] = "the domain of Integer numbers";
    USAGE[ID.Integrate] = "indefinite or definite integral of a function";
    USAGE[ID.Interrupt] = "interrupt evaluation and return '$Aborted'";
    USAGE[ID.Intersection] = "enumerate common elements";
    USAGE[ID.IntersectingQ] = "test whether two lists have common elements";
    USAGE[ID.InverseGudermannian] = "Gudermannian function gd^-1(z)";

    USAGE[ID.JaccardDissimilarity] = "Jaccard dissimilarity";
    USAGE[ID.JavaForm] = "translate expressions as Java source code";
    USAGE[ID.Join] = "join lists together at any level";

    USAGE[ID.Key] = "indicate a key within a part specification";
    USAGE[ID.Keys] = "list association keys";
    USAGE[ID.KroneckerProduct] = "Kronecker product";
    USAGE[ID.Kurtosis] = "kurtosis coefficient";

    USAGE[ID.Last] = "last element of a list or expression";
    USAGE[ID.LCM] = "least common multiple";
    USAGE[ID.LeafCount] = "get the total number of atomic subexpressions";
    USAGE[ID.Length] = "number of elements in a list or expression";
    USAGE[ID.Less] = "less than";
    USAGE[ID.LessEqual] = "less than or equal to";
    USAGE[ID.LetterNumber] = "position of a letter in an alphabet";
    USAGE[ID.LetterQ] = "test whether all the characters are letters";
    USAGE[ID.Level] = "get parts specified by a given number of indices";
    USAGE[ID.LevelQ] = "test whether is a valid level specification";
    USAGE[ID.Limit] = "directed and undirected limits";
    USAGE[ID.List] = "specify a list explicitly";
    USAGE[ID.Listable] = "Attribute for functions that automatically thread over their arguments";
    USAGE[ID.ListQ] = "test if an expression is a list";
    USAGE[ID.Locked] = "keep all attributes locked (settable but not clearable)";
    USAGE[ID.Log] = "natural logarithm function";
    USAGE[ID.LogGamma] = "logarithm of the gamma function";
    USAGE[ID.Log2] = "base-2 logarithm function";
    USAGE[ID.Log10] = "base-10 logarithm function";
    USAGE[ID.LogisticSigmoid] = "logistic function";
    USAGE[ID.Lookup] =
        "perform lookup of a value by key, returning a specified default if it is not found";
    USAGE[ID.LowerCaseQ] = "test wether all the characters are lower-case letters";

    USAGE[ID.MantissaExponent] = "decomposes numbers as mantissa and exponent";
    USAGE[ID.MatchingDissimilarity] = "simple matching dissimilarity";
    USAGE[ID.MathMLForm] = "format expression as MathML commands";
    USAGE[ID.MatrixForm] = "format as a matrix";
    USAGE[ID.MatrixQ] = "gives 'True' if the given argument is a list of equal-length lists";
    USAGE[ID.Max] = "the smallest argument or the largest element of a list";
    USAGE[ID.Mean] = "returns the statistical mean of a list";
    USAGE[ID.Median] = "central value of a dataset";
    USAGE[ID.MemberQ] = "test whether an element is a member of a list";
    USAGE[ID.Message] = "display a message";
    USAGE[ID.MessageName] = "message identifyier";
    USAGE[ID.Min] = "the largest argument or the largest element of a list";
    USAGE[ID.Minus] = "arithmetic negate";
    USAGE[ID.MinimalPolynomial] = "minimal polynomial for a general algebraic number";
    USAGE[ID.Missing] = "default value if a key is not found";
    USAGE[ID.Mod] = "the remainder in an integer division";
    USAGE[ID.ModularInverse] = "returns the modular inverse k^(-1) MOD n";
    USAGE[ID.Most] = "remove the last element";
    USAGE[ID.Multinomial] = "multinomial coefficients";

    USAGE[ID.Names] = "find a list of symbols with names matching a pattern";
    USAGE[ID.Nand] = "negation of logic conjunction";
    USAGE[ID.Nearest] = "the nearest element from a list";
    USAGE[ID.Negative] = "test whether an expression is a negative number";
    USAGE[ID.Nest] = "give the result of nesting a function";
    USAGE[ID.NestList] = "successively nest a function and append the results to a list";
    USAGE[ID.NestWhile] = "nest while a condition is satisfied returning the last expression";
    USAGE[ID.NextPrime] = "closest, smallest prime number";
    USAGE[ID.NHoldAll] =
        "attribute that indicates that the arguments must not be evaluated in numerical evaluations";
    USAGE[ID.NHoldFirst] =
        "attribute that indicates that the first argument must not be evaluated in numerical evaluations";
    USAGE[ID.NHoldRest] =
        "attribute that indicates that just the first  argument must be evaluated in numerical evaluations";
    USAGE[ID.NIntegrate] = "numerical integration";
    USAGE[ID.None] = "option value that disables the option";
    USAGE[ID.Nor] = "negation of logic (inclusive) disjunction";
    USAGE[ID.Normal] = "convert objects to normal expressions";
    USAGE[ID.Normalize] = "normalizes a vector";
    USAGE[ID.Not] = "logic negation";
    USAGE[ID.NotListQ] = "test if an expression is not a list";
    USAGE[ID.Null] = "implicit result for expressions that do not yield a result";
    USAGE[ID.NumberLinePlot] = "plot along a number line";
    USAGE[ID.NumberQ] = "test whether an expression is a number";
    USAGE[ID.NumberString] = "characters in string representation of a number";
    USAGE[ID.Numerator] = "numerator of an expression";
    USAGE[ID.NumericFunction] = "attribute that indicates that a symbol is a numerical function";
    USAGE[ID.NonNegative] = "test whether an expression is a non-negative number";
    USAGE[ID.NonPositive] = "test whether an expression is a non-positive number";

    USAGE[ID.O] = "symbolic representation of a higher-order series term";
    USAGE[ID.OddQ] = "test whether elements are odd numbers";
    // USAGE[ID.Off] = "turn off a message for printing";
    // USAGE[ID.On] = "turn on a message for printing";
    USAGE[ID.OneIdentity] =
        "attribute specifying that a function behaves like the Identity in pattern matching";
    USAGE[ID.Operate] = "apply a function to the head of an expression";
    USAGE[ID.Or] = "logic (inclusive) disjunction";
    USAGE[ID.Order] = "order expressions";
    USAGE[ID.OrderedQ] = "test whether elements are canonically sorted";
    USAGE[ID.Orderless] =
        "attribute for functions with results that does not depends on the order of their arguments";
    USAGE[ID.OutputForm] = "plain-text output format";

    USAGE[ID.PadLeft] = "pad out by the left a ragged array to make a matrix";
    USAGE[ID.PadRight] = "pad out by the right a ragged array to make a matrix";
    USAGE[ID.Part] = "get/set any part of an expression";
    USAGE[ID.ParametricPlot] = "2D parametric curves or regions";
    USAGE[ID.Partition] = "partition a list into sublists of a given length";
    USAGE[ID.PartitionsP] = "number of unrestricted partitions";
    USAGE[ID.PauliMatrix] = "Pauli spin matrix";
    USAGE[ID.Permutations] = "form permutations of a list";
    USAGE[ID.Pick] = "pick out elements according to a boolean mask";
    USAGE[ID.Piecewise] = "an arbitrary piecewise function";
    USAGE[ID.Plot] = "curves of one or more functions";
    USAGE[ID.Plus] = "add";
    USAGE[ID.Pochhammer] = "Pochhammer's symbols";
    USAGE[ID.PolarPlot] = "draw a polar plot";
    USAGE[ID.PolyGamma] = "polygamma function";
    USAGE[ID.PolynomialQ] = "test if the expression is a polynomial in a variable";
    USAGE[ID.PossibleZeroQ] =
        "returns 'True' if basic symbolic and numerical methods suggest that expr has value zero, and 'False' otherwise";
    USAGE[ID.Position] = "positions of matching elements";
    USAGE[ID.Positive] = "test whether an expression is a positive number";
    USAGE[ID.Postfix] = "postfix form";
    USAGE[ID.Power] = "exponentiate";
    USAGE[ID.PowerExpand] = "expand out powers";
    USAGE[ID.PowerMod] = "modular powers and roots";
    USAGE[ID.Prefix] = "prefix form";
    USAGE[ID.Prepend] = "add an element at the beginning";
    USAGE[ID.PrependTo] = "add an element at the beginning of an stored list or expression";
    USAGE[ID.Prime] = "n-th prime number";
    USAGE[ID.Primes] = "the domain of the prime numbers";
    USAGE[ID.PrimePi] = "amount of prime numbers less than or equal";
    USAGE[ID.PrimePowerQ] = "test if a number is a power of a prime number";
    USAGE[ID.PrimeQ] = "test whether elements are prime numbers";
    USAGE[ID.Print] = "print strings and formatted text";
    USAGE[ID.Product] = "discrete product";
    USAGE[ID.Projection] = "find the projection of one vector on another";
    USAGE[ID.Protect] = "protect a symbol against redefinitions";
    USAGE[ID.Protected] = "attribute of protected symbols";

    USAGE[ID.Quantile] =
        "cut points dividing the range of a probability distribution into continuous intervals";
    USAGE[ID.Quartiles] = "list of quartiles";
    USAGE[ID.Quiet] = "evaluate without showing messages";
    USAGE[ID.Quit] = "terminates the Symja session...";
    USAGE[ID.Quotient] = "integer quotient";
    USAGE[ID.QuotientRemainder] = "integer quotient and remainder";

    USAGE[ID.RandomInteger] = "pick an integer number at random from a range";
    USAGE[ID.RandomChoice] = "choice items at random from a list";
    USAGE[ID.RandomComplex] = "pick a complex number at random from a rectangular region";
    USAGE[ID.RandomPrime] = "picks a random prime in an interval";
    USAGE[ID.RandomReal] = "pick a real number at random from an interval";
    USAGE[ID.RandomSample] = "pick a sample at random from a list";
    USAGE[ID.Range] = "generate a list of equispaced, consecutive numbers";
    USAGE[ID.RankedMax] = "the n-th largest item";
    USAGE[ID.RankedMin] = "the n-th smallest item";
    USAGE[ID.Rational] = "head for rational numbers";
    USAGE[ID.Rationals] = "the domain of Rational numbers";
    USAGE[ID.Re] = "returns the real component of the complex number $z$";
    USAGE[ID.ReadProtected] = "attribute of symbols with hidden definitions";
    USAGE[ID.RealAbs] = "real absolute value";
    USAGE[ID.Reals] = "the domain of the Real numbers";
    USAGE[ID.RealSign] = "real sign";
    USAGE[ID.Reap] = "create lists of elements \"sown\" inside programs";
    USAGE[ID.RemoveDiacritics] = "remove diacritics from a string";
    USAGE[ID.Real] = "head for real numbers";
    USAGE[ID.ReleaseHold] =
        "removes any 'Hold', 'HoldForm', 'HoldPattern' or 'HoldComplete' head from $expr$";
    USAGE[ID.RealValuedNumberQ] =
        "returns 'True' if $expr$ is an explicit number with no imaginary component";
    USAGE[ID.RegularExpression] = "string to regular expression";
    USAGE[ID.ReplacePart] = "replace elements at given positions";
    USAGE[ID.Rest] = "remove the first element";
    USAGE[ID.Return] = "return from a function";
    USAGE[ID.Reverse] = "reverse a list at any level";
    USAGE[ID.Riffle] = "intersperse additional elements";
    USAGE[ID.RogersTanimotoDissimilarity] = "Rogers-Tanimoto dissimilarity";
    USAGE[ID.Root] = "the i-th root of a polynomial";
    USAGE[ID.RotateLeft] = "cyclically rotate lists to the left, at any depth";
    USAGE[ID.RotateRight] = "cyclically rotate lists to the right, at any depth";
    USAGE[ID.RotationTransform] = "symbolic representation of a rotation in 3D";
    USAGE[ID.RussellRaoDissimilarity] = "Russell-Rao dissimilarity";

    USAGE[ID.SameQ] = "literal symbolic identity";
    USAGE[ID.ScalingTransform] = "symbolic representation of a scale transformation";
    USAGE[ID.Sec] = "secant function";
    USAGE[ID.Sech] = "hyperbolic secant function";
    USAGE[ID.Select] = "pick elements according to a criterion";
    USAGE[ID.Sequence] = "represents a sequence of arguments to a function";
    USAGE[ID.SequenceHold] =
        "attribute that prevents 'Sequence' objects from being spliced into a function's arguments.";
    USAGE[ID.Series] = "power series and asymptotic expansions";
    USAGE[ID.SeriesData] = "power series of a variable about a point";
    USAGE[ID.SetAttributes] = "set attributes for a symbol";
    USAGE[ID.ShearingTransform] = "symbolic representation of a shearing transformation";
    USAGE[ID.Sign] = "return -1, 0, or 1 depending on whether $x$ is negative, zero, or positive";
    USAGE[ID.Simplify] = "apply transformations to simplify an expression";
    USAGE[ID.Sin] = "sine function";
    USAGE[ID.Sinh] = "hyperbolic sine function";
    USAGE[ID.Skewness] = "skewness coefficient";
    USAGE[ID.Slot] = "`#` serves as a pure function's first parameter ";
    USAGE[ID.SokalSneathDissimilarity] = "Sokal-Sneath dissimilarity";
    USAGE[ID.Solve] = "find generic solutions for variables";
    USAGE[ID.Sort] = "sort the elements of an expression";
    USAGE[ID.SortBy] = "sort by the values of a function applied to elements";
    USAGE[ID.Sow] = "send an expression to the innermost Reap";
    USAGE[ID.Span] = "general specification for spans or blocks of elements";
    USAGE[ID.Split] = "split into runs of identical elements";
    USAGE[ID.SplitBy] = "split based on values of a function applied to elements";
    USAGE[ID.Sqrt] = "square root";
    USAGE[ID.StandardDeviation] = "standard deviation of a dataset";
    USAGE[ID.StandardForm] = "default output format";
    USAGE[ID.StieltjesGamma] = "Stieltjes' function";
    USAGE[ID.String] = "head for strings";
    USAGE[ID.StringCases] = "occurrences of string patterns in a string";
    USAGE[ID.StringContainsQ] = "test whether a pattern matches with a substring";
    USAGE[ID.StringDrop] = "drop a part of a string";
    USAGE[ID.StringExpression] = "an arbitrary string expression";
    // USAGE[ID.StringForm] = "make an string from a template and a list of parameters";
    USAGE[ID.StringFreeQ] = "test whether a string is free of substrings matching a pattern";
    USAGE[ID.StringInsert] = "insert a string in a given position";
    USAGE[ID.StringJoin] = "join strings together";
    USAGE[ID.StringLength] = "length of a string (in Unicode characters)";
    USAGE[ID.StringMatchQ] = "test whether a string matches a pattern";
    USAGE[ID.StringPosition] = "range of positions where substrings match a pattern";
    USAGE[ID.StringQ] = "test whether an expression is a string";
    USAGE[ID.StringRepeat] = "build a string by concatenating repetitions";
    USAGE[ID.StringReplace] = "apply replace rules to substrings";
    USAGE[ID.StringReverse] = "revert the order of the characters";
    USAGE[ID.StringRiffle] = "assemble a string from a list, inserting delimiters";
    USAGE[ID.StringSplit] = "split strings at whitespace, or at a pattern";
    USAGE[ID.StringTake] = "sub-string from a range of positions";
    USAGE[ID.StringTrim] = "trim whitespace etc. from strings";
    USAGE[ID.Subscript] = "format an expression with a subscript";
    USAGE[ID.SubsetQ] = "test if a list is a subset of another list";
    USAGE[ID.Subsets] = "list all the subsets";
    USAGE[ID.Subsuperscript] = "format an expression with a subscript and a superscript";
    USAGE[ID.Subtract] = "subtract";
    USAGE[ID.Sum] = "discrete sum";
    USAGE[ID.Superscript] = "format an expression with a superscript";
    USAGE[ID.Switch] = "switch based on a value, with patterns allowed";
    USAGE[ID.Symbol] = "the head of a symbol; create a symbol from a name";
    USAGE[ID.SymbolName] = "give the name of a symbol as a string";
    // USAGE[ID.SymjaForml] = "translate expressions to Symja";
    // USAGE[ID.Syntax] = "syntax messages";

    USAGE[ID.Table] = "make a table of values of an expression";
    USAGE[ID.TableForm] = "format as a table";
    USAGE[ID.Take] = "pick a range of elements";
    USAGE[ID.TakeLargest] = "sublist of n largest elements";
    USAGE[ID.TakeLargestBy] = "sublist of n largest elements according to a given criteria";
    USAGE[ID.TakeSmallest] = "sublist of n smallest elements";
    USAGE[ID.TakeSmallestBy] = "sublist of n largest elements according to a criteria";
    USAGE[ID.Tally] = "tally all distinct elements in a list";
    USAGE[ID.Tan] = "tangent function";
    USAGE[ID.Tanh] = "hyperbolic tangent function";
    USAGE[ID.TensorProduct] = "tensor product";
    USAGE[ID.TeXForm] = "formatted expression as TeX commands";
    USAGE[ID.Through] = "distribute operators that appears inside the head of expressions";
    USAGE[ID.Throw] = "throw an expression to be caught by a surrounding 'Catch'";
    USAGE[ID.Times] = "multiply";
    USAGE[ID.ToCharacterCode] = "convert a string to a list of character codes";
    USAGE[ID.ToExpression] = "build an expression from formatted text";
    USAGE[ID.ToLowerCase] = "turn all the letters into lower case";
    USAGE[ID.Together] = "put over a common denominator";
    USAGE[ID.ToString] = "format an expression and produce a string";
    USAGE[ID.ToUpperCase] = "turn all the letters into upper case";
    USAGE[ID.Total] = "adds all values in $list$...";
    USAGE[ID.TransformationFunction] = "general symbolic representation of transformation";
    USAGE[ID.TranslationTransform] = "symbolic representation of translation";
    USAGE[ID.Transliterate] = "transliterate an UTF string in different alphabets to ASCII";
    USAGE[ID.Transpose] = "transpose to rearrange indices in any way";
    USAGE[ID.True] = "boolean constant for True";
    USAGE[ID.TrueQ] = "returns 'true' if and only if $expr$ is 'true'";
    USAGE[ID.Tuples] = "form n-tuples from a list";

    USAGE[ID.UpperCaseQ] = "test wether all the characters are upper-case letters";
    USAGE[ID.Undefined] = "undefined value";
    USAGE[ID.Unequal] = "numerical inequality";
    USAGE[ID.Unevaluated] =
        "temporarily leaves $expr$ in an unevaluated form when it appears as a function argument";
    USAGE[ID.Union] = "enumerate all distinct elements in a list";
    USAGE[ID.UnitVector] = "unit vector along a coordinate direction";
    USAGE[ID.Unprotect] = "remove protection against redefinitions";
    USAGE[ID.UnsameQ] = "not literal symbolic identity";
    USAGE[ID.UpTo] = "a certain number of elements, or as many as are available";

    USAGE[ID.ValueQ] = "test whether a symbol can be considered to have a value";
    USAGE[ID.Values] = "list association values";
    USAGE[ID.Variables] = "list of variables in a polynomial";
    USAGE[ID.Variance] = "variance of a dataset";
    USAGE[ID.VectorQ] = "test whether an object is a vector";

    USAGE[ID.Which] = "test which of a sequence of conditions are true";
    USAGE[ID.While] = "evaluate an expression while a criterion is true";
    USAGE[ID.Whitespace] = "sequence of whitespace characters";
    USAGE[ID.WhitespaceCharacter] = "space, newline, tab, or other whitespace character";
    USAGE[ID.WordBoundary] = "boundary between word characters and others";
    USAGE[ID.WordCharacter] = "letter or digit";

    USAGE[ID.Xor] = "logic (exclusive) disjunction";

    USAGE[ID.YuleDissimilarity] = "Yule dissimilarity";
  }


  /**
   * Return a short summary of the built-in symbols functionality.
   * 
   */
  public static String summaryText(IBuiltInSymbol symbol) {
    if (!IS_INITIALIZED.get()) {
      init();
    }
    String str = USAGE[symbol.ordinal()];
    return str != null ? str : "";
  }
}
