package org.matheclipse.core.eval;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.ApfloatInterruptedException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.parser.client.math.MathException;
import edu.jas.kern.PreemptingException;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.cache.PebbleCache;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.PrintNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.RootNode;
import io.pebbletemplates.pebble.node.TextNode;
import io.pebbletemplates.pebble.node.expression.ContextVariableExpression;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

public class Errors {
  private static final Logger LOGGER = LogManager.getLogger();

  private static PebbleEngine PEBBLE_ENGINE = new PebbleEngine.Builder().build();

  private final static Errors ERRORS_INSTANCE = new Errors();

  private Errors() {}

  public static void initGeneralMessages() {
    for (int i = 0; i < Errors.MESSAGES.length; i += 2) {
      S.General.putMessage(IPatternMatcher.SET, Errors.MESSAGES[i],
          F.stringx(Errors.MESSAGES[i + 1]));
    }
  }

  /**
   * Contains pairwise an error or warning messages shortcut and the corresponding message template
   * printed for that shortcut.
   */
  private static final String[] MESSAGES = { //
      "accg", "Value of option `1` is not Automatic or a machine-sized integer.", //
      "affind", "`1` should be a list of `2` or more affinely independent points.", //
      "argillegal", "Illegal arguments: \"`1`\" in `2`", //
      "argb", "`1` called with `2` arguments; between `3` and `4` arguments are expected.", //
      "argct", "`1` called with `2` arguments. `3`", //
      "argctu", "`1` called with 1 argument.", //
      "argm", "`1` called with `2` arguments; `3` or more arguments are expected.", //
      "argr", "`1` called with 1 argument; `2` arguments are expected.", //
      "argrx", "`1` called with `2` arguments; `3` arguments are expected.", //
      "argx", "`1` called with `2` arguments; 1 argument is expected.", //
      "args", "`1` called with invalid parameters.", //
      "argt", "`1` called with `2` arguments; `3` or `4` arguments are expected.", //
      "argtu", "`1` called with 1 argument; `2` or `3` arguments are expected.", //
      "argtype",
      "Arguments `1` and `2` of `3` should be either non-negative integers or one-character strings.", //
      "arg2", "Cannot divide sides of an equation or inequality by 0.", //
      "asm", "The sum of angles `1` and `2` should be less than `3`.", //
      "attnf", "`1` is not a known attribute.", //
      "base", "Requested base `1` in `2` should be between 2 and `3`.", //
      "bbrac",
      "`1` is only applicable for univariate real functions and requires two real starting values that bracket the root.", //
      "bdmtrc", "`1` does not define a metric in `2` dimensions.", //
      "bdpt", "Evaluation point `1` is not a valid set of polar or hyperspherical coordinates.", //
      "bset",
      "The second argument `1` of Element should be one of: Primes, Integers, Rationals, Algebraics, Reals, Complexes or Booleans.", //
      "bfun", "`1` is not a boolean-valued pure function.", //
      "bldim", "The arguments `1` and `2` do not have compatible dimensions.", //
      "boxfmt", "`1` is not a box formatting type.", //
      "bdomv", "Warning: `1` is not a valid domain specification.", //
      "cfn", "Numerical error encountered, proceeding with uncompiled evaluation.", //
      "coef", "The first argument `1` of `2` should be a non-empty list of positive integers.", //
      "color", "`1` is not a valid color or gray-level specification.", //
      "compat", "`1` and `2` are incompatible units", //
      "condp", "Pattern `1` appears on the right-hand-side of condition `2`.", //
      "crs",
      "Warning: the column element `1` and row element `2` at positions `3` and `4` are not the same. Using column element.", //
      "cxt", "`1` is not a valid context name.", //
      "ctnc", "The constraint `1` is not convex.", //
      "cvmit", "Failed to converge to the requested accuracy or precision within `1` iterations.", //
      "depth",
      "The array depth of the expression at position `1` of `2` must be at least equal to the specified rank `3`.", //
      "divz", "The argument `1` should be nonzero.", //
      "digit", "Digit at position `1` in `2` is too large to be used in base `3`.", //
      "dmval",
      "Input value `1` lies outside the range of data in the interpolating function. Extrapolation will be used.",
      "dotdim",
      "Dot contraction of `1` and `2` is invalid because dimensions `3` and `4` are incompatible.",
      "dotsh", "Tensors `1` and `2` have incompatible shapes.", //
      "dpvar", "The variable `1` has been specified more than once.", //
      "drop", "Cannot drop positions `1` through `2` in `3`.", //
      "dsdelim", "The delimiter specification is not valid.", //
      "dstlms",
      "The requested number of elements `1` is greater than the number of distinct elements `2`. Only `2` elements will be returned.", //
      "dup", "Duplicate local variable `1` found in local variable specification `2`.", //
      "dvar",
      "Multiple derivative specifier `1` does not have the form {variable, n} where n is a symbolic expression or a non-negative integer.", //
      "empt", "Argument `1` should be a non-empty list.", //
      "eqf", "`1` is not a well-formed equation.", //
      "eqin", "`1` should be an equation or inequality.", //
      "eqineq",
      "Constraints in `1` are not all equality or inequality constraints. Constraints with Unequal(!=) are not supported.", //
      "eqgele",
      "Constraints in `1` are not all 'equality' or 'less equal' or 'greater equal' linear constraints. Constraints with Unequal(!=) are not supported.", //
      "error", "`1`", // without terminating '.'
      "exact", "Argument `1` is not an exact number.", //
      "exdims", "The dimensions cannot be determined from the position `1`.", //
      "experimental", "Experimental implementation (search in Github issues for identifier `1`).",
      "fdup", "Duplicate parameter `1` found in `2`.", //
      "fdguess", "Form of start specification `1` supports only one start value for a variable.",
      "fdss", "Search specification `1` should be a list with 1 to 3 elements.", //
      "flpar", "Parameter specification `1` in `2` should be a symbol or a list of symbols.", //
      "fftl", "Argument `1` is not a non-empty list or rectangular array of numeric quantities.", //
      "fttype", "The transform type `1` should be 1, 2, 3 or 4.", //
      "fpct", "To many parameters in `1` to be filled from `2`.", //
      "fnsym", "First argument in `1` is not a symbol or a string naming a symbol.", //
      "hdiv", "`1` does not exist. Arguments  are not consistent.", //
      "heads", "Heads `1` and `2` are expected to be the same.", //
      "heads2", "Heads `1` and `2` at positions `3` and `4` are expected to be the same.", //
      "herm", "The matrix `1` is not hermitian or real and symmetric.", //
      "ibase", "Base `1` is not an integer greater than `2`.", //
      "idim", "`1` and `2` must have the same length.", //
      "idir", "Direction vector `1` has zero magnitude.", //
      "idiv", "Integral of `1` does not converge on `2`.", //
      "ifun", "Inverse functions are being used. Values may be lost for multivalued inverses.", //
      "ilsmn",
      "Single or list of non-negative machine-sized integers expected at position `1` of `2`.", //
      "ilsnn", "Single or list of non-negative integers expected at position `1`.", //
      "incmp", "The arguments `1` and `2` in `3` are incompatible.", //
      "incom",
      "Length `1` of dimension `2` in `3` is incommensurate with length `4` of dimension `5` in `6`.", //
      "ilim", "Invalid integration variable or limit(s) in `1`.", //
      "incomp", "Expressions `1` and `2` have incompatible shapes.", //
      "incompCF", "Warning: `1` terminated before `2` terms.", //
      "incpt", "incompatible elements in `1` cannot be joined.", //
      "indet", "Indeterminate expression `1` encountered.", //
      "infy", "Infinite expression `1` encountered.", //
      "innf", "Non-negative integer or Infinity expected at position `1` in `2`.", //
      "ins", "Cannot insert at position `1` in `2`.", //
      "insuff", "Cannot take `1` elements from a list of length `2`.", //
      "int", "Integer expected at position `2` in `1`.", //
      "intjava", "Java int value greater equal `1` expected instead of `2`.", //
      "intlevel", "Level specification value greater equal `1` expected instead of `2`.", //
      "intnn", "Non-negative integer expected.", //
      "intnm", "Non-negative machine-sized integer expected at position `2` in `1`.", //
      "intm", "Machine-sized integer expected at position `2` in `1`.", //
      "intp", "Positive integer expected at position `2` in `1`.", //
      "intpm", "Positive machine-sized integer expected at position `2` in `1`.", //
      "intpoint", "`1` is expected to contain a list of lists of integers.", //
      "intpp", "Positive integer argument expected in `1`.", //
      "intrange", "Integer expected in range `1` to `2`.", //
      "intype", "Interpreter type specification `1` is invalid.", //
      "inv", "The argument `2`  in  `1`  is not a valid parameter.", //
      "invcpts",
      "`1` should be a rectangular array of machine-sized real numbers of any depth, whose dimensions are greater than 1.", //
      "inv02", "The argument `2`  in  `1`  is not valid. 0 or 2 arguments expected.", //
      "invak", "The argument is not a rule or a list of rules.", //
      "invdt", "The argument `1` is not a valid Association.", //
      "invdt2", "The argument `1` is not a rule or a list of rules.", //
      "invidx2", "Index `1` should be a machine sized integer between `2` and `3`.", //
      "invrl", "The argument `1` is not a valid Association or a list of rules.", //
      "iopnf", "Value of option `1` should be a non-negative integer or Infinity.", //
      "ipnf", "Positive integer or Infinity expected at position `1` in `2`.", //
      "iterb", "Iterator does not have appropriate bounds.", //
      "itform", "Argument `1` at position `2` does not have the correct form for an iterator.", //
      "itlim", "Iteration limit of `1` exceeded for `2`.", //
      "itlimpartial", "Iteration limit of `1` exceeded. Returning partial results.", //
      "itendless", "Endless iteration detected in `1` in evaluation loop.", //
      "intnz", "Nonzero integer expected at position `1` in `2`.", //
      "itraw", "Raw object `1` cannot be used as an iterator.", //
      "ivar", "`1` is not a valid variable.", //
      "ldata", "`1` is not a valid dataset or a list of datasets.", //
      "ldir",
      "Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided or a list of these.", //
      "lend",
      "The argument at position `1` in `2` should be a vector of unsigned byte values or a Base64 encoded string.", //
      "length", "The vectors `1` and `2` have different lengths.", //
      "level", "Level specification `1` is not of the form n, {n}, or {m, n}.", //
      "levelpad",
      "The padding specification `1` involves `2` levels, the list `3` has only `4` level.", //
      "lim", "Limit specification `1` is not of the form x->x0.", //
      "limset",
      "Cannot set $RecursionLimit to `1`; value must be Infinity or an integer at least 20.", //
      "linobj",
      "The objective function `1` is not a numeric valued linear function of the variables `2`.", //
      "list", "List expected at position `1` in `2`.", //
      "list1", "The argument `1` is not a valid list of Associations or rules or list of rules.", //
      "listofbigints", "List of Java BigInteger numbers expected in `1`.", //
      "listofints", "List of Java int numbers expected in `1`.", //
      "listoflongs", "List of Java long numbers expected in `1`.", //
      "listrp", "List or SparseArray or structured array expected at position `1` in `2`.", //
      "locked", "Symbol `1` is locked.", //
      "lowlen", "Required length `1` is smaller than maximum `2` of support of `3`.", //
      "lpn", "`1` is not a list of numbers or pairs of numbers.", //
      "lpsnf", "No solution can be found that satisfies the constraints.", //
      "lrgexp", "Exponent ist out of bounds for function `1`.", //
      "lslc", "Coefficient matrix and target vector or matrix do not have the same dimensions.", //
      "lstpat", "List or pattern matching a list expected at position `1` in `2`.", //
      "lvlist", "Local variable specification `1` is not a List.", //
      "lvws", "Variable `1` in local variable specification `2` requires assigning a value", //
      "lvset",
      "Local variable specification `1` contains `2`, which is an assignment to `3`; only assignments to symbols are allowed.", //
      "lvsym",
      "Local variable specification `1` contains `2` which is not a symbol or an assignment to a symbol.", //
      "matrix", "Argument `1` at position `2` is not a non-empty rectangular matrix.", //
      "matsq", "Argument `1` at position `2` is not a non-empty square matrix.", //
      "maxrts", "The value `1` of the `2` options is not a positive integer, Infinity or Automatic", //
      "memlimit",
      "This computation has exceeded the memeory limit settings of this evaluation engines instance.", //
      "meprec", "Internal precision limit `1` reached while evaluating `2`.", //
      "mindet", "Input matrix contains an indeterminate entry.", //
      "minv", "The `1` arguments to `2` must be ordinary integers.", // or gaussian
      "mptd", "Object `1` at position {2,`2`} in `3` has only `4` of required `5` dimensions.", //
      "mseqs",
      "Sequence specification or a list of sequence specifications expected at position `1` in `2`.", //
      "nalph", "The alphabet `1` is not known or not available.", //
      "nas", "The argument `1` is not a string.", //
      "natt", "The `1` is not attained at any point satisfying the constraints.", //
      "needsjdk",
      "For compiling functions, Symja needs to be executed on a Java Development Kit with javax.tools.JavaCompiler installed.", //
      "nconvss",
      "The argument `1` cannot be converted to a NumericArray of type `2` using method `3`", //
      "ncvi", "NIntegrate failed to converge after `1` refinements in `2` in the region `3`.", //
      "ndimv", "There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.", //
      "nliter", "Non-list iterator `1` at position `2` does not evaluate to a real numeric value.", //
      "nlnmt2",
      "The first argument is not a number or a vector, or the second argument is not a norm function that always returns a non-negative real number for any numeric argument.", // ",
      "nil", "unexpected NIL expression encountered.", //
      "ninv", "`1` is not invertible modulo `2`.", //
      "nmet", "Unable to find the domain with the available methods.", //
      "nmtx", "The first two levels of `1` cannot be transposed.", //
      "nnumeq",
      "`1` is expected to be a polynomial equation in the variable `2` with numeric coefficients.", //
      "nocatch", "Uncaught `1` returned to top level.", //
      "nofirst", "`1` has zero length and no first element.", //
      "nofwd", "No enclosing For, While or Do found for `1`.", //
      "noneg", "Argument `1` should be a real non-negative number.", //
      "nonegs", "Surd is not defined for even roots of negative values.", //
      "nolast", "`1` has zero length and no last element.", //
      "nomost", "Cannot take Most of expression `1` with length zero.", //
      "nonn1",
      "The arguments are expected to be vectors of equal length, and the number of arguments is expected to be 1 less than their length.", //
      "nonopt",
      "Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule or a list of rules.", //
      "noopen", "Cannot open `1`.", //
      "noprime", "There are no primes in the specified interval.", //
      "nord", "Invalid comparison with `1` attempted.", //
      "norel", "Expressions `1` and `2` cannot be related by a permutation.", //
      "norep", "Assignment on `2` for `1` not found.", //
      "normal", "Nonatomic expression expected at position `1` in `2`.", //
      "nostr", "`1` is not a string.", //
      "notdata", "The first argument is not a vector or matrix.", //
      "notent",
      "`1` is not a known entity, class or tag for GraphData. Use GraphData for a list of entities.", //
      "notunicode",
      "A character unicode, which should be a non-negative integer less than 1114112, is expected at position `2` in `1`.", //
      "noval", "Symbol `1` in part assignment does not have an immediate value.", //
      "npa", "The angle `1` should be a positive number less than `2`.", //
      "nps", "The triangle side `1`should be a positive number.", //
      "nquan",
      "The Quantile specification `1` should be a number or a list of numbers between `2` and `3`.",
      "nrnum", "The Function value `1` is not a real number at `2`=`3`.", "nsmet",
      "The system cannot be solved with the methods available to `1`.", //
      "nsolc", "There are no points which satisfy the constraints.", //
      "nupr", "`1` is not a univariate polynomial with rational number coefficients.", //
      "nvld", "The expression `1` is not a valid interval.", //
      "nvm", "The first Norm argument should be a scalar, vector or matrix.", //
      "nwargs",
      "Argument `2` in `1` is not of the form i, {i,j}, {i,Infinity}, or All, where i and j are non-negative machine-sized integers.", //
      "openx", "`1` is not open.", //
      "optb", "Optional object `1` in `2` is not a single blank.", //
      "optnf", "Option name `2` not found in defaults for `1`.", //
      "opttf", "Value of option `1` -> `2` should be True or False.", //
      "opttfa", "Value of option `1` -> `2` should be True, False or Automatic.", //
      "optx", "Unknown option `1` in `2`.", //
      "ovfl", "Overflow occurred in computation.", //
      "ovls", "Value of option `1` must be True, False or All.", //
      "padlevel",
      "The padding specification `1` involves `2` levels; the list `3` has only `4` level.", //
      "pair", "Argument `1` is expected to be a pair, a list of pairs or an Interval object.", //
      "pairs", "The first argument `1` of `2` is not a list of pairs.", //
      "par", "Inappropriate parameter: `1`.", //
      "partd", "Part specification `1` is longer than depth of object.", //
      "partw", "Part `1` of `2` does not exist.", //
      "patop", "Pattern `1` contains inappropriate optional object.", //
      "patvar", "First element in `1` is not a valid pattern name.", //
      "perm", "`1` is not a valid permutation.", //
      "perm2", "Entry `1` in `2` is out of bounds for a permutation of length `3`.", //
      "permlist", "Invalid permutation list `1`.", //
      "phy", "`1` is not physical.", //
      "pilist",
      "The arguments to `1` must be two lists of integers of identical length, with the second list only containing positive integers.", //
      "pint", "The value `1` in position `2` must be a non-negative machine sized integer.", //
      "plen", "`1` and `2` should have the same length.", //
      "plld", "Endpoints in `1` must be distinct machine-size real numbers.", //
      "pllim", "Range specification `1` is not of the form {x, xmin, xmax}.", //
      "plln", "Limiting value `1` in `2` is not a machine-size real number.", //
      "pkspec1", "The expression `1` cannot be used as a part specification.", //
      "poly", "`1` is not a polynomial.", //
      "polynomial", "Polynomial expected at position `1` in `2`.", //
      "posdim",
      "The dimension parameter `1` is expected to be a positive integer or a list of positive integers.", //
      "pospoint", "`1` contains integers that are not positive.", //
      "posprm", "Parameter `1` at position `2` in `3` is expected to be positive.", //
      "posr", "The left hand side of `2` in `1` doesn't match an int-array of depth `3`.", //
      "post", "The threshold `1` should be positive.", //
      "ppnt", "The value `1` of argument `2`  must be a positive integer.", //
      "preal", "The parameter `1` should be real-valued.", //
      "precsm", "Requested precision `1` is smaller than `2`.", //
      "precgt", "Requested precision `1` is greater than `2`.", //
      "prng",
      "Value of option `1` is not All, Full, Automatic, a positive machine number, or an appropriate list of range specifications.",
      "psl",
      "Position specification `1` in `2` is not a machine sized integer or a list of machine-sized integers.", //
      "psl1", "Position specification `1` in `2` is not applicable.", //
      "pspec", "Part specification `1` is neither an integer nor a list of integer.", //
      "pts", "`1` should be a non-empty list of points.", //
      "ptype",
      "The second argument of Norm, `1`, should be a symbol, Infinity, or a number greater equal 1 for p-norms, or \"Frobenius\" for matrix norms.",
      "range", "Range specification in `1` does not have appropriate bounds.", //
      "rbase", "Base `1` is not a real number greater than 1.", //
      "rank", "The rank `1` is not an integer between `2` and  `3`.", //
      "rankl", "The list `1` of dimensions must have length `2`.", "rctndm1",
      "The argument `1` at position `2` should be a rectangular array of real numbers with length greater than the dimension of the array or two such arrays with of equal dimension.",
      "realx", "The value `1` is not a real number.", //
      "reclim2", "Recursion depth of `1` exceeded during evaluation of `2`.", //
      "rect", "Nonrectangular tensor encountered", //
      "rectt", "Rectangular array expected at position `1` in `2`.", //
      "reppoint", "`1` contains repeated integers.", //
      "reps",
      "(`1`) is neither a list of replacement rules nor a valid dispatch table and cannot be used for replacing.", //
      "root", "Unable to determine the appropriate root for the periodic continued fraction.", //
      "rrlim", "Exiting after `1` scanned `2` times.", //
      "rvalue", "`1` is not a variable with a value, so its value cannot be changed.", //
      "rvec", "Input `1` is not a vector of reals or integers.", //
      "rvec2", "Input `1` is not a real-valued vector.", //
      "rubiendless",
      "Endless iteration detected in `1` (rule number `2`) for Rubi pattern-matching rules.", //
      "sclr", "The scalar expression `1` does not have a `2`.", //
      "sdmint",
      "The number of subdivisions given in position `1` of `2` should be a positive machine-sized integer.", //
      "seq", //
      "Position `1` of `2` must be All, None an integer, or a list of 1,2 or 3 integers, with the third (if present) nonzero.", //
      "seqs", //
      "Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in `1`.", //
      "seqso", "Sequence specification (+n,-n,{+n},{-n} or {m,n}) expected at position `2` in `1`.", //
      "setp", "Part assignment to `1` could not be made", //
      "setraw", "Cannot assign to raw object `1`.", //
      "setps", "`1` in the part assignment is not a symbol.", //
      "sfr", "Item `1` requested in `2` out of range. `3` itms available.", //
      "shapespec", "Shape specification `1` is invalid.", //
      "shlen", "The argument `1` should have at least `2` arguments.", //
      "sing", "Matrix `1` is singular.", //
      "sing1", "The matrix `1` is singular; a factorization will not be saved.", //
      "span", "`1` is not a valid Span specification.", //
      "ssdn", "Attempt to evaluate a series at the number `1`. Returning Indeterminate.", //
      "ssle", "Symbol, string or HoldPattern(symbol) expected at position `2` in `1`.", //
      "step", "The step size `1` is expected to be positive", //
      "stream", "`1` is not string, InputStream[], or OutputStream[].", //
      "string", "String expected at position `1` in `2`.", //
      "strse", "String or list of strings expected at position `1` in `2`.", //
      "sym", "Argument `1` at position `2` is expected to be a symbol.", //
      "tag", "Rule for `1` can only be attached to `2`.", //
      "tagnf", "Tag `1` not found in `2`.", //
      "take", "Cannot take positions `1` through `2` in `3`.", //
      "takeeigen", "Cannot take eigenvalues `1` through `2` out of the total of `3` eigenvalues.", //
      "targ", "Argument `1` at position `2` is not List or SparseArray.", //
      "tbnval",
      "Values `1` produced by the function `2` cannot be used for numerical sorting because they are not all real.", //
      "tdlen", "Objects of unequal length in `1` cannot be combined.", //
      "tllen", "Lists of unequal length in `1` cannot be added.", //
      "toggle", "ToggleFeature `1` is disabled.", //
      "tolnn", "Tolerance specification `1` must be a non-negative number.", //
      "tri", "`1` is not triangular.", //
      "udist", "The specification `1` is not a random distribution recognized by the system.", //
      "unkunit", "Unable to interpret unit specification `1`.", //
      "unsdst", "The first argument `1` is not a valid distribution.", //
      "unsupported", "`1` currently not supported in `2`.", //
      "usraw", "Cannot unset object `1`.", //
      "vctnln",
      "The argument `1` at position `2` should be a vector of real numbers with length greater than `3`.", //
      "vctnln3",
      "The argument `1` at position `2` should be a vector of real numbers with length equal to the vector given at position `3`.", //
      "vloc",
      "The variable `1` cannot be localized so that it can be assigned to numerical values.", //
      "vector", "Argument `1` at position `2` is not a non-empty vector.", //
      "vpow2", "Argument `1` is restricted to vectors with a length of power of 2.", //
      "vrule", "Cannot set `1` to `2`, which is not a valid list of replacement rules.", //
      "write", "Tag `1` in `2` is Protected.", //
      "wrsym", "Symbol `1` is Protected.", //
      "ucdec", "An invalid unicode sequence was encountered and ignored.", //
      // Symja special
      "zzdivzero", "Division by zero `1`.", //
      "zzmaxast", "Maximum AST limit `1` exceeded.", //
      "zznotimpl", "Function `1` not implemented.", //
      "zzonlyimpl", "Function `1` only implemented for `2` list arguments.", //
      "zzprime", "Maximum Prime limit `1` exceeded.", //
      "zzregex", "Regex expression `1` error message: `2`.", //
      "zzapfloatcld", "Complete loss of accurate digits (apfloat).", //
      "zzdsex", "Non deserialized expression `1`." //
  };

