## NeedlemanWunschSimilarity

```
NeedlemanWunschSimilarity(s1, s2)
```

> returns the number of matching elements in an optimal global alignment of the strings, lists or biomolecular sequences `s1` and `s2`, found with the [Needleman-Wunsch algorithm](https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm).

A global alignment aligns `s1` and `s2` from their first to their last element. The scoring of the alignment decides which alignment is optimal, but the result is always the number of elements which match in it.

The elements of a string or a [BioSequence](BioSequence.md) are its characters; a list is aligned element by element. The alignment scores `+1` for two equal elements, `-1` for two different ones and `-1` for each gap. The options are:

* `GapPenalty -> p` - the penalty `p` for each element of a gap; `GapPenalty -> {open, extend}` has a penalty `open` for the first element of a gap and `extend` for each further element (default `1`)
* `IgnoreCase -> True` - compare letters ignoring their case (default `False`)
* `SimilarityRules -> "matrix"` - score with a named substitution matrix: `"BLOSUM30"` to `"BLOSUM100"`, `"PAM250"`, `"GONNET250"`, `"IDENTITY"`, `"NUC.4.2"` or `"NUC.4.4"`; the spelling of the name is normalized, so `"NUC44"` works too
* `SimilarityRules -> {{a, b} -> s, ...}` - score `s` for aligning the element `a` with `b`; `_` matches any element

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> NeedlemanWunschSimilarity("abc", "abc")
3

>> NeedlemanWunschSimilarity({1, 2, 3, 4}, {1, 3, 4})
3

>> NeedlemanWunschSimilarity(BioSequence("Peptide", "MWA"), BioSequence("Peptide", "MWA"))
3
```

Ignoring the case:

```
>> NeedlemanWunschSimilarity("ABC", "abc")
0

>> NeedlemanWunschSimilarity("ABC", "abc", IgnoreCase -> True)
3
```

A global alignment of sequences of different lengths must contain a gap, so a large gap penalty doesn't change the result:

```
>> NeedlemanWunschSimilarity("ACGT", "AGT", GapPenalty -> 100)
3
```

Substitution matrices and rules:

```
>> NeedlemanWunschSimilarity("MWA", "MYA", SimilarityRules -> "BLOSUM62")
2

>> NeedlemanWunschSimilarity("ACGT", "ACGT", SimilarityRules -> "NUC.4.4")
4

>> NeedlemanWunschSimilarity("ab", "ax", SimilarityRules -> {{"b", "x"} -> 5})
1
```

### Related terms
[BioSequence](BioSequence.md), [EditDistance](EditDistance.md), [SequenceAlignment](SequenceAlignment.md), [SmithWatermanSimilarity](SmithWatermanSimilarity.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NeedlemanWunschSimilarity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioAlignmentFunctions.java)
