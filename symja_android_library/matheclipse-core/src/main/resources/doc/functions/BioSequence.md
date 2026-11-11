## BioSequence

```
BioSequence("type", "letters")
```

> a biomolecular sequence of the given `type` with the given `letters`.

```
BioSequence("letters")
```

> a biomolecular sequence whose type is inferred from the `letters`: `"DNA"` if they are DNA letters, otherwise `"RNA"`, otherwise `"Peptide"`.

```
BioSequence("type", n)
```

> a sequence of `n` wildcard letters: `N` for a nucleotide type, `X` for `"Peptide"`.

The types are `"DNA"`, `"RNA"`, `"CircularDNA"`, `"CircularRNA"`, `"Peptide"` and `"HybridStrand"`. The nucleotide letters may be IUPAC ambiguity letters like `R` (`A` or `G`) or `N` (any base); they are read case-insensitively and a nucleotide `X` is stored as `N`. In a `"Peptide"` sequence the stop `*` is stored as `.`. Many amino acid letters are also IUPAC nucleotide letters, so a peptide is best given with its type `"Peptide"`.

A sequence prints with its type, its letters (only the first six and the last three of a longer sequence) and its length. String functions like [StringLength](StringLength.md), [StringTake](StringTake.md), [StringReplace](StringReplace.md) or [StringCount](StringCount.md) accept a sequence in place of a string; a string in their result becomes a sequence of the same type again. In a string pattern an ambiguity letter matches every letter it stands for.

[Import](Import.md) reads the sequences of a FASTA or GenBank file, and [Export](Export.md) writes sequences to a FASTA file.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequence("DNA", "ACGT")
BioSequence[Type: DNA, Content: ACGT (4 letters)]

>> BioSequence("Peptide", "MSTA")
BioSequence[Type: Peptide, Content: MSTA (4 letters)]

>> BioSequence("CircularDNA", "ACGT")
BioSequence[Type: CircularDNA, Content: ACGT (4 letters)]
```

The type is inferred from the letters:

```
>> BioSequence("acgt")
BioSequence[Type: DNA, Content: ACGT (4 letters)]

>> BioSequence("ACGU")
BioSequence[Type: RNA, Content: ACGU (4 letters)]
```

Wildcard letters:

```
>> BioSequence("DNA", 5)
BioSequence[Type: DNA, Content: NNNNN (5 letters)]

>> BioSequence("Peptide", 3)
BioSequence[Type: Peptide, Content: XXX (3 letters)]
```

A longer sequence prints shortened, and string functions work on its letters:

```
>> BioSequence("DNA", "ATGGCATTTAAA")
BioSequence[Type: DNA, Content: ATGGCA-AAA (12 letters)]

>> StringLength(BioSequence("DNA", "ATGGCATTTAAA"))
12

>> StringTake(BioSequence("DNA", "ATGGCATTTAAA"), 6)
BioSequence[Type: DNA, Content: ATGGCA (6 letters)]

>> StringPosition(BioSequence("DNA", "ATGGCATGC"), "ATG")
{{1,3},{6,8}}
```

The ambiguity letter `N` in a pattern matches every base:

```
>> StringCount(BioSequence("DNA", "ATGGCATGC"), "TN")
2
```

An unknown type or letters which are not in the alphabet of the type print a message:

```
>> BioSequence("Protein", "MSTA")
BioSequence(Protein,MSTA)

>> BioSequence("DNA", "ACGTZ")
BioSequence(DNA,ACGTZ)
```

### Related terms
[BioSequenceBackTranslateList](BioSequenceBackTranslateList.md), [BioSequenceComplement](BioSequenceComplement.md), [BioSequenceInstances](BioSequenceInstances.md), [BioSequenceModify](BioSequenceModify.md), [BioSequenceQ](BioSequenceQ.md), [BioSequenceReverseComplement](BioSequenceReverseComplement.md), [BioSequenceTranscribe](BioSequenceTranscribe.md), [BioSequenceTranslate](BioSequenceTranslate.md), [Export](Export.md), [Import](Import.md), [StringCount](StringCount.md), [StringTake](StringTake.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequence](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
