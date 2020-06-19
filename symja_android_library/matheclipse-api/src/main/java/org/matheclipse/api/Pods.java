package org.matheclipse.api;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.matheclipse.api.parser.FuzzyParser;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.data.ElementData1;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.Tries;
import org.owasp.encoder.Encode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Suppliers;

public class Pods {
	private static class LevenshteinDistanceComparator implements Comparator<IPod> {
		static final LevenshteinDistance ld = new LevenshteinDistance(128);

		String str;

		public LevenshteinDistanceComparator(String str) {
			this.str = str;
		}

		@Override
		public int compare(IPod arg0, IPod arg1) {
			int d0 = ld.apply(str, arg0.keyWord());
			int d1 = ld.apply(str, arg1.keyWord());
			return d0 > d1 ? 1 : d0 < d1 ? -1 : 0;
		}

	}

	public static final Object[] STEMS = new Object[] { //
			"abort", F.Abort, //
			"ab", F.Abs, //
			"absarg", F.AbsArg, //
			"accumul", F.Accumulate, //
			"addto", F.AddTo, //
			"adjacencymatrix", F.AdjacencyMatrix, //
			"all", F.All, //
			"alltru", F.AllTrue, //
			"altern", F.Alternatives, //
			"anglevector", F.AngleVector, //
			"annuiti", F.Annuity, //
			"annuitydu", F.AnnuityDue, //
			"antihermitianmatrixq", F.AntihermitianMatrixQ, //
			"antisymmetricmatrixq", F.AntisymmetricMatrixQ, //
			"anytru", F.AnyTrue, //
			"apart", F.Apart, //
			"append", F.Append, //
			"appendto", F.AppendTo, //
			"appli", F.Apply, //
			"arcco", F.ArcCos, //
			"arccosh", F.ArcCosh, //
			"arccot", F.ArcCot, //
			"arccoth", F.ArcCoth, //
			"arccsc", F.ArcCsc, //
			"arccsch", F.ArcCsch, //
			"arcsec", F.ArcSec, //
			"arcsech", F.ArcSech, //
			"arcsin", F.ArcSin, //
			"arcsinh", F.ArcSinh, //
			"arctan", F.ArcTan, //
			"arctanh", F.ArcTanh, //
			"arg", F.Arg, //
			"argmax", F.ArgMax, //
			"argmin", F.ArgMin, //
			"arithmeticgeometricmean", F.ArithmeticGeometricMean, //
			"arrai", F.Array, //
			"arraydepth", F.ArrayDepth, //
			"arraypad", F.ArrayPad, //
			"arrayq", F.ArrayQ, //
			"arrayreshap", F.ArrayReshape, //
			"associ", F.Association, //
			"associationq", F.AssociationQ, //
			"atomq", F.AtomQ, //
			"attribut", F.Attributes, //
			"baseform", F.BaseForm, //
			"begin", F.Begin, //
			"beginpackag", F.BeginPackage, //
			"bellb", F.BellB, //
			"belli", F.BellY, //
			"bernoullib", F.BernoulliB, //
			"bernoullidistribut", F.BernoulliDistribution, //
			"bess", F.BesselI, //
			"besselj", F.BesselJ, //
			"besseljzero", F.BesselJZero, //
			"besselk", F.BesselK, //
			"bess", F.BesselY, //
			"beta", F.Beta, //
			"binarydeseri", F.BinaryDeserialize, //
			"binaryseri", F.BinarySerialize, //
			"bincount", F.BinCounts, //
			"binomi", F.Binomial, //
			"binomialdistribut", F.BinomialDistribution, //
			"bitlength", F.BitLength, //
			"block", F.Block, //
			"bool", F.Boole, //
			"booleanconvert", F.BooleanConvert, //
			"booleanminim", F.BooleanMinimize, //
			"booleanq", F.BooleanQ, //
			"boolean", F.Booleans, //
			"booleant", F.BooleanTable, //
			"booleanvari", F.BooleanVariables, //
			"braycurtisdist", F.BrayCurtisDistance, //
			"break", F.Break, //
			"bytearrai", F.ByteArray, //
			"bytearrayq", F.ByteArrayQ, //
			"c", F.C, //
			"canberradist", F.CanberraDistance, //
			"cancel", F.Cancel, //
			"carmichaellambda", F.CarmichaelLambda, //
			"cartesianproduct", F.CartesianProduct, //
			"case", F.Cases, //
			"catalan", F.Catalan, //
			"catalannumb", F.CatalanNumber, //
			"caten", F.Catenate, //
			"cdf", F.CDF, //
			"ceil", F.Ceiling, //
			"centralmo", F.CentralMoment, //
			"characteristicpolynomi", F.CharacteristicPolynomial, //
			"chebyshevt", F.ChebyshevT, //
			"chebyshevu", F.ChebyshevU, //
			"check", F.Check, //
			"chessboarddist", F.ChessboardDistance, //
			"chineseremaind", F.ChineseRemainder, //
			"choleskydecomposit", F.CholeskyDecomposition, //
			"chop", F.Chop, //
			"circlepoint", F.CirclePoints, //
			"clear", F.Clear, //
			"clearal", F.ClearAll, //
			"clearattribut", F.ClearAttributes, //
			"clip", F.Clip, //
			"coeffici", F.Coefficient, //
			"coefficientlist", F.CoefficientList, //
			"coefficientrul", F.CoefficientRules, //
			"collect", F.Collect, //
			"complement", F.Complement, //
			"complex", F.Complex, //
			"complex", F.Complexes, //
			"complexexpand", F.ComplexExpand, //
			"complexinfin", F.ComplexInfinity, //
			"complexplot3d", F.ComplexPlot3D, //
			"composelist", F.ComposeList, //
			"composeseri", F.ComposeSeries, //
			"composit", F.Composition, //
			"compoundexpress", F.CompoundExpression, //
			"condit", F.Condition, //
			"conditionalexpress", F.ConditionalExpression, //
			"conjug", F.Conjugate, //
			"conjugatetranspos", F.ConjugateTranspose, //
			"constant", F.Constant, //
			"constantarrai", F.ConstantArray, //
			"containsonli", F.ContainsOnly, //
			"context", F.Context, //
			"continu", F.Continue, //
			"continuedfract", F.ContinuedFraction, //
			"converg", F.Convergents, //
			"coprimeq", F.CoprimeQ, //
			"correl", F.Correlation, //
			"co", F.Cos, //
			"cosh", F.Cosh, //
			"coshintegr", F.CoshIntegral, //
			"cosinedist", F.CosineDistance, //
			"cosintegr", F.CosIntegral, //
			"cot", F.Cot, //
			"coth", F.Coth, //
			"count", F.Count, //
			"count", F.Counts, //
			"covari", F.Covariance, //
			"cross", F.Cross, //
			"csc", F.Csc, //
			"csch", F.Csch, //
			"cuberoot", F.CubeRoot, //
			"curl", F.Curl, //
			"cyclotom", F.Cyclotomic, //
			"d", F.D, //
			"decrement", F.Decrement, //
			"default", F.Default, //
			"defer", F.Defer, //
			"definit", F.Definition, //
			"degre", F.Degree, //
			"delet", F.Delete, //
			"deletecas", F.DeleteCases, //
			"deletedupl", F.DeleteDuplicates, //
			"denomin", F.Denominator, //
			"depth", F.Depth, //
			"deriv", F.Derivative, //
			"designmatrix", F.DesignMatrix, //
			"det", F.Det, //
			"diagon", F.Diagonal, //
			"diagonalmatrix", F.DiagonalMatrix, //
			"dialoginput", F.DialogInput, //
			"dicedissimilar", F.DiceDissimilarity, //
			"digitcount", F.DigitCount, //
			"digitq", F.DigitQ, //
			"dimens", F.Dimensions, //
			"diracdelta", F.DiracDelta, //
			"directedinfin", F.DirectedInfinity, //
			"discretedelta", F.DiscreteDelta, //
			"discreteuniformdistribut", F.DiscreteUniformDistribution, //
			"discrimin", F.Discriminant, //
			"distribut", F.Distribute, //
			"div", F.Div, //
			"divid", F.Divide, //
			"dividebi", F.DivideBy, //
			"divis", F.Divisible, //
			"divisor", F.Divisors, //
			"divisorsigma", F.DivisorSigma, //
			"divisorsum", F.DivisorSum, //
			"do", F.Do, //
			"dot", F.Dot, //
			"drop", F.Drop, //
			"dsolv", F.DSolve, //
			"e", F.E, //
			"edgelist", F.EdgeList, //
			"edgeq", F.EdgeQ, //
			"effectiveinterest", F.EffectiveInterest, //
			"eigenvalu", F.Eigenvalues, //
			"eigenvector", F.Eigenvectors, //
			"element", F.Element, //
			"elementdata", F.ElementData, //
			"elimin", F.Eliminate, //
			"elliptic", F.EllipticE, //
			"ellipticf", F.EllipticF, //
			"elliptick", F.EllipticK, //
			"ellipticpi", F.EllipticPi, //
			"end", F.End, //
			"endpackag", F.EndPackage, //
			"equal", F.Equal, //
			"equival", F.Equivalent, //
			"erf", F.Erf, //
			"erfc", F.Erfc, //
			"erlangdistribut", F.ErlangDistribution, //
			"euclideandist", F.EuclideanDistance, //
			"euler", F.EulerE, //
			"euleriangraphq", F.EulerianGraphQ, //
			"eulerphi", F.EulerPhi, //
			"evalu", F.Evaluate, //
			"evenq", F.EvenQ, //
			"exactnumberq", F.ExactNumberQ, //
			"except", F.Except, //
			"exp", F.Exp, //
			"expand", F.Expand, //
			"expandal", F.ExpandAll, //
			"expect", F.Expectation, //
			"expintegral", F.ExpIntegralE, //
			"expintegralei", F.ExpIntegralEi, //
			"expon", F.Exponent, //
			"exponentialdistribut", F.ExponentialDistribution, //
			"export", F.Export, //
			"extendedgcd", F.ExtendedGCD, //
			"extract", F.Extract, //
			"factor", F.Factor, //
			"factori", F.Factorial, //
			"factorial2", F.Factorial2, //
			"factorinteg", F.FactorInteger, //
			"factorsquarefre", F.FactorSquareFree, //
			"factorsquarefreelist", F.FactorSquareFreeList, //
			"factorterm", F.FactorTerms, //
			"fals", F.False, //
			"fibonacci", F.Fibonacci, //
			"findeuleriancycl", F.FindEulerianCycle, //
			"findfit", F.FindFit, //
			"findhamiltoniancycl", F.FindHamiltonianCycle, //
			"findinst", F.FindInstance, //
			"findroot", F.FindRoot, //
			"findshortestpath", F.FindShortestPath, //
			"findshortesttour", F.FindShortestTour, //
			"findspanningtre", F.FindSpanningTree, //
			"findvertexcov", F.FindVertexCover, //
			"first", F.First, //
			"fit", F.Fit, //
			"fivenum", F.FiveNum, //
			"fixedpoint", F.FixedPoint, //
			"fixedpointlist", F.FixedPointList, //
			"flat", F.Flat, //
			"flatten", F.Flatten, //
			"flattenat", F.FlattenAt, //
			"floor", F.Floor, //
			"fold", F.Fold, //
			"foldlist", F.FoldList, //
			"fourier", F.Fourier, //
			"fouriermatrix", F.FourierMatrix, //
			"fractionalpart", F.FractionalPart, //
			"frechetdistribut", F.FrechetDistribution, //
			"freeq", F.FreeQ, //
			"frobeniusnumb", F.FrobeniusNumber, //
			"frobeniussolv", F.FrobeniusSolve, //
			"fromcharactercod", F.FromCharacterCode, //
			"fromcontinuedfract", F.FromContinuedFraction, //
			"fromdigit", F.FromDigits, //
			"frompolarcoordin", F.FromPolarCoordinates, //
			"fullform", F.FullForm, //
			"fullsimplifi", F.FullSimplify, //
			"functionexpand", F.FunctionExpand, //
			"gamma", F.Gamma, //
			"gammadistribut", F.GammaDistribution, //
			"gather", F.Gather, //
			"gatherbi", F.GatherBy, //
			"gcd", F.GCD, //
			"gegenbauerc", F.GegenbauerC, //
			"geodist", F.GeoDistance, //
			"geometricdistribut", F.GeometricDistribution, //
			"geometricmean", F.GeometricMean, //
			"get", F.Get, //
			"glaisher", F.Glaisher, //
			"goldenratio", F.GoldenRatio, //
			"grad", F.Grad, //
			"graph", F.Graph, //
			"graphcent", F.GraphCenter, //
			"graphdiamet", F.GraphDiameter, //
			"graphperipheri", F.GraphPeriphery, //
			"graphq", F.GraphQ, //
			"graphradiu", F.GraphRadius, //
			"greater", F.Greater, //
			"greaterequ", F.GreaterEqual, //
			"groebnerbasi", F.GroebnerBasis, //
			"gumbeldistribut", F.GumbelDistribution, //
			"hamiltoniangraphq", F.HamiltonianGraphQ, //
			"harmonicmean", F.HarmonicMean, //
			"harmonicnumb", F.HarmonicNumber, //
			"haversin", F.Haversine, //
			"head", F.Head, //
			"hermiteh", F.HermiteH, //
			"hermitianmatrixq", F.HermitianMatrixQ, //
			"hilbertmatrix", F.HilbertMatrix, //
			"hold", F.Hold, //
			"holdal", F.HoldAll, //
			"holdfirst", F.HoldFirst, //
			"holdform", F.HoldForm, //
			"holdpattern", F.HoldPattern, //
			"holdrest", F.HoldRest, //
			"hornerform", F.HornerForm, //
			"hurwitzzeta", F.HurwitzZeta, //
			"hypergeometric0f1", F.Hypergeometric0F1, //
			"hypergeometric1f1", F.Hypergeometric1F1, //
			"hypergeometric2f1", F.Hypergeometric2F1, //
			"hypergeometricdistribut", F.HypergeometricDistribution, //
			"hypergeometricpfq", F.HypergeometricPFQ, //
			"i", F.I, //
			"ident", F.Identity, //
			"identitymatrix", F.IdentityMatrix, //
			"im", F.Im, //
			"impli", F.Implies, //
			"import", F.Import, //
			"increment", F.Increment, //
			"indetermin", F.Indeterminate, //
			"inexactnumberq", F.InexactNumberQ, //
			"infin", F.Infinity, //
			"inner", F.Inner, //
			"input", F.Input, //
			"inputstr", F.InputString, //
			"integ", F.Integer, //
			"integerdigit", F.IntegerDigits, //
			"integerexpon", F.IntegerExponent, //
			"integerlength", F.IntegerLength, //
			"integernam", F.IntegerName, //
			"integerpart", F.IntegerPart, //
			"integerpartit", F.IntegerPartitions, //
			"integerq", F.IntegerQ, //
			"integ", F.Integers, //
			"integr", F.Integrate, //
			"interpolatingfunct", F.InterpolatingFunction, //
			"interpolatingpolynomi", F.InterpolatingPolynomial, //
			"interrupt", F.Interrupt, //
			"intersect", F.Intersection, //
			"interv", F.Interval, //
			"invers", F.Inverse, //
			"inversecdf", F.InverseCDF, //
			"inverseerf", F.InverseErf, //
			"inverseerfc", F.InverseErfc, //
			"inversefouri", F.InverseFourier, //
			"inversefunct", F.InverseFunction, //
			"inversehaversin", F.InverseHaversine, //
			"inverselaplacetransform", F.InverseLaplaceTransform, //
			"inverseseri", F.InverseSeries, //
			"jaccarddissimilar", F.JaccardDissimilarity, //
			"jacobiamplitud", F.JacobiAmplitude, //
			"jacobicn", F.JacobiCN, //
			"jacobidn", F.JacobiDN, //
			"jacobimatrix", F.JacobiMatrix, //
			"jacobisn", F.JacobiSN, //
			"jacobisymbol", F.JacobiSymbol, //
			"javaform", F.JavaForm, //
			"join", F.Join, //
			"jsform", F.JSForm, //
			"kei", F.Keys, //
			"keysort", F.KeySort, //
			"khinchin", F.Khinchin, //
			"kolmogorovsmirnovtest", F.KolmogorovSmirnovTest, //
			"kroneckerdelta", F.KroneckerDelta, //
			"kurtosi", F.Kurtosis, //
			"laguerrel", F.LaguerreL, //
			"laplacetransform", F.LaplaceTransform, //
			"last", F.Last, //
			"lcm", F.LCM, //
			"leafcount", F.LeafCount, //
			"leastsquar", F.LeastSquares, //
			"legendrep", F.LegendreP, //
			"legendreq", F.LegendreQ, //
			"length", F.Length, //
			"less", F.Less, //
			"lessequ", F.LessEqual, //
			"letterq", F.LetterQ, //
			"level", F.Level, //
			"levelq", F.LevelQ, //
			"limit", F.Limit, //
			"linearprogram", F.LinearProgramming, //
			"linearrecurr", F.LinearRecurrence, //
			"linearsolv", F.LinearSolve, //
			"list", F.List, //
			"listabl", F.Listable, //
			"listconvolv", F.ListConvolve, //
			"listcorrel", F.ListCorrelate, //
			"listlineplot", F.ListLinePlot, //
			"listplot", F.ListPlot, //
			"listplot3d", F.ListPlot3D, //
			"listq", F.ListQ, //
			"log", F.Log, //
			"log10", F.Log10, //
			"log2", F.Log2, //
			"logintegr", F.LogIntegral, //
			"logisticsigmoid", F.LogisticSigmoid, //
			"lognormaldistribut", F.LogNormalDistribution, //
			"lookup", F.Lookup, //
			"lowertriangular", F.LowerTriangularize, //
			"lucasl", F.LucasL, //
			"ludecomposit", F.LUDecomposition, //
			"machinenumberq", F.MachineNumberQ, //
			"mangoldtlambda", F.MangoldtLambda, //
			"manhattandist", F.ManhattanDistance, //
			"manipul", F.Manipulate, //
			"map", F.Map, //
			"mapindex", F.MapIndexed, //
			"mapthread", F.MapThread, //
			"matchingdissimilar", F.MatchingDissimilarity, //
			"matchq", F.MatchQ, //
			"mathmlform", F.MathMLForm, //
			"matrixminimalpolynomi", F.MatrixMinimalPolynomial, //
			"matrixpow", F.MatrixPower, //
			"matrixq", F.MatrixQ, //
			"matrixrank", F.MatrixRank, //
			"max", F.Max, //
			"maxfilt", F.MaxFilter, //
			"maxim", F.Maximize, //
			"mean", F.Mean, //
			"meanfilt", F.MeanFilter, //
			"median", F.Median, //
			"medianfilt", F.MedianFilter, //
			"memberq", F.MemberQ, //
			"mersenneprimeexpon", F.MersennePrimeExponent, //
			"mersenneprimeexponentq", F.MersennePrimeExponentQ, //
			"messag", F.Message, //
			"messagenam", F.MessageName, //
			"min", F.Min, //
			"minfilt", F.MinFilter, //
			"minim", F.Minimize, //
			"minu", F.Minus, //
			"missingq", F.MissingQ, //
			"mod", F.Mod, //
			"modul", F.Module, //
			"moebiusmu", F.MoebiusMu, //
			"monomiallist", F.MonomialList, //
			"most", F.Most, //
			"multinomi", F.Multinomial, //
			"multiplicativeord", F.MultiplicativeOrder, //
			"n", F.N, //
			"nakagamidistribut", F.NakagamiDistribution, //
			"nand", F.Nand, //
			"nd", F.ND, //
			"neg", F.Negative, //
			"nest", F.Nest, //
			"nestlist", F.NestList, //
			"nestwhil", F.NestWhile, //
			"nestwhilelist", F.NestWhileList, //
			"nextprim", F.NextPrime, //
			"nholdal", F.NHoldAll, //
			"nholdfirst", F.NHoldFirst, //
			"nholdrest", F.NHoldRest, //
			"nintegr", F.NIntegrate, //
			"nmaxim", F.NMaximize, //
			"nminim", F.NMinimize, //
			"none", F.None, //
			"nonetru", F.NoneTrue, //
			"nonneg", F.NonNegative, //
			"nonposit", F.NonPositive, //
			"nor", F.Nor, //
			"norm", F.Norm, //
			"normal", F.Normal, //
			"normaldistribut", F.NormalDistribution, //
			"normal", F.Normalize, //
			"nroot", F.NRoots, //
			"nsolv", F.NSolve, //
			"null", F.Null, //
			"nullspac", F.NullSpace, //
			"numberq", F.NumberQ, //
			"numer", F.Numerator, //
			"numericq", F.NumericQ, //
			"oddq", F.OddQ, //
			"off", F.Off, //
			"oneident", F.OneIdentity, //
			"oper", F.Operate, //
			"optimizeexpress", F.OptimizeExpression, //
			"option", F.Optional, //
			"order", F.Order, //
			"orderedq", F.OrderedQ, //
			"order", F.Ordering, //
			"orderless", F.Orderless, //
			"orthogon", F.Orthogonalize, //
			"orthogonalmatrixq", F.OrthogonalMatrixQ, //
			"outer", F.Outer, //
			"padleft", F.PadLeft, //
			"padright", F.PadRight, //
			"parametricplot", F.ParametricPlot, //
			"part", F.Part, //
			"partit", F.Partition, //
			"partitionsp", F.PartitionsP, //
			"partitionsq", F.PartitionsQ, //
			"patterntest", F.PatternTest, //
			"pdf", F.PDF, //
			"perfectnumb", F.PerfectNumber, //
			"perfectnumberq", F.PerfectNumberQ, //
			"permut", F.Permutations, //
			"pi", F.Pi, //
			"piecewis", F.Piecewise, //
			"plot", F.Plot, //
			"plot3d", F.Plot3D, //
			"plu", F.Plus, //
			"pochhamm", F.Pochhammer, //
			"poissondistribut", F.PoissonDistribution, //
			"polarplot", F.PolarPlot, //
			"polygamma", F.PolyGamma, //
			"polynomialextendedgcd", F.PolynomialExtendedGCD, //
			"polynomialgcd", F.PolynomialGCD, //
			"polynomiallcm", F.PolynomialLCM, //
			"polynomialq", F.PolynomialQ, //
			"polynomialquoti", F.PolynomialQuotient, //
			"polynomialquotientremaind", F.PolynomialQuotientRemainder, //
			"polynomialremaind", F.PolynomialRemainder, //
			"posit", F.Position, //
			"posit", F.Positive, //
			"possiblezeroq", F.PossibleZeroQ, //
			"power", F.Power, //
			"powerexpand", F.PowerExpand, //
			"powermod", F.PowerMod, //
			"predecr", F.PreDecrement, //
			"preincrement", F.PreIncrement, //
			"prepend", F.Prepend, //
			"prependto", F.PrependTo, //
			"prime", F.Prime, //
			"primeomega", F.PrimeOmega, //
			"primepi", F.PrimePi, //
			"primepowerq", F.PrimePowerQ, //
			"primeq", F.PrimeQ, //
			"primitiverootlist", F.PrimitiveRootList, //
			"probabl", F.Probability, //
			"product", F.Product, //
			"productlog", F.ProductLog, //
			"project", F.Projection, //
			"pseudoinvers", F.PseudoInverse, //
			"qrdecomposit", F.QRDecomposition, //
			"quantil", F.Quantile, //
			"quantiti", F.Quantity, //
			"quantitymagnitud", F.QuantityMagnitude, //
			"quartil", F.Quartiles, //
			"quiet", F.Quiet, //
			"quotient", F.Quotient, //
			"quotientremaind", F.QuotientRemainder, //
			"randomchoic", F.RandomChoice, //
			"randominteg", F.RandomInteger, //
			"randomprim", F.RandomPrime, //
			"randomr", F.RandomReal, //
			"randomsampl", F.RandomSample, //
			"rang", F.Range, //
			"ration", F.Rational, //
			"ration", F.Rationalize, //
			"re", F.Re, //
			"real", F.Real, //
			"realnumberq", F.RealNumberQ, //
			"real", F.Reals, //
			"reap", F.Reap, //
			"refin", F.Refine, //
			"replac", F.Replace, //
			"replaceal", F.ReplaceAll, //
			"replacelist", F.ReplaceList, //
			"replacepart", F.ReplacePart, //
			"replacerep", F.ReplaceRepeated, //
			"rescal", F.Rescale, //
			"rest", F.Rest, //
			"result", F.Resultant, //
			"return", F.Return, //
			"revers", F.Reverse, //
			"riffl", F.Riffle, //
			"rogerstanimotodissimilar", F.RogersTanimotoDissimilarity, //
			"romannumer", F.RomanNumeral, //
			"root", F.Roots, //
			"rotateleft", F.RotateLeft, //
			"rotateright", F.RotateRight, //
			"rotationmatrix", F.RotationMatrix, //
			"round", F.Round, //
			"rowreduc", F.RowReduce, //
			"rule", F.Rule, //
			"ruledelai", F.RuleDelayed, //
			"russellraodissimilar", F.RussellRaoDissimilarity, //
			"sameq", F.SameQ, //
			"satisfiabilitycount", F.SatisfiabilityCount, //
			"satisfiabilityinst", F.SatisfiabilityInstances, //
			"satisfiableq", F.SatisfiableQ, //
			"scan", F.Scan, //
			"sec", F.Sec, //
			"sech", F.Sech, //
			"select", F.Select, //
			"selectfirst", F.SelectFirst, //
			"semanticimport", F.SemanticImport, //
			"semanticimportstr", F.SemanticImportString, //
			"seri", F.Series, //
			"seriescoeffici", F.SeriesCoefficient, //
			"seriesdata", F.SeriesData, //
			"set", F.Set, //
			"setattribut", F.SetAttributes, //
			"setdelai", F.SetDelayed, //
			"sign", F.Sign, //
			"simplifi", F.Simplify, //
			"sin", F.Sin, //
			"sinc", F.Sinc, //
			"singularvaluedecomposit", F.SingularValueDecomposition, //
			"sinh", F.Sinh, //
			"sinhintegr", F.SinhIntegral, //
			"sinintegr", F.SinIntegral, //
			"skew", F.Skewness, //
			"slot", F.Slot, //
			"slotsequ", F.SlotSequence, //
			"sokalsneathdissimilar", F.SokalSneathDissimilarity, //
			"solv", F.Solve, //
			"sort", F.Sort, //
			"sortbi", F.SortBy, //
			"sow", F.Sow, //
			"span", F.Span, //
			"sphericalbesselj", F.SphericalBesselJ, //
			"sphericalbess", F.SphericalBesselY, //
			"split", F.Split, //
			"splitbi", F.SplitBy, //
			"sqrt", F.Sqrt, //
			"squaredeuclideandist", F.SquaredEuclideanDistance, //
			"squarefreeq", F.SquareFreeQ, //
			"squarematrixq", F.SquareMatrixQ, //
			"standarddevi", F.StandardDeviation, //
			"stirlings1", F.StirlingS1, //
			"stirlings2", F.StirlingS2, //
			"stringcas", F.StringCases, //
			"stringcontainsq", F.StringContainsQ, //
			"stringjoin", F.StringJoin, //
			"stringlength", F.StringLength, //
			"stringmatchq", F.StringMatchQ, //
			"stringpart", F.StringPart, //
			"stringq", F.StringQ, //
			"stringreplac", F.StringReplace, //
			"stringsplit", F.StringSplit, //
			"struveh", F.StruveH, //
			"struvel", F.StruveL, //
			"studenttdistribut", F.StudentTDistribution, //
			"subdivid", F.Subdivide, //
			"subfactori", F.Subfactorial, //
			"subset", F.Subsets, //
			"subtract", F.Subtract, //
			"subtractfrom", F.SubtractFrom, //
			"sum", F.Sum, //
			"surd", F.Surd, //
			"survivalfunct", F.SurvivalFunction, //
			"switch", F.Switch, //
			"symbol", F.Symbol, //
			"symbolnam", F.SymbolName, //
			"symbolq", F.SymbolQ, //
			"symmetricmatrixq", F.SymmetricMatrixQ, //
			"syntaxq", F.SyntaxQ, //
			"systemdialoginput", F.SystemDialogInput, //
			"tabl", F.Table, //
			"take", F.Take, //
			"tan", F.Tan, //
			"tanh", F.Tanh, //
			"tautologyq", F.TautologyQ, //
			"texform", F.TeXForm, //
			"thread", F.Thread, //
			"through", F.Through, //
			"time", F.Times, //
			"timesbi", F.TimesBy, //
			"timevalu", F.TimeValue, //
			"time", F.Timing, //
			"tocharactercod", F.ToCharacterCode, //
			"toeplitzmatrix", F.ToeplitzMatrix, //
			"toexpress", F.ToExpression, //
			"togeth", F.Together, //
			"topolarcoordin", F.ToPolarCoordinates, //
			"tostr", F.ToString, //
			"total", F.Total, //
			"tr", F.Tr, //
			"trace", F.Trace, //
			"transpos", F.Transpose, //
			"treeform", F.TreeForm, //
			"trigexpand", F.TrigExpand, //
			"trigreduc", F.TrigReduce, //
			"trigtoexp", F.TrigToExp, //
			"true", F.True, //
			"trueq", F.TrueQ, //
			"tupl", F.Tuples, //
			"unequ", F.Unequal, //
			"uniformdistribut", F.UniformDistribution, //
			"union", F.Union, //
			"uniqu", F.Unique, //
			"unitconvert", F.UnitConvert, //
			"unit", F.Unitize, //
			"unitstep", F.UnitStep, //
			"unitvector", F.UnitVector, //
			"unsameq", F.UnsameQ, //
			"unset", F.Unset, //
			"uppercaseq", F.UpperCaseQ, //
			"uppertriangular", F.UpperTriangularize, //
			"valueq", F.ValueQ, //
			"valu", F.Values, //
			"vandermondematrix", F.VandermondeMatrix, //
			"variabl", F.Variables, //
			"varianc", F.Variance, //
			"vectorangl", F.VectorAngle, //
			"vectorq", F.VectorQ, //
			"vertexeccentr", F.VertexEccentricity, //
			"vertexlist", F.VertexList, //
			"vertexq", F.VertexQ, //
			"weibulldistribut", F.WeibullDistribution, //
			"which", F.Which, //
			"while", F.While, //
			"xor", F.Xor, //
			"yuledissimilar", F.YuleDissimilarity, //
			"zeta", F.Zeta, //
	};