  public static IExpr message(ISymbol symbol, String messageShortcut, final IAST list,
      int maximumLength) {
    IExpr temp = symbol.evalMessage(messageShortcut);
    String message = null;
    if (temp.isPresent()) {
      message = temp.toString();
    } else {
      temp = S.General.evalMessage(messageShortcut);
      if (temp.isPresent()) {
        message = temp.toString();
      }
    }
    if (message != null) {
      message = rawMessage(list, message, maximumLength);
      return F.stringx(symbol.toString() + ": " + message);
    }
    return F.NIL;
  }

  /**
   * argr, argx, argrx, argt messages
   *
   * <p>
   * <b>Example:</b> &quot;`1` called with 1 argument; `2` arguments are expected.&quot;
   *
   * @param ast
   * @param expected
   * @param engine
   * @return
   */
  public static IAST printArgMessage(IAST ast, int[] expected, EvalEngine engine) {
    IExpr head = ast.head();
    final ISymbol topHead = ast.topHead();
    int argSize = ast.argSize();
    if (expected[0] == expected[1]) {
      if (expected[0] == 1) {
        return printMessage(topHead, "argx", F.list(head, F.ZZ(argSize), F.ZZ(expected[0])),
            engine);
      }
      if (argSize == 1) {
        return printMessage(topHead, "argr", F.list(head, F.ZZ(expected[0])), engine);
      }
      return printMessage(topHead, "argrx", F.list(head, F.ZZ(argSize), F.ZZ(expected[0])), engine);
    }
    if (expected[1] == Integer.MAX_VALUE) {
      return printMessage(topHead, "argm", F.list(head, F.ZZ(argSize), F.ZZ(expected[0])), engine);
    }
    return printMessage(topHead, "argt",
        F.List(head, F.ZZ(argSize), F.ZZ(expected[0]), F.ZZ(expected[1])), engine);
  }

