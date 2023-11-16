package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

public class AssociationTest extends ExprEvaluatorTestCase {

  @Test
  public void testAssociation001() {
    check("Head(<|a -> x, b -> y, c -> z|>)", //
        "Association");
    check("<|a -> x, b -> y|>", //
        "<|a->x,b->y|>");
    check("Association({a -> x, b -> y})", //
        "<|a->x,b->y|>");
    check("<|a -> x, b -> y, <|a -> z, d -> t|>|>", //
        "<|a->z,b->y,d->t|>");
    check("<|a -> x, b -> y, c -> <|d -> t|>|>", //
        "<|a->x,b->y,c-><|d->t|>|>");
    check("%(\"s\")", //
        "Missing(KeyAbsent,s)");
    check("<|a -> x, b + c -> y, {<|{}|>, a -> {z}}|>", //
        "<|a->{z},b+c->y|>");
    check("%(a)", //
        "{z}");
    check("<|\"x\" -> 1, {y} -> 1|>", //
        "<|x->1,{y}->1|>");
    check(" %(\"x\")", //
        "1");
  }

  @Test
  public void testAssociateTo() {
    check("aa=42", //
        "42");
    // AssociateTo: aa is not a variable with a value, so its value cannot be changed.
    check("AssociateTo(aa , c->17)", //
        "AssociateTo(aa,c->17)");

    check("assoc = <|\"A\" -> <|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3|>|>", //
        "<|A-><|a->1,b->2,c->3|>|>");
    // for (int i = 0; i < 300000; i++) {
    // check(
    // "AssociateTo(assoc, \"" + i + "\" -> 11);", //
    // "");
    // }
    check("AssociateTo(assoc, \"A\" -> 11)", //
        "<|A->11|>");
    check("assoc", //
        "<|A->11|>");

    check("assoc = Association({a -> 1, b -> 2, c -> 3})", //
        "<|a->1,b->2,c->3|>");
    check("AssociateTo(assoc, a -> 11)", //
        "<|a->11,b->2,c->3|>");
    check("AssociateTo(assoc, d -> 4)", //
        "<|a->11,b->2,c->3,d->4|>");
    check("assoc", //
        "<|a->11,b->2,c->3,d->4|>");

    check("assoc = Association({a -> 1, b -> 2})", //
        "<|a->1,b->2|>");
    check("assoc2 = <|c -> 3|>", //
        "<|c->3|>");
    check("AssociateTo(assoc, assoc2)", //
        "<|a->1,b->2,c->3|>");
    check("AppendTo(assoc, assoc2)", //
        "<|a->1,b->2,c->3|>");
    check("assoc", //
        "<|a->1,b->2,c->3|>");
    check("assoc2", //
        "<|c->3|>");

    check("a = {Association({a -> 1, b -> 2}), Association({c -> 3, d -> 4})}", //
        "{<|a->1,b->2|>,<|c->3,d->4|>}");
    check("AssociateTo(a[[2]], c->17)", //
        "{<|a->1,b->2|>,<|c->17,d->4|>}");
    check("a", //
        "{<|a->1,b->2|>,<|c->17,d->4|>}");
  }

