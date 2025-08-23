package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IBuiltInSymbol;

public class BuiltinUsage {
  private static final String[] USAGE = new String[ID.Zeta + 10];

  static {

    USAGE[ID.$ContextPath] = "the current context search path";
    USAGE[ID.$IterationLimit] =
        "specifies the maximum number of times a reevaluation of an expression may happen";

    USAGE[ID.$MaxMachineNumber] = "largest normalized positive machine number";
    USAGE[ID.$MinMachineNumber] = "smallest normalized positive machine number";

    USAGE[ID.$Packages] = "list the packages loaded in the current session";

    USAGE[ID.$RecursionLimit] =
        "specifies the maximum allowable recursion depth after which a calculation is terminated";

    USAGE[ID.AASTriangle] = "triangle from angle, angle, side";
    USAGE[ID.Abort] = "generate an abort";
    USAGE[ID.Abs] = "absolute value of a number";
    USAGE[ID.AbsoluteTime] = "get absolute time in seconds";
    USAGE[ID.AbsoluteTiming] = "get total wall-clock time to run a Symja command";
    USAGE[ID.Accumulate] = "accumulates the values of $list$, returning a new list";
    USAGE[ID.AddTo] = "add a value and assignes that returning the new value";
    USAGE[ID.Algebraics] = "domain of the Algebraic numbers";
    USAGE[ID.All] = "option value that specify using everything";
    USAGE[ID.AllTrue] = "all the elements are True";
    USAGE[ID.Alphabet] = "lowercase letters in an alphabet";
    USAGE[ID.And] = "logic conjunction";
    // USAGE[ID.AnglePath] = "form a path from a sequence of \"turtle-like\" turns and motions";
    USAGE[ID.AnyTrue] = "some of the elements are True";
    USAGE[ID.AngleVector] = "create a vector at a specified angle";
    USAGE[ID.Apart] = "partial fraction decomposition";
    USAGE[ID.Apply] = "apply a function to a list, at specified levels";
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
    USAGE[ID.ArrayDepth] = "the rank of a tensor";
    USAGE[ID.ArrayQ] = "test whether an object is a tensor of a given rank";
    USAGE[ID.ASATriangle] = "triangle from angle, side, angle";
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
    USAGE[ID.Begin] = "temporarily set the current context";
    USAGE[ID.BeginPackage] = "temporarily set the context and clean the context path";
    USAGE[ID.BellB] = "Bell numbers";
    USAGE[ID.BernoulliB] = "Bernoulli number and polynomial";
    USAGE[ID.Beta] = "Euler's Beta function";
    USAGE[ID.Between] = "test if value or values are in range";
    USAGE[ID.Block] = "evaluate an expression using local values for some given symbols";
    USAGE[ID.Boole] = "translate 'True' to 1, and 'False' to 0";
    USAGE[ID.Booleans] = "domain of boolean values";
    USAGE[ID.BooleanQ] = "test whether the expression evaluates to a boolean constant";
    USAGE[ID.Binomial] = "binomial coefficients";
    USAGE[ID.BitAnd] = "bitwise AND of integer numbers";
    USAGE[ID.BitClear] = "clear the designated bit of an integer number";
    USAGE[ID.BitFlip] = "toggle the designated bit of an integer number on/off";
    USAGE[ID.BitGet] = "returns 1 if the designated bit is set; otherwise returns 0";
    USAGE[ID.BitLength] = "number of bits in the minimal two's-complement representation";
    USAGE[ID.BitNot] = "bitwise NOT of an integer number";
    USAGE[ID.BitOr] = "bitwise OR of integer numbers";
    USAGE[ID.BitSet] = "set the designated bit of an integer number";
    USAGE[ID.BitXor] = "bitwise XOR of integer numbers";
    USAGE[ID.Break] = "exit a 'For', 'While', or 'Do' loop";
    USAGE[ID.ByteArray] = "array of bytes";

    USAGE[ID.C] = "n-th intertermined constant in the solution of a differential equation";
    USAGE[ID.Cancel] = "cancel common factors in rational expressions";
    USAGE[ID.Catalan] = "Catalan's constant C≃0.916";
    USAGE[ID.Catch] = "handle an exception raised by a 'Throw'";
    USAGE[ID.Catenate] = "catenate elements from a list of lists";
    USAGE[ID.CentralMoment] = "central moments of distributions and data";
    USAGE[ID.Condition] = "expression defined under condition";
    USAGE[ID.Context] = "the current context";
    USAGE[ID.Contexts] = "list all the defined contexts";
    USAGE[ID.Characters] = "list the characters in a string";
    USAGE[ID.CharacterRange] = "range of characters with successive character codes";
    USAGE[ID.ChebyshevT] = "Chebyshev's polynomials of the first kind";
    USAGE[ID.ChebyshevU] = "Chebyshev's polynomials of the second kind";
    USAGE[ID.Check] = "discard the result if the evaluation produced messages";
    USAGE[ID.Clear] = "clear values associated with the LHS or symbol";
    USAGE[ID.ClearAll] = "clear all values, definitions, messages and defaults for symbols";
    USAGE[ID.ClearAttributes] = "clear the attributes of a symbol";
    USAGE[ID.ClebschGordan] = "Clebsch-Gordan coefficient";
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
    USAGE[ID.ComplexInfinity] = "infinite complex quantity of undetermined direction";
    USAGE[ID.Complexes] = "the domain of Complex numbers";
    USAGE[ID.CompoundExpression] = "execute expressions in sequence";
    USAGE[ID.CompositeQ] = "test whether a number is composite";
    USAGE[ID.Condition] = "conditional definition";
    USAGE[ID.Conjugate] = "complex conjugate value";
    USAGE[ID.ConjugateTranspose] = "conjugate transposed matrix";
    USAGE[ID.Context] = "the context of a symbol";
    USAGE[ID.Correlation] = "Pearson's correlation of a pair of datasets";
    USAGE[ID.CoprimeQ] = "test whether elements are coprime";
    USAGE[ID.Cos] = "cosine function";
    USAGE[ID.Cosh] = "hyperbolic cosine function";
    USAGE[ID.Cot] = "cotangent function";
    USAGE[ID.Coth] = "hyperbolic cotangent function";
    USAGE[ID.Cross] = "get vector cross product";
    USAGE[ID.Csc] = "cosecant function";
    USAGE[ID.Csch] = "hyperbolic cosecant function";
    USAGE[ID.Count] = "count the number of occurrences of a pattern";
    USAGE[ID.Covariance] = "covariance matrix for a pair of datasets";
    USAGE[ID.CubeRoot] = "real-valued cube root";
    USAGE[ID.Curl] = "get vector curl";

    USAGE[ID.D] = "partial derivative of a function";
    USAGE[ID.Decrement] =
        "decreases the value by one and assigns that returning the original value";
    USAGE[ID.DedekindNumber] = "known Dedekind numbers [0..9]";
    USAGE[ID.Default] = "predefined default arguments for a function";
    USAGE[ID.Definition] = "give values of a symbol in a form that can be stored in a package";
    USAGE[ID.Degree] = "1 Degree are Pi/180 radians";
    USAGE[ID.Delete] = "delete elements from a list at given positions";
    USAGE[ID.DeleteCases] = "delete all occurrences of a pattern";
    USAGE[ID.DeleteDuplicates] = "delete duplicate elements in a list";
    USAGE[ID.Denominator] = "denominator of an expression";
    USAGE[ID.Depth] = "get maximum number of indices to specify any part";
    USAGE[ID.Derivative] = "symbolic and numerical derivative functions";
    USAGE[ID.DesignMatrix] = "design matrix for a linear model";
    USAGE[ID.Det] = "determinant of a matrix";
    USAGE[ID.Diagonal] = "gives a list with the diagonal elements of a given matrix";
    USAGE[ID.DiagonalMatrix] = "give a diagonal matrix with the elements of a given list";
    USAGE[ID.DiceDissimilarity] = "Dice dissimilarity";
    USAGE[ID.DigitCharacter] = "digit 0-9";
    USAGE[ID.DigitQ] = "test whether all the characters are digits";
    USAGE[ID.Dimensions] = "the dimensions of a tensor";
    USAGE[ID.DirectedInfinity] = "infinite quantity with a defined direction in the complex plane";
    USAGE[ID.DiscretePlot] = "discrete plot of a one-parameter function";
    USAGE[ID.DisjointQ] = "test whether two lists do not have common elements";
    USAGE[ID.Divide] = "divide";
    USAGE[ID.DivideBy] = "divide a value and assigns that returning the new value";
    USAGE[ID.Divisible] = "test whether one number is divisible by the other";
    USAGE[ID.Divisors] = "integer divisors";
    USAGE[ID.Do] = "evaluate an expression looping over a variable";
    USAGE[ID.Dot] = "dot product of vectors and matrices";
    USAGE[ID.DownValues] =
        "get transformation rules corresponding to all downvalues defined for a symbol";
    USAGE[ID.Drop] = "remove a number of elements from a list";
    USAGE[ID.DSolve] = "differential equation analytical solver";

    USAGE[ID.E] = "exponential constant E≃2.7182";
    USAGE[ID.Echo] = "files and pipes that echoes the input";
    USAGE[ID.EditDistance] = "edit distance";
    USAGE[ID.Eigensystem] = "eigenvalues and corresponding eigenvectors of a matrix";
    USAGE[ID.Eigenvalues] = "eigenvalues of a matrix";
    USAGE[ID.Eigenvectors] = "list of matrix eigenvectors";
    USAGE[ID.Element] = "check whether the element belongs to the domain";
    USAGE[ID.EllipticE] = "elliptic integral of the second kind E(ϕ|m)";
    USAGE[ID.EllipticF] = "elliptic integral F(ϕ|m)";
    USAGE[ID.EllipticK] = "elliptic integral of the first kind K(m)";
    USAGE[ID.EllipticPi] = "elliptic integral of the third kind P(n|m)";
    USAGE[ID.End] = "revert to the context previous to the nearest `Begin` statement";
    USAGE[ID.EndOfLine] = "a string pattern matching EOL";
    USAGE[ID.EndPackage] =
        "restore the context and the context path to the state before the nearest call to `BeginPackage`";
    USAGE[ID.Equal] = "numerical equality";
    USAGE[ID.Equivalent] = "logic equivalence";
    USAGE[ID.Erf] = "error function";
    USAGE[ID.Erfc] = "complementary error function";
    USAGE[ID.EulerE] = "Euler numbers";
    USAGE[ID.EulerGamma] = "Euler's constant γ≃0.5772";
    USAGE[ID.EulerPhi] = "Euler totient function";
    USAGE[ID.Evaluate] =
        "forces evaluation of $expr$, even if it occurs inside a held argument or a 'Hold' form";
    USAGE[ID.EvenQ] = "test whether elements are even numbers";
    USAGE[ID.ExactNumberQ] = "test if an expression is an exact real or complex number";
    USAGE[ID.Except] = "match to expressions that do not match with a pattern";
    USAGE[ID.Exp] = "exponential function";
    USAGE[ID.Expand] = "expand out products and powers";
    USAGE[ID.ExpandAll] = "expand products and powers, including negative integer powers";
    USAGE[ID.ExpandDenominator] = "expand products and powers in the denominator";
    USAGE[ID.ExpandNumerator] = "expand products and powers in the numerator";
    USAGE[ID.ExpIntegralE] = "exponential integral function of order n";
    USAGE[ID.ExpIntegralEi] = "exponential integral function";
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
    USAGE[ID.Fibonacci] = "Fibonacci's numbers";
    USAGE[ID.FileFormat] = "determine the file format of a file";
    USAGE[ID.FilterRules] = "select rules such that the pattern matches some other given patterns";
    USAGE[ID.FindMaximum] = "local maximum optimization";
    USAGE[ID.FindMinimum] = "local minimum optimization";
    USAGE[ID.FindRoot] = "looks for a root of an equation or a zero of a numerical expression";
    USAGE[ID.FindClusters] = "divide data into lists of similar elements";
    USAGE[ID.First] = "first element of a list or expression";
    USAGE[ID.FirstCase] = "first element that matches a pattern";
    USAGE[ID.FirstPosition] = "position of the first element matching a pattern";
    USAGE[ID.FittedModel] = "fitted model generated by LinearModelFit";
    USAGE[ID.FixedPoint] = "nest until a fixed point is reached returning the last expression";
    USAGE[ID.FixedPointList] = "nest until a fixed point is reached return a list";
    USAGE[ID.Flat] = "attribute for functions that must be flattened";
    USAGE[ID.Flatten] = "flatten out any sequence of levels in a nested list";
    USAGE[ID.FlattenAt] = "flatten out sequences at particular positions";
    USAGE[ID.Fold] = "iterative application of a binary operation over elements of a list";
    USAGE[ID.FoldList] =
        "list of the results of applying a binary operation interatively over elements of a list";
    USAGE[ID.For] = "a 'For' loop";
    USAGE[ID.FractionalPart] = "fractional part of a number";
    USAGE[ID.FreeQ] = "test whether an expression is free of subexpressions matching a pattern";
    USAGE[ID.FresnelC] = "Fresnel's integral C";
    USAGE[ID.FresnelC] = "Fresnel's integral S";

    USAGE[ID.FractionalPart] = "fractional part of a number";
    USAGE[ID.FromCharacterCode] = "convert from a list of character codes to a string";
    USAGE[ID.FromContinuedFraction] =
        "reconstructs a number from its continued fraction representation";
    USAGE[ID.FromRomanNumeral] = "convert roman numeral strings to integers";
    USAGE[ID.FullForm] = "get the underlying M-Expression representation";
    USAGE[ID.FullSimplify] = "apply a full set of simplification transformations";

    USAGE[ID.Gamma] = "complete and incomplete gamma functions";
    USAGE[ID.Gather] = "gather sublists of identical elements";
    USAGE[ID.GatherBy] = "gather based on values of a function applied to elements";
    USAGE[ID.GCD] = "greatest common divisor";
    USAGE[ID.GegenbauerC] = "Gegenbauer's polynomials";
    USAGE[ID.General] = "general-purpose messages";
    USAGE[ID.Glaisher] = "Glaisher's constant A≃1.282";
    USAGE[ID.Greater] = "greater than";
    USAGE[ID.GreaterEqual] = "greater than or equal to";
    USAGE[ID.Gudermannian] = "Gudermannian function gd(z)";

    USAGE[ID.HammingDistance] = "Hamming distance";
    USAGE[ID.Haversine] = "haversine function";
    USAGE[ID.HermiteH] = "Hermite's polynomials";
    USAGE[ID.Hold] = "prevents $expr$ from being evaluated";
    USAGE[ID.HoldAll] = "attribute specifying that all the arguments should be left unevaluated";
    USAGE[ID.HoldAllComplete] =
        "attribute specifying that all the arguments should be left unevaluated, even if includes sequences, or upvalues";
    USAGE[ID.HoldComplete] =
        "prevents $expr$ from being evaluated, and also prevents 'sequence' objects from being spliced into argument lists";
    USAGE[ID.HoldFirst] = "attribute specifying that the first argument should be left unevaluated";
    USAGE[ID.HoldForm] = "is equivalent to 'Hold[$expr$]', but prints as $expr$";
    USAGE[ID.HoldPattern] = "took the expression as a literal pattern";
    USAGE[ID.HoldRest] =
        "attribute specifying that all but the first argument should be left unevaluated";
    USAGE[ID.Hypergeometric1F1] = "compute Kummer confluent hypergeometric function";
    USAGE[ID.Hypergeometric2F1] = "compute Gauss hypergeometric function function";
    USAGE[ID.HypergeometricPFQ] = "compute the generalized hypergeometric function";
    USAGE[ID.HypergeometricU] = "Tricomi confluent hypergeometric function";

    USAGE[ID.I] = "represents the imaginary number 'Sqrt(-1)'";
    USAGE[ID.If] = "test if a condition is true, false, or of unknown truth value";
    USAGE[ID.IdentityMatrix] = "give the identity matrix with a given dimension";
    USAGE[ID.Im] = "imaginary part of a complex number";
    USAGE[ID.Implies] = "logic implication";
    USAGE[ID.Import] = "import elements from a file";
    USAGE[ID.ImportString] = "import elements from a string";
    USAGE[ID.Increment] =
        "increases the value by one and assigns that returning the original value";
    USAGE[ID.Indeterminate] = "indeterminate value";
    USAGE[ID.Inequality] = "chain of inequalities";
    USAGE[ID.InexactNumberQ] = "test if an expression is an not exact real or complex number";
    USAGE[ID.Infinity] = "infinite real quantity";
    USAGE[ID.Infix] = "infix form";
    USAGE[ID.Information] = "get information about all assignments for a symbol";
    USAGE[ID.Inner] = "generalized inner product";
    USAGE[ID.Input] = "the name of the current input stream";
    USAGE[ID.InputForm] = "plain-text input format";
    USAGE[ID.Insert] = "insert an element at a given position";
    USAGE[ID.Integer] = "head for integer numbers";
    USAGE[ID.IntegerPart] = "integer part of a number";
    USAGE[ID.IntegerPartitions] = "list integer partitions";
    USAGE[ID.IntegerQ] = "test whether an expression is an integer";
    USAGE[ID.Integers] = "the domain of Integer numbers";
    USAGE[ID.Integrate] = "indefinite or definite integral of a function";
    USAGE[ID.Interrupt] = "interrupt evaluation and return '$Aborted'";
    USAGE[ID.Intersection] = "enumerate common elements";
    USAGE[ID.IntersectingQ] = "test whether two lists have common elements";
    USAGE[ID.Inverse] = "inverse matrix";
    USAGE[ID.InverseErf] = "inverse of the error function";
    USAGE[ID.InverseErfc] = "inverse of the complementary error function";
    USAGE[ID.InverseHaversine] = "inverse Haversine function";
    USAGE[ID.InverseGudermannian] = "Gudermannian function gd^-1(z)";

    USAGE[ID.JaccardDissimilarity] = "Jaccard dissimilarity";
    // USAGE[ID.JacobiP] = "Jacobi's polynomials";
    USAGE[ID.JacobiSymbol] = "Jacobi symbol";
    USAGE[ID.JavaForm] = "translate expressions as Java source code";
    USAGE[ID.Join] = "join lists together at any level";

    USAGE[ID.Key] = "indicate a key within a part specification";
    USAGE[ID.Keys] = "list association keys";
    USAGE[ID.Khinchin] = "Khinchin's constant K≃2.6854";
    USAGE[ID.KnownUnitQ] = "tests whether its argument is a canonical unit.";
    USAGE[ID.KroneckerProduct] = "Kronecker product";
    USAGE[ID.KroneckerSymbol] = "Kronecker symbol";
    USAGE[ID.Kurtosis] = "kurtosis coefficient";

    USAGE[ID.LaguerreL] = "Laguerre's polynomials";
    // USAGE[ID.LaguerreP] = "Legendre's polynomials of first kind";
    USAGE[ID.Last] = "last element of a list or expression";
    USAGE[ID.LCM] = "least common multiple";
    USAGE[ID.LeafCount] = "get the total number of atomic subexpressions";
    USAGE[ID.LeastSquares] = "least square solver for linear problems";
    USAGE[ID.LegendreP] = "Legendre's polynomials of first kind";
    USAGE[ID.LegendreQ] = "Legendre's polynomials of second kind";
    USAGE[ID.Length] = "number of elements in a list or expression";
    USAGE[ID.Less] = "less than";
    USAGE[ID.LessEqual] = "less than or equal to";
    USAGE[ID.LetterNumber] = "position of a letter in an alphabet";
    USAGE[ID.LetterQ] = "test whether all the characters are letters";
    USAGE[ID.Level] = "get parts specified by a given number of indices";
    USAGE[ID.LevelQ] = "test whether is a valid level specification";
    USAGE[ID.LeviCivitaTensor] = "give the Levi-Civita tensor with a given dimension";
    USAGE[ID.Limit] = "directed and undirected limits";
    USAGE[ID.LinearModelFit] = "fit a linear model to a dataset";
    USAGE[ID.LinearSolve] = "solves linear systems in matrix form";
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
    USAGE[ID.LucasL] = "Lucas number";

    USAGE[ID.MachineNumberQ] = "test if expression is a machine precision real or complex number";
    USAGE[ID.MantissaExponent] = "decomposes numbers as mantissa and exponent";
    USAGE[ID.Map] = "map a function over a list, at specified levels";
    USAGE[ID.MapAt] = "map a function at particular positions";
    USAGE[ID.MapApply] = "apply a function to a list, at the top level";
    USAGE[ID.MapIndexed] = "map a function, including index information";
    USAGE[ID.MapThread] = "map a function across corresponding elements in multiple lists";
    USAGE[ID.MatchingDissimilarity] = "simple matching dissimilarity";
    USAGE[ID.MathMLForm] = "format expression as MathML commands";
    USAGE[ID.MatrixExp] = "matrix exponentiation";
    USAGE[ID.MatrixForm] = "format as a matrix";
    USAGE[ID.MatrixPower] = "power of a matrix";
    USAGE[ID.MatrixQ] = "gives 'True' if the given argument is a list of equal-length lists";
    USAGE[ID.MatrixRank] = "rank of a matrix";
    USAGE[ID.Max] = "the smallest argument or the largest element of a list";
    USAGE[ID.Maximize] = "compute the maximum of a function";
    USAGE[ID.Mean] = "returns the statistical mean of a list";
    USAGE[ID.Median] = "central value of a dataset";
    USAGE[ID.MeijerG] = "compute the Meijer G-function";
    USAGE[ID.MemberQ] = "test whether an element is a member of a list";
    USAGE[ID.MersennePrimeExponent] = "known Mersenne prime exponents [1..47]";
    USAGE[ID.MersennePrimeExponentQ] = "True if 2^n - 1 is a prime number";
    USAGE[ID.Message] = "display a message";
    USAGE[ID.Messages] = "get the messages associated with a particular symbol";
    USAGE[ID.MessageName] = "message identifier";
    USAGE[ID.Min] = "the largest argument or the largest element of a list";
    USAGE[ID.Minimize] = "compute the minimum of a function";
    USAGE[ID.Minus] = "arithmetic negate";
    USAGE[ID.MinimalPolynomial] = "minimal polynomial for a general algebraic number";
    USAGE[ID.Missing] = "default value if a key is not found";
    USAGE[ID.Mod] = "the remainder in an integer division";
    USAGE[ID.Module] =
        "generates symbols with names of the form x$nnn to represent each local variable.";
    USAGE[ID.ModularInverse] = "returns the modular inverse k^(-1) MOD n";
    USAGE[ID.MoebiusMu] = "Mobius function";
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
    USAGE[ID.NonNegative] = "test whether an expression is a non-negative number";
    USAGE[ID.NonPositive] = "test whether an expression is a non-positive number";
    USAGE[ID.Nor] = "negation of logic (inclusive) disjunction";
    USAGE[ID.Norm] = "get norm of a vector or matrix";
    USAGE[ID.Normal] = "convert objects to normal expressions";
    USAGE[ID.Normalize] = "normalizes a vector";
    USAGE[ID.Not] = "logic negation";
    USAGE[ID.NotListQ] = "test if an expression is not a list";
    USAGE[ID.Null] = "implicit result for expressions that do not yield a result";
    USAGE[ID.NullSpace] = "generator for the null space of a matrix";
    // USAGE[ID.NumberDigits] = "digits of a real number";
    USAGE[ID.NumberLinePlot] = "plot along a number line";
    USAGE[ID.NumberQ] = "test whether an expression is a number";
    USAGE[ID.NumberString] = "characters in string representation of a number";
    USAGE[ID.NumericQ] = "test whether an expression is a number";
    USAGE[ID.Numerator] = "numerator of an expression";
    USAGE[ID.NumericFunction] = "attribute that indicates that a symbol is a numerical function";


    USAGE[ID.O] = "symbolic representation of a higher-order series term";
    USAGE[ID.OddQ] = "test whether elements are odd numbers";
    // USAGE[ID.Off] = "turn off a message for printing";
    // USAGE[ID.On] = "turn on a message for printing";
    USAGE[ID.OneIdentity] =
        "attribute specifying that a function behaves like the Identity in pattern matching";
    USAGE[ID.Operate] = "apply a function to the head of an expression";
    USAGE[ID.Options] = "the list of optional arguments and their default values";
    USAGE[ID.OptionsPattern] = "a sequence of optional named arguments";
    USAGE[ID.OptionValue] = "retrieve values of options while executing a function";
    USAGE[ID.Or] = "logic (inclusive) disjunction";
    USAGE[ID.Order] = "order expressions";
    USAGE[ID.OrderedQ] = "test whether elements are canonically sorted";
    USAGE[ID.Orderless] =
        "attribute for functions with results that does not depends on the order of their arguments";
    USAGE[ID.OutputForm] = "plain-text output format";
    USAGE[ID.Outer] = "generalized outer product";
    USAGE[ID.Overflow] = "overflow in numeric evaluation";

    USAGE[ID.PadLeft] = "pad out by the left a ragged array to make a matrix";
    USAGE[ID.PadRight] = "pad out by the right a ragged array to make a matrix";
    USAGE[ID.Part] = "get/set any part of an expression";
    USAGE[ID.ParametricPlot] = "2D parametric curves or regions";
    USAGE[ID.Partition] = "partition a list into sublists of a given length";
    USAGE[ID.PartitionsP] = "number of unrestricted partitions";
    USAGE[ID.Pattern] = "a named pattern";
    USAGE[ID.PatternTest] = "match to a pattern conditioned to a test result";
    USAGE[ID.PauliMatrix] = "Pauli spin matrix";
    USAGE[ID.Permutations] = "form permutations of a list";
    USAGE[ID.Pi] = "Pi, \u03c0≃3.1416";
    USAGE[ID.Pick] = "pick out elements according to a boolean mask";
    USAGE[ID.Piecewise] = "an arbitrary piecewise function";
    USAGE[ID.Plot] = "curves of one or more functions";
    USAGE[ID.Plus] = "add";
    USAGE[ID.Pochhammer] = "Pochhammer's symbols";
    USAGE[ID.PolarPlot] = "draw a polar plot";
    USAGE[ID.PolyGamma] = "polygamma function";
    USAGE[ID.PolyLog] = "polylogarithm function";
    USAGE[ID.PolygonalNumber] = "polygonal number";
    USAGE[ID.PolynomialQ] = "test if the expression is a polynomial in a variable";
    USAGE[ID.PossibleZeroQ] = "test whether an expression is estimated to be zero";
    USAGE[ID.Position] = "positions of matching elements";
    USAGE[ID.Positive] = "test whether an expression is a positive number";
    USAGE[ID.Postfix] = "postfix form";
    USAGE[ID.Power] = "exponentiate";
    USAGE[ID.PowerExpand] = "expand out powers";
    USAGE[ID.PowerMod] = "modular powers and roots";
    USAGE[ID.PowersRepresentations] = "represent a number as a sum of powers";
    USAGE[ID.PreDecrement] = "decrease the value by one and assigns that returning the new value";
    USAGE[ID.Prefix] = "prefix form";
    USAGE[ID.PreIncrement] = "increase the value by one and assigns that returning the new value";
    USAGE[ID.Prepend] = "add an element at the beginning";
    USAGE[ID.PrependTo] = "add an element at the beginning of an stored list or expression";
    USAGE[ID.Prime] = "n-th prime number";
    USAGE[ID.Primes] = "the domain of the prime numbers";
    USAGE[ID.PrimePi] = "amount of prime numbers less than or equal";
    USAGE[ID.PrimePowerQ] = "test if a number is a power of a prime number";
    USAGE[ID.PrimeQ] = "test whether elements are prime numbers";
    USAGE[ID.Print] = "print strings and formatted text";
    USAGE[ID.Product] = "discrete product";
    USAGE[ID.ProductLog] = "Lambert's W function";
    USAGE[ID.Projection] = "find the projection of one vector on another";
    USAGE[ID.Protect] = "protect a symbol against redefinitions";
    USAGE[ID.Protected] = "attribute of protected symbols";
    USAGE[ID.PseudoInverse] = "pseudo inverse of a matrix";

    USAGE[ID.Quantile] =
        "cut points dividing the range of a probability distribution into continuous intervals";
    USAGE[ID.Quantity] = "represents a quantity with units";
    USAGE[ID.QuantityMagnitude] = "get magnitude associated with a quantity";
    USAGE[ID.QuantityQ] = "tests whether the argument is a quantity";
    USAGE[ID.QuantityUnit] = "the unit associated to a quantity";
    USAGE[ID.Quartiles] = "list of quartiles";
    USAGE[ID.Quiet] = "evaluate without showing messages";
    USAGE[ID.Quit] = "terminates the Symja session...";
    USAGE[ID.Quotient] = "integer quotient";
    USAGE[ID.QuotientRemainder] = "integer quotient and remainder";
    USAGE[ID.QRDecomposition] = "QR decomposition of a matrix";

    USAGE[ID.RamseyNumber] = "known Ramsey numbers";
    USAGE[ID.Random] = "pick a random number (deprecated function)";
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
    USAGE[ID.Re] = "real part of a complex number";
    USAGE[ID.ReadProtected] = "attribute of symbols with hidden definitions";
    USAGE[ID.RealAbs] = "real absolute value";
    USAGE[ID.RealDigits] = "digits of a real number";
    USAGE[ID.Reals] = "the domain of the Real numbers";
    USAGE[ID.RealSign] = "real sign";
    USAGE[ID.Reap] = "create lists of elements \"sown\" inside programs";
    USAGE[ID.RemoveDiacritics] = "remove diacritics from a string";
    USAGE[ID.Real] = "head for real numbers";
    USAGE[ID.RealAbs] = "real absolute value";
    USAGE[ID.RealSign] = "real sign";
    USAGE[ID.ReleaseHold] =
        "removes any 'Hold', 'HoldForm', 'HoldPattern' or 'HoldComplete' head from $expr$";
    USAGE[ID.RealValuedNumberQ] =
        "returns 'True' if $expr$ is an explicit number with no imaginary component";
    USAGE[ID.RegularExpression] = "string to regular expression";
    USAGE[ID.ReplaceAt] = "replace an expression at particular positions";
    USAGE[ID.ReplacePart] = "replace elements at given positions";
    USAGE[ID.Rest] = "remove the first element";
    USAGE[ID.Return] = "return from a function";
    USAGE[ID.Reverse] = "reverse a list at any level";
    USAGE[ID.ReverseSort] = "sort in reverse order";
    USAGE[ID.Riffle] = "intersperse additional elements";
    USAGE[ID.RogersTanimotoDissimilarity] = "Rogers-Tanimoto dissimilarity";
    USAGE[ID.RomanNumeral] = "convert integers to roman numeral strings";
    USAGE[ID.Root] = "the i-th root of a polynomial";
    USAGE[ID.RotateLeft] = "cyclically rotate lists to the left, at any depth";
    USAGE[ID.RotateRight] = "cyclically rotate lists to the right, at any depth";
    USAGE[ID.RotationTransform] = "symbolic representation of a rotation in 3D";
    USAGE[ID.Round] = "find closest integer or multiple of";
    USAGE[ID.RowReduce] = "matrix reduced row-echelon form";
    USAGE[ID.RussellRaoDissimilarity] = "Russell-Rao dissimilarity";

    USAGE[ID.SASTriangle] = "triangle from side, angle, side";
    USAGE[ID.SameQ] = "literal symbolic identity";
    USAGE[ID.ScalingTransform] = "symbolic representation of a scale transformation";
    USAGE[ID.Scan] = "scan over every element of a list, applying a function";
    USAGE[ID.Sec] = "secant function";
    USAGE[ID.Sech] = "hyperbolic secant function";
    USAGE[ID.Select] = "pick elements according to a criterion";
    USAGE[ID.Sequence] = "represents a sequence of arguments to a function";
    USAGE[ID.SequenceHold] =
        "attribute that prevents 'Sequence' objects from being spliced into a function's arguments.";
    USAGE[ID.Series] = "power series and asymptotic expansions";
    USAGE[ID.SeriesCoefficient] = "power series coefficient";
    USAGE[ID.SeriesData] = "power series of a variable about a point";
    USAGE[ID.SetAttributes] = "set attributes for a symbol";
    USAGE[ID.Set] = "assign a value";
    USAGE[ID.SetDelayed] = "assign a delayed value";
    USAGE[ID.ShearingTransform] = "symbolic representation of a shearing transformation";
    USAGE[ID.Sign] = "return -1, 0, or 1 depending on whether $x$ is negative, zero, or positive";
    USAGE[ID.Simplify] = "apply transformations to simplify an expression";
    USAGE[ID.Sin] = "sine function";
    USAGE[ID.SingularValueDecomposition] = "singular value decomposition of a matrix";
    USAGE[ID.Sinh] = "hyperbolic sine function";
    USAGE[ID.SixJSymbol] = "values of the Wigner 6-j symbol";
    USAGE[ID.Skewness] = "skewness coefficient";
    USAGE[ID.Slot] = "`#` serves as a pure function's first parameter ";
    USAGE[ID.SokalSneathDissimilarity] = "Sokal-Sneath dissimilarity";
    USAGE[ID.Solve] = "find generic solutions for variables";
    USAGE[ID.Sort] = "sort the elements of an expression";
    USAGE[ID.SortBy] = "sort by the values of a function applied to elements";
    USAGE[ID.Sow] = "send an expression to the innermost Reap";
    USAGE[ID.Span] = "general specification for spans or blocks of elements";
    USAGE[ID.SparseArray] = "an array by the values of the non-zero elements";
    USAGE[ID.SparseArrayQ] = "test whether an expression is a SparseArray";
    USAGE[ID.SphericalHarmonicY] = "3D Spherical Harmonic";
    USAGE[ID.Split] = "split into runs of identical elements";
    USAGE[ID.SplitBy] = "split based on values of a function applied to elements";
    USAGE[ID.Sqrt] = "square root";
    USAGE[ID.SSSTriangle] = "triangle from 3 sides";
    USAGE[ID.StandardDeviation] = "standard deviation of a dataset";
    USAGE[ID.StandardForm] = "default output format";
    USAGE[ID.StieltjesGamma] = "Stieltjes' function";
    USAGE[ID.String] = "head for strings";
    USAGE[ID.StringCases] = "occurrences of string patterns in a string";
    USAGE[ID.StringContainsQ] = "test whether a pattern matches with a substring";
    USAGE[ID.StringDrop] = "drop a part of a string";
    USAGE[ID.StringExpression] = "an arbitrary string expression";
    USAGE[ID.StringForm] = "make a string from a template and a list of parameters";
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
    USAGE[ID.Subfactorial] = "subfactorial";
    USAGE[ID.Subscript] = "format an expression with a subscript";
    USAGE[ID.SubsetQ] = "test if a list is a subset of another list";
    USAGE[ID.Subsets] = "list all the subsets";
    USAGE[ID.Subsuperscript] = "format an expression with a subscript and a superscript";
    USAGE[ID.Subtract] = "subtract";
    USAGE[ID.SubtractFrom] = "subtract a value and assins that returning the new value";
    USAGE[ID.Sum] = "discrete sum";
    USAGE[ID.Superscript] = "format an expression with a superscript";
    USAGE[ID.Switch] = "switch based on a value, with patterns allowed";
    USAGE[ID.Symbol] = "the head of a symbol; create a symbol from a name";
    USAGE[ID.SymbolName] = "give the name of a symbol as a string";
    // USAGE[ID.SymjaForml] = "translate expressions to Symja";
    USAGE[ID.SyntaxQ] = "test whether a string is a syntactically-correct expression";
    // USAGE[ID.Syntax] = "syntax messages";

    USAGE[ID.Table] = "make a table of values of an expression";
    USAGE[ID.TableForm] = "format as a table";
    USAGE[ID.TagSet] =
        "assign a value to an expression, associating the corresponding assignment with the a symbol";
    USAGE[ID.TagSetDelayed] =
        "assign a delayed value to an expression, associating the corresponding assignment with the a symbol";
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
    USAGE[ID.Thread] = "thread a function across lists that appear in its arguments";
    USAGE[ID.ThreeJSymbol] = "values of the Wigner 3-j symbol";
    USAGE[ID.Through] = "distribute operators that appears inside the head of expressions";
    USAGE[ID.Throw] = "throw an expression to be caught by a surrounding 'Catch'";
    USAGE[ID.Times] = "multiply";
    USAGE[ID.TimesBy] = "multiply a value and assigns that returning the new value";
    USAGE[ID.ToCharacterCode] = "convert a string to a list of character codes";
    USAGE[ID.ToExpression] = "build an expression from formatted text";
    USAGE[ID.ToLowerCase] = "turn all the letters into lower case";
    USAGE[ID.Together] = "put over a common denominator";
    USAGE[ID.ToString] = "format an expression and produce a string";
    USAGE[ID.ToUpperCase] = "turn all the letters into upper case";
    USAGE[ID.Total] = "adds all values in $list$...";
    USAGE[ID.Tr] = "trace of a matrix";
    USAGE[ID.Trace] = "trace the succesive evaluations";
    USAGE[ID.TransformationFunction] = "general symbolic representation of transformation";
    USAGE[ID.TranslationTransform] = "symbolic representation of translation";
    USAGE[ID.Transliterate] = "transliterate an UTF string in different alphabets to ASCII";
    USAGE[ID.Transpose] = "transpose to rearrange indices in any way";
    USAGE[ID.True] = "boolean constant for True";
    USAGE[ID.TrueQ] = "returns 'true' if and only if $expr$ is 'true'";
    USAGE[ID.Tuples] = "form n-tuples from a list";

    USAGE[ID.UpperCaseQ] = "test wether all the characters are upper-case letters";
    USAGE[ID.Undefined] = "undefined value";
    USAGE[ID.Underflow] = "underflow in numeric evaluation";
    USAGE[ID.Unequal] = "numerical inequality";
    USAGE[ID.Unevaluated] =
        "temporarily leaves $expr$ in an unevaluated form when it appears as a function argument";
    USAGE[ID.Union] = "enumerate all distinct elements in a list";
    USAGE[ID.Unique] = "generate a new symbols with a unique name";
    USAGE[ID.UnitConvert] = "convert between units";
    USAGE[ID.UnitStep] = "unit step function of a number";
    USAGE[ID.UnitVector] = "unit vector along a coordinate direction";
    USAGE[ID.Unprotect] = "remove protection against redefinitions";
    USAGE[ID.UnsameQ] = "not literal symbolic identity";
    USAGE[ID.Unset] = "unset a value of the LHS";
    USAGE[ID.UpSet] = "set value and associate the assignment with symbols that occur at level one";
    USAGE[ID.UpSetDelayed] =
        "set a delayed value and associate the assignment with symbols that occur at level one";
    USAGE[ID.UpTo] = "a certain number of elements, or as many as are available";
    USAGE[ID.UpValues] = "get transformation rules corresponding to upvalues defined for a symbol";

    USAGE[ID.ValueQ] = "test whether a symbol can be considered to have a value";
    USAGE[ID.Values] = "list association values";
    USAGE[ID.Variables] = "list of variables in a polynomial";
    USAGE[ID.Variance] = "variance of a dataset";
    USAGE[ID.VectorQ] = "test whether an object is a vector";
    USAGE[ID.VectorGreater] = "test whether vector 1 is greater than vector2";
    USAGE[ID.VectorGreaterEqual] = "test whether vector 1 is greater equal than vector2";
    USAGE[ID.VectorLess] = "test whether vector 1 is less than vector2";
    USAGE[ID.VectorLessEqual] = "test whether vector 1 is less equal than vector2";

    USAGE[ID.Which] = "test which of a sequence of conditions are true";
    USAGE[ID.While] = "evaluate an expression while a criterion is true";
    USAGE[ID.Whitespace] = "sequence of whitespace characters";
    USAGE[ID.WhitespaceCharacter] = "space, newline, tab, or other whitespace character";
    USAGE[ID.WordBoundary] = "boundary between word characters and others";
    USAGE[ID.WignerD] = "Wigner D-function";
    USAGE[ID.With] = "replace variables by some constant values";
    USAGE[ID.WordCharacter] = "letter or digit";

    USAGE[ID.Xor] = "logic (exclusive) disjunction";

    USAGE[ID.YuleDissimilarity] = "Yule dissimilarity";
    USAGE[ID.Zeta] = "Riemann's ζ function";
  }

  public static String summaryText(String symbolStr) {
    Integer ordinalId = ID.STRING_TO_ID_MAP.get(symbolStr);
    if (ordinalId != null) {
      String str = USAGE[ordinalId];
      return str != null ? str : "";
    }
    return "";
  }

  /**
   * Return a short summary of the built-in symbols functionality.
   * 
   */
  public static String summaryText(IBuiltInSymbol symbol) {
    // if (!IS_INITIALIZED.get()) {
    // init();
    // }
    String str = USAGE[symbol.ordinal()];
    return str != null ? str : "";
  }
}
