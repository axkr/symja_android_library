## BioSequenceBackTranslateList

```
BioSequenceBackTranslateList(peptide)
```

> returns the list of all RNA sequences which translate to the peptide sequence `peptide` with the standard genetic code.

```
BioSequenceBackTranslateList(peptide, gtt)
```

> uses the genetic code translation table `gtt`, given by its number or name as in [BioSequenceTranslate](BioSequenceTranslate.md).

The number of sequences is the product of the numbers of codons of the amino acids, so it grows fast with the length of the peptide. With the option `MaxItems -> n` (default `10000`) a larger result prints a message instead.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

Methionine (`M`) and tryptophan (`W`) have one codon each, lysine (`K`) has two:

```
>> BioSequenceBackTranslateList(BioSequence("Peptide", "MW"))
{BioSequence[Type: RNA, Content: AUGUGG (6 letters)]}

>> BioSequenceBackTranslateList(BioSequence("Peptide", "MK"))
{BioSequence[Type: RNA, Content: AUGAAA (6 letters)],BioSequence[Type: RNA, Content: AUGAAG (6 letters)]}
```

Leucine has six codons:

```
>> Length(BioSequenceBackTranslateList(BioSequence("Peptide", "LLL")))
216
```

In the vertebrate mitochondrial code `AUA` codes for methionine and `UGA` for tryptophan:

```
>> BioSequenceBackTranslateList(BioSequence("Peptide", "MW"), 2)
{BioSequence[Type: RNA, Content: AUAUGA (6 letters)],BioSequence[Type: RNA, Content: AUAUGG (6 letters)],BioSequence[Type: RNA, Content: AUGUGA (6 letters)],BioSequence[Type: RNA, Content: AUGUGG (6 letters)]}
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceInstances](BioSequenceInstances.md), [BioSequenceTranslate](BioSequenceTranslate.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceBackTranslateList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