  @Test
  public void testAssociation() {
    check(" <|{}|>", //
        "<||>");
    check("PossibleZeroQ(RegularExpression(<|a->0,b:>1|>))", //
        "False");
    check("pinfo = <|\"firstName\" -> \"John\", \"lastName\" -> \"Doe\"|>", //
        "<|firstName->John,lastName->Doe|>");
    check("pinfo[[\"lastName\"]]", //
        "Doe");
    check("pinfo[[{\"lastName\"}]]", //
        "<|lastName->Doe|>");
    check("pinfo[[{\"firstName\", \"lastName\"}]]", //
        "<|firstName->John,lastName->Doe|>");
    check("AppendTo(pinfo, \"lastName\" -> \"Wayne\")", //
        "<|firstName->John,lastName->Wayne|>");

    check("<|\"a\" -> b, \"c\" -> d|>[\"c\"]", //
        "d");

    check("<|\"a\" -> b, \"c\" -> d|>[[\"c\"]]", //
        "d");

    check("<|a -> x, b -> y, <|a -> z, d -> t|>|>", //
        "<|a->z,b->y,d->t|>");
    check("<|a -> x, b -> y, <|a -> z, d -> t|>|>[\"s\"]", //
        "Missing(KeyAbsent,s)");
    check("<|a -> x, b + c -> y, {<|{}|>, a -> {z}}|>", //
        "<|a->{z},b+c->y|>");
    check("<|<|a -> v|> -> x, <|b -> y, a -> <|c -> z|>, {}, <||>|>, {d}|>[c]", //
        "Association(<|a->v|>->x,<|b->y,a-><|c->z|>|>,{d})[c]");

    check("assoc = <|a -> x, \"b\" -> y, c -> z|>", //
        "<|a->x,b->y,c->z|>");
    check("assoc[\"b\"] = w", //
        "w");
    check("assoc", //
        "<|a->x,b->w,c->z|>");

    check("Association()", //
        "<||>");
    check("Association(a->x)", //
        "<|a->x|>");
    check(
        "Association({f->g,h->o},<|A->1,B->2,C->3,D->4|>,<|a->1,b->2,c->3,d->4,e->5|>,<|U->1,V->2|>)", //
        "<|f->g,h->o,A->1,B->2,C->3,D->4,a->1,b->2,c->3,d->4,e->5,U->1,V->2|>");
    // TODO
    // check("f(<|1 -> 2, 3 -> 4|>) /. f(<|a_ -> b_, ___|>) -> f(a, b)", //
    // "");
    check("<|a -> 6, b -> 2, c -> 3|> +x+2*y+z^3", //
        "<|a->6+x+2*y+z^3,b->2+x+2*y+z^3,c->3+x+2*y+z^3|>");
    check("Sin(<|a -> 6, b -> 2, c -> 3|>)", //
        "<|a->Sin(6),b->Sin(2),c->Sin(3)|>");

    check("f[#u, #v, #u] &[<|\"u\" -> x, \"v\" -> y|>]", //
        "f(x,y,x)");
    check("#y &[<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>]", //
        "2");
    check("#2[\"y\"] &[<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>]", //
        "4");
    check("#2 &[<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>]", //
        "<|x->3,y->4|>");

    check("Extract(<|a -> 1, b -> 2|>, {{Key(a)}, {Key(b)}})", //
        "{1,2}");
    check("{<|\"a\" -> x, \"b\" -> z|>}[[1, \"b\"]]", //
        "z");
    check("{<|a -> x, b -> {y, z}|>, w}[[1, Key(b), 2]]", //
        "z");
    check("<|a -> b, c -> d|>[[Key(a)]]", //
        "b");
    check("<|\"a\" -> b, \"c\" -> d|>[[\"c\"]]", //
        "d");
    check("<|a -> b, c -> d|>[k]", //
        "Missing(KeyAbsent,k)");

    check("KeySort(<|2 -> y, 3 -> z, 1 -> x|>)", //
        "<|1->x,2->y,3->z|>");
    check("KeySort(<|2 -> y, 3 -> z, 1 -> x|>, Greater)", //
        "<|3->z,2->y,1->x|>");

    check("Counts({1,2,3,4,5,6,7,8,9,7,5,4,5,6,7,3,2,1,3,4,5,2,2,2,3,3,3,3,3})", //
        "<|1->2,2->5,3->8,4->3,5->4,6->2,7->3,8->1,9->1|>");
    check("Counts({a,b,c,a})", //
        "<|a->2,b->1,c->1|>");
    // check("Counts(RandomInteger({0, 1}, 100))", //
    // "<|0->55,1->45|>");

    //
    check("ref = <|a -> x, b -> y, c -> z|>", //
        "<|a->x,b->y,c->z|>");
    check("ref[b]=w", //
        "w");
    check("ref", //
        "<|a->x,b->w,c->z|>");
    check("<|a :> (Print(z); 1), b :> (Print(y); 2), c :> (Print(z); 3)|>", //
        "<|a:>(Print(z);1),b:>(Print(y);2),c:>(Print(z);3)|>");
    check("<|a :> (Print(z); 1), b :> (Print(y); 2), c :> (Print(z); 3)|>[b]", //
        "2");
    check("Select(<|a -> 4, b -> 2, c -> 1, d -> 5|>, # > 3 &)", //
        "<|a->4,d->5|>");

    check(" <|a :> 1 + 1, b -> Nothing|> ", //
        "<|a:>1+1,b->Nothing|>");
    check("<|a->b, b->x,{}, a->d, b->y, a->e, {}|>", //
        "<|a->e,b->y|>");
    check("Total( <|a -> 4, b -> 2, c -> 1, d -> 5|> )", //
        "12");

    check("Map(f, <|a -> 4, b -> 2, c -> 1, d -> 5|>)", //
        "<|a->f(4),b->f(2),c->f(1),d->f(5)|>");
    check("Association({ahey->avalue, bkey->bvalue, ckey->cvalue})", //
        "<|ahey->avalue,bkey->bvalue,ckey->cvalue|>");
    check("<|a->x, b->y, c->z|>[b]", //
        "y");
    check("<|a->x, b->y, c->z|>", //
        "<|a->x,b->y,c->z|>");
    check("Normal(<|a->x, b->y, c->z|>)", //
        "{a->x,b->y,c->z}");

    check("<|a->x, b->y, c->z|> // FullForm", //
        "Association(Rule(a, x), Rule(b, y), Rule(c, z))");
    check("Level(<|a -> x, b -> y|>, {1})", //
        "{x,y}");
    check("Depth(<|a -> x, b -> y|>)", //
        "2");
    check("Count(<|1 -> 1 + x^2, 2 -> x^4, 3 -> a + (1 + x^2)^2|>, x^_, Infinity)", //
        "3");

    // Fall back if no rules were parsed
    check("<|a, b|>", //
        "Association(a,b)");
    check("<|a -> 6, b -> 2, c -> 3|> + 500", //
        "<|a->506,b->502,c->503|>");
    check(
        "f(#[\"apples\"], #[\"oranges\"]) &[<|\"apples\" -> 10, \"oranges\" -> 12, \"pears\" -> 4|>]", //
        "f(10,12)");
    check("f(#apples, #oranges) &[<|\"apples\" -> 10, \"oranges\" -> 12, \"pears\" -> 4|>] ", //
        "f(10,12)");
  }