	public static final Object[] STEMS1 = new Object[] { //
			"convert", F.UnitConvert, "convers", F.UnitConvert, //
			"expand", F.ExpandAll, "expans", F.ExpandAll, //
			"deriv", F.D, "differenti", F.D, //
			"horner", F.HornerForm, //
			"mathml", F.MathMLForm, //
			"simplif", F.FullSimplify, "simplifi", F.FullSimplify, //
			"solver", F.Solve, //
			"tex", F.TeXForm, //
			"tree", F.TreeForm,//
	};
	public static final Trie<String, IBuiltInSymbol> STEM_MAP = Tries.forStrings();
	// output formats
	public static final String HTML_STR = "html";
	public static final String PLAIN_STR = "plaintext";
	public static final String SYMJA_STR = "sinput";
	public static final String MATHML_STR = "mathml";
	public static final String LATEX_STR = "latex";
	public static final String MARKDOWN_STR = "markdown";
	public static final String MATHCELL_STR = "mathcell";
	public static final String JSXGRAPH_STR = "jsxgraph";
	public static final String PLOTLY_STR = "plotly";

	public static final String VISJS_STR = "visjs";
	public static final int HTML = 0x0001;
	public static final int PLAIN = 0x0002;
	public static final int SYMJA = 0x0004;
	public static final int MATHML = 0x0008;
	public static final int LATEX = 0x0010;
	public static final int MARKDOWN = 0x0020;
	public static final int MATHCELL = 0x0040;
	public static final int JSXGRAPH = 0x0080;
	public static final int PLOTLY = 0x0100;
	public static final int VISJS = 0x0200;