  /**
   * Print the Symja {@link MathException#getMessage()} into the error log
   * 
   * @param symbol
   * @param mex
   * @param engine
   * @return
   */
  public static IExpr printMessage(ISymbol symbol, final MathException mex, EvalEngine engine) {
    if (Config.SHOW_STACKTRACE) {
      mex.printStackTrace();
    }
    return printMessage(symbol, "error", F.List(mex.getMessage()), engine);
  }

  /**
   * Print the exception into the default error log.
   * 
   * @param symbol
   * @param mex
   * @param engine
   * @return
   */
  public static IAST printMessage(ISymbol symbol, final Throwable exception) {
    return printMessage(symbol, exception, EvalEngine.get());
  }

  /**
   * Print the exception into the default error log.
   * 
   * @param symbol
   * @param mex
   * @param engine
   * @return
   */
  public static IAST printMessage(ISymbol symbol, final Throwable exception, EvalEngine engine) {
    if (Config.SHOW_STACKTRACE) {
      exception.printStackTrace();
    }
    String message = exception.getMessage();
    if (message == null) {
      return printMessage(symbol, "error", F.List(exception.toString()), engine);
    }
    return printMessage(symbol, "error", F.List(message), engine);
  }

  /**
   * Print message <code>experimental</code> -
   * <code>Experimental implementation (search in Github issues for identifier `1`).</code>
   *
   * @param symbol
   * @return
   */
  public static IAST printExperimental(IBuiltInSymbol symbol) {
    EvalEngine engine = EvalEngine.get();
    if (!engine.containsExperimental(symbol)) {
      printMessage(symbol, "experimental", F.list(symbol), EvalEngine.get());
      engine.incExperimentalCounter(symbol);
    }
    return F.NIL;
  }