  @Test
  public void testAssociationMap() {
    check("AssociationMap(Reverse,<|U->1,V->2|>)", //
        "<|1->U,2->V|>");
    check("AssociationMap(f,{U,V})", //
        "<|U->f(U),V->f(V)|>");

    check("AssociationMap(f, {a, b, c, d})", //
        "<|a->f(a),b->f(b),c->f(c),d->f(d)|>");
    check("AssociationMap(Reverse, <|a -> 1, b -> 2, c -> 3, d -> 4|>)", //
        "<|1->a,2->b,3->c,4->d|>");

    check("AssociationMap(f) @ {a, b, c, d}", //
        "<|a->f(a),b->f(b),c->f(c),d->f(d)|>");
    check("AssociationMap[Length, <|a -> 1, b -> 2|>]", //
        "Association(2,2)");
  }

  @Test
  public void testAssociationQ() {
    check("AssociationQ(<|a, b|>)", //
        "False");
    check("AssociationQ(<|a->x, b->y, c->z|>)", //
        "True");
    check("AssociationQ(<|a, b|>)", //
        "False");
    check("AssociationQ(<|ahey->avalue, bkey->bvalue, ckey->cvalue|>)", //
        "True");
  }

  @Test
  public void testAssociationThread() {
    check("AssociationThread({1, 2, 1} :> {\"eins\", \"two\", \"one\"})", //
        "<|1:>one,2:>two|>");
    check("AssociationThread({1, 2, 1} -> {\"eins\", \"two\", \"one\"})", //
        "<|1->one,2->two|>");

    check("AssociationThread({\"U\",\"V\"},{1,2})", //
        "<|U->1,V->2|>");
    check("AssociationThread({U,V}->{1,2})", //
        "<|U->1,V->2|>");
    check("AssociationThread({U,V}:>{1,2})", //
        "<|U:>1,V:>2|>");
  }

  @Test
  public void testKey() {
    check("<|1 -> a, 3 -> b|>[[Key(3)]]", //
        "b");
    check("<|a -> b, c -> d|>[Key(a)]", //
        "Missing(KeyAbsent,Key(a))");
    check("Key(z)[<|a -> b, c -> d|>]", //
        "Missing(KeyAbsent,z)");
    check("Key(a)[<|a -> b, c -> d|>]", //
        "b");

    check("<|\"a\" -> b, \"c\" -> d|>[[\"c\"]]", //
        "d");
  }

  @Test
  public void testKeys01() {
    check("a = <|k1 -> 1, k2 -> 2, {k1, k2} -> 3, Key(k2) -> 4|>", //
        "<|k1->1,k2->2,{k1,k2}->3,Key(k2)->4|>");
    check("Lookup(a ,k1)", //
        "1");
    check("Lookup(a ,Key(k1))", //
        "1");
    check("Lookup(a ,{Key(k1), Key(k2)})", //
        "{1,2}");
    check("Lookup(a ,Key({k1, k2}))", //
        "3");
    check("Lookup(a ,Key(k2))", //
        "2");
    check("Lookup(a ,Key(Key(k2)))", //
        "4");
  }

