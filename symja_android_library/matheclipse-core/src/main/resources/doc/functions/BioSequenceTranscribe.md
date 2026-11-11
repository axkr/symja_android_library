## BioSequenceTranscribe

```
BioSequenceTranscribe(seq)
```

> transcribes the DNA sequence `seq` to RNA, and reverse transcribes the RNA sequence `seq` to DNA.

The letter `T` becomes `U`, or `U` becomes `T`; the other letters are kept. A circular sequence stays circular. A string of letters is read as by `BioSequence("letters")`.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceTranscribe(BioSequence("DNA", "ATGC"))
BioSequence[Type: RNA, Content: AUGC (4 letters)]

>> BioSequenceTranscribe(BioSequence("RNA", "AUGC"))
BioSequence[Type: DNA, Content: ATGC (4 letters)]

>> BioSequenceTranscribe(BioSequence("CircularDNA", "ATGC"))
BioSequence[Type: CircularRNA, Content: AUGC (4 letters)]
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceComplement](BioSequenceComplement.md), [BioSequenceTranslate](BioSequenceTranslate.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceTranscribe](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