  /**
   * Print: 'Inverse functions are being used. Values may be lost for multivalued inverses.'
   */
  public static void printIfunMessage(ISymbol symbol) {
    // Inverse functions are being used. Values may be lost for multivalued inverses.
    printMessage(symbol, "ifun", F.CEmptyList, EvalEngine.get());
  }

  /**
   * Format a message according to the shortcut from the {@link MESSAGES} array and print it to the
   * error stream with the <code>engine.printMessage()</code>method.
   *
   * <p>
   * Usage pattern:
   *
   * <pre>
   *    // corresponding long text of "&lt;message-shortcut&gt;" stored in the MESSAGES array
   *    printMessage(S.&lt;builtin-symbol&gt;, "&lt;message-shortcut&gt;", F.list(&lt;param1&gt;, &lt;param2&gt;, ...), engine);
   * </pre>
   *
   * @param symbol
   * @param messageShortcut the message shortcut defined in the {@link MESSAGES} array
   * @param listOfParameters a list of arguments which should be inserted into the message shortcuts
   *        placeholder
   * @return always <code>F.NIL</code>
   */
  public static IAST printMessage(ISymbol symbol, String messageShortcut,
      final IAST listOfParameters) {
    return printMessage(symbol, messageShortcut, listOfParameters, EvalEngine.get());
  }