  @Test
  public void testKeys02() {
    check("Keys(<|a -> x, a -> y, <|a -> z, <|b -> t|>, <||>, {}|>|>)", //
        "{a,b}");
    check("Keys(<|a -> x, b -> y|>)", //
        "{a,b}");
    check("Keys({a -> x, b -> y})", //
        "{a,b}");
    check("Keys({<|a -> x, b -> y|>, {w -> z, {}}})", //
        "{{a,b},{w,{}}}");
    check("Keys({c -> z, b -> y, a -> x})", //
        "{c,b,a}");
    check("Keys(a:>x)", //
        "a");
    check("Keys({a -> x, a -> y, {a -> z, <|b -> t|>, <||>, {}}})", //
        "{a,a,{a,{b},{},{}}}");
    check("Keys({a -> x, a -> y, <|a -> z, {b -> t}, <||>, {}|>})", //
        "{a,a,{a,b}}");
    check("Keys(<|a -> x, a -> y, <|a -> z, <|b -> t|>, <||>, {}|>|>)", //
        "{a,b}");
    check("Keys(<|a -> x, a -> y, {a -> z, {b -> t}, <||>, {}}|>)", //
        "{a,b}");
    /*
     * #> Keys[<|a -> x, a -> y, {a -> z, {b -> t}, <||>, {}}|>] = {a, b} #> Keys[<|a -> x, <|a ->
     * y, b|>|>] : The argument Association[a -> x, Association[a -> y, b]] is not a valid
     * Association or a list of rules. = Keys[Association[a -> x, Association[a -> y, b]]] #>
     * Keys[<|a -> x, {a -> y, b}|>] : The argument Association[a -> x, {a -> y, b}] is not a valid
     * Association or a list of rules. = Keys[Association[a -> x, {a -> y, b}]] #> Keys[{a -> x, <|a
     * -> y, b|>}] : The argument Association[a -> y, b] is not a valid Association or a list of
     * rules. = Keys[{a -> x, Association[a -> y, b]}] #> Keys[{a -> x, {a -> y, b}}] : The argument
     * b is not a valid Association or a list of rules. = Keys[{a -> x, {a -> y, b}}]
     */
    // check("Keys({a -> x, {a -> y, b}})", //
    // "");
    check("Keys(a -> x, b -> y)", //
        "(b->y)[a]");

    check("Keys(<|k->v|>)", //
        "{k}");
    check("Keys(k:>v,f)", //
        "f(k)");
    check("Keys(<|ahey->avalue, bkey->bvalue, ckey->cvalue|>)", //
        "{ahey,bkey,ckey}");
    check("Keys( <|a -> 2, Nothing -> 2|>, Hold )", //
        "{Hold(a),Hold(Nothing)}");
    check("Keys( <|a -> 4, b -> 2, c -> 1, d -> 5|> )", //
        "{a,b,c,d}");
    check("Keys({ahey->avalue, bkey->bvalue, ckey->cvalue})", //
        "{ahey,bkey,ckey}");
    check("Keys({<|a -> 1, b -> 2|>, {w -> 3, {}}})", //
        "{{a,b},{w,{}}}");
    check("Keys({<|a -> x, b -> y|>, <|a -> <|1 -> i, 2 -> j|>, b -> y|>})", //
        "{{a,b},{a,b}}");
  }

  @Test
  public void testKeySelect() {

    check("r = {beta -> 4, alpha -> 2, x -> 4, z -> 2, w -> 0.8};", //
        "");
    check("KeySelect(r, MatchQ(#,alpha|x)&)", //
        "<|alpha->2,x->4|>");
    check("KeySelect(<|1 -> a, 2 -> b, 3 -> c|>, OddQ)", //
        "<|1->a,3->c|>");
    check("KeySelect(<|1 -> a, 2 -> b, 3 -> c|>, <|1 -> False, 2 -> True,  3 -> True|>)", //
        "<|2->b,3->c|>");
    check("KeySelect(OddQ)[<|1 -> 2, 2 -> 3, 3 -> 4|>]", //
        "<|1->2,3->4|>");
  }

  @Test
  public void testKeySort() {
    check("KeySort(<|c -> 1, b -> 2, a -> 4|>)", //
        "<|a->4,b->2,c->1|>");
  }

