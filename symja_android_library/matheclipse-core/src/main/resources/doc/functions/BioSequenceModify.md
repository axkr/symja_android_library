## BioSequenceModify

```
BioSequenceModify("op", seq)
```

> applies the operation `"op"` to the sequence `seq`.

```
BioSequenceModify("op", seq, bond)
```

> applies an operation with the argument `bond`.

The operations are:

* `"MakeCircular"` - the circular form of a DNA or RNA sequence (type `"CircularDNA"` or `"CircularRNA"`)
* `"MakeLinear"` - the linear form of a circular sequence
* `"DropToStartCodon"` - the nucleotide sequence from its first start codon `ATG` (`AUG` in RNA) on
* `"AddBond"` and `"DeleteBond"` - add the `bond` to, or delete it from, the bonds the sequence carries

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceModify("MakeCircular", BioSequence("DNA", "ACGT"))
BioSequence[Type: CircularDNA, Content: ACGT (4 letters)]

>> BioSequenceModify("MakeLinear", BioSequence("CircularDNA", "ACGT"))
BioSequence[Type: DNA, Content: ACGT (4 letters)]

>> BioSequenceModify("DropToStartCodon", BioSequence("DNA", "CCATGGCA"))
BioSequence[Type: DNA, Content: ATGGCA (6 letters)]

>> BioSequenceModify("DropToStartCodon", BioSequence("RNA", "CCAUGGCA"))
BioSequence[Type: RNA, Content: AUGGCA (6 letters)]
```

A sequence without a start codon, and an unknown operation, print a message:

```
>> BioSequenceModify("DropToStartCodon", BioSequence("DNA", "CCCC"))
BioSequenceModify(DropToStartCodon,BioSequence[Type: DNA, Content: CCCC (4 letters)])

>> BioSequenceModify("Reverse", BioSequence("DNA", "ACGT"))
BioSequenceModify(Reverse,BioSequence[Type: DNA, Content: ACGT (4 letters)])
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceTranslate](BioSequenceTranslate.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceModify](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
