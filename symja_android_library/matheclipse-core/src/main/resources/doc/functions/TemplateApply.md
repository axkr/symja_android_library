## TemplateApply

```
TemplateApply(string, values)
```

> renders a  `StringTemplate` expression by replacing  `TemplateSlot`s with mapped values.
	 

### Examples

```
>> TemplateApply("The quick brown `a` jumps over the lazy `b`.",<|"a" -> "fox", "b" -> "dog"|>)
The quick brown fox jumps over the lazy dog.

>> TemplateApply(StringTemplate("The quick brown `a` jumps over the lazy `b`."),<|"a" -> "fox", "b" -> "dog"|>)
The quick brown fox jumps over the lazy dog.
```

### Related terms 
[StringTemplate](StringTemplate.md), [TemplateIf](TemplateIf.md), [TemplateSlot](TemplateSlot.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TemplateApply](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2711) 