  @Test
  public void testGroupBy() {

    check("GroupBy({7},{})", //
        "{7}");
    check("assoc=<|key1->2, key2->4, key3->4, key4->2, key5->7, key6->4|>", //
        "<|key1->2,key2->4,key3->4,key4->2,key5->7,key6->4|>");
    check("GroupBy(assoc, Identity, Keys)", //
        "<|2->{key1,key4},4->{key2,key3,key6},7->{key5}|>");

    check("expr = {{a}, {a, b}, {a, c}, {a, b, c, d}, {a, b, c, f}, {b, c}, {b, d}};", //
        "");
    check(
        "hg = Normal @ GroupBy(# /. {} -> Nothing , First -> Rest,  Function(x, hg(x, #2))) /. {Rule(a_, {b_}) :> Rule(a, b), Rule(a_, {}) :> #2(a)} &;", //
        "");
    check("hg(expr, func)", //
        "{a->{b->c->{func(d),func(f)},func(c)},b->{func(c),func(d)}}");

    check("GroupBy({{a, b}, {a, c}, {b, c}}, First)", //
        "<|a->{{a,b},{a,c}},b->{{b,c}}|>");
    check("GroupBy({{a, x}, {b, v}, {a, y}, {a, z}, {b, w}}, First -> Last, Mean)", //
        "<|a->1/3*(x+y+z),b->1/2*(v+w)|>");
    check("GroupBy(Range(10), {PrimeQ, OddQ})", //
        "<|False-><|False->{4,6,8,10},True->{1,9}|>,True-><|False->{2},True->{3,5,7}|>|>");
    check("GroupBy(<|a -> 1, b -> 2, c -> 4|>, {EvenQ, PrimeQ})", //
        "<|False-><|False-><|a->1|>|>,True-><|False-><|c->4|>,True-><|b->2|>|>|>");
    check("GroupBy({{{a}, b}, {{a}, d}}, Extract({1, 1}))", //
        "<|a->{{{a},b},{{a},d}}|>");
    check("GroupBy({{{a}, b}, {{a}, d}}, Extract(2) ->Extract({1, 1}))", //
        "<|b->{a},d->{a}|>");
    check("GroupBy({<|1 -> a, 2 -> b|>, <|1 -> a, 2 -> c|>}, First)", //
        "<|a->{<|1->a,2->b|>,<|1->a,2->c|>}|>");
    check("GroupBy({<|1 -> a, 2 -> b|>, <|2 -> a, 3 -> c|>}, Key(2))", //
        "<|a->{<|2->a,3->c|>},b->{<|1->a,2->b|>}|>");
    check("GroupBy({<|1 -> a, 2 -> c|>, <|1 -> b, 2 -> c|>}, Key(2)-> Key(1))", //
        "<|c->{a,b}|>");
    check("GroupBy({<|1 -> a, 2 -> b|>, <|1 -> a, 3 -> c|>}, Key(2))", //
        "<|b->{<|1->a,2->b|>},Missing(KeyAbsent,2)->{<|1->a,3->c|>}|>");
    check("GroupBy(Range(-4, 4), {Positive, EvenQ}, MatrixForm)", //
        "<|False-><|False->{-3,-1},True->{-4,-2,0}|>,True-><|False->{1,3},True->{2,4}|>|>");

    check("GroupBy(First) @ {{a, b}, {a, c}, {b, c}}", //
        "<|a->{{a,b},{a,c}},b->{{b,c}}|>");
    check("GroupBy(<|a -> 1, b -> 2, c -> 4|>, EvenQ -> (# + 1 &))", //
        "<|False-><|a->2|>,True-><|b->3,c->5|>|>");
  }

  @Test
  public void testKeyExistsQ() {
    check("KeyExistsQ(<|1->U,2->V|>, 1)", //
        "True");
    check("KeyExistsQ(<|1->U,2->V|>, V)", //
        "False");

    check("KeyExistsQ(<|a -> x, b -> y, c -> z|>)[a]", //
        "True");
    check("KeyExistsQ(<|a -> x, b -> y, c -> z|>, a)", //
        "True");
    check("KeyExistsQ(<|a -> x, b -> y, c -> z|>, d)", //
        "False");

    check("KeyExistsQ({a -> x, b -> y, c -> z}, a)", //
        "True");
    check("KeyExistsQ({a -> x, b -> y, c -> z}, d)", //
        "False");
  }

