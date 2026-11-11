## SequenceAlignment

```
SequenceAlignment(s1, s2)
```

> finds an optimal alignment of the strings, lists or biomolecular sequences `s1` and `s2`.

The result is a list of the runs which `s1` and `s2` have in common, and of pairs `{a1, a2}` where they differ: `a1` is the part of `s1` and `a2` the part of `s2`, and an empty part is a gap. For two strings the parts are strings; otherwise they are lists of elements.

The elements of a string or a [BioSequence](BioSequence.md) are its characters; a list is aligned element by element. The alignment scores `+1` for two equal elements, `-1` for two different ones and `-1` for each gap. The options are:

* `GapPenalty -> p` - the penalty `p` for each element of a gap; `GapPenalty -> {open, extend}` has a penalty `open` for the first element of a gap and `extend` for each further element (default `1`)
* `IgnoreCase -> True` - compare letters ignoring their case (default `False`)
* `SimilarityRules -> "matrix"` - score with a named substitution matrix: `"BLOSUM30"` to `"BLOSUM100"`, `"PAM250"`, `"GONNET250"`, `"IDENTITY"`, `"NUC.4.2"` or `"NUC.4.4"`; the spelling of the name is normalized, so `"NUC44"` works too
* `SimilarityRules -> {{a, b} -> s, ...}` - score `s` for aligning the element `a` with `b`; `_` matches any element
* `MergeDifferences -> False` - give a pair for each aligned element instead of merging adjacent differences into one pair (default `True`)
* `Method -> "Local"` - a local alignment, which leaves out the parts before and after the best matching region; the default is a global alignment of the whole sequences

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> SequenceAlignment("BANANA", "ANANAS")
{{B,},ANANA,{,S}}

>> SequenceAlignment("abcXdef", "abcYdef")
{abc,{X,Y},def}

>> SequenceAlignment("abcdef", "abef")
{ab,{cd,},ef}

>> SequenceAlignment({1, 2, 3, 4}, {1, 3, 4})
{{1},{{2},{}},{3,4}}
```

Adjacent differences are merged into one pair unless `MergeDifferences -> False`:

```
>> SequenceAlignment("aXYb", "aZWb")
{a,{XY,ZW},b}

>> SequenceAlignment("aXYb", "aZWb", MergeDifferences -> False)
{a,{X,Z},{Y,W},b}
```

A global and a local alignment:

```
>> SequenceAlignment("xxABCxx", "yABCy")
{{xx,y},ABC,{xx,y}}

>> SequenceAlignment("xxABCxx", "yABCy", Method -> "Local")
{ABC}
```

Ignoring the case:

```
>> SequenceAlignment("ABC", "abc")
{{ABC,abc}}

>> SequenceAlignment("ABC", "abc", IgnoreCase -> True)
{ABC}
```

### Related terms
[BioSequence](BioSequence.md), [EditDistance](EditDistance.md), [NeedlemanWunschSimilarity](NeedlemanWunschSimilarity.md), [SmithWatermanSimilarity](SmithWatermanSimilarity.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SequenceAlignment](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioAlignmentFunctions.java)