  /**
   * Format a message according to the shortcut from the {@link MESSAGES} array and print it to the
   * error stream with the help of the {@link EvalEngine#getErrorPrintStream()} method.
   *
   * <p>
   * Usage pattern:
   *
   * <pre>
   *    // corresponding long text of "&lt;message-shortcut&gt;" stored in the MESSAGES array
   *    printMessage(S.&lt;builtin-symbol&gt;, "&lt;message-shortcut&gt;", F.list(&lt;param1&gt;, &lt;param2&gt;, ...), engine);
   * </pre>
   *
   * @param symbol
   * @param messageShortcut the message shortcut defined in the {@link MESSAGES} array
   * @param listOfParameters a list of arguments which should be inserted into the message shortcuts
   *        placeholder
   * @param engine
   * @return always {@link F#NIL}
   */
  public static IAST printMessage(ISymbol symbol, String messageShortcut,
      final IAST listOfParameters, EvalEngine engine) {
    IExpr temp = symbol.evalMessage(messageShortcut);
    String message = null;
    if (temp.isPresent()) {
      message = temp.toString();
    } else {
      temp = S.General.evalMessage(messageShortcut);
      if (temp.isPresent()) {
        message = temp.toString();
      }
    }
    if (message == null) {
      message = "Undefined message shortcut: " + messageShortcut;
      engine.setMessageShortcut(messageShortcut);
      logMessage(symbol, message, engine);
    } else {
      try {
        final IAST cacheKey = F.List(symbol, F.stringx(messageShortcut), listOfParameters);
        Object value = engine.getObjectCache(cacheKey);
        if (value instanceof Errors) {
          engine.setMessageShortcut(messageShortcut);
          return F.NIL;
        }
        Writer writer = new StringWriter();
        Map<String, Object> context = new HashMap<String, Object>();
        if (listOfParameters != null) {
          for (int i = 1; i < listOfParameters.size(); i++) {
            context.put(Integer.toString(i), shorten(listOfParameters.get(i), 256));
          }
        }

        templateApply(message, writer, context);
        engine.setMessageShortcut(messageShortcut);
        logMessage(symbol, writer.toString(), engine);

        engine.putObjectCache(cacheKey, ERRORS_INSTANCE);
      } catch (IOException e) {
        LOGGER.error("IOFunctions.printMessage() failed", e);
      }
    }
    return F.NIL;
  }