  @Test
  public void testKeyTake() {
    check("r = {beta -> 4, alpha -> 2, x -> 4, z -> 2, w -> 0.8};", //
        "");
    check("KeyTake(r, {alpha,x})", //
        "<|alpha->2,x->4|>");

    // operator form
    check("KeyTake({a, e})[<|a -> b, c -> d, e -> f, g -> h|>]", //
        "<|a->b,e->f|>");

    check("KeyTake({<|a -> 1, b -> 2|>, <|b -> 2, c -> 3|>}, {a, b})", //
        "{<|a->1,b->2|>,<|b->2|>}");
    check("KeyTake({1 -> 2, 2 -> 4, 3 -> 9, 4 -> 16, 5 -> 25}, {2, 3})", //
        "<|2->4,3->9|>");
    check("KeyTake({{1 -> 2, 2 -> 4, 3 -> 9}, {4 -> 16, 5 -> 25}}, {2, 3})", //
        "{<|2->4,3->9|>,<||>}");
    check("KeyTake({a -> b, b -> c, c -> d}, b)", //
        "<|b->c|>");
    check("KeyTake(<|a -> 1, b -> 2, c -> 3|>, {a, b})", //
        "<|a->1,b->2|>");
  }

  @Test
  public void testLookup() {
    check("rmatRowNames=<|\"A\"->1,\"B\"->2,\"C\"->3,\"D\"->4|>", //
        "<|A->1,B->2,C->3,D->4|>");
    check("rowNames=<|\"f\"->1,\"g\"->2,\"D\"->3|>", //
        "<|f->1,g->2,D->3|>");
    check("Lookup(rowNames,Keys(rmatRowNames),None)", //
        "{None,None,None,3}");

    check("Lookup(a)@ <|a -> 1|>", //
        "1");
    check("Lookup({a -> 1, b -> 2}, b)", //
        "2");
    check("Lookup({{a -> 1, b -> 2}, {a -> 3}}, a)", //
        "{1,3}");
    check("Lookup(<|a -> 1, b -> 2|>, a)", //
        "1");
    check("Lookup(<|a -> 1, b -> 2|>, c)", //
        "Missing(KeyAbsent,c)");
    check("Lookup(<|a -> 1, b -> 2|>, c, 42)", //
        "42");
    check("Lookup(<|a -> 1, b -> 2|>, b, Print(\"unevaluated\"))", //
        "2");
    check("Lookup({<|a -> 1, b -> 2|>,<|a -> 3, b -> 4|>,<|a -> 5, b -> 6|>}, a)", //
        "{1,3,5}");
    check("Lookup({<|a -> 1, b -> 2|>,<|a -> 3, b -> 4|>,<|a -> 5, b -> 6|>}, b)", //
        "{2,4,6}");
    check("Lookup({<|a -> 1, b -> 2|>,<|a -> 3, b -> 4|>,<|a -> 5, b -> 6|>}, {a,b})", //
        "{{1,2},{3,4},{5,6}}");
    check("Lookup(<|a -> 1, b -> 2|>, a)", //
        "1");
    check("Lookup(<|a -> 1, b -> 2|>, a)", //
        "1");
  }

  @Test
  public void testNormal() {
    check("assoc = AssociationThread({\"U\",\"V\"},{1,2}) ", //
        "<|U->1,V->2|>");
    check("Normal(assoc) ", //
        "{U->1,V->2}");
  }

  @Test
  public void testPart() {
    check("<|a->1, b->2, c->3|>[[2]] = 0.5", //
        "0.5");
    check("assoc = <|a ->  <|a -> x, b -> y, c -> z|> , b -> y, c -> z|>", //
        "<|a-><|a->x,b->y,c->z|>,b->y,c->z|>");
    check("Part[assoc,1,All]", //
        "<|a->x,b->y,c->z|>");
    check("assoc = <|a -> x, b -> y, c -> z|>", //
        "<|a->x,b->y,c->z|>");
    check("Part[assoc,1,All]", //
        "<|a->x,b->y,c->z|>[[1,All]]");

    check("l = {a,b,c}", //
        "{a,b,c}");
    check("Part[l,1,All]", //
        "{a,b,c}[[1,All]]");
  }

  @Test
  public void testPartAll() {
    check(
        "assoc=<|Rule(\"RowNames\", List(\"a\", \"b\", \"c\", \"d\" )),Rule(\"ColumnNames\", List(\"a\", \"b\", \"c\", \"d\", \"e\"))|>", //
        "<|RowNames->{a,b,c,d},ColumnNames->{a,b,c,d,e}|>");

    check("Part(assoc,{1,2})", //
        "<|RowNames->{a,b,c,d},ColumnNames->{a,b,c,d,e}|>");
    check("Part(assoc,{1,2},All)", //
        "<|RowNames->{a,b,c,d},ColumnNames->{a,b,c,d,e}|>");
    check("Part(assoc,{1,1})", //
        "<|RowNames->{a,b,c,d}|>");
    check("Part(assoc,{1,1},All)", //
        "<|RowNames->{a,b,c,d}|>");
  }

