## SmithWatermanSimilarity

```
SmithWatermanSimilarity(s1, s2)
```

> returns the number of matching elements in an optimal local alignment of the strings, lists or biomolecular sequences `s1` and `s2`, found with the [Smith-Waterman algorithm](https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm).

A local alignment aligns the best matching regions of `s1` and `s2` and leaves out the rest. The scoring of the alignment decides which alignment is optimal, but the result is always the number of elements which match in it.

The elements of a string or a [BioSequence](BioSequence.md) are its characters; a list is aligned element by element. The alignment scores `+1` for two equal elements, `-1` for two different ones and `-1` for each gap. The options are:

* `GapPenalty -> p` - the penalty `p` for each element of a gap; `GapPenalty -> {open, extend}` has a penalty `open` for the first element of a gap and `extend` for each further element (default `1`)
* `IgnoreCase -> True` - compare letters ignoring their case (default `False`)
* `SimilarityRules -> "matrix"` - score with a named substitution matrix: `"BLOSUM30"` to `"BLOSUM100"`, `"PAM250"`, `"GONNET250"`, `"IDENTITY"`, `"NUC.4.2"` or `"NUC.4.4"`; the spelling of the name is normalized, so `"NUC44"` works too
* `SimilarityRules -> {{a, b} -> s, ...}` - score `s` for aligning the element `a` with `b`; `_` matches any element

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> SmithWatermanSimilarity("xxxxABCx", "yABCyyyy")
3

>> SmithWatermanSimilarity("abc", "xyz")
0

>> SmithWatermanSimilarity({1, 2, 3, 4}, {1, 3, 4})
2

>> SmithWatermanSimilarity(BioSequence("DNA", "ACGT"), BioSequence("DNA", "ACGT"))
4
```

The global alignment of the same lists matches more elements:

```
>> NeedlemanWunschSimilarity({1, 2, 3, 4}, {1, 3, 4})
3
```

With a substitution matrix and ignoring the case:

```
>> SmithWatermanSimilarity("MWAKL", "WAK", SimilarityRules -> "BLOSUM62")
3

>> SmithWatermanSimilarity("abc", "ABC", IgnoreCase -> True)
3
```

### Related terms
[BioSequence](BioSequence.md), [EditDistance](EditDistance.md), [NeedlemanWunschSimilarity](NeedlemanWunschSimilarity.md), [SequenceAlignment](SequenceAlignment.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SmithWatermanSimilarity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioAlignmentFunctions.java)
