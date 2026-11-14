package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Classify, ClassifierFunction and ClassifierMeasurements. */
public class ClassifyTest extends ExprEvaluatorTestCase {

  @Test
  public void testTrainingDataForms() {
    // a list of rules, the classes in canonical order
    check("c = Classify({7 -> \"q\", 4 -> \"p\", 6.5 -> \"q\", 5 -> \"p\", 8 -> \"r\"}); "
        + "{Information(c, \"Classes\"), Information(c, \"Method\"), Information(c, \"ExampleNumber\")}", //
        "{{p,q,r},LogisticRegression,5}");
    // parallel lists
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}); "
        + "{c(-3.5), c(21.5), Information(c, \"Classes\")}", //
        "{u,v,{u,v}}");
    // an association of the classes with their examples
    check("c = Classify(<|\"cold\" -> {-8, -6, -5}, \"warm\" -> {18, 20, 25}|>); "
        + "{c(-7), c(19), Information(c, \"Classes\")}", //
        "{cold,warm,{cold,warm}}");
    check("Classify({-1 -> neg, 1 -> pos, 2 -> pos}, 0.9)", //
        "pos");
    check("Classify({-1 -> neg, 1 -> pos, 2 -> pos}, -0.9, \"Probabilities\")", //
        "<|neg->0.999971,pos->0.0000288577|>");
  }

  @Test
  public void testListOfExamples() {
    // scalar examples: a list is a list of examples
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}); c({-10, 30, 0})", //
        "{u,v,u}");
    // vector examples: a vector of the training length is one example
    check("c = Classify({{3, 4}, {4, 3}, {30, 40}, {40, 30}} -> {lo, lo, hi, hi}, "
        + "Method -> \"NearestNeighbors\"); {c({5, 5}), c({{2, 2}, {35, 35}})}", //
        "{lo,{lo,hi}}");
    // a symbolic example isn't classified
    check("c = Classify({{3, 4}, {4, 3}, {30, 40}, {40, 30}} -> {lo, lo, hi, hi}); "
        + "{Length(c({s, t})), First(c({s, t}))}", //
        "{1,{s,t}}");
  }

  @Test
  public void testExampleProperties() {
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}); "
        + "{c(-3, \"Decision\"), c(-3, \"Probabilities\"), c(-3, {\"Probability\", v}), "
        + "c(19, \"TopProbabilities\"), c(19, {\"TopProbabilities\", 1})}", //
        "{u,<|u->0.999998,v->1.61735*10^-6|>,1.61735*10^-6,{v->0.999985},{v->0.999985}}");
    // the probabilities add up to one
    check("c = Classify(<|1 -> {{0, 0}, {2, 1}}, 2 -> {{9, 0}, {8, 2}}, 3 -> {{4, 9}, {5, 8}}|>); "
        + "{c({{1, 1}, {9, 1}, {4, 8}}), Chop(Total(c({3, 3}, \"Probabilities\")) - 1)}", //
        "{{1,2,3},0}");
    // the midpoint of a symmetric training set is a tie, decided for the later class
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}); "
        + "{c(9), c(9, \"Probabilities\")}", //
        "{v,<|u->0.5,v->0.5|>}");
  }

  @Test
  public void testNearestNeighbors() {
    // the default number of neighbors has the least leave-one-out cross-entropy; a class with m
    // of the k neighbors gets a probability proportional to m + s*prior, s = 1/2
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}, "
        + "Method -> \"NearestNeighbors\"); {Information(c, \"MethodOption\"), "
        + "c(-10, \"Probabilities\"), c(9, \"Probabilities\")}", //
        "{Method->{NearestNeighbors,NeighborsNumber->2,DistributionSmoothing->0.5},"
            + "<|u->0.9,v->0.1|>,<|u->0.5,v->0.5|>}");
    check("c = Classify({0.5 -> s, 1.5 -> t, 4 -> t, 9 -> w}, "
        + "Method -> {\"NearestNeighbors\", \"NeighborsNumber\" -> 1}); "
        + "{Information(c, \"Method\"), c(0), c(1.2), c(8), c(9, \"Probabilities\"), "
        + "c(9, \"TopProbabilities\")}", //
        "{NearestNeighbors,s,t,w,<|s->0.0952381,t->0.142857,w->0.761905|>,"
            + "{w->0.761905,t->0.142857,s->0.0952381}}");
    check("c = Classify({0.5 -> s, 1.5 -> t, 4 -> t, 9 -> w}, Method -> {\"NearestNeighbors\", "
        + "\"NeighborsNumber\" -> 3, \"DistributionSmoothing\" -> 0}); "
        + "c(3, \"Probabilities\")", //
        "<|s->0.333333,t->0.666667,w->0.0|>");
    check("Check(Classify({0 -> s, 2 -> t}, "
        + "Method -> {\"NearestNeighbors\", \"NeighborsNumber\" -> 0}), bad)", //
        "bad");
    // one neighbor (Floor(Sqrt(2))): of two equally near training examples the earlier one
    check("c = Classify({0 -> s, 2 -> t}, Method -> \"NearestNeighbors\"); "
        + "c(1, \"Probabilities\")", //
        "<|s->0.833333,t->0.166667|>");
  }

  @Test
  public void testTextFeatures() {
    // strings are text, classified by their characters
    check("c = Classify({\"oak\" -> conifer, \"rose\" -> flower, \"pine\" -> conifer, "
        + "\"tulip\" -> flower}); {c(\"oak\"), c(\"tulip\"), c(\"spruce\"), "
        + "Information(c, \"Method\"), Information(c, \"FeatureTypes\")}", //
        "{conifer,flower,flower,Markov,<|f1->Text|>}");
    check("c = Classify({\"aaa\" -> x, \"bbb\" -> y}); "
        + "{c(\"ab\", \"Probabilities\"), c(\"aab\", \"Probabilities\")}", //
        "{<|x->0.5,y->0.5|>,<|x->0.96875,y->0.03125|>}");
    // a number is no text
    check("c = Classify({\"aaa\" -> x, \"bbb\" -> y}); Check(c(5), bad)", //
        "bad");
    check("Check(Classify({1 -> x, 2 -> y}, Method -> \"Markov\"), bad)", //
        "bad");
  }

  @Test
  public void testNominalFeatures() {
    check("c = Classify({{\"x\", 1} -> p, {\"y\", 1} -> q, {\"x\", 2} -> p, {\"y\", 2} -> q}); "
        + "{c({\"y\", 1.5}), Information(c, \"FeatureTypes\")}", //
        "{q,<|f1->Nominal,f2->Numerical|>}");
  }

  @Test
  public void testMethodOption() {
    check("Classify({1 -> \"a\", 2 -> \"b\"}, Method -> \"Unknown\")", //
        "Classify({1->a,2->b},Method->Unknown)");
    check("Check(Classify({1 -> \"a\", 2 -> \"b\"}, Method -> \"Unknown\"), failed)", //
        "failed");
    check("c = Classify({1 -> \"a\", 2 -> \"b\"}, Method -> Automatic); Information(c, \"Method\")", //
        "LogisticRegression");
    check("c = Classify({1 -> \"a\", 2 -> \"b\"}, Method -> {\"NearestNeighbors\"}); Information(c, \"Method\")", //
        "NearestNeighbors");
  }

  @Test
  public void testStringIsAnExample() {
    // a string is an example, not a property name
    check("c = Classify({-4, -3, 20, 21} -> {u, v, v, v}); Check(c(\"Method\"), bad)", //
        "bad");
    check("c = Classify({-4, -3, 20, 21} -> {u, v, v, v}); "
        + "Quiet({Length(c(\"Method\")), First(c(\"Method\"))})", //
        "{1,Method}");
    check("c = Classify({{0, 1}, {9, 8}} -> {u, v}); Check(c(\"Classes\"), bad)", //
        "bad");
    // where the training values are strings, a string property name is classified
    check("c = Classify({\"Classes\" -> u, \"Method\" -> v}); {c(\"Method\"), c(\"Classes\")}", //
        "{v,u}");
    check("c = Classify({-4, -3, 20, 21} -> {u, v, v, v}); "
        + "{Information(c, \"ClassNumber\"), Information(c, \"Unknown\")}", //
        "{2,Missing(UnknownProperty,Unknown)}");
  }

  @Test
  public void testManyParameters() {
    // more than 200 parameters are fitted with L-BFGS; constant components get no weight, so the
    // fit agrees with the Newton fit of the informative component alone
    check("xs = {-4, -3, -2, 0.5, 1, 20, 21, 22}; ys = {u, u, u, v, u, v, v, v}; "
        + "pad = Join({#}, ConstantArray(0, 210)) &; c1 = Classify(xs -> ys); "
        + "c2 = Classify(pad /@ xs -> ys); qs = {-1, 0.7, 5}; "
        + "{c2(pad /@ qs), Max(Abs(Values(c1(qs, \"Probabilities\")) "
        + "- Values(c2(pad /@ qs, \"Probabilities\")))) < 10^-6}", //
        "{{u,u,v},True}");
    check("X = Table(Join({Mod(i, 3)}, Table(Sin(i*j), {j, 1, 120})), {i, 1, 300}); "
        + "Y = Table(Mod(i, 3), {i, 1, 300}); c = Classify(X -> Y); Count(c(X) - Y, 0)", //
        "300");
  }

  @Test
  public void testManyNeighbors() {
    // more neighbors than the automatic choice tries
    check("c = Classify(Join(Range(20), Range(101, 120)) -> Join(Table(a, 20), Table(b, 20)), "
        + "Method -> {\"NearestNeighbors\", \"NeighborsNumber\" -> 30}); "
        + "{c(0, \"Probabilities\"), c(200, \"Probabilities\")}", //
        "{<|a->0.663934,b->0.336066|>,<|a->0.336066,b->0.663934|>}");
  }

  @Test
  public void testInformation() {
    // the classifier may be given by any expression
    check("cs = {Classify({1, 2, 10, 11} -> {a, a, b, b})}; "
        + "{Information(cs[[1]], \"Method\"), Information(First(cs), \"Classes\"), "
        + "Information(Classify({1, 2, 10, 11} -> {a, a, b, b}), \"ClassNumber\")}", //
        "{LogisticRegression,{a,b},2}");
    check("c = Classify({1, 2, 10, 11} -> {a, a, b, b}); "
        + "{Information(c, \"FeatureNumber\"), Information(c, \"Nope\")}", //
        "{1,Missing(UnknownProperty,Nope)}");
    // other symbols are unchanged
    check("g(x_) := x^2; Information(g, \"Name\")", //
        "g");
  }

  @Test
  public void testSingleClass() {
    // at least two classes are needed
    check("Check(Classify({3 -> k, 4 -> k}), bad)", //
        "bad");
    check("Quiet(Classify({3 -> k, 4 -> k}))", //
        "Classify({3->k,4->k})");
  }

  @Test
  public void testNeighborsAndTies() {
    // one neighbor is the least cross-entropy here
    check("c = Classify({-6, -5, 7, 8} -> {lo, lo, hi, hi}, Method -> \"NearestNeighbors\"); "
        + "Information(c, \"MethodOption\")", //
        "Method->{NearestNeighbors,NeighborsNumber->1,DistributionSmoothing->0.5}");
    // a tie of probabilities goes to the later class
    check("c = Classify({-6, -5, 7, 8} -> {lo, lo, hi, hi}, "
        + "Method -> {\"NearestNeighbors\", \"NeighborsNumber\" -> 2}); "
        + "{c(1, \"Probabilities\"), c(1), c(1, \"TopProbabilities\")}", //
        "{<|hi->0.5,lo->0.5|>,lo,{lo->0.5,hi->0.5}}");
    // known methods which aren't implemented
    check("Check(Classify({1 -> a, 2 -> b}, Method -> \"RandomForest\"), bad)", //
        "bad");
  }

  @Test
  public void testInputFormRoundTrip() {
    check("c = Classify({-4, -3, 20, 21} -> {u, u, v, v}); "
        + "{c, InputForm(c)}", //
        "{ClassifierFunction(<|Method->LogisticRegression,Classes->{u,v},ExampleNumber->4|>),"
            + "ClassifierFunction(<|\"Method\"->{\"LogisticRegression\",\"L2Regularization\"->1.0`*^-6},"
            + "\"TrainingSet\"->{-4->u,-3->u,20->v,21->v}|>)}");
    check("f = ClassifierFunction(<|\"Method\" -> \"NearestNeighbors\", "
        + "\"TrainingSet\" -> {-4 -> u, 21 -> v}|>); f(15)", //
        "v");
    check("c = Classify({-4, -3, 20, 21} -> {u, u, v, v}, "
        + "Method -> {\"NearestNeighbors\", \"NeighborsNumber\" -> 1}); "
        + "f = ToExpression(ToString(InputForm(c))); {f === c, Information(f, \"MethodOption\")}", //
        "{True,Method->{NearestNeighbors,NeighborsNumber->1,DistributionSmoothing->0.5}}");
    // a symbol is no number
    check("c = Classify({-4, -3, 20, 21} -> {u, u, v, v}); Check(c(s), bad)", //
        "bad");
  }

  @Test
  public void testClassifierMeasurements() {
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}); "
        + "m = ClassifierMeasurements(c, {-5 -> u, 0 -> v, 19 -> v, 30 -> u, 25 -> v}); "
        + "{Head(m), m(\"Accuracy\"), m(\"Error\"), m(\"ConfusionMatrix\"), m(\"Recall\"), "
        + "m(\"AccuracyBaseline\")}", //
        "{ClassifierMeasurementsObject,0.6,0.4,{{1,1,0},{1,2,0}},<|u->0.5,v->0.666667|>,0.6}");
    check("c = Classify({-4, -3, -2, 20, 21, 22} -> {u, u, u, v, v, v}); "
        + "{ClassifierMeasurements(c, <|u -> {-1, -7}, v -> {15}|>, \"Accuracy\"), "
        + "ClassifierMeasurements(c, {-1, 15} -> {v, v}, {\"Accuracy\", \"ConfusionMatrix\"})}", //
        "{1.0,{0.5,{{0,0,0},{1,1,0}}}}");
    // a symbolic test example isn't classified
    check("c = Classify({-4, -3, 20, 21} -> {u, u, v, v}); "
        + "Head(ClassifierMeasurements(c, {s -> u}))", //
        "ClassifierMeasurements");
  }
}
