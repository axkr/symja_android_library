## BioSequenceReverseComplement

```
BioSequenceReverseComplement(seq)
```

> returns the reverse complement of the nucleotide sequence `seq`: the complementary strand, read in the opposite direction.

This is the [BioSequenceComplement](BioSequenceComplement.md) of `seq` with the order of the letters reversed. The type of the sequence is kept. A string of letters is read as by `BioSequence("letters")`.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceReverseComplement(BioSequence("DNA", "AAGT"))
BioSequence[Type: DNA, Content: ACTT (4 letters)]

>> BioSequenceReverseComplement(BioSequence("RNA", "AAGU"))
BioSequence[Type: RNA, Content: ACUU (4 letters)]

>> BioSequenceReverseComplement("ATGC")
BioSequence[Type: DNA, Content: GCAT (4 letters)]
```

`ACGT` is its own reverse complement:

```
>> BioSequenceReverseComplement(BioSequence("DNA", "ACGT"))
BioSequence[Type: DNA, Content: ACGT (4 letters)]
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceComplement](BioSequenceComplement.md), [BioSequenceTranscribe](BioSequenceTranscribe.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceReverseComplement](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