  @Test
  public void testPartAllList() {
    check(
        "l ={Rule(\"RowNames\", List(\"a\", \"b\", \"c\", \"d\" )),Rule(\"ColumnNames\", List(\"a\", \"b\", \"c\", \"d\", \"e\"))}", //
        "{RowNames->{a,b,c,d},ColumnNames->{a,b,c,d,e}}");

    check("Part(l,{1,2})", //
        "{RowNames->{a,b,c,d},ColumnNames->{a,b,c,d,e}}");
    check("Part(l,{1,2},All)", //
        "{RowNames->{a,b,c,d},ColumnNames->{a,b,c,d,e}}");
    check("Part(l,{1,1})", //
        "{RowNames->{a,b,c,d},RowNames->{a,b,c,d}}");
    check("Part(l,{1,1},All)", //
        "{RowNames->{a,b,c,d},RowNames->{a,b,c,d}}");
    // TODO
    // check("Part(l,{1,1},All,All)", //
    // "{String()->{a,b,c,d},String()->{a,b,c,d}}");
    // check("Part(l,{1,1},All,All,All)", //
    // "{String()->{String(),String(),String(),String()},String()->{String(),String(),String(),String()}}");
  }

  @Test
  public void testPartAllAssociation() {
    check(
        "people = <|\r\n" + "236234 -> <|\"name\" -> \"bob\", \"age\" -> 20, \"sex\" -> \"M\"|>, \n"
            + "253456 -> <|\"name\" -> \"sue\", \"age\" -> 25, \"sex\" -> \"F\"|>, \n"
            + "323442 -> <|\"name\" -> \"ann\", \"age\" -> 18, \"sex\" -> \"F\"|>\n" + "|>;", //
        "");
    check("people[[All, \"age\"]]", //
        "<|236234->20,253456->25,323442->18|>");
    check("people[[ All, \"sex\" ]]", //
        "<|236234->M,253456->F,323442->F|>");
    check("people[[ Key[323442], \"age\" ]]", //
        "18");
    check("people[[ Key[323442], {\"age\",\"sex\"} ]]", //
        "<|age->18,sex->F|>");
  }

  @Test
  public void testPrependTo() {
    check("x = <|1 :> a, 2 :> b|>;PrependTo(x, 4 -> d)", //
        "<|4->d,1:>a,2:>b|>");
    check("x = <|1 :> a, 2 :> b|>;PrependTo(x, 4 -> d);PrependTo(x, 2 :> d)", //
        "<|2:>d,4->d,1:>a|>");
    check("x = <|1 -> a, 2 -> b|>;PrependTo(x, 4 -> d)", //
        "<|4->d,1->a,2->b|>");
  }

  final static String timing1 = "GetKey(assoc_, index_) := First @ Keys @ Take(assoc, {index});\n"
      + "a = 100*100;\r\n" + "k = Table(i, {i, a}); \n" + "v = RandomReal({0, 1000000}, {a}); \n"
      + "assoc = Association(MapThread(Rule, {k, v})); \n"
      + "Print(AbsoluteTiming(Keys(assoc)[[10000]]) );\n"
      + "Print(AbsoluteTiming(GetKey(assoc, 10000)) )";

  @Test
  public void testTiming() {
    check(timing1, //
        "");
  }

  @Test
  public void testSet() {
    check("yy=<||>;For(i=2023,i<=2025,i++,yy(i)=i-1);yy", //
        "<|2023->2022,2024->2023,2025->2024|>");
  }