  public static void logMessage(ISymbol symbol, String str, EvalEngine engine) {
    if (engine.isQuietMode()) {
      LOGGER.log(engine.getLogLevel(), "{}: {}", symbol, str);
    } else {
      engine.getErrorPrintStream().append(symbol.toString() + ": " + str + "\n");
    }
  }

  public static IAST printRuntimeException(ISymbol symbol, RuntimeException exception,
      EvalEngine engine) {
    try {
      String message = exception.getMessage();
      Writer writer = new StringWriter();
      writer.append(message);
      logMessage(symbol, writer.toString(), engine);
    } catch (IOException e) {
      LOGGER.error("IOFunctions.printMessage() failed", e);
    }

    return F.NIL;
  }

  public static String getMessage(String messageShortcut, final IAST listOfArgs) {
    return getMessage(messageShortcut, listOfArgs, EvalEngine.get());
  }

  public static String getMessage(String messageShortcut, final IAST listOfArgs,
      EvalEngine engine) {
    IExpr temp = S.General.evalMessage(messageShortcut);
    String message = null;
    if (temp.isPresent()) {
      message = temp.toString();
    }
    if (message == null) {
      message = "Undefined message shortcut: " + messageShortcut;
      engine.setMessageShortcut(messageShortcut);
      return message;
    }
    for (int i = 1; i < listOfArgs.size(); i++) {
      message = StringUtils.replace(message, "`" + (i) + "`",
          shorten(listOfArgs.get(i), engine.getOutputSizeLimit()));
    }
    engine.setMessageShortcut(messageShortcut);
    return message;
  }

