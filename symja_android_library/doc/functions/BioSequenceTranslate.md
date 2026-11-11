## BioSequenceTranslate

```
BioSequenceTranslate(seq)
```

> translates the codons of the DNA or RNA sequence `seq` to a peptide sequence, with the standard genetic code.

```
BioSequenceTranslate(seq, gtt)
```

> translates `seq` with the genetic code translation table `gtt`.

The table `gtt` is an [NCBI translation table](https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi), given by its number or by its name, like `1` or `"Standard"`, and `2` or `"VertebrateMitochondrial"`. The case of a name and underscores in it don't matter, so `"VERTEBRATE_MITOCHONDRIAL"` works too.

A stop codon is translated to `*`, and the translation doesn't stop there. Letters after the last complete codon are ignored. A string of letters is read as by `BioSequence("letters")`.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceTranslate(BioSequence("DNA", "ATGGCA"))
BioSequence[Type: Peptide, Content: MA (2 letters)]

>> BioSequenceTranslate(BioSequence("RNA", "AUGGCAUAA"))
BioSequence[Type: Peptide, Content: MA* (3 letters)]

>> BioSequenceTranslate("ATGGCA")
BioSequence[Type: Peptide, Content: MA (2 letters)]
```

`TGA` is a stop codon in the standard code, but codes for tryptophan (`W`) in the vertebrate mitochondrial code:

```
>> BioSequenceTranslate(BioSequence("DNA", "ATGTGGTGA"))
BioSequence[Type: Peptide, Content: MW* (3 letters)]

>> BioSequenceTranslate(BioSequence("DNA", "ATGTGGTGA"), 2)
BioSequence[Type: Peptide, Content: MWW (3 letters)]

>> BioSequenceTranslate(BioSequence("DNA", "ATGTGGTGA"), "VertebrateMitochondrial")
BioSequence[Type: Peptide, Content: MWW (3 letters)]
```

Translate from the first start codon:

```
>> BioSequenceTranslate(BioSequenceModify("DropToStartCodon", BioSequence("DNA", "CCATGGCATAA")))
BioSequence[Type: Peptide, Content: MA* (3 letters)]
```

An unknown table prints a message:

```
>> BioSequenceTranslate(BioSequence("DNA", "ATGGCA"), 99)
BioSequenceTranslate(BioSequence[Type: DNA, Content: ATGGCA (6 letters)],99)
```

### Related terms
[BioSequence](BioSequence.md), [BioSequenceBackTranslateList](BioSequenceBackTranslateList.md), [BioSequenceModify](BioSequenceModify.md), [BioSequenceTranscribe](BioSequenceTranscribe.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceTranslate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