  @Test
  public void testLookUp() {
    check(
        "a=Association();b=Association(); For(i = 2021, i <= 2035, i++, a(i) = i; b(i) = a(i) + 10);b", //
        "<|2021->2031,2022->2032,2023->2033,2024->2034,2025->2035,2026->2036,2027->2037,\n"
            + "2028->2038,2029->2039,2030->2040,2031->2041,2032->2042,2033->2043,2034->2044,\n"
            + "2035->2045|>");
    check(
        "a=Association[];b=Association[]; For[i = 2021, i <= 2035, i++, a[i] = i; b[i] = a[i] + 10];b", //
        "<|2021->2031,2022->2032,2023->2033,2024->2034,2025->2035,2026->2036,2027->2037,\n"
            + "2028->2038,2029->2039,2030->2040,2031->2041,2032->2042,2033->2043,2034->2044,\n"
            + "2035->2045|>");
    check("a=Association();b=Association();" //
        + "For(i = 2021, i <= 2035, i++, " //
        + "a(i) = i;" //
        + "b(i) = LookUp(a,i) + 10);" //
        + "b", //
        "<|2021->2031,2022->2032,2023->2033,2024->2034,2025->2035,2026->2036,2027->2037,\n"
            + "2028->2038,2029->2039,2030->2040,2031->2041,2032->2042,2033->2043,2034->2044,\n"
            + "2035->2045|>");
  }

  @Test
  public void testForLoop001() {
    check("b=Association();b(2020)=12/2;For(i = 2021, i <= 2035, i++, b(i) = N(i/2) );b", //
        "<|2020->6,2021->1010.5,2022->1011.0,2023->1011.5,2024->1012.0,2025->1012.5,2026->1013.0,\n"
            + "2027->1013.5,2028->1014.0,2029->1014.5,2030->1015.0,2031->1015.5,2032->1016.0,\n"
            + "2033->1016.5,2034->1017.0,2035->1017.5|>");
    check("b=Association();b(2020)=12/2;For(i = 2021, i <= 2035, i++, b(i) = i/2.0);b", //
        "<|2020->6,2021->1010.5,2022->1011.0,2023->1011.5,2024->1012.0,2025->1012.5,2026->1013.0,\n"
            + "2027->1013.5,2028->1014.0,2029->1014.5,2030->1015.0,2031->1015.5,2032->1016.0,\n"
            + "2033->1016.5,2034->1017.0,2035->1017.5|>");
    check("b=Association();b(2020)=12/2;For(i = 2021, i <= 2035, i++, b(i) = i/2);b", //
        "<|2020->6,2021->2021/2,2022->1011,2023->2023/2,2024->1012,2025->2025/2,2026->\n"
            + "1013,2027->2027/2,2028->1014,2029->2029/2,2030->1015,2031->2031/2,2032->1016,\n"
            + "2033->2033/2,2034->1017,2035->2035/2|>");
  }

  @Test
  public void testForLoop002() {
    check("assoc=Association();For(i = 1, i <= 10, i++, assoc(i) = i);assoc", //
        "<|1->1,2->2,3->3,4->4,5->5,6->6,7->7,8->8,9->9,10->10|>");
    check("Keys(assoc)", //
        "{1,2,3,4,5,6,7,8,9,10}");
  }

  @Test
  public void testTimes() {
    // issue 728
    check("Association[2025->10, 2026->11] * Association[2025->10, 2026->20]", //
        "<|2025->100,2026->220|>");
  }

  @Test
  public void testPlus() {
    // issue 728

    // message: Association: The arguments <|2025->10,2026->11|> and <|2025->10,2027->20|> in
    // <|2025->10,2026->11|>+<|2025->10,2027->20|> are incompatible.
    check("<|2025->10, 2026->11|> + <|2025->10, 2027->20 |>", //
        "<|2025->10,2026->11|>+<|2025->10,2027->20|>");
    // message: Thread: Objects of unequal length in
    // <|2025->10,2026->11|>+<|2025->10,2026->20,2027->2|> cannot be combined.
    check("<|2025->10, 2026->11|> + <|2025->10, 2026->20, 2027->2|>", //
        "<|2025->10,2026->11|>+<|2025->10,2026->20,2027->2|>");

    check("<|2025->10, 2026->11|> + <|2025->10, 2026->20|>", //
        "<|2025->20,2026->31|>");
    check("<|2025->10, 2026->11|> + <|2025->10, 2026:>20|>", //
        "<|2025->20,2026:>11+20|>");
    check("<|2025:>10, 2026->11|> + <|2025->10, 2026->20|>", //
        "<|2025->20,2026->31|>");
  }

  @Test
  public void testSubtract() {
    // issue 728
    check("<|2025->10, 2026->11|> - <|2025->10, 2026->20|>", //
        "<|2025->0,2026->-9|>");
  }

  @Test
  public void testDivide() {
    // issue 728
    check("<|2025->10, 2026->11|> / <|2025->10, 2026->20|>", //
        "<|2025->1,2026->11/20|>");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.MAX_AST_SIZE = 1000000;
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