  private static String rawMessage(final IAST list, String message, int maximumLength) {
    for (int i = 2; i < list.size(); i++) {
      message =
          StringUtils.replace(message, "`" + (i - 1) + "`", shorten(list.get(i), maximumLength));
    }
    return message;
  }

  /**
   * Shorten the output string generated from <code>expr</code> to a maximum length of
   * {@link Config#SHORTEN_STRING_LENGTH} characters. Insert <code>&lt;&lt;SHORT&gt;&gt;</code> as
   * substitute in the middle of the expression if necessary.
   *
   * @param expr
   * @return
   */
  public static String shorten(IExpr expr) {
    return shorten(expr, Config.SHORTEN_STRING_LENGTH);
  }

  /**
   * Shorten the output string generated from <code>expr</code> to a maximum length of <code>
   * maximuLength</code> characters. Insert <code>&lt;&lt;SHORT&gt;&gt;</code> as substitute of the
   * middle in the expression if necessary.
   *
   * @param expr
   * @param maximumLength the maximum length of the result string.
   * @return
   */
  public static String shorten(IExpr expr, int maximumLength) {
    String str = expr.toString();
    return shorten(str, maximumLength);
  }

  /**
   * Shorten the output string to a maximum length of <code>
   * maximuLength</code> characters. Insert <code>&lt;&lt;SHORT&gt;&gt;</code> as substitute in the
   * middle of the expression if necessary.
   *
   * @param str
   * @param maximumLength
   * @return
   */
  public static String shorten(String str, int maximumLength) {
    if (str.length() > maximumLength) {
      StringBuilder buf = new StringBuilder(maximumLength);
      int halfLength = (maximumLength / 2) - 14;
      buf.append(str.substring(0, halfLength));
      buf.append("<<SHORT>>");
      buf.append(str.substring(str.length() - halfLength));
      return buf.toString();
    }
    return str;
  }