	// output
	public static final String JSON = "JSON";
	public static final Soundex SOUNDEX = new Soundex();

	public static final Trie<String, ArrayList<IPod>> SOUNDEX_MAP = Tries.forStrings();
	static {
		for (int i = 0; i < STEMS.length; i += 2) {
			STEM_MAP.put((String) STEMS[i], (IBuiltInSymbol) STEMS[i + 1]);
		}
		for (int i = 0; i < STEMS1.length; i += 2) {
			STEM_MAP.put((String) STEMS1[i], (IBuiltInSymbol) STEMS1[i + 1]);
		}
	}

	private static Supplier<Trie<String, ArrayList<IPod>>> LAZY_SOUNDEX = Suppliers.memoize(Pods::initSoundex);

	final static String JSXGRAPH_IFRAME = //
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
					"\n" + //
					"<!DOCTYPE html PUBLIC\n" + //
					"  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n" + //
					"  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + //
					"\n" + //
					"<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
					+ //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>JSXGraph</title>\n" + //
					"\n" + //
					"<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n" + //
					"\n" + //
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css\" />\n"
					+ //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.9/build/math.js\"></script>\n"
					+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js\"\n" + //
					"        type=\"text/javascript\"></script>\n" + //
					"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js\"\n" + //
					"        type=\"text/javascript\"></script>\n" + //

