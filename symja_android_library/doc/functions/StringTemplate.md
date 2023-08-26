## StringTemplate

```
StringTemplate(string)
```

> gives a  `StringTemplate` expression with name `string`.
	 

### Examples

```
>> StringTemplate("The quick brown `a` jumps over the lazy `b`.")[<|"a" -> "fox", "b" -> "dog"|>]
The quick brown fox jumps over the lazy dog.

>> TemplateApply(StringTemplate("The quick brown `a` jumps over the lazy `b`."),<|"a" -> "fox", "b" -> "dog"|>)
The quick brown fox jumps over the lazy dog.
```

### Related terms 
[TemplateApply](TemplateApply.md),[TemplateIf](TemplateIf.md), [TemplateSlot](TemplateSlot.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringTemplate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2691) 