  /**
   * Compile the template into an object hierarchy representation for the
   * <a href="https://github.com/PebbleTemplates/pebble">Pebble template engine</a>.
   *
   * @param templateStr
   * @return
   */
  private static PebbleTemplate templateCompile(String templateStr) {
    List<RenderableNode> nodes = new ArrayList<>();
    final int length = templateStr.length();
    int currentPosition = 0;
    int counter = 1;
    int lastLineNumber = 0;
    int lineNumber = 0;
    int lastPosition = 0;
    while (currentPosition < length) {
      char ch = templateStr.charAt(currentPosition++);
      if (ch == '\n') {
        lineNumber++;
        continue;
      }
      if (ch == '`') {
        if (lastPosition < currentPosition - 1) {
          nodes.add(new TextNode(templateStr.substring(lastPosition, currentPosition - 1),
              lastLineNumber));
          lastPosition = currentPosition;
          lastLineNumber = lineNumber;
        }

        int j = currentPosition;
        StringBuilder nameBuf = new StringBuilder();
        while (j < length) {
          char nextCh = templateStr.charAt(j++);
          if (nextCh == '`') {
            if (j == currentPosition + 1) {
              nameBuf.append(counter++);
            }
            currentPosition = j;
            break;
          }
          nameBuf.append(nextCh);
        }
        Expression<?> expression = new ContextVariableExpression(nameBuf.toString(), lineNumber);
        nodes.add(new PrintNode(expression, lineNumber));
        lastPosition = currentPosition;
        lastLineNumber = lineNumber;
      }
    }
    if (lastPosition < length) {
      nodes.add(new TextNode(templateStr.substring(lastPosition, length), lineNumber));
      lastPosition = currentPosition;
    }
    BodyNode body = new BodyNode(0, nodes);
    RootNode rootNode = new RootNode(body);
    return new PebbleTemplateImpl(PEBBLE_ENGINE, rootNode, templateStr);
  }

  /**
   * Render the template with the assigned context variable strings.
   *
   * @param templateString the template which should be rendered
   * @param outputWriter use {@link Writer#toString()} to get the rendered result
   * @param context the assigned variables which should be rendered in the template
   * @throws IOException
   */
  private static void templateApply(String templateString, Writer outputWriter,
      Map<String, Object> context) throws IOException {
    PebbleCache<Object, PebbleTemplate> cache = PEBBLE_ENGINE.getTemplateCache();
    PebbleTemplate template =
        cache.computeIfAbsent(templateString, x -> templateCompile(templateString));

    template.evaluate(outputWriter, context);
  }

  /**
   * Render the template string with the <code>args</code> parameters. Typically the <code>args
   * </code> are a <code>List</code> or an <code>Association</code> expressiosn, otherwise the
   * <code>args</code> parameter will be ignored.
   *
   * @param templateStr
   * @param args a <code>List</code> or an <code>Association</code> expression
   * @return the rendered template as an {@link IStringX} expression
   */
  public static IStringX templateApply(String templateStr, IExpr args) {
    String str = templateRender(templateStr, args);
    return F.stringx(str);
  }

  /**
   * Render the template string with the <code>args</code> parameters. Typically the <code>args
   * </code> are a <code>List</code> or an <code>Association</code> expression, otherwise the <code>
   * args</code> parameter will be ignored.
   *
   * @param templateStr
   * @param args
   * @return the rendered template as a {@link String} expression
   */
  public static String templateRender(String templateStr, IExpr args) {
    try {
      Map<String, Object> context = new HashMap<>();
      if (args.isListOrAssociation()) {

        if (args.isList()) {
          IAST list = (IAST) args;
          for (int i = 1; i < list.size(); i++) {
            context.put(Integer.toString(i), list.get(i).toString());
          }
        } else if (args.isAssociation()) {
          IAssociation assoc = (IAssociation) args;
          for (int i = 1; i < assoc.size(); i++) {
            IAST rule = assoc.getRule(i);
            IExpr lhs = rule.arg1();
            IExpr rhs = rule.arg2();
            context.put(lhs.toString(), rhs.toString());
          }
        }
      }
      Writer writer = new StringWriter();
      templateApply(templateStr, writer, context);
      return writer.toString();
    } catch (IOException e) {
      LOGGER.error("IOFunctions.templateRender()", e);
    }
    return templateStr;
  }

  /**
   * Render the template string with the <code>args</code> string parameters.
   *
   * @param templateStr
   * @param args
   * @return
   */
  public static String templateRender(String templateStr, String[] args) {
    try {
      Map<String, Object> context = new HashMap<>();
      int i = 1;
      for (String str : args) {
        context.put(Integer.toString(i++), str);
      }
      Writer writer = new StringWriter();
      templateApply(templateStr, writer, context);
      return writer.toString();
    } catch (IOException e) {
      LOGGER.error("IOFunctions.templateRender() failed", e);
    }
    return templateStr;
  }


  public static void rethrowsInterruptException(Exception e) {
    if (e instanceof ApfloatInterruptedException || e instanceof PreemptingException
        || e instanceof TimeoutException) {
      throw (RuntimeException) e;
    }
    if (e instanceof RuntimeException && e.getCause() instanceof InterruptedException) {
      throw (RuntimeException) e;
    }
  }
}