					"\n" + //
					"<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden\">\n"
					+ //
					"<script>\n" + //
					"`1`\n" + //
					"</script>\n" + //
					"</div>\n" + //
					"\n" + //
					"</body>\n" + //
					"</html>";//

	protected final static String MATHCELL_IFRAME = //
			// "<html style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
					"\n" + //
					"<!DOCTYPE html PUBLIC\n" + //
					"  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n" + //
					"  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + //
					"\n" + //
					"<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
					+ //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>MathCell</title>\n" + //
					"</head>\n" + //
					"\n" + //
					"<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n" + //
					"\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.9/build/math.js\"></script>\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.8.8/build/mathcell.js\"></script>\n"
					+ //
					"<script src=\"https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML\"></script>"
					+ //
					"\n" + //
					"<div class=\"mathcell\" style=\"display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
					+ //
					"<script>\n" + //
					"\n" + //
					"var parent = document.scripts[ document.scripts.length - 1 ].parentNode;\n" + //
					"\n" + //
					"var id = generateId();\n" + //
					"parent.id = id;\n" + //
					"\n" + //
					"`1`\n" + //
					"\n" + //
					"parent.update( id );\n" + //
					"\n" + //
					"</script>\n" + //
					"</div>\n" + //
					"\n" + //
					"</body>\n" + //
					"</html>";//

	protected final static String PLOTLY_IFRAME = //
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
					"\n" + //
					"<!DOCTYPE html PUBLIC\n" + //
					"  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n" + //
					"  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + //
					"\n" + //
					"<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
					+ //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>Plotly</title>\n" + //
					"\n" + //
					"   <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n" + //
					"</head>\n" + //
					"<body>\n" + //
					"<div id='plotly' ></div>\n" + //
					"`1`\n" + //
					"</body>\n" + //
					"</html>";//

	protected final static String VISJS_IFRAME = //
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
					"\n" + //
					"<!DOCTYPE html PUBLIC\n" + //
					"  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n" + //
					"  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + //
					"\n" + //
					"<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
					+ //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>VIS-NetWork</title>\n" + //
					"\n" + //
					"  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js\"></script>\n"
					+ //
					"</head>\n" + //
					"<body>\n" + //
					"\n" + //
					"<div id=\"vis\" style=\"width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
					+ //
					"<script type=\"text/javascript\">\n" + //
					"`1`\n" + //
					"  var container = document.getElementById('vis');\n" + //
					"  var data = {\n" + //
					"    nodes: nodes,\n" + //
					"    edges: edges\n" + //
					"  };\n" + //
					"`2`\n" + //
					"  var network = new vis.Network(container, data, options);\n" + //
					"</script>\n" + //
					"</div>\n" + //
					"</body>\n" + //
					"</html>";//

	private static void addElementData(String soundex, String value) {
		ArrayList<IPod> list = SOUNDEX_MAP.get(soundex);
		if (list == null) {
			list = new ArrayList<IPod>();
			list.add(new ElementDataPod(value));
			SOUNDEX_MAP.put(soundex, list);
		} else {
			list.add(new ElementDataPod(value));
		}
	}

