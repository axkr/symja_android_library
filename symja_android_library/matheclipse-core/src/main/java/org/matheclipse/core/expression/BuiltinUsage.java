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
    USAGE[ID.$RecursionLimit] =
        "specifies the maximum allowable recursion depth after which a calculation is terminated";
    USAGE[ID.Abort] = "generate an abort";
    USAGE[ID.Abs] = "absolute value of a number";
    USAGE[ID.Accumulate] = "accumulates the values of $list$, returning a new list";
    USAGE[ID.All] = "all the parts in the level";
    USAGE[ID.Alphabet] = "lowercase letters in an alphabet";
    // USAGE[ID.AnglePath] = "form a path from a sequence of \"turtle-like\" turns and motions";
    USAGE[ID.AngleVector] = "create a vector at a specified angle";
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
    USAGE[ID.Attributes] = "returns the attributes of $symbol$...";
    USAGE[ID.Append] = "add an element at the end of an expression";
    USAGE[ID.AppendTo] = "add an element at the end of an stored list or expression";
    USAGE[ID.Association] = "an association between keys and values";
    USAGE[ID.AssociationQ] = "test if an expression is a valid association";
    USAGE[ID.Assuming] = "set assumptions during the evaluation";
    USAGE[ID.Assumptions] = "assumptions used to simplify expressions";
    USAGE[ID.BaseForm] = "print with all numbers given in a base";
    USAGE[ID.Boole] = "translate 'True' to 1, and 'False' to 0";
    USAGE[ID.BooleanQ] = "returns 'True' if $expr$ is either 'True' or 'False'";
    USAGE[ID.Break] = "exit a 'For', 'While', or 'Do' loop";
    USAGE[ID.ByteArray] = "array of bytes";
    USAGE[ID.Catch] = "handle an exception raised by a 'Throw'";
    USAGE[ID.Catenate] = "catenate elements from a list of lists";
    USAGE[ID.Catenate] = "central moments of distributions and data";
    USAGE[ID.CentralMoment] = "expression defined under condition";
    USAGE[ID.Characters] = "list the characters in a string";
    USAGE[ID.CharacterRange] = "range of characters with successive character codes";
    USAGE[ID.Check] = "discard the result if the evaluation produced messages";
    USAGE[ID.ClearAttributes] = "removes $attrib$ from $symbol$'s attributes";
    USAGE[ID.Conjugate] = "complex conjugation";
    USAGE[ID.Constant] = "attribute that indicates that a symbol is a (numerical) constant";
    USAGE[ID.ConstantArray] = "form a constant array";
    USAGE[ID.Continue] = "continue with the next iteration in a 'For', 'While' or 'Do' loop";
    USAGE[ID.ContainsOnly] = "test if all the elements of a list appear into another list";
    USAGE[ID.Complement] = "find the complement with respect to a universal set";
    USAGE[ID.Complex] = "head for complex numbers";
    USAGE[ID.CompoundExpression] = "execute expressions in sequence";
    USAGE[ID.Conjugate] = "complex conjugate value";
    USAGE[ID.Context] = "give the name of the context of a symbol";
    USAGE[ID.Correlation] = "Pearson's correlation of a pair of datasets";
    USAGE[ID.Cos] = "cosine function";
    USAGE[ID.Cosh] = "hyperbolic cosine function";
    USAGE[ID.Cot] = "cotangent function";
    USAGE[ID.Coth] = "hyperbolic cotangent function";
    USAGE[ID.Csc] = "cosecant function";
    USAGE[ID.Csch] = "hyperbolic cosecant function";
    USAGE[ID.Count] = "count the number of occurrences of a pattern";
    USAGE[ID.Covariance] = "covariance matrix for a pair of datasets";
    USAGE[ID.Delete] = "delete elements from a list at given positions";
    USAGE[ID.DeleteCases] = "delete all occurrences of a pattern";
    USAGE[ID.DeleteDuplicates] = "delete duplicate elements in a list";
    USAGE[ID.DigitCharacter] = "digit 0-9";
    USAGE[ID.DigitQ] = "test whether all the characters are digits";
    USAGE[ID.DirectedInfinity] = "infinite quantity with a defined direction in the complex plane";
    USAGE[ID.DisjointQ] = "test whether two lists do not have common elements";
    USAGE[ID.Do] = "evaluate an expression looping over a variable";
    USAGE[ID.Drop] = "remove a number of elements from a list";
    USAGE[ID.Definition] = "give values of a symbol in a form that can be stored in a package";
    USAGE[ID.EndOfLine] = "a string pattern matching EOL";
    USAGE[ID.Equal] = "numerical equality";
    USAGE[ID.Evaluate] =
        "forces evaluation of $expr$, even if it occurs inside a held argument or a 'Hold' form";
    USAGE[ID.Exp] = "exponential function";
    USAGE[ID.Extract] = "extract elements that appear at a list of positions";
    // USAGE[ID.Failure] = "a failure at the level of the interpreter";
    USAGE[ID.FindClusters] = "divide data into lists of similar elements";
    USAGE[ID.First] = "first element of a list or expression";
    USAGE[ID.FirstCase] = "first element that matches a pattern";
    USAGE[ID.FirstPosition] = "position of the first element matching a pattern";
    USAGE[ID.FixedPoint] = "nest until a fixed point is reached returning the last expression";
    USAGE[ID.FixedPointList] = "nest until a fixed point is reached return a list";
    USAGE[ID.Flat] = "attribute for functions that must be flattened";
    USAGE[ID.Fold] = "iterative application of a binary operation over elements of a list";
    USAGE[ID.FoldList] =
        "list of the results of applying a binary operation interatively over elements of a list";
    USAGE[ID.For] = "a 'For' loop";
    USAGE[ID.FromCharacterCode] = "convert from a list of character codes to a string";
    USAGE[ID.FullForm] = "get the underlying M-Expression representation";
    USAGE[ID.Gather] = "gather sublists of identical elements";
    USAGE[ID.GatherBy] = "gather based on values of a function applied to elements";
    USAGE[ID.General] = "general-purpose messages";
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
    USAGE[ID.InverseHaversine] = "inverse haversine function";
    USAGE[ID.I] = "represents the imaginary number 'sqrt[-1]'";
    USAGE[ID.If] = "test if a condition is true, false, or of unknown truth value";
    USAGE[ID.Im] = "returns the imaginary component of the complex number $z$";
    USAGE[ID.Infix] = "infix form";
    USAGE[ID.Information] = "get information about all assignments for a symbol";
    USAGE[ID.InputForm] = "plain-text input format";
    USAGE[ID.Insert] = "insert an element at a given position";
    USAGE[ID.Integer] = "head for integer numbers";
    USAGE[ID.Interrupt] = "interrupt evaluation and return '$Aborted'";
    USAGE[ID.Intersection] = "enumerate common elements";
    USAGE[ID.IntersectingQ] = "test whether two lists have common elements";
    USAGE[ID.JavaForm] = "translate expressions as Java source code";
    USAGE[ID.Join] = "join lists together at any level";
    USAGE[ID.Key] = "indicate a key within a part specification";
    USAGE[ID.Keys] = "list association keys";
    USAGE[ID.Kurtosis] = "kurtosis coefficient";
    USAGE[ID.Last] = "last element of a list or expression";
    USAGE[ID.LeafCount] = "the total number of atomic subexpressions";
    USAGE[ID.Length] = "number of elements in a list or expression";
    USAGE[ID.LetterNumber] = "position of a letter in an alphabet";
    USAGE[ID.LetterQ] = "test whether all the characters are letters";
    USAGE[ID.Level] = "get subexpressions at a level specification";
    USAGE[ID.LevelQ] = "test whether is a valid level specification";
    USAGE[ID.List] = "specify a list explicitly";
    USAGE[ID.Listable] = "Attribute for functions that automatically thread over their arguments";
    USAGE[ID.ListQ] = "test if an expression is a list";
    USAGE[ID.Log] = "natural logarithm function";
    USAGE[ID.Log2] = "base-2 logarithm function";
    USAGE[ID.Log10] = "base-10 logarithm function";
    USAGE[ID.LogisticSigmoid] = "logistic function";
    USAGE[ID.Lookup] =
        "perform lookup of a value by key, returning a specified default if it is not found";
    USAGE[ID.LowerCaseQ] = "test wether all the characters are lower-case letters";
    USAGE[ID.MathMLForm] = "format expression as MathML commands";
    USAGE[ID.MatrixForm] = "format as a matrix";
    USAGE[ID.Max] = "The smallest argument or the largest element of a list";
    USAGE[ID.Mean] = "returns the statistical mean of $list$";
    USAGE[ID.Median] = "central value of a dataset";
    USAGE[ID.MemberQ] = "test whether an element is a member of a list";
    USAGE[ID.Message] = "display a message";
    USAGE[ID.MessageName] = "message identifyier";
    USAGE[ID.Min] = "The largest argument or the largest element of a list";
    USAGE[ID.Missing] = "default value if a key is not found";
    USAGE[ID.Most] = "remove the last element";
    USAGE[ID.Names] = "find a list of symbols with names matching a pattern";
    USAGE[ID.Nearest] = "the nearest element from a list";
    USAGE[ID.Negative] = "returns 'True' if $x$ is a negative real number";
    USAGE[ID.Nest] = "give the result of nesting a function";
    USAGE[ID.NestList] = "successively nest a function and append the results to a list";
    USAGE[ID.NestWhile] = "nest while a condition is satisfied returning the last expression";
    USAGE[ID.NHoldAll] =
        "attribute that indicates that the arguments must not be evaluated in numerical evaluations";
    USAGE[ID.NHoldFirst] =
        "attribute that indicates that the first argument must not be evaluated in numerical evaluations";
    USAGE[ID.NHoldRest] =
        "attribute that indicates that just the first  argument must be evaluated in numerical evaluations";
    USAGE[ID.Normal] = "convert objects to normal expressions";
    USAGE[ID.NotListQ] = "test if an expression is not a list";
    USAGE[ID.NumberQ] = "test whether an expression is a number";
    USAGE[ID.NumberString] = "characters in string representation of a number";
    USAGE[ID.NumericFunction] = "attribute that indicates that a symbol is a numerical function";
    USAGE[ID.NonNegative] = "returns 'True' if $x$ is a positive real number or zero";
    USAGE[ID.NonPositive] = "returns 'True' if $x$ is a negative real number or zero";
    // USAGE[ID.Off] = "turn off a message for printing";
    // USAGE[ID.On] = "turn on a message for printing";
    USAGE[ID.OneIdentity] =
        "attribute specifying that a function behaves like the Identity in pattern matching";
    USAGE[ID.Orderless] =
        "attribute for functions with results that does not depends on the order of their arguments";
    USAGE[ID.OutputForm] = "plain-text output format";
    USAGE[ID.PadLeft] = "pad out by the left a ragged array to make a matrix";
    USAGE[ID.PadRight] = "pad out by the right a ragged array to make a matrix";
    USAGE[ID.Part] = "get/set any part of an expression";
    USAGE[ID.Partition] = "partition a list into sublists of a given length";
    USAGE[ID.Permutations] = "form permutations of a list";
    USAGE[ID.Pick] = "pick out elements according to a boolean mask";
    USAGE[ID.Piecewise] = "an arbitrary piecewise function";
    USAGE[ID.PossibleZeroQ] =
        "returns 'True' if basic symbolic and numerical methods suggest that expr has value zero, and 'False' otherwise";
    USAGE[ID.Position] = "positions of matching elements";
    USAGE[ID.Positive] = "returns 'True' if $x$ is a positive real number";
    USAGE[ID.Postfix] = "postfix form";
    USAGE[ID.Prefix] = "prefix form";
    USAGE[ID.Prepend] = "add an element at the beginning";
    USAGE[ID.PrependTo] = "add an element at the beginning of an stored list or expression";
    USAGE[ID.Print] = "print strings and formatted text";
    USAGE[ID.Product] = "discrete product";
    USAGE[ID.Quantile] =
        "cut points dividing the range of a probability distribution into continuous intervals";
    USAGE[ID.Quartiles] = "list of quartiles";
    USAGE[ID.Quiet] = "evaluate without showing messages";
    USAGE[ID.Quit] = "terminates the Symja session...";
    USAGE[ID.RandomInteger] = "pick an integer number at random from a range";
    USAGE[ID.RandomChoice] = "choice items at random from a list";
    USAGE[ID.RandomComplex] = "pick a complex number at random from a rectangular region";
    USAGE[ID.RandomReal] = "pick a real number at random from an interval";
    USAGE[ID.RandomSample] = "pick a sample at random from a list";
    USAGE[ID.Range] = "generate a list of equispaced, consecutive numbers";
    USAGE[ID.RankedMax] = "the n-th largest item";
    USAGE[ID.RankedMin] = "the n-th smallest item";
    USAGE[ID.Rational] = "head for rational numbers";
    USAGE[ID.Re] = "returns the real component of the complex number $z$";
    USAGE[ID.Reap] = "create lists of elements \"sown\" inside programs";
    USAGE[ID.RemoveDiacritics] = "remove diacritics from a string";
    USAGE[ID.Real] = "head for real numbers";
    USAGE[ID.ReleaseHold] =
        "removes any 'Hold', 'HoldForm', 'HoldPattern' or 'HoldComplete' head from $expr$";
    USAGE[ID.RealNumberQ] =
        "returns 'True' if $expr$ is an explicit number with no imaginary component";
    USAGE[ID.RegularExpression] = "string to regular expression";
    USAGE[ID.ReplacePart] = "replace elements at given positions";
    USAGE[ID.Rest] = "remove the first element";
    USAGE[ID.Return] = "return from a function";
    USAGE[ID.Reverse] = "reverse a list at any level";
    USAGE[ID.Riffle] = "intersperse additional elements";
    USAGE[ID.RotateLeft] = "cyclically rotate lists to the left, at any depth";
    USAGE[ID.RotateRight] = "cyclically rotate lists to the right, at any depth";
    USAGE[ID.SameQ] = "literal symbolic identity";
    USAGE[ID.Sec] = "secant function";
    USAGE[ID.Sech] = "hyperbolic secant function";
    USAGE[ID.Select] = "pick elements according to a criterion";
    USAGE[ID.Sequence] = "represents a sequence of arguments to a function";
    USAGE[ID.SequenceHold] =
        "attribute that prevents 'Sequence' objects from being spliced into a function's arguments.";
    USAGE[ID.SetAttributes] = "adds $attrib$ to the list of $symbol$'s attributes";
    USAGE[ID.Sign] = "return -1, 0, or 1 depending on whether $x$ is negative, zero, or positive";
    USAGE[ID.Sin] = "sine function";
    USAGE[ID.Sinh] = "hyperbolic sine function";
    USAGE[ID.Skewness] = "skewness coefficient";
    USAGE[ID.Sow] = "send an expression to the innermost Reap";
    USAGE[ID.Span] = "general specification for spans or blocks of elements";
    USAGE[ID.Split] = "split into runs of identical elements";
    USAGE[ID.SplitBy] = "split based on values of a function applied to elements";
    USAGE[ID.StandardDeviation] = "standard deviation of a dataset";
    USAGE[ID.StandardForm] = "default output format";
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
    USAGE[ID.Subsuperscript] = "format an expression with a subscript and a superscript";
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
    USAGE[ID.TeXForm] = "formatted expression as TeX commands";
    USAGE[ID.Throw] = "throw an expression to be caught by a surrounding 'Catch'";
    USAGE[ID.ToCharacterCode] = "convert a string to a list of character codes";
    USAGE[ID.ToExpression] = "build an expression from formatted text";
    USAGE[ID.ToLowerCase] = "turn all the letters into lower case";
    USAGE[ID.ToString] = "format an expression and produce a string";
    USAGE[ID.ToUpperCase] = "turn all the letters into upper case";
    USAGE[ID.Total] = "adds all values in $list$...";
    USAGE[ID.Transliterate] = "transliterate an UTF string in different alphabets to ASCII";
    USAGE[ID.TrueQ] = "returns 'true' if and only if $expr$ is 'true'";
    USAGE[ID.Tuples] = "form n-tuples from a list";
    USAGE[ID.UpperCaseQ] = "test wether all the characters are upper-case letters";
    USAGE[ID.Unequal] = "numerical inequality";
    USAGE[ID.Unevaluated] =
        "temporarily leaves $expr$ in an unevaluated form when it appears as a function argument";
    USAGE[ID.Union] = "enumerate all distinct elements in a list";
    USAGE[ID.UnitVector] = "unit vector along a coordinate direction";
    USAGE[ID.UnsameQ] = "not literal symbolic identity";
    USAGE[ID.UpTo] = "a certain number of elements, or as many as are available";
    USAGE[ID.ValueQ] = "test whether a symbol can be considered to have a value";
    USAGE[ID.Values] = "list association values";
    USAGE[ID.Variance] = "variance of a dataset";
    USAGE[ID.Which] = "test which of a sequence of conditions are true";
    USAGE[ID.While] = "evaluate an expression while a criterion is true";
    USAGE[ID.Whitespace] = "sequence of whitespace characters";
    USAGE[ID.WhitespaceCharacter] = "space, newline, tab, or other whitespace character";
    USAGE[ID.WordBoundary] = "boundary between word characters and others";
    USAGE[ID.WordCharacter] = "letter or digit";
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
