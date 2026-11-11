## BioSequenceComplement

```
BioSequenceComplement(seq)
```

> returns the complementary sequence of the nucleotide sequence `seq`, in the same order of the letters.

Each base is replaced by its Watson-Crick partner: `A` and `T` (or `U` in RNA), `C` and `G`. The IUPAC ambiguity letters are replaced by the letters of the complementary bases, for example `R` (`A` or `G`) by `Y` (`C` or `T`). The type of the sequence is kept. A string of letters is read as by `BioSequence("letters")`.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceComplement(BioSequence("DNA", "AACG"))
BioSequence[Type: DNA, Content: TTGC (4 letters)]

>> BioSequenceComplement(BioSequence("RNA", "ACGU"))
BioSequence[Type: RNA, Content: UGCA (4 letters)]

>> BioSequenceComplement(BioSequence("DNA", "RYKMN"))
BioSequence[Type: DNA, Content: YRMKN (5 letters)]

>> BioSequenceComplement("AACG")
BioSequence[Type: DNA, Content: TTGC (4 letters)]
```

A peptide sequence has no complement:

```
>> BioSequenceComplement(BioSequence("Peptide", "MA"))
BioSequenceComplement(BioSequence[Type: Peptide, Content: MA (2 letters)])
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceReverseComplement](BioSequenceReverseComplement.md), [BioSequenceTranscribe](BioSequenceTranscribe.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceComplement](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
