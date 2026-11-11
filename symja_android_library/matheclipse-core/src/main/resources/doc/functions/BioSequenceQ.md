## BioSequenceQ

```
BioSequenceQ(expr)
```

> returns `True` if `expr` is a biomolecular sequence created by [BioSequence](BioSequence.md), and `False` otherwise.

A string of letters is not a sequence.

The function is part of the `matheclipse-bio` module, which is based on [BioJava](https://biojava.org).

### Examples

```
>> BioSequenceQ(BioSequence("DNA", "ACGT"))
True

>> BioSequenceQ("ACGT")
False

>> BioSequenceQ(42)
False
```

### Related terms
[BioSequence](BioSequence.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BioSequenceQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-bio/src/main/java/org/matheclipse/bio/builtin/BioSequenceFunctions.java)