	/** package private */
	static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String title, String scanner, int formats,
			ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		temp.add(node);
		createJSONFormat(node, engine, outExpr, formats);
	}

	/** package private */
	static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext, String title, String scanner,
			int formats, ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		temp.add(node);
		createJSONFormat(node, engine, outExpr, plaintext, "", formats);
	}

	/** package private */
	static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext, String sinput, String title,
			String scanner, int formats, ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		temp.add(node);
		createJSONFormat(node, engine, outExpr, plaintext, sinput, formats);
	}

	/** package private */
	static void addSymjaPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String title, String scanner, int formats,
			ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		temp.add(node);
		createJSONFormat(node, engine, StringFunctions.inputForm(inExpr), outExpr, formats);
	}

	/** package private */
	static void addSymjaPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext, String title,
			String scanner, int formats, ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		temp.add(node);
		createJSONFormat(node, engine, plaintext, StringFunctions.inputForm(inExpr), outExpr, formats);
	}

	/**
	 * Create a truth table for logic formulas with less equal 5 variables
	 * 
	 * @param podsArray
	 * @param booleanFormula
	 * @param variables
	 * @param formats
	 * @param mapper
	 * @param engine
	 * @return
	 */
	private static int booleanPods(ArrayNode podsArray, IExpr booleanFormula, IAST variables, int formats,
			ObjectMapper mapper, EvalEngine engine) {
		int htmlFormats = formats | HTML;
		if ((htmlFormats & PLAIN) == PLAIN) {
			htmlFormats ^= PLAIN;
		}
		int numpods = 0;
		if (variables.argSize() > 0 && variables.argSize() <= 5) {
			IExpr outExpr = F.BooleanTable(F.Append(variables, booleanFormula), variables);
			IExpr podOut = engine.evaluate(outExpr);

			int[] dim = podOut.isMatrix();
			if (dim != null && dim[0] > 0 && dim[1] > 0) {
				IAST matrix = (IAST) podOut;
				int rowDimension = dim[0];
				int columnDimension = dim[1];
				StringBuilder tableForm = new StringBuilder();
				tableForm.append("<table style=\"border:solid 1px;\">");
				tableForm.append("<thead><tr>");
				for (int i = 1; i < variables.size(); i++) {
					tableForm.append("<th>");
					tableForm.append(variables.get(i).toString());
					tableForm.append("</th>");
				}
				tableForm.append("<th>");
				tableForm.append(booleanFormula.toString());
				tableForm.append("</th>");
				tableForm.append("</tr></thead>");

				tableForm.append("<tbody><tr>");
				for (int i = 0; i < rowDimension; i++) {
					tableForm.append("<tr>");
					for (int j = 0; j < columnDimension; j++) {
						IExpr x = matrix.getPart(i + 1, j + 1);
						tableForm.append("<td>");
						tableForm.append(x.isTrue() ? "T" : x.isFalse() ? "F" : x.toString());
						tableForm.append("</td>");
					}
					tableForm.append("</tr>");
				}
				tableForm.append("</tr></tbody>");
				tableForm.append("</table>");
				addSymjaPod(podsArray, outExpr, podOut, tableForm.toString(), "Truth table", "Boolean", htmlFormats,
						mapper, engine);
				numpods++;
			}
		}

		IExpr outExpr = F.SatisfiabilityInstances(booleanFormula, variables, F.C1);
		IExpr podOut = engine.evaluate(outExpr);
		int[] dim = podOut.isMatrix();
		if (dim != null && dim[0] > 0 && dim[1] > 0) {
			IAST matrix = (IAST) podOut;
			int rowDimension = dim[0];
			int columnDimension = dim[1];

			StringBuilder tableForm = new StringBuilder();
			tableForm.append("<table style=\"border:solid 1px;\">");
			tableForm.append("<thead><tr>");
			for (int i = 1; i < variables.size(); i++) {
				tableForm.append("<th>");
				tableForm.append(variables.get(i).toString());
				tableForm.append("</th>");
			}
			tableForm.append("</tr></thead>");

			tableForm.append("<tbody><tr>");
			for (int i = 0; i < rowDimension; i++) {
				tableForm.append("<tr>");
				for (int j = 0; j < columnDimension; j++) {
					IExpr x = matrix.getPart(i + 1, j + 1);
					tableForm.append("<td>");
					tableForm.append(x.isTrue() ? "T" : x.isFalse() ? "F" : x.toString());
					tableForm.append("</td>");
				}
				tableForm.append("</tr>");
			}
			tableForm.append("</tr></tbody>");
			tableForm.append("</table>");

			addSymjaPod(podsArray, outExpr, podOut, tableForm.toString(), "Satisfiability instance", "Boolean",
					htmlFormats, mapper, engine);
			numpods++;
		}

		return numpods;
	}

	static ObjectNode createJSONErrorString(String str) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode outJSON = mapper.createObjectNode();
		outJSON.put("prefix", "Error");
		outJSON.put("message", Boolean.TRUE);
		outJSON.put("tag", "syntax");
		outJSON.put("symbol", "General");
		outJSON.put("text", "<math><mrow><mtext>" + str + "</mtext></mrow></math>");

		ObjectNode resultsJSON = mapper.createObjectNode();
		resultsJSON.putNull("line");
		resultsJSON.putNull("result");

		ArrayNode temp = mapper.createArrayNode();
		temp.add(outJSON);
		resultsJSON.putPOJO("out", temp);

		temp = mapper.createArrayNode();
		temp.add(resultsJSON);
		ObjectNode json = mapper.createObjectNode();
		json.putPOJO("results", temp);
		return json;
	}

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr, int formats) {
		createJSONFormat(json, engine, outExpr, null, "", formats);
	}

	/**
	 * 
	 * @param json
	 * @param engine
	 * @param outExpr
	 * @param plainText
	 *            text which should obligatory be used for plaintext format
	 * @param sinput
	 *            Symja input string
	 * @param formats
	 */
	private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr, String plainText,
			String sinput, int formats) {

		String encodedPlainText = plainText == null ? null : Encode.forHtmlContent(plainText);
		if ((formats & HTML) != 0x00) {
			if (encodedPlainText != null && encodedPlainText.length() > 0) {
				json.put(HTML_STR, encodedPlainText);
			}
		}

		if ((formats & PLAIN) != 0x00) {
			if (encodedPlainText != null && encodedPlainText.length() > 0) {
				json.put(PLAIN_STR, encodedPlainText);
			} else {
				if (outExpr.isPresent()) {
					String exprStr = Encode.forHtmlContent(outExpr.toString());
					json.put(PLAIN_STR, exprStr);
				}
			}
		}
		if ((formats & SYMJA) != 0x00) {
			if (sinput != null && sinput.length() > 0) {
				String encodedSInput = Encode.forHtmlContent(sinput);
				json.put(SYMJA_STR, encodedSInput);
			}
		}
		if ((formats & MATHML) != 0x00) {
			if (outExpr.isPresent()) {
				StringWriter stw = new StringWriter();
				MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
				if (!mathUtil.toMathML(F.HoldForm(outExpr), stw, true)) {
					// return createJSONErrorString("Max. output size exceeded " +
					// Config.MAX_OUTPUT_SIZE);
				} else {
					json.put(MATHML_STR, stw.toString());
				}
			}
		}
		if ((formats & LATEX) != 0x00) {
			if (outExpr.isPresent()) {
				StringWriter stw = new StringWriter();
				TeXUtilities texUtil = new TeXUtilities(engine, engine.isRelaxedSyntax());
				if (!texUtil.toTeX(F.HoldForm(outExpr), stw)) {
					//
				} else {
					json.put(LATEX_STR, stw.toString());
				}
			}
		}
		if ((formats & MARKDOWN) != 0x00) {
			if (encodedPlainText != null && encodedPlainText.length() > 0) {
				json.put(MARKDOWN_STR, encodedPlainText);
			} else {

			}
		}
		if ((formats & MATHCELL) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				try {
					String html = MATHCELL_IFRAME;
					html = StringUtils.replace(html, "`1`", plainText);
					html = StringEscapeUtils.escapeHtml4(html);
					html = "<iframe srcdoc=\"" + html
							+ "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
					json.put(MATHCELL_STR, html);
				} catch (Exception ex) {
					if (FEConfig.SHOW_STACKTRACE) {
						ex.printStackTrace();
					}
				}

			} else {

			}
		}
		if ((formats & JSXGRAPH) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				try {
					String html = JSXGRAPH_IFRAME;
					html = StringUtils.replace(html, "`1`", plainText);
					html = StringEscapeUtils.escapeHtml4(html);
					html = "<iframe srcdoc=\"" + html
							+ "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
					json.put(JSXGRAPH_STR, html);
				} catch (Exception ex) {
					if (FEConfig.SHOW_STACKTRACE) {
						ex.printStackTrace();
					}
				}

			} else {

			}
		}

		if ((formats & PLOTLY) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				try {
					String html = PLOTLY_IFRAME;
					html = StringUtils.replace(html, "`1`", plainText);
					html = StringEscapeUtils.escapeHtml4(html);
					html = "<iframe srcdoc=\"" + html
							+ "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
					json.put(PLOTLY_STR, html);
				} catch (Exception ex) {
					if (FEConfig.SHOW_STACKTRACE) {
						ex.printStackTrace();
					}
				}

			} else {

			}
		}

		if ((formats & VISJS) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				try {
					String html = VISJS_IFRAME;
					html = StringUtils.replace(html, "`1`", plainText);
					html = StringEscapeUtils.escapeHtml4(html);
					html = "<iframe srcdoc=\"" + html
							+ "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
					json.put(VISJS_STR, html);
				} catch (Exception ex) {
					if (FEConfig.SHOW_STACKTRACE) {
						ex.printStackTrace();
					}
				}

			} else {

			}
		}
	}

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, String sinput, IExpr outExpr,
			int formats) {
		createJSONFormat(json, engine, outExpr, null, sinput, formats);
	}

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, String plaintext, String sinput,
			IExpr outExpr, int formats) {
		createJSONFormat(json, engine, outExpr, plaintext, sinput, formats);
	}

	public static ObjectNode createResult(String inputStr, int formats) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode messageJSON = mapper.createObjectNode();

		ObjectNode queryresult = mapper.createObjectNode();
		messageJSON.putPOJO("queryresult", queryresult);
		queryresult.put("success", "false");
		queryresult.put("error", "false");
		queryresult.put("numpods", 0);
		queryresult.put("version", "0.1");

		boolean error = false;
		int numpods = 0;
		IExpr inExpr = F.Null;
		IExpr outExpr = F.Null;
		EvalEngine engine = EvalEngine.get();
		try {
			ArrayNode podsArray = mapper.createArrayNode();
			inExpr = parseInput(inputStr, engine);
			if (inExpr.isPresent()) {
				long numberOfLeaves = inExpr.leafCount();
				if (numberOfLeaves < Config.MAX_INPUT_LEAVES) {
					outExpr = engine.evaluate(inExpr);
					// if (inExpr.isNumericFunction()) {
					// outExpr = engine.evaluate(inExpr);
					// }

					IExpr podOut = outExpr;
					// if (podOut.isAST(F.JSFormData)) {
					addSymjaPod(podsArray, inExpr, inExpr, "Input", "Identity", formats, mapper, engine);
					// } else {
					// addSymjaPod(podsArray, inExpr, podOut, "Input", "Identity", formats, mapper,
					// engine);
					// }
					numpods++;

					IExpr numExpr = F.NIL;
					IExpr evaledNumExpr = F.NIL;
					if (outExpr.isNumericFunction()) {
						numExpr = F.N(inExpr);
						evaledNumExpr = engine.evaluate(F.N(outExpr));
					}
					if (outExpr.isNumber() || //
							outExpr.isQuantity()) {
						if (outExpr.isInteger()) {
							numpods += integerPods(podsArray, inExpr, (IInteger) outExpr, formats, mapper, engine);
							resultStatistics(queryresult, error, numpods, podsArray);
							return messageJSON;
						} else {
							podOut = outExpr;
							if (outExpr.isRational()) {
								addSymjaPod(podsArray, inExpr, podOut, "Exact result", "Rational", formats, mapper,
										engine);
								numpods++;
							}

							if (evaledNumExpr.isInexactNumber() || //
									evaledNumExpr.isQuantity()) {
								addSymjaPod(podsArray, numExpr, evaledNumExpr, "Decimal form", "Numeric", formats,
										mapper, engine);
								numpods++;
							}

							if (outExpr.isFraction()) {
								IFraction frac = (IFraction) outExpr;
								if (!frac.integerPart().equals(F.C0)) {
									inExpr = F.List(F.IntegerPart(outExpr), F.FractionalPart(outExpr));
									podOut = engine.evaluate(inExpr);
									String plaintext = podOut.first().toString() + " " + podOut.second().toString();
									addSymjaPod(podsArray, inExpr, podOut, plaintext, "Mixed fraction", "Rational",
											formats, mapper, engine);
									numpods++;

									inExpr = F.ContinuedFraction(outExpr);
									podOut = engine.evaluate(inExpr);
									StringBuilder plainBuf = new StringBuilder();
									if (podOut.isList() && podOut.size() > 1) {
										IAST list = (IAST) podOut;
										plainBuf.append('[');
										plainBuf.append(list.arg1().toString());
										plainBuf.append(';');
										for (int i = 2; i < list.size(); i++) {
											plainBuf.append(' ');
											plainBuf.append(list.get(i).toString());
											if (i < list.size() - 1) {
												plainBuf.append(',');
											}
										}
										plainBuf.append(']');
									}
									addSymjaPod(podsArray, inExpr, podOut, plainBuf.toString(), "Continued fraction",
											"ContinuedFraction", formats, mapper, engine);
									numpods++;
								}
							}

							resultStatistics(queryresult, error, numpods, podsArray);
							return messageJSON;
						}
					} else {
						if (outExpr.isAST(F.Plot, 2) && outExpr.first().isList()) {
							outExpr = outExpr.first();
						}
						if (outExpr.isList()) {
							IAST list = (IAST) outExpr;
							ListPod listPod = new ListPod(list);
							numpods += listPod.addJSON(mapper, podsArray, formats, engine);
						}

						if (evaledNumExpr.isInexactNumber() || //
								evaledNumExpr.isQuantity()) {
							addSymjaPod(podsArray, numExpr, evaledNumExpr, "Decimal form", "Numeric", formats, mapper,
									engine);
							numpods++;
						}

						if (outExpr.isSymbol() || outExpr.isString()) {
							String inputWord = outExpr.toString();
							StringBuilder buf = new StringBuilder();
							if (Documentation.getMarkdown(buf, inputWord)) {
								DocumentationPod.addDocumentationPod(new DocumentationPod((ISymbol) outExpr), mapper,
										podsArray, buf, formats);
								numpods++;
								resultStatistics(queryresult, error, numpods, podsArray);
								return messageJSON;
							} else {
								ArrayList<IPod> soundsLike = listOfPods(inputWord);
								if (soundsLike != null) {
									boolean evaled = false;
									for (int i = 0; i < soundsLike.size(); i++) {
										IPod pod = soundsLike.get(i);
										if (pod.keyWord().equalsIgnoreCase(inputWord)) {
											int numberOfEntries = pod.addJSON(mapper, podsArray, formats, engine);
											if (numberOfEntries > 0) {
												numpods += numberOfEntries;
												evaled = true;
												break;
											}
										}
									}
									if (!evaled) {
										for (int i = 0; i < soundsLike.size(); i++) {
											IPod pod = soundsLike.get(i);
											int numberOfEntries = pod.addJSON(mapper, podsArray, formats, engine);
											if (numberOfEntries > 0) {
												numpods += numberOfEntries;
											}
										}
									}
									resultStatistics(queryresult, error, numpods, podsArray);
									return messageJSON;
								}
							}
						} else {
							if (inExpr.isAST(F.D, 2, 3)) {
								if (inExpr.isAST1()) {
									VariablesSet varSet = new VariablesSet(inExpr.first());
									IAST variables = varSet.getVarList();
									IASTAppendable result = ((IAST) inExpr).copyAppendable();
									result.appendArgs(variables);
									inExpr = result;
								}
								outExpr = engine.evaluate(inExpr);
								podOut = outExpr;
								addSymjaPod(podsArray, inExpr, podOut, "Derivative", "Derivative", formats, mapper,
										engine);
								numpods++;

								if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
									inExpr = F.TrigToExp(outExpr);
									podOut = engine.evaluate(inExpr);
									if (!F.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
										addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
												formats, mapper, engine);
										numpods++;
									}
								}
								resultStatistics(queryresult, error, numpods, podsArray);
								return messageJSON;
							} else if (inExpr.isAST(F.Integrate, 2, 3)) {
								if (inExpr.isAST1()) {
									VariablesSet varSet = new VariablesSet(inExpr.first());
									IAST variables = varSet.getVarList();
									IASTAppendable result = ((IAST) inExpr).copyAppendable();
									result.appendArgs(variables);
									inExpr = result;
								}
								outExpr = engine.evaluate(inExpr);
								podOut = outExpr;
								addSymjaPod(podsArray, inExpr, podOut, "Integration", "Integral", formats, mapper,
										engine);
								numpods++;

								if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
									inExpr = F.TrigToExp(outExpr);
									podOut = engine.evaluate(inExpr);
									if (!F.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
										addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
												formats, mapper, engine);
										numpods++;
									}
								}
								resultStatistics(queryresult, error, numpods, podsArray);
								return messageJSON;
							} else if (inExpr.isAST(F.Solve, 2, 4)) {
								if (inExpr.isAST1()) {
									VariablesSet varSet = new VariablesSet(inExpr.first());
									IAST variables = varSet.getVarList();
									IASTAppendable result = ((IAST) inExpr).copyAppendable();
									result.append(variables);
									inExpr = result;
								}
								outExpr = engine.evaluate(inExpr);
								podOut = outExpr;
								addSymjaPod(podsArray, inExpr, podOut, "Solve equation", "Solver", formats, mapper,
										engine);
								numpods++;

								if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
									inExpr = F.TrigToExp(outExpr);
									podOut = engine.evaluate(inExpr);
									if (!F.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
										addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
												formats, mapper, engine);
										numpods++;
									}
								}
								resultStatistics(queryresult, error, numpods, podsArray);
								return messageJSON;
							} else {
								IExpr expr = inExpr;
								// outExpr = engine.evaluate(expr);

								if (outExpr.isAST(F.JSFormData, 3)) {
									podOut = outExpr;
									int form = internFormat(SYMJA, podOut.second().toString());
									addPod(podsArray, inExpr, podOut, podOut.first().toString(),
											StringFunctions.inputForm(inExpr), "Function", "Plotter", form, mapper,
											engine);
									numpods++;
								} else if (outExpr instanceof GraphExpr) {
									String javaScriptStr = GraphFunctions.graphToJSForm((GraphExpr) outExpr);
									if (javaScriptStr != null) {
										String html = VISJS_IFRAME;
										html = StringUtils.replace(html, "`1`", javaScriptStr);
										html = StringUtils.replace(html, "`2`", //
												"  var options = { };\n" //
										);
										// html = StringEscapeUtils.escapeHtml4(html);
										int form = internFormat(SYMJA, "visjs");
										addPod(podsArray, inExpr, podOut, html, "Graph data", "Graph", form, mapper,
												engine);
										numpods++;
									}
								} else {
									IExpr head = outExpr.head();
									if (head instanceof IBuiltInSymbol && outExpr.size() > 1) {
										IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
										if (evaluator instanceof IDistribution) {
											// if (evaluator instanceof IDiscreteDistribution) {
											int snumpods = statisticsPods(podsArray, (IAST) outExpr, podOut, formats,
													mapper, engine);
											numpods += snumpods;
										}
									}

									VariablesSet varSet = new VariablesSet(outExpr);
									IAST variables = varSet.getVarList();
									if (outExpr.isBooleanFormula()) {
										numpods += booleanPods(podsArray, outExpr, variables, formats, mapper, engine);
									}
									if (outExpr.isAST(F.Equal, 3)) {
										IExpr arg1 = outExpr.first();
										IExpr arg2 = outExpr.second();
										if (arg1.isNumericFunction(varSet) && //
												arg2.isNumericFunction(varSet)) {
											if (variables.size() == 2) {
												IExpr plot2D = F.Plot(F.List(arg1, arg2),
														F.List(variables.arg1(), F.num(-20), F.num(20)));
												podOut = engine.evaluate(plot2D);
												if (podOut.isAST(F.JSFormData, 3)) {
													int form = internFormat(SYMJA, podOut.second().toString());
													addPod(podsArray, inExpr, podOut, podOut.first().toString(),
															StringFunctions.inputForm(plot2D), "Function", "Plotter",
															form, mapper, engine);
													numpods++;
												}
											}
											if (!arg1.isZero() && //
													!arg2.isZero()) {
												inExpr = F.Equal(engine.evaluate(F.Subtract(arg1, arg2)), F.C0);
												podOut = inExpr;
												addSymjaPod(podsArray, inExpr, podOut, "Alternate form",
														"Simplification", formats, mapper, engine);
												numpods++;
											}
											inExpr = F.Solve(F.binaryAST2(F.Equal, arg1, arg2), variables);
											podOut = engine.evaluate(inExpr);
											addSymjaPod(podsArray, inExpr, podOut, "Solution", "Reduce", formats,
													mapper, engine);
											numpods++;
										}

										resultStatistics(queryresult, error, numpods, podsArray);
										return messageJSON;
									} else {
										if (!inExpr.equals(outExpr)) {
											addSymjaPod(podsArray, inExpr, outExpr, "Result", "Identity", formats,
													mapper, engine);
											numpods++;
										}
									}

									boolean isNumericFunction = outExpr.isNumericFunction(varSet);
									if (isNumericFunction) {
										if (variables.argSize() == 1) {
											IExpr plot2D = F.Plot(outExpr,
													F.List(variables.arg1(), F.num(-7), F.num(7)));
											podOut = engine.evaluate(plot2D);
											if (podOut.isAST(F.JSFormData, 3)) {
												int form = internFormat(SYMJA, podOut.second().toString());
												addPod(podsArray, inExpr, podOut, podOut.first().toString(),
														StringFunctions.inputForm(plot2D), "Function", "Plotter", form,
														mapper, engine);
												numpods++;
											}
										} else if (variables.argSize() == 2) {
											IExpr plot3D = F.Plot3D(outExpr,
													F.List(variables.arg1(), F.num(-3.5), F.num(3.5)),
													F.List(variables.arg2(), F.num(-3.5), F.num(3.5)));
											podOut = engine.evaluate(plot3D);
											if (podOut.isAST(F.JSFormData, 3)) {
												int form = internFormat(SYMJA, podOut.second().toString());
												addPod(podsArray, inExpr, podOut, podOut.first().toString(),
														StringFunctions.inputForm(plot3D), "3D plot", "Plot", form,
														mapper, engine);
												numpods++;
											}
										}
									}
									if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
										inExpr = F.TrigToExp(outExpr);
										podOut = engine.evaluate(inExpr);
										if (!F.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
											addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
													formats, mapper, engine);
											numpods++;
										}
									}

									if (isNumericFunction && variables.argSize() == 1) {
										if (outExpr.isPolynomial(variables)) {
											inExpr = F.Factor(outExpr);
											podOut = engine.evaluate(inExpr);
											addSymjaPod(podsArray, inExpr, podOut, "Factor", "Polynomial", formats,
													mapper, engine);
											numpods++;
										}
										inExpr = F.D(outExpr, variables.arg1());
										podOut = engine.evaluate(inExpr);
										addSymjaPod(podsArray, inExpr, podOut, "Derivative", "Derivative", formats,
												mapper, engine);
										numpods++;

										inExpr = F.Integrate(outExpr, variables.arg1());
										podOut = engine.evaluate(inExpr);
										addSymjaPod(podsArray, inExpr, podOut, "Indefinite integral", "Integral",
												formats, mapper, engine);
										numpods++;
									}
								}
								if (numpods == 1) {
									// only Identity pod was appended
									addSymjaPod(podsArray, expr, outExpr, "Evaluated result", "Expression", formats,
											mapper, engine);
									numpods++;
								}

								resultStatistics(queryresult, error, numpods, podsArray);
								return messageJSON;
							}
						}
					}
					if (numpods > 0) {
						resultStatistics(queryresult, error, numpods, podsArray);
						return messageJSON;
					}
				}
			}
		} catch (RuntimeException rex) {
			rex.printStackTrace();
			error = true;
			outExpr = F.$Aborted;
		}

		queryresult.put("error", error ? "true" : "false");
		return messageJSON;
	}

	public static String errorJSON(String code, String msg) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode messageJSON = mapper.createObjectNode();
		ObjectNode queryresult = mapper.createObjectNode();
		messageJSON.putPOJO("queryresult", queryresult);
		queryresult.put("success", "false");
		ObjectNode error = mapper.createObjectNode();
		queryresult.putPOJO("error", error);
		error.put("code", code);
		error.put("msg", msg);
		queryresult.put("numpods", 0);
		queryresult.put("version", "0.1");
		return messageJSON.toString();
	}

	private static IASTAppendable flattenTimes(final IAST ast) {
		if (ast.isTimes() && ast.isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
			IASTAppendable result = flattenTimesRecursive(ast);
			if (result.isPresent()) {
				result.addEvalFlags(IAST.IS_FLATTENED);
				return result;
			}
		}
		ast.addEvalFlags(IAST.IS_FLATTENED);
		return F.NIL;
	}

	public static IASTAppendable flattenTimesRecursive(final IAST ast) {
		int[] newSize = new int[1];
		newSize[0] = 0;
		boolean[] flattened = new boolean[] { false };
		ast.forEach(expr -> {
			if (ast.isTimes() && ast.isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
				flattened[0] = true;
				newSize[0] += ast.size();
			} else {
				newSize[0]++;
			}
		});
		if (flattened[0]) {
			IASTAppendable result = F.ast(ast.head(), newSize[0], false);
			ast.forEach(expr -> {
				if (expr.isTimes() && ((IAST) expr).isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
					result.appendArgs(flattenTimesRecursive((IAST) expr).orElse((IAST) expr));
				} else {
					result.append(expr);
				}
			});
			return result;
		}
		return F.NIL;
	}

	private static String getStemForm(String term) {

		TokenStream tokenStream = null;

		try {
			StandardTokenizer stdToken = new StandardTokenizer();
			stdToken.setReader(new StringReader(term));

			tokenStream = new PorterStemFilter(stdToken);
			tokenStream.reset();

			// eliminate duplicate tokens by adding them to a set
			Set<String> stems = new HashSet<>();

			CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

			while (tokenStream.incrementToken()) {
				stems.add(token.toString());
			}

			// if stem form was not found or more than 2 stems have been found, return null
			if (stems.size() != 1) {
				return null;
			}

			String stem = stems.iterator().next();

			// if the stem form has non-alphanumerical chars, return null
			if (!stem.matches("[a-zA-Z0-9-]+")) {
				return null;
			}

			return stem;
		} catch (IOException ioe) {

		} finally {
			if (tokenStream != null) {
				try {
					tokenStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static Trie<String, ArrayList<IPod>> initSoundex() {
		Map<String, String> map = AST2Expr.PREDEFINED_SYMBOLS_MAP;

		IAST[] list = ElementData1.ELEMENTS;
		for (int i = 0; i < list.length; i++) {
			String keyWord = list[i].arg3().toString();
			addElementData(list[i].arg2().toString().toLowerCase(), keyWord);
			soundexElementData(list[i].arg3().toString(), keyWord);
		}
		for (int i = 0; i < ID.Zeta; i++) {
			ISymbol sym = F.symbol(i);
			soundexHelp(sym.toString().toLowerCase(), sym);
		}
		// for (Map.Entry<String, String> entry : map.entrySet()) {
		// soundexHelp(entry.getKey(), entry.getKey());
		// }
		// appendSoundex();
		soundexHelp("cosine", F.Cos);
		soundexHelp("sine", F.Sin);
		soundexHelp("integral", F.Integrate);

		return SOUNDEX_MAP;
	}

	private static int integerPods(ArrayNode podsArray, IExpr inExpr, final IInteger outExpr, int formats,
			ObjectMapper mapper, EvalEngine engine) {
		int htmlFormats = formats | HTML;
		if ((htmlFormats & PLAIN) == PLAIN) {
			htmlFormats ^= PLAIN;
		}
		int numpods = 0;
		int intValue = outExpr.toIntDefault();
		IInteger n = outExpr;

		if (!inExpr.equals(outExpr)) {
			addSymjaPod(podsArray, inExpr, outExpr, "Result", "Simplification", formats, mapper, engine);
			numpods++;
		}
		inExpr = outExpr;

		inExpr = F.IntegerName(n, F.stringx("Words"));
		IExpr podOut = engine.evaluateNull(inExpr);
		if (podOut.isPresent()) {
			addSymjaPod(podsArray, inExpr, podOut, "Number name", "Integer", formats, mapper, engine);
			numpods++;
		}

		if (intValue >= net.numericalchameleon.util.romannumerals.RomanNumeral.MIN_VALUE && //
				intValue <= net.numericalchameleon.util.romannumerals.RomanNumeral.MAX_VALUE) {
			inExpr = F.RomanNumeral(n);
			podOut = engine.evaluate(inExpr);
			addSymjaPod(podsArray, inExpr, podOut, "Roman numerals", "Integer", formats, mapper, engine);
			numpods++;
		}

		inExpr = F.BaseForm(outExpr, F.C2);
		podOut = engine.evaluate(inExpr);
		StringBuilder plainText = new StringBuilder();
		if (podOut.isAST(F.Subscript, 3)) {
			plainText.append(podOut.first().toString());
			plainText.append("_");
			plainText.append(podOut.second().toString());
		}
		addSymjaPod(podsArray, inExpr, podOut, plainText.toString(), "Binary form", "Integer", formats, mapper, engine);
		numpods++;

		if (n.bitLength() < 200) {
			inExpr = F.FactorInteger(n);
			podOut = engine.evaluate(inExpr);
			int[] matrixDimension = podOut.isMatrix();
			plainText = new StringBuilder();
			if (n.isProbablePrime()) {
				plainText.append(n.toString());
				plainText.append(" is a prime number.");
				addSymjaPod(podsArray, inExpr, podOut, plainText.toString(), "Prime factorization", "Integer", formats,
						mapper, engine);
				numpods++;
			} else {
				if (matrixDimension[1] == 2) {
					IAST list = (IAST) podOut;
					IASTAppendable times = F.TimesAlloc(podOut.size());
					for (int i = 1; i < list.size(); i++) {
						IExpr arg1 = list.get(i).first();
						IExpr arg2 = list.get(i).second();
						plainText.append(arg1.toString());
						if (!arg2.isOne()) {
							times.append(F.Power(arg1, arg2));
							plainText.append("^");
							plainText.append(arg2.toString());
						} else {
							times.append(arg1);
						}
						if (i < list.size() - 1) {
							plainText.append("*");
						}
					}
					addSymjaPod(podsArray, inExpr, times.oneIdentity0(), plainText.toString(), "Prime factorization",
							"Integer", formats, mapper, engine);
					numpods++;
				}
			}
		}

		IAST range = (IAST) F.Range.of(F.C2, F.C9);
		inExpr = F.Mod(n, range);
		podOut = engine.evaluate(inExpr);
		// StringBuilder tableForm = new StringBuilder();
		// if (podOut.isList()) {
		// IAST list = (IAST) podOut;
		// tableForm.append("|m|");
		// for (int i = 1; i < range.size(); i++) {
		// tableForm.append(range.get(i).toString());
		//// if (i < range.size() - 1) {
		// tableForm.append("|");
		//// }
		// }
		// tableForm.append("\n|-|-|-|-|-|-|-|-|-|\n|");
		// tableForm.append(n.toString());
		// tableForm.append(" mod m |");
		//
		// for (int i = 1; i < list.size(); i++) {
		// tableForm.append(list.get(i).toString());
		//// if (i < range.size() - 1) {
		// tableForm.append("|");
		//// }
		// }
		// }

		if (podOut.isList()) {
			StringBuilder tableForm = new StringBuilder();
			tableForm.append("<table style=\"border:solid 1px;\">");
			tableForm.append("<thead><tr>");

			IAST list = (IAST) podOut;
			tableForm.append("<th>m</th>");
			for (int i = 1; i < range.size(); i++) {
				tableForm.append("<th>");
				tableForm.append(range.get(i).toString());
				tableForm.append("</th>");
			}
			tableForm.append("</tr></thead>");

			tableForm.append("<tbody><tr><td>");
			tableForm.append(n.toString());
			tableForm.append(" mod m");
			tableForm.append("</td>");
			for (int i = 1; i < list.size(); i++) {
				tableForm.append("<td>");
				tableForm.append(list.get(i).toString());
				tableForm.append("</td>");
			}
			tableForm.append("</tr></tbody>");
			tableForm.append("</table>");

			addSymjaPod(podsArray, inExpr, podOut, tableForm.toString(), "Residues modulo small integers", "Integer",
					htmlFormats, mapper, engine);
			numpods++;
		}

		integerPropertiesPod(podsArray, outExpr, podOut, "Properties", "Integer", formats, mapper, engine);
		numpods++;

		if (n.isPositive() && n.isLT(F.ZZ(100))) {
			inExpr = F.Union(F.PowerMod(F.Range(F.C0, F.QQ(n, F.C2)), F.C2, n));
			podOut = engine.evaluate(inExpr);
			addSymjaPod(podsArray, inExpr, podOut, "Quadratic residues modulo " + n.toString(), "Integer", formats,
					mapper, engine);
			numpods++;

			if (n.isProbablePrime()) {
				inExpr = F.Select(F.Range(n.add(F.CN1)),
						F.Function(F.Equal(F.MultiplicativeOrder(F.Slot1, n), F.EulerPhi(n))));
				podOut = engine.evaluate(inExpr);
				addSymjaPod(podsArray, inExpr, podOut, "Primitive roots modulo " + n.toString(), "Integer", formats,
						mapper, engine);
				numpods++;
			}
		}
		return numpods;
	}

	static void integerPropertiesPod(ArrayNode podsArray, IInteger inExpr, IExpr outExpr, String title, String scanner,
			int formats, ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		int numsubpods = 0;
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", numsubpods);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		try {
			if (inExpr.isEven()) {
				ObjectNode node = mapper.createObjectNode();
				temp.add(node);
				createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an even number.",
						StringFunctions.inputForm(F.EvenQ(inExpr)), formats);

				numsubpods++;
			} else {
				ObjectNode node = mapper.createObjectNode();
				temp.add(node);
				createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an odd number.",
						StringFunctions.inputForm(F.OddQ(inExpr)), formats);

				numsubpods++;
			}
			if (inExpr.isProbablePrime()) {
				IExpr primePiExpr = F.PrimePi(inExpr);
				IExpr primePi = engine.evaluate(inExpr);
				if (primePi.isInteger()) {
					ObjectNode node = mapper.createObjectNode();
					temp.add(node);
					createJSONFormat(node, engine, F.NIL,
							inExpr.toString() + " is the " + primePi.toString() + "th prime number.",
							StringFunctions.inputForm(primePiExpr), formats);
					numsubpods++;
				} else {
					ObjectNode node = mapper.createObjectNode();
					temp.add(node);
					createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is a prime number.",
							StringFunctions.inputForm(F.PrimeQ(inExpr)), formats);
					numsubpods++;
				}
			}
		} finally {
			subpodsResult.put("numsubpods", numsubpods);
		}
	}

	/** package private */
	static int internFormat(int intern, String str) {
		if (str.equals(HTML_STR)) {
			intern |= HTML;
		} else if (str.equals(PLAIN_STR)) {
			intern |= PLAIN;
		} else if (str.equals(SYMJA_STR)) {
			intern |= SYMJA;
		} else if (str.equals(MATHML_STR)) {
			intern |= MATHML;
		} else if (str.equals(LATEX_STR)) {
			intern |= LATEX;
		} else if (str.equals(MARKDOWN_STR)) {
			intern |= MARKDOWN;
		} else if (str.equals(MATHCELL_STR)) {
			intern |= MATHCELL;
		} else if (str.equals(JSXGRAPH_STR)) {
			intern |= JSXGRAPH;
		} else if (str.equals(PLOTLY_STR)) {
			intern |= PLOTLY;
		} else if (str.equals(VISJS_STR) || str.equals("treeform")) {
			intern |= VISJS;
		}
		return intern;
	}

	public static int internFormat(String[] formats) {
		int intern = 0;
		for (String str : formats) {
			intern = internFormat(intern, str);
		}
		return intern;
	}

	private static ArrayList<IPod> listOfPods(String inputWord) {
		Map<String, ArrayList<IPod>> map = LAZY_SOUNDEX.get();
		ArrayList<IPod> soundsLike = map.get(inputWord.toLowerCase());
		if (soundsLike == null) {
			soundsLike = map.get(SOUNDEX.encode(inputWord));
		}
		if (soundsLike != null) {
			LevenshteinDistanceComparator ldc = new LevenshteinDistanceComparator(inputWord);
			Collections.sort(soundsLike, ldc);
		}
		return soundsLike;
	}

	/** package private */
	static IExpr parseInput(String inputStr, EvalEngine engine) {
		engine.setPackageMode(false);
		final FuzzyParser parser = new FuzzyParser(engine);
		IExpr inExpr = F.NIL;
		try {
			inExpr = parser.parseFuzzyList(inputStr);
		} catch (SyntaxError serr) {
			// this includes syntax errors
			if (FEConfig.SHOW_STACKTRACE) {
				serr.printStackTrace();
			}
			TeXParser texConverter = new TeXParser(engine);
			inExpr = texConverter.toExpression(inputStr);
		}

		if (inExpr == F.$Aborted) {
			return F.NIL;
		}
		if (inExpr.isList() && inExpr.size() == 2) {
			inExpr = inExpr.first();
		}
		if (inExpr.isTimes() && //
				!inExpr.isNumericFunction() && //
				inExpr.argSize() <= 4) {
			if (((IAST) inExpr).isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
				inExpr = flattenTimes((IAST) inExpr).orElse(inExpr);
				IAST rest = ((IAST) inExpr).setAtClone(0, F.List);
				IASTAppendable specialFunction = F.NIL;
				String stemForm = getStemForm(rest.arg1().toString().toLowerCase());
				IExpr head = rest.head();
				if (stemForm != null) {
					head = STEM_MAP.get(stemForm);
					if (head != null) {
						specialFunction = rest.setAtClone(0, head);
						specialFunction.remove(1);
					}
				}
				if (!specialFunction.isPresent()) {
					stemForm = getStemForm(rest.last().toString().toLowerCase());
					if (stemForm != null) {
						head = STEM_MAP.get(stemForm);
						if (head != null) {
							specialFunction = rest.setAtClone(0, head);
							specialFunction.remove(rest.size() - 1);
						}
					}
				}
				if (specialFunction.isPresent()) {

					if (head != null) {
						if (head == F.UnitConvert) {
							IExpr temp = unitConvert(engine, rest.rest());
							if (temp.isPresent()) {
								return temp;
							}
						} else {

							int i = 1;
							while (i < specialFunction.size()) {
								String argStr = specialFunction.get(i).toString().toLowerCase();
								if (argStr.equalsIgnoreCase("by") || //
										argStr.equalsIgnoreCase("for")) {
									specialFunction.remove(i);
									continue;
								}
								i++;
							}

							return specialFunction;
						}
					}
				}

				if (rest.arg1().toString().equalsIgnoreCase("convert")) {
					rest = inExpr.rest();
				}
				if (rest.argSize() > 2) {
					rest = rest.removeIf(x -> x.toString().equals("in"));
				}
				IExpr temp = unitConvert(engine, rest);
				if (temp.isPresent()) {
					return temp;
				}
			}
		}
		return inExpr;

	}

	private static void resultStatistics(ObjectNode queryresult, boolean error, int numpods, ArrayNode podsArray) {
		queryresult.putPOJO("pods", podsArray);
		queryresult.put("success", "true");
		queryresult.put("error", error ? "true" : "false");
		queryresult.put("numpods", numpods);
	}

	private static void soundexElementData(String key, String value) {
		String soundex = SOUNDEX.encode(key);
		addElementData(soundex, value);
	}

	private static void soundexHelp(String key, ISymbol value) {
		String soundex = SOUNDEX.encode(key);
		ArrayList<IPod> list = SOUNDEX_MAP.get(soundex);
		if (list == null) {
			list = new ArrayList<IPod>();
			list.add(new DocumentationPod(value));
			SOUNDEX_MAP.put(soundex, list);
		} else {
			list.add(new DocumentationPod(value));
		}
	}

	private static int statisticsPods(ArrayNode podsArray, IAST inExpr, IExpr outExpr, int formats, ObjectMapper mapper,
			EvalEngine engine) {
		int numpods = 0;
		int htmlFormats = formats | HTML;
		if ((htmlFormats & PLAIN) == PLAIN) {
			htmlFormats ^= PLAIN;
		}
		IExpr mean = F.Mean.ofNIL(engine, inExpr);
		if (mean.isPresent()) {
			// IExpr mode = F.Mode.of(engine, inExpr);
			IExpr standardDeviation = F.StandardDeviation.of(engine, inExpr);
			IExpr variance = F.Variance.of(engine, inExpr);
			IExpr skewness = F.Skewness.of(engine, inExpr);
			IExpr podOut = F.List(//
					F.Mean(inExpr), //
					F.StandardDeviation(inExpr), //
					F.Variance(inExpr), //
					F.Skewness(inExpr));

			StringBuilder tableForm = new StringBuilder();
			tableForm.append("<table style=\"border:solid 1px;\">");
			tableForm.append("<tbody>");

			tableForm.append("<tr>");
			tableForm.append("<td>mean</td>");
			tableForm.append("<td>");
			tableForm.append(mean.toString());
			tableForm.append("</td>");
			tableForm.append("<tr>");

			tableForm.append("<tr>");
			tableForm.append("<td>standard deviation</td>");
			tableForm.append("<td>");
			tableForm.append(standardDeviation.toString());
			tableForm.append("</td>");
			tableForm.append("<tr>");

			tableForm.append("<tr>");
			tableForm.append("<td>variance</td>");
			tableForm.append("<td>");
			tableForm.append(variance.toString());
			tableForm.append("</td>");
			tableForm.append("<tr>");

			tableForm.append("<tr>");
			tableForm.append("<td>skewness</td>");
			tableForm.append("<td>");
			tableForm.append(skewness.toString());
			tableForm.append("</td>");
			tableForm.append("<tr>");

			tableForm.append("</tbody>");
			tableForm.append("</table>");

			addSymjaPod(podsArray, F.List(), podOut, tableForm.toString(), "Statistical properties", "Statistics",
					htmlFormats, mapper, engine);
			numpods++;

			inExpr = F.PDF(outExpr, F.x);
			podOut = engine.evaluate(inExpr);
			addSymjaPod(podsArray, inExpr, podOut, "Probability density function (PDF)", "Statistics", formats, mapper,
					engine);
			numpods++;

			inExpr = F.CDF(outExpr, F.x);
			podOut = engine.evaluate(inExpr);
			addSymjaPod(podsArray, inExpr, podOut, "Cumulative distribution function (CDF)", "Statistics", formats,
					mapper, engine);
		}
		numpods++;
		return numpods;
	}

	/**
	 * Test if this is a unit conversion question? If YES return <code>F.UnitConvert(...)</code> expression
	 * 
	 * @param engine
	 * @param rest
	 * @return <code>F.NIL</code> if it's not a <code>F.UnitConvert(...)</code> expression
	 */
	private static IExpr unitConvert(EvalEngine engine, IAST rest) {
		if (rest.argSize() == 3) {
			// check("UnitConvert(Quantity(10^(-6), \"MOhm\"),\"Ohm\" )", //
			// "1[Ohm]");
			// check("UnitConvert(Quantity(1, \"nmi\"),\"km\" )", //
			// "463/250[km]");
			IExpr inExpr = F.Quantity(rest.arg1(), F.stringx(rest.arg2().toString()));
			IExpr q1 = engine.evaluate(inExpr);
			if (q1.isQuantity()) {
				return F.UnitConvert(inExpr, F.stringx(rest.last().toString()));
			}
		}
		return F.NIL;
	}
}
