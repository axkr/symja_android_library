## BioSequenceInstances

```
BioSequenceInstances(seq)
```

> returns the list of all unambiguous sequences which the nucleotide sequence `seq` with IUPAC ambiguity letters stands for.

Each ambiguity letter is replaced by each of the bases it stands for, for example `R` by `A` and `G`, and `N` by all four bases. A sequence without ambiguity letters stands only for itself. With the option `MaxItems -> n` (default `10000`) a larger result prints a message instead.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceInstances(BioSequence("DNA", "AR"))
{BioSequence[Type: DNA, Content: AA (2 letters)],BioSequence[Type: DNA, Content: AG (2 letters)]}

>> BioSequenceInstances(BioSequence("RNA", "NA"))
{BioSequence[Type: RNA, Content: AA (2 letters)],BioSequence[Type: RNA, Content: CA (2 letters)],BioSequence[Type: RNA, Content: GA (2 letters)],BioSequence[Type: RNA, Content: UA (2 letters)]}

>> BioSequenceInstances(BioSequence("DNA", "AC"))
{BioSequence[Type: DNA, Content: AC (2 letters)]}
```

`NNNNNNNN` stands for `4^8 = 65536` sequences, more than the default `MaxItems` allows:

```
>> BioSequenceInstances(BioSequence("DNA", "NNNNNNNN"))
BioSequenceInstances(BioSequence[Type: DNA, Content: NNNNNNNN (8 letters)])
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceBackTranslateList](BioSequenceBackTranslateList.md), [BioSequenceComplement](BioSequenceComplement.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceInstances](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
