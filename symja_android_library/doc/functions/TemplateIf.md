## TemplateIf

```
TemplateIf(condition-expression, true-expression, false-expression)
```

> in `TemplateApply` evaluation insert `true-expression` if `condition-expression` evaluates to `true`, otherwise insert `false-expression`.
	 

### Examples

```
>> t=TemplateIf( TemplateSlot("summer"), "in summer ice cream is delicious", "ice cream is boring in winter"); TemplateApply(t, <|"summer" -> True|>)
in summer ice cream is delicious
```


### Related terms 
[StringTemplate](StringTemplate.md), [TemplateApply](TemplateApply.md), [TemplateSlot](TemplateSlot.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TemplateIf](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2821) 
